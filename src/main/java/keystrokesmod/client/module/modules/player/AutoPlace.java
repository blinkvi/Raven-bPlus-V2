package keystrokesmod.client.module.modules.player;

import org.lwjgl.input.Mouse;

import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.module.setting.impl.DescriptionSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.utils.ReflectUtil;
import keystrokesmod.client.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "AutoPlace", category = Category.Player)
public class AutoPlace extends ClientModule {
    private final DescriptionSetting ds = new DescriptionSetting("FD: FPS/80", this);
    private final SliderSetting c = new SliderSetting("Frame delay", this, 8.0, 0.0, 30.0, 1.0);
    private final TickSetting a = new TickSetting("Hold right", this, true);
    private double lfd = 0.0;
    private final int d = 25;
    private long l = 0;
    private int f = 0;
    private MovingObjectPosition lm = null;
    private BlockPos lp = null;
    
    @Override
    public void guiUpdate() {
        if (this.lfd != c.getInput()) {
            this.rv();
        }
        this.lfd = c.getInput();
    }
    
    @Override
    public void onDisable() {
        if (a.isToggled()) {
            this.rd(4);
        }
        this.rv();
    }
    
    @Override
    public void update() {
        final FastPlace fastPlace = (FastPlace) Raven.moduleManager.getModuleByClazz(FastPlace.class);
        if (a.isToggled() && Mouse.isButtonDown(1) && !mc.thePlayer.capabilities.isFlying && fastPlace != null && !fastPlace.isEnabled()) {
            final ItemStack i = mc.thePlayer.getHeldItem();
            if (i == null || !(i.getItem() instanceof ItemBlock)) {
                return;
            }
            this.rd((mc.thePlayer.motionY > 0.0) ? 1 : 1000);
        }
    }
    
    @SubscribeEvent
    public void bh(final DrawBlockHighlightEvent ev) {
        if (Utils.Player.isPlayerInGame() && mc.currentScreen == null && !mc.thePlayer.capabilities.isFlying) {
            final ItemStack i = mc.thePlayer.getHeldItem();
            if (i != null && i.getItem() instanceof ItemBlock) {
                final MovingObjectPosition m = mc.objectMouseOver;
                if (m != null && m.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK && m.sideHit != EnumFacing.UP && m.sideHit != EnumFacing.DOWN) {
                    if (this.lm != null && this.f < c.getInput()) {
                        ++this.f;
                    }
                    else {
                        this.lm = m;
                        final BlockPos pos = m.getBlockPos();
                        if (this.lp == null || pos.getX() != this.lp.getX() || pos.getY() != this.lp.getY() || pos.getZ() != this.lp.getZ()) {
                            final Block b = mc.theWorld.getBlockState(pos).getBlock();
                            if (b != null && b != Blocks.air && !(b instanceof BlockLiquid) && (!a.isToggled() || Mouse.isButtonDown(1))) {
                                final long n = System.currentTimeMillis();
                                if (n - this.l >= 25L) {
                                    this.l = n;
                                    if (mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, i, pos, m.sideHit, m.hitVec)) {
                                        Utils.Client.setMouseButtonState(1, true);
                                        mc.thePlayer.swingItem();
                                        mc.getItemRenderer().resetEquippedProgress();
                                        Utils.Client.setMouseButtonState(1, false);
                                        this.lp = pos;
                                        this.f = 0;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void rd(final int i) {
    	ReflectUtil.setRightClickDelayTimer(i);
    }
    
    private void rv() {
        this.lp = null;
        this.lm = null;
        this.f = 0;
    }
}
