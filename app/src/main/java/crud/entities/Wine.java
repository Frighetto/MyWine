package crud.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"column", "line"}, tableName = "wines")
public class Wine {

    @NonNull
    private Integer column;

    @NonNull
    private Integer line;

    @ColumnInfo(name = "barcode")
    String barcode;

    @ColumnInfo(name = "country")
    String country;

    @ColumnInfo(name = "producer")
    String producer;

    @ColumnInfo(name = "type")
    String type;

    @ColumnInfo(name = "grape")
    String grape;

    @ColumnInfo(name = "vintage")
    String vintage;

    @ColumnInfo(name = "body")
    String body;

    @ColumnInfo(name = "alcoholic_graduation")
    String alcoholic_graduation;

    @ColumnInfo(name = "price")
    String price;

    @ColumnInfo(name = "description")
    String description;

    @ColumnInfo(name = "image")
    String image;

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Integer getLine() {
        return line;
    }

    public void setLine(Integer line) {
        this.line = line;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getGrape() {
        return grape;
    }

    public void setGrape(String grape) {
        this.grape = grape;
    }

    public String getVintage() {
        return vintage;
    }

    public void setVintage(String vintage) {
        this.vintage = vintage;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getAlcoholic_graduation() {
        return alcoholic_graduation;
    }

    public void setAlcoholic_graduation(String alcoholic_graduation) {
        this.alcoholic_graduation = alcoholic_graduation;
    }
    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Wine{" +
                "column=" + column +
                ", line=" + line + ( barcode == null ? "" :
                ", barcode='" + barcode + '\'') + (country == null ? "" :
                ", country='" + country + '\'') + (producer == null ? "" :
                ", producer='" + producer + '\'') + (type == null ? "" :
                ", type='" + type + '\'') + (grape == null ? "" :
                ", grape='" + grape + '\'') + (vintage == null ? "" :
                ", vintage='" + vintage + '\'') + (body == null ? "" :
                ", body='" + body + '\'') + (alcoholic_graduation == null ? "" :
                ", alcoholic_graduation='" + alcoholic_graduation + '\'') + (price == null ? "" :
                ", price='" + price + '\'') + (description == null ? "" :
                ", description='" + description + '\'') + (image == null ? "" :
                ", image='" + image + '\'') +
                '}';
    }
}