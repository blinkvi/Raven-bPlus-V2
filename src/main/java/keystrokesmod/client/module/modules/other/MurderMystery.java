package keystrokesmod.client.module.modules.other;

import java.awt.Color;
import java.util.ArrayList;

import keystrokesmod.client.events.TickEvent;
import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.RenderUtils;
import keystrokesmod.client.utils.Utils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "MurderMystery", category = Category.Other)
public class MurderMystery extends ClientModule {
	private final TickSetting checkBow = new TickSetting("Check bow", this, false);
	
	private final TickSetting drawGold = new TickSetting("Draw gold esp", this, true);
	private final TickSetting drawBow = new TickSetting("Draw bow esp", this, false);
	private final TickSetting drawMurder = new TickSetting("Draw murder esp", this, false);
	private final TickSetting drawDetective = new TickSetting("Draw detective esp", this, false);

	private final ArrayList<EntityPlayer> murderers = new ArrayList<>();
	private final ArrayList<EntityPlayer> detectives = new ArrayList<>();
	
	@SubscribeEvent
	public void onChangeWorld(WorldEvent.Load event) {
		murderers.clear();
		detectives.clear();
	}
	
	@Override
	public void onDisable() {
		murderers.clear();
		detectives.clear();
	}
	
	@SubscribeEvent
	public void onTick(TickEvent event) {
		if (!Utils.Player.isPlayerInGame()) return;

		for (EntityPlayer player : mc.theWorld.playerEntities) {
			if (player.getHeldItem() == null || detectives.contains(player) || player == mc.thePlayer) continue;
			String itemName = player.getHeldItem().getDisplayName();

			if (!murderers.contains(player) && isMurder(itemName)) {
				murderers.add(player);
				mc.thePlayer.playSound("note.pling", 1.0F, 1.0F);
				sendNotification(player.getName() + " es el asesino.", EnumChatFormatting.RED, "!");
			}

			if (checkBow.isToggled() && isBow(itemName) && !murderers.contains(player)) {
				detectives.add(player);
				sendNotification(player.getName() + " tiene un arco.", EnumChatFormatting.BLUE, "*");
			}
		}
	}
	
	@SubscribeEvent
	public void onRenderWorldLast(RenderWorldLastEvent event) {
		for (Entity entity : mc.theWorld.loadedEntityList) {
			if (entity == mc.thePlayer) continue;

			if (entity instanceof EntityItem && ((EntityItem) entity).getEntityItem().getItem() == Items.gold_ingot && drawGold.isToggled()) {
				RenderUtils.drawSimpleItemBox(entity, Color.YELLOW);
			} else if (entity instanceof EntityArmorStand && drawBow.isToggled() && isArmorStandHoldingBow((EntityArmorStand) entity)) {
				RenderUtils.drawSimpleItemBox(entity, Color.CYAN);
			} else if (entity instanceof EntityPlayer) {
				if (murderers.contains(entity) && drawMurder.isToggled()) {
					RenderUtils.drawSimpleBox((EntityPlayer) entity, Color.RED.getRGB(), event.partialTicks);
				} else if (detectives.contains(entity) && drawDetective.isToggled()) {
					RenderUtils.drawSimpleBox((EntityPlayer) entity, Color.BLUE.getRGB(), event.partialTicks);
				}
			}
		}
	}
	
	private boolean isArmorStandHoldingBow(EntityArmorStand armorStand) {
		return armorStand.getEquipmentInSlot(0) != null && armorStand.getEquipmentInSlot(0).getItem() == Items.bow;
	}

	private void sendNotification(String message, EnumChatFormatting color, String symbol) {
		display(EnumChatFormatting.YELLOW + "[" + color + symbol + EnumChatFormatting.YELLOW + "] " + color + message + EnumChatFormatting.RESET);
	}

	private boolean isMurder(String itemName) {
		return itemName.contains("Knife") || itemName.contains("Cuchillo");
	}

	private boolean isBow(String itemName) {
		return itemName.contains("Bow") || itemName.contains("Arco");
	}
	
    private void display(final Object message, final Object... objects) {
        if (mc.thePlayer != null) {
            final String format = String.format(message.toString(), objects);
            mc.thePlayer.addChatMessage(new ChatComponentText(format));
        }
    }
}
