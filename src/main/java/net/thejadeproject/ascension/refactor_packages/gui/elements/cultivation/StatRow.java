package net.thejadeproject.ascension.refactor_packages.gui.elements.cultivation;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public class StatRow extends RenderableElement {
    private final Holder<Attribute> attribute;
    private final String label;

    public StatRow(UIFrame frame, String label, Holder<Attribute> attribute) {
        super(frame);
        this.label = label;
        this.attribute = attribute;
        setWidth(148);
        setHeight(10);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        super.render(gfx, mouseX, mouseY, partialTick);
        AttributeInstance inst = Minecraft.getInstance().player.getAttribute(attribute);
        String valueStr = inst == null ? "—" : String.format("%.2f", inst.getValue());
        gfx.drawString(Minecraft.getInstance().font, label, 0, 1, 0xFFAAAAAA, false);
        gfx.drawString(Minecraft.getInstance().font, "·  " + valueStr, 80, 1, 0xFFFFFFFF, false);
    }
}
