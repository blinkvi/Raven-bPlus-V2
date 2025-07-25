package keystrokesmod.client.module.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.module.setting.impl.SliderSetting;
import keystrokesmod.client.utils.Utils;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "Xray", category = Category.Render)
public class Xray extends ClientModule {

    private final SliderSetting range = new SliderSetting("Range", this, 20.0, 5.0, 50.0, 1.0);

    private final TickSetting iron = new TickSetting("Iron", this, true);
    private final TickSetting gold = new TickSetting("Gold", this, true);
    private final TickSetting diamond = new TickSetting("Diamond", this, true);
    private final TickSetting emerald = new TickSetting("Emerald", this, true);
    private final TickSetting lapis = new TickSetting("Lapis", this, true);
    private final TickSetting redstone = new TickSetting("Redstone", this, true);
    private final TickSetting coal = new TickSetting("Coal", this, true);
    private final TickSetting spawner = new TickSetting("Spawner", this, true);

    private final List<BlockPos> renderList = new ArrayList<>();
    private final long scanInterval = 200L;
    private Timer scanTimer;

    @Override
    public void onEnable() {
        renderList.clear();
        scanTimer = new Timer();
        scanTimer.scheduleAtFixedRate(createScanTask(), 0L, scanInterval);
    }

    @Override
    public void onDisable() {
        if (scanTimer != null) {
            scanTimer.cancel();
            scanTimer.purge();
            scanTimer = null;
        }
        renderList.clear();
    }

    private TimerTask createScanTask() {
        return new TimerTask() {
            @Override
            public void run() {
                renderList.clear();

                int scanRange = (int) range.getInput();
                for (int y = scanRange; y >= -scanRange; y--) {
                    for (int x = -scanRange; x <= scanRange; x++) {
                        for (int z = -scanRange; z <= scanRange; z++) {
                            if (!Utils.Player.isPlayerInGame()) continue;

                            BlockPos pos = new BlockPos(
                                mc.thePlayer.posX + x,
                                mc.thePlayer.posY + y,
                                mc.thePlayer.posZ + z
                            );

                            Block block = mc.theWorld.getBlockState(pos).getBlock();
                            if (shouldHighlight(block)) {
                                renderList.add(pos);
                            }
                        }
                    }
                }
            }
        };
    }

    @SubscribeEvent
    public void onRenderWorld(RenderWorldLastEvent event) {
        if (!Utils.Player.isPlayerInGame() || renderList.isEmpty()) return;

        List<BlockPos> currentList = new ArrayList<>(renderList);
        for (BlockPos pos : currentList) {
            drawBlock(pos);
        }
    }

    private void drawBlock(BlockPos pos) {
        Block block = mc.theWorld.getBlockState(pos).getBlock();
        int[] rgb = getColorForBlock(block);
        if (rgb[0] + rgb[1] + rgb[2] != 0) {
            int color = new Color(rgb[0], rgb[1], rgb[2]).getRGB();
            Utils.HUD.re(pos, color, true);
        }
    }

    private boolean shouldHighlight(Block block) {
        return (iron.isToggled() && block.equals(Blocks.iron_ore)) ||
               (gold.isToggled() && block.equals(Blocks.gold_ore)) ||
               (diamond.isToggled() && block.equals(Blocks.diamond_ore)) ||
               (emerald.isToggled() && block.equals(Blocks.emerald_ore)) ||
               (lapis.isToggled() && block.equals(Blocks.lapis_ore)) ||
               (redstone.isToggled() && block.equals(Blocks.redstone_ore)) ||
               (coal.isToggled() && block.equals(Blocks.coal_ore)) ||
               (spawner.isToggled() && block.equals(Blocks.mob_spawner));
    }

    private int[] getColorForBlock(Block block) {
        int r = 0, g = 0, b = 0;

        if (block.equals(Blocks.iron_ore)) {
            r = g = b = 255;
        } else if (block.equals(Blocks.gold_ore)) {
            r = g = 255;
        } else if (block.equals(Blocks.diamond_ore)) {
            g = 220;
            b = 255;
        } else if (block.equals(Blocks.emerald_ore)) {
            r = 35;
            g = 255;
        } else if (block.equals(Blocks.lapis_ore)) {
            g = 50;
            b = 255;
        } else if (block.equals(Blocks.redstone_ore)) {
            r = 255;
        } else if (block.equals(Blocks.mob_spawner)) {
            r = 30;
            b = 135;
        }

        return new int[]{r, g, b};
    }
}