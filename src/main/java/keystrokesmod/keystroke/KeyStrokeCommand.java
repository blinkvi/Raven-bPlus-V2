package keystrokesmod.keystroke;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;

public class KeyStrokeCommand extends CommandBase {
	
	@Override
    public String getCommandName() {
        return "keystrokesmod";
    }
    
	@Override
    public void processCommand(final ICommandSender sender, final String[] args) {
        KeyStrokeMod.toggleKeyStrokeConfigGui();
    }
    
	@Override
    public String getCommandUsage(final ICommandSender sender) {
        return "/keystrokesmod";
    }
    
	@Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
    
	@Override
    public boolean canCommandSenderUseCommand(final ICommandSender sender) {
        return true;
    }
}
