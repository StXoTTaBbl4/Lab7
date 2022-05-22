package Program.Server;

import Program.Common.Command.CommandManager;
import Program.Common.DataClasses.Transporter;
import Program.Common.Serializer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.concurrent.locks.ReentrantLock;

public class PackForChannel {
    Transporter transporter;
    DatagramSocket socket;
    InnerServerTransporter innerTransporter;
    DatagramPacket incoming;
    Serializer serializer;
    CommandManager manager;
    byte[] data;
    ReentrantLock locker;

    public PackForChannel(Transporter transporter, DatagramSocket socket, InnerServerTransporter innerTransporter, DatagramPacket incoming, Serializer serializer, CommandManager manager, byte[] data, ReentrantLock locker) {
        this.transporter = transporter;
        this.socket = socket;
        this.innerTransporter = innerTransporter;
        this.incoming = incoming;
        this.serializer = serializer;
        this.manager = manager;
        this.data = data;
        this.locker = locker;
    }

}
