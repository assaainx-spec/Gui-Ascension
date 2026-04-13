package net.thejadeproject.ascension.refactor_packages.gui.elements.skill_view.slots;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.events.EasyEvents;
import net.lucent.easygui.gui.events.EventPhase;
import net.lucent.easygui.gui.events.type.EasyEvent;
import net.lucent.easygui.gui.textures.ITextureData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.refactor_packages.gui.elements.skill_view.ISkillDragContainer;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;

public class ActiveSkillIcon extends RenderableElement {

    private ResourceLocation skillId;
    private ITextureData skillIcon;
    private boolean hovered;
    public ActiveSkillIcon(UIFrame frame) {
        super(frame);
        setWidth(18);
        setHeight(18);
        addEventListener(EasyEvents.GLOBAL_MOUSE_MOVE_EVENT,this::onMouseMove, EventPhase.BUBBLE);
        addEventListener(EasyEvents.MOUSE_DOWN_EVENT,this::onMouseDown,EventPhase.BUBBLE);
    }
    public void onMouseDown(EasyEvent event){
        if(event.getTarget() != this) return;

        if(getUiFrame().getElementById("container") instanceof ISkillDragContainer container){
            container.setHeldSkill(skillId);
        }
    }
    public void setSkill(ResourceLocation skill){
        this.skillId = skill;
        skillIcon = skill != null ? AscensionRegistries.Skills.SKILL_REGISTRY.get(skill).getIcon() : null;
    }
    public ResourceLocation getSkill(){
        return skillId;
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        super.render(gfx, mouseX, mouseY, partialTick);
        gfx.fill(0,  0,  18, 18, 0xFF0A1E30);
        gfx.fill(0,  0,  18, 1,  0xFF2255AA);
        gfx.fill(0,  17, 18, 18, 0xFF2255AA);
        gfx.fill(0,  0,  1,  18, 0xFF2255AA);
        gfx.fill(17, 0,  18, 18, 0xFF2255AA);
        if (skillIcon != null) {
            gfx.pose().pushPose();
            gfx.pose().translate(1, 1, 0);
            skillIcon.render(gfx);
            gfx.pose().popPose();
        }
        if (isHovered()) gfx.fill(1, 1, 17, 17, 0x33FFFFFF);
    }
    public boolean isHovered(){return hovered;}
    public void onMouseMove(EasyEvent event){
        hovered = event.getTarget() == this;
    }
}
