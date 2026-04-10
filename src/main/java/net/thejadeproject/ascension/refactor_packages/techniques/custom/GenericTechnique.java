package net.thejadeproject.ascension.refactor_packages.techniques.custom;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.breakthroughs.IBreakthroughInstance;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.IEntityFormData;
import net.thejadeproject.ascension.refactor_packages.forms.forms.ModForms;
import net.thejadeproject.ascension.refactor_packages.paths.ModPaths;
import net.thejadeproject.ascension.refactor_packages.paths.PathData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.skills.custom.ModSkills;
import net.thejadeproject.ascension.refactor_packages.skills.custom.cultivation.skill_data.GenericCultivationSkillData;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;
import net.thejadeproject.ascension.refactor_packages.techniques.stability.IStabilityHandler;
import net.thejadeproject.ascension.refactor_packages.techniques.stability.LnStabilityHandler;

import java.util.Set;

public class GenericTechnique implements ITechnique {
    private ResourceLocation path;
    private Component title;
    private Component shortDescription;
    private Component description;
    private double baseRate;
    private Set<ResourceLocation> secondaryPaths;
    private final IStabilityHandler stabilityHandler = new LnStabilityHandler();
    public GenericTechnique(ResourceLocation path,Component title,double baseRate,Set<ResourceLocation> secondaryPaths){
        this.path = path;
        this.title = title;
        this.baseRate = baseRate;
        this.secondaryPaths = secondaryPaths;
    }



    @Override
    public Component getDisplayTitle() {
        return title;
    }

    @Override
    public Component getShortDescription() {
        return shortDescription;
    }

    @Override
    public Component getDescription() {
        return description;
    }

    @Override
    public ResourceLocation getPath() {
        return path;
    }

    @Override
    public void onTechniqueAdded(IEntityData heldEntity) {
        if(getPath().equals(ModPaths.ESSENCE.getId())){
            heldEntity.giveSkill(ModSkills.BASIC_CULTIVATION_SKILL.getId(),new GenericCultivationSkillData(baseRate, secondaryPaths), ModForms.MORTAL_VESSEL.getId());
        }
        if(getPath().equals(ModPaths.SWORD.getId())){
            heldEntity.giveSkill(ModSkills.SWORD_CULTIVATION_SKILL.getId(),ModForms.MORTAL_VESSEL.getId());
        }
    }

    @Override
    public void onTechniqueRemoved(IEntityData heldEntity, ITechniqueData techniqueData) {
        heldEntity.getPathData(getPath()).handleRealmChange(heldEntity.getPathData(getPath()).getMajorRealm(),0,heldEntity);
        if(getPath().equals(ModPaths.ESSENCE.getId())){
            heldEntity.removeSkill(ModSkills.BASIC_CULTIVATION_SKILL.getId(), ModForms.MORTAL_VESSEL.getId());
        }
        if(getPath().equals(ModPaths.SWORD.getId())){
            heldEntity.removeSkill(ModSkills.SWORD_CULTIVATION_SKILL.getId(),ModForms.MORTAL_VESSEL.getId());
        }
    }

    @Override
    public void onRealmChange(IEntityData entityData, int oldMajorRealm, int oldMinorRealm, int newMajorRealm, int newMinorRealm) {
        System.out.println("technique: "+AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.getKey(this).toString());
        System.out.println("realm: ("+oldMajorRealm+","+oldMinorRealm+") -> ("+newMajorRealm+","+newMinorRealm+")");
    }

    @Override
    public void onFormRemoved(IEntityData heldEntity, IEntityFormData removedForm, PathData pathData) {

    }

    @Override
    public void onFormAdded(IEntityData heldEntity, IEntityFormData addedForm, PathData pathData) {

    }

    @Override
    public boolean isCompatibleWith(ResourceLocation technique) {
        return AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(technique) instanceof GenericTechnique;
    }

    @Override
    public IStabilityHandler getStabilityHandler() {
        return stabilityHandler;
    }

    @Override
    public ITechniqueData freshTechniqueData(IEntityData heldEntity) {
        return null;
    }

    @Override
    public ITechniqueData fromCompound(CompoundTag tag) {
        return null;
    }

    @Override
    public ITechniqueData fromNetwork(RegistryFriendlyByteBuf buf) {
        return null;
    }

    @Override
    public IBreakthroughInstance freshBreakthroughData(IEntityData heldEntity) {
        return null;
    }

    @Override
    public IBreakthroughInstance breakthroughInstanceFromCompound(CompoundTag tag) {
        return null;
    }

    @Override
    public IBreakthroughInstance breakthroughInstanceFromNetwork(RegistryFriendlyByteBuf buf) {
        return null;
    }
}
