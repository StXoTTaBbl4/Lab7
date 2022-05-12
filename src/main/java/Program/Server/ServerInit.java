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
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ServerInit {
    private LinkedList<Worker> WorkersData;
    private int port;
    private String ip;
    private Connection c;
    private PackForChannel packForChannel;

    public ServerInit(int port, String ip) {
        this.port = port;
        this.ip = ip;
    }
    public ServerInit(){}

    public static void main(String[] args){
        ServerInit server;
        try {
            //server = new ServerInit();
            server = new ServerInit(56666,"localhost");
            server.execute();
        }catch (NumberFormatException e){
            System.out.println("port: Integer");
        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("Error. Ex: 56666 or 0 for rnd port");
        }

    }

    public void initialize() {
        
        Connection connection = null;
        try{
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Lab7", "postgres", "Ltvjys1");
            setC(connection);
        }catch (SQLException e){
            System.out.println("Failed to connect to database");
            System.exit(0);
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
        //DatagramPacket incoming = new DatagramPacket(buffer, buffer.length);
        DatagramPacket incoming = null;

        System.out.println("Server started, port: " + socket.getLocalPort());
        while(true) {
            try {
                //Получаем данные
                incoming = new DatagramPacket(buffer, buffer.length);
                socket.receive(incoming);
                byte[] data = incoming.getData();
                transporter = (Transporter) serializer.deserialize(data);

                System.out.println("Сервер получил: " + transporter.getCommand() + "\n");

                innerTransporter.setWorkersData(WorkersData);
                innerTransporter.setArgs(transporter.getCommand());
                innerTransporter.setIncome(incoming);
                innerTransporter.setSocket(socket);
                innerTransporter.setLogin(transporter.getLogin());
                if(!transporter.getPassword().equals(""))
                    innerTransporter.setPassword(getSHA512Encode(transporter.getPassword()));
                else
                    innerTransporter.setPassword(transporter.getPassword());

                //setWorkersData(innerTransporter.getWorkersData());

                //Отправляем данные клиенту
                ExecutorService executor = Executors.newCachedThreadPool();
                executor.submit(new PackForChannel(transporter,socket,innerTransporter,incoming,serializer,manager,data));

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
        System.out.println("Console opened.");
        BufferedReader reader =new BufferedReader(new InputStreamReader(System.in));
        String s;
        while(true) {
            try {
                s = reader.readLine();
              /*  if (s.equals("save")){
                    Communicator communicator = new Communicator();
                    communicator.merge_db(getC(),getWorkersData());
                }

               */
                if(s.equals("shutdown")){
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

    public PackForChannel getPackForChannel() {
        return packForChannel;
    }

    public String getSHA512Encode(String passwordToHash){
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] bytes = md.digest(passwordToHash.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte aByte : bytes) {
                sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
            }
            passwordToHash = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return passwordToHash;
    }

}
