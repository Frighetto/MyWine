package crud.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import crud.entities.Led;

@Dao
public interface LedDao {

    @Query("SELECT * FROM leds")
    List<Led> getAll();

    @Query("SELECT * FROM leds WHERE `column` = :column AND line = :line")
    Led findByName(String column, String line);

    @Insert
    void insertAll(Led... leds);

    @Update
    void updateAll(Led... leds);

    @Delete
    void delete(Led led);
}