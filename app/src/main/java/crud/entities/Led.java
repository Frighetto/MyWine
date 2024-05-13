package crud.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;

@Entity(primaryKeys = {"column", "line"}, tableName = "leds")
public class Led {

    @NonNull
    private String column;

    @NonNull
    private String line;

    @ColumnInfo(name = "value")
    String value;

    public String getColumn() {
        return column;
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Led{" +
                "column='" + column + '\'' +
                ", line='" + line + '\'' + (value == null ? "" :
                ", value='" + value + '\'') +
                '}';
    }
}