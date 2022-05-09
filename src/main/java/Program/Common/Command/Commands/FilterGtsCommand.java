package Program.Common.Command.Commands;

import Program.Common.Command.ICommand;
import Program.Common.DataClasses.Worker;
import Program.Server.InnerServerTransporter;

import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Выводит элементы, значение поля salary которых больше заданного.
 */
public class FilterGtsCommand implements ICommand {
    @Override
    public Boolean inputValidate(String args) {
        try{
            Float.parseFloat(args);
            return true;
        }catch (NumberFormatException e){
            System.out.println("Incorrect data type.");
            return false;
        }
    }

    @Override
    public InnerServerTransporter handle(InnerServerTransporter transporter) {
        String args = transporter.getArgs();
        LinkedList<Worker> WorkersData = transporter.getWorkersData();
         Float salary = null;
        try {
            salary = Float.parseFloat(args);
        }
            catch (NumberFormatException e){
            transporter.setMsg("Invalid data type. Example: 1500.99.");
            return transporter;
        }

        StringBuilder stringBuilder = new StringBuilder();
        for (Worker w : WorkersData) {
            try {
                if(w.getSalary() > salary)
                    stringBuilder.append(w);
                }
            catch (NullPointerException e){
                if(w.getSalary() == null)
                stringBuilder.append("The salary field is not set for id: " + w.getId() +" .\n");
            }
        }
        transporter.setMsg(String.valueOf(stringBuilder));
        return transporter;

    }

    @Override
    public String getName() {
        return "filter_greater_than_salary";
    }

    @Override
    public String getHelp() {
        return "Displays elements whose salary field value is greater than the specified value.";
    }
}
