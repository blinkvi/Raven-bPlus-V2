package keystrokesmod.client.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.ClientModule;
import net.minecraft.client.gui.FontRenderer;

@Mixin(FontRenderer.class)
public class MixinFontRenderer {

	@ModifyVariable(method = "renderStringAtPos", at = @At("HEAD"), index = 1, argsOnly = true)
	private String modifyRenderStringAtPosText(String text) {
		return applyModuleModifications(text);
	}

	@ModifyVariable(method = "getStringWidth", at = @At("HEAD"), index = 1, argsOnly = true)
	private String modifyGetStringWidthText(String text) {
		return applyModuleModifications(text);
	}

	private String applyModuleModifications(String text) {
		for (ClientModule m : Raven.moduleManager.getModules()) {
			if (m.isEnabled()) {
				text = m.getUnformattedTextForChat(text);
			}
		}
		return text;
	}
}