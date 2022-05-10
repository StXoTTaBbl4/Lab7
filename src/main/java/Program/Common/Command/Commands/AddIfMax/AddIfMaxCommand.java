package Program.Common.Command.Commands.AddIfMax;

import Program.Common.Command.CommandManager;
import Program.Common.Command.Commands.AddCommand;
import Program.Common.Command.ICommand;
import Program.Common.Communicator;
import Program.Common.DataClasses.Worker;
import Program.Server.InnerServerTransporter;

import java.sql.Connection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции
 */
public class AddIfMaxCommand implements ICommand {
    private Connection connection;
    CommandManager manager;

    public AddIfMaxCommand(CommandManager commandManager, Connection connection) {
        this.connection = connection;
        this.manager = commandManager;
    }

    public AddIfMaxCommand() {}

    @Override
    public Boolean inputValidate(String args) {
        return true;
    }

    @Override
    public InnerServerTransporter handle(InnerServerTransporter transporter) {

        AddIfMaxComparator addIfMaxComparator = new AddIfMaxComparator();
        LinkedList<Worker> WorkersData = transporter.getWorkersData();
        String args = transporter.getArgs();

        Collections.sort(WorkersData);
        LinkedList<Worker> newWorker = new LinkedList<>();
        transporter.setWorkersData(newWorker);
        AddCommand addCommand = new AddCommand();
        Worker worker = addCommand.createNewWorker(transporter).getWorkersData().getLast();

        Communicator communicator = new Communicator();
        LinkedList<Worker> toUpload = new LinkedList<>();
        toUpload.add(worker);
        try{
            worker.setId(WorkersData.getLast().getId()+1);
        }
        catch (NoSuchElementException e){
            worker.setId(1);
            boolean k = communicator.merge_db(connection,toUpload);
            if(k) {
                WorkersData.add(worker);
                transporter.setWorkersData(WorkersData);
                transporter.setMsg("Command completed.");
            }else {
                transporter.setWorkersData(WorkersData);
                transporter.setMsg("Failed to add to DB.");
            }
            return transporter;
        }

        WorkersData.sort(addIfMaxComparator);
        try {
            if(addIfMaxComparator.compare(worker,WorkersData.getLast()) > 0) {
                boolean k = communicator.merge_db(connection,toUpload);
                if(k)
                    WorkersData.add(worker);
                else{
                    transporter.setMsg("Failed to add to DB.");
                    return transporter;
                }

            }
        }
        catch (IndexOutOfBoundsException e){
            transporter.setMsg("Список пуст, не с чем сравнивать.");
            return transporter;
        }

        return transporter;
    }

    @Override
    public String getName() {
        return "add_if_max";
    }

    @Override
    public String getHelp() {
        return "Добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции";
    }
}
