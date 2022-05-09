package Program.Common.DataClasses;

/**
 * Класс для создания объектов типа Coordinates;
 */
public class Coordinates {
    private Float x;//Поле не может быть null
    private Double y;//Поле не может быть null

    /**
     * @param x Поле не может быть null
     * @param y Поле не может быть null
     */
    public Coordinates(Float x, Double y) {
        this.x = x;
        this.y = y;
    }

    public Float getX() {
        return x;
    }

    public Double getY() {
        return y;
    }

    public void setX(Float x) {this.x = x;}

    public void setY(Double y) {this.y = y;}
}
