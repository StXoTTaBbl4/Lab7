package Program.Common.Command.Commands.AddIfMax;

import Program.Common.Command.CommandManager;
import Program.Common.Command.ICommand;
import Program.Common.DataClasses.Worker;
import Program.Server.InnerServerTransporter;

import java.util.Collections;
import java.util.LinkedList;
import java.util.NoSuchElementException;

/**
 * добавить новый элемент в коллекцию, если его значение превышает значение наибольшего элемента этой коллекции
 */
public class AddIfMaxCommand implements ICommand {

    CommandManager manager;

    public AddIfMaxCommand(CommandManager commandManager) {
        this.manager = commandManager;
    }

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
        transporter.setArgs("add " + args.replaceAll(",",""));
        transporter.setWorkersData(newWorker);
        Worker worker = manager.CommandHandler(transporter).getWorkersData().get(0);

        try{
            worker.setId(WorkersData.getLast().getId()+1);
        }
        catch (NoSuchElementException e){
            worker.setId(1);
            WorkersData.add(worker);
            transporter.setWorkersData(WorkersData);
            transporter.setMsg("Command completed.");
            return transporter;
        }

        WorkersData.sort(addIfMaxComparator);
        try {
            if(addIfMaxComparator.compare(worker,WorkersData.getLast()) > 0)
                WorkersData.add(worker);
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
