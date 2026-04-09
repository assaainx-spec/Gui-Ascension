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
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;

import java.util.ArrayList;
import java.util.List;

public class CultivationMenuContainer extends RenderableElement {

    private static final int WIDTH = 350;
    private static final int HEIGHT = 240;
    private static final int SIDEBAR_W = 90;
    private static final int PANEL_W = 260;
    private static final int TAB_H = 18;
    private static final int TAB_GAP = 3;
    private static final int TAB_X = 4;
    private static final int TAB_W = SIDEBAR_W - 8; // 82

    private final List<ResourceLocation> pathTabs = new ArrayList<>();
    private final PathDetailPanel pathPanel;
    private final StatsPanel statsPanel;
    private final TechniquePopup techniquePopup;

    private ResourceLocation selectedPath = null;
    private boolean statsSelected = false;

    public CultivationMenuContainer(UIFrame frame) {
        super(frame);
        getPositioning().setPositioningRule(PositioningRules.CENTER);
        getPositioning().setX(-WIDTH / 2);
        getPositioning().setY(-HEIGHT / 2);
        setWidth(WIDTH);
        setHeight(HEIGHT);

        // show all registered paths
        pathTabs.addAll(AscensionRegistries.Paths.PATHS_REGISTRY.keySet());

        techniquePopup = new TechniquePopup(frame);
        techniquePopup.getPositioning().setX(SIDEBAR_W + PANEL_W + 4);
        techniquePopup.getPositioning().setY(0);
        techniquePopup.setActive(false);

        pathPanel = new PathDetailPanel(frame, techniquePopup);
        pathPanel.getPositioning().setX(SIDEBAR_W);
        pathPanel.getPositioning().setY(0);
        pathPanel.setActive(false);

        statsPanel = new StatsPanel(frame);
        statsPanel.getPositioning().setX(SIDEBAR_W);
        statsPanel.getPositioning().setY(0);
        statsPanel.setActive(false);

        addChild(pathPanel);
        addChild(statsPanel);
        addChild(techniquePopup);

        // select first path, or stats if none
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
        techniquePopup.setActive(false);
    }

    private void selectStats() {
        selectedPath = null;
        statsSelected = true;
        statsPanel.setActive(true);
        pathPanel.setActive(false);
        techniquePopup.setActive(false);
    }

    private void onMouseDown(EasyEvent event) {
        if (!(event instanceof EasyMouseEvent mouseEvent)) return;
        Vec2 local = globalToLocalPositionPoint((float) mouseEvent.getMouseX(), (float) mouseEvent.getMouseY());
        double mx = local.x;
        double my = local.y;

        // check path tabs
        int tabsStartY = 19;
        for (int i = 0; i < pathTabs.size(); i++) {
            int tabY = tabsStartY + i * (TAB_H + TAB_GAP);
            if (mx >= TAB_X && mx <= TAB_X + TAB_W && my >= tabY && my <= tabY + TAB_H) {
                selectPath(pathTabs.get(i));
                event.setCanceled(true);
                return;
            }
        }

        // check stats tab
        int statsTabY = HEIGHT - TAB_H - 6;
        if (mx >= TAB_X && mx <= TAB_X + TAB_W && my >= statsTabY && my <= statsTabY + TAB_H) {
            selectStats();
            event.setCanceled(true);
        }
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        Font font = Minecraft.getInstance().font;

        // outer frame background
        gfx.fill(0, 0, WIDTH, HEIGHT, 0xD1050810);

        // outer border (2px thick, 4 sides)
        gfx.fill(0, 0, WIDTH, 2, 0xFFC8960A);
        gfx.fill(0, HEIGHT - 2, WIDTH, HEIGHT, 0xFFC8960A);
        gfx.fill(0, 0, 2, HEIGHT, 0xFFC8960A);
        gfx.fill(WIDTH - 2, 0, WIDTH, HEIGHT, 0xFFC8960A);

        // sidebar background
        gfx.fill(2, 2, 88, HEIGHT - 2, 0x88050810);

        // sidebar/panel divider
        gfx.fill(89, 0, 90, HEIGHT, 0x66C8960A);

        // "PATHS" label
        gfx.drawString(font, "PATHS", 6, 6, 0xFFF0B800, false);

        // thin divider under PATHS label
        gfx.fill(4, 15, SIDEBAR_W - 4, 16, 0x55C8960A);

        // path tabs
        int tabsStartY = 19;
        for (int i = 0; i < pathTabs.size(); i++) {
            ResourceLocation pathId = pathTabs.get(i);
            int tabY = tabsStartY + i * (TAB_H + TAB_GAP);
            boolean active = pathId.equals(selectedPath);

            if (active) {
                gfx.fill(TAB_X, tabY, TAB_X + TAB_W, tabY + TAB_H, 0x2EC8960A);
                gfx.fill(TAB_X, tabY, TAB_X + TAB_W, tabY + 1, 0xFFC8960A);
                gfx.fill(TAB_X, tabY + TAB_H - 1, TAB_X + TAB_W, tabY + TAB_H, 0xFFC8960A);
                gfx.fill(TAB_X, tabY, TAB_X + 1, tabY + TAB_H, 0xFFC8960A);
                gfx.fill(TAB_X + TAB_W - 1, tabY, TAB_X + TAB_W, tabY + TAB_H, 0xFFC8960A);
            } else {
                gfx.fill(TAB_X, tabY, TAB_X + TAB_W, tabY + TAB_H, 0x18050810);
                gfx.fill(TAB_X, tabY, TAB_X + TAB_W, tabY + 1, 0x44C8960A);
                gfx.fill(TAB_X, tabY + TAB_H - 1, TAB_X + TAB_W, tabY + TAB_H, 0x44C8960A);
                gfx.fill(TAB_X, tabY, TAB_X + 1, tabY + TAB_H, 0x44C8960A);
                gfx.fill(TAB_X + TAB_W - 1, tabY, TAB_X + TAB_W, tabY + TAB_H, 0x44C8960A);
            }

            String rawName = pathId.getPath();
            String name = rawName.isEmpty() ? rawName
                    : Character.toUpperCase(rawName.charAt(0)) + rawName.substring(1);
            String label = active ? "\u25b6 " + name : "  " + name;
            int color = active ? 0xFFF0B800 : 0xFF888888;
            gfx.drawString(font, label, TAB_X + 3, tabY + (TAB_H - 8) / 2, color, false);
        }

        // stats tab pinned to bottom
        int statsTabY = HEIGHT - TAB_H - 6;
        if (statsSelected) {
            gfx.fill(TAB_X, statsTabY, TAB_X + TAB_W, statsTabY + TAB_H, 0x2EC8960A);
            gfx.fill(TAB_X, statsTabY, TAB_X + TAB_W, statsTabY + 1, 0xFFC8960A);
            gfx.fill(TAB_X, statsTabY + TAB_H - 1, TAB_X + TAB_W, statsTabY + TAB_H, 0xFFC8960A);
            gfx.fill(TAB_X, statsTabY, TAB_X + 1, statsTabY + TAB_H, 0xFFC8960A);
            gfx.fill(TAB_X + TAB_W - 1, statsTabY, TAB_X + TAB_W, statsTabY + TAB_H, 0xFFC8960A);
            gfx.drawString(font, "\u25b6 Stats", TAB_X + 3, statsTabY + (TAB_H - 8) / 2, 0xFFF0B800, false);
        } else {
            gfx.fill(TAB_X, statsTabY, TAB_X + TAB_W, statsTabY + TAB_H, 0x18050810);
            gfx.fill(TAB_X, statsTabY, TAB_X + TAB_W, statsTabY + 1, 0x44C8960A);
            gfx.fill(TAB_X, statsTabY + TAB_H - 1, TAB_X + TAB_W, statsTabY + TAB_H, 0x44C8960A);
            gfx.fill(TAB_X, statsTabY, TAB_X + 1, statsTabY + TAB_H, 0x44C8960A);
            gfx.fill(TAB_X + TAB_W - 1, statsTabY, TAB_X + TAB_W, statsTabY + TAB_H, 0x44C8960A);
            gfx.drawString(font, "  Stats", TAB_X + 3, statsTabY + (TAB_H - 8) / 2, 0xFF888888, false);
        }

        super.render(gfx, mouseX, mouseY, partialTick);

        // popup visibility sync
        techniquePopup.setActive(pathPanel.isActive() && pathPanel.isPopupVisible());
    }
}
