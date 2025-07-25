package keystrokesmod.client.command;

import java.util.*;

import keystrokesmod.client.clickgui.raven.Terminal;
import keystrokesmod.client.command.commands.*;
import keystrokesmod.client.main.Raven;
import keystrokesmod.client.module.modules.client.HUD;
import keystrokesmod.client.utils.Utils;

public class CommandManager {
    private final List<Command> commandList = new ArrayList<>();
    private final List<Command> sortedCommandList = new ArrayList<>();

    public CommandManager() {
        registerCommands(
            new Help(),
            new ConfigCommand(),
            new Clear(),
            new Cname(),
            new Debug(),
            new Fakechat(),
            new Ping(),
            new Shoutout(),
            new Uwu(),
            new Friends(),
            new F3Name()
        );
    }

    private void registerCommands(Command... commands) {
        for (Command cmd : commands) {
            addCommand(cmd);
        }
    }

    public void addCommand(Command command) {
        commandList.add(command);
        sortedCommandList.add(command);
    }

    public List<Command> getCommandList() {
        return Collections.unmodifiableList(commandList);
    }

    public Command getCommandByName(String name) {
        for (Command command : commandList) {
            if (command.getName().equalsIgnoreCase(name)) {
                return command;
            }
            for (String alias : command.getAliases()) {
                if (alias.equalsIgnoreCase(name)) {
                    return command;
                }
            }
        }
        return null;
    }

    public void noSuchCommand(String name) {
        Terminal.print("Command '" + name + "' not found! Report this on the Discord if this is an error!");
    }

    public void executeCommand(String commandName, String[] args) {
        Command command = getCommandByName(commandName);
        if (command == null) {
            noSuchCommand(commandName);
            return;
        }
        command.onCall(args);
    }

    public void sort() {
    	HUD hud = (HUD) Raven.moduleManager.getModuleByClazz(HUD.class);

        if (hud.alphabeticalSort.isToggled()) {
            sortedCommandList.sort(Comparator.comparing(Command::getName, String.CASE_INSENSITIVE_ORDER));
        } else {
            sortedCommandList.sort(Comparator.comparingInt(
                cmd -> -Utils.mc.fontRendererObj.getStringWidth(cmd.getName())
            ));
        }
    }
}
