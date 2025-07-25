package keystrokesmod.client.module.modules.macros;

import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.Utils;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

@ModuleInfo(name = "Blocks", category = Category.Macros)
public class Blocks extends ClientModule {
    private final TickSetting preferSlot = new TickSetting("Prefer a slot", this, false);
    private final SliderSetting hotbarSlotPreference = new SliderSetting("Prefer wich slot", this, 9.0, 1.0, 9.0, 1.0);

    @Override
    public void onEnable() {
        if (!Utils.Player.isPlayerInGame()) {
            return;
        }
        if (this.preferSlot.isToggled()) {
            final int preferedSlot = (int)this.hotbarSlotPreference.getInput() - 1;
            final ItemStack itemInSlot = Blocks.mc.thePlayer.inventory.getStackInSlot(preferedSlot);
            if (itemInSlot != null && itemInSlot.getItem() instanceof ItemBlock) {
                Blocks.mc.thePlayer.inventory.currentItem = preferedSlot;
                this.disable();
                return;
            }
        }
        int slot = 0;
        while (slot <= 8) {
            final ItemStack itemInSlot = Blocks.mc.thePlayer.inventory.getStackInSlot(slot);
            if (itemInSlot != null && itemInSlot.getItem() instanceof ItemBlock && (((ItemBlock)itemInSlot.getItem()).getBlock().isFullBlock() || ((ItemBlock)itemInSlot.getItem()).getBlock().isFullCube())) {
                if (Blocks.mc.thePlayer.inventory.currentItem != slot) {
                    Blocks.mc.thePlayer.inventory.currentItem = slot;
                    this.disable();
                }
                return;
            }
            else {
                ++slot;
            }
        }
        this.onDisable();
        this.disable();
    }
}
