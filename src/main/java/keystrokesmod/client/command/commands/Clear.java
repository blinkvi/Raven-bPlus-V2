package keystrokesmod.client.command.commands;

import keystrokesmod.client.clickgui.raven.Terminal;
import keystrokesmod.client.command.Command;

public class Clear extends Command
{
    public Clear() {
        super("clear", "Clears the terminal", 0, 0, new String[0], new String[] { "l", "clr" });
    }
    
    @Override
    public void onCall(final String[] args) {
        Terminal.clearTerminal();
    }
}
