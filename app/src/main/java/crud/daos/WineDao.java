package crud.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import crud.entities.Wine;

@Dao
public interface WineDao {

    @Query("SELECT * FROM wines")
    List<Wine> getAll();

    @Query("SELECT * FROM wines WHERE `barcode` = :barcode LIMIT 1")
    Wine findByBarcode(String barcode);

    @Query("SELECT * FROM wines WHERE `column` = :column AND line = :line")
    Wine findByPrimaryKey(Integer column, Integer line);

    @Query("SELECT DISTINCT country FROM wines WHERE country IS NOT NULL AND country <> '' ORDER BY country")
    String[] countries();

    @Query("SELECT DISTINCT producer FROM wines WHERE producer IS NOT NULL AND producer <> '' ORDER BY producer")
    String[] producers();

    @Query("SELECT DISTINCT type FROM wines WHERE type IS NOT NULL AND type <> '' ORDER BY type")
    String[] types();

    @Query("SELECT DISTINCT grape FROM wines WHERE grape IS NOT NULL AND grape <> '' ORDER BY grape")
    String[] grapes();

    @Query("SELECT DISTINCT vintage FROM wines WHERE vintage IS NOT NULL AND vintage <> '' ORDER BY vintage")
    String[] vintages();

    @Query("SELECT DISTINCT body FROM wines WHERE body IS NOT NULL AND body <> '' ORDER BY body")
    String[] bodies();

    @Query("SELECT DISTINCT alcoholic_graduation FROM wines WHERE alcoholic_graduation IS NOT NULL AND alcoholic_graduation <> '' ORDER BY alcoholic_graduation")
    String[] alcoholic_graduations();

    @Query("SELECT * FROM wines WHERE (:country IS NULL OR country = :country) AND (:producer IS NULL OR producer = :producer) AND (:type IS NULL OR type = :type) AND (:grape IS NULL OR grape = :grape) AND (:vintage IS NULL OR vintage = :vintage) AND (:body IS NULL OR body = :body) AND (:alcoholic_graduation IS NULL OR alcoholic_graduation = :alcoholic_graduation)")
    Wine[] menuFilter(String country, String producer, String type, String grape, String vintage, String body, String alcoholic_graduation);

    @Insert
    void insertAll(Wine... wine);

    @Update
    void updateAll(Wine... wine);

    @Delete
    void delete(Wine wine);

}