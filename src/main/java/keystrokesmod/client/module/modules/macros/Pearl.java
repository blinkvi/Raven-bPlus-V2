package keystrokesmod.client.module.modules.macros;

import java.util.ArrayList;

import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.Utils;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemStack;

@ModuleInfo(name = "Pearl", category = Category.Macros)
public class Pearl extends ClientModule {
    private final TickSetting preferSlot = new TickSetting("Prefer a slot", this, false);
    private final SliderSetting hotbarSlotPreference = new SliderSetting("Preferred slot", this, 6.0, 1.0, 9.0, 1.0);
    public static final ArrayList<KeyBinding> changedKeybinds = new ArrayList<>();

    @Override
    public void onEnable() {
        if (!Utils.Player.isPlayerInGame()) {
            this.disable();
            return;
        }

        int slot = -1;

        if (preferSlot.isToggled()) {
            int preferredSlot = (int) hotbarSlotPreference.getInput() - 1;
            if (isEnderPearl(preferredSlot)) {
                slot = preferredSlot;
            }
        } else {
            for (int i = 0; i <= 8; i++) {
                if (isEnderPearl(i)) {
                    slot = i;
                    break;
                }
            }
        }

        if (slot != -1 && mc.thePlayer.inventory.currentItem != slot) {
            mc.thePlayer.inventory.currentItem = slot;
        }
        this.onDisable();
        this.disable();
    }

    private boolean isEnderPearl(int slot) {
        ItemStack item = mc.thePlayer.inventory.getStackInSlot(slot);
        return item != null && "ender pearl".equalsIgnoreCase(item.getDisplayName());
    }
}
