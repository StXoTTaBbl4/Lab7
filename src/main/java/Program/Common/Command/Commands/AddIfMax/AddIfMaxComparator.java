package Program.Common.Command.Commands.AddIfMax;

import Program.Common.DataClasses.Worker;

import java.util.Comparator;

/**
 * Класс - компаратор для команды add_if_max, позволяет проводить сравнение объектов класса Worker.
 */
public class AddIfMaxComparator implements Comparator<Worker> {
    @Override
    public int compare(Worker a, Worker b) {
        int aScore;
        int bScore;
        try {
            aScore = (int) (a.getSalary() + a.getPerson().getWeight() + a.getPerson().getHeight());
        }
        catch (NullPointerException e){
            aScore = 0;
        }
        try {
            bScore = (int) (b.getSalary() + b.getPerson().getWeight() + b.getPerson().getHeight());
        }
        catch (NullPointerException e){
            bScore = 0;
        }
        return Integer.compare(aScore - bScore, 0);
    }
}
