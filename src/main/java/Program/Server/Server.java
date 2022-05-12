package Program.Server;

/**
 * Класс для запуска сервера и его консоли {@link ServerInit#initialize()}, {@link ServerInit#consoleMonitor()}.
 */
@Deprecated
public class Server {
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

}
