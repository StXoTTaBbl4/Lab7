package Program.Server;

import Program.Common.Command.CommandManager;
import Program.Common.DataClasses.Transporter;
import Program.Common.Serializer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class PackForChannel {
    Transporter transporter;
    DatagramSocket socket;
    InnerServerTransporter innerTransporter;
    DatagramPacket incoming;
    Serializer serializer;
    CommandManager manager;
    byte[] data;

    public PackForChannel(Transporter transporter, DatagramSocket socket, InnerServerTransporter innerTransporter, DatagramPacket incoming, Serializer serializer, CommandManager manager, byte[] data) {
        this.transporter = transporter;
        this.socket = socket;
        this.innerTransporter = innerTransporter;
        this.incoming = incoming;
        this.serializer = serializer;
        this.manager = manager;
        this.data = data;
    }

    /*@Override
    public void run() {
        try {

            innerTransporter = manager.CommandHandler(innerTransporter);
            //setWorkersData(innerTransporter.getWorkersData());
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
        catch (IOException e){
            e.printStackTrace();
        }


    }

     */

}
