package net.thejadeproject.ascension.gui.elements.cultivation;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.elements.built_in.EasyLabel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;

public class StatRow extends RenderableElement {
    private final Holder<Attribute> attribute;
    private final EasyLabel valueLabel;

    public StatRow(UIFrame frame, String label, Holder<Attribute> attribute) {
        super(frame);
        this.attribute = attribute;
        setWidth(148);
        setHeight(10);

        EasyLabel nameLabel = new EasyLabel(frame);
        nameLabel.setText(Component.literal(label));
        nameLabel.setTextColor(0xFFAAAAAA);
        nameLabel.getPositioning().setX(0);
        nameLabel.getPositioning().setY(2);
        addChild(nameLabel);

        valueLabel = new EasyLabel(frame);
        valueLabel.setText(Component.literal("—"));
        valueLabel.setTextColor(0xFFFFFFFF);
        valueLabel.getPositioning().setX(80);
        valueLabel.getPositioning().setY(2);
        addChild(valueLabel);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        AttributeInstance inst = Minecraft.getInstance().player.getAttribute(attribute);
        valueLabel.setText(Component.literal(inst == null ? "—" : String.format("%.2f", inst.getValue())));
        super.render(gfx, mouseX, mouseY, partialTick);
    }
}
