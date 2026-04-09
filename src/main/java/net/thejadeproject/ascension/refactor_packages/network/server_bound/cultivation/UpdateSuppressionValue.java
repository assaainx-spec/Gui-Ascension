package net.thejadeproject.ascension.refactor_packages.network.server_bound.cultivation;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.thejadeproject.ascension.AscensionCraft;

public record UpdateSuppressionValue(ResourceLocation attributeId, double value) implements CustomPacketPayload {
    public static final Type<UpdateSuppressionValue> TYPE = new Type<>(
            ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "update_suppression_value"));
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateSuppressionValue> STREAM_CODEC =
            StreamCodec.composite(
                    ResourceLocation.STREAM_CODEC,
                    UpdateSuppressionValue::attributeId,
                    ByteBufCodecs.DOUBLE,
                    UpdateSuppressionValue::value,
                    UpdateSuppressionValue::new
            );

    private static final ResourceLocation SUPPRESSION_ID =
            ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "suppression_modifier");

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handlePayload(UpdateSuppressionValue payload, IPayloadContext context) {
        context.enqueueWork(() -> {
            Holder<Attribute> holder = BuiltInRegistries.ATTRIBUTE
                    .getHolder(payload.attributeId()).orElse(null);
            if (holder == null) return;
            AttributeInstance instance = context.player().getAttribute(holder);
            if (instance == null) return;
            double clamped = Math.max(0.0, Math.min(1.0, payload.value()));
            instance.removeModifier(SUPPRESSION_ID);
            if (clamped > 0.0) {
                instance.addPermanentModifier(new AttributeModifier(
                        SUPPRESSION_ID,
                        -clamped,
                        AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL
                ));
            }
        });
    }
}
