package keystrokesmod.client.command.commands;

import keystrokesmod.client.clickgui.raven.Terminal;
import keystrokesmod.client.command.Command;
import keystrokesmod.client.main.Raven;

public class Uwu extends Command
{
    private static boolean u;
    
    public Uwu() {
        super("uwu", "hevex/blowsy added this lol", 0, 0, new String[0], new String[] { "hevex", "blowsy", "weeb", "torture", "noplsno" });
        Uwu.u = false;
    }
    @Override
    public void onCall(final String[] args) {
        if (Uwu.u) {
            return;
        }

        Raven.getExecutor().execute(() -> {
            Uwu.u = true;

            for (int i = 0; i < 4; ++i) {
                switch (i) {
                    case 0: Terminal.print("nya");
                    case 1: Terminal.print("ichi ni san");
                    case 2: Terminal.print("nya");
                    case 3: Terminal.print("arigatou!");
                }

                try {
                    Thread.sleep(500L);
                } catch (InterruptedException ignored) {
                    // Optionally restore interrupted state: Thread.currentThread().interrupt();
                }
            }

            Uwu.u = false;
        });
    }
}
