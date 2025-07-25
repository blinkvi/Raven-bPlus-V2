package keystrokesmod.client.main;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import keystrokesmod.client.clickgui.raven.ClickGui;
import keystrokesmod.client.command.CommandManager;
import keystrokesmod.client.config.ConfigManager;
import keystrokesmod.client.module.ModuleManager;
import keystrokesmod.client.utils.ChatHelper;
import keystrokesmod.client.utils.MouseManager;
import keystrokesmod.client.utils.Utils;
import keystrokesmod.keystroke.KeyStrokeCommand;
import keystrokesmod.keystroke.KeyStrokeRenderer;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class Raven {
    public static final String VERSION = "1.0.26";
    public static boolean debugger = false;

    public static final ModuleManager moduleManager = new ModuleManager();
    public static CommandManager commandManager;
    public static ConfigManager configManager;
    public static ClientConfig clientConfig;
    public static ClickGui clickGui;

    private static final ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

    public static void init() {
        registerEvents();
        initSystems();

        Runtime.getRuntime().addShutdownHook(new Thread(executor::shutdown));
    }

    private static void registerEvents() {
        MinecraftForge.EVENT_BUS.register(new Raven());
        MinecraftForge.EVENT_BUS.register(new MouseManager());
        MinecraftForge.EVENT_BUS.register(new ChatHelper());
        MinecraftForge.EVENT_BUS.register(new KeyStrokeRenderer());
        ClientCommandHandler.instance.registerCommand(new KeyStrokeCommand());
    }

    private static void initSystems() {
        commandManager = new CommandManager();
        clickGui = new ClickGui();
        configManager = new ConfigManager();

        clientConfig = new ClientConfig();
        clientConfig.applyConfig();
    }

    @SubscribeEvent
    public void onTick(final TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || !Utils.Player.isPlayerInGame()) {
            return;
        }

        final Minecraft mc = Minecraft.getMinecraft();
        final boolean inGame = mc.currentScreen == null;
        final boolean inGui = mc.currentScreen instanceof ClickGui;

        moduleManager.getModules().forEach(module -> {
            if (inGame) {
                module.keybind();
            } else if (inGui) {
                module.guiUpdate();
            }

            if (module.isEnabled()) {
                module.update();
            }
        });
    }

    public static ScheduledExecutorService getExecutor() {
        return executor;
    }
}