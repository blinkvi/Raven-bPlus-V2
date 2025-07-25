package keystrokesmod.client.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import keystrokesmod.client.events.DragEvent;
import net.minecraft.client.gui.GuiChat;
import net.minecraftforge.common.MinecraftForge;

@Mixin(GuiChat.class)
public class MixinGuiChat {

    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void onDrawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new DragEvent(mouseX, mouseY, partialTicks));
    }
}