package Program.Common.Command.Commands;

import Program.Common.Command.ICommand;
import Program.Common.DataClasses.Worker;
import Program.Server.InnerServerTransporter;

import java.util.LinkedList;

/**
 * Очищает коллекцию.
 */
public class ClearCommand implements ICommand {
    @Override
    public Boolean inputValidate(String args) {
        return true;
    }

    @Override
    public InnerServerTransporter handle(InnerServerTransporter transporter) {
        LinkedList<Worker> workers = transporter.getWorkersData();
        workers.removeIf(w -> (w.getLogin().equals(transporter.getLogin()) || w.getPassword().equals(transporter.getPassword())));

        return transporter;
    }

    @Override
    public String getName() {
        return "clear";
    }

    @Override
    public String getHelp() {
        return "Clears spec. user files in the collection.";
    }
}
