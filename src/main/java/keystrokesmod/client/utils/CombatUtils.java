package keystrokesmod.client.utils;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;

public class CombatUtils implements IMinecraft {
    public static boolean canTarget(final Entity entity, final boolean idk) {
        if (entity != null && entity != mc.thePlayer) {
            EntityLivingBase entityLivingBase = null;
            if (entity instanceof EntityLivingBase) {
                entityLivingBase = (EntityLivingBase)entity;
            }
            final boolean isTeam = isTeam((EntityPlayer)mc.thePlayer, entity);
            final boolean isVisible = !entity.isInvisible();
            return !(entity instanceof EntityArmorStand) && isVisible && ((entity instanceof EntityPlayer && !isTeam && !idk) || entity instanceof EntityAnimal || entity instanceof EntityMob || (entity instanceof EntityLivingBase && entityLivingBase.isEntityAlive()));
        }
        return false;
    }
    
    public static boolean isTeam(final EntityPlayer player, final Entity entity) {
        if (entity instanceof EntityPlayer && ((EntityPlayer)entity).getTeam() != null && player.getTeam() != null) {
            final Character entity_3 = entity.getDisplayName().getFormattedText().charAt(3);
            final Character player_3 = player.getDisplayName().getFormattedText().charAt(3);
            final Character entity_4 = entity.getDisplayName().getFormattedText().charAt(2);
            final Character player_4 = player.getDisplayName().getFormattedText().charAt(2);
            boolean isTeam = false;
            if (entity_3.equals(player_3) && entity_4.equals(player_4)) {
                isTeam = true;
            }
            else {
                final Character entity_5 = entity.getDisplayName().getFormattedText().charAt(1);
                final Character player_5 = player.getDisplayName().getFormattedText().charAt(1);
                final Character entity_6 = entity.getDisplayName().getFormattedText().charAt(0);
                final Character player_6 = player.getDisplayName().getFormattedText().charAt(0);
                if (entity_5.equals(player_5) && Character.isDigit(0) && entity_6.equals(player_6)) {
                    isTeam = true;
                }
            }
            return isTeam;
        }
        return true;
    }
}
