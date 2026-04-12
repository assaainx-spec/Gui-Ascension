package net.thejadeproject.ascension.gui.elements.cultivation;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.neoforged.neoforge.network.PacketDistributor;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.network.server_bound.cultivation.UpdateSuppressionValue;

public class LimiterStatRow extends RenderableElement {
    private static final ResourceLocation SUPPRESSION_ID =
            ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "suppression_modifier");

    private final Holder<Attribute> attribute;
    private final ResourceLocation attributeId;
    private final String label;

    public LimiterStatRow(UIFrame frame, String label, Holder<Attribute> attribute, ResourceLocation attributeId) {
        super(frame);
        this.label = label;
        this.attribute = attribute;
        this.attributeId = attributeId;
        setWidth(148);
        setHeight(12);
    }

    public void tryClick(double lx, double ly) {
        int plusX = getWidth() - 11;
        int minusX = plusX - 13;
        if (lx >= minusX && lx <= minusX + 8 && ly >= 1 && ly <= 11) {
            changeSuppression(getStep());
        } else if (lx >= plusX && lx <= plusX + 8 && ly >= 1 && ly <= 11) {
            changeSuppression(-getStep());
        }
    }

    private double getCurrentSuppression() {
        AttributeInstance inst = Minecraft.getInstance().player.getAttribute(attribute);
        if (inst == null) return 0.0;
        AttributeModifier mod = inst.getModifier(SUPPRESSION_ID);
        return mod == null ? 0.0 : Math.abs(mod.amount());
    }

    private double getStep() {
        if (Screen.hasShiftDown() && Screen.hasControlDown()) return 1.0;
        if (Screen.hasControlDown()) return 0.001;
        if (Screen.hasShiftDown()) return 0.1;
        return 0.01;
    }

    private void changeSuppression(double delta) {
        double current = getCurrentSuppression();
        double next = Math.max(0.0, Math.min(1.0, current + delta));
        PacketDistributor.sendToServer(new UpdateSuppressionValue(attributeId, next));
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        super.render(gfx, mouseX, mouseY, partialTick);
        AttributeInstance inst = Minecraft.getInstance().player.getAttribute(attribute);
        String realStr = inst == null ? "—" : String.format("%.2f", inst.getValue());

        var font = Minecraft.getInstance().font;
        int plusX = getWidth() - 11;
        int minusX = plusX - 13;

        gfx.drawString(font, label, 0, 2, 0xFFAAAAAA, false);
        gfx.drawString(font, realStr, 80, 2, 0xFFFFFFFF, false);

        // − button (suppress more / lower stat)
        gfx.fill(minusX, 1, minusX + 8, 11, 0xFF222222);
        gfx.drawString(font, "-", minusX + 1, 2, 0xFFFFFFFF, false);

        // + button (suppress less / restore stat)
        gfx.fill(plusX, 1, plusX + 8, 11, 0xFF222222);
        gfx.drawString(font, "+", plusX + 1, 2, 0xFFFFFFFF, false);
    }
}
