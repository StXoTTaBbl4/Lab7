package Program.Common.Command;

import Program.Common.DataClasses.Worker;
import Program.Server.InnerServerTransporter;

import java.util.LinkedList;
/**
 * интерфейс, служащий гарантом того что любая команда будет содержать требуемый набор методов.
 */
public interface ICommand {

    /**
     *
     * @param args Команда и аргументы со стороны клиента.
     * @return true если ввод соотвествует базовым требованиям команды.
     */
    Boolean inputValidate(String args);

    /**
     * @param transporter объект {@link InnerServerTransporter}, сожержащий данные для выполнения команды.
     * @return объект, после его обработки командой.
     */
    //Поментять на InnerServerTransporter
    InnerServerTransporter handle(InnerServerTransporter transporter);

    /** Метод возвращает имя команды.
     *
     * @return имя команды
     */
    String getName();

    /** Метода возвращает развернутое описание команды.
     *
     * @return информация о команде
     */
    String getHelp();

}
