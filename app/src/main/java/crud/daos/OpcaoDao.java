package crud.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import crud.entities.Opcao;

@Dao
public interface OpcaoDao {

    @Query("SELECT * FROM Opcao")
    List<Opcao> getAll();

    @Query("SELECT * FROM Opcao WHERE `id` = :id")
    Opcao findById(String id);

    @Insert
    void insertAll(Opcao... opcao);

    @Update
    void updateAll(Opcao... opcao);

    @Delete
    void delete(Opcao opcao);
}