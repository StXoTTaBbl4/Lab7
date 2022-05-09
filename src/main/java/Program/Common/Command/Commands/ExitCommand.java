package Program.Common.Command.Commands;

import Program.Common.Command.ICommand;
import Program.Common.DataClasses.Worker;
import Program.Server.InnerServerTransporter;

import java.util.LinkedList;

/**
 * Завершает программу (без сохранения в файл).
 */
public class ExitCommand implements ICommand {
    @Override
    public Boolean inputValidate(String args) {
        return false;
    }

    @Override
    public InnerServerTransporter handle(InnerServerTransporter transporter) {
        //System.exit(0);
        transporter.setMsg("The client cannot shut down the server.");
        return transporter;
    }

    @Override
    public String getName() {
        return "exit";
    }

    @Override
    public String getHelp() {
        return "Terminates the program (without saving to a file).";
    }
}
