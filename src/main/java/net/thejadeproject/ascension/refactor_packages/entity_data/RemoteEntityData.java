package net.thejadeproject.ascension.refactor_packages.entity_data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.attributes.AscensionAttributeHolder;
import net.thejadeproject.ascension.refactor_packages.bloodlines.IBloodline;
import net.thejadeproject.ascension.refactor_packages.bloodlines.IBloodlineData;
import net.thejadeproject.ascension.refactor_packages.forms.IEntityForm;
import net.thejadeproject.ascension.refactor_packages.forms.IEntityFormData;
import net.thejadeproject.ascension.refactor_packages.paths.PathData;
import net.thejadeproject.ascension.refactor_packages.physiques.IPhysique;
import net.thejadeproject.ascension.refactor_packages.physiques.IPhysiqueData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.skill_casting.SkillCastHandler;
import net.thejadeproject.ascension.refactor_packages.skills.IPersistentSkillData;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
//TODO implement properly
public class RemoteEntityData implements IEntityData{


    public RemoteEntityData(UUID attachedEntity){

    }

    @Override
    public UUID getAttachedEntity() {
        return null;
    }

    @Override
    public boolean isAttachedEntityLoaded() {
        return false;
    }

    @Override
    public void setAttachedEntityLoaded(boolean loaded) {

    }

    @Override
    public IEntityFormData getActiveFormData() {
        return null;
    }

    @Override
    public IEntityFormData removeEntityForm(ResourceLocation form) {
        return null;
    }

    @Override
    public IEntityFormData getEntityFormData(ResourceLocation form) {
        return null;
    }

    @Override
    public IEntityFormData getEntityFormData(IEntityForm form) {
        return null;
    }

    @Override
    public List<IEntityFormData> getFormData() {
        return List.of();
    }

    @Override
    public void addEntityForm(ResourceLocation form) {

    }

    @Override
    public void addEntityForm(ResourceLocation form, IEntityFormData formData) {

    }

    @Override
    public void setActiveForm(ResourceLocation activeForm) {

    }

    @Override
    public void setFormData(ResourceLocation form, IEntityFormData formData) {

    }

    @Override
    public boolean setPhysique(ResourceLocation physique) {
        return false;
    }

    @Override
    public boolean setPhysique(ResourceLocation physique, IPhysiqueData existingData) {
        return false;
    }

    @Override
    public boolean setPhysique(ResourceLocation physique, IPhysiqueData existingData, ResourceLocation form) {
        return false;
    }


    @Override
    public IPhysiqueData getPhysiqueData() {
        return null;
    }

    @Override
    public ResourceLocation getPhysiqueForm() {
        return null;
    }

    @Override
    public IPhysiqueData removePhysique() {
        return null;
    }

    @Override
    public IPhysique getPhysique() {
        return null;
    }

    @Override
    public void movePhysique(ResourceLocation form) {

    }

    @Override
    public void setBloodline(ResourceLocation bloodline) {

    }

    @Override
    public void setBloodline(ResourceLocation bloodline, IBloodlineData existingData) {

    }


    @Override
    public IBloodlineData getBloodlineData() {
        return null;
    }

    @Override
    public ResourceLocation getBloodlineForm() {
        return null;
    }

    @Override
    public IBloodlineData removeBloodline() {
        return null;
    }

    @Override
    public IBloodline getBloodline() {
        return null;
    }

    @Override
    public void moveBloodline(ResourceLocation form) {

    }

    @Override
    public boolean hasPath(ResourceLocation path) {
        return false;
    }

    @Override
    public boolean isCultivating() {
        return false;
    }

    @Override
    public boolean isCultivating(ResourceLocation path) {
        return false;
    }

    @Override
    public ResourceLocation getTechnique(ResourceLocation path) {
        return null;
    }

    @Override
    public ITechniqueData getTechniqueData(ResourceLocation path) {
        return null;
    }

    @Override
    public PathData getPathData(ResourceLocation path) {
        return null;
    }

    @Override
    public Collection<ResourceLocation> getPathDataForms(ResourceLocation path) {
        return List.of();
    }

    @Override
    public Collection<PathData> getAllPathData() {
        return List.of();
    }

    @Override
    public ITechniqueData removeTechnique(ResourceLocation path) {
        return null;
    }

    @Override
    public boolean setTechnique(ResourceLocation path) {
        return false;
    }

    @Override
    public boolean setTechnique(ResourceLocation technique, ITechniqueData techniqueData) {
        return false;
    }

    @Override
    public void addPathData(ResourceLocation path, PathData pathData) {

    }

    @Override
    public void removePath(ResourceLocation path) {

    }

    @Override
    public boolean isBreakingThrough(ResourceLocation path) {
        return false;
    }

    @Override
    public void giveSkill(ResourceLocation skill, ResourceLocation form) {

    }

    @Override
    public void giveSkill(ResourceLocation skill, IPersistentSkillData skillData, ResourceLocation form) {

    }

    @Override
    public void removeSkill(ResourceLocation skill, ResourceLocation form) {

    }

    @Override
    public boolean hasSkill(ResourceLocation skill) {
        return false;
    }

    @Override
    public IPersistentSkillData getSkillData(ResourceLocation skill) {
        return null;
    }

    @Override
    public Set<ResourceLocation> getAllSkills() {
        return Set.of();
    }

    @Override
    public SkillCastHandler getSkillCastHandler() {
        return null;
    }

    @Override
    public AscensionAttributeHolder getAscensionAttributeHolder() {
        return null;
    }

    @Override
    public void write(CompoundTag tag) {

    }
}
