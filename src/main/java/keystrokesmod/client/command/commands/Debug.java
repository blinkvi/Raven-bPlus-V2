package keystrokesmod.client.command.commands;

import keystrokesmod.client.clickgui.raven.Terminal;
import keystrokesmod.client.command.Command;
import keystrokesmod.client.main.Raven;

public class Debug extends Command
{
    public Debug() {
        super("debug", "Toggles B+ debbugger", 0, 0, new String[0], new String[] { "dbg", "log" });
    }
    
    @Override
    public void onCall(final String[] args) {
        Raven.debugger = !Raven.debugger;
        Terminal.print((Raven.debugger ? "Enabled" : "Disabled") + " debugging.");
    }
}
