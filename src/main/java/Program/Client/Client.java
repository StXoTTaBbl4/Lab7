package Program.Client;

import Program.Common.Command.CommandManager;
import Program.Common.Communicator;
import Program.Common.DataClasses.Transporter;
import Program.Common.Serializer;

import java.io.*;
import java.net.*;

/**
 * Класс, объекты которого являются "клиентами" серверов. На его стороне не происходит непосредственного взаимодействия с коллекцией.
 */
public class Client {

    public static void main(String[] args) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        final CommandManager manager = new CommandManager();
        DatagramSocket socket = new DatagramSocket();
        Transporter transporter = new Transporter();
        Serializer serializer = new Serializer();
        Communicator communicator = new Communicator();
        String ip = null;
        int port = -1;
        try {
            //ip = args[0];
            //port = Integer.parseInt(args[1]);
            ip = "localhost";
            port = 56666;
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("Input: ip port(int)");
            System.exit(0);
        } catch (NumberFormatException e) {
            System.out.println("Port var. type - integer");
            System.exit(0);
        }

        System.out.println("Server online: " + isServerOnline(port));

        while (true) {
            //Ожидаем ввод сообщения серверу
            System.out.println("Request : ");
            String command = reader.readLine();
            transporter.setCommand(command);
            System.out.println("Login: ");
            String login = reader.readLine();
            transporter.setLogin(login);
            System.out.println("Password: ");
            String password = reader.readLine();
            transporter.setPassword(password);

            if (command.equals("exit"))
                System.exit(0);

            if(isServerOnline(port)) {
                if (manager.validate(transporter)) {
                    //Отправляем сообщение
                    communicator.send(transporter, serializer, socket, InetAddress.getByName(ip), port);

                    //буфер для получения входящих данных
                    byte[] buffer = new byte[65536];
                    DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

                    //Получаем данные
                    socket.receive(reply);
                    byte[] data = reply.getData();
                    try {
                        transporter = communicator.receive(reply, serializer);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    System.out.println("Сервер: " + reply.getAddress().getHostAddress() + ", порт: " + reply.getPort() + ", получил: " + transporter.getCommand());
                    System.out.println(transporter.getMessage());
                }
            }else
                System.out.println("Server: offline");
        }
    }

    public static boolean isServerOnline(int port) throws IOException {
        ServerSocket ss = null;
        DatagramSocket ds = null;
        try {
            ss = new ServerSocket(port);
            ss.setReuseAddress(true);
            ds = new DatagramSocket(port);
            ds.setReuseAddress(true);
            return false;
        } catch (IOException e) {
            return true;
        } finally {
            if(ds != null)
            ds.close();
            if(ss != null)
            ss.close();
        }
    }

}
