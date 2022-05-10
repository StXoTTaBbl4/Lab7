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

                    LinkedList<Worker> toTable = getWorkersData();
                    String sql = null;
                    for(Worker w : WorkersData){
                        String login ="'"+ w.getLogin()+"'";
                        String password ="'"+ w.getPassword()+"'";
                        String name ="'"+ w.getName()+"'";
                        String x = String.valueOf(w.getCoordinates().getX());
                        String y = String.valueOf(w.getCoordinates().getY());

                        String cd_raw = String.valueOf(w.getCreationDate()).replace("T"," ");
                        int in1 = cd_raw.indexOf("+");
                        String creation_date ="'"+ cd_raw.substring(0,19) + cd_raw.substring(in1,in1+3)+"'";

                        String salary = String.valueOf(w.getSalary());
                        String start_date ="'"+ w.getStartDate() +"'";
                        String end_date = "'"+String.valueOf(w.getEndDate()).replace("T", " ")+"'";
                        String position = "'"+ w.getPosition() +"'";
                        String birthday = "'"+String.valueOf(w.getPerson().getBirthday()).replace("T"," ")+"'";
                        String height = String.valueOf(w.getPerson().getHeight());
                        String weight = String.valueOf(w.getPerson().getWeight());
                        String passportID = "'"+w.getPerson().getPassportID()+"'";


                        Statement statement;
                        try {
                            String request = "UPDATE workers SET  name =" + name + ", x =" +x+", y ="+ y+", salary =" +salary+", start_date ="+ start_date+", end_date ="+ end_date+", position ="+ position+", birthday ="+ birthday+", height ="+ height+", weight ="+ weight+", passport_id ="+ passportID +"  WHERE (id ="+ w.getId() +"and login ="+ login+"and password ="+ password+");";
                            System.out.println(request);
                            boolean z = getC().prepareStatement(request).execute();
                        } catch (SQLException e) {
                            String req = "(" +
                                    "'" + login +"'" + ", " +
                                    "'"+password+"'" + ", " +
                                    "default, " +
                                    "'"+name+"'" + ", " +
                                    x + ", " +
                                    y + ", " +
                                    "'"+creation_date+"'" + ", " +
                                    salary + ", " +
                                    "'"+start_date+"'" + ", " +
                                    "'"+end_date+"'" + ", " +
                                    "'"+position+"'" + ", " +
                                    "'"+birthday+"'" + ", " +
                                    height + ", " +
                                    weight + ", " +
                                    "'"+passportID+"');";
                            System.out.println("insert into workers values"+req);
                            PrintStream printStream = new PrintStream(System.out, true, "UTF-8");
                            printStream.println(e);
                            String request = "insert into workers values"+req;
                            try {
                                boolean z = getC().prepareStatement(request).execute();
                            } catch (SQLException ex) {
                                System.out.println("У " + w.getId() + " не заданы все поля.");
                            }

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
