package crud.entities;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "opcao")
public class Opcao {

    @PrimaryKey
    @NonNull
    String id;

    @ColumnInfo(name = "value")
    String value;

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "Opcao{" +
                "id='" + id + '\'' + (value == null ? "" :
                ", value='" + value + '\'') +
                '}';
    }
}