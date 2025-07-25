package keystrokesmod.client.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraftforge.fml.client.SplashProgress;

@Mixin(value = SplashProgress.class, remap = false)
public class MixinSplashProgress {

    @Inject(method = "getString", at = @At("RETURN"), cancellable = true)
    private static void onGetString(String firstArg, String secondArg, CallbackInfoReturnable<String> cir) {
        if (secondArg != null && secondArg.contains("fml:textures/gui/forge.gif")) {
            cir.setReturnValue(secondArg.replace("fml:textures/gui/forge.gif", "keystrokes:raven_loading.png"));
        }
    }
}