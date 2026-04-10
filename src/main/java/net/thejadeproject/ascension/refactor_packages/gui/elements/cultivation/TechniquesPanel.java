package net.thejadeproject.ascension.refactor_packages.gui.elements.cultivation;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.events.EasyEvents;
import net.lucent.easygui.gui.events.type.EasyEvent;
import net.lucent.easygui.gui.events.type.EasyMouseEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.phys.Vec2;

public class TechniquesPanel extends RenderableElement {

    private Runnable onClose;

    public TechniquesPanel(UIFrame frame) {
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
        int w = getWidth(), h = getHeight();
        PathDetailPanel.drawChrome(gfx, Minecraft.getInstance().font, w, h, "TECHNIQUES");
        gfx.drawString(Minecraft.getInstance().font, "Coming soon", 6, 30, 0xFF666666, false);
        super.render(gfx, mouseX, mouseY, partialTick);
    }
}
