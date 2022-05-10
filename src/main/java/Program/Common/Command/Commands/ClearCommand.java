package Program.Common.Command.Commands;

import Program.Common.Command.ICommand;
import Program.Common.Communicator;
import Program.Common.DataClasses.Worker;
import Program.Server.InnerServerTransporter;

import java.sql.Connection;
import java.util.LinkedList;

/**
 * Очищает коллекцию.
 */
public class ClearCommand implements ICommand {
    private Connection connection;

    public ClearCommand(Connection connection) {
        this.connection = connection;
    }

    public ClearCommand() {}

    @Override
    public Boolean inputValidate(String args) {
        return true;
    }

    @Override
    public InnerServerTransporter handle(InnerServerTransporter transporter) {
        LinkedList<Worker> workers = transporter.getWorkersData();
        Communicator communicator = new Communicator();
        for(Worker w:workers){
        if(w.getLogin().equals(transporter.getLogin()) || w.getPassword().equals(transporter.getPassword())){
                communicator.delete_db(connection,w);
                workers.remove(w);
            }
        }
        transporter.setWorkersData(workers);

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
