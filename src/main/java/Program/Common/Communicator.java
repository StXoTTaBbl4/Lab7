package Program.Common;

import Program.Common.DataClasses.Transporter;
import Program.Common.DataClasses.Worker;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.LinkedList;

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

    public boolean merge_db(Connection connection, LinkedList<Worker> WorkersData){
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

            try {
                String request = "UPDATE workers SET  name =" + name + ", x =" +x+", y ="+ y+", salary =" +salary+", start_date ="+ start_date+", end_date ="+ end_date+", position ="+ position+", birthday ="+ birthday+", height ="+ height+", weight ="+ weight+", passport_id ="+ passportID +"  WHERE (id ="+ w.getId() +"and login ="+ login+"and password ="+ password+");";
                System.out.println(request);
                boolean z;
                return z = connection.prepareStatement(request).execute();
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
                PrintStream printStream = null;
                try {
                    printStream = new PrintStream(System.out, true, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException(ex);
                }
                printStream.println(e);
                String request = "insert into workers values"+req;
                try {
                    boolean z;
                    return z = connection.prepareStatement(request).execute();
                } catch (SQLException ex) {
                    System.out.println("У " + w.getId() + " не заданы все поля.");
                }
            }
        }
        return false;
    }

    public void delete_db(Connection connection, Worker w){
            String login ="'"+ w.getLogin()+"'";
            String password ="'"+ w.getPassword()+"'";
            String req = "delete from Products where (login =" + login + "and password =" + password + " and id=" + w.getId() + " );";
            try {
                boolean z = connection.prepareStatement(req).execute();
            } catch (SQLException ex) {
                System.out.println("Delete id: "+w.getId()+" failed.");
            }

    }

}
