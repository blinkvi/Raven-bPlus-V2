package keystrokesmod.client.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.ClientModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

@Mixin(EntityPlayer.class)
public class MixinEntityPlayer {
    @Inject(method = "attackTargetEntityWithCurrentItem", at = @At("TAIL"))
    private void onAttackTargetEntityWithCurrentItem(Entity target, CallbackInfo ci) {
        for (ClientModule m : Raven.moduleManager.getModules()) {
            if (m.isEnabled()) {
                m.onAttackTargetEntityWithCurrentItem(target);
            }
        }
    }
}
