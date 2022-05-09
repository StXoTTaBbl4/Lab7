package Program.Common.Command.Commands;

import Program.Common.Command.CommandManager;
import Program.Common.Command.ICommand;
import Program.Common.DataClasses.Worker;
import Program.Server.InnerServerTransporter;

import java.util.LinkedList;

/**
 * Выводит справку по доступным командам.
 */
public class HelpCommand implements ICommand {

    private final CommandManager manager;

    /** Запрашивает существующий commandManager для возможности вызова у них методов help и getName.
     *
     * @param manager CommandManager для доступа к методам других команд.
     */
    public HelpCommand(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public Boolean inputValidate(String args) {
        return true;
    }

    @Override
    public InnerServerTransporter handle(InnerServerTransporter transporter) {

        String args = transporter.getArgs();

        if(args.isEmpty()){
            StringBuilder builder = new StringBuilder();

            builder.append("Command List:\n");

            manager.getCommands().stream().map(ICommand::getName).forEach((it) -> builder.append(it).append('\n'));
            transporter.setMsg(String.valueOf(builder));
        }else {

            ICommand command = manager.getCommand(args);

            if (command == null) {
                transporter.setMsg("Command " + args + " not found.\n");
            }else{
                transporter.setMsg(command.getHelp());
            }
        }
        return transporter;
    }

    @Override
    public String getName() {
        return "help";
    }

    @Override
    public String getHelp() {
        return "Use this command to get help on other commands\n" +
                "help [command name].";
    }
}
