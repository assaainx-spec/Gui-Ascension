package net.thejadeproject.ascension.refactor_packages.gui.elements.cultivation;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.elements.built_in.EasyLabel;
import net.lucent.easygui.gui.events.EasyEvents;
import net.lucent.easygui.gui.events.EventPhase;
import net.lucent.easygui.gui.events.type.EasyEvent;
import net.lucent.easygui.gui.events.type.EasyMouseEvent;
import net.lucent.easygui.gui.textures.ITextureData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.thejadeproject.ascension.refactor_packages.gui.elements.skill_view.ISkillDragContainer;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;

public class SkillRow extends RenderableElement {

    private final ResourceLocation skillId;
    private final ITextureData icon;
    private final boolean isActive;
    private boolean hovered;

    public SkillRow(UIFrame frame, ResourceLocation skillId, boolean isActive) {
        super(frame);
        this.skillId = skillId;
        this.isActive = isActive;
        setWidth(154);
        setHeight(18);

        var skill = AscensionRegistries.Skills.SKILL_REGISTRY.get(skillId);
        this.icon = skill != null ? skill.getIcon() : null;
        Component name = skill != null ? skill.getTitle() : Component.literal(skillId.getPath());

        EasyLabel nameLabel = new EasyLabel(frame);
        nameLabel.setText(name);
        nameLabel.setTextColor(isActive ? 0xFF88BBFF : 0xFFBB88FF);
        nameLabel.setTextScale(0.75f);
        nameLabel.getPositioning().setX(21);
        nameLabel.getPositioning().setY(5);
        addChild(nameLabel);

        EasyLabel typeLabel = new EasyLabel(frame);
        typeLabel.setText(Component.literal(isActive ? "ACT" : "PAS"));
        typeLabel.setTextColor(0xFF888888);
        typeLabel.setTextScale(0.75f);
        typeLabel.getPositioning().setX(135);
        typeLabel.getPositioning().setY(5);
        addChild(typeLabel);

        addEventListener(EasyEvents.GLOBAL_MOUSE_MOVE_EVENT, this::onMouseMove, EventPhase.BUBBLE);
        addEventListener(EasyEvents.MOUSE_DOWN_EVENT, this::onMouseDown, EventPhase.BUBBLE);
    }

    private void onMouseMove(EasyEvent event) {
        if (!(event instanceof EasyMouseEvent me)) return;
        Vec2 local = globalToLocalPositionPoint((float) me.getMouseX(), (float) me.getMouseY());
        hovered = local.x >= 0 && local.x < getWidth() && local.y >= 0 && local.y < getHeight();
    }

    private void onMouseDown(EasyEvent event) {
        if (!(event instanceof EasyMouseEvent)) return;
        if (getUiFrame().getElementById("container") instanceof ISkillDragContainer container) {
            container.setHeldSkill(skillId);
        }
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        int bg = isActive ? 0xFF0A1E30 : 0xFF1A0A20;
        gfx.fill(0, 0, getWidth(), getHeight() - 1, bg);
        if (hovered) gfx.fill(0, 0, getWidth(), getHeight() - 1, 0x22FFFFFF);
        if (icon != null) {
            gfx.pose().pushPose();
            gfx.pose().translate(1, 1, 0);
            icon.render(gfx);
            gfx.pose().popPose();
        }
        gfx.fill(0, 17, getWidth(), 18, 0x144FC3F7);
        super.render(gfx, mouseX, mouseY, partialTick);
    }
}
