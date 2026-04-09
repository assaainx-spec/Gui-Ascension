package net.thejadeproject.ascension.refactor_packages.stats;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.modifiers.AscensionModifier;
import net.thejadeproject.ascension.refactor_packages.util.ByteBufHelper;
import net.thejadeproject.ascension.refactor_packages.util.IDataInstance;
import net.thejadeproject.ascension.refactor_packages.util.value_modifiers.ValueContainer;
import net.thejadeproject.ascension.refactor_packages.util.value_modifiers.ValueContainerModifier;

import java.util.HashMap;

public class StatInstance extends ValueContainer {

    private final Stat stat;

    public StatInstance(Stat stat, double base) {
        super(AscensionRegistries.Stats.STATS_REGISTRY.getKey(stat), stat.getDisplayName(), base);
        this.stat = stat;
    }

    public Stat getStat(){return stat;}


    public void log(){
        System.out.print(getDisplayName().getString()+" : ");
        System.out.print(getValue());
        System.out.println(" ("+getBaseValue()+")");
    }
}
