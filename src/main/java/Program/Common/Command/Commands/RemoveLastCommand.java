package Program.Common.Command.Commands;

import Program.Common.Command.ICommand;
import Program.Common.Communicator;
import Program.Common.DataClasses.Worker;
import Program.Server.InnerServerTransporter;

import java.sql.Connection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Удаляет последний элемент из коллекции.
 */
public class RemoveLastCommand implements ICommand {
    private Connection connection;

    public RemoveLastCommand() {
    }

    public RemoveLastCommand(Connection connection) {
        this.connection = connection;
    }

    @Override
    public Boolean inputValidate(String args) {
        return true;
    }

    @Override
    public InnerServerTransporter handle(InnerServerTransporter transporter) {

        LinkedList<Worker> WorkersData = transporter.getWorkersData();

        try {
            if(!WorkersData.getLast().getLogin().equals(transporter.getLogin()) || !WorkersData.getLast().getPassword().equals(transporter.getPassword())) {
                Communicator communicator = new Communicator();
                Collections.sort(WorkersData);
                communicator.delete_db(connection,WorkersData.getLast());
                WorkersData.removeLast();
                transporter.setWorkersData(WorkersData);
                transporter.setMsg("Command completed.");
            }
            else{
                transporter.setMsg("You do not have access to this file.");
                return transporter;
            }
        }catch (NoSuchElementException e){
            System.out.println("Коллекция пуста.");
        }

        return transporter;
    }

    @Override
    public String getName() {
        return "remove_last";
    }

    @Override
    public String getHelp() {
        return "Removes the last element from the collection.";
    }
}
