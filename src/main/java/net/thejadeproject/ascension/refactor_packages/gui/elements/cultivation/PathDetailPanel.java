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
import net.neoforged.neoforge.network.PacketDistributor;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.network.server_bound.cultivation.TriggerBreakthrough;
import net.thejadeproject.ascension.refactor_packages.paths.PathData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.techniques.ITechnique;

public class PathDetailPanel extends RenderableElement {

    private final ResourceLocation pathId;
    private final TechniquePopup popup;
    private boolean popupVisible = false;

    // tracked during render for click detection
    private int techniqueTextY = -1;
    private int techniqueTextX = -1;
    private int techniqueTextWidth = 0;
    private int breakthroughBtnY = -1;

    public PathDetailPanel(UIFrame frame, ResourceLocation pathId, TechniquePopup popup) {
        super(frame);
        this.pathId = pathId;
        this.popup = popup;
        setWidth(260);
        setHeight(220);
        addEventListener(EasyEvents.MOUSE_DOWN_EVENT, this::onMouseDown);
    }

    public boolean isPopupVisible() {
        return popupVisible;
    }

    private void onMouseDown(EasyEvent event) {
        if (!(event instanceof EasyMouseEvent mouseEvent)) return;
        double mx = mouseEvent.getMouseX();
        double my = mouseEvent.getMouseY();

        // technique name click
        if (techniqueTextY >= 0 && my >= techniqueTextY && my <= techniqueTextY + 10
                && mx >= techniqueTextX && mx <= techniqueTextX + techniqueTextWidth) {
            IEntityData entityData = Minecraft.getInstance().player.getData(ModAttachments.ENTITY_DATA);
            PathData pathData = entityData.getPathData(pathId);
            if (pathData != null && pathData.getLastUsedTechnique() != null) {
                popupVisible = !popupVisible;
                popup.setTechnique(pathData.getLastUsedTechnique());
                popup.setOnClose(() -> popupVisible = false);
            }
            event.setCanceled(true);
            return;
        }

        // breakthrough button click
        if (breakthroughBtnY >= 0) {
            int btnX = 10;
            int btnW = getWidth() - 20;
            int btnH = 14;
            if (mx >= btnX && mx <= btnX + btnW && my >= breakthroughBtnY && my <= breakthroughBtnY + btnH) {
                PacketDistributor.sendToServer(new TriggerBreakthrough(pathId));
                event.setCanceled(true);
            }
        }
    }

    private int progressBarColor() {
        String path = pathId.getPath();
        if (path.contains("body")) return 0xFF44DD44;
        if (path.contains("essence")) return 0xFF3399FF;
        if (path.contains("intent")) return 0xFFAA44FF;
        return 0xFFF0B800;
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        int w = getWidth();
        int h = getHeight();
        Font font = Minecraft.getInstance().font;

        // background
        gfx.fill(0, 0, w, h, 0xA5050810);

        // border
        gfx.fill(0, 0, w, 1, 0x66C8960A);
        gfx.fill(0, h - 1, w, h, 0x66C8960A);
        gfx.fill(0, 0, 1, h, 0x66C8960A);
        gfx.fill(w - 1, 0, w, h, 0x66C8960A);

        // path name header
        String pathName = pathId.getPath();
        String headerText = pathName.isEmpty() ? pathName
                : Character.toUpperCase(pathName.charAt(0)) + pathName.substring(1);
        gfx.drawString(font, headerText, 6, 4, 0xFFF0B800, false);

        // header divider
        gfx.fill(4, 16, w - 4, 17, 0x55C8960A);

        IEntityData entityData = Minecraft.getInstance().player.getData(ModAttachments.ENTITY_DATA);
        PathData pathData = entityData != null ? entityData.getPathData(pathId) : null;
        ITechnique technique = null;
        if (pathData != null && pathData.getLastUsedTechnique() != null) {
            technique = AscensionRegistries.Techniques.TECHNIQUES_REGISTRY.get(pathData.getLastUsedTechnique());
        }

        int y = 22;

        if (pathData == null) {
            gfx.drawString(font, "No path data", 6, y, 0xFFAAAAAA, false);
            super.render(gfx, mouseX, mouseY, partialTick);
            return;
        }

        int majorRealm = pathData.getMajorRealm();
        int minorRealm = pathData.getMinorRealm();
        double progress = pathData.getCurrentRealmProgress();

        // major realm row
        String majorLabel = "Major Realm";
        String majorValue = technique != null
                ? technique.getMajorRealmName(majorRealm).getString()
                : String.valueOf(majorRealm);
        gfx.drawString(font, majorLabel, 6, y, 0xFFAAAAAA, false);
        gfx.drawString(font, majorValue, 6 + font.width(majorLabel) + 4, y, 0xFFFFFFFF, false);
        y += 12;

        // minor realm row
        String minorLabel = "Minor Realm";
        String minorValue = technique != null
                ? technique.getMinorRealmName(majorRealm, minorRealm).getString()
                : String.valueOf(minorRealm);
        gfx.drawString(font, minorLabel, 6, y, 0xFFAAAAAA, false);
        gfx.drawString(font, minorValue, 6 + font.width(minorLabel) + 4, y, 0xFFFFFFFF, false);
        y += 14;

        // progress bar
        double maxProgress = technique != null ? technique.getMaxQiForRealm(majorRealm, minorRealm) : 1.0;
        double fillFraction = maxProgress > 0 ? Math.min(progress / maxProgress, 1.0) : 0.0;
        int barX = 6;
        int barW = w - 12;
        int barH = 8;
        gfx.fill(barX, y, barX + barW, y + barH, 0xFF111111);
        int fillW = (int) (barW * fillFraction);
        if (fillW > 0) {
            gfx.fill(barX, y, barX + fillW, y + barH, progressBarColor());
        }
        // progress label
        String progressText = String.format("%.0f / %.0f", progress, maxProgress);
        int progressTextX = barX + barW / 2 - font.width(progressText) / 2;
        gfx.drawString(font, progressText, progressTextX, y + 1, 0xFFFFFFFF, false);
        y += barH + 4;

        // horizontal divider separating realm/progress from bottom section
        int dividerY = y + 2;
        gfx.fill(4, dividerY, w - 4, dividerY + 1, 0x55C8960A);

        // bottom section: vertically centred between divider and panel bottom
        int bottomSectionTop = dividerY + 1;
        int bottomSectionHeight = h - bottomSectionTop;

        // reset tracked hit areas
        techniqueTextY = -1;
        breakthroughBtnY = -1;

        // determine if breakthrough is available
        boolean canBreakthrough = technique != null && entityData != null
                && technique.canBreakthrough(entityData, majorRealm, minorRealm, progress);

        // items to draw in bottom section
        int itemCount = 1; // technique name always shown (or placeholder)
        if (canBreakthrough) itemCount++;
        int itemSpacing = 14;
        int totalItemsHeight = itemCount * itemSpacing - (itemSpacing - 10);
        int bottomMidY = bottomSectionTop + bottomSectionHeight / 2;
        int itemsStartY = bottomMidY - totalItemsHeight / 2;

        int iy = itemsStartY;

        // technique name
        if (technique != null) {
            String techName = technique.getDisplayTitle().getString();
            int techTextX = 6;
            int techTextW = font.width(techName);
            techniqueTextX = techTextX;
            techniqueTextY = iy;
            techniqueTextWidth = techTextW;
            gfx.drawString(font, techName, techTextX, iy, 0xFFF0B800, false);
            // underline
            gfx.fill(techTextX, iy + 10, techTextX + techTextW, iy + 11, 0xFFF0B800);
        } else {
            gfx.drawString(font, "No Technique", 6, iy, 0xFFAAAAAA, false);
        }
        iy += itemSpacing;

        // breakthrough button
        if (canBreakthrough) {
            int btnX = 10;
            int btnW = w - 20;
            int btnH = 14;
            breakthroughBtnY = iy;
            // background + border
            gfx.fill(btnX, iy, btnX + btnW, iy + btnH, 0xE5051E0F);
            gfx.fill(btnX, iy, btnX + btnW, iy + 1, 0xFF44CC44);
            gfx.fill(btnX, iy + btnH - 1, btnX + btnW, iy + btnH, 0xFF44CC44);
            gfx.fill(btnX, iy, btnX + 1, iy + btnH, 0xFF44CC44);
            gfx.fill(btnX + btnW - 1, iy, btnX + btnW, iy + btnH, 0xFF44CC44);
            String btnText = "Breakthrough";
            int btnTextX = btnX + btnW / 2 - font.width(btnText) / 2;
            gfx.drawString(font, btnText, btnTextX, iy + 3, 0xFF88FFAA, false);
        }

        super.render(gfx, mouseX, mouseY, partialTick);
    }
}
