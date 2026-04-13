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

    private static void fillBorderedRect(GuiGraphics gfx, int x, int y, int w, int h, int bg, int border) {
        gfx.fill(x, y, x + w, y + h, bg);
        gfx.fill(x, y, x + w, y + 1, border);
        gfx.fill(x, y + h - 1, x + w, y + h, border);
        gfx.fill(x, y, x + 1, y + h, border);
        gfx.fill(x + w - 1, y, x + w, y + h, border);
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

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        int w = getWidth();
        int h = getHeight();
        Font font = Minecraft.getInstance().font;

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

        String pathName = pathId.getPath();
        String headerText = pathName.isEmpty() ? pathName : Character.toUpperCase(pathName.charAt(0)) + pathName.substring(1);
        gfx.drawString(font, headerText, 5, 3, 0xFF4FC3F7, false);
        gfx.drawString(font, "\u00d7", w - 12 + (10 - font.width("\u00d7") + 1) / 2, 3, 0xFFFF5555, false);

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
        String p = pathId.getPath();
        int barColor = p.contains("body") ? 0xFF44DD44 : p.contains("essence") ? 0xFF3399FF : p.contains("intent") ? 0xFFAA44FF : 0xFF4FC3F7;
        int barX = 6, barW = w - 12, barH = 8;
        gfx.fill(barX, y, barX + barW, y + barH, 0xFF111111);
        int fillW = (int) (barW * fillFraction);
        if (fillW > 0) gfx.fill(barX, y, barX + fillW, y + barH, barColor);
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
            fillBorderedRect(gfx, btnX, iy, btnW, btnH, 0xE5051E0F, 0xFF44CC44);
            String btnText = "Breakthrough";
            gfx.drawString(font, btnText, btnX + btnW / 2 - font.width(btnText) / 2, iy + 3, 0xFF88FFAA, false);
        }

        super.render(gfx, mouseX, mouseY, partialTick);
    }

}
