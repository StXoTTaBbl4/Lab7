package Program.Common.Command;

import Program.Common.Command.Commands.*;
import Program.Common.Command.Commands.AddIfMax.AddIfMaxCommand;
import Program.Common.DataClasses.Transporter;
import Program.Common.DataClasses.Worker;
import Program.Server.InnerServerTransporter;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Класс, который отвечает за обработку пользовательских команд и передачу их дальше для исполнения.
 */
public class CommandManager {

    private final List<ICommand> commands = new ArrayList<>();
    private Connection connection = null;

    /**
     * @return Список доступных команд.
     */
    public List<ICommand> getCommands() {
        return commands;
    }

    /**
     * Конструктор, в который добавляются все объекты классов-команд с помощью {@link CommandManager#addCommand(ICommand)}.
     *
     * @param c Связь с БД.
     */
    public CommandManager(Connection c){
        addCommand(new AddCommand(c));
        addCommand(new AddIfMaxCommand(this,c));
        addCommand(new ClearCommand(c));
        addCommand(new CountGtpCommand());
        addCommand(new ExecuteScriptCommand(this));
        addCommand(new ExitCommand());
        addCommand(new FilterGtsCommand());
        addCommand(new GroupCbsCommand());
        addCommand(new HelpCommand(this));
        //addCommand(new InfoCommand(path));
        addCommand(new RemoveIdCommand(c));
        addCommand(new RemoveLastCommand(c));
        //addCommand(new SaveCommand());
        addCommand(new ShowCommand());
        addCommand(new SortCommand());
        addCommand(new UpdateIdCommand(c));
        this.connection = c;
    }

    /**
     * Конструктор, в который добавляются все объекты классов-команд с помощью {@link CommandManager#addCommand(ICommand)}.
     * Для "клиентской" части.
     */
    public CommandManager(){
        addCommand(new AddCommand());
        addCommand(new AddIfMaxCommand());
        addCommand(new ClearCommand());
        addCommand(new CountGtpCommand());
        addCommand(new ExecuteScriptCommand(this));
        addCommand(new ExitCommand());
        addCommand(new FilterGtsCommand());
        addCommand(new GroupCbsCommand());
        addCommand(new HelpCommand(this));
        addCommand(new InfoCommand());
        addCommand(new RemoveIdCommand());
        addCommand(new RemoveLastCommand());
        addCommand(new SaveCommand());
        addCommand(new ShowCommand());
        addCommand(new SortCommand());
        addCommand(new UpdateIdCommand());
    }

    /**
     * Внутренний метод для добавления команды в общий список доступных команд.
     *
     * @param cmd Объект класса-команды
     */
    private void addCommand(ICommand cmd){
        boolean nameFound = this.commands.stream().anyMatch((it) -> it.getName().equalsIgnoreCase(cmd.getName()));
        if(nameFound){
            throw new IllegalArgumentException("Command: "+cmd.getName()+" already exists");
        }
        commands.add(cmd);
    }

    /** Метод для поиска команды по ее имени.
     * @param search имя искомой команды.
     * @return В случае существования команды возвращает объект класса-команды.
     */
    public ICommand getCommand(String search){
        String searchLower = search.toLowerCase();

        for (ICommand cmd: this.commands) {
            if(cmd.getName().equals(searchLower)){
                return cmd;
            }
        }
        return null;
    }

    /**
     * @param transporter Объект, содержащий коллекцию, {@link InnerServerTransporter}.
     * @return Объект, содержащий коллекцию после ее обработки командой и текст отчета.
     */
    public InnerServerTransporter CommandHandler(InnerServerTransporter transporter){
        String[] data = transporter.getArgs().split(" ");

        ICommand cmd  = this.getCommand(data[0]);

            if (cmd != null) {
                List<String> args = Arrays.asList(data).subList(1, data.length);
                transporter.setArgs(args.toString().substring(1, args.toString().length() - 1));

                transporter = cmd.handle(transporter);
            } else {
                System.out.println("Command not found!\n");
            }
        return transporter;
    }

    /**
     * @param t Объект класса {@link Transporter}, содержащий данные от пользователя(команда + аргументы).
     * @return true, если команда соотвествует минимальным требованиям объекта класса-команды для выполнения.
     */
    public Boolean validate(Transporter t){
        String[] data = t.getCommand().split(" ");
        ICommand cmd  = this.getCommand(data[0]);
        boolean val = false;

        if(cmd != null){
            List<String> args = Arrays.asList(data).subList(1, data.length);

            val = cmd.inputValidate(args.toString().substring(1,args.toString().length()-1));
        }else{
            System.out.println("Command not found!\n");
        }

        return val;
    }
}
