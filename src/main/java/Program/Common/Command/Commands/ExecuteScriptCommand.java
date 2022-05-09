package Program.Common.Command.Commands;

import Program.Common.Command.CommandManager;
import Program.Common.Command.ICommand;
import Program.Common.DataClasses.Transporter;
import Program.Common.DataClasses.Worker;
import Program.Common.Serializer;
import Program.Server.InnerServerTransporter;

import java.io.*;
import java.net.DatagramPacket;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Считывает и исполнить скрипт из указанного файла. В скрипте содержатся команды в таком же виде, в котором их вводит пользователь в интерактивном режиме.
 */
public class ExecuteScriptCommand implements ICommand {

    private final CommandManager manager;

    /** Запрашивает существующий commandManager для возможности вызова у них методов help и getName.
     *
     * @param manager CommandManager для доступа к методу handle команд.
     */
    public ExecuteScriptCommand(CommandManager manager) {
        this.manager = manager;
    }

    @Override
    public Boolean inputValidate(String args) {
        if(args.equals("")){
            System.out.println("You must specify the path to the file.");
            return false;
        }
        else {
            try {
                Scanner scanner = new Scanner(new FileInputStream(args));
                return true;
            } catch (FileNotFoundException e) {
                System.out.println("The specified file does not exist.");
                return false;
            }
        }
    }

    @Override
    public InnerServerTransporter handle(InnerServerTransporter transporter) {

        Scanner scanner;
        LinkedList<Worker> localList = transporter.getWorkersData();
        String args = transporter.getArgs();

        if (args.equals("")) {
            transporter.setMsg("You must specify the path to the file.");
            return transporter;
        } else {
            try {
                scanner = new Scanner(new FileInputStream(args));
            } catch (FileNotFoundException e) {
                transporter.setMsg("The specified file does not exist.");
                return transporter;
            }
        }

        if(!recursionCheck(scanner,args)){
            transporter.setMsg("Recursion detected, script execution prohibited.");
            return transporter;
        }


        StringBuilder stringBuilder = new StringBuilder();
        try {
            while (scanner.hasNext()) {
                String line = scanner.nextLine();
                transporter.setArgs(line);
                stringBuilder.append("Command execution: ").append(line).append(".\n");
                transporter = manager.CommandHandler(transporter);
                stringBuilder.append(transporter.getMsg());
            }
        } catch (StackOverflowError e) {
            transporter.setMsg("Recursion detected, script aborted.");
            return transporter;
        }

        transporter.setMsg(String.valueOf(stringBuilder));
        transporter.setWorkersData(localList);
        return transporter;
    }
        /*

         stringBuilder.append("Script completed:\n" +
                "accept result - 1,\n" +
                "cancel - 2,\n" +
                "show result - 3.");

        while(true) {

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                switch (reader.readLine()) {
                    case "1":
                        transporter.setWorkersData(localList);
                        return transporter;
                    case "2":
                        return transporter;
                    case "3":

                        for (Worker w:localList) {
                            System.out.println(w.toString());
                        }

                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        */

    private boolean recursionCheck(Scanner scanner, String args){
        while (scanner.hasNext()) {
            String line = scanner.nextLine();
            if(line.equals("execute_script " + args))
                return false;
        }
    return true;
    }

    @Override
    public String getName() {
        return "execute_script";
    }

    @Override
    public String getHelp() {
        return "Reads and executes commands from the specified file, the command syntax is similar to user ones.";
    }
}
