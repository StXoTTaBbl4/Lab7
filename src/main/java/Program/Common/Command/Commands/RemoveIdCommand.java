package Program.Common.Command.Commands;

import Program.Common.Command.ICommand;
import Program.Common.DataClasses.Worker;
import Program.Server.InnerServerTransporter;

import java.util.LinkedList;

/**
 * Удаляет элемент из коллекции по его id.
 */
public class RemoveIdCommand implements ICommand {
    @Override
    public Boolean inputValidate(String args) {
        try{
            int id = Integer.parseInt(args);
            return true;
        }catch (NumberFormatException e){
            System.out.println("id must be Integer");
            return false;
        }
    }

    @Override
    public InnerServerTransporter handle(InnerServerTransporter transporter) {
        int id;
        LinkedList<Worker> WorkersData = transporter.getWorkersData();

        try {
            id = Integer.parseInt(transporter.getArgs());
        } catch (NumberFormatException e) {
            transporter.setMsg("ID must be a positive integer greater than 0.");
            return transporter;
        }

        for(Worker worker : WorkersData){
            if(worker.getId() == id){
                WorkersData.remove(worker);
                transporter.setWorkersData(WorkersData);
                transporter.setMsg("Command completed.");
                return transporter;
            }
        }

        transporter.setMsg("There is no worker with this id.");

        return transporter;
    }

    @Override
    public String getName() {
        return "remove_by_id";
    }

    @Override
    public String getHelp() {
        return "Removes an element with a specific id.";
    }
}
