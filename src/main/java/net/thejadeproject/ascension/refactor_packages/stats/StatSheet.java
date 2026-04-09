package net.thejadeproject.ascension.refactor_packages.stats;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.util.IDataInstance;
import net.thejadeproject.ascension.refactor_packages.util.value_modifiers.ValueContainerModifier;
import org.checkerframework.checker.units.qual.C;

import java.util.HashMap;

public class StatSheet {

    private final HashMap<Stat,StatInstance> sheetStats = new HashMap<>();


    public StatInstance getStatInstance(Stat stat){
        return sheetStats.get(stat);
    }



    public void addStat(Stat stat, double value){
        if(!sheetStats.containsKey(stat)) sheetStats.put(stat,stat.instance());

        sheetStats.get(stat).setBaseValue(Math.max(0,sheetStats.get(stat).getBaseValue()+value));
    }
    public void removeStat(Stat stat,double value){
        if(!sheetStats.containsKey(stat)) return;

        addStat(stat,-value);
    }
    public void addStatModifier(Stat stat, ValueContainerModifier modifier){
        if(!sheetStats.containsKey(stat)) sheetStats.put(stat,stat.instance());
        sheetStats.get(stat).addModifier(modifier);
    }
    public boolean hasStat(Stat stat){return sheetStats.containsKey(stat);}


    public void log(){
        System.out.println("Stats:");
        for(StatInstance instance:sheetStats.values()){
            instance.log();
        }
    }
}
