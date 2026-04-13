package net.thejadeproject.ascension.command.cultivation;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.paths.PathData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;

public class SetCultivationCommand {

    private static final String[] VALID_PATHS = {
            "ascension:essence",
    };

    private static final String[] SIMPLE_PATH_NAMES = {
            "body",
            "essence",
            "intent",
    };

    @SubscribeEvent
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("cultivation")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("set")
                        .then(Commands.argument("target", EntityArgument.players())
                                .then(Commands.argument("path", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            // Suggest both simple names and full IDs
                                            return SharedSuggestionProvider.suggest(SIMPLE_PATH_NAMES, builder);
                                        })
                                        .then(Commands.argument("majorRealm", IntegerArgumentType.integer(0, 11))
                                                .then(Commands.argument("minorRealm", IntegerArgumentType.integer(0, 9))
                                                        // Branch without progress argument
                                                        .executes(SetCultivationCommand::setCultivationRealm)
                                                        // Branch with optional progress percentage
                                                        .then(Commands.argument("progress", IntegerArgumentType.integer(0, 100))
                                                                .executes(SetCultivationCommand::setCultivationRealm)
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.literal("get")
                        .then(Commands.argument("target", EntityArgument.player())
                                .executes(SetCultivationCommand::getCultivationInfo)
                        )
                )
        );
    }

    private static int setCultivationRealm(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        var players = EntityArgument.getPlayers(context, "target");
        String inputPath = StringArgumentType.getString(context, "path");
        int majorRealm = IntegerArgumentType.getInteger(context, "majorRealm");
        int minorRealm = IntegerArgumentType.getInteger(context, "minorRealm");

        // Extract optional progress argument (-1 means not provided)
        int progressPercent = -1;
        try {
            progressPercent = IntegerArgumentType.getInteger(context, "progress");
            // Validate progress range
            if (progressPercent < 0 || progressPercent > 100) {
                context.getSource().sendFailure(
                        Component.literal("Progress must be between 0 and 100")
                );
                return 0;
            }
        } catch (IllegalArgumentException e) {
            // Progress argument not provided - continue without it
        }

        String normalizedPath = normalizePath(inputPath.toLowerCase());

        if (!isValidPath(normalizedPath)) {
            context.getSource().sendFailure(
                    Component.literal("Invalid path. Valid paths are: body, essence, intent")
            );
            return 0;
        }

        // Validate minor realm range
        if (minorRealm < 0 || minorRealm > 9) {
            context.getSource().sendFailure(
                    Component.literal("Minor realm must be between 0 and 9")
            );
            return 0;
        }

        // Validate major realm range
        if (majorRealm < 0 || majorRealm > 11) {
            context.getSource().sendFailure(
                    Component.literal("Major realm must be between 0 and 11")
            );
            return 0;
        }

        int successCount = 0;
        for (ServerPlayer player : players) {
            if (setPlayerCultivationRealm(player, normalizedPath, majorRealm, minorRealm, progressPercent, context.getSource())) {
                successCount++;
            }
        }

        return successCount;
    }

    private static int getCultivationInfo(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(context, "target");
        //TODO
        return 1;
    }

    private static String normalizePath(String input) {
        input = input.toLowerCase();

        if (input.contains(":")) {
            if (input.startsWith("ascension:")) {
                return input;
            }
            String[] parts = input.split(":");
            if (parts.length == 2) {
                String pathName = parts[1];
                if (isValidPathName(pathName)) {
                    return "ascension:" + pathName;
                }
            }
        }

        if (isValidPathName(input)) {
            return "ascension:" + input;
        }

        return input;
    }

    private static boolean isValidPathName(String pathName) {
        return pathName.equals("body") || pathName.equals("essence") || pathName.equals("intent");
    }

    private static boolean isValidPath(String path) {
        for (String validPath : VALID_PATHS) {
            if (validPath.equals(path)) {
                return true;
            }
        }
        return false;
    }

    private static boolean setPlayerCultivationRealm(ServerPlayer player, String pathId,
                                                     int newMajorRealm, int newMinorRealm,
                                                     int progressPercent,
                                                     CommandSourceStack source) {
        try {
            //TODO FIX
            System.out.println("trying to get data for path: "+pathId);
            ResourceLocation path = ResourceLocation.parse(pathId);
            PathData data = player.getData(ModAttachments.ENTITY_DATA).getPathData(path);
            data.handleRealmChange(newMajorRealm,newMinorRealm,player.getData(ModAttachments.ENTITY_DATA));
            data.sync(player);

            data.setCurrentRealmProgress((AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(data.getLastUsedTechnique()).getMaxQiForRealm(data.getMajorRealm(),data.getMinorRealm()))*(progressPercent/100.0));
            // Build feedback message with progress info if provided
            String progressStr = (progressPercent >= 0) ? String.format(" with %d%% progress", progressPercent) : "";
            String feedbackToSource = String.format(
                    "Set %s's %s cultivation to realm %d.%d (was %d.%d)%s",
                    player.getName().getString(),
                    getSimplePathName(pathId),
                    newMajorRealm,
                    newMinorRealm,
                    0,
                    0,
                    progressStr
            );

            source.sendSuccess(() -> Component.literal(feedbackToSource), true);
            if (source.getPlayer() != player) {
                String feedbackToPlayer = String.format(
                        "Your %s cultivation has been set to realm %d.%d%s",
                        getSimplePathName(pathId),
                        newMajorRealm,
                        newMinorRealm,
                        progressStr
                );
                player.sendSystemMessage(Component.literal(feedbackToPlayer));
            }

            return true;

        } catch (Exception e) {
            player.sendSystemMessage(
                    Component.literal("Error setting cultivation realm: " + e.getMessage())
            );
            source.sendFailure(
                    Component.literal("Failed to set cultivation for " + player.getName().getString())
            );
            return false;
        }
    }

    private static Component buildCultivationInfo(ServerPlayer player) {
        StringBuilder info = new StringBuilder();
        info.append("=== Cultivation Info for ").append(player.getName().getString()).append(" ===\n");

        boolean hasData = false;
        //TODO FIX
        return Component.literal(info.toString());
    }

    private static String getSimplePathName(String pathId) {
        if (pathId.contains(":")) {
            String[] parts = pathId.split(":");
            if (parts.length == 2) {
                String name = parts[1];
                return name.substring(0, 1).toUpperCase() + name.substring(1);
            }
        }
        return pathId;
    }

    private static String getSimpleTechniqueName(String techniqueId) {
        if (techniqueId.equals("ascension:none")) {
            return "None";
        }
        if (techniqueId.contains(":")) {
            String[] parts = techniqueId.split(":");
            if (parts.length == 2) {
                return parts[1];
            }
        }
        return techniqueId;
    }
}