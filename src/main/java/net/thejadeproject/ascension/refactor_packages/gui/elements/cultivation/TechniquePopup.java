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
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;

import java.util.ArrayList;
import java.util.List;

public class TechniquePopup extends RenderableElement {

    private ITechnique technique = null;
    private Runnable onClose = null;

    public TechniquePopup(UIFrame frame) {
        super(frame);
        setWidth(135);
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
        // close button: top-right corner, x=width-18, y=2, w=14, h=12
        int bx = getWidth() - 18;
        double mx = local.x;
        double my = local.y;
        if (mx >= bx && mx <= bx + 14 && my >= 2 && my <= 14) {
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
                if (!current.isEmpty()) {
                    lines.add(current.toString());
                }
                current = new StringBuilder(word);
            } else {
                current = new StringBuilder(candidate);
            }
        }
        if (!current.isEmpty()) {
            lines.add(current.toString());
        }
        return lines;
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        int w = getWidth();
        int h = getHeight();

        // background
        gfx.fill(0, 0, w, h, 0xE1050810);

        // border
        gfx.fill(0, 0, w, 1, 0xFFC8960A);
        gfx.fill(0, h - 1, w, h, 0xFFC8960A);
        gfx.fill(0, 0, 1, h, 0xFFC8960A);
        gfx.fill(w - 1, 0, w, h, 0xFFC8960A);

        if (technique == null) {
            super.render(gfx, mouseX, mouseY, partialTick);
            return;
        }

        Font font = Minecraft.getInstance().font;

        // --- header ---
        String title = technique.getDisplayTitle().getString();
        gfx.drawString(font, title, 6, 5, 0xFFF0B800, false);

        // close button
        int bx = w - 18;
        gfx.fill(bx, 2, bx + 14, 14, 0x4C961414);
        gfx.fill(bx, 2, bx + 14, 3, 0xFFCC4444);
        gfx.fill(bx, 13, bx + 14, 14, 0xFFCC4444);
        gfx.fill(bx, 2, bx + 1, 14, 0xFFCC4444);
        gfx.fill(bx + 13, 2, bx + 14, 14, 0xFFCC4444);
        gfx.drawString(font, "\u00d7", bx + 4, 4, 0xFFFF8888, false);

        // divider 1
        gfx.fill(4, 18, w - 4, 19, 0x55C8960A);

        int y = 23;

        // --- path ---
        String rawPath = technique.getPath().getPath();
        String pathValue = rawPath.isEmpty() ? rawPath
                : Character.toUpperCase(rawPath.charAt(0)) + rawPath.substring(1);
        gfx.drawString(font, "Path", 6, y, 0xFFAAAAAA, false);
        gfx.drawString(font, pathValue, 6 + font.width("Path") + 4, y, 0xFFFFFFFF, false);
        y += 12;

        // --- description ---
        gfx.drawString(font, "Description", 6, y, 0xFFAAAAAA, false);
        y += 11;
        int textMaxWidth = w - 12;
        String desc = technique.getShortDescription().getString();
        List<String> lines = wrapText(desc, font, textMaxWidth);
        for (String line : lines) {
            gfx.drawString(font, line, 6, y, 0xFFCCCCCC, false);
            y += 10;
        }

        // divider 2
        y += 2;
        gfx.fill(4, y, w - 4, y + 1, 0x55C8960A);
        y += 5;

        // --- max realm ---
        int maxMajor = technique.getMaxMajorRealm();
        int maxMinor = technique.getMaxMinorRealm(maxMajor);
        String realmName = technique.getMajorRealmName(maxMajor).getString();
        String realmValue = realmName + " (" + maxMajor + " \u00b7 " + maxMinor + ")";
        gfx.drawString(font, "Max Realm", 6, y, 0xFFAAAAAA, false);
        y += 11;
        gfx.drawString(font, realmValue, 6, y, 0xFFFFFFFF, false);

        super.render(gfx, mouseX, mouseY, partialTick);
    }
}
