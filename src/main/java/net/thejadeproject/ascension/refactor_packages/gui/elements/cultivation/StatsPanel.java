package net.thejadeproject.ascension.refactor_packages.gui.elements.cultivation;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.lucent.easygui.gui.events.EasyEvents;
import net.lucent.easygui.gui.events.type.EasyEvent;
import net.lucent.easygui.gui.events.type.EasyMouseEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;

public class StatsPanel extends RenderableElement {
    private boolean helpVisible = false;

    public StatsPanel(UIFrame frame) {
        super(frame);
        setWidth(195);
        setHeight(180);

        int y = 14;

        // read-only rows
        addStatRow(frame, "Max HP",     vanillaId("generic.max_health"),        y); y += 10;
        addStatRow(frame, "Armour",     vanillaId("generic.armor"),             y); y += 10;
        addStatRow(frame, "Toughness",  vanillaId("generic.armor_toughness"),   y); y += 10;
        addStatRow(frame, "Mining Spd", vanillaId("player.block_break_speed"),  y); y += 12;

        y += 3; // gap before limiter rows

        // limiter rows
        addLimiterRow(frame, "Atk Damage",    vanillaId("generic.attack_damage"),    y); y += 10;
        addLimiterRow(frame, "Atk Speed",     vanillaId("generic.attack_speed"),     y); y += 10;
        addLimiterRow(frame, "Atk Knockback", vanillaId("generic.attack_knockback"), y); y += 10;
        addLimiterRow(frame, "Move Speed",    vanillaId("generic.movement_speed"),   y); y += 10;
        addLimiterRow(frame, "Jump Height",   vanillaId("generic.jump_strength"),    y); y += 10;
        addLimiterRow(frame, "Step Height",   vanillaId("generic.step_height"),      y);

        addEventListener(EasyEvents.MOUSE_DOWN_EVENT, this::onMouseDown);
    }

    private void onMouseDown(EasyEvent event) {
        if (!(event instanceof EasyMouseEvent mouseEvent)) return;
        Vec2 local = globalToLocalPositionPoint((float) mouseEvent.getMouseX(), (float) mouseEvent.getMouseY());
        double mx = local.x;
        double my = local.y;
        int qx = getWidth() - 14;
        if (mx >= qx && mx <= qx+12 && my >= 2 && my <= 14) {
            helpVisible = !helpVisible;
            event.setCanceled(true);
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
    }

    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        // panel background and border
        gfx.fill(0, 0, getWidth(), getHeight(), 0xD0050810);
        gfx.fill(0, 0, getWidth(), 1, 0xFFC8960A);
        gfx.fill(0, getHeight()-1, getWidth(), getHeight(), 0xFFC8960A);
        gfx.fill(0, 0, 1, getHeight(), 0xFFC8960A);
        gfx.fill(getWidth()-1, 0, getWidth(), getHeight(), 0xFFC8960A);

        // header
        gfx.drawString(Minecraft.getInstance().font, "STATS", 6, 4, 0xFFF0B800, false);

        // ? button top-right
        int qx = getWidth() - 14;
        gfx.fill(qx, 2, qx+12, 14, 0xFF222222);
        gfx.fill(qx, 2, qx+12, 3, 0xFFC8960A);
        gfx.fill(qx, 13, qx+12, 14, 0xFFC8960A);
        gfx.fill(qx, 2, qx+1, 14, 0xFFC8960A);
        gfx.fill(qx+11, 2, qx+12, 14, 0xFFC8960A);
        gfx.drawString(Minecraft.getInstance().font, "?", qx+3, 4, 0xFFF0B800, false);

        // divider between read-only and limiter rows
        int divY = 14 + 4*10 + 3;
        gfx.fill(4, divY-2, getWidth()-4, divY-1, 0x55C8960A);

        super.render(gfx, mouseX, mouseY, partialTick);

        // help popup drawn on top
        if (helpVisible) {
            int pw = 110;
            int ph = 56;
            int px = getWidth() - pw - 2;
            int py = 0;
            gfx.fill(px, py, px+pw, py+ph, 0xEE050810);
            gfx.fill(px, py, px+pw, py+1, 0xFFC8960A);
            gfx.fill(px, py+ph-1, px+pw, py+ph, 0xFFC8960A);
            gfx.fill(px, py, px+1, py+ph, 0xFFC8960A);
            gfx.fill(px+pw-1, py, px+pw, py+ph, 0xFFC8960A);
            var font = Minecraft.getInstance().font;
            gfx.drawString(font, "Step sizes:", px+5, py+5,  0xFFF0B800, false);
            gfx.drawString(font, "Default  0.01",   px+5, py+16, 0xFFAAAAAA, false);
            gfx.drawString(font, "Shift    0.1",    px+5, py+26, 0xFFAAAAAA, false);
            gfx.drawString(font, "Ctrl     0.001",  px+5, py+36, 0xFFAAAAAA, false);
            gfx.drawString(font, "Shft+Ctrl  1.0",  px+5, py+46, 0xFFAAAAAA, false);
        }
    }

}
