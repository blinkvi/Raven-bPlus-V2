package keystrokesmod.client.module.modules.combat;

import java.awt.Color;
import java.util.List;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.modules.world.AntiBot;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.ReflectUtil;
import keystrokesmod.client.utils.Utils;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@ModuleInfo(name = "HitBox", category = Category.Combat)
public class HitBox extends ClientModule {
    private final SliderSetting a = new SliderSetting("Multiplier", this, 1.2, 1.0, 5.0, 0.05);
    private final TickSetting b = new TickSetting("Show new hitbox", this, false);
    private MovingObjectPosition mv;
    
    @Override
    public void update() {
        gmo(1.0f);
    }
    
    @SubscribeEvent
    public void m(final MouseEvent e) {
        if (!Utils.Player.isPlayerInGame()) {
            return;
        }
        if (e.button == 0 && e.buttonstate && mv != null) {
            mc.objectMouseOver = mv;
        }
    }
    
    @SubscribeEvent
    public void ef(final TickEvent.RenderTickEvent ev) {
        if (!Utils.Player.isPlayerInGame()) {
            return;
        }
        final ClientModule autoClicker = Raven.moduleManager.getModuleByClazz(LeftClicker.class);
        if (autoClicker != null && !autoClicker.isEnabled()) {
            return;
        }
        if (autoClicker != null && autoClicker.isEnabled() && Mouse.isButtonDown(0) && mv != null) {
            mc.objectMouseOver = mv;
        }
    }
    
    @SubscribeEvent
    public void r1(final RenderWorldLastEvent e) {
        if (b.isToggled() && Utils.Player.isPlayerInGame()) {
            for (final Entity en : mc.theWorld.loadedEntityList) {
                if (en != mc.thePlayer && en instanceof EntityLivingBase && ((EntityLivingBase)en).deathTime == 0 && !(en instanceof EntityArmorStand) && !en.isInvisible()) {
                    this.rh(en, Color.WHITE);
                }
            }
        }
    }
    
    public double exp(final Entity en) {
        final ClientModule hitBox = Raven.moduleManager.getModuleByClazz(HitBox.class);
        final AntiBot bot = (AntiBot) Raven.moduleManager.getModuleByClazz(AntiBot.class);
        return (hitBox != null && isEnabled() && !bot.bot(en)) ? a.getInput() : 1.0;
    }
    
    public void gmo(final float partialTicks) {
        if (mc.getRenderViewEntity() != null && mc.theWorld != null) {
            mc.pointedEntity = null;
            Entity pE = null;
            final double d0 = 3.0;
            mv = mc.getRenderViewEntity().rayTrace(d0, partialTicks);
            double d2 = d0;
            final Vec3 vec3 = mc.getRenderViewEntity().getPositionEyes(partialTicks);
            if (mv != null) {
                d2 = mv.hitVec.distanceTo(vec3);
            }
            final Vec3 vec4 = mc.getRenderViewEntity().getLook(partialTicks);
            final Vec3 vec5 = vec3.addVector(vec4.xCoord * d0, vec4.yCoord * d0, vec4.zCoord * d0);
            Vec3 vec6 = null;
            final float f1 = 1.0f;
            final List list = mc.theWorld.getEntitiesWithinAABBExcludingEntity(mc.getRenderViewEntity(), mc.getRenderViewEntity().getEntityBoundingBox().addCoord(vec4.xCoord * d0, vec4.yCoord * d0, vec4.zCoord * d0).expand((double)f1, (double)f1, (double)f1));
            double d3 = d2;
            for (final Object o : list) {
                final Entity entity = (Entity)o;
                if (entity.canBeCollidedWith()) {
                    final float ex = (float)(entity.getCollisionBorderSize() * exp(entity));
                    final AxisAlignedBB ax = entity.getEntityBoundingBox().expand((double)ex, (double)ex, (double)ex);
                    final MovingObjectPosition mop = ax.calculateIntercept(vec3, vec5);
                    if (ax.isVecInside(vec3)) {
                        if (0.0 >= d3 && d3 != 0.0) {
                            continue;
                        }
                        pE = entity;
                        vec6 = ((mop == null) ? vec3 : mop.hitVec);
                        d3 = 0.0;
                    }
                    else {
                        if (mop == null) {
                            continue;
                        }
                        final double d4 = vec3.distanceTo(mop.hitVec);
                        if (d4 >= d3 && d3 != 0.0) {
                            continue;
                        }
                        if (entity == mc.getRenderViewEntity().ridingEntity && !entity.canRiderInteract()) {
                            if (d3 != 0.0) {
                                continue;
                            }
                            pE = entity;
                            vec6 = mop.hitVec;
                        }
                        else {
                            pE = entity;
                            vec6 = mop.hitVec;
                            d3 = d4;
                        }
                    }
                }
            }
            if (pE != null && (d3 < d2 || mv == null)) {
                mv = new MovingObjectPosition(pE, vec6);
                if (pE instanceof EntityLivingBase || pE instanceof EntityItemFrame) {
                    mc.pointedEntity = pE;
                }
            }
        }
    }
    
    private void rh(final Entity e, final Color c) {
        if (e instanceof EntityLivingBase) {
            final double x = e.lastTickPosX + (e.posX - e.lastTickPosX) * ReflectUtil.getTimer().renderPartialTicks - mc.getRenderManager().viewerPosX;
            final double y = e.lastTickPosY + (e.posY - e.lastTickPosY) * ReflectUtil.getTimer().renderPartialTicks - mc.getRenderManager().viewerPosY;
            final double z = e.lastTickPosZ + (e.posZ - e.lastTickPosZ) * ReflectUtil.getTimer().renderPartialTicks - mc.getRenderManager().viewerPosZ;
            final float ex = (float)(e.getCollisionBorderSize() * a.getInput());
            final AxisAlignedBB bbox = e.getEntityBoundingBox().expand((double)ex, (double)ex, (double)ex);
            final AxisAlignedBB axis = new AxisAlignedBB(bbox.minX - e.posX + x, bbox.minY - e.posY + y, bbox.minZ - e.posZ + z, bbox.maxX - e.posX + x, bbox.maxY - e.posY + y, bbox.maxZ - e.posZ + z);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(3042);
            GL11.glDisable(3553);
            GL11.glDisable(2929);
            GL11.glDepthMask(false);
            GL11.glLineWidth(2.0f);
            GL11.glColor3d((double)c.getRed(), (double)c.getGreen(), (double)c.getBlue());
            RenderGlobal.drawSelectionBoundingBox(axis);
            GL11.glEnable(3553);
            GL11.glEnable(2929);
            GL11.glDepthMask(true);
            GL11.glDisable(3042);
        }
    }
}
