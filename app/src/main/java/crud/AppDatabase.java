package crud;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import crud.daos.OpcaoDao;
import crud.daos.LedDao;
import crud.daos.WineDao;
import crud.entities.Opcao;
import crud.entities.Led;
import crud.entities.Wine;


@Database(entities = {Led.class, Wine.class, Opcao.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase INSTANCE;

    public abstract LedDao ledDao();
    public abstract WineDao wineDao();
    public abstract OpcaoDao opcaoDao();

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class, "mywine").build();
        }
        return INSTANCE;
    }
    public static void destroyInstance() {
        INSTANCE = null;
    }
}