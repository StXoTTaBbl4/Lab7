package Program.Common.Command.Commands;

import Program.Common.Command.ICommand;
import Program.Common.DataClasses.Worker;
import Program.Server.InnerServerTransporter;

import java.util.Collections;
import java.util.LinkedList;

/**
 * Сортирует коллекцию в естественном порядке(по ID).
 */
public class SortCommand implements ICommand {

    @Override
    public Boolean inputValidate(String args) {
        return true;
    }

    @Override
    public InnerServerTransporter handle(InnerServerTransporter transporter) {
        LinkedList<Worker> WorkersData = transporter.getWorkersData();

        try {
            Collections.sort(WorkersData);
            transporter.setWorkersData(WorkersData);
            transporter.setMsg("Command completed.");
        }catch (NullPointerException e){
            transporter.setMsg("Sorting error, checking the fields Coordinates, Person, Salary. They must not be null.");
        }

        return transporter;
    }

    @Override
    public String getName() {
        return "sort";
    }

    @Override
    public String getHelp() {
        return "Sorts the collection in natural order (by ID).";
    }
}
