package net.thejadeproject.ascension.refactor_packages.gui.elements.cultivation;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.events.EasyEvents;
import net.lucent.easygui.gui.events.type.EasyEvent;
import net.lucent.easygui.gui.events.type.EasyMouseEvent;
import net.lucent.easygui.gui.layout.positioning.rules.PositioningRules;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;

import java.util.ArrayList;
import java.util.List;

public class CultivationMenuContainer extends RenderableElement {

    private static final int WIDTH = 228;
    private static final int HEIGHT = 180;
    private static final int SIDEBAR_W = 68;
    private static final int PANEL_W = 160;
    private static final int TAB_H = 14;
    private static final int BOTTOM_TAB_H = 11;
    private static final int TAB_GAP = 2;
    private static final int TAB_X = 3;
    private static final int TAB_W = SIDEBAR_W - 6;

    private final List<ResourceLocation> pathTabs = new ArrayList<>();
    private final PathDetailPanel pathPanel;
    private final StatsPanel statsPanel;
    private final TechniquesPanel techniquesPanel;
    private final PhysiquePanel physiquePanel;
    private final TechniquePopup techniquePopup;

    private ResourceLocation selectedPath = null;
    private boolean statsSelected = false;
    private boolean techniquesSelected = false;
    private boolean physiqueSelected = false;
    private RenderableElement rightSlotPanel = null;
    private RenderableElement leftSlotPanel = null;


    public CultivationMenuContainer(UIFrame frame) {
        super(frame);
        getPositioning().setPositioningRule(PositioningRules.CENTER);
        getPositioning().setX(-WIDTH / 2);
        getPositioning().setY(-HEIGHT / 2);
        setWidth(WIDTH);
        setHeight(HEIGHT);

        IEntityData entityData = Minecraft.getInstance().player.getData(ModAttachments.ENTITY_DATA);
        for (ResourceLocation pathId : AscensionRegistries.Paths.PATHS_REGISTRY.keySet()) {
            if (entityData != null && entityData.hasPath(pathId)) {
                pathTabs.add(pathId);
            }
        }

        techniquePopup = new TechniquePopup(frame);
        techniquePopup.getPositioning().setX(SIDEBAR_W - 1);
        techniquePopup.getPositioning().setY(0);
        techniquePopup.setActive(false);

        pathPanel = new PathDetailPanel(frame, techniquePopup);
        pathPanel.getPositioning().setX(SIDEBAR_W - 1);
        pathPanel.getPositioning().setY(0);
        pathPanel.setActive(false);

        statsPanel = new StatsPanel(frame);
        statsPanel.getPositioning().setX(SIDEBAR_W - 1);
        statsPanel.getPositioning().setY(0);
        statsPanel.setActive(false);

        techniquesPanel = new TechniquesPanel(frame);
        techniquesPanel.setActive(false);

        physiquePanel = new PhysiquePanel(frame);
        physiquePanel.setActive(false);

        addChild(pathPanel);
        addChild(statsPanel);
        addChild(techniquesPanel);
        addChild(physiquePanel);
        addChild(techniquePopup);

        if (!pathTabs.isEmpty()) {
            selectPath(pathTabs.get(0));
        } else {
            selectStats();
        }

        addEventListener(EasyEvents.MOUSE_DOWN_EVENT, this::onMouseDown);
    }

    private void selectPath(ResourceLocation pathId) {
        selectedPath = pathId;
        statsSelected = false;
        pathPanel.setPath(pathId);
        pathPanel.setActive(true);
        statsPanel.setActive(false);
    }

    private void selectStats() {
        selectedPath = null;
        statsSelected = true;
        statsPanel.setActive(true);
        pathPanel.setActive(false);
    }

    private void selectTechniques() {
        toggleSidePanel(techniquesPanel);
        techniquesSelected = (rightSlotPanel == techniquesPanel) || (leftSlotPanel == techniquesPanel);
    }

    private void selectPhysique() {
        toggleSidePanel(physiquePanel);
        physiqueSelected = (rightSlotPanel == physiquePanel) || (leftSlotPanel == physiquePanel);
    }

    private void selectNone() {
        selectedPath = null;
        statsSelected = false;
        pathPanel.setActive(false);
        statsPanel.setActive(false);
        techniquePopup.setActive(false);
    }

    private void toggleSidePanel(RenderableElement panel) {
        if (panel == rightSlotPanel) {
            rightSlotPanel = null;
            panel.setActive(false);
        } else if (panel == leftSlotPanel) {
            leftSlotPanel = null;
            panel.setActive(false);
        } else if (rightSlotPanel == null) {
            rightSlotPanel = panel;
            panel.getPositioning().setX(WIDTH);
            panel.getPositioning().setY(0);
            panel.setActive(true);
            registerCloseCallback(panel);
        } else if (leftSlotPanel == null) {
            leftSlotPanel = panel;
            panel.getPositioning().setX(-panel.getWidth());
            panel.getPositioning().setY(0);
            panel.setActive(true);
            registerCloseCallback(panel);
        }
    }

    private void registerCloseCallback(RenderableElement panel) {
        if (panel instanceof TechniquesPanel tp) tp.setOnClose(() -> {
            toggleSidePanel(panel);
            techniquesSelected = false;
        });
        else if (panel instanceof PhysiquePanel pp) pp.setOnClose(() -> {
            toggleSidePanel(panel);
            physiqueSelected = false;
        });
    }

    private RenderableElement activeContentPanel() {
        if (selectedPath != null) return pathPanel;
        if (statsSelected) return statsPanel;
        return null;
    }

    private void onMouseDown(EasyEvent event) {
        if (!(event instanceof EasyMouseEvent mouseEvent)) return;
        Vec2 local = globalToLocalPositionPoint((float) mouseEvent.getMouseX(), (float) mouseEvent.getMouseY());
        double mx = local.x;
        double my = local.y;

        // Title-bar interaction on the active content panel
        RenderableElement activePanel = activeContentPanel();
        if (activePanel != null) {
            int panelX = activePanel.getPositioning().getX();
            int panelY = activePanel.getPositioning().getY();

            // × close button (rightmost 10px, y=2..12 in panel)
            int closeBx = panelX + activePanel.getWidth() - 12;
            if (mx >= closeBx && mx < closeBx + 10 && my >= panelY + 2 && my < panelY + 12) {
                selectNone();
                event.setCanceled(true);
                return;
            }

            boolean inTitleBar = mx >= panelX && mx < panelX + activePanel.getWidth()
                    && my >= panelY && my < panelY + TAB_H;
        }

        // Sidebar tab clicks
        int tabsStartY = 19;
        for (int i = 0; i < pathTabs.size(); i++) {
            int tabY = tabsStartY + i * (TAB_H + TAB_GAP);
            if (mx >= TAB_X && mx <= TAB_X + TAB_W && my >= tabY && my <= tabY + TAB_H) {
                selectPath(pathTabs.get(i));
                event.setCanceled(true);
                return;
            }
        }

        int statsTabY = HEIGHT - BOTTOM_TAB_H - 4;
        int physiqueTabY = statsTabY - BOTTOM_TAB_H - TAB_GAP;
        int techniquesTabY = physiqueTabY - BOTTOM_TAB_H - TAB_GAP;

        if (mx >= TAB_X && mx <= TAB_X + TAB_W && my >= techniquesTabY && my <= techniquesTabY + BOTTOM_TAB_H) {
            selectTechniques();
            event.setCanceled(true);
            return;
        }
        if (mx >= TAB_X && mx <= TAB_X + TAB_W && my >= physiqueTabY && my <= physiqueTabY + BOTTOM_TAB_H) {
            selectPhysique();
            event.setCanceled(true);
            return;
        }
        if (mx >= TAB_X && mx <= TAB_X + TAB_W && my >= statsTabY && my <= statsTabY + BOTTOM_TAB_H) {
            selectStats();
            event.setCanceled(true);
            return;
        }

        // Delegate content clicks to the active panel using its actual position
        if (activePanel != null) {
            int panelX = activePanel.getPositioning().getX();
            int panelY = activePanel.getPositioning().getY();
            double px = mx - panelX;
            double py = my - panelY;
            if (px >= 0 && px < activePanel.getWidth() && py >= 0 && py < activePanel.getHeight()) {
                if (statsSelected) statsPanel.tryClick(px, py);
                else if (selectedPath != null) pathPanel.tryClick(px, py);
            }
        }
        event.setCanceled(true);
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;

        gfx.fill(0, 0, WIDTH, HEIGHT, 0xD1050810);
        gfx.fill(2, 2, SIDEBAR_W, HEIGHT - 2, 0x88050810);
        gfx.fill(0, 0, WIDTH, 1, 0xFF4FC3F7);
        gfx.fill(0, HEIGHT - 1, WIDTH, HEIGHT, 0xFF4FC3F7);
        gfx.fill(0, 0, 1, HEIGHT, 0xFF4FC3F7);
        gfx.fill(WIDTH - 1, 0, WIDTH, HEIGHT, 0xFF4FC3F7);
        // Corner accents
        gfx.fill(0, 0, 6, 2, 0xFF4FC3F7);
        gfx.fill(0, 0, 2, 6, 0xFF4FC3F7);
        gfx.fill(WIDTH - 6, 0, WIDTH, 2, 0xFF4FC3F7);
        gfx.fill(WIDTH - 2, 0, WIDTH, 6, 0xFF4FC3F7);
        gfx.fill(0, HEIGHT - 2, 6, HEIGHT, 0xFF4FC3F7);
        gfx.fill(0, HEIGHT - 6, 2, HEIGHT, 0xFF4FC3F7);
        gfx.fill(WIDTH - 6, HEIGHT - 2, WIDTH, HEIGHT, 0xFF4FC3F7);
        gfx.fill(WIDTH - 2, HEIGHT - 6, WIDTH, HEIGHT, 0xFF4FC3F7);
        // Inner border
        gfx.fill(2, 2, WIDTH - 2, 3, 0xFF1A4A6A);
        gfx.fill(2, HEIGHT - 3, WIDTH - 2, HEIGHT - 2, 0xFF1A4A6A);
        gfx.fill(2, 2, 3, HEIGHT - 2, 0xFF1A4A6A);
        gfx.fill(WIDTH - 3, 2, WIDTH - 2, HEIGHT - 2, 0xFF1A4A6A);
        // PATHS header underline
        gfx.fill(1, 17, SIDEBAR_W, 18, 0xFF4FC3F7);

        gfx.drawString(font, "PATHS", 6, 6, 0xFF4FC3F7, false);

        int tabsStartY = 19;
        for (int i = 0; i < pathTabs.size(); i++) {
            ResourceLocation pathId = pathTabs.get(i);
            int tabY = tabsStartY + i * (TAB_H + TAB_GAP);
            boolean active = pathId.equals(selectedPath);

            if (active) {
                gfx.fill(TAB_X, tabY, TAB_X + TAB_W, tabY + TAB_H, 0x2E006396);
                gfx.fill(TAB_X, tabY, TAB_X + TAB_W, tabY + 1, 0xFF006396);
                gfx.fill(TAB_X, tabY + TAB_H - 1, TAB_X + TAB_W, tabY + TAB_H, 0xFF006396);
                gfx.fill(TAB_X, tabY, TAB_X + 1, tabY + TAB_H, 0xFF006396);
                gfx.fill(TAB_X + TAB_W - 1, tabY, TAB_X + TAB_W, tabY + TAB_H, 0xFF006396);
            } else {
                gfx.fill(TAB_X, tabY, TAB_X + TAB_W, tabY + TAB_H, 0x18050810);
                gfx.fill(TAB_X, tabY, TAB_X + TAB_W, tabY + 1, 0x44006396);
                gfx.fill(TAB_X, tabY + TAB_H - 1, TAB_X + TAB_W, tabY + TAB_H, 0x44006396);
                gfx.fill(TAB_X, tabY, TAB_X + 1, tabY + TAB_H, 0x44006396);
                gfx.fill(TAB_X + TAB_W - 1, tabY, TAB_X + TAB_W, tabY + TAB_H, 0x44006396);
            }

            String rawName = pathId.getPath();
            String name = rawName.isEmpty() ? rawName : Character.toUpperCase(rawName.charAt(0)) + rawName.substring(1);
            gfx.drawString(font, name, TAB_X + 3, tabY + (TAB_H - 8) / 2, active ? 0xFF4FC3F7 : 0xFF888888, false);
        }

        // bottom tabs: Techniques, Physique, Stats
        int statsTabY = HEIGHT - BOTTOM_TAB_H - 4;
        int physiqueTabY = statsTabY - BOTTOM_TAB_H - TAB_GAP;
        int techniquesTabY = physiqueTabY - BOTTOM_TAB_H - TAB_GAP;

        renderBottomTab(gfx, font, techniquesTabY, "Techniques", techniquesSelected);
        renderBottomTab(gfx, font, physiqueTabY, "Physique", physiqueSelected);
        renderBottomTab(gfx, font, statsTabY, "Stats", statsSelected);

        super.render(gfx, mouseX, mouseY, partialTick);
    }

    private void renderBottomTab(GuiGraphics gfx, Font font, int tabY, String label, boolean active) {
        int h = BOTTOM_TAB_H;
        int textY = tabY + (h - 8) / 2;
        if (active) {
            gfx.fill(TAB_X, tabY, TAB_X + TAB_W, tabY + h, 0x2E006396);
            gfx.fill(TAB_X, tabY, TAB_X + TAB_W, tabY + 1, 0xFF006396);
            gfx.fill(TAB_X, tabY + h - 1, TAB_X + TAB_W, tabY + h, 0xFF006396);
            gfx.fill(TAB_X, tabY, TAB_X + 1, tabY + h, 0xFF006396);
            gfx.fill(TAB_X + TAB_W - 1, tabY, TAB_X + TAB_W, tabY + h, 0xFF006396);
            gfx.drawString(font, label, TAB_X + 3, textY, 0xFF4FC3F7, false);
        } else {
            gfx.fill(TAB_X, tabY, TAB_X + TAB_W, tabY + h, 0x18050810);
            gfx.fill(TAB_X, tabY, TAB_X + TAB_W, tabY + 1, 0x44006396);
            gfx.fill(TAB_X, tabY + h - 1, TAB_X + TAB_W, tabY + h, 0x44006396);
            gfx.fill(TAB_X, tabY, TAB_X + 1, tabY + h, 0x44006396);
            gfx.fill(TAB_X + TAB_W - 1, tabY, TAB_X + TAB_W, tabY + h, 0x44006396);
            gfx.drawString(font, label, TAB_X + 3, textY, 0xFF888888, false);
        }
    }
}
