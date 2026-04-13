package net.thejadeproject.ascension.refactor_packages.gui.elements.skill_view;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.events.EasyEvents;
import net.lucent.easygui.gui.events.type.EasyEvent;
import net.lucent.easygui.gui.events.type.EasyMouseEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.gui.elements.general.ScrollBox;
import net.thejadeproject.ascension.refactor_packages.gui.elements.skill_view.slots.PassiveSkillIcon;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.skills.castable.ICastableSkill;

public class PassiveSkillList extends RenderableElement {
    ScrollBox scrollBox;
    private Runnable onClose;

    public PassiveSkillList(UIFrame frame) {
        super(frame);
        setWidth(122);
        setHeight(208);

        this.scrollBox = new ScrollBox(frame, 22);
        this.scrollBox.setWidth(98);
        this.scrollBox.setHeight(180);
        scrollBox.getPositioning().setY(16);
        scrollBox.getPositioning().setX(12);

        refreshSkills();
        addChild(scrollBox);
        addEventListener(EasyEvents.MOUSE_DOWN_EVENT, this::onMouseDown);
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
    }

    public void refreshSkills() {
        IEntityData entityData = Minecraft.getInstance().player.getData(ModAttachments.ENTITY_DATA);
        for (ResourceLocation skillId : entityData.getAllSkills()) {
            if (AscensionRegistries.Skills.SKILL_REGISTRY.get(skillId) instanceof ICastableSkill skill) continue;
            scrollBox.addChild(new PassiveSkillIcon(getUiFrame(), skillId));
        }
    }

    private void onMouseDown(EasyEvent event) {
        if (!(event instanceof EasyMouseEvent mouseEvent)) return;
        Vec2 local = globalToLocalPositionPoint((float) mouseEvent.getMouseX(), (float) mouseEvent.getMouseY());
        int bx = getWidth() - 12;
        if (local.x >= bx && local.x < bx + 10 && local.y >= 2 && local.y < 12) {
            if (onClose != null) onClose.run();
            event.setCanceled(true);
        }
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        int w = getWidth(), h = getHeight();
        var font = Minecraft.getInstance().font;
        gfx.fill(0, 0, w, h, 0xCC050810);
        gfx.fill(0,   0,   w,   1,   0xFF4FC3F7);
        gfx.fill(0,   h-1, w,   h,   0xFF4FC3F7);
        gfx.fill(0,   0,   1,   h,   0xFF4FC3F7);
        gfx.fill(w-1, 0,   w,   h,   0xFF4FC3F7);
        gfx.fill(0, 13, w, 14, 0x55006396);
        // corner bracket accents
        gfx.fill(1, 1, 4, 2, 0x88006396);  gfx.fill(1, 1, 2, 4, 0x88006396);
        gfx.fill(w-4, 1, w-1, 2, 0x88006396);  gfx.fill(w-2, 1, w-1, 4, 0x88006396);
        gfx.fill(1, h-2, 4, h-1, 0x88006396);  gfx.fill(1, h-4, 2, h-1, 0x88006396);
        gfx.fill(w-4, h-2, w-1, h-1, 0x88006396);  gfx.fill(w-2, h-4, w-1, h-1, 0x88006396);

        gfx.drawString(font, "PASSIVE", 5, 3, 0xFF4FC3F7, false);
        gfx.drawString(font, "\u00d7", w - 12 + (10 - font.width("\u00d7") + 1) / 2, 3, 0xFFFF5555, false);
        super.render(gfx, mouseX, mouseY, partialTick);
    }
}
