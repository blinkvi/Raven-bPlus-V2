package keystrokesmod.client.command.commands;

import keystrokesmod.client.clickgui.raven.Terminal;
import keystrokesmod.client.command.Command;
import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.modules.combat.AimAssist;
import net.minecraft.entity.Entity;

public class Friends extends Command
{
    public Friends() {
        super("friends", "Allows you to manage and view your friends list", 1, 2, new String[] { "add / remove / list", "Player's name" }, new String[] { "f", "amigos", "lonely4ever" });
    }
    
    @Override
    public void onCall(final String[] args) {
    	AimAssist aim = (AimAssist) Raven.moduleManager.getModuleByClazz(AimAssist.class);
        if (args.length == 0) {
            this.listFriends();
        }
        else if (args[0].equalsIgnoreCase("list")) {
            this.listFriends();
        }
        else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) {
                final boolean added = aim.addFriend(args[1]);
                if (added) {
                    Terminal.print("Successfully added " + args[1] + " to your friends list!");
                }
                else {
                    Terminal.print("An error occurred!");
                }
            }
            else if (args[0].equalsIgnoreCase("remove")) {
                final boolean removed = aim.removeFriend(args[1]);
                if (removed) {
                    Terminal.print("Successfully removed " + args[1] + " from your friends list!");
                }
                else {
                    Terminal.print("An error occurred!");
                }
            }
        }
        else {
            this.incorrectArgs();
        }
    }
    
    public void listFriends() {
    	AimAssist aim = (AimAssist) Raven.moduleManager.getModuleByClazz(AimAssist.class);

        if (aim.getFriends().isEmpty()) {
            Terminal.print("You have no friends. :(");
        }
        else {
            Terminal.print("Your friends are:");
            for (final Entity entity : aim.getFriends()) {
                Terminal.print(entity.getName());
            }
        }
    }
}
