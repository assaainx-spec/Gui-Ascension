package net.thejadeproject.ascension.refactor_packages.gui.elements.cultivation;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.events.EasyEvents;
import net.lucent.easygui.gui.events.type.EasyEvent;
import net.lucent.easygui.gui.events.type.EasyMouseEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.thejadeproject.ascension.AscensionCraft;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;

import java.util.ArrayList;
import java.util.List;

public class TechniquePopup extends RenderableElement {

    private ITechnique technique = null;
    private Runnable onClose = null;

    public TechniquePopup(UIFrame frame) {
        super(frame);
        setWidth(100);
        setHeight(180);
        addEventListener(EasyEvents.MOUSE_DOWN_EVENT, this::onMouseDown);
    }

    public void setTechnique(ResourceLocation techniqueId) {
        this.technique = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(techniqueId);
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

    private List<String> wrapText(String text, Font font, int maxWidth) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder current = new StringBuilder();
        for (String word : words) {
            String candidate = current.isEmpty() ? word : current + " " + word;
            if (font.width(candidate) > maxWidth) {
                if (!current.isEmpty()) lines.add(current.toString());
                current = new StringBuilder(word);
            } else {
                current = new StringBuilder(candidate);
            }
        }
        if (!current.isEmpty()) lines.add(current.toString());
        return lines;
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        int w = getWidth();
        int h = getHeight();

        Font font = Minecraft.getInstance().font;
        String title = technique == null ? "Technique" : technique.getDisplayTitle().getString();
        PathDetailPanel.drawChrome(gfx, font, w, h, title);

        if (technique == null) {
            super.render(gfx, mouseX, mouseY, partialTick);
            return;
        }

        int y = 23;

        String rawPath = technique.getPath().getPath();
        String pathValue = rawPath.isEmpty() ? rawPath : Character.toUpperCase(rawPath.charAt(0)) + rawPath.substring(1);
        gfx.drawString(font, "Path", 6, y, 0xFFAAAAAA, false);
        gfx.drawString(font, pathValue, 6 + font.width("Path") + 4, y, 0xFFFFFFFF, false);
        y += 12;

        gfx.drawString(font, "Description", 6, y, 0xFFAAAAAA, false);
        y += 11;
        for (String line : wrapText(technique.getShortDescription().getString(), font, w - 12)) {
            gfx.drawString(font, line, 6, y, 0xFFCCCCCC, false);
            y += 10;
        }

        y += 2;
        gfx.fill(4, y, w - 4, y + 1, 0x55006396);
        y += 5;

        int maxMajor = technique.getMaxMajorRealm();
        int maxMinor = technique.getMaxMinorRealm(maxMajor);
        String realmValue = technique.getMajorRealmName(maxMajor).getString() + " (" + maxMajor + " \u00b7 " + maxMinor + ")";
        gfx.drawString(font, "Max Realm", 6, y, 0xFFAAAAAA, false);
        y += 11;
        gfx.drawString(font, realmValue, 6, y, 0xFFFFFFFF, false);

        super.render(gfx, mouseX, mouseY, partialTick);
    }
}
