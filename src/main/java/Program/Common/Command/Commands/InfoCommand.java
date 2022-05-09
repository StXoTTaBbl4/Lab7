package Program.Common.Command.Commands;

import Program.Common.Command.ICommand;
import Program.Common.DataClasses.Worker;
import Program.Server.InnerServerTransporter;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import java.util.Date;
import java.util.LinkedList;

/**
 * Выводит в стандартный поток вывода информацию о коллекции (тип, дата инициализации, количество элементов).
 */
public class InfoCommand  implements ICommand {
    String path;
    /**
     * @param path Путь к файлу коллекции.
     */

    public InfoCommand(String path) {
        this.path = path;
    }

    public InfoCommand() {}

    @Override
    public Boolean inputValidate(String args) {
        return true;
    }

    @Override
    public InnerServerTransporter handle(InnerServerTransporter transporter) {
        File file = new File(path);
        LinkedList<Worker> WorkersData = transporter.getWorkersData();
        Date d = new Date(file.lastModified());
        DateFormat dateParser = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        transporter.setMsg("Type: " + WorkersData.getClass().toString().replace("class","")+ "\n" +
                           "Last modified date: "+ dateParser.format(d) + "\n" +
                           "Number of elements: " + WorkersData.size() + "\n");

        return transporter;
    }

    @Override
    public String getName() {
        return "info";
    }

    @Override
    public String getHelp() {
        return "Prints information about the collection to standard output.\n" +
                "(type, initialization date, number of elements).";
    }
}
