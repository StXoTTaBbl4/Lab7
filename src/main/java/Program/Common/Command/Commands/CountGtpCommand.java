package Program.Common.Command.Commands;

import Program.Common.Command.ICommand;
import Program.Common.DataClasses.Person;
import Program.Common.DataClasses.Worker;
import Program.Server.InnerServerTransporter;

import java.util.LinkedList;

/**
 * Выводит количество элементов, значение поля person которых больше заданного.
 */
public class CountGtpCommand implements ICommand {
    @Override
    public Boolean inputValidate(String args) {
        String[] userData = args.replaceAll(",","").split(" ");
        if(userData.length < 5){
            System.out.println("The person fields are incorrect, should be 5.\n" +
                                "Example: 2002-02-02 12:20 180 75 passID.");
            return false;
        }
        else
            return true;
    }

    @Override
    public InnerServerTransporter handle(InnerServerTransporter transporter) {

        AddCommand addCommand = new AddCommand();
        Person person = new Person();
        String args = transporter.getArgs();
        LinkedList<Worker> WorkersData = transporter.getWorkersData();

        String[] userData = args.replaceAll(",","").split(" ");

        if(userData.length < 5){
            transporter.setMsg("The person fields are incorrect, should be 5.\n" +
                                "Example: 2002-02-02 12:20 180 75 passID.");
            return transporter;
        }
        else {
            AddCommand.PersonCreator personCreator = new AddCommand.PersonCreator();
            personCreator = addCommand.createNewPerson(userData[0], userData[1], userData[2], userData[3], userData[4], person);
            person = personCreator.getPerson();
        }

        int k = 0;
        if(person == null) {
            transporter.setMsg("Person is null, cannot be counted.");
            return transporter;
        }
        else {
            for (Worker w : WorkersData) {
                try {
                    if (compare(w.getPerson(), person) > 0)
                        k++;
                } catch (NullPointerException e) {
                    transporter.setMsg("The person field is not set for id: " + w.getId() + " .\n");
                }
            }

        }

        transporter.setMsg(String.valueOf(k));
        return transporter;
    }

    @Override
    public String getName() {
        return "count_greater_than_person";
    }

    @Override
    public String getHelp() {
        return "Returns the number of elements whose person field value is greater than the specified value.";
    }

    private static int compare(Person a, Person b){
        int aScore = 0;
        int bScore = 0;

        try {
            aScore = (int) (a.getHeight() + a.getWeight());
            bScore = (int) (b.getHeight() + b.getWeight()) ;
        }catch (NullPointerException e){
            System.out.println("Data error.");
        }
        return aScore - bScore;
    }

}
