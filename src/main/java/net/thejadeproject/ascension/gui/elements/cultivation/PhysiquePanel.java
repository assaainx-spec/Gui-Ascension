package net.thejadeproject.ascension.gui.elements.cultivation;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.events.EasyEvents;
import net.lucent.easygui.gui.events.type.EasyEvent;
import net.lucent.easygui.gui.events.type.EasyMouseEvent;
import net.lucent.easygui.gui.textures.ITextureData;
import net.lucent.easygui.gui.textures.TextureDataSubsection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.thejadeproject.ascension.AscensionCraft;

public class PhysiquePanel extends RenderableElement {
    private static final ITextureData BG = new TextureDataSubsection(
            ResourceLocation.fromNamespaceAndPath(AscensionCraft.MOD_ID, "textures/gui/screen/cultivation_physique_panel.png"),
            160, 180, 0, 0, 160, 180);

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
        int w = getWidth();
        BG.render(gfx);
        gfx.drawString(font, "PHYSIQUE", 5, 3, 0xFF4FC3F7, false);
        gfx.drawString(font, "\u00d7", w - 12 + (10 - font.width("\u00d7") + 1) / 2, 3, 0xFFFF5555, false);
        gfx.drawString(font, "Coming soon", 6, 30, 0xFF666666, false);
        super.render(gfx, mouseX, mouseY, partialTick);
    }
}
