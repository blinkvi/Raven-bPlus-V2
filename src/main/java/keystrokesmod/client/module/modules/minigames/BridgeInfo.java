package keystrokesmod.client.module.modules.minigames;

import java.awt.Color;
import java.io.IOException;

import keystrokesmod.client.module.Category;
import keystrokesmod.client.module.ClientModule;
import keystrokesmod.client.module.ModuleInfo;
import keystrokesmod.client.module.setting.impl.TickSetting;
import keystrokesmod.client.module.setting.impl.DescriptionSetting;
import keystrokesmod.client.utils.Utils;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.client.config.GuiButtonExt;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

@ModuleInfo(name = "BridgeInfo", category = Category.Minigames)
public class BridgeInfo extends ClientModule {
    private final DescriptionSetting a = new DescriptionSetting("Only for solos.", this);
    private final TickSetting ep = new TickSetting("Edit position", this, false);
    private static final int rgb;
    private static int hudX;
    private static int hudY;
    private String en;
    private BlockPos g1p;
    private BlockPos g2p;
    private boolean q;
    private double d1;
    private double d2;
    private int blc;
    
    public BridgeInfo() {
        this.en = "";
        this.g1p = null;
        this.g2p = null;
        this.q = false;
        this.d1 = 0.0;
        this.d2 = 0.0;
        this.blc = 0;
    }
    
    @Override
    public void onDisable() {
        this.rv();
    }
    
    @Override
    public void guiButtonToggled(final TickSetting b) {
        if (b == ep) {
            ep.disable();
            mc.displayGuiScreen((GuiScreen)new eh());
        }
    }
    
    @Override
    public void update() {
        if (!this.en.isEmpty() && this.ibd()) {
            EntityPlayer enem = null;
            for (final Entity e : BridgeInfo.mc.theWorld.loadedEntityList) {
                if (e instanceof EntityPlayer) {
                    if (!e.getName().equals(this.en)) {
                        continue;
                    }
                    enem = (EntityPlayer)e;
                }
                else {
                    if (!(e instanceof EntityArmorStand)) {
                        continue;
                    }
                    final String g2t = "Jump in to score!";
                    final String g1t = "Defend!";
                    if (e.getName().contains(g1t)) {
                        this.g1p = e.getPosition();
                    }
                    else {
                        if (!e.getName().contains(g2t)) {
                            continue;
                        }
                        this.g2p = e.getPosition();
                    }
                }
            }
            if (this.g1p != null && this.g2p != null) {
                this.d1 = Utils.Java.round(BridgeInfo.mc.thePlayer.getDistance((double)this.g2p.getX(), (double)this.g2p.getY(), (double)this.g2p.getZ()) - 1.4, 1);
                if (this.d1 < 0.0) {
                    this.d1 = 0.0;
                }
                this.d2 = ((enem == null) ? 0.0 : Utils.Java.round(enem.getDistance((double)this.g1p.getX(), (double)this.g1p.getY(), (double)this.g1p.getZ()) - 1.4, 1));
                if (this.d2 < 0.0) {
                    this.d2 = 0.0;
                }
            }
            int blc2 = 0;
            for (int i = 0; i < 9; ++i) {
                final ItemStack stack = BridgeInfo.mc.thePlayer.inventory.getStackInSlot(i);
                if (stack != null && stack.getItem() instanceof ItemBlock && ((ItemBlock)stack.getItem()).block.equals(Blocks.stained_hardened_clay)) {
                    blc2 += stack.stackSize;
                }
            }
            this.blc = blc2;
        }
    }
    
    @SubscribeEvent
    public void a(final TickEvent.RenderTickEvent ev) {
        if (ev.phase == TickEvent.Phase.END && Utils.Player.isPlayerInGame() && this.ibd()) {
            if (BridgeInfo.mc.currentScreen != null || BridgeInfo.mc.gameSettings.showDebugInfo) {
                return;
            }
            final String t1 = "Enemy: ";
            BridgeInfo.mc.fontRendererObj.drawString(t1 + this.en, (float)BridgeInfo.hudX, (float)BridgeInfo.hudY, BridgeInfo.rgb, true);
            final String t2 = "Distance to goal: ";
            BridgeInfo.mc.fontRendererObj.drawString(t2 + this.d1, (float)BridgeInfo.hudX, (float)(BridgeInfo.hudY + 11), BridgeInfo.rgb, true);
            final String t3 = "Enemy distance to goal: ";
            BridgeInfo.mc.fontRendererObj.drawString(t3 + this.d2, (float)BridgeInfo.hudX, (float)(BridgeInfo.hudY + 22), BridgeInfo.rgb, true);
            final String t4 = "Blocks: ";
            BridgeInfo.mc.fontRendererObj.drawString(t4 + this.blc, (float)BridgeInfo.hudX, (float)(BridgeInfo.hudY + 33), BridgeInfo.rgb, true);
        }
    }
    
    @SubscribeEvent
    public void o(final ClientChatReceivedEvent c) {
        if (Utils.Player.isPlayerInGame()) {
            final String s = Utils.Java.str(c.message.getUnformattedText());
            if (s.startsWith(" ")) {
                final String qt = "First player to score 5 goals wins";
                if (s.contains(qt)) {
                    this.q = true;
                }
                else if (this.q && s.contains("Opponent:")) {
                    String n = s.split(":")[1].trim();
                    if (n.contains("[")) {
                        n = n.split("] ")[1];
                    }
                    this.en = n;
                    this.q = false;
                }
            }
        }
    }
    
    @SubscribeEvent
    public void w(final EntityJoinWorldEvent j) {
        if (j.entity == BridgeInfo.mc.thePlayer) {
            this.rv();
        }
    }
    
    private boolean ibd() {
        if (Utils.Client.isHyp()) {
            for (final String s : Utils.Client.getPlayersFromScoreboard()) {
                final String s2 = s.toLowerCase();
                final String bd = "the brid";
                if (s2.contains("mode") && s2.contains(bd)) {
                    return true;
                }
            }
        }
        return false;
    }
    
    private void rv() {
        this.en = "";
        this.q = false;
        this.g1p = null;
        this.g2p = null;
        this.d1 = 0.0;
        this.d2 = 0.0;
        this.blc = 0;
    }
    
    static {
        rgb = new Color(0, 200, 200).getRGB();
        BridgeInfo.hudX = 5;
        BridgeInfo.hudY = 70;
    }
    
    static class eh extends GuiScreen
    {
        final String a = "Enemy: Player123-Distance to goal: 17.2-Enemy distance to goal: 16.3-Blocks: 98";
        GuiButtonExt rp;
        boolean d;
        int miX;
        int miY;
        int maX;
        int maY;
        int aX;
        int aY;
        int laX;
        int laY;
        int lmX;
        int lmY;
        
        eh() {
            this.d = false;
            this.miX = 0;
            this.miY = 0;
            this.maX = 0;
            this.maY = 0;
            this.aX = 5;
            this.aY = 70;
            this.laX = 0;
            this.laY = 0;
            this.lmX = 0;
            this.lmY = 0;
        }
        
        public void initGui() {
            super.initGui();
            this.buttonList.add(this.rp = new GuiButtonExt(1, this.width - 90, 5, 85, 20, "Reset position"));
            this.aX = BridgeInfo.hudX;
            this.aY = BridgeInfo.hudY;
        }
        
        public void drawScreen(final int mX, final int mY, final float pt) {
            drawRect(0, 0, this.width, this.height, -1308622848);
            final int miX = this.aX;
            final int miY = this.aY;
            final int maX = miX + 140;
            final int maY = miY + 41;
            final FontRenderer fontRendererObj = this.mc.fontRendererObj;
            this.getClass();
            this.d(fontRendererObj, "Enemy: Player123-Distance to goal: 17.2-Enemy distance to goal: 16.3-Blocks: 98");
            this.miX = miX;
            this.miY = miY;
            this.maX = maX;
            this.maY = maY;
            BridgeInfo.hudX = miX;
            BridgeInfo.hudY = miY;
            final ScaledResolution res = new ScaledResolution(this.mc);
            final int x = res.getScaledWidth() / 2 - 84;
            final int y = res.getScaledHeight() / 2 - 20;
            Utils.HUD.drawColouredText("Edit the HUD position by dragging.", '-', x, y, 2L, 0L, true, this.mc.fontRendererObj);
            try {
                this.handleInput();
            }
            catch (IOException ex) {}
            super.drawScreen(mX, mY, pt);
        }
        
        private void d(final FontRenderer fr, final String t) {
            final int x = this.miX;
            int y = this.miY;
            final String[] var5 = t.split("-");
            final int var6 = var5.length;
            for (final String s : var5) {
                fr.drawString(s, (float)x, (float)y, BridgeInfo.rgb, true);
                y += fr.FONT_HEIGHT + 2;
            }
        }
        
        protected void mouseClickMove(final int mX, final int mY, final int b, final long t) {
            super.mouseClickMove(mX, mY, b, t);
            if (b == 0) {
                if (this.d) {
                    this.aX = this.laX + (mX - this.lmX);
                    this.aY = this.laY + (mY - this.lmY);
                }
                else if (mX > this.miX && mX < this.maX && mY > this.miY && mY < this.maY) {
                    this.d = true;
                    this.lmX = mX;
                    this.lmY = mY;
                    this.laX = this.aX;
                    this.laY = this.aY;
                }
            }
        }
        
        protected void mouseReleased(final int mX, final int mY, final int s) {
            super.mouseReleased(mX, mY, s);
            if (s == 0) {
                this.d = false;
            }
        }
        
        public void actionPerformed(final GuiButton b) {
            if (b == this.rp) {
                this.aX = (BridgeInfo.hudX = 5);
                this.aY = (BridgeInfo.hudY = 70);
            }
        }
        
        public boolean doesGuiPauseGame() {
            return false;
        }
    }
}
