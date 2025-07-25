package keystrokesmod.client.module.modules.render;

import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.DescriptionSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "NameTagsV2", category = Category.Render)
public class NameTagsV2 extends ClientModule {
    private final SliderSetting distance = new SliderSetting("Distance", this, 2.4f, 1, 7, 0.1f);
    private final SliderSetting ownScale = new SliderSetting("Own Scale", this, 2.4f, 0.5f, 10, 0.1f);
    private final SliderSetting scale = new SliderSetting("Enemy Scale", this, 2.4f, 0.5f, 10, 0.1f);
    private final SliderSetting offset = new SliderSetting("Offset", this, 0.0, -40.0, 40.0, 1.0);
    private final TickSetting shadow = new TickSetting("Shadow", this, true);
    private final TickSetting selfNametag = new TickSetting("Self NameTag", this, true);
    private final TickSetting onlyRenderName = new TickSetting("Only Render Name", this, true);
    private final TickSetting showInvisibles = new TickSetting("Show Invisibles", this, true);
    
    private final DescriptionSetting desc = new DescriptionSetting("Equipament", this);
    
    private final TickSetting showArmor = new TickSetting("Show Armor", this, false);
    private final TickSetting showEnchants = new TickSetting("Show Protection", this, true, showArmor::isToggled);
    private final TickSetting showDurability = new TickSetting("Show Durability", this, true, showArmor::isToggled);
    private final TickSetting customScale = new TickSetting("Custom Scale", this, true);
    private final SliderSetting scaleArmor = new SliderSetting("Scale Armor Items", this, 0.8f, 0.6f, 1, 0.1f, customScale::isToggled);
    private final SliderSetting scaleItems = new SliderSetting("Scale Hand Items", this, 0.95f, 0.6f, 1, 0.1f, customScale::isToggled);
    private final SliderSetting scaleDurability = new SliderSetting("Scale Durability", this, 0.6f, 0.6f, 1, 0.1f, customScale::isToggled);
    private final SliderSetting scaleEnchant = new SliderSetting("Scale Enchant", this, 0.8f, 0.6f, 1, 0.1f, customScale::isToggled);
    
    private float scaleArmorItems;
    private float scaleHandItem;
    private float scaleForDurability;
    private float scaleForEnchant;

    @SubscribeEvent
    public void nameTag(final RenderLivingEvent.Specials.Pre<? extends EntityLivingBase> event) {
        if (event.entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) event.entity;
            if (player.deathTime == 0) {
                if (!showInvisibles.isToggled() && player.isInvisible()) return;
                if (player == mc.thePlayer && !selfNametag.isToggled()) return;

                event.setCanceled(true);

                String name = onlyRenderName.isToggled() ? player.getName() : player.getDisplayName().getFormattedText();

                renderNewTag(event, player, name);
            }
        }
    }

    private void renderNewTag(RenderLivingEvent.Specials.Pre<? extends EntityLivingBase> event, EntityPlayer player, String name) {
        double scaleRatio = player == mc.thePlayer ? ownScale.getInput() : getSize(player) / 10.0F * scale.getInput() * 1.5D;
        float baseScale = 0.02666667F;

        GlStateManager.pushMatrix();
        GlStateManager.translate(event.x, event.y + player.height + 0.5F, event.z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-baseScale * scaleRatio, -baseScale * scaleRatio, baseScale * scaleRatio);

        if (showArmor.isToggled()) {
            renderArmor(player);
        }

        if (player.isSneaking()) {
            GlStateManager.translate(0.0F, 9.375F, 0.0F);
        }

        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);

        Tessellator tess = Tessellator.getInstance();
        WorldRenderer wr = tess.getWorldRenderer();
        int offsetY = (int) -offset.getInput();
        int nameWidth = mc.fontRendererObj.getStringWidth(name) / 2;

        GlStateManager.disableTexture2D();
        wr.begin(7, DefaultVertexFormats.POSITION_COLOR);
        wr.pos(-nameWidth - 1, -1 + offsetY, 0).color(0f, 0f, 0f, 0.55f).endVertex();
        wr.pos(-nameWidth - 1, 8 + offsetY, 0).color(0f, 0f, 0f, 0.55f).endVertex();
        wr.pos(nameWidth + 1, 8 + offsetY, 0).color(0f, 0f, 0f, 0.55f).endVertex();
        wr.pos(nameWidth + 1, -1 + offsetY, 0).color(0f, 0f, 0f, 0.55f).endVertex();
        tess.draw();
        GlStateManager.enableTexture2D();

        mc.fontRendererObj.drawString(name, -nameWidth, offsetY, -1, shadow.isToggled());

        GlStateManager.disableBlend();
        GlStateManager.enableDepth();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    private void renderItemStack(final ItemStack stack, final int x, final int y) {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();

        fixGlintShit();
        mc.getRenderItem().zLevel = -150.0F;
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);

        if (!(stack.getItem() instanceof ItemSword) && !(stack.getItem() instanceof ItemBow) &&
            !(stack.getItem() instanceof ItemTool) && !(stack.getItem() instanceof ItemArmor)) {
            mc.getRenderItem().renderItemOverlays(mc.fontRendererObj, stack, x, y);
        }

        mc.getRenderItem().zLevel = 0.0F;
        renderEnchantText(stack, x, y);

        GlStateManager.popMatrix();
    }

    private void renderArmor(EntityPlayer e) {
        int pos = 0;
        
        if (customScale.isToggled()) {
        	scaleArmorItems = (float) scaleArmor.getInput();
        	scaleHandItem = (float) scaleItems.getInput();
        } else {
	        scaleArmorItems = 0.9f;
	        scaleHandItem = 0.95f;
        }
        
        for (ItemStack is : e.inventory.armorInventory) {
            if (is != null) pos -= 8;
        }
        if (e.getHeldItem() != null) {
            pos -= 8;
        }
        
        if (e.getHeldItem() != null) {
            ItemStack item = e.getHeldItem().copy();
            if (item.hasEffect() && (item.getItem() instanceof ItemTool || item.getItem() instanceof ItemArmor)) {
                item.stackSize = 1;
            }

            float handOffsetX = 0;
            float handOffsetY = 4;

            GlStateManager.pushMatrix();
            GlStateManager.scale(scaleHandItem, scaleHandItem, scaleHandItem);

            float drawX = (pos + handOffsetX) / scaleHandItem;
            float drawY = (-22 + handOffsetY) / scaleHandItem;

            renderItemStack(item, (int) drawX, (int) drawY);
            GlStateManager.popMatrix();

            pos += 16;
        }

        for (int i = 3; i >= 0; --i) {
            ItemStack stack = e.inventory.armorInventory[i];
            if (stack != null) {
                float offsetX = 0;
                float offsetY = 0;

                switch (i) {
                    case 3: offsetX = 0; offsetY = 4; break;
                    case 2: offsetX = 0; offsetY = 4; break;
                    case 1: offsetX = 0; offsetY = 4; break;
                    case 0: offsetX = 0; offsetY = 4; break;
                }

                GlStateManager.pushMatrix();
                GlStateManager.scale(scaleArmorItems, scaleArmorItems, scaleArmorItems);

                float drawX = (pos + offsetX) / scaleArmorItems;
                float drawY = (-20 + offsetY) / scaleArmorItems;

                renderItemStack(stack, (int) drawX, (int) drawY);
                GlStateManager.popMatrix();

                pos += 16;
            }
        }
    }

    private void renderEnchantText(ItemStack stack, int x, int y) {
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        int enchantY = y - 24;
        
        if (customScale.isToggled()) {
            scaleForDurability = (float) scaleDurability.getInput();
            scaleForEnchant = (float) scaleEnchant.getInput();
        } else {
            scaleForDurability = 0.6f;
            scaleForEnchant = 0.6f;
        }

        if (showDurability.isToggled() && stack.getItem() instanceof ItemArmor) {
            ItemArmor armor = (ItemArmor) stack.getItem();
            int armorSlot = armor.armorType;

            float durabilityOffsetX = 0;
            float durabilityOffsetY = 0;

            switch (armorSlot) {
                case 0:
                    durabilityOffsetX = 18;
                    durabilityOffsetY = 4;
                    break;
                case 1:
                    durabilityOffsetX = 0;
                    durabilityOffsetY = 4;
                    break;
                case 2:
                    durabilityOffsetX = -14;
                    durabilityOffsetY = 4;
                    break;
                case 3:
                    durabilityOffsetX = -30;
                    durabilityOffsetY = 4;
                    break;
            }

            int remainingDurability = stack.getMaxDamage() - stack.getItemDamage();
            String durabilityText = String.valueOf(remainingDurability);
            int textWidth = mc.fontRendererObj.getStringWidth(durabilityText);

            float invDurabilityScale = 1.0f / scaleForDurability;

            GlStateManager.pushMatrix();
            GlStateManager.scale(scaleForDurability, scaleForDurability, scaleForDurability);

            float drawX = (x * 2 - textWidth / 2F + 20 + durabilityOffsetX) * invDurabilityScale;
            float drawY = (y - 12 + durabilityOffsetY) * invDurabilityScale;

            mc.fontRendererObj.drawStringWithShadow(durabilityText, drawX, drawY, 0xFFFFFF);

            GlStateManager.popMatrix();
        }

        if (showEnchants.isToggled() && stack.getEnchantmentTagList() != null && stack.getEnchantmentTagList().tagCount() < 6) {
            Item item = stack.getItem();

            if (item instanceof ItemTool || item instanceof ItemSword || item instanceof ItemBow || item instanceof ItemArmor) {
                NBTTagList nbttaglist = stack.getEnchantmentTagList();

                float offsetX = 0;
                float offsetY = 0;

                if (item instanceof ItemArmor) {
                    int armorSlot = ((ItemArmor) item).armorType;
                    switch (armorSlot) {
                        case 0: offsetX += 20; offsetY += 4; break;
                        case 1: offsetX = 0; offsetY += 4; break;
                        case 2: offsetX -= 14; offsetY += 4; break;
                        case 3: offsetX -= 30; offsetY += 4; break;
                    }
                } else if (item instanceof ItemSword) {
                    offsetX += 35;
                    offsetY += 0;
                } else if (item instanceof ItemTool) {
                    offsetX += 35;
                    offsetY += 0;
                } else if (item instanceof ItemBow) {
                    offsetX += 35;
                    offsetY += 0;
                }

                float invEnchantScale = 1.0f / scaleForEnchant;

                GlStateManager.pushMatrix();
                GlStateManager.scale(scaleForEnchant, scaleForEnchant, scaleForEnchant);

                for (int i = 0; i < nbttaglist.tagCount(); ++i) {
                    NBTTagCompound enchantCompound = nbttaglist.getCompoundTagAt(i);
                    int id = enchantCompound.getShort("id");
                    int lvl = enchantCompound.getShort("lvl");

                    if (lvl > 0) {
                        String abbreviated = getEnchantmentAbbreviated(id, stack);
                        if (abbreviated != null) {
                            String text = abbreviated + lvl;
                            int textWidth = mc.fontRendererObj.getStringWidth(text);

                            float drawX = (x * 2 - textWidth / 2F + 20 + offsetX) * invEnchantScale;
                            float drawY = (enchantY + 20 + offsetY + i * 6) * invEnchantScale;

                            mc.fontRendererObj.drawString(text, drawX, drawY, -1, true);
                        }
                    }
                }

                GlStateManager.popMatrix();
            }
        }

        GlStateManager.enableDepth();
        GlStateManager.popMatrix();
    }

    private String getEnchantmentAbbreviated(int id, ItemStack stack) {
        if (stack == null || stack.getItem() == null) return null;
        Item item = stack.getItem();

        if (id == 0 && item instanceof ItemArmor) return "p";
        if (id == 5 && item instanceof ItemArmor) return "thr";

        if (item instanceof ItemBow) {
            switch (id) {
                case 48: return "pow";
                case 49: return "pun";
                case 50: return "flm";
                case 51: return "inf";
            }
        }

        if (item instanceof ItemSword) {
            switch (id) {
                case 16: return "sh";
                case 19: return "kb";
                case 20: return "fa";
            }
        }

        if (item instanceof ItemTool) {
            if (id == 32) return "eff";
        }

        return null;
    }

    private float getSize(EntityPlayer player) {
        return (float) Math.max(mc.thePlayer.getDistanceToEntity(player) / 4.0F, distance.getInput());
    }

    private static void fixGlintShit() {
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableBlend();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
        GlStateManager.disableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.disableAlpha();
        GlStateManager.disableBlend();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
    }
}