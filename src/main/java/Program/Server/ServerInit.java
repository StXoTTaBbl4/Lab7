package Program.Server;

import Program.Common.CollectionInit.Initializer;
import Program.Common.Command.CommandManager;
import Program.Common.Command.Commands.SaveCommand;
import Program.Common.DataClasses.Transporter;
import Program.Common.DataClasses.Worker;
import Program.Common.Serializer;
import org.postgresql.jdbc.PgConnection;

import java.io.*;
import java.net.*;
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
                innerTransporter.setPassword(transporter.getPassword());
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
        System.out.println("Console opened.");
        BufferedReader reader =new BufferedReader(new InputStreamReader(System.in));
        String s;
        while(true) {
            try {
                s = reader.readLine();
                if (s.equals("save")){
                    String function = "CREATE FUNCTION merge_db(key INT, data TEXT) RETURNS VOID AS" +
                            "$$" +
                            "BEGIN" +
                            "    LOOP" +
                            "        UPDATE workers SET name = t_name WHERE id = t_id;" +
                            "        IF found THEN" +
                            "            RETURN;" +
                            "        END IF;" +
                            "" +
                            "        BEGIN" +
                            "            INSERT INTO workers(id,name) VALUES (t_id, t_name);" +
                            "            RETURN;" +
                            "        EXCEPTION WHEN unique_violation THEN" +
                            "        END;" +
                            "    END LOOP;" +
                            "END;" +
                            "$$" +
                            "LANGUAGE plpgsql;";
                    try {
                        Statement statement = getC().createStatement();
                        statement.execute(function);
                    }catch (SQLException e){
                        System.out.println("Function add error in ServerInit");
                    }

                    LinkedList<Worker> toTable = getWorkersData();
                    String sql = null;
                    for(Worker w : WorkersData){
                        String req = "select merge_db()";



                        Statement statement;
                        try {
                            int z = getC().prepareStatement(req).executeUpdate();
                        } catch (SQLException e) {
                            e.printStackTrace();
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
}
