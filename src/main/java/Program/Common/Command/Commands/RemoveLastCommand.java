package Program.Common.Command.Commands;

import Program.Common.Command.ICommand;
import Program.Common.DataClasses.Worker;
import Program.Server.InnerServerTransporter;

import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * Удаляет последний элемент из коллекции.
 */
public class RemoveLastCommand implements ICommand {
    @Override
    public Boolean inputValidate(String args) {
        return true;
    }

    @Override
    public InnerServerTransporter handle(InnerServerTransporter transporter) {

        LinkedList<Worker> WorkersData = transporter.getWorkersData();

        try {
            WorkersData.removeLast();
            transporter.setWorkersData(WorkersData);
            transporter.setMsg("Command completed.");
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
