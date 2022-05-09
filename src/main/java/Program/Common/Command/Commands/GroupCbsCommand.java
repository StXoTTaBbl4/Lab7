package Program.Common.Command.Commands;

import Program.Common.Command.ICommand;
import Program.Common.DataClasses.Worker;
import Program.Server.InnerServerTransporter;

import java.util.LinkedList;

/**
 * Группирует элементы коллекции по значению поля salary, выводит количество элементов в каждой группе.
 */
public class GroupCbsCommand implements ICommand {
    @Override
    public Boolean inputValidate(String args) {
        return true;
    }

    @Override
    public InnerServerTransporter handle(InnerServerTransporter transporter) {

        LinkedList<Worker> WorkersData = transporter.getWorkersData();

        if(WorkersData.size() == 0) {
            transporter.setMsg("The collection is empty.");
        }
        else if(WorkersData.size() < 2) {
            transporter.setMsg("There is only 1 item in the collection with salary " + WorkersData.get(0).getSalary() +".\n");
        }
        else {
            float minSalary = Float.MAX_VALUE,
                                maxSalary = 0,
                                middleSalary,
                                midMinSalary,
                                midMaxSalary;

            for (Worker w : WorkersData) {
                if (w.getSalary() < minSalary)
                    minSalary = w.getSalary();
                if (w.getSalary() > maxSalary)
                    maxSalary = w.getSalary();
            }

            if(maxSalary == minSalary){
                transporter.setMsg("All items in the collection have a salary of " + minSalary +".\n");
            }
            else {
                middleSalary = (minSalary + maxSalary) / 2;
                midMinSalary = (middleSalary + minSalary) / 2;
                midMaxSalary = (middleSalary + maxSalary) / 2;

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(printData(minSalary, midMinSalary, WorkersData));
                stringBuilder.append(printData(midMinSalary + 0.1F, midMaxSalary, WorkersData));
                stringBuilder.append(printData(midMaxSalary + 0.1F, maxSalary, WorkersData));

                transporter.setMsg(String.valueOf(stringBuilder));
            }
        }
        return transporter;
    }

    @Override
    public String getName() {
        return "group_counting_by_salary";
    }

    @Override
    public String getHelp() {
        return "Groups the elements of the collection by the value of the salary field, displays the number of elements in each group.";
    }

    private String printData(float leftBorder, float rightBorder, LinkedList<Worker> WorkersData){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Зарплата от ").append(leftBorder).append(" до ").append(rightBorder);

        long k = WorkersData.stream().filter(w -> w.getSalary() >= leftBorder && w.getSalary() <= rightBorder).count();
        stringBuilder.append(k).append("\n");

        return String.valueOf(stringBuilder);
    }


}
