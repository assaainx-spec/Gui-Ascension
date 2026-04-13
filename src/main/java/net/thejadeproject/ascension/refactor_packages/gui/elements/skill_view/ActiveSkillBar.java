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
import net.thejadeproject.ascension.refactor_packages.gui.elements.general.Container;
import net.thejadeproject.ascension.refactor_packages.gui.elements.skill_view.slots.ActiveSkillSlot;

public class ActiveSkillBar extends RenderableElement {
    Container container;

    private static final int BTN_X = 144;
    private static final int BTN_Y = 2;
    private static final int BTN_W = 43;
    private static final int BTN_H = 10;

    public ActiveSkillBar(UIFrame frame) {
        super(frame);
        setWidth(192);
        setHeight(83);
        container = new Container(frame, 162, 36);
        for (int i = 0; i < 18; i++) {
            ActiveSkillSlot slot = new ActiveSkillSlot(frame, i);
            slot.getPositioning().setX(i % 9 * 18);
            slot.getPositioning().setY(i < 9 ? 0 : 18);
            container.addChild(slot);
        }
        container.getPositioning().setY(32);
        container.getPositioning().setX(15);
        addChild(container);
        addEventListener(EasyEvents.MOUSE_DOWN_EVENT, this::onMouseDown);
    }

    public void removeSkill(ResourceLocation skill) {
        if (skill == null) return;
        for (RenderableElement child : container.getChildren()) {
            if (child instanceof ActiveSkillSlot slot) {
                if (skill.equals(slot.getSkill())) {
                    slot.setSkillNoPacket(null);
                }
            }
        }
    }

    private void onMouseDown(EasyEvent event) {
        if (!(event instanceof EasyMouseEvent mouseEvent)) return;
        Vec2 local = globalToLocalPositionPoint((float) mouseEvent.getMouseX(), (float) mouseEvent.getMouseY());
        if (local.x >= BTN_X && local.x < BTN_X + BTN_W && local.y >= BTN_Y && local.y < BTN_Y + BTN_H) {
            clearAll();
            event.setCanceled(true);
        }
    }

    private void clearAll() {
        for (RenderableElement child : container.getChildren()) {
            if (child instanceof ActiveSkillSlot slot) {
                slot.setSkill(null);
            }
        }
        if (getUiFrame().getElementById("container") instanceof ISkillDragContainer drag) {
            drag.setHeldSkill(null);
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

        gfx.drawString(font, "HOTBAR", 5, 3, 0xFF4FC3F7, false);

        // CLEAR button
        gfx.fill(BTN_X, BTN_Y, BTN_X + BTN_W, BTN_Y + BTN_H, 0xCC0A0C10);
        gfx.fill(BTN_X, BTN_Y,              BTN_X + BTN_W, BTN_Y + 1,         0xFF2A3A4A);
        gfx.fill(BTN_X, BTN_Y + BTN_H - 1, BTN_X + BTN_W, BTN_Y + BTN_H,     0xFF2A3A4A);
        gfx.fill(BTN_X, BTN_Y,              BTN_X + 1,     BTN_Y + BTN_H,     0xFF2A3A4A);
        gfx.fill(BTN_X + BTN_W - 1, BTN_Y, BTN_X + BTN_W, BTN_Y + BTN_H,     0xFF2A3A4A);
        String clearText = "CLEAR";
        gfx.drawString(font, clearText,
                BTN_X + (BTN_W - font.width(clearText)) / 2,
                BTN_Y + (BTN_H - 8) / 2 + 1,
                0xFFAAAAAA, false);

        // slot backgrounds
        for (int i = 0; i < 18; i++) {
            int sx = 15 + (i % 9) * 18;
            int sy = 32 + (i / 9) * 18;
            gfx.fill(sx,      sy,      sx + 18, sy + 18, 0xFF0A0C10);
            gfx.fill(sx,      sy,      sx + 18, sy + 1,  0xFF2A3A4A);
            gfx.fill(sx,      sy + 17, sx + 18, sy + 18, 0xFF2A3A4A);
            gfx.fill(sx,      sy,      sx + 1,  sy + 18, 0xFF2A3A4A);
            gfx.fill(sx + 17, sy,      sx + 18, sy + 18, 0xFF2A3A4A);
        }
        super.render(gfx, mouseX, mouseY, partialTick);
    }
}
