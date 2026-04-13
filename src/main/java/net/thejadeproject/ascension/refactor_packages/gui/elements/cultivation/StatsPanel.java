package net.thejadeproject.ascension.refactor_packages.gui.elements.cultivation;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.elements.built_in.EasyLabel;
import net.lucent.easygui.gui.events.EasyEvents;
import net.lucent.easygui.gui.events.type.EasyEvent;
import net.lucent.easygui.gui.events.type.EasyMouseEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

import java.util.ArrayList;
import java.util.List;

public class StatsPanel extends RenderableElement {

    private boolean helpVisible = false;
    private Runnable onClose;

    private record LimiterEntry(LimiterStatRow row, int y) {}
    private final List<LimiterEntry> limiterEntries = new ArrayList<>();

    public StatsPanel(UIFrame frame) {
        super(frame);
        setWidth(160);
        setHeight(180);

        EasyLabel titleLabel = new EasyLabel(frame);
        titleLabel.setText(Component.literal("STATS"));
        titleLabel.setTextColor(0xFF4FC3F7);
        titleLabel.getPositioning().setX(5);
        titleLabel.getPositioning().setY(3);
        addChild(titleLabel);

        EasyLabel helpLabel = new EasyLabel(frame);
        helpLabel.setText(Component.literal("?"));
        helpLabel.setTextColor(0xFF4FC3F7);
        helpLabel.getPositioning().setX(getWidth() - 23);
        helpLabel.getPositioning().setY(3);
        addChild(helpLabel);

        int y = 18;

        addStatRow(frame, "Max HP",     vanillaId("generic.max_health"),        y); y += 10;
        addStatRow(frame, "Armour",     vanillaId("generic.armor"),             y); y += 10;
        addStatRow(frame, "Toughness",  vanillaId("generic.armor_toughness"),   y); y += 10;
        addStatRow(frame, "Mining Spd", vanillaId("player.block_break_speed"),  y); y += 12;

        y += 3;

        addLimiterRow(frame, "Atk Damage",    vanillaId("generic.attack_damage"),    y); y += 10;
        addLimiterRow(frame, "Atk Speed",     vanillaId("generic.attack_speed"),     y); y += 10;
        addLimiterRow(frame, "Atk Knockback", vanillaId("generic.attack_knockback"), y); y += 10;
        addLimiterRow(frame, "Move Speed",    vanillaId("generic.movement_speed"),   y); y += 10;
        addLimiterRow(frame, "Jump Height",   vanillaId("generic.jump_strength"),    y); y += 10;
        addLimiterRow(frame, "Step Height",   vanillaId("generic.step_height"),      y);

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
            return;
        }
        tryClick(local.x, local.y);
        event.setCanceled(true);
    }

    public void tryClick(double px, double py) {
        int qx = getWidth() - 26;
        if (px >= qx && px <= qx + 12 && py >= 2 && py <= 12) {
            helpVisible = !helpVisible;
            return;
        }
        for (LimiterEntry entry : limiterEntries) {
            if (py >= entry.y() && py < entry.y() + entry.row().getHeight()) {
                entry.row().tryClick(px - 6, py - entry.y());
                return;
            }
        }
    }

    private ResourceLocation vanillaId(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    private void addStatRow(UIFrame frame, String label, ResourceLocation attrId, int y) {
        var holder = Minecraft.getInstance().player.level().registryAccess()
                .registryOrThrow(Registries.ATTRIBUTE).getHolder(attrId).orElse(null);
        if (holder == null) return;
        StatRow row = new StatRow(frame, label, holder);
        row.getPositioning().setX(6);
        row.getPositioning().setY(y);
        addChild(row);
    }

    private void addLimiterRow(UIFrame frame, String label, ResourceLocation attrId, int y) {
        var holder = Minecraft.getInstance().player.level().registryAccess()
                .registryOrThrow(Registries.ATTRIBUTE).getHolder(attrId).orElse(null);
        if (holder == null) return;
        LimiterStatRow row = new LimiterStatRow(frame, label, holder, attrId);
        row.getPositioning().setX(6);
        row.getPositioning().setY(y);
        addChild(row);
        limiterEntries.add(new LimiterEntry(row, y));
    }

    private static void fillBorderedRect(GuiGraphics gfx, int x, int y, int w, int h, int bg, int border) {
        gfx.fill(x, y, x + w, y + h, bg);
        gfx.fill(x, y, x + w, y + 1, border);
        gfx.fill(x, y + h - 1, x + w, y + h, border);
        gfx.fill(x, y, x + 1, y + h, border);
        gfx.fill(x + w - 1, y, x + w, y + h, border);
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
        gfx.drawString(font, "\u00d7", w - 12 + (10 - font.width("\u00d7") + 1) / 2, 3, 0xFFFF5555, false);

        super.render(gfx, mouseX, mouseY, partialTick);

        if (helpVisible) {
            int pw = 110, ph = 56;
            int px = w - pw, py = -ph - 2;
            fillBorderedRect(gfx, px, py, pw, ph, 0xEE050810, 0xFF4FC3F7);
            gfx.drawString(font, "Step sizes:", px+5, py+5, 0xFF4FC3F7, false);
            gfx.drawString(font, "Default  0.01",  px+5, py+16, 0xFFAAAAAA, false);
            gfx.drawString(font, "Shift    0.1",   px+5, py+26, 0xFFAAAAAA, false);
            gfx.drawString(font, "Ctrl     0.001", px+5, py+36, 0xFFAAAAAA, false);
            gfx.drawString(font, "Shft+Ctrl  1.0", px+5, py+46, 0xFFAAAAAA, false);
        }
    }
}
