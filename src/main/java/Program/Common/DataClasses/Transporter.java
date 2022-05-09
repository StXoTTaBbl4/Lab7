package Program.Common.DataClasses;

import java.io.Serializable;

public class Transporter implements Serializable {
    String message = "def";
    String command = "def";
    String login = null;
    String password = null;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getLogin() {return login;}

    public void setLogin(String login) {this.login = login;}

    public String getPassword() {return password;}

    public void setPassword(String password) {this.password = password;}
}
