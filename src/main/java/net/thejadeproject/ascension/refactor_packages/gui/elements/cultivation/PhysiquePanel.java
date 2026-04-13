package net.thejadeproject.ascension.refactor_packages.gui.elements.cultivation;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.events.EasyEvents;
import net.lucent.easygui.gui.events.type.EasyEvent;
import net.lucent.easygui.gui.events.type.EasyMouseEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec2;

public class PhysiquePanel extends RenderableElement {

    private Runnable onClose;

    public PhysiquePanel(UIFrame frame) {
        super(frame);
        setWidth(160);
        setHeight(180);
        addEventListener(EasyEvents.MOUSE_DOWN_EVENT, this::onMouseDown);
    }

    public void setOnClose(Runnable onClose) {
        this.onClose = onClose;
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
        var font = Minecraft.getInstance().font;
        int w = getWidth(), h = getHeight();
        gfx.fill(0, 0, w, h, 0xFF050810);
        gfx.fill(0,   0,   w,   1,   0xFF4FC3F7);
        gfx.fill(0,   h-1, w,   h,   0xFF4FC3F7);
        gfx.fill(0,   0,   1,   h,   0xFF4FC3F7);
        gfx.fill(w-1, 0,   w,   h,   0xFF4FC3F7);
        gfx.fill(0, 13, w, 14, 0x55006396);
        gfx.fill(1, 1, 4, 2, 0x88006396);  gfx.fill(1, 1, 2, 4, 0x88006396);
        gfx.fill(w-4, 1, w-1, 2, 0x88006396);  gfx.fill(w-2, 1, w-1, 4, 0x88006396);
        gfx.fill(1, h-2, 4, h-1, 0x88006396);  gfx.fill(1, h-4, 2, h-1, 0x88006396);
        gfx.fill(w-4, h-2, w-1, h-1, 0x88006396);  gfx.fill(w-2, h-4, w-1, h-1, 0x88006396);
        gfx.drawString(font, "PHYSIQUE", 5, 3, 0xFF4FC3F7, false);
        gfx.drawString(font, "\u00d7", w - 12 + (10 - font.width("\u00d7") + 1) / 2, 3, 0xFFFF5555, false);
        String comingSoon = "Coming Soon";
        gfx.drawString(font, comingSoon, (w - font.width(comingSoon)) / 2, h / 2 - 4, 0xFF555566, false);
        super.render(gfx, mouseX, mouseY, partialTick);
    }
}
