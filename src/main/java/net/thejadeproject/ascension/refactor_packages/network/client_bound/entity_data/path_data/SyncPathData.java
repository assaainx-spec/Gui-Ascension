package net.thejadeproject.ascension.refactor_packages.network.client_bound.entity_data.path_data;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.forms.IEntityFormData;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.entity_data.skills.SyncHeldSkills;
import net.thejadeproject.ascension.refactor_packages.network.client_bound.entity_data.skills.casting.SyncCastingInstance;
import net.thejadeproject.ascension.refactor_packages.paths.PathData;
import net.thejadeproject.ascension.refactor_packages.skills.HeldSkills;
import net.thejadeproject.ascension.refactor_packages.util.ByteBufHelper;

public record SyncPathData(ResourceLocation form,PathData pathData)implements CustomPacketPayload {

    public static final Type<SyncPathData> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID,"sync_path_data"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SyncPathData> STREAM_CODEC =
            StreamCodec.of(SyncPathData::encode, SyncPathData::decode);

    public static void encode(RegistryFriendlyByteBuf buf,SyncPathData packet){
        ByteBufHelper.encodeString(buf,packet.form.toString());
        ByteBufHelper.encodeString(buf,packet.pathData.getPath().toString());
        packet.pathData.encode(buf);
    }
    public static SyncPathData decode(RegistryFriendlyByteBuf buf){
        ResourceLocation form = ByteBufHelper.readResourceLocation(buf);
        ResourceLocation path = ByteBufHelper.readResourceLocation(buf);
        PathData pathData = new PathData(path);
        pathData.decode(buf);
        return new SyncPathData(form,pathData);
    }
    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
    public static void handlePayload(SyncPathData payload, IPayloadContext context) {
        context.enqueueWork(()->{
            IEntityData entityData = context.player().getData(ModAttachments.ENTITY_DATA);
            IEntityFormData formData = entityData.getEntityFormData(payload.form);
            System.out.println("trying to sync for form: "+payload.form);
            formData.addPathData(payload.pathData.getPath(),payload.pathData);

        });
    }
}