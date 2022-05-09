package Program.Server;

import Program.Common.DataClasses.Worker;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.LinkedList;

/**
 * Класс для работы с коллекцией на стороне сервера.
 */
public class InnerServerTransporter {
    LinkedList<Worker> WorkersData = null;
    String args;
    String msg = null;
    String login = null;
    String password = null;
    DatagramSocket socket;
    DatagramPacket income;

    public LinkedList<Worker> getWorkersData() {
        return WorkersData;
    }

    public void setWorkersData(LinkedList<Worker> workersData) {
        WorkersData = workersData;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getArgs() {
        return args;
    }

    public void setArgs(String args) {
        this.args = args;
    }

    public DatagramSocket getSocket() {
        return socket;
    }

    public void setSocket(DatagramSocket socket) {
        this.socket = socket;
    }

    public DatagramPacket getIncome() {
        return income;
    }

    public void setIncome(DatagramPacket income) {
        this.income = income;
    }

    public String getLogin() {return login;}

    public void setLogin(String login) {this.login = login;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}
}
