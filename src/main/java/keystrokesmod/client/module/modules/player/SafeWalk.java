package keystrokesmod.client.module.modules.player;

import java.awt.Color;

import org.lwjgl.input.Keyboard;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.DescriptionSetting;
import keystrokesmod.client.module.setting.impl.DoubleSliderSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.Clock;
import keystrokesmod.client.utils.Utils;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@ModuleInfo(name = "SafeWalk", category = Category.Player)
public class SafeWalk extends ClientModule {
	public final TickSetting doShift = new TickSetting("Shift", this, false);
	private final TickSetting shiftOnJump = new TickSetting("Shift during jumps", this, false);
	private final DoubleSliderSetting shiftTime = new DoubleSliderSetting("Shift time: (s)", this, 140.0, 200.0, 0.0, 280.0, 5.0);
	private final TickSetting onHold = new TickSetting("On shift hold", this, false);
	public final TickSetting blocksOnly = new TickSetting("Blocks only", this, true);
	private final TickSetting showBlockAmount = new TickSetting("Show amount of blocks", this, true);
	private final SliderSetting blockShowMode = new SliderSetting("Block display info:", this, 2.0, 1.0, 2.0, 1.0);
	private final DescriptionSetting blockShowModeDesc = new DescriptionSetting("Mode:", this);
	public final TickSetting lookDown = new TickSetting("Only when looking down", this, true);
	public final TickSetting onlyBackwars = new TickSetting("Only backwards", this, true);
	public final DoubleSliderSetting pitchRange = new DoubleSliderSetting("Pitch min range:", this, 70.0, 85.0, 0.0, 90.0, 1.0);

    private static boolean shouldBridge = false;
    private static boolean isShifting = false;
    private boolean allowedShift;
    private Clock shiftTimer = new Clock(0L);

    @Override
    public void onDisable() {
        if (doShift.isToggled() && Utils.Player.playerOverAir()) {
            this.setShift(false);
        }
        shouldBridge = false;
        isShifting = false;
    }
    
    @Override
    public void guiUpdate() {
        blockShowModeDesc.setDesc("Mode: " + BlockAmountInfo.values()[(int)blockShowMode.getInput() - 1]);
    }
    
    @SubscribeEvent
    public void p(final TickEvent.PlayerTickEvent e) {
        if (!Utils.Client.currentScreenMinecraft()) {
            return;
        }
        if (!Utils.Player.isPlayerInGame()) {
            return;
        }
        final boolean shiftTimeSettingActive = shiftTime.getInputMax() > 0.0;
        if (doShift.isToggled()) {
            if (lookDown.isToggled() && (mc.thePlayer.rotationPitch < pitchRange.getInputMin() || mc.thePlayer.rotationPitch > pitchRange.getInputMax())) {
                shouldBridge = false;
                if (Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
                    this.setShift(true);
                }
                return;
            }
            if (onHold.isToggled() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
                shouldBridge = false;
                return;
            }
            if (blocksOnly.isToggled()) {
                final ItemStack i = mc.thePlayer.getHeldItem();
                if (i == null || !(i.getItem() instanceof ItemBlock)) {
                    if (isShifting) {
                        this.setShift(isShifting = false);
                    }
                    return;
                }
            }
            if (mc.thePlayer.onGround) {
                if (Utils.Player.playerOverAir()) {
                    if (shiftTimeSettingActive) {
                        this.shiftTimer.setCooldown(Utils.Java.randomInt(shiftTime.getInputMin(), shiftTime.getInputMax() + 0.1));
                        this.shiftTimer.start();
                    }
                    this.setShift(isShifting = true);
                    shouldBridge = true;
                }
                else if (mc.thePlayer.isSneaking() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && onHold.isToggled()) {
                    isShifting = false;
                    this.setShift(shouldBridge = false);
                }
                else if (onHold.isToggled() && !Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode())) {
                    isShifting = false;
                    this.setShift(shouldBridge = false);
                }
                else if (mc.thePlayer.isSneaking() && Keyboard.isKeyDown(mc.gameSettings.keyBindSneak.getKeyCode()) && onHold.isToggled() && (!shiftTimeSettingActive || this.shiftTimer.hasFinished())) {
                    this.setShift(isShifting = false);
                    shouldBridge = true;
                }
                else if (mc.thePlayer.isSneaking() && !onHold.isToggled() && (!shiftTimeSettingActive || this.shiftTimer.hasFinished())) {
                    this.setShift(isShifting = false);
                    shouldBridge = true;
                }
            }
            else if (shouldBridge && mc.thePlayer.capabilities.isFlying) {
                this.setShift(false);
                shouldBridge = false;
            }
            else if (shouldBridge && Utils.Player.playerOverAir() && shiftOnJump.isToggled()) {
                this.setShift(isShifting = true);
            }
            else {
                this.setShift(isShifting = false);
            }
        }
    }
    
    @SubscribeEvent
    public void r(final TickEvent.RenderTickEvent e) {
        if (!showBlockAmount.isToggled() || !Utils.Player.isPlayerInGame()) {
            return;
        }
        if (e.phase == TickEvent.Phase.END && mc.currentScreen == null && shouldBridge) {
            final ScaledResolution res = new ScaledResolution(mc);
            int totalBlocks = 0;
            if (BlockAmountInfo.values()[(int)blockShowMode.getInput() - 1] == BlockAmountInfo.BLOCKS_IN_CURRENT_STACK) {
                totalBlocks = Utils.Player.getBlockAmountInCurrentStack(mc.thePlayer.inventory.currentItem);
            }
            else {
                for (int slot = 0; slot < 36; ++slot) {
                    totalBlocks += Utils.Player.getBlockAmountInCurrentStack(slot);
                }
            }
            if (totalBlocks <= 0) {
                return;
            }
            int rgb;
            if (totalBlocks < 16.0) {
                rgb = Color.red.getRGB();
            }
            else if (totalBlocks < 32.0) {
                rgb = Color.orange.getRGB();
            }
            else if (totalBlocks < 128.0) {
                rgb = Color.yellow.getRGB();
            }
            else if (totalBlocks > 128.0) {
                rgb = Color.green.getRGB();
            }
            else {
                rgb = Color.black.getRGB();
            }
            final String t = totalBlocks + " blocks";
            final int x = res.getScaledWidth() / 2 - mc.fontRendererObj.getStringWidth(t) / 2;
            int y;
            if (Raven.debugger) {
                y = res.getScaledHeight() / 2 + 17 + mc.fontRendererObj.FONT_HEIGHT;
            }
            else {
                y = res.getScaledHeight() / 2 + 15;
            }
            mc.fontRendererObj.drawString(t, (float)x, (float)y, rgb, false);
        }
    }
    
    private void setShift(final boolean sh) {
        KeyBinding.setKeyBindState(mc.gameSettings.keyBindSneak.getKeyCode(), sh);
    }

    public enum BlockAmountInfo
    {
        BLOCKS_IN_TOTAL, 
        BLOCKS_IN_CURRENT_STACK;
    }
}
