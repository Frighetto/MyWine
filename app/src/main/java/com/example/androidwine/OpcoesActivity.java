package com.example.androidwine;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import crud.AppDatabase;
import crud.entities.Opcao;
import crud.entities.Led;
import crud.entities.Wine;

public class OpcoesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_opcoes);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        loadFromDatabase();
    }

    private void loadFromDatabase(){
        Context context = this;
        AsyncTask.execute(() -> {
            AppDatabase appDatabase = AppDatabase.getAppDatabase(context);
            Opcao image_dir = appDatabase.opcaoDao().findById("image_dir");
            if(image_dir != null) {
                TextView myAwesomeTextView = findViewById(R.id.EditText_images_path);
                myAwesomeTextView.setText(image_dir.getValue());
            }
            Opcao price_password = appDatabase.opcaoDao().findById("price_password");
            if(price_password != null) {
                TextView myAwesomeTextView = findViewById(R.id.EditText_secret);
                myAwesomeTextView.setText(price_password.getValue());
            }

            Opcao ledlink = appDatabase.opcaoDao().findById("ledlink");
            if(ledlink != null) {
                TextView myAwesomeTextView = findViewById(R.id.EditText_ledlink);
                myAwesomeTextView.setText(ledlink.getValue());
            }

            List<Led> leds = appDatabase.ledDao().getAll();
            if(leds != null){
                StringBuilder sb = new StringBuilder();
                for(Led led: leds){
                    sb.append("("  + led.getColumn() + " " + led.getLine() + " " + led.getValue() + "),");
                }
                TextView myAwesomeTextView = findViewById(R.id.EditText_leds);
                myAwesomeTextView.setText(sb.toString());
            }

            Opcao opcao_columns = appDatabase.opcaoDao().findById("columns");
            Opcao opcao_lines = appDatabase.opcaoDao().findById("lines");
            Opcao opcao_horizontal_grids = appDatabase.opcaoDao().findById("horizontal_grids");
            Opcao opcao_vertical_grids = appDatabase.opcaoDao().findById("vertical_grids");

            if(opcao_columns != null && opcao_lines != null && opcao_horizontal_grids != null && opcao_vertical_grids != null) {
                Integer columns = Integer.parseInt(opcao_columns.getValue());
                Integer lines = Integer.parseInt(opcao_lines.getValue());

                Integer horizontal_grids = Integer.parseInt(opcao_horizontal_grids.getValue());
                Integer vertical_grids = Integer.parseInt(opcao_vertical_grids.getValue());

                TextView myAwesomeTextView = findViewById(R.id.EditText_columns);
                myAwesomeTextView.setText(columns.toString());
                myAwesomeTextView = findViewById(R.id.EditText_lines);
                myAwesomeTextView.setText(lines.toString());

                myAwesomeTextView = findViewById(R.id.EditText_horizontal_grids);
                myAwesomeTextView.setText(horizontal_grids.toString());
                myAwesomeTextView = findViewById(R.id.EditText_vertical_grids);
                myAwesomeTextView.setText(vertical_grids.toString());

                matrix = new Matrix(columns, lines, horizontal_grids, vertical_grids);
            } else {
                matrix = new Matrix(4, 8, 4, 4);
            }
        });
    }

    public void exit(View view){
        this.finish();
    }

    public void salvar(View view){
        Context context = this;
        AsyncTask.execute(() -> {
            AppDatabase appDatabase = AppDatabase.getAppDatabase(context);

            TextView myAwesomeTextView = findViewById(R.id.EditText_images_path);
            save_config(appDatabase, "image_dir", myAwesomeTextView.getText().toString());

            myAwesomeTextView = findViewById(R.id.EditText_columns);
            save_config(appDatabase, "columns", myAwesomeTextView.getText().toString());
            myAwesomeTextView = findViewById(R.id.EditText_lines);
            save_config(appDatabase, "lines", myAwesomeTextView.getText().toString());

            myAwesomeTextView = findViewById(R.id.EditText_horizontal_grids);
            save_config(appDatabase, "horizontal_grids", myAwesomeTextView.getText().toString());
            myAwesomeTextView = findViewById(R.id.EditText_vertical_grids);
            save_config(appDatabase, "vertical_grids", myAwesomeTextView.getText().toString());

            myAwesomeTextView = findViewById(R.id.EditText_secret);
            save_config(appDatabase, "price_password", myAwesomeTextView.getText().toString());

            myAwesomeTextView = findViewById(R.id.EditText_ledlink);
            save_config(appDatabase, "ledlink", myAwesomeTextView.getText().toString());

            myAwesomeTextView = findViewById(R.id.EditText_leds);
            String ledsText = myAwesomeTextView.getText().toString();
            String[] ledsString = ledsText.split(",");
            for(String ledString : ledsString){
                if(ledString.length() > 1) {
                    ledString = ledString.replace("(", "").replace(")", "");
                    String[] led_values = ledString.split(" ");
                    Led led = new Led();
                    led.setColumn(led_values[0]);
                    led.setLine(led_values[1]);
                    led.setValue(led_values[2]);
                    appDatabase.ledDao().delete(led);
                    appDatabase.ledDao().insertAll(led);
                }
            }
        });
    }


    public void salvar_backup(View view){
        Context context = this;
        AsyncTask.execute(() -> {
            AppDatabase appDatabase = AppDatabase.getAppDatabase(context);
            List<Led> ledList = appDatabase.ledDao().getAll();
            List<Opcao> opcaoList = appDatabase.opcaoDao().getAll();
            List<Wine> wineList = appDatabase.wineDao().getAll();
            String backupContent = ledList.toString() + opcaoList.toString() + wineList.toString();
            Opcao image_dir = appDatabase.opcaoDao().findById("image_dir");
            File appDir = new File(image_dir.getValue());
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            String fileName = "backup.txt";
            File file = new File(appDir, fileName);
            try {
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(backupContent.getBytes());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void carregar_backup(View view){
        Context context = this;
        AsyncTask.execute(() -> {
            AppDatabase appDatabase = AppDatabase.getAppDatabase(context);
            Opcao image_dir = appDatabase.opcaoDao().findById("image_dir");
            File appDir = new File(image_dir.getValue());
            if (!appDir.exists()) {
                appDir.mkdir();
            }
            String fileName = "backup.txt";
            File file = new File(appDir, fileName);
            try {
                byte[] fileBytes = new byte[(int) file.length()];
                FileInputStream fis = new FileInputStream(file);
                fis.read(fileBytes);
                String backupContent = new String(fileBytes);
                int ledIndex;
                int wineIndex;
                int opcaoIndex;
                do {
                    ledIndex = backupContent.indexOf("Led{");
                    if(ledIndex > -1){
                        String ledstr = backupContent.substring(ledIndex);
                        ledstr = ledstr.substring(0, ledstr.indexOf("}") + 1);
                        backupContent = backupContent.replace(ledstr, "");

                        Led led = new Led();
                        led.setColumn(valueFromString(ledstr, "column", false));
                        led.setLine(valueFromString(ledstr, "line", false));
                        led.setValue(valueFromString(ledstr, "value", false));

                        appDatabase.ledDao().delete(led);
                        appDatabase.ledDao().insertAll(led);
                    }

                    wineIndex = backupContent.indexOf("Wine{");
                    if(wineIndex > -1){
                        String winestr = backupContent.substring(wineIndex);
                        winestr = winestr.substring(0, winestr.indexOf("}") + 1);
                        backupContent = backupContent.replace(winestr, "");

                        Wine wine = new Wine();
                        String wineColumn = valueFromString(winestr, "column", true);
                        wine.setColumn(Integer.parseInt(wineColumn));
                        String wineLine = valueFromString(winestr, "line", true);
                        wine.setLine(Integer.parseInt(wineLine));
                        wine.setBarcode(valueFromString(winestr, "barcode", false));
                        wine.setCountry(valueFromString(winestr, "country", false));
                        wine.setProducer(valueFromString(winestr, "producer", false));
                        wine.setType(valueFromString(winestr, "type", false));
                        wine.setGrape(valueFromString(winestr, "grape", false));
                        wine.setVintage(valueFromString(winestr, "vintage", false));
                        wine.setBody(valueFromString(winestr, "body", false));
                        wine.setAlcoholic_graduation(valueFromString(winestr, "alcoholic_graduation", false));
                        wine.setPrice(valueFromString(winestr, "price", false));
                        wine.setDescription(valueFromString(winestr, "description", false));
                        wine.setImage(valueFromString(winestr, "image", false));

                        appDatabase.wineDao().delete(wine);
                        appDatabase.wineDao().insertAll(wine);
                    }

                    opcaoIndex = backupContent.indexOf("Opcao{");
                    if(opcaoIndex > -1){
                        String opcaostr = backupContent.substring(opcaoIndex);
                        opcaostr = opcaostr.substring(0, opcaostr.indexOf("}") + 1);
                        backupContent = backupContent.replace(opcaostr, "");

                        Opcao opcao = new Opcao();
                        opcao.setId(valueFromString(opcaostr, "id", false));
                        opcao.setValue(valueFromString(opcaostr, "value", false));

                        appDatabase.opcaoDao().delete(opcao);
                        appDatabase.opcaoDao().insertAll(opcao);
                    }

                } while(ledIndex > -1 || wineIndex > -1 || opcaoIndex > -1);

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                this.finish();
                Intent intent = new Intent(this, OpcoesActivity.class);
                startActivity(intent);
            }
        });
    }

    private String valueFromString(String str, String valueName, Boolean number){
        String beforeValue = valueName;
        if(number){
            beforeValue += "=";
        } else {
            beforeValue += "='";
        }
        Integer valueIndex = str.indexOf(beforeValue);
        if(valueIndex == -1){
            return null;
        }
        valueIndex += beforeValue.length();
        String value = str.substring(valueIndex);
        Integer endIndex;
        if(number){
            endIndex = value.indexOf(",") != -1 ? value.indexOf(",") : value.indexOf("}");
        } else {
            endIndex = value.indexOf("',") != -1 ? value.indexOf("',") : value.indexOf("'}");
        }
        value = value.substring(0, endIndex);
        return value;
    }

    public static Matrix matrix;

    public void led_serpentina(View view){
        TextView myAwesomeTextView = findViewById(R.id.EditText_leds);
        myAwesomeTextView.setText(led_serpentina_str());
    }

    public static String led_serpentina_str(){
        StringBuilder sb = new StringBuilder();
        List<Led> leds = new ArrayList<>();
        for (int verticalIndex = 0; verticalIndex < matrix.getVertical_grids(); verticalIndex = verticalIndex + 1) {
            for (int line = 1; line <= matrix.getLines(); line = line + 1) {
                int current_line = line + verticalIndex * matrix.getLines();
                Boolean serpentine = current_line % 2 == 0;
                for(int horizontalIndex = 0; horizontalIndex < matrix.getHorizontal_grids(); horizontalIndex = horizontalIndex + 1) {
                    for (int column = 1; column <= matrix.getColumns(); column = column + 1) {
                        int current_column = column + horizontalIndex * matrix.getColumns();
                        Led led = new Led();
                        led.setLine(current_line+ "");
                        if(serpentine){
                            led.setColumn(((matrix.getHorizontal_grids() * matrix.getColumns()) - current_column + 1) + "");
                        } else {
                            led.setColumn(current_column + "");
                        }
                        led.setValue((leds.size() + 1) + "");
                        leds.add(led);
                        sb.append("("  + led.getColumn() + " " + led.getLine() + " " + led.getValue() + "),");
                    }
                }
            }
        }
        return sb.toString();
    }

    public void default_config(View view){
        Context context = this;
        AsyncTask.execute(() -> {
            AppDatabase appDatabase = AppDatabase.getAppDatabase(context);
            List<Wine> wineList = appDatabase.wineDao().getAll();
            for(Wine wine: wineList){
                appDatabase.wineDao().delete(wine);
            }
            initialize(appDatabase);
            this.finish();
            Intent intent = new Intent(this, OpcoesActivity.class);
            startActivity(intent);
        });
    }

    public static void initialize(AppDatabase appDatabase){
        if(matrix == null) {
            matrix = new Matrix(4, 8, 4, 2);
        }
        AsyncTask.execute(() -> {
            for(int horizontalIndex = 0; horizontalIndex <= matrix.getHorizontal_grids(); horizontalIndex = horizontalIndex + 1){
                for(int verticalIndex = 0; verticalIndex <= matrix.getVertical_grids(); verticalIndex = verticalIndex + 1){
                    for (int column = 1; column <= matrix.getColumns(); column = column + 1){
                        for(int line = 1; line <= matrix.getLines(); line = line + 1){
                            int current_column = column + horizontalIndex * matrix.getColumns();
                            int current_line = line + verticalIndex * matrix.getLines();
                            Wine wine = new Wine();
                            wine.setColumn(current_column);
                            wine.setLine(current_line);
                            appDatabase.wineDao().insertAll(wine);
                        }
                    }
                }
            }

            save_config(appDatabase, "price_password", "1234");

            save_config(appDatabase, "image_dir", "/storage/emulated/0/Pictures/MyWine/");

            save_config(appDatabase, "columns", "4");
            save_config(appDatabase, "lines", "8");

            save_config(appDatabase, "horizontal_grids", "4");
            save_config(appDatabase, "vertical_grids", "2");

            save_config(appDatabase, "ledlink", "http://127.0.0.1:80/");

            String ledsText = led_serpentina_str();
            String[] ledsString = ledsText.split(",");
            for(String ledString : ledsString){
                if(ledString.length() > 1) {
                    ledString = ledString.replace("(", "").replace(")", "");
                    String[] led_values = ledString.split(" ");
                    Led led = new Led();
                    led.setColumn(led_values[0]);
                    led.setLine(led_values[1]);
                    led.setValue(led_values[2]);
                    appDatabase.ledDao().delete(led);
                    appDatabase.ledDao().insertAll(led);
                }
            }
        });
    }

    private static void save_config(AppDatabase appDatabase, String id, String value){
        Opcao opcao = new Opcao();
        opcao.setId(id);
        opcao.setValue(value);
        appDatabase.opcaoDao().delete(opcao);
        appDatabase.opcaoDao().insertAll(opcao);
    }

}
