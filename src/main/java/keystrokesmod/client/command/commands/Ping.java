package keystrokesmod.client.command.commands;

import keystrokesmod.client.command.Command;
import keystrokesmod.client.utils.ChatHelper;

public class Ping extends Command
{
    public Ping() {
        super("ping", "Gets your ping", 0, 0, new String[0], new String[] { "p", "connection", "lag" });
    }
    
    @Override
    public void onCall(final String[] args) {
        ChatHelper.checkPing();
    }
}
