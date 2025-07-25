package keystrokesmod.client.command.commands;

import keystrokesmod.client.clickgui.raven.Terminal;
import keystrokesmod.client.command.Command;
import keystrokesmod.client.module.modules.other.NameHider;

public class Cname extends Command
{
    public Cname() {
        super("cname", "Hides your name client-side", 1, 1, new String[] { "New name" }, new String[] { "cn", "changename" });
    }
    
    @Override
    public void onCall(final String[] args) {
        if (args.length == 0) {
            this.incorrectArgs();
            return;
        }
        NameHider.name = args[0];
        Terminal.print("Nick has been set to: " + NameHider.name);
    }
}
