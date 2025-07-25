package keystrokesmod.client.module.modules.combat;

import java.util.concurrent.ThreadLocalRandom;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.modules.world.AntiBot;
import keystrokesmod.client.module.setting.impl.ComboSetting;
import keystrokesmod.client.module.setting.impl.DoubleSliderSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.utils.Clock;
import keystrokesmod.client.utils.Utils;
import keystrokesmod.client.utils.Utils.Modes.SprintResetTimings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@ModuleInfo(name = "BlockHit", category = Category.Combat)
public class BlockHit extends ClientModule {
    public TickSetting onlyPlayers = new TickSetting("Only combo players", this, true);
    public TickSetting onRightMBHold = new TickSetting("When holding down rmb", this, true);
    public DoubleSliderSetting waitMs = new DoubleSliderSetting("Action Time (MS)", this, 110.0, 150.0, 1.0, 500.0, 1.0);
    public DoubleSliderSetting hitPer = new DoubleSliderSetting("Once every ... hits", this, 1.0, 1.0, 1.0, 10.0, 1.0);
    public DoubleSliderSetting postDelay = new DoubleSliderSetting("Post Delay (MS)", this, 10.0, 40.0, 0.0, 500.0, 1.0);
    public SliderSetting chance = new SliderSetting("Chance %", this, 100.0, 0.0, 100.0, 1.0);
    public SliderSetting range = new SliderSetting("Range: ", this, 3.0, 1.0, 6.0, 0.05);
    private final ComboSetting mode = new ComboSetting("Mode", this, SprintResetTimings.PRE, SprintResetTimings.values());
    public boolean executingAction;
    public boolean hitCoolDown;
    public boolean alreadyHit;
    public boolean safeGuard;
    public int hitTimeout;
    public int hitsWaited;
    private Clock actionTimer = new Clock(0);
    private Clock postDelayTimer = new Clock(0);
    private boolean waitingForPostDelay;

    @SubscribeEvent
    public void onTick(final TickEvent.RenderTickEvent e) {
        if (!Utils.Player.isPlayerInGame()) {
            return;
        }
    	AntiBot bot = (AntiBot) Raven.moduleManager.getModuleByClazz(AntiBot.class);

        if (onRightMBHold.isToggled() && !Utils.Player.tryingToCombo()) {
            if (!safeGuard || (Utils.Player.isPlayerHoldingWeapon() && Mouse.isButtonDown(0))) {
                safeGuard = true;
                finishCombo();
            }
            return;
        }
        if (this.waitingForPostDelay) {
            if (this.postDelayTimer.hasFinished()) {
                executingAction = true;
                startCombo();
                this.waitingForPostDelay = false;
                if (safeGuard) {
                    safeGuard = false;
                }
                this.actionTimer.start();
            }
            return;
        }
        if (!executingAction) {
            if (onRightMBHold.isToggled() && Utils.Player.tryingToCombo()) {
                if (mc.objectMouseOver == null || mc.objectMouseOver.entityHit == null) {
                    if (!safeGuard || (Utils.Player.isPlayerHoldingWeapon() && Mouse.isButtonDown(0))) {
                        safeGuard = true;
                        finishCombo();
                    }
                    return;
                }
                final Entity target = mc.objectMouseOver.entityHit;
                
                if (target.isDead) {
                    if (!safeGuard || (Utils.Player.isPlayerHoldingWeapon() && Mouse.isButtonDown(0))) {
                        safeGuard = true;
                        finishCombo();
                    }
                    return;
                }
            }
            if (mc.objectMouseOver != null && mc.objectMouseOver.entityHit instanceof Entity && Mouse.isButtonDown(0)) {
                final Entity target = mc.objectMouseOver.entityHit;
                if (target.isDead) {
                    if (onRightMBHold.isToggled() && Mouse.isButtonDown(1) && Mouse.isButtonDown(0) && (!safeGuard || (Utils.Player.isPlayerHoldingWeapon() && Mouse.isButtonDown(0)))) {
                        safeGuard = true;
                        finishCombo();
                    }
                    return;
                }
                if (mc.thePlayer.getDistanceToEntity(target) <= range.getInput()) {
                    if ((target.hurtResistantTime >= 10 && Utils.Modes.SprintResetTimings.values()[(int)mode.getIndex() - 1] == Utils.Modes.SprintResetTimings.POST) || (target.hurtResistantTime <= 10 && Utils.Modes.SprintResetTimings.values()[(int)mode.getIndex() - 1] == Utils.Modes.SprintResetTimings.PRE)) {
                        if (onlyPlayers.isToggled() && !(target instanceof EntityPlayer)) {
                            return;
                        }
                        if (bot.bot(target)) {
                            return;
                        }
                        if (hitCoolDown && !alreadyHit) {
                            ++hitsWaited;
                            if (hitsWaited < hitTimeout) {
                                alreadyHit = true;
                                return;
                            }
                            hitCoolDown = false;
                            hitsWaited = 0;
                        }
                        if (chance.getInput() != 100.0 && Math.random() > chance.getInput() / 100.0) {
                            return;
                        }
                        if (!alreadyHit) {
                            this.guiUpdate();
                            if (hitPer.getInputMin() == hitPer.getInputMax()) {
                                hitTimeout = (int)hitPer.getInputMin();
                            }
                            else {
                                hitTimeout = ThreadLocalRandom.current().nextInt((int)hitPer.getInputMin(), (int)hitPer.getInputMax());
                            }
                            hitCoolDown = true;
                            hitsWaited = 0;
                            this.actionTimer.setCooldown((long)ThreadLocalRandom.current().nextDouble(waitMs.getInputMin(), waitMs.getInputMax() + 0.01));
                            if (postDelay.getInputMax() != 0.0) {
                                this.postDelayTimer.setCooldown((long)ThreadLocalRandom.current().nextDouble(postDelay.getInputMin(), postDelay.getInputMax() + 0.01));
                                this.postDelayTimer.start();
                                this.waitingForPostDelay = true;
                            }
                            else {
                                executingAction = true;
                                startCombo();
                                this.actionTimer.start();
                                alreadyHit = true;
                                if (safeGuard) {
                                    safeGuard = false;
                                }
                            }
                            alreadyHit = true;
                        }
                    }
                    else {
                        if (alreadyHit) {
                            alreadyHit = false;
                        }
                        if (safeGuard) {
                            safeGuard = false;
                        }
                    }
                }
            }
            return;
        }
        if (this.actionTimer.hasFinished()) {
            executingAction = false;
            finishCombo();
        }
    }
    
    private void finishCombo() {
        final int key = mc.gameSettings.keyBindUseItem.getKeyCode();
        KeyBinding.setKeyBindState(key, false);
        Utils.Client.setMouseButtonState(1, false);
    }
    
    private void startCombo() {
        if (Keyboard.isKeyDown(mc.gameSettings.keyBindForward.getKeyCode())) {
            final int key = mc.gameSettings.keyBindUseItem.getKeyCode();
            KeyBinding.setKeyBindState(key, true);
            KeyBinding.onTick(key);
            Utils.Client.setMouseButtonState(1, true);
        }
    }
}
