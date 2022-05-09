package Program.Common.Command.Commands;

import Program.Common.Command.ICommand;
import Program.Common.DataClasses.Worker;
import Program.Server.InnerServerTransporter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.util.LinkedList;

/**
 * Сохраняет коллекцию в файл.
 */
public class SaveCommand implements ICommand {


    public SaveCommand() {
    }

    @Override
    public Boolean inputValidate(String args) {
       return false;
    }

    @Override
    public InnerServerTransporter handle(InnerServerTransporter transporter) {
        String path = transporter.getArgs();
        LinkedList<Worker> WorkersData = transporter.getWorkersData();

        if(!Files.exists(Paths.get(path))){
            try {
                Files.createFile(Paths.get(path));
            } catch (IOException e) {
                transporter.setMsg("Such a file already exists.");
            }
        }

        Gson gson = new GsonBuilder()
                        .setPrettyPrinting()
                        //.registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeSerializer())
                        .create();
        try(Writer fw = new OutputStreamWriter(new FileOutputStream(path))){
                fw.write(gson.toJson(WorkersData));
                fw.flush();
                transporter.setMsg("Command completed.");
            } catch (AccessDeniedException | FileNotFoundException e) {
            transporter.setMsg("Access error, unable to write.");
        }catch (IOException e) {
            e.printStackTrace();
        }

        return transporter;
    }

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getHelp() {
        return "Saves the collection to the specified file.";
    }
}
