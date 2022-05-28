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
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.TimeZone;

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
        for(Worker w : WorkersData){
            String login = w.getLogin();
            String password = w.getPassword();
            String name = w.getName();
            float x = w.getCoordinates().getX();
            double y = w.getCoordinates().getY();

            String cd_raw = String.valueOf(w.getCreationDate()).replace("T"," ");
            int in1 = cd_raw.indexOf("+");
            String creation_date = cd_raw.substring(0,19) + cd_raw.substring(in1,in1+3);

            float salary = w.getSalary();
            String start_date = w.getStartDate().toString();
            String end_date = String.valueOf(w.getEndDate()).replace("T", " ");
            String position;
            if(w.getPosition() == null)
                position = "default";
            else
                position =  w.getPosition().toString();
            String birthday = String.valueOf(w.getPerson().getBirthday()).replace("T"," ");
            int height = w.getPerson().getHeight();
            float weight = w.getPerson().getWeight();
            String passportID = w.getPerson().getPassportID();

            try {
                String request = "insert into workers values (?,?,default,?,?,?,'"+creation_date+"',?,'"+start_date+"','"+end_date+"',?,'"+birthday+"',?,?,?,'false')";
                PreparedStatement statement = connection.prepareStatement(request);
                statement.setString(1,login);
                statement.setString(2,password);
                statement.setString(3,name);
                statement.setFloat(4,x);
                statement.setDouble(5,y);
                statement.setFloat(6,salary);
                statement.setString(7,position);
                statement.setInt(8,height);
                statement.setFloat(9,weight);
                statement.setString(10,passportID);
                statement.executeUpdate();
                    return true;
            } catch (SQLException e) {
                String request = "UPDATE workers SET  name =?, x =?, y =? , salary = ?, start_date ='"+start_date+"' , end_date = '"+end_date+"', position = ?, birthday = '"+birthday+"', height = ?, weight = ?, passport_id = ?  WHERE (id = ? and login = ? and password = ?);";
                PrintStream printStream;
                try {
                    printStream = new PrintStream(System.out, true, "UTF-8");
                } catch (UnsupportedEncodingException ex) {
                    throw new RuntimeException(ex);
                }
                printStream.println(e);

                try {
                    PreparedStatement statement = connection.prepareStatement(request);
                    statement.setString(1,name);
                    statement.setFloat(2,x);
                    statement.setDouble(3,y);
                    statement.setFloat(4,salary);
                    statement.setString(5,position);
                    statement.setInt(6,height);
                    statement.setFloat(7,weight);
                    statement.setString(8,passportID);
                    statement.setInt(9,w.getId());
                    statement.setString(10,login);
                    statement.setString(11,password);
                    statement.executeUpdate();
                    return true;
                } catch (SQLException ex) {
                    System.out.println("У " + w.getId() + " не заданы все поля.");
                    return false;
                }
            }
        }
        return false;
    }

    public boolean delete_db(Connection connection, Worker w){
            String login ="'"+ w.getLogin()+"'";
            String password ="'"+ w.getPassword()+"'";
            String req = "delete from workers where (login =" + login + "and password =" + password + " and id=" + w.getId() + " );";
            try {
                connection.prepareStatement(req).execute();
                return true;
            } catch (SQLException ex) {
                System.out.println("Delete id: "+w.getId()+" failed.");
                return false;
            }

    }

}
