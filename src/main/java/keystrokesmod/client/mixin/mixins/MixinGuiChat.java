package keystrokesmod.client.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.ClientModule;
import net.minecraft.client.gui.GuiChat;

@Mixin(GuiChat.class)
public class MixinGuiChat {

    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        for (ClientModule m : Raven.moduleManager.getModules()) {
            if (m.isEnabled()) {
                m.onDrag(mouseX, mouseY, partialTicks);
            }
        }
    }
}