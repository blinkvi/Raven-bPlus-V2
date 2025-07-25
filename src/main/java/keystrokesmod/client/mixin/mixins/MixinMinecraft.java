package keystrokesmod.client.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import keystrokesmod.client.events.TickEvent;
import keystrokesmod.client.main.Raven;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "runTick", at = @At("HEAD"))
    private void onRunTick(CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new TickEvent());
    }
    
	@Inject(method = "startGame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;ingameGUI:Lnet/minecraft/client/gui/GuiIngame;", shift = At.Shift.AFTER))
	private void injectStartGame(CallbackInfo ci) {
		Raven.init();
	}

}
