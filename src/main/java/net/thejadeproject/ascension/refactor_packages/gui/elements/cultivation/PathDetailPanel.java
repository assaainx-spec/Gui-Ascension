package net.thejadeproject.ascension.refactor_packages.gui.elements.cultivation;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.PacketDistributor;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.network.server_bound.cultivation.TriggerBreakthrough;
import net.thejadeproject.ascension.refactor_packages.paths.PathData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;

public class PathDetailPanel extends RenderableElement {

    private ResourceLocation pathId;
    private final TechniquePopup popup;
    private boolean popupVisible = false;

    private int techniqueTextY = -1;
    private int techniqueTextX = -1;
    private int techniqueTextWidth = 0;
    private int breakthroughBtnY = -1;

    public PathDetailPanel(UIFrame frame, TechniquePopup popup) {
        super(frame);
        this.popup = popup;
        setWidth(160);
        setHeight(180);
    }

    public void setPath(ResourceLocation pathId) {
        this.pathId = pathId;
    }

    public boolean isPopupVisible() {
        return popupVisible;
    }

    public void tryClick(double px, double py) {
        if (techniqueTextY >= 0 && py >= techniqueTextY && py <= techniqueTextY + 10
                && px >= techniqueTextX && px <= techniqueTextX + techniqueTextWidth) {
            IEntityData entityData = Minecraft.getInstance().player.getData(ModAttachments.ENTITY_DATA);
            PathData pathData = entityData.getPathData(pathId);
            if (pathData != null && pathData.getLastUsedTechnique() != null) {
                popupVisible = !popupVisible;
                popup.setTechnique(pathData.getLastUsedTechnique());
                popup.setOnClose(() -> {
                    popupVisible = false;
                    popup.setActive(false);
                });
                popup.setActive(popupVisible);
            }
            return;
        }

        if (breakthroughBtnY >= 0) {
            int btnX = 10;
            int btnW = getWidth() - 20;
            int btnH = 14;
            if (px >= btnX && px <= btnX + btnW && py >= breakthroughBtnY && py <= breakthroughBtnY + btnH) {
                PacketDistributor.sendToServer(new TriggerBreakthrough(pathId));
            }
        }
    }

    private int progressBarColor() {
        String path = pathId.getPath();
        if (path.contains("body")) return 0xFF44DD44;
        if (path.contains("essence")) return 0xFF3399FF;
        if (path.contains("intent")) return 0xFFAA44FF;
        return 0xFF4FC3F7;
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        int w = getWidth();
        int h = getHeight();
        Font font = Minecraft.getInstance().font;

        String pathName = pathId.getPath();
        String headerText = pathName.isEmpty() ? pathName : Character.toUpperCase(pathName.charAt(0)) + pathName.substring(1);
        drawChrome(gfx, font, w, h, headerText);

        IEntityData entityData = Minecraft.getInstance().player.getData(ModAttachments.ENTITY_DATA);
        boolean hasPath = entityData != null && entityData.hasPath(pathId);
        PathData pathData = hasPath ? entityData.getPathData(pathId) : null;
        ITechnique technique = null;
        if (pathData != null && pathData.getLastUsedTechnique() != null) {
            technique = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(pathData.getLastUsedTechnique());
        }

        int y = 22;

        if (!hasPath) {
            gfx.drawString(font, "You haven't started this path yet.", 6, y, 0xFF666666, false);
            super.render(gfx, mouseX, mouseY, partialTick);
            return;
        }

        int majorRealm = pathData.getMajorRealm();
        int minorRealm = pathData.getMinorRealm();
        double progress = pathData.getCurrentRealmProgress();

        String majorLabel = "Major Realm";
        String majorValue = technique != null ? technique.getMajorRealmName(majorRealm).getString() : String.valueOf(majorRealm);
        gfx.drawString(font, majorLabel, 6, y, 0xFFAAAAAA, false);
        gfx.drawString(font, majorValue, 6 + font.width(majorLabel) + 4, y, 0xFFFFFFFF, false);
        y += 12;

        String minorLabel = "Minor Realm";
        String minorValue = technique != null ? technique.getMinorRealmName(majorRealm, minorRealm).getString() : String.valueOf(minorRealm);
        gfx.drawString(font, minorLabel, 6, y, 0xFFAAAAAA, false);
        gfx.drawString(font, minorValue, 6 + font.width(minorLabel) + 4, y, 0xFFFFFFFF, false);
        y += 14;

        double maxProgress = technique != null ? technique.getMaxQiForRealm(majorRealm, minorRealm) : 1.0;
        double fillFraction = maxProgress > 0 ? Math.min(progress / maxProgress, 1.0) : 0.0;
        int barX = 6, barW = w - 12, barH = 8;
        gfx.fill(barX, y, barX + barW, y + barH, 0xFF111111);
        int fillW = (int) (barW * fillFraction);
        if (fillW > 0) gfx.fill(barX, y, barX + fillW, y + barH, progressBarColor());
        String progressText = String.format("%.0f / %.0f", progress, maxProgress);
        gfx.drawString(font, progressText, barX + barW / 2 - font.width(progressText) / 2, y + 1, 0xFFFFFFFF, false);
        y += barH + 4;

        int divY = y + 2;
        gfx.fill(4, divY, w - 4, divY + 1, 0x55006396);

        int sectionTop = divY + 1;
        int sectionH = h - sectionTop;

        techniqueTextY = -1;
        breakthroughBtnY = -1;

        boolean canBreakthrough = technique != null && entityData != null
                && technique.canBreakthrough(entityData, majorRealm, minorRealm, progress);

        int itemCount = canBreakthrough ? 2 : 1;
        int spacing = 14;
        int iy = sectionTop + sectionH / 2 - (itemCount * spacing - (spacing - 10)) / 2;

        if (technique != null) {
            String techName = technique.getDisplayTitle().getString();
            techniqueTextX = 6;
            techniqueTextY = iy;
            techniqueTextWidth = font.width(techName);
            gfx.drawString(font, techName, 6, iy, 0xFF4FC3F7, false);
            gfx.fill(6, iy + 10, 6 + techniqueTextWidth, iy + 11, 0xFF4FC3F7);
        } else {
            gfx.drawString(font, "No Technique", 6, iy, 0xFFAAAAAA, false);
        }
        iy += spacing;

        if (canBreakthrough) {
            int btnX = 10, btnW = w - 20, btnH = 14;
            breakthroughBtnY = iy;
            gfx.fill(btnX, iy, btnX + btnW, iy + btnH, 0xE5051E0F);
            gfx.fill(btnX, iy, btnX + btnW, iy + 1, 0xFF44CC44);
            gfx.fill(btnX, iy + btnH - 1, btnX + btnW, iy + btnH, 0xFF44CC44);
            gfx.fill(btnX, iy, btnX + 1, iy + btnH, 0xFF44CC44);
            gfx.fill(btnX + btnW - 1, iy, btnX + btnW, iy + btnH, 0xFF44CC44);
            String btnText = "Breakthrough";
            gfx.drawString(font, btnText, btnX + btnW / 2 - font.width(btnText) / 2, iy + 3, 0xFF88FFAA, false);
        }

        super.render(gfx, mouseX, mouseY, partialTick);
    }

    static void drawChrome(GuiGraphics gfx, Font font, int w, int h, String title) {
        // Background
        gfx.fill(0, 0, w, h, 0xE8050810);
        // Outer border
        gfx.fill(0, 0, w, 1, 0xFF4FC3F7);
        gfx.fill(0, h - 1, w, h, 0xFF4FC3F7);
        gfx.fill(0, 0, 1, h, 0xFF4FC3F7);
        gfx.fill(w - 1, 0, w, h, 0xFF4FC3F7);
        // Corner accent marks (2px thick)
        gfx.fill(0, 0, 6, 2, 0xFF4FC3F7);
        gfx.fill(0, 0, 2, 6, 0xFF4FC3F7);
        gfx.fill(w - 6, 0, w, 2, 0xFF4FC3F7);
        gfx.fill(w - 2, 0, w, 6, 0xFF4FC3F7);
        gfx.fill(0, h - 2, 6, h, 0xFF4FC3F7);
        gfx.fill(0, h - 6, 2, h, 0xFF4FC3F7);
        gfx.fill(w - 6, h - 2, w, h, 0xFF4FC3F7);
        gfx.fill(w - 2, h - 6, w, h, 0xFF4FC3F7);
        // Inner border
        gfx.fill(2, 2, w - 2, 3, 0xFF1a4a6a);
        gfx.fill(2, h - 3, w - 2, h - 2, 0xFF1a4a6a);
        gfx.fill(2, 2, 3, h - 2, 0xFF1a4a6a);
        gfx.fill(w - 3, 2, w - 2, h - 2, 0xFF1a4a6a);
        // Title bar fill + bottom divider
        gfx.fill(1, 1, w - 1, 14, 0xDD001E30);
        gfx.fill(1, 13, w - 1, 14, 0xFF4FC3F7);
        // Title text
        gfx.drawString(font, title, 5, 3, 0xFF4FC3F7, false);
        // × close button
        gfx.fill(w - 12, 2, w - 2, 12, 0xFF3a0808);
        gfx.fill(w - 12, 2, w - 2, 3, 0xFFaa2222);
        gfx.fill(w - 12, 11, w - 2, 12, 0xFFaa2222);
        gfx.fill(w - 12, 2, w - 11, 12, 0xFFaa2222);
        gfx.fill(w - 3, 2, w - 2, 12, 0xFFaa2222);
        gfx.drawString(font, "\u00d7", w - 11, 3, 0xFFff5555, false);
        // Inner content frame: corner brackets + thin connectors
        int f = 3, ft = 16, fb = h - 3, bl = 12;
        gfx.fill(f + bl, ft,     w - f - bl, ft + 1, 0x664FC3F7);
        gfx.fill(f + bl, fb - 1, w - f - bl, fb,     0x664FC3F7);
        gfx.fill(f,      ft + bl, f + 1, fb - bl,    0x664FC3F7);
        gfx.fill(w-f-1,  ft + bl, w - f, fb - bl,    0x664FC3F7);
        gfx.fill(f,      ft,      f + bl, ft + 1,    0xFF4FC3F7);
        gfx.fill(f,      ft,      f + 1,  ft + bl,   0xFF4FC3F7);
        gfx.fill(w-f-bl, ft,      w - f,  ft + 1,    0xFF4FC3F7);
        gfx.fill(w-f-1,  ft,      w - f,  ft + bl,   0xFF4FC3F7);
        gfx.fill(f,      fb - 1,  f + bl, fb,         0xFF4FC3F7);
        gfx.fill(f,      fb - bl, f + 1,  fb,         0xFF4FC3F7);
        gfx.fill(w-f-bl, fb - 1,  w - f,  fb,         0xFF4FC3F7);
        gfx.fill(w-f-1,  fb - bl, w - f,  fb,         0xFF4FC3F7);
    }
}
