package Program.Common.Command.Commands;

import Program.Common.Command.ICommand;
import Program.Common.DataClasses.Worker;
import Program.Server.InnerServerTransporter;

import java.util.LinkedList;

/**
 * Выводит в стандартный поток вывода все элементы коллекции в строковом представлении.
 */
public class ShowCommand implements ICommand {
    @Override
    public Boolean inputValidate(String args) {
        return true;
    }

    @Override
    public InnerServerTransporter handle(InnerServerTransporter transporter) {

        LinkedList<Worker> WorkersData = transporter.getWorkersData();

            if(WorkersData.size() == 0) {
                transporter.setMsg("The collection is empty.");
                return transporter;
            }
            else {
                StringBuilder stringBuilder = new StringBuilder();
                for (Worker worker : WorkersData) {
                    try {
                        stringBuilder.append(worker.toString());
                        stringBuilder.append("\n");
                    } catch (NullPointerException e) {
                        stringBuilder.append("Worker with id: ").append(worker.getId()).append("have incorrect data.\n");
                        stringBuilder.append("\n");
                    }
                }
                transporter.setMsg(String.valueOf(stringBuilder));
            }

        return transporter;
    }

    @Override
    public String getName() {
        return "show";
    }

    @Override
    public String getHelp() {
        return "Returns a list of the elements of the collection.";
    }
}
