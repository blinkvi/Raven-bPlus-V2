package keystrokesmod.client.utils;

import keystrokesmod.client.clickgui.raven.Terminal;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ChatHelper implements IMinecraft {
    private static boolean e;
    private static long s;
    
    @SubscribeEvent
    public void onChatMessageReceived(final ClientChatReceivedEvent event) {
        if (ChatHelper.e && Utils.Player.isPlayerInGame() && Utils.Java.str(event.message.getUnformattedText()).startsWith("Unknown")) {
            event.setCanceled(true);
            ChatHelper.e = false;
            this.getPing();
        }
    }
    
    public static void checkPing() {
        Terminal.print("Checking...");
        if (ChatHelper.e) {
            Terminal.print("Please wait.");
        }
        else {
            mc.thePlayer.sendChatMessage("/...");
            ChatHelper.e = true;
            ChatHelper.s = System.currentTimeMillis();
        }
    }
    
    private void getPing() {
        int ping = (int)(System.currentTimeMillis() - ChatHelper.s) - 20;
        if (ping < 0) {
            ping = 0;
        }
        Terminal.print("Your ping: " + ping + "ms");
        reset();
    }
    
    public static void reset() {
        ChatHelper.e = false;
        ChatHelper.s = 0L;
    }
    
    static {
        ChatHelper.e = false;
        ChatHelper.s = 0L;
    }
}
