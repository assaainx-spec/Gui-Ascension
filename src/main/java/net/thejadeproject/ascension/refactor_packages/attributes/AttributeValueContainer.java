package net.thejadeproject.ascension.refactor_packages.attributes;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.stats.Stat;
import net.thejadeproject.ascension.refactor_packages.stats.StatInstance;
import net.thejadeproject.ascension.refactor_packages.stats.StatSheet;
import net.thejadeproject.ascension.refactor_packages.util.value_modifiers.ValueContainer;
import net.thejadeproject.ascension.refactor_packages.util.value_modifiers.ValueContainerModifier;

import java.util.HashMap;

/**
 * need to be careful when handling remote entity data. this should only be held on Loaded entities
 * so when i store the entity data remotely ensure i remove this and add it to the remote instance
 *<br>
 * the remote entity data should listen to stat changed event and then "refresh"
 *<br>
 * TODO remember to save the suppression values
 */
public class AttributeValueContainer extends ValueContainer {
    private final Holder<Attribute> attributeHolder;
    private final LivingEntity attachedEntity;
    public AttributeValueContainer(LivingEntity attachedEntity,Holder<Attribute> attributeHolder, Component valueName) {
        super(attributeHolder.getKey().location(), valueName, 0);
        this.attributeHolder = attributeHolder;
        this.attachedEntity =attachedEntity;
        calculateCachedVal();
    }
    private final HashMap<Stat,ValueContainer> statMultipliers = new HashMap<>();
    double cachedBaseStat;
    double cachedAttributeValue;
    double suppressedValue;
    boolean suppressed;
    @Override
    public void calculateCachedVal() {
        if(cachedAttributeValue+cachedBaseStat != getBaseValue()) setBaseValue(cachedAttributeValue+cachedBaseStat);
        super.calculateCachedVal();
    }

    public void addStatScaling(Stat stat, double scaling){
        if(!statMultipliers.containsKey(stat)) statMultipliers.put(stat,new ValueContainer(
                AscensionRegistries.Stats.STATS_REGISTRY.getKey(stat),
                stat.getDisplayName(),
                0
                ));
        statMultipliers.get(stat).setBaseValue(Math.max(0,statMultipliers.get(stat).getBaseValue()+scaling));
    }
    public void removeStatScaling(Stat stat,double scaling){
        if(!statMultipliers.containsKey(stat)) return;

        addStatScaling(stat,-scaling);
    }
    public void addStatScalingModifier(Stat stat,ValueContainerModifier modifier){
        if(!statMultipliers.containsKey(stat)) statMultipliers.put(stat,new ValueContainer(
                AscensionRegistries.Stats.STATS_REGISTRY.getKey(stat),
                stat.getDisplayName(),
                0
        ));
        statMultipliers.get(stat).addModifier(modifier);

    }

    public void updateValue(IEntityData entityData){
        //used to retrieve the stat sheet for calculations
        StatSheet statSheet = entityData.getActiveFormData().getStatSheet();

        double baseVal = 0;
        for (Stat stat : statMultipliers.keySet()){
            StatInstance instance = statSheet.getStatInstance(stat);
            baseVal += instance.getValue()*statMultipliers.get(stat).getValue();
        }
        cachedBaseStat = baseVal;

        calculateCachedVal();
    }
    public void validateAttributeValue(){
        if(attachedEntity == null) return;
        if(attachedEntity.getAttribute(attributeHolder)!=null && attachedEntity.getAttribute(attributeHolder).getValue() != cachedAttributeValue) {
            cachedAttributeValue = attachedEntity.getAttribute(attributeHolder).getValue();
            calculateCachedVal();
        }
    }
    @Override
    public double getValue() {
        validateAttributeValue();
        return isSuppressed()?getSuppressedValue():super.getValue();
    }

    @Override
    public double getBaseValue() {
        validateAttributeValue();
        return super.getBaseValue();
    }

    public double getSuppressedValue(){
        return suppressedValue;
    }
    public void setSuppressedValue(double value){
        this.suppressedValue = value;
        setSuppressed(getValue()>getSuppressedValue());
    }
    public void setSuppressed(boolean suppressed){
        this.suppressed = suppressed;
    }
    public boolean isSuppressed(){
        if(super.getValue()<=getSuppressedValue()) setSuppressed(false);
        return suppressed;
    }

    public void log(){
        System.out.print(getDisplayName().getString() +" : ");
        System.out.print((isSuppressed() ? getSuppressedValue() : getValue()));
        if(isSuppressed()) System.out.print(" ("+getValue()+")");
        System.out.println(" base : "+getBaseValue());
    }
}
