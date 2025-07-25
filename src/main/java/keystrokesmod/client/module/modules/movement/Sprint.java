package keystrokesmod.client.module.modules.movement;

import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@ModuleInfo(name = "Sprint", category = Category.Movement)
public class Sprint extends ClientModule {
    private final TickSetting o = new TickSetting("OmniSprint", this, false);
    public final TickSetting ignoreBlindness = new TickSetting("Ignore Blindness", this, true);
    public final TickSetting multiDir = new TickSetting("Multi Direction", this, false);

    @SubscribeEvent
    public void onTick(final TickEvent.PlayerTickEvent e) {
        if (!Utils.Player.isPlayerInGame() || !mc.inGameHasFocus) return;

        if (o.isToggled()) {
            if (Utils.Player.isMoving() && mc.thePlayer.getFoodStats().getFoodLevel() > 6) {
                mc.thePlayer.setSprinting(true);
            }
        } else {
            KeyBinding.setKeyBindState(mc.gameSettings.keyBindSprint.getKeyCode(), true);
        }
    }
}
