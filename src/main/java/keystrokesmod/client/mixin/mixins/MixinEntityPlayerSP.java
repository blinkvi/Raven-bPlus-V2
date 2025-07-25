package keystrokesmod.client.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import com.mojang.authlib.GameProfile;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.modules.movement.NoSlow;
import keystrokesmod.client.module.modules.movement.Sprint;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.potion.Potion;
import net.minecraft.util.MovementInput;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

    public MixinEntityPlayerSP(World p_i45074_1_, GameProfile p_i45074_2_) {
        super(p_i45074_1_, p_i45074_2_);
    }

    @Override
	@Shadow
    public abstract void setSprinting(boolean p_setSprinting_1_);

    @Shadow
    protected int sprintToggleTimer;
    
    @Shadow
    public float prevTimeInPortal;
    
    @Shadow
    public float timeInPortal;
    
    @Shadow
    protected Minecraft mc;
    
    @Shadow
    public MovementInput movementInput;
    
    @Shadow
    public int sprintingTicksLeft;

    @Override
	@Shadow
    protected abstract boolean pushOutOfBlocks(double p_pushOutOfBlocks_1_, double p_pushOutOfBlocks_3_, double p_pushOutOfBlocks_5_);

    @Override
	@Shadow
    public abstract void sendPlayerAbilities();

    @Shadow
    protected abstract boolean isCurrentViewEntity();

    @Shadow
    public abstract boolean isRidingHorse();

    @Shadow
    private int horseJumpPowerCounter;
    @Shadow
    private float horseJumpPower;

    @Shadow
    protected abstract void sendHorseJump();

	@Overwrite
    public void onLivingUpdate() {
        if (sprintingTicksLeft > 0) {
            --sprintingTicksLeft;
            if (sprintingTicksLeft == 0)
				setSprinting(false);
        }

        if (sprintToggleTimer > 0)
			--sprintToggleTimer;

        prevTimeInPortal = timeInPortal;
        if (inPortal) {
            if ((mc.currentScreen != null) && !mc.currentScreen.doesGuiPauseGame())
				mc.displayGuiScreen(null);

            if (timeInPortal == 0.0F)
				mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("portal.trigger"),
                        (rand.nextFloat() * 0.4F) + 0.8F));

            timeInPortal += 0.0125F;
            if (timeInPortal >= 1.0F)
				timeInPortal = 1.0F;

            inPortal = false;
        } else if (isPotionActive(Potion.confusion)
                && (getActivePotionEffect(Potion.confusion).getDuration() > 60)) {
            timeInPortal += 0.006666667F;
            if (timeInPortal > 1.0F)
				timeInPortal = 1.0F;
        } else {
            if (timeInPortal > 0.0F)
				timeInPortal -= 0.05F;

            if (timeInPortal < 0.0F)
				timeInPortal = 0.0F;
        }

        if (timeUntilPortal > 0)
			--timeUntilPortal;

        NoSlow noSlow = (NoSlow) Raven.moduleManager.getModuleByClazz(NoSlow.class);
        Sprint sprint = (Sprint) Raven.moduleManager.getModuleByClazz(Sprint.class);

        boolean flag = movementInput.jump;
        boolean flag1 = movementInput.sneak;
        float f = 0.8F;
        boolean flag2 = movementInput.moveForward >= f;
        movementInput.updatePlayerMoveState();
        if (isUsingItem() && !isRiding()) {

            MovementInput var10000 = movementInput;

            if (noSlow.isEnabled()) {
                float slowdown = (float) ((100 - noSlow.b.getInput()) / 100F);
                var10000.moveStrafe *= slowdown;
                var10000.moveForward *= slowdown;
            } else {
                var10000.moveStrafe *= 0.2F;
                var10000.moveForward *= 0.2F;
                sprintToggleTimer = 0;
            }
        }

        pushOutOfBlocks(posX - ((double) width * 0.35D), getEntityBoundingBox().minY + 0.5D,
                posZ + ((double) width * 0.35D));
        pushOutOfBlocks(posX - ((double) width * 0.35D), getEntityBoundingBox().minY + 0.5D,
                posZ - ((double) width * 0.35D));
        pushOutOfBlocks(posX + ((double) width * 0.35D), getEntityBoundingBox().minY + 0.5D,
                posZ - ((double) width * 0.35D));
        pushOutOfBlocks(posX + ((double) width * 0.35D), getEntityBoundingBox().minY + 0.5D,
                posZ + ((double) width * 0.35D));
        boolean flag3 = ((float) getFoodStats().getFoodLevel() > 6.0F) || capabilities.allowFlying;
        if (onGround && !flag1 && !flag2
                && ((movementInput.moveForward >= f) || (sprint.isEnabled() && sprint.multiDir.isToggled()
                        && ((movementInput.moveForward != 0) || (movementInput.moveStrafe != 0))))
                && !isSprinting() && flag3 && (!isUsingItem() || noSlow.isEnabled())
                && (!isPotionActive(Potion.blindness)
                        || (sprint.isEnabled() && sprint.ignoreBlindness.isToggled())))
			if ((sprintToggleTimer <= 0) && !mc.gameSettings.keyBindSprint.isKeyDown())
				sprintToggleTimer = 7;
			else
				setSprinting(true);

        if (!isSprinting()
                && ((movementInput.moveForward >= f) || (sprint.isEnabled() && sprint.multiDir.isToggled()
                        && ((movementInput.moveForward != 0) || (movementInput.moveStrafe != 0))))
                && flag3 && (!isUsingItem() || noSlow.isEnabled())
                && (!isPotionActive(Potion.blindness)
                        || (sprint.isEnabled() && sprint.ignoreBlindness.isToggled()))
                && mc.gameSettings.keyBindSprint.isKeyDown())
			setSprinting(true);

        if (isSprinting() && (((sprint.isEnabled() && sprint.multiDir.isToggled())
                ? !((movementInput.moveForward != 0) || (movementInput.moveStrafe != 0))
                : movementInput.moveForward < f) || isCollidedHorizontally || !flag3))
			setSprinting(false);

        if (capabilities.allowFlying)
			if (mc.playerController.isSpectatorMode()) {
                if (!capabilities.isFlying) {
                    capabilities.isFlying = true;
                    sendPlayerAbilities();
                }
            } else if (!flag && movementInput.jump)
				if (flyToggleTimer == 0)
					flyToggleTimer = 7;
				else {
                    capabilities.isFlying = !capabilities.isFlying;
                    sendPlayerAbilities();
                    flyToggleTimer = 0;
                }

        if (capabilities.isFlying && isCurrentViewEntity()) {
            if (movementInput.sneak)
				motionY -= capabilities.getFlySpeed() * 3.0F;

            if (movementInput.jump)
				motionY += capabilities.getFlySpeed() * 3.0F;
        }

        if (isRidingHorse()) {
            if (horseJumpPowerCounter < 0) {
                ++horseJumpPowerCounter;
                if (horseJumpPowerCounter == 0)
					horseJumpPower = 0.0F;
            }

            if (flag && !movementInput.jump) {
                horseJumpPowerCounter = -10;
                sendHorseJump();
            } else if (!flag && movementInput.jump) {
                horseJumpPowerCounter = 0;
                horseJumpPower = 0.0F;
            } else if (flag) {
                ++horseJumpPowerCounter;
                if (horseJumpPowerCounter < 10)
					horseJumpPower = (float) horseJumpPowerCounter * 0.1F;
				else
					horseJumpPower = 0.8F + ((2.0F / (float) (horseJumpPowerCounter - 9)) * 0.1F);
            }
        } else
			horseJumpPower = 0.0F;

        super.onLivingUpdate();
        if (onGround && capabilities.isFlying && !mc.playerController.isSpectatorMode()) {
            capabilities.isFlying = false;
            sendPlayerAbilities();
        }

    }
}
