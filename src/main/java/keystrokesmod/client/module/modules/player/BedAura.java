package keystrokesmod.client.module.modules.player;

import java.util.Timer;
import java.util.TimerTask;

import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.utils.Utils;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;

@ModuleInfo(name = "BedAura", category = Category.Player)
public class BedAura extends ClientModule {
    private final SliderSetting r = new SliderSetting("Range", this, 5.0, 2.0, 10.0, 1.0);
    private Timer t;
    private BlockPos m = null;
    private final long per = 600L;

    @Override
    public void onEnable() {
        (this.t = new Timer()).scheduleAtFixedRate(this.t(), 0L, 600L);
    }
    
    @Override
    public void onDisable() {
        if (this.t != null) {
            this.t.cancel();
            this.t.purge();
            this.t = null;
        }
        this.m = null;
    }
    
    public TimerTask t() {
        return new TimerTask() {
            @Override
            public void run() {
                int y;
                for (int ra = y = (int)r.getInput(); y >= -ra; --y) {
                    for (int x = -ra; x <= ra; ++x) {
                        for (int z = -ra; z <= ra; ++z) {
                            if (Utils.Player.isPlayerInGame()) {
                                final BlockPos p = new BlockPos(mc.thePlayer.posX + x, mc.thePlayer.posY + y, mc.thePlayer.posZ + z);
                                final boolean bed = mc.theWorld.getBlockState(p).getBlock() == Blocks.bed;
                                if (BedAura.this.m == p) {
                                    if (!bed) {
                                    	BedAura.this.m = null;
                                    }
                                }
                                else if (bed) {
                                	BedAura.this.mi(p);
                                    BedAura.this.m = p;
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        };
    }
    
    private void mi(final BlockPos p) {
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, p, EnumFacing.NORTH));
        mc.thePlayer.sendQueue.addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, p, EnumFacing.NORTH));
    }
}
