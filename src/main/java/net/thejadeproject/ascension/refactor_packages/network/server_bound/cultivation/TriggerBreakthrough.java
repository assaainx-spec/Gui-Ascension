package net.thejadeproject.ascension.refactor_packages.network.server_bound.cultivation;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;

public record TriggerBreakthrough(ResourceLocation pathId) implements CustomPacketPayload {
    public static final Type<TriggerBreakthrough> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "trigger_breakthrough"));
    public static final StreamCodec<RegistryFriendlyByteBuf, TriggerBreakthrough> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC,
                    TriggerBreakthrough::pathId,
                    TriggerBreakthrough::new
            );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handlePayload(TriggerBreakthrough payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            IEntityData entityData = context.player().getData(ModAttachments.ENTITY_DATA);
            if (entityData.hasPath(payload.pathId())) {
                entityData.getPathData(payload.pathId()).setBreakingThrough(true);
            }
        });
    }
}
