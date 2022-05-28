package Program.Common.CollectionInit;

import Program.Common.DataClasses.Coordinates;
import Program.Common.DataClasses.Person;
import Program.Common.DataClasses.Position;
import Program.Common.DataClasses.Worker;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.*;
import java.util.Collections;
import java.util.LinkedList;
//1033424
/**
 * Класс, используемый для инициализации коллекции и проверке ее элементов при запуске программы.
 */
public class Initializer {

    /** Метод для проверки полей объекта на соответствие требованиям.
     *
     * @param worker Проверяемый объект.
     */
    public void DataChecker(Worker worker){
        if(worker.getName() == null || worker.getName().equals(""))
            System.out.printf("У работника с id: %s не задано имя \n", worker.getId());
        if(worker.getCoordinates() == null)
            System.out.printf("У работника с id: %s не заданы координаты \n", worker.getId());
        if(worker.getCoordinates().getX() == null)
            System.out.printf("У работника с id: %s не задана координата X\n", worker.getId());
        if(worker.getCoordinates().getY() == null)
            System.out.printf("У работника с id: %s не задана координата Y\n", worker.getId());
        if(worker.getCreationDate()== null)
            System.out.printf("У работника с id: %s не задано время создания файла \n", worker.getId());
        if(worker.getSalary() == null || worker.getSalary() <= 0)
            System.out.printf("У работника с id: %s не установлена зарплата \n", worker.getId());
        if(worker.getStartDate() == null)
            System.out.printf("У работника с id: %s не задано поле startDate \n", worker.getId());
        if(worker.getPerson() == null)
            System.out.printf("У работника с id: %s не задано поле person \n", worker.getId());
        if(worker.getPerson().getHeight() <= 0)
            System.out.printf("У работника с id: %s некорректно задано поле height(должно быть >0) \n", worker.getId());
        if(worker.getPerson().getWeight() <= 0)
            System.out.printf("У работника с id: %s некорректно задано поле weight(должно быть >0) \n", worker.getId());
        if(worker.getPerson().getPassportID().length() > 29 || worker.getPerson().getPassportID().length() < 4)
            System.out.printf("У работника с id: %s некорректно задано поле passportID(3<x<30) \n", worker.getId());
    }

    /** Метод для загрузки коллекции из базы данных.
     * @param c Подключение к конкретной БД.
     * @return Коллекция типа LinkedList.
     */
    public LinkedList<Worker> initializeCollection(Connection c){
        LinkedList<Worker> WorkersData = new LinkedList<>();
        Statement stmt;

        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM WORKERS;");
            while (rs.next()) {
                String login = rs.getString("login");
                String password = rs.getString("password");
                int id = rs.getInt("id");
                String name = rs.getString("name");
                float x = rs.getFloat("x");
                double y = rs.getDouble("y");

                String cd = rs.getString("creation_date");
                ZonedDateTime creationDate = ZDTparser(cd);

                float salary = rs.getFloat("salary");
                LocalDate startDate = LDparser(rs.getString("start_date").split("-"));

                LocalDateTime endDate = LDTparser(rs.getString("end_date"));
                Position position = null;
                try{
                    position = Position.valueOf(rs.getString("w_position"));
                }catch (Exception ignored){}
                LocalDateTime birthday = LDTparser(rs.getString("birthday"));
                int height = rs.getInt("height");
                float weight = rs.getFloat("weight");
                String passportID = rs.getString("passport_id");

                Worker worker = new Worker(login,
                        password,
                        id,
                        name,
                        new Coordinates(x,y),
                        creationDate,
                        salary,
                        startDate,
                        endDate,
                        position,
                        new Person(birthday, height, weight, passportID));
                WorkersData.add(worker);
            }
            rs.close();
        }catch (SQLException e){
            PrintStream printStream;
            try {
                printStream = new PrintStream(System.out, true, "UTF-8");
            } catch (UnsupportedEncodingException ex) {
                throw new RuntimeException(ex);
            }
            printStream.println(e);
            String sql = "create table workers(" +
                    "login text not null unique," +
                    "password text," +
                    "id serial primary key unique," +
                    "name text not null," +
                    "x real not null," +
                    "y double precision not null," +
                    "creation_date timestamp with time zone not null," +
                    "salary real not null check(salary >0)," +
                    "start_date date not null," +
                    "end_date timestamp default null," +
                    "position text default null," +
                    "birthday timestamp default null," +
                    "height int check(height >0)," +
                    "weight real check(weight >0 or weight = null)," +
                    "passport_id text check(length(passport_id) > 3 and length(passport_id) < 30) not null," +
                    "isBlocked boolean default false);";
            try {
                Statement statement = c.createStatement();
                statement.executeUpdate(sql);
                statement.close();
            }catch (SQLException ex){
                try {
                    printStream = new PrintStream(System.out, true, "UTF-8");
                } catch (UnsupportedEncodingException exx) {
                    throw new RuntimeException(exx);
                }
                printStream.println(ex);
                System.out.println("Database creation fail.");
            }
        }
        Collections.sort(WorkersData);
        return WorkersData;
    }

    /**
     * Метод для парсинга ZoneDateTime объектов
     * @param zdt объект класса {@link ZonedDateTime}
     * @return  строковое представление объекта.
     */
    private ZonedDateTime ZDTparser(String zdt){
        String[] s = zdt.split(" ");
        String[] time = s[1].split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);
        int second = Integer.parseInt(time[2].substring(0,time[2].indexOf("+")));

        return ZonedDateTime.of(LDparser(s[0].split("-")), LocalTime.of(hour,minute,second), ZoneId.of(time[2].substring(time[2].indexOf("+"))));
    }

    /**
     * Метод для парсинга LocaleDate объектов
     * @param ld объект класса {@link LocalDate}
     * @return строковое представление объекта.
     */
    private LocalDate LDparser(String[] ld){
        return LocalDate.of(Integer.parseInt(ld[0]), Integer.parseInt(ld[1]), Integer.parseInt(ld[2]));
    }

    /**
     * Метод для парсинга LocaleDate объектов
     * @param str объект класса {@link LocalDateTime}
     * @return строковое представление объекта.
     */
    private LocalDateTime LDTparser(String str){
        String[] s = str.split(" ");
        String[] time = s[1].split(":");
        int hour = Integer.parseInt(time[0]);
        int minute = Integer.parseInt(time[1]);
        int second = Integer.parseInt(time[2]);
        return LocalDateTime.of(LDparser(s[0].split("-")), LocalTime.of(hour,minute,second));
    }
}
