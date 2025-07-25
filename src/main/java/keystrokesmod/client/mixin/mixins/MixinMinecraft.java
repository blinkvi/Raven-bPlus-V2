package keystrokesmod.client.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.ClientModule;
import net.minecraft.client.Minecraft;

@Mixin(Minecraft.class)
public class MixinMinecraft {
    @Inject(method = "runTick", at = @At("HEAD"))
    private void onRunTick(CallbackInfo ci) {
        for (ClientModule m : Raven.moduleManager.getModules()) {
            if (m.isEnabled()) {
                m.onTick();
            }
        }
    }
    
	@Inject(method = "startGame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;ingameGUI:Lnet/minecraft/client/gui/GuiIngame;", shift = At.Shift.AFTER))
	private void injectStartGame(CallbackInfo ci) {
		Raven.init();
	}

}
