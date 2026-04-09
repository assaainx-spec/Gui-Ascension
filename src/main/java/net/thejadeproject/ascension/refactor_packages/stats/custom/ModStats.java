package net.thejadeproject.ascension.refactor_packages.stats.custom;

import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.skills.ISkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.GenericCultivationSkill;
import net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.SwordCultivationSkill;
import net.thejadeproject.ascension.refactor_packages.stats.Stat;

public class ModStats {
    public static final DeferredRegister<Stat> STATS =DeferredRegister.create(AscensionRegistries.Stats.STATS_REGISTRY, AscensionCraft.MOD_ID);


    public static final DeferredHolder<Stat,Stat> VITALITY = STATS.register("vitality",()->
            new Stat(Component.literal("Vitality"),Component.literal("VIT"),Component.empty()));

    public static final DeferredHolder<Stat,Stat> AGILITY = STATS.register("agility",()->
            new Stat(Component.literal("Agility"),Component.literal("AGI"),Component.empty()));

    public static final DeferredHolder<Stat,Stat> STRENGTH = STATS.register("strength",()->
            new Stat(Component.literal("Strength"),Component.literal("STR"),Component.empty()));

    public static void register(IEventBus modEventBus){
        STATS.register(modEventBus);
    }
}
