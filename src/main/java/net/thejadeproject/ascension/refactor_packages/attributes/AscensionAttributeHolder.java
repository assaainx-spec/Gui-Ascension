package net.thejadeproject.ascension.refactor_packages.attributes;

import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;

import java.util.HashMap;

public class AscensionAttributeHolder {

    private final HashMap<Holder<Attribute>,AttributeValueContainer> attributes = new HashMap<>();

    private final LivingEntity attachedEntity;
    public AscensionAttributeHolder(LivingEntity attachedEntity){this.attachedEntity = attachedEntity;}

    public void addAttribute(Holder<Attribute> attributeHolder, Component displayName){
        attributes.put(attributeHolder,new AttributeValueContainer(attachedEntity,attributeHolder,displayName));
    }

    public void removeAttribute(Holder<Attribute> attributeHolder){
        attributes.remove(attributeHolder);
    }

    public AttributeValueContainer getAttribute(Holder<Attribute> attributeHolder){
        return attributes.get(attributeHolder);
    }

    public void updateAttributes(IEntityData entityData){
        for(AttributeValueContainer container:attributes.values()) container.updateValue(entityData);
        log();
        if(entityData.getActiveFormData() != null){
            entityData.getActiveFormData().getStatSheet().log();
        }
    }

    public void log(){
        System.out.println("Ascension Attributes: ");
        for(AttributeValueContainer instance : attributes.values()){
            instance.log();
        }
    }
}
