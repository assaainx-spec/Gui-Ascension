package net.thejadeproject.ascension.refactor_packages.gui.elements.cultivation;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class StatsPanel extends RenderableElement {
    private boolean helpVisible = false;

    private record LimiterEntry(LimiterStatRow row, int y) {}
    private final List<LimiterEntry> limiterEntries = new ArrayList<>();

    public StatsPanel(UIFrame frame) {
        super(frame);
        setWidth(160);
        setHeight(180);

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

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        var font = Minecraft.getInstance().font;
        int w = getWidth(), h = getHeight();
        PathDetailPanel.drawChrome(gfx, font, w, h, "STATS");

        // ? help button (to the left of the × button)
        int qx = w - 26;
        gfx.fill(qx, 2, qx + 12, 12, 0x44006396);
        gfx.drawString(font, "?", qx + 3, 3, 0xFF4FC3F7, false);

        super.render(gfx, mouseX, mouseY, partialTick);

        if (helpVisible) {
            int pw = 110, ph = 56;
            int px = w + 4, py = 0;
            gfx.fill(px, py, px+pw, py+ph, 0xEE050810);
            gfx.fill(px, py, px+pw, py+1, 0xFF4FC3F7);
            gfx.fill(px, py+ph-1, px+pw, py+ph, 0xFF4FC3F7);
            gfx.fill(px, py, px+1, py+ph, 0xFF4FC3F7);
            gfx.fill(px+pw-1, py, px+pw, py+ph, 0xFF4FC3F7);
            gfx.drawString(font, "Step sizes:", px+5, py+5, 0xFF4FC3F7, false);
            gfx.drawString(font, "Default  0.01",  px+5, py+16, 0xFFAAAAAA, false);
            gfx.drawString(font, "Shift    0.1",   px+5, py+26, 0xFFAAAAAA, false);
            gfx.drawString(font, "Ctrl     0.001", px+5, py+36, 0xFFAAAAAA, false);
            gfx.drawString(font, "Shft+Ctrl  1.0", px+5, py+46, 0xFFAAAAAA, false);
        }
    }
}
