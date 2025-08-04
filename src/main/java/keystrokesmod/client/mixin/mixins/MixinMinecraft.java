package keystrokesmod.client.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import keystrokesmod.client.events.ClientAttackEvent;
import keystrokesmod.client.events.TickEvent;
import keystrokesmod.client.main.Raven;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.common.MinecraftForge;

@Mixin(Minecraft.class)
public class MixinMinecraft {
	
	@Shadow
	public MovingObjectPosition objectMouseOver;
	
    @Inject(method = "runTick", at = @At("HEAD"))
    private void onRunTick(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new TickEvent());
    }
    
	@Inject(method = "startGame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;ingameGUI:Lnet/minecraft/client/gui/GuiIngame;", shift = At.Shift.AFTER))
	private void injectStartGame(CallbackInfo ci) {
		Raven.init();
	}

	@Inject(method = "clickMouse", at = @At("HEAD"))
	public void clickMouse(CallbackInfo ci) {
		if (this.objectMouseOver != null && this.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY && objectMouseOver.entityHit instanceof EntityLivingBase) {
			final ClientAttackEvent event = new ClientAttackEvent((EntityLivingBase) this.objectMouseOver.entityHit);
			MinecraftForge.EVENT_BUS.post(event);
		}
	}
}
