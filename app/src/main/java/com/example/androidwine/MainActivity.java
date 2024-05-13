package com.example.androidwine;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import crud.AppDatabase;
import crud.entities.Opcao;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        firstTime();
    }

    private void firstTime() {
        Context context = this;
        AsyncTask.execute(() -> {
            AppDatabase appDatabase = AppDatabase.getAppDatabase(context);
            Opcao columns = appDatabase.opcaoDao().findById("columns");
            if (columns == null) {
                OpcoesActivity.initialize(appDatabase);
            }
        });
    }
    public void pesquisar(View view){
        DataSingleton.getInstance().setWineHouseMode(DataSingleton.WineHouseMode.MENU);
        initializeWineHouseActivity();
    }

    public void adicionar(View view){
        DataSingleton.getInstance().setWineHouseMode(DataSingleton.WineHouseMode.ADD);
        initializeWineHouseActivity();
    }

    public void excluir(View view){
        DataSingleton.getInstance().setWineHouseMode(DataSingleton.WineHouseMode.REMOVE);
        initializeWineHouseActivity();
    }

    public void opcoes(View view){
        Intent intent = new Intent(this, OpcoesActivity.class);
        startActivity(intent);
    }

    public void sobre(View view){
        Intent intent = new Intent(this, SobreActivity.class);
        startActivity(intent);
    }

    public void exit(View view){
        this.finish();
    }

    private void initializeWineHouseActivity(){
        Intent intent = new Intent(this, WineHouseActivity.class);
        startActivity(intent);
    }

}
