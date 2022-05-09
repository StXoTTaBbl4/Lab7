package Program.Common.Command.Commands;

import Program.Common.Command.ICommand;
import Program.Common.DataClasses.Coordinates;
import Program.Common.DataClasses.Person;
import Program.Common.DataClasses.Position;
import Program.Common.DataClasses.Worker;
import Program.Server.InnerServerTransporter;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedList;

/**
 * Обновляет значение элемента коллекции, id которого равен заданному.
 */
public class UpdateIdCommand implements ICommand {


    @Override
    public Boolean inputValidate(String args) {
        try {
            return !args.equals("");
        }catch (NullPointerException e){
            return false;
        }
    }

    @Override
    public InnerServerTransporter handle(InnerServerTransporter transporter) {
        String[] data = transporter.getArgs().replaceAll(",", "").split(" ");
        LinkedList<Worker> WorkerData = transporter.getWorkersData();

        Worker worker = null;
        for (Worker w: WorkerData) {
            if(w.getId() == Integer.parseInt(data[0])) {
                worker = w;
                WorkerData.remove(w);
            }
        }

        if(worker == null) {
            transporter.setMsg("No worker with this ID was found.");
        }
        else {
            switch (data[1]){
                case "name":
                    try {
                        worker.setName(data[2]);
                    }catch (ArrayIndexOutOfBoundsException e){
                        transporter.setMsg("The name field cannot be empty.");
                    }
                    break;
                case"coordinates":
                    try {
                        Coordinates coordinates = new Coordinates(Float.parseFloat(data[2]),Double.parseDouble(data[3]));
                        worker.setCoordinates(coordinates);
                    }
                    catch (ArrayIndexOutOfBoundsException e){
                        transporter.setMsg("The x or y field cannot be empty.");
                    }
                    catch (NumberFormatException e){
                        transporter.setMsg("Invalid data type for x or y.");
                    }
                    break;
                case"salary":
                    try {
                        if(Float.parseFloat(data[2]) > 0) {
                            worker.setSalary(Float.parseFloat(data[2]));
                        }
                        else {
                            transporter.setMsg("Salary cannot be 0.");
                        }
                    }
                    catch (NumberFormatException e){
                        transporter.setMsg("Invalid data type for salary.");
                    }
                    catch (ArrayIndexOutOfBoundsException t){
                        worker.setSalary(null);
                    }
                    break;
                case"startDate":
                    try {
                        String[] sd = data[2].split("-");
                        worker.setStartDate(LocalDate.of(Integer.parseInt(sd[0]),
                                Integer.parseInt(sd[1]),
                                Integer.parseInt(sd[2])));
                    }
                    catch (DateTimeException | ArrayIndexOutOfBoundsException | NumberFormatException e){
                        transporter.setMsg("Incorrect data. Format: Y-M-D.");
                    }
                    break;
                case"endDate":
                    try {
                        String[] ed = data[2].split("-");
                        String[] et = data[3].split(":");

                        worker.setEndDate(LocalDateTime.of(Integer.parseInt(ed[0]),
                                Integer.parseInt(ed[1]),
                                Integer.parseInt(ed[2]),
                                Integer.parseInt(et[0]),
                                Integer.parseInt(et[1])));
                    }
                    catch (DateTimeException | NumberFormatException e){
                        transporter.setMsg("Incorrect data. Format: Y-M-D H:M.");
                    }
                    catch ( ArrayIndexOutOfBoundsException e){
                        worker.setEndDate(null);
                    }
                    break;
                case"position":
                    try {
                        if(data[2].equals("\"\"")){
                            worker.setPosition(null);
                        }else {
                            worker.setPosition(Position.valueOf(data[2]));
                        }
                    }catch (ArrayIndexOutOfBoundsException e){
                        worker.setPosition(null);
                    }
                    catch (IllegalArgumentException e){
                        transporter.setMsg("Incorrect data. Example: MANAGER.");
                    }
                    break;
                case"person":
                    Person person = new Person();
                    //birthday
                    try {
                        if(!data[2].equals("\"\"")) {
                            String[] edPerson = data[2].split("-");
                            String[] etPerson = data[3].split(":");
                            LocalDateTime localDateTime = LocalDateTime.of(Integer.parseInt(edPerson[0]),
                                    Integer.parseInt(edPerson[1]),
                                    Integer.parseInt(edPerson[2]),
                                    Integer.parseInt(etPerson[0]),
                                    Integer.parseInt(etPerson[1]));
                            person.setBirthday(localDateTime);
                        }else
                            person.setBirthday(null);
                    }
                    catch (DateTimeException | ArrayIndexOutOfBoundsException | NumberFormatException e ) {
                        transporter.setMsg("Invalid birthday field data. Format: Y-M-D H:M.");
                    }
                    //height
                    try {
                        if(Integer.parseInt(data[4]) > 0)
                        person.setHeight(Integer.parseInt(data[4]));
                        else {
                            transporter.setMsg("The height parameter must be greater than 0.");
                        }
                    }
                    catch (NumberFormatException e){
                        transporter.setMsg("Invalid data type for salary.");
                    }
                    catch (ArrayIndexOutOfBoundsException e){
                        transporter.setMsg("The salary field cannot be empty.");
                    }
                    //weight
                    try {
                        if(data[5].equals("\"\""))
                            person.setWeight(null);
                        else if(Float.parseFloat(data[5]) > 0)
                            person.setWeight(Float.parseFloat(data[5]));
                        else {
                            transporter.setMsg("Weight cannot be 0.");
                        }
                    }
                    catch (NumberFormatException e){
                        transporter.setMsg("Invalid data type for weight.");
                    }
                    catch (ArrayIndexOutOfBoundsException e){
                        transporter.setMsg("The weight field cannot be empty.");
                    }
                    //passportID
                    try{
                        if(data[6].length() < 1)
                            throw new IllegalArgumentException();
                        else if(data[6].length() > 29 || data[6].length() < 4)
                            throw new IllegalArgumentException();
                        else
                            person.setPassportID(data[6]);
                    }catch (IllegalArgumentException | ArrayIndexOutOfBoundsException e){
                        transporter.setMsg("The passportID field must not be empty or\n " +
                                            "go beyond 4 - 29 characters.");
                    }
                    worker.setPerson(person);
                    break;
                default:
                    transporter.setMsg("Field not found");
                    break;
            }
            WorkerData.add(worker);
            transporter.setMsg("Command completed.");
            transporter.setWorkersData(WorkerData);
        }
        return transporter;
    }

    @Override
    public String getName() {
        return "update";
    }

    @Override
    public String getHelp() {
        return "Updates the settings of the collection element with the specified ID.";
    }
}