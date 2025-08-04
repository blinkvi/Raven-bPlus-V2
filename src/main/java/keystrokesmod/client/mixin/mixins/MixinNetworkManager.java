package keystrokesmod.client.mixin.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import io.netty.channel.ChannelHandlerContext;
import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.utils.PacketUtil;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;

@Mixin(NetworkManager.class)
public class MixinNetworkManager {
    @Inject(method = "sendPacket", at = @At("HEAD"), cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        for (ClientModule m : Raven.moduleManager.getModules()) {
            if (m.isEnabled() && m.onSend(packet)) {
                ci.cancel();
            }
        }
    }

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    private void onReceivePacket(ChannelHandlerContext ctx, Packet<?> packet, CallbackInfo ci) {
        for (ClientModule m : Raven.moduleManager.getModules()) {
        	
        	if(!PacketUtil.handleSendPacket(packet)) {
                if (packet instanceof Packet && m.isEnabled() && m.onReceive(packet)) {
                    ci.cancel();
                }	
        	}
        }
    }
}
