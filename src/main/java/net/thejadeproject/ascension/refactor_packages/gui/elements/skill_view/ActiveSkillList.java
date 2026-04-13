package net.thejadeproject.ascension.refactor_packages.gui.elements.skill_view;

import net.lucent.easygui.gui.RenderableElement;
import net.lucent.easygui.gui.UIFrame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.thejadeproject.ascension.data_attachments.ModAttachments;
import net.thejadeproject.ascension.refactor_packages.entity_data.IEntityData;
import net.thejadeproject.ascension.refactor_packages.gui.elements.general.ScrollBox;
import net.thejadeproject.ascension.refactor_packages.gui.elements.skill_view.slots.ActiveSkillIcon;
import net.thejadeproject.ascension.refactor_packages.gui.elements.skill_view.slots.ActiveSkillSlot;
import net.thejadeproject.ascension.refactor_packages.registries.AscensionRegistries;
import net.thejadeproject.ascension.refactor_packages.skills.castable.ICastableSkill;

public class ActiveSkillList extends RenderableElement {
    private ScrollBox scrollBox;

    public ActiveSkillList(UIFrame frame) {
        super(frame);
        setWidth(192);
        setHeight(119);
        ScrollBox scrollBox = new ScrollBox(frame,18);
        scrollBox.getPositioning().setY(32);
        scrollBox.getPositioning().setX(15);
        scrollBox.setWidth(81);
        scrollBox.setHeight(72);
        this.scrollBox = scrollBox;
        addChild(scrollBox);

        refreshSkills();
    }

    public void refreshSkills(){
        IEntityData entityData = Minecraft.getInstance().player.getData(ModAttachments.ENTITY_DATA);
        for(ResourceLocation skillId : entityData.getAllSkills()){
            if(!(AscensionRegistries.Skills.SKILL_REGISTRY.get(skillId) instanceof ICastableSkill skill)) continue;
            //only do active skills
            ActiveSkillIcon skillIcon = new ActiveSkillIcon(getUiFrame());
            System.out.println("trying to add skill :" +skillId.toString());
            skillIcon.setSkill(skillId);
            scrollBox.addChild(skillIcon);
        }
    }


    @Override
    public void render(GuiGraphics gfx, int mouseX, int mouseY, float partialTick) {
        int w = getWidth(), h = getHeight();
        var font = Minecraft.getInstance().font;
        gfx.fill(0, 0, w, h, 0xCC050810);
        gfx.fill(0,   0,   w,   1,   0xFF4FC3F7);
        gfx.fill(0,   h-1, w,   h,   0xFF4FC3F7);
        gfx.fill(0,   0,   1,   h,   0xFF4FC3F7);
        gfx.fill(w-1, 0,   w,   h,   0xFF4FC3F7);
        gfx.fill(0, 13, w, 14, 0x55006396);
        // corner bracket accents
        gfx.fill(1, 1, 4, 2, 0x88006396);  gfx.fill(1, 1, 2, 4, 0x88006396);
        gfx.fill(w-4, 1, w-1, 2, 0x88006396);  gfx.fill(w-2, 1, w-1, 4, 0x88006396);
        gfx.fill(1, h-2, 4, h-1, 0x88006396);  gfx.fill(1, h-4, 2, h-1, 0x88006396);
        gfx.fill(w-4, h-2, w-1, h-1, 0x88006396);  gfx.fill(w-2, h-4, w-1, h-1, 0x88006396);
        gfx.drawString(font, "ACTIVE", 5, 3, 0xFF4FC3F7, false);
        // empty slot grid behind the scrollbox (4 cols × 4 rows at scrollbox origin x=15, y=32)
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                int sx = 15 + col * 18;
                int sy = 32 + row * 18;
                gfx.fill(sx,      sy,      sx + 18, sy + 18, 0xFF0A0C10);
                gfx.fill(sx,      sy,      sx + 18, sy + 1,  0xFF2A3A4A);
                gfx.fill(sx,      sy + 17, sx + 18, sy + 18, 0xFF2A3A4A);
                gfx.fill(sx,      sy,      sx + 1,  sy + 18, 0xFF2A3A4A);
                gfx.fill(sx + 17, sy,      sx + 18, sy + 18, 0xFF2A3A4A);
            }
        }
        super.render(gfx, mouseX, mouseY, partialTick);
    }

}
