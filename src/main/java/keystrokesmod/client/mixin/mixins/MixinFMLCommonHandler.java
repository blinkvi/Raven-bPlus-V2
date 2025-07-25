package keystrokesmod.client.mixin.mixins;

import java.util.List;
import java.util.Map;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import com.google.common.collect.Lists;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.modules.client.ClientNameSpoof;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Loader;

@Mixin(value = FMLCommonHandler.class, remap = false)
public class MixinFMLCommonHandler {

    @Inject(method = "getModName", at = @At("HEAD"), remap = false, cancellable = true)
    public void injectGetModName(CallbackInfoReturnable<String> cir) {
        ClientModule spoof = Raven.moduleManager.getModuleByClazz(ClientNameSpoof.class);

        if (spoof != null && spoof.isEnabled()) {
            cir.setReturnValue(ClientNameSpoof.newName);
            return;
        }

        List<String> modNames = Lists.newArrayListWithExpectedSize(3);
        modNames.add("fml");
        modNames.add("forge");

        Map<String, String> branding = Loader.instance().getFMLBrandingProperties();
        if (branding.containsKey("snooperbranding")) {
            modNames.add(branding.get("snooperbranding"));
        }

        cir.setReturnValue(String.join(",", modNames));
    }
}