package net.thejadeproject.ascension.refactor_packages.gui.elements.skill_view.slots;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.elements.built_in.EasyLabel;
import net.lucent.easygui.gui.events.EasyEvents;
import net.lucent.easygui.gui.events.EventPhase;
import net.lucent.easygui.gui.events.type.EasyEvent;
import net.lucent.easygui.gui.textures.ITextureData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;

public class PassiveSkillIcon extends RenderableElement {
    private ResourceLocation skillId;
    private ITextureData skillIcon;
    private Component title;
    private boolean hovered;

    private EasyLabel label;

    public PassiveSkillIcon(UIFrame frame, ResourceLocation skill) {
        super(frame);
        setWidth(92);
        setHeight(20);
        label = new EasyLabel(frame);
        label.getPositioning().setX(27);
        label.getPositioning().setY(5);
        label.setWidth(65);
        label.setHeight(8);
        label.setTextPositioningY(EasyLabel.TextPositionRule.CENTER);
        label.setTextPositioningX(EasyLabel.TextPositionRule.START);

        label.setTextScale(0.5f);
        setSkill(skill);
        addEventListener(EasyEvents.GLOBAL_MOUSE_MOVE_EVENT,this::onMouseMove, EventPhase.BUBBLE);
        addChild(label);
        this.skillId = skill;
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        int w = getWidth(), h = getHeight();
        gfx.fill(0, 0, w, h, 0xCC050810);
        if (isHovered()) gfx.fill(0, 0, w, h, 0x22FFFFFF);
        gfx.fill(0, h - 1, w, h, 0x33006396);
        if (skillIcon != null) skillIcon.renderAt(gfx, 2, 2);
        super.render(gfx, mouseX, mouseY, partialTick);
    }

    public void setSkill(ResourceLocation skill){
        this.skillId = skill;
        skillIcon = skill != null ? AscensionRegistries.Skills.SKILL_REGISTRY.get(skill).getIcon() : null;
        Component text = skill != null ? AscensionRegistries.Skills.SKILL_REGISTRY.get(skill).getTitle() : Component.empty();
        if(text == null) text = Component.empty();
        label.setText(text);
    }
    public boolean isHovered(){return hovered;}
    public void onMouseMove(EasyEvent event){
        hovered = event.getTarget() == this;
    }
}
