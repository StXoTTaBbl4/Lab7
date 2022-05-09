package Program.Common;

import Program.Common.DataClasses.Transporter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Communicator {

    public void send(Transporter transporter, Serializer serializer, DatagramSocket ds ,InetAddress ip, int port) throws IOException {
        byte[] arr = serializer.serialize(transporter);
        DatagramPacket dp = new DatagramPacket(arr, arr.length, ip, port);

        ds.send(dp);
    }

    public Transporter receive(DatagramPacket dp, Serializer serializer) throws IOException, ClassNotFoundException {
        byte[] data = dp.getData();

        return (Transporter) serializer.deserialize(data);
    }
}
