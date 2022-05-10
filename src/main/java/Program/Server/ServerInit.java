package Program.Server;

import Program.Common.CollectionInit.Initializer;
import Program.Common.Command.CommandManager;
import Program.Common.DataClasses.Transporter;
import Program.Common.DataClasses.Worker;
import Program.Common.Serializer;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerInit {
    private LinkedList<Worker> WorkersData;
    private int port;
    private String ip;
    private Connection c;

    public ServerInit(int port, String ip) {
        this.port = port;
        this.ip = ip;
    }
    public ServerInit(){}

    void initialize() {
        Connection connection = null;
        try{
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Lab7", "postgres", "Ltvjys1");
            setC(connection);
        }catch (SQLException e){
            System.out.println("Failed to connect to database");
        }

        final CommandManager manager = new CommandManager(connection);
        Initializer initializer = new Initializer();
        assert connection != null;
        WorkersData = initializer.initializeCollection(connection);

        if (WorkersData == null) {
            WorkersData = new LinkedList<>();
        } else {
            for (Worker worker : WorkersData) {
                initializer.DataChecker(worker);
            }
        }

        setWorkersData(WorkersData);

        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(port);
        }catch (SocketException e){
            System.out.println("Failed to create socket.");
            System.exit(0);
        }

        byte[] buffer = new byte[65536];
        Serializer serializer = new Serializer();
        Transporter transporter = new Transporter();
        InnerServerTransporter innerTransporter = new InnerServerTransporter();
        DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);

        System.out.println("Server started, port: " + socket.getLocalPort());

        while(true) {
            try {
                //Получаем данные
                socket.receive(incoming);
                byte[] data = incoming.getData();
                transporter = (Transporter) serializer.deserialize(data);

                System.out.println("Сервер получил: " + transporter.getCommand() + "\n");

                innerTransporter.setWorkersData(WorkersData);
                innerTransporter.setArgs(transporter.getCommand());
                innerTransporter.setIncome(incoming);
                innerTransporter.setSocket(socket);
                innerTransporter.setLogin(transporter.getLogin());
                innerTransporter.setPassword(getSHA512Encode(transporter.getPassword()));
                innerTransporter = manager.CommandHandler(innerTransporter);
                setWorkersData(innerTransporter.getWorkersData());

                transporter.setMessage(innerTransporter.getMsg());
                //Отправляем данные клиенту
                data = serializer.serialize(transporter);
                DatagramPacket dp = new DatagramPacket(data, data.length, incoming.getAddress(), incoming.getPort());
                socket.send(dp);
            }catch (SocketException e){
                transporter.setMessage("A program execution error occurred, message was not generated.");
                byte[] data;
                try {
                    data = serializer.serialize(transporter);
                    DatagramPacket dp = new DatagramPacket(data, data.length, incoming.getAddress(), incoming.getPort());
                    socket.send(dp);
                } catch (IOException ex) {ex.printStackTrace();}
            }
            catch (IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }

    }

    public void consoleMonitor() {
        boolean functionAdded = false;
        System.out.println("Console opened.");
        BufferedReader reader =new BufferedReader(new InputStreamReader(System.in));
        String s;
        while(true) {
            try {
                s = reader.readLine();
                if (s.equals("save") && !functionAdded){
                    try {
                        Statement statement = getC().createStatement();
                        statement.executeUpdate(function);
                        functionAdded = true;
                    }catch (SQLException e){
                        PrintStream printStream = new PrintStream(System.out, true, "UTF-8");
                        printStream.println(e);
                        System.out.println("Function add error in ServerInit or already exists");
                    }

                    LinkedList<Worker> toTable = getWorkersData();
                    String sql = null;
                    for(Worker w : WorkersData){
                        String login = w.getLogin();
                        String password = w.getPassword();
                        String name = w.getName();
                        String x = String.valueOf(w.getCoordinates().getX());
                        String y = String.valueOf(w.getCoordinates().getY());
                        String creation_date = String.valueOf(w.getCreationDate()).replace("T"," ");
                        String salary = String.valueOf(w.getSalary());
                        String start_date = String.valueOf(w.getStartDate());
                        String end_date = String.valueOf(w.getEndDate()).replace("T", " ");
                        String position = String.valueOf(w.getPosition());
                        String birthday = String.valueOf(w.getPerson().getBirthday()).replace("T"," ");
                        String height = String.valueOf(w.getPerson().getHeight());
                        String weight = String.valueOf(w.getPerson().getWeight());
                        String passportID = w.getPerson().getPassportID();
                        String req = "select merge_db(" +
                                login + ", " +
                                password + ", " +
                                "default, " +
                                name + ", " +
                                x + ", " +
                                y + ", " +
                                creation_date + ", " +
                                salary + ", " +
                                start_date + ", " +
                                end_date + ", " +
                                position + ", " +
                                birthday + ", " +
                                height + ", " +
                                weight + ", " +
                                passportID  + ")";

                        System.out.println(req);

                        Statement statement;
                        try {
                            boolean z = getC().prepareStatement(req).execute();
                        } catch (SQLException e) {
                            PrintStream printStream = new PrintStream(System.out, true, "UTF-8");
                            printStream.println(e);
                            System.out.println("У " + w.getId() + "не заданы все поля.");
                        }
                    }
                }
                else if(s.equals("shutdown")){
                    System.exit(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void execute(){
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        executorService.submit(this::consoleMonitor);
        executorService.submit(this::initialize);

        executorService.shutdown();
    }

    private LinkedList<Worker> getWorkersData() {
        return WorkersData;
    }

    public void setWorkersData(LinkedList<Worker> data) {
        this.WorkersData = data;
    }

    private void setC(Connection c) {this.c = c;}

    private Connection getC() {return c;}

    final static private String function = "CREATE FUNCTION merge_db(t_login text,    \n" +
            "             t_password text,    \n" +
            "             t_id int,    \n" +
            "             t_name     text,    \n" +
            "             t_x         real,    \n" +
            "             t_y double precision,    \n" +
            "             t_creation_date timestamp with time zone,    \n" +
            "             t_salary real,    \n" +
            "             t_start_date date,    \n" +
            "             t_end_date timestamp,    \n" +
            "             t_position text,    \n" +
            "             t_birthday timestamp,    \n" +
            "             t_height int,    \n" +
            "             t_weight real,    \n" +
            "             t_passport_id text ) RETURNS VOID AS    \n" +
            "         $$    \n" +
            "         BEGIN    \n" +
            "             LOOP    \n" +
            "                 UPDATE workers SET  name = t_name, x = t_x, y = t_y, salary = t_salary, start_date = t_start_date, end_date = t_end_date, position = t_position, birthday = t_birthday, height = t_height, weight = t_weight, passport_id = t_passport_id  WHERE id = t_id, login = t_login, password = t_password;\n" +
            "                 IF found THEN    \n" +
            "                     RETURN;    \n" +
            "                 END IF;    \n" +
            "             \n" +
            "                 BEGIN    \n" +
            "                     INSERT INTO workers(login,\n" +
            "\t\t\t\t\t\t\t\t\t\t password,\n" +
            "\t\t\t\t\t\t\t\t\t\t id,\n" +
            "\t\t\t\t\t\t\t\t\t\t name,\n" +
            "\t\t\t\t\t\t\t\t\t\t x,\n" +
            "\t\t\t\t\t\t\t\t\t\t y,\n" +
            "\t\t\t\t\t\t\t\t\t\t creation_date,\n" +
            "\t\t\t\t\t\t\t\t\t\t salary,\n" +
            "\t\t\t\t\t\t\t\t\t\t start_date,\n" +
            "\t\t\t\t\t\t\t\t\t\t end_date,\n" +
            "\t\t\t\t\t\t\t\t\t\t position,\n" +
            "\t\t\t\t\t\t\t\t\t\t birthday,\n" +
            "\t\t\t\t\t\t\t\t\t\t height,\n" +
            "\t\t\t\t\t\t\t\t\t\t weight,\n" +
            "\t\t\t\t\t\t\t\t\t\t passport_id) VALUES (t_login,    \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tt_password,    \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tt_id,    \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tt_name,    \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tt_x,    \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tt_y,    \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tt_creation_date,\n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tt_salary,    \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tt_start_date,  \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tt_end_date,    \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tt_position,    \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tt_birthday,    \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tt_height,    \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tt_weight,    \n" +
            "\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\tt_passport_id);" +
            "                     RETURN;    \n" +
            "                 EXCEPTION WHEN unique_violation THEN    \n" +
            "                 END;    \n" +
            "             END LOOP;    \n" +
            "         END;    \n" +
            "         $$    \n" +
            "         LANGUAGE plpgsql; \n";

    public String getSHA512Encode(String passwordToHash){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++){
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            passwordToHash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return passwordToHash;
    }
}
