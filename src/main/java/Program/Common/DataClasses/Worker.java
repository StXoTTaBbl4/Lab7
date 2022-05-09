package Program.Common.DataClasses;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
/**
 * Класс - шаблон для элементов коллекции.
 */
public class Worker implements Comparable<Worker> {

    private String login;
    private String password;
    private Integer id; //Поле не может быть null, Значение поля должно быть больше 0, Значение этого поля должно быть уникальным, Значение этого поля должно генерироваться автоматически
    private String name; //Поле не может быть null, Строка не может быть пустой
    private Coordinates coordinates; //Поле не может быть null
    private java.time.ZonedDateTime creationDate; //Поле не может быть null, Значение этого поля должно генерироваться автоматически
    private Float salary; //Поле не может быть null, Значение поля должно быть больше 0
    private java.time.LocalDate startDate; //Поле не может быть null
    private java.time.LocalDateTime endDate; //Поле может быть null
    private Position position; //Поле может быть null
    private Person person; //Поле не может быть null

    public Worker(){}

    /**
     *
     * @param id Поле не может быть null, значение поля должно быть больше 0, Значение этого поля должно быть уникальным и генерироваться автоматически
     * @param creationDate Поле не может быть null, значение этого поля должно генерироваться автоматически
     */
    public Worker(Integer id, ZonedDateTime creationDate){
        this.creationDate = creationDate;
        this.id = id;
    }

    /**
     *
     * @param id Поле не может быть null, значение поля должно быть больше 0, Значение этого поля должно быть уникальным и генерироваться автоматически
     * @param name Поле не может быть null, строка не может быть пустой
     * @param coordinates Поле не может быть null
     * @param creationDate Поле не может быть null, значение этого поля должно генерироваться автоматически
     * @param salary Поле не может быть null, значение поля должно быть больше 0
     * @param startDate Поле не может быть null
     * @param endDate Поле может быть null
     * @param position Поле может быть null
     * @param person Поле не может быть null
     */
    public Worker(String login, String password ,Integer id, String name, Coordinates coordinates, ZonedDateTime creationDate, Float salary, LocalDate startDate, LocalDateTime endDate, Position position, Person person) {
        this.login = login;
        this.password = password;
        this.id = id;
        this.name = name;
        this.coordinates = coordinates;
        this.creationDate = creationDate;
        this.salary = salary;
        this.startDate = startDate;
        this.endDate = endDate;
        this.position = position;
        this.person = person;
    }

    public void setId(Integer id) {this.id = id; }

    public void setName(String name) {
        this.name = name;
    }

    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    public void setCreationDate(ZonedDateTime creationDate) {this.creationDate = creationDate;}

    public void setSalary(Float salary) {
        this.salary = salary;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public ZonedDateTime getCreationDate() {
        return creationDate;
    }

    public Float getSalary() {
        return salary;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public Position getPosition() {
        return position;
    }

    public Person getPerson() {
        return person;
    }

    public String getLogin() {return login;}

    public String getPassword() {return password;}

    /**
     *
     * @return Строковое представление объекта класса Worker.
     */
    @Override
    public String toString() {
        return "Creator: "+ login +"\n" +
                "Worker " + name + ", id: " + id +"\n"+
                "coordinates: x " + coordinates.getX() + ", y " + coordinates.getY() +"\n" +
                "creationDate: " + creationDate + "\n" +
                "salary: " + salary +"\n" +
                "startDate: " + startDate +"\n" +
                "endDate: " + endDate + "\n" +
                "position: " + position + "\n" + person;
    }

    /** Метод сравнивает объекты на основе их полей ID. Применяется для упорядочивания объектов коллекции.
     *
     * @param o Объект, с котором нужно сравнить.
     * @return Возвращает число, которое определяет, какой из объектов больше.
     */
    @Override
    public int compareTo(Worker o) {
        return this.getId() - o.getId();
    }
}
