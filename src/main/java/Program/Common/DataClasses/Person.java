package Program.Common.DataClasses;

import java.time.LocalDateTime;
/**
 * Класс для создания объектов типа Person;
 */
public class Person {
    private java.time.LocalDateTime birthday;//Поле может быть null
    private int height;//Значение поля должно быть больше 0
    private Float weight; //Поле может быть null, Значение поля должно быть больше 0
    private String passportID; //Длина строки должна быть не меньше 4, Строка не может быть пустой, Длина строки не должна быть больше 29, Поле не может быть null

    public Person(){}

    /**
     * @param birthday Поле может быть null.
     * @param height Значение поля должно быть больше 0.
     * @param weight Поле может быть null, Значение поля должно быть больше 0.
     * @param passportID Длина строки должна быть не меньше 4, Строка не может быть пустой, Длина строки не должна быть больше 29, Поле не может быть null.
     */
    public Person(LocalDateTime birthday, int height, Float weight, String passportID) {
        this.birthday = birthday;
        this.height = height;
        this.weight = weight;
        this.passportID = passportID;
    }

    public int getHeight() {
        return height;
    }

    public Float getWeight() {
        return weight;
    }

    public String getPassportID() {
        return passportID;
    }

    public void setBirthday(LocalDateTime birthday) {
        this.birthday = birthday;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public void setWeight(Float weight) {
        this.weight = weight;
    }

    public void setPassportID(String passportID) {
        this.passportID = passportID;
    }

    /** Выводит данные об объекте в виде
     * "Birthday:
     * "Height:
     * "Weight:
     * "Passport ID:
     *
     * @return Строковое представление объекта класса Person
     */
    @Override
    public String toString() {
        return "Birthday: " + birthday + "\n" +
                "Height: " + height + "\n" +
                "Weight: " + weight + "\n" +
                "Passport ID: " + passportID + "\n";
    }
}
