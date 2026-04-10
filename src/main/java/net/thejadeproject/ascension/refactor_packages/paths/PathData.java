package net.thejadeproject.ascension.refactor_packages.paths;

import net.minecraft.nbt.*;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.breakthroughs.IBreakthroughInstance;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.IEntityFormData;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.entity_data.path_data.SyncPathData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechniqueData;
import net.thejadeproject.ascension.refactor_packages.util.ByteBufHelper;
import oshi.util.tuples.Pair;

import java.util.*;

/*
    holds the instanced data of a path
    like cultivation progress and technique used for that progress
    *DOES NOT STORE CURRENT TECHNIQUE*
 */
public class PathData {
    private ResourceLocation path;

    private int majorRealm;
    private int minorRealm;
    private double currentRealmProgress;
    private int currentRealmStability;
    private boolean cultivating;
    private ResourceLocation lastUsedTechnique; //the current technique

    public PathData(ResourceLocation path){
        this.path = path;
    }


    /*
        stores the stability of each realm, the reason im not just storing the current stability
        is that this makes it easier to handle balance changes
        stores the stability of the realm im breaking into
        e.g if im 0,9-1,0 with 100 stability then it is stored under 1
        however, the technique used is the one in new Major realm -1
        so in the example 0s
    */
    private final ArrayList<Integer> realmStability = new ArrayList<>();

    private boolean breakingThrough;
    //only for the current breakthrough
    private IBreakthroughInstance breakthroughInstance;

    /*
        stores a history of what techniques where used to reach each major realm
        useful for compatibility and balance change stuff
        does not store the current major realm and current technique
     */
    private final ArrayList<ResourceLocation> techniqueHistory = new ArrayList<>();
    private final HashMap<ResourceLocation, ITechniqueData> techniqueData = new HashMap<>();
    public Set<Integer> getTechniqueRealms(ResourceLocation technique){
        HashSet<Integer> realms = new HashSet<>();
        for(int i =0;i<techniqueHistory.size();i++){
            if(technique.equals(techniqueHistory.get(i))) realms.add(i);
        }
        return realms;
    }
    public Set<Pair<Integer,Integer>> getTechniqueBreakthroughs(ResourceLocation technique){
        HashSet<Pair<Integer,Integer>> realms = new HashSet<>();
        for(int realm =0; realm<techniqueHistory.size();realm++){
            if(techniqueHistory.get(realm).equals(technique) && realm<realmStability.size()){
                realms.add(new Pair<>(realm,realmStability.get(realm)));
            }
        }
        return realms;
    }

    public boolean isBreakingThrough(){return this.breakingThrough;}
    public void setBreakingThrough(boolean state){breakingThrough = state;}

    public void setBreakthroughInstance(IBreakthroughInstance instance){this.breakthroughInstance = instance;}
    public IBreakthroughInstance getBreakthroughInstance(){return breakthroughInstance;}

    public int getMajorRealm(){return majorRealm;}
    public int getMinorRealm(){return minorRealm;}
    public double getCurrentRealmProgress(){return currentRealmProgress;}
    public int getCurrentRealmStability(){return currentRealmStability;}
    public ResourceLocation getLastUsedTechnique(){return lastUsedTechnique;}
    public ResourceLocation getPath(){return path;}
    public boolean isCultivating(){return this.cultivating;}
    public ITechniqueData getTechniqueData(ResourceLocation technique){
        return techniqueData.get(technique);
    }

    //if you want to stability for when you breakthrough from 0-1 you put 0, since that is the realm you where stable in
    public int getStability(int realm){
        return realmStability.get(realm);
    }
    public void handleRealmChange(int newMajorRealm,int newMinorRealm,IEntityData entityData){
        if(lastUsedTechnique == null) return;
        //TODO
        //make sure to properly add and remove data as needed, calling the proper methods
        if(newMajorRealm > majorRealm || (newMajorRealm == majorRealm && newMinorRealm > minorRealm)){
            ITechnique technique = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(lastUsedTechnique);
            int boundedMajorRealm = Math.min(newMajorRealm,technique.getMaxMajorRealm());
            int boundedMinorRealm = Math.min(newMinorRealm,technique.getMaxMinorRealm(boundedMajorRealm));

            //realm increased;
            if(boundedMajorRealm > majorRealm){
                realmStability.add(currentRealmStability);
                techniqueHistory.add(lastUsedTechnique);

                currentRealmStability = 0;
                //majorRealm breakthrough
                for(int i=majorRealm+1;i<boundedMajorRealm;i++){
                    if(i >=realmStability.size()) realmStability.add(currentRealmStability); //generally not recommended to increase by multiple major realms at once cus of this
                    if(i >=techniqueHistory.size()) techniqueHistory.add(lastUsedTechnique);
                }

            }
            int oldMajorRealm = majorRealm;
            int oldMinorRealm = minorRealm;

            this.minorRealm = boundedMinorRealm;
            this.majorRealm = boundedMajorRealm;
            currentRealmProgress = 0.0;
            technique.onRealmChange(entityData,oldMajorRealm,oldMinorRealm,boundedMajorRealm,boundedMinorRealm);
            //TODO handle event stuff

        }else {
            //TODO handle case where we go from say 5 0 -> 3 2 and the technique at 3 is different than the current technique
            //TODO in this scenario change to 3 0 then make sure we have correct technique and update to 3 2
            //realm decreased
            ITechnique technique = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(lastUsedTechnique);
            technique.onRealmChange(entityData,majorRealm,minorRealm,newMajorRealm,newMinorRealm);
            //TODO HANDLE EVENT STUFF
            if(newMajorRealm<majorRealm){
                //majorRealm changed
                for(int i=majorRealm;i>newMajorRealm;i--){
                    //TODO handle removal, for each major realm decreased
                    //TODO basically add technique. then reduce cultivation, then remove technique
                    realmStability.removeLast();//generally not recommended to increase by multiple major realms at once cus of this
                    techniqueHistory.removeLast();
                }
            }
            this.minorRealm = newMinorRealm;
            this.majorRealm = newMajorRealm;
        }

    }
    public void resetCultivation(){
        //TODO
        //similar to realm change BUT also removes the technique learned at 0 0
    }

    //TODO add checks to make sure it can be set to this new value
    //TODO also have it move technique to history on major realm change
    public void setMajorRealm(int majorRealm){
        this.majorRealm = majorRealm;
    }
    public void setMinorRealm(int minorRealm){this.minorRealm = minorRealm;}
    public void setCurrentRealmProgress(double progress){this.currentRealmProgress = progress;}
    public void setCurrentRealmStability(int cultivationTicks){this.currentRealmStability = cultivationTicks;}
    public void setLastUsedTechnique(ResourceLocation technique){
        this.lastUsedTechnique = technique;
    }
    public void addTechniqueData(ResourceLocation technique,ITechniqueData techniqueData){
        if(techniqueData == null) return;
        this.techniqueData.put(technique, techniqueData);
    }
    public ITechniqueData removeTechniqueData(ResourceLocation technique){
        return techniqueData.remove(technique);
    }
    public void setCultivating(boolean cultivating){this.cultivating = cultivating;}

    public void removeLastUsedTechnique(){
        if(!techniqueHistory.contains(lastUsedTechnique)) techniqueData.remove(lastUsedTechnique);
        lastUsedTechnique = null;
    }
    public void onFormRemoved(IEntityData heldEntity, IEntityFormData removedFormData){
        for(ResourceLocation technique : techniqueData.keySet()){
            AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(technique).onFormRemoved(heldEntity,removedFormData,this);
        }
    };
    public void onFormAdded(IEntityData heldEntity, IEntityFormData addedFormData){
        for(ResourceLocation technique : techniqueData.keySet()){
            AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(technique).onFormAdded(heldEntity,addedFormData,this);
        }
    };

    public Collection<ResourceLocation> getTechniqueHistory(){
        return techniqueHistory;
    }

    public CompoundTag write(){
        CompoundTag tag = new CompoundTag();
        tag.putInt("major_realm",majorRealm);
        tag.putInt("minor_realm",minorRealm);
        tag.putDouble("progress",currentRealmProgress);
        tag.putInt("stability",currentRealmStability);
        tag.putString("technique",lastUsedTechnique==null?"none":lastUsedTechnique.toString());
        ListTag previousStability = new ListTag();
        for (Integer stability : realmStability) {
            previousStability.add(IntTag.valueOf(stability));
        }
        tag.put("previous_stability",previousStability);
        ListTag techniqueDataTags = new ListTag();
        for(ResourceLocation technique : this.techniqueData.keySet()){
            CompoundTag techniqueDataTag = new CompoundTag();
            techniqueDataTag.putString("technique",technique.toString());
            techniqueDataTag.put("data",techniqueData.get(technique) == null? new CompoundTag(): techniqueData.get(technique).write());
            techniqueDataTags.add(techniqueDataTag);
        }
        tag.put("technique_data",techniqueDataTags);
        ListTag techniqueHistory = new ListTag();
        for (ResourceLocation technique : this.techniqueHistory) {
            techniqueHistory.add(StringTag.valueOf(technique.toString()));
        }
        tag.put("technique_history",techniqueHistory);

        return tag;
    }

    public void read(CompoundTag tag,IEntityData entityData){
        ListTag previousStability = tag.getList("previous_stability", Tag.TAG_INT);
        ListTag techniqueData = tag.getList("technique_data",Tag.TAG_COMPOUND);
        ListTag techniqueHistory = tag.getList("technique_history",Tag.TAG_STRING);

        //load stability
        for(int i =0;i<previousStability.size();i++){
            int stability = previousStability.getInt(i);
            this.realmStability.add(stability);
        }
        //load techniques
        HashMap<ResourceLocation,ITechniqueData> cachedTechniqueData = new HashMap<>();
        for(int i=0;i<techniqueData.size();i++){
            CompoundTag techniqueDataTag = techniqueData.getCompound(i);
            ResourceLocation techniqueId = ResourceLocation.bySeparator(techniqueDataTag.getString("technique"),':');
            ITechniqueData techniqueDataInstance = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(techniqueId).fromCompound(techniqueDataTag.getCompound("data"));
            if(techniqueDataInstance != null) cachedTechniqueData.put(techniqueId,techniqueDataInstance);
        }
        //simulate history
        for(int i =0;i<techniqueHistory.size();i++){
            ResourceLocation techniqueId = ResourceLocation.bySeparator(techniqueHistory.getString(i),':');

            if(entityData.setTechnique(techniqueId,cachedTechniqueData.get(techniqueId))){
                System.out.println("current technique for sim : "+(lastUsedTechnique == null?"none":lastUsedTechnique.toString()));
            }else System.out.println("failed to set technique for sim");
            handleRealmChange(i+1,0,entityData);
        }

        //simulate current realm
        int majorRealm = tag.getInt("major_realm");
        int minorRealm = tag.getInt("minor_realm");
        double progress = tag.getDouble("progress");
        int stability = tag.getInt("stability");
        String rawTechnique = tag.getString("technique");
        if(!rawTechnique.equals("none")){
            ResourceLocation technique = ResourceLocation.bySeparator(tag.getString("technique"),':');

            entityData.setTechnique(technique,cachedTechniqueData.get(technique));

            handleRealmChange(majorRealm,minorRealm,entityData);

            this.currentRealmProgress = progress;
            this.currentRealmStability = stability;
        }


    }

    /**
     *
     * @param player the player this is attached to, not used for syncing other players
     */
    public void sync(Player player){
        IEntityData entityData = player.getData(ModAttachments.ENTITY_DATA);
        for(ResourceLocation form : entityData.getPathDataForms(getPath()))  PacketDistributor.sendToPlayer((ServerPlayer) player,new SyncPathData(form,this));

    }
    //encodes full path data
    public void encode(RegistryFriendlyByteBuf buf){
        //TODO
        buf.writeInt(majorRealm);
        buf.writeInt(minorRealm);
        buf.writeDouble(currentRealmProgress);
        buf.writeInt(currentRealmStability);
        buf.writeBoolean(cultivating);
        buf.writeBoolean(lastUsedTechnique != null);
        if(lastUsedTechnique != null) ByteBufHelper.encodeString(buf,lastUsedTechnique.toString());

        //breakthrough stuff
        buf.writeBoolean(breakingThrough);
        buf.writeBoolean(breakthroughInstance != null);
        if(breakthroughInstance != null){
            breakthroughInstance.encode(buf);
        }
        buf.writeInt(realmStability.size());
        for(Integer stability : realmStability){
            buf.writeInt(stability);
        }
        buf.writeInt(techniqueHistory.size());
        for(ResourceLocation technique:techniqueHistory){
            ByteBufHelper.encodeString(buf,technique.toString());
        }
        buf.writeInt(techniqueData.size());
        for(ResourceLocation technique:techniqueData.keySet()){
            ByteBufHelper.encodeString(buf,technique.toString());
            System.out.println("trying to write data for skill : "+technique.toString());
            techniqueData.get(technique).encode(buf);
        }

    }
    public void decode(RegistryFriendlyByteBuf buf){
        //TODO
        majorRealm = buf.readInt();
        minorRealm = buf.readInt();
        currentRealmProgress = buf.readDouble();
        currentRealmStability = buf.readInt();
        cultivating = buf.readBoolean();
        if(buf.readBoolean())lastUsedTechnique = ByteBufHelper.readResourceLocation(buf);

        breakingThrough = buf.readBoolean();
        if(buf.readBoolean()){
            breakthroughInstance = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(lastUsedTechnique).breakthroughInstanceFromNetwork(buf);
        }
        int size = buf.readInt();
        realmStability.clear();
        for(int i=0;i<size;i++){
            realmStability.add(buf.readInt());
        }
        size = buf.readInt();
        techniqueHistory.clear();
        for(int i=0;i<size;i++){
            techniqueHistory.add(ByteBufHelper.readResourceLocation(buf));
        }
        size = buf.readInt();
        techniqueData.clear();
        for(int i =0;i<size;i++){
            ResourceLocation technique = ByteBufHelper.readResourceLocation(buf);
            ITechniqueData techniqueDataInstance = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(technique).fromNetwork(buf);
            techniqueData.put(technique,techniqueDataInstance);
        }
    }
}
