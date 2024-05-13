package com.example.androidwine;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import crud.AppDatabase;
import crud.entities.Led;
import crud.entities.Opcao;
import crud.entities.Wine;

import static android.os.Environment.getExternalStorageDirectory;

public class WineHouseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wine_house_activity);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        TextView myAwesomeTextView = findViewById(R.id.EditText_price);
        Button btn_confirm = findViewById(R.id.Button_confirm);
        if(DataSingleton.getInstance().isMENU()) {
            btn_confirm.setText("LED");
        }
        if(DataSingleton.getInstance().isREMOVE()) {
            btn_confirm.setText("EXCLUIR");
            myAwesomeTextView.setHint("PREÇO");
        }
        if(DataSingleton.getInstance().isADD()) {
            btn_confirm.setText("SALVAR");
            myAwesomeTextView.setHint("PREÇO");
        }

        Context context = this;
        AsyncTask.execute(() -> {
            AppDatabase appDatabase = AppDatabase.getAppDatabase(context);
            Opcao opcao_price_password = appDatabase.opcaoDao().findById("price_password");
            price_password = opcao_price_password.getValue();

            Opcao image_dir = appDatabase.opcaoDao().findById("image_dir");
            IMAGE_DIR = getExternalStorageDirectory() + image_dir.getValue();

            Opcao opcao_columns = appDatabase.opcaoDao().findById("columns");
            Opcao opcao_lines = appDatabase.opcaoDao().findById("lines");
            Opcao opcao_horizontal_grids = appDatabase.opcaoDao().findById("horizontal_grids");
            Opcao opcao_vertical_grids = appDatabase.opcaoDao().findById("vertical_grids");

            columns = Integer.parseInt(opcao_columns.getValue());
            lines = Integer.parseInt(opcao_lines.getValue());

            horizontal_grids = Integer.parseInt(opcao_horizontal_grids.getValue());
            vertical_grids = Integer.parseInt(opcao_vertical_grids.getValue());

            matrix = new Matrix(columns, lines, horizontal_grids, vertical_grids);

            updateGrid();
        });

        disable();

    }

    private static float x1 = 0;
    private static float y1 = 0;
    private static float x2 = 0;
    private static float y2 = 0;
    private static int events = 0;
    private static long last_event = System.currentTimeMillis();

    private String price_password = null;

    @Override
    public boolean onTouchEvent(MotionEvent event){
        if(System.currentTimeMillis() - last_event >= 360){
            x1 = 0;
            y1 = 0;
            x2 = 0;
            y2 = 0;
            events = 0;
        }
        if(event.getX() >= 850){
            x1 = x1 == 0 ? event.getX() : x1;
            y1 = y1 == 0 ? event.getY() : y1;
            x2 = event.getX();
            y2 = event.getY();
            events++;
            last_event = System.currentTimeMillis();
            if(events == 7){
                float x = x1 - x2;
                float y = y1 - y2;
                float dif_x = x < 0 ? x * - 1 : x;
                float dif_y = y < 0 ? y * - 1 : y;
                if(dif_x > dif_y * 3.1416){
                    if(x < 0){
                        right(null);
                    } else {
                        left(null);
                    }
                } else if (dif_y > dif_x * 3.1416){
                    if(y < 0){
                        up(null);
                    } else {
                        down(null);
                    }
                }
            }
        }

        return true;
    }

    private Integer columns;
    private Integer lines;

    private Integer horizontal_grids;
    private Integer vertical_grids;

    private String IMAGE_DIR = null;

    private Integer INPUT_TYPE_PASSWORD = 129;

    private static final int BARCODE_SCANNER_REQUEST = 1;
    private static final int CAMERA_REQUEST = 2;
    private static final int RESULT_LOAD_IMAGE = 3;
    private static final String IMAGE = "imagem";
    private static final String ATRIBUTE = "atributo";

    private final String COUNTRY = "country";
    private final String PRODUCER = "producer";
    private final String TYPE = "type";
    private final String GRAPE = "grape";
    private final String VINTAGE = "vintage";
    private final String BODY = "body";
    private final String ALCOHOLIC_GRADUATION = "alcoholic_graduation";

    private static String[] selected_attributes = {};

    private class Attribute {
        String id;
        Integer button;
        Integer editText;

        public Attribute(String id, Integer button, Integer editText) {
            this.id = id;
            this.button = button;
            this.editText = editText;
        }
    }

    private Attribute current_attribute = null;

    private List<Attribute> attributes = initializeAttributes();

    private List<Attribute> initializeAttributes(){
        List<Attribute> attributes = new ArrayList<>();

        attributes.add(new Attribute(COUNTRY, R.id.Button_country, R.id.EditText_county));
        attributes.add(new Attribute(PRODUCER, R.id.Button_producer, R.id.EditText_producer));
        attributes.add(new Attribute(TYPE, R.id.Button_type, R.id.EditText_type));
        attributes.add(new Attribute(GRAPE, R.id.Button_grape, R.id.EditText_grape));
        attributes.add(new Attribute(VINTAGE, R.id.Button_vintage, R.id.EditText_vintage));
        attributes.add(new Attribute(BODY, R.id.Button_body, R.id.EditText_body));
        attributes.add(new Attribute(ALCOHOLIC_GRADUATION, R.id.Button_alcoholic_graduation, R.id.EditText_alcoholic_graduation));

        return attributes;
    }

    private Attribute getAttribute(String id){
        for(Attribute attribute : attributes){
            if(attribute.id.equalsIgnoreCase(id)){
                return attribute;
            }
        }
        return null;
    }

    private Attribute getAttribute(Integer id){
        for(Attribute attribute : attributes){
            if(attribute.button.equals(id)){
                return attribute;
            }
            if(attribute.editText.equals(id)){
                return attribute;
            }
        }
        return null;
    }

    private class Position {
        Integer column;
        Integer line;
        Integer view_id;

        public Position(Integer column, Integer line, Integer view_id) {
            this.column = column;
            this.line = line;
            this.view_id = view_id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Position position = (Position) o;
            return Objects.equals(column, position.column) &&
                    Objects.equals(line, position.line) &&
                    Objects.equals(view_id, position.view_id);
        }

        @Override
        public int hashCode() {
            return Objects.hash(column, line, view_id);
        }
    }

    private Position current_position = null;

    private List<Position> positions = initializeGrids();

    private List<Position> initializeGrids(){
        List<Position> positions = new ArrayList<>();

        positions.add(new Position(1, 1, R.id.Button_1_1));
        positions.add(new Position(2, 1, R.id.Button_2_1));
        positions.add(new Position(3, 1, R.id.Button_3_1));
        positions.add(new Position(4, 1, R.id.Button_4_1));

        positions.add(new Position(1, 2, R.id.Button_1_2));
        positions.add(new Position(2, 2, R.id.Button_2_2));
        positions.add(new Position(3, 2, R.id.Button_3_2));
        positions.add(new Position(4, 2, R.id.Button_4_2));

        positions.add(new Position(1, 3, R.id.Button_1_3));
        positions.add(new Position(2, 3, R.id.Button_2_3));
        positions.add(new Position(3, 3, R.id.Button_3_3));
        positions.add(new Position(4, 3, R.id.Button_4_3));

        positions.add(new Position(1, 4, R.id.Button_1_4));
        positions.add(new Position(2, 4, R.id.Button_2_4));
        positions.add(new Position(3, 4, R.id.Button_3_4));
        positions.add(new Position(4, 4, R.id.Button_4_4));

        positions.add(new Position(1, 5, R.id.Button_1_5));
        positions.add(new Position(2, 5, R.id.Button_2_5));
        positions.add(new Position(3, 5, R.id.Button_3_5));
        positions.add(new Position(4, 5, R.id.Button_4_5));

        positions.add(new Position(1, 6, R.id.Button_1_6));
        positions.add(new Position(2, 6, R.id.Button_2_6));
        positions.add(new Position(3, 6, R.id.Button_3_6));
        positions.add(new Position(4, 6, R.id.Button_4_6));

        positions.add(new Position(1, 7, R.id.Button_1_7));
        positions.add(new Position(2, 7, R.id.Button_2_7));
        positions.add(new Position(3, 7, R.id.Button_3_7));
        positions.add(new Position(4, 7, R.id.Button_4_7));

        positions.add(new Position(1, 8, R.id.Button_1_8));
        positions.add(new Position(2, 8, R.id.Button_2_8));
        positions.add(new Position(3, 8, R.id.Button_3_8));
        positions.add(new Position(4, 8, R.id.Button_4_8));

        return positions;
    }

    private Position getPosition(Integer column, Integer line){
        for(Position position : positions){
            if(position.column.equals(column) && position.line.equals(line)){
                return position;
            }
        }
        return null;
    }

    private Position getPosition(Integer view_id){
        for(Position position : positions){
            if(position.view_id.equals(view_id)){
                return position;
            }
        }
        return null;
    }

    private List<Wine> leds = new ArrayList<>();


    Matrix matrix;

    public void up(View view){
        matrix.up();
        updateGrid();
        disable();
    }

    public void left(View view){
        matrix.left();
        updateGrid();
        disable();
    }

    public void right(View view){
        matrix.right();
        updateGrid();
        disable();
    }

    public void down(View view){
        matrix.down();
        updateGrid();
        disable();
    }

    private Wine[] wines_filter = new Wine[0];

    private void filterMatrix(){
        List<Wine> queue = new ArrayList<>();
        for (int grid_line = 1; grid_line <= matrix.getHorizontal_grids() + 1; grid_line++){
            for(int grid_column = 1; grid_column <= matrix.getVertical_grids() + 1; grid_column++){
                for (int line = 1; line <= matrix.getLines(); line++) {
                    int current_line = line + (grid_line - 1) * matrix.getLines();
                    for (int column = 1; column <= matrix.getColumns(); column++) {
                        int current_column = column + (grid_column - 1) * matrix.getColumns();
                        for(Wine w : wines_filter){
                            if(w.getLine() == current_line && w.getColumn() == current_column){
                                queue.add(w);
                            }
                        }
                    }
                }
            }
        }
        for(int i = 0; i < queue.size(); i++){
            wines_filter[i] = queue.get(i);
        }
        matrix = new Matrix(columns, lines, horizontal_grids, vertical_grids);
        updateGrid();
    }

    private void updateGridDB(int current_line, int current_column, Button btn){
        Context context = this;
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                AppDatabase appDatabase = AppDatabase.getAppDatabase(context);
                return appDatabase.wineDao().findByPrimaryKey(current_column, current_line);
            }

            @Override
            protected void onPostExecute(Object result) {
                Wine wine = (Wine) result;

                boolean a = wine.getBarcode() != null && !wine.getBarcode().isEmpty();
                boolean b = wine.getCountry() != null && !wine.getCountry().isEmpty();
                boolean c = wine.getProducer() != null && !wine.getProducer().isEmpty();
                boolean d = wine.getType() != null && !wine.getType().isEmpty();
                boolean e = wine.getGrape() != null && !wine.getGrape().isEmpty();
                boolean f = wine.getVintage() != null && !wine.getVintage().isEmpty();
                boolean g = wine.getBody() != null && !wine.getBody().isEmpty();
                boolean h = wine.getAlcoholic_graduation() != null && !wine.getAlcoholic_graduation().isEmpty();
                boolean i = wine.getImage() != null && !wine.getImage().isEmpty();
                boolean j = wine.getPrice() != null && !wine.getPrice().isEmpty();
                boolean k = wine.getDescription() != null && !wine.getDescription().isEmpty();
                Boolean isCurrentPosition = current_position != null && current_column() == wine.getColumn() && current_line() == wine.getLine();
                if(isLit(wine.getColumn(), wine.getLine())){
                    btn.setBackgroundColor(isCurrentPosition ? Color.BLUE : Color.CYAN);
                } else {
                    btn.setBackgroundColor(isCurrentPosition ? Color.GRAY : Color.LTGRAY);
                }
                if(a || b || c || d || e || f || g || h || i || j || k){
                    if(DataSingleton.getInstance().isMENU() || DataSingleton.getInstance().isREMOVE()) {
                        btn.setEnabled(true);
                    } else {
                        if(!isLit(wine.getColumn(), wine.getLine())){
                            btn.setBackgroundColor(Color.GRAY);
                        }
                    }
                } else {
                    if(DataSingleton.getInstance().isMENU() || DataSingleton.getInstance().isREMOVE()) {
                        btn.setEnabled(false);
                    }
                }
            }
        };
        asyncTask.execute();
    }

    private void updateGrid(){
        int queueCount = 0;
        int queueJump = matrix.getLines() * matrix.getColumns() * (matrix.getHorizontal_index() - 1);
        queueJump = queueJump + matrix.getLines() * matrix.getColumns() * matrix.getHorizontal_grids() * (matrix.getVertical_index() - 1);

        for (int line = 1; line <= matrix.getLines(); line++) {
            int current_line = line + (matrix.getVertical_index() - 1) * matrix.getLines();
            for (int column = 1; column <= matrix.getColumns(); column++) {
                int current_column = column + (matrix.getHorizontal_index() - 1) * matrix.getColumns();
                Button btn = findViewById(getPosition(column, line).view_id);
                if(wines_filter.length > 0){
                    int index = queueJump + queueCount;
                    if(index < wines_filter.length){
                        btn.setText(wines_filter[index].getColumn() + " " + wines_filter[index].getLine());
                        btn.setEnabled(true);
                        Boolean isCurrentPosition = wines_filter[index].getColumn() == current_column() && wines_filter[index].getLine() == current_line();
                        if(isLit(wines_filter[index].getColumn(), wines_filter[index].getLine())){
                            btn.setBackgroundColor(isCurrentPosition ? Color.BLUE : Color.CYAN);
                        } else {
                            btn.setBackgroundColor(isCurrentPosition ? Color.GRAY : Color.LTGRAY);
                        }
                    } else {
                        btn.setText("");
                        btn.setEnabled(false);
                        btn.setBackgroundColor(Color.LTGRAY);
                    }
                    queueCount = queueCount + 1;
                } else {
                    btn.setText(current_column + " " + current_line);
                    updateGridDB(current_line, current_column, btn);
                }

            }
        }
    }

    public void exit(View view){
        if(DataSingleton.getInstance().isMENU()) {
            if(initialized()){
                reset();
            } else {
                this.finish();
            }
        } else {
            this.finish();
        }
    }

    public void confirm(View view){
        if(DataSingleton.getInstance().isMENU()) {
            led();
        }
        if(DataSingleton.getInstance().isREMOVE()) {
            delete();
        }
        if(DataSingleton.getInstance().isADD()) {
            save();
        }
    }

    private void led(){
        Wine wine = new Wine();
        wine.setColumn(current_column());
        wine.setLine(current_line());

        int total_leds = leds.size();
        for(int i = 0; i < leds.size(); i++){
            if (leds.get(i).getLine() == wine.getLine() && leds.get(i).getColumn() == wine.getColumn()){
                leds.remove(i);
            }
        }
        if(total_leds == leds.size()){
            leds.add(wine);
        }

        updateGrid();

        ledHttpRequest();
    }

    private boolean isLit(Integer column, Integer line){
        for(int i = 0; i < leds.size(); i++){
            if (leds.get(i).getColumn() == column && leds.get(i).getLine() == line){
                return true;
            }
        }
        return false;
    }


    public void delete(){
        Button btn = findViewById(current_position.view_id);
        btn.setEnabled(true);

        Wine wine = new Wine();
        wine.setColumn(current_column());
        wine.setLine(current_line());

        Context context = this;
        AsyncTask.execute(()->{
            AppDatabase appDatabase = AppDatabase.getAppDatabase(context);
            appDatabase.wineDao().updateAll(wine);
        });
        filterMenu();
        disable();
        updateGrid();
    }

    public void save(){
        Button btn = findViewById(current_position.view_id);
        btn.setEnabled(true);

        Wine wine = new Wine();
        wine.setColumn(current_column());
        wine.setLine(current_line());

        TextView myAwesomeTextView = findViewById(R.id.EditText_barcode);
        wine.setBarcode(myAwesomeTextView.getText().toString().toUpperCase().trim());

        myAwesomeTextView = findViewById(R.id.EditText_county);
        wine.setCountry(myAwesomeTextView.getText().toString().toUpperCase().trim());

        myAwesomeTextView = findViewById(R.id.EditText_producer);
        wine.setProducer(myAwesomeTextView.getText().toString().toUpperCase().trim());

        myAwesomeTextView = findViewById(R.id.EditText_type);
        wine.setType(myAwesomeTextView.getText().toString().toUpperCase().trim());

        myAwesomeTextView = findViewById(R.id.EditText_grape);
        wine.setGrape(myAwesomeTextView.getText().toString().toUpperCase().trim());

        myAwesomeTextView = findViewById(R.id.EditText_vintage);
        wine.setVintage(myAwesomeTextView.getText().toString().toUpperCase().trim());

        myAwesomeTextView = findViewById(R.id.EditText_body);
        wine.setBody(myAwesomeTextView.getText().toString().toUpperCase().trim());

        myAwesomeTextView = findViewById(R.id.EditText_alcoholic_graduation);
        wine.setAlcoholic_graduation(myAwesomeTextView.getText().toString().toUpperCase().trim());

        myAwesomeTextView = findViewById(R.id.EditText_price);
        wine.setPrice(myAwesomeTextView.getText().toString());

        myAwesomeTextView = findViewById(R.id.EditText_description);
        wine.setDescription(myAwesomeTextView.getText().toString());

        ImageView imageView = findViewById(R.id.imageView);
        if(matrix.isNullMatrix(imageView.getImageMatrix())){
            wine.setImage(null);
        } else {
            wine.setImage(IMAGE_DIR + current_column() + " " + current_line());
        }
        imageView.setImageBitmap(null);

        Context context = this;
        AsyncTask.execute(()->{
            AppDatabase appDatabase = AppDatabase.getAppDatabase(context);
            appDatabase.wineDao().updateAll(wine);
        });

        disable();
        updateGrid();
    }

    private void reset(){
        TextView myAwesomeTextView = findViewById(R.id.EditText_barcode);
        myAwesomeTextView.setText("");
        myAwesomeTextView = findViewById(R.id.EditText_county);
        myAwesomeTextView.setText("");
        myAwesomeTextView = findViewById(R.id.EditText_producer);
        myAwesomeTextView.setText("");
        myAwesomeTextView = findViewById(R.id.EditText_type);
        myAwesomeTextView.setText("");
        myAwesomeTextView = findViewById(R.id.EditText_grape);
        myAwesomeTextView.setText("");
        myAwesomeTextView = findViewById(R.id.EditText_vintage);
        myAwesomeTextView.setText("");
        myAwesomeTextView = findViewById(R.id.EditText_body);
        myAwesomeTextView.setText("");
        myAwesomeTextView = findViewById(R.id.EditText_alcoholic_graduation);
        myAwesomeTextView.setText("");
        myAwesomeTextView = findViewById(R.id.EditText_price);
        myAwesomeTextView.setText("");
        myAwesomeTextView = findViewById(R.id.EditText_description);
        myAwesomeTextView.setText("");
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(null);
        imageView.setClickable(false);

        if(current_position != null){
            Button btn = findViewById(current_position.view_id);
            btn.setEnabled(true);
            btn = findViewById(R.id.Button_confirm);
            btn.setEnabled(false);
        }
        if(DataSingleton.getInstance().isMENU()) {
            Button btn = findViewById(R.id.button_price);
            btn.setEnabled(false);

            myAwesomeTextView = findViewById(R.id.EditText_price);
            myAwesomeTextView.setInputType(INPUT_TYPE_PASSWORD);
            myAwesomeTextView.setEnabled(false);
        }
        current_position = null;

        filterMenu();

        leds.clear();
        updateGrid();
    }

    private void ledHttpRequest(){
        Context context = this;
        AsyncTask.execute(() -> {
            AppDatabase appDatabase = AppDatabase.getAppDatabase(context);
            Opcao ledlink = appDatabase.opcaoDao().findById("ledlink");

            try {
                URL url = new URL(ledlink.getValue() + "BLANK");
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(360);
                urlConnection.getInputStream();
                urlConnection.disconnect();

                for(int i = 0; i < leds.size(); i++){
                    Led led = appDatabase.ledDao().findByName(leds.get(i).getColumn().toString(), leds.get(i).getLine().toString());
                    url = new URL(ledlink.getValue() + "(" + led.getValue() + "=BLUE)");
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.getInputStream();
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        });
    }

    public void disable(){
        DataSingleton.WineHouseMode wineHouseMode = DataSingleton.getInstance().getWineHouseMode();
        Boolean enabled = wineHouseMode == DataSingleton.WineHouseMode.MENU || wineHouseMode == DataSingleton.WineHouseMode.REMOVE;
        TextView myAwesomeTextView = findViewById(R.id.EditText_barcode);
        myAwesomeTextView.setText("");
        myAwesomeTextView.setEnabled(false);
        myAwesomeTextView.setTextColor(Color.BLACK);
        Button btn = findViewById(R.id.Button_barcode);
        btn.setEnabled(enabled);

        myAwesomeTextView = findViewById(R.id.EditText_county);
        myAwesomeTextView.setText("");
        myAwesomeTextView.setEnabled(false);
        myAwesomeTextView.setTextColor(Color.BLACK);
        btn = findViewById(R.id.Button_country);
        btn.setEnabled(enabled);

        myAwesomeTextView = findViewById(R.id.EditText_producer);
        myAwesomeTextView.setText("");
        myAwesomeTextView.setEnabled(false);
        myAwesomeTextView.setTextColor(Color.BLACK);
        btn = findViewById(R.id.Button_producer);
        btn.setEnabled(enabled);

        myAwesomeTextView = findViewById(R.id.EditText_type);
        myAwesomeTextView.setText("");
        myAwesomeTextView.setEnabled(false);
        myAwesomeTextView.setTextColor(Color.BLACK);
        btn = findViewById(R.id.Button_type);
        btn.setEnabled(enabled);

        myAwesomeTextView = findViewById(R.id.EditText_grape);
        myAwesomeTextView.setText("");
        myAwesomeTextView.setEnabled(false);
        myAwesomeTextView.setTextColor(Color.BLACK);
        btn = findViewById(R.id.Button_grape);
        btn.setEnabled(enabled);

        myAwesomeTextView = findViewById(R.id.EditText_vintage);
        myAwesomeTextView.setText("");
        myAwesomeTextView.setEnabled(false);
        myAwesomeTextView.setTextColor(Color.BLACK);
        btn = findViewById(R.id.Button_vintage);
        btn.setEnabled(enabled);

        myAwesomeTextView = findViewById(R.id.EditText_body);
        myAwesomeTextView.setText("");
        myAwesomeTextView.setEnabled(false);
        myAwesomeTextView.setTextColor(Color.BLACK);
        btn = findViewById(R.id.Button_body);
        btn.setEnabled(enabled);

        myAwesomeTextView = findViewById(R.id.EditText_alcoholic_graduation);
        myAwesomeTextView.setText("");
        myAwesomeTextView.setEnabled(false);
        myAwesomeTextView.setTextColor(Color.BLACK);
        btn = findViewById(R.id.Button_alcoholic_graduation);
        btn.setEnabled(enabled);

        myAwesomeTextView = findViewById(R.id.EditText_price);
        myAwesomeTextView.setText("");
        myAwesomeTextView.setEnabled(false);
        myAwesomeTextView.setTextColor(Color.BLACK);

        myAwesomeTextView = findViewById(R.id.EditText_description);
        myAwesomeTextView.setText("");
        myAwesomeTextView.setEnabled(false);
        myAwesomeTextView.setTextColor(Color.BLACK);

        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(null);
        imageView.setClickable(false);

        if(current_position != null){
            btn = findViewById(current_position.view_id);
            btn.setEnabled(true);
            btn = findViewById(R.id.Button_confirm);
            btn.setEnabled(false);
        }

        current_position = null;
    }

    public void scanBarcode(View view) {
        Intent intent = new Intent(this, BarcodeSannerActivity.class);
        startActivityForResult(intent, BARCODE_SCANNER_REQUEST);
    }

    public void photo(View view) {
        String[] selectionOptions = new String[2];
        selectionOptions[0] = "ARQUIVOS";
        selectionOptions[1] = "CAMERA";
        String title = "Chose";
        String emptyItemTitle = "NENHUM";
        int initialSelection = 0;
        showSingleChoiceDialogWithNoneOption(title, selectionOptions, initialSelection, emptyItemTitle, IMAGE);
    }

    public void selectAttribute(View view){
        current_attribute = getAttribute(view.getId());
        String column_name = current_attribute.id;

        Context context = this;
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                AppDatabase appDatabase = AppDatabase.getAppDatabase(context);

                if(column_name == COUNTRY){
                    selected_attributes = appDatabase.wineDao().countries();
                } else if(column_name == PRODUCER) {
                    selected_attributes = appDatabase.wineDao().producers();
                }else if(column_name == TYPE) {
                    selected_attributes = appDatabase.wineDao().types();
                }else if(column_name == GRAPE) {
                    selected_attributes = appDatabase.wineDao().grapes();
                }else if(column_name == VINTAGE) {
                    selected_attributes = appDatabase.wineDao().vintages();
                }else if(column_name == BODY) {
                    selected_attributes = appDatabase.wineDao().bodies();
                }else if(column_name == ALCOHOLIC_GRADUATION) {
                    selected_attributes = appDatabase.wineDao().alcoholic_graduations();
                }

                return selected_attributes;
            }

            @Override
            protected void onPostExecute(Object result) {
                String[] listaCaracteristica = (String[]) result;
                String title = "Selecione";
                String emptyItemTitle = "NENHUM";
                int initialSelection = 0;
                showSingleChoiceDialogWithNoneOption(title, listaCaracteristica, initialSelection, emptyItemTitle, ATRIBUTE);
            }
        };
        asyncTask.execute();
    }

    private Boolean initialized(){
        Boolean anyNotEmpty = false;

        TextView myAwesomeTextView = findViewById(R.id.EditText_county);
        String text = myAwesomeTextView.getText().toString();
        anyNotEmpty = anyNotEmpty || !text.isEmpty();
        myAwesomeTextView = findViewById(R.id.EditText_producer);
        text = myAwesomeTextView.getText().toString();
        anyNotEmpty = anyNotEmpty || !text.isEmpty();
        myAwesomeTextView = findViewById(R.id.EditText_type);
        text = myAwesomeTextView.getText().toString();
        anyNotEmpty = anyNotEmpty || !text.isEmpty();
        myAwesomeTextView = findViewById(R.id.EditText_grape);
        text = myAwesomeTextView.getText().toString();
        anyNotEmpty = anyNotEmpty || !text.isEmpty();
        myAwesomeTextView = findViewById(R.id.EditText_vintage);
        text = myAwesomeTextView.getText().toString();
        anyNotEmpty = anyNotEmpty || !text.isEmpty();
        myAwesomeTextView = findViewById(R.id.EditText_body);
        text = myAwesomeTextView.getText().toString();
        anyNotEmpty = anyNotEmpty || !text.isEmpty();
        myAwesomeTextView = findViewById(R.id.EditText_alcoholic_graduation);
        text = myAwesomeTextView.getText().toString();
        anyNotEmpty = anyNotEmpty || !text.isEmpty();

        return anyNotEmpty || current_position != null;
    }

    private void filterMenu(){
        Boolean anyNotNull = false;
        TextView myAwesomeTextView = findViewById(R.id.EditText_county);
        String text = myAwesomeTextView.getText().toString();
        String country = text.isEmpty() ? null : text;
        anyNotNull = anyNotNull || country != null;
        myAwesomeTextView = findViewById(R.id.EditText_producer);
        text = myAwesomeTextView.getText().toString();
        String producer = text.isEmpty() ? null : text;
        anyNotNull = anyNotNull || producer != null;
        myAwesomeTextView = findViewById(R.id.EditText_type);
        text = myAwesomeTextView.getText().toString();
        String type = text.isEmpty() ? null : text;
        anyNotNull = anyNotNull || type != null;
        myAwesomeTextView = findViewById(R.id.EditText_grape);
        text = myAwesomeTextView.getText().toString();
        String grape = text.isEmpty() ? null : text;
        anyNotNull = anyNotNull || grape != null;
        myAwesomeTextView = findViewById(R.id.EditText_vintage);
        text = myAwesomeTextView.getText().toString();
        String vintage = text.isEmpty() ? null : text;
        anyNotNull = anyNotNull || vintage != null;
        myAwesomeTextView = findViewById(R.id.EditText_body);
        text = myAwesomeTextView.getText().toString();
        String body = text.isEmpty() ? null : text;
        anyNotNull = anyNotNull || body != null;
        myAwesomeTextView = findViewById(R.id.EditText_alcoholic_graduation);
        text = myAwesomeTextView.getText().toString();
        String alcoholic_graduation = text.isEmpty() ? null : text;
        anyNotNull = anyNotNull || alcoholic_graduation != null;
        if(anyNotNull) {
            Context context = this;
            AsyncTask asyncTask = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    AppDatabase appDatabase = AppDatabase.getAppDatabase(context);
                    return appDatabase.wineDao().menuFilter(country, producer, type, grape, vintage, body, alcoholic_graduation);
                }

                @Override
                protected void onPostExecute(Object result) {
                    wines_filter = (Wine[]) result;
                    filterMatrix();
                }
            };
            asyncTask.execute();
        } else {
            wines_filter = new Wine[0];
            filterMatrix();
        }
    }

    private void showSingleChoiceDialogWithNoneOption(String title, final String[] titleItems, int initialSelection, String emptyItemTitle, String type) {

        final String[] extendedItems = addEmptyItem(titleItems, emptyItemTitle);
        final int[] selectedPosition = {initialSelection};

        new AlertDialog.Builder(this)
                .setTitle(title)
                .setSingleChoiceItems(extendedItems, initialSelection, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        selectedPosition[0] = which;
                        Log.d("MyTag", String.format("Selected item '%s' at position %s.", extendedItems[which], which));
                    }
                })
                .setNegativeButton("Cancel", null)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("MyTag", String.format("Confirmed the selection of '%s' at position %s.", extendedItems[selectedPosition[0]], selectedPosition[0]));
                        onSelectionConfirmed(selectedPosition[0], type);
                    }
                })
                .show();
    }

    @NonNull
    private String[] addEmptyItem(String[] titleItems, String emptyTitle) {
        String[] tempArray = new String[titleItems.length + 1];
        tempArray[0] = emptyTitle;
        System.arraycopy(titleItems, 0, tempArray, 1, titleItems.length);
        return tempArray;
    }

    private void onSelectionConfirmed(int position, String type) {
        if(type.equalsIgnoreCase(ATRIBUTE)){
            TextView myAwesomeTextView = findViewById(current_attribute.editText);
            if (position == 0){
                //Handle your empty selection
                myAwesomeTextView.setText(null);
            }else{
                //Selected item at position
                String selectedItem = selected_attributes[position - 1];
                myAwesomeTextView.setText(selectedItem);
            }
            if(DataSingleton.getInstance().isMENU() || DataSingleton.getInstance().isREMOVE()){
                filterMenu();
            }
        }
        if(type.equalsIgnoreCase(IMAGE)){
            if(position == 0){
                ImageView imageView = findViewById(R.id.imageView);
                imageView.setImageBitmap(null);
            } else if(position == 1){
                Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, RESULT_LOAD_IMAGE);
            } else{
                Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST);
            }

        }
    }

    public void showPrice(View view){
        TextView myAwesomeTextView = findViewById(R.id.EditText_price);
        if(myAwesomeTextView.getText().toString().equalsIgnoreCase(price_password)) {
            myAwesomeTextView.setInputType(EditorInfo.TYPE_CLASS_TEXT);
            Context context = this;
            AsyncTask asyncTask = new AsyncTask() {
                @Override
                protected Object doInBackground(Object[] objects) {
                    AppDatabase appDatabase = AppDatabase.getAppDatabase(context);
                    return appDatabase.wineDao().findByPrimaryKey(current_column(), current_line());
                }

                @Override
                protected void onPostExecute(Object result) {
                    Wine wine = (Wine) result;
                    myAwesomeTextView.setText(wine.getPrice());
                }
            };
            asyncTask.execute();
        }
    }

    public void loadWineByView(View view){
        if (current_position != null && current_position != getPosition(view.getId())) {
            Button btn = findViewById(current_position.view_id);
            updateGridDB(current_line(), current_column(), btn);
            TextView myAwesomeTextView = findViewById(R.id.EditText_price);
            myAwesomeTextView.setInputType(INPUT_TYPE_PASSWORD);
        }
        current_position = getPosition(view.getId());
        Button btn = (Button) view;
        if(isLit(current_column(), current_line())){
            btn.setBackgroundColor(Color.BLUE);
        } else {
            btn.setBackgroundColor(Color.GRAY);
        }
        btn = findViewById(R.id.Button_confirm);
        btn.setEnabled(true);
        loadWine(null);
    }

    private Integer current_column(){
        if(current_position != null){
            Button btn = findViewById(current_position.view_id);
            String txt = btn.getText().toString();
            txt = txt.subSequence(0, txt.indexOf(" ")).toString();
            return Integer.parseInt(txt);
        }
        return null;
    }

    private Integer current_line(){
        if(current_position != null) {
            Button btn = findViewById(current_position.view_id);
            String txt = btn.getText().toString();
            txt = txt.subSequence(txt.indexOf(" ") + 1, txt.length()).toString();
            return Integer.parseInt(txt);
        }
        return null;
    }

    private void loadWine(String barcode) {
        Context context = this;
        AsyncTask asyncTask = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                if(barcode == null) {
                    AppDatabase appDatabase = AppDatabase.getAppDatabase(context);
                    return appDatabase.wineDao().findByPrimaryKey(current_column(), current_line());
                } else {
                    AppDatabase appDatabase = AppDatabase.getAppDatabase(context);
                    Wine wine = appDatabase.wineDao().findByBarcode(barcode);
                    Integer view_column = matrix.getViewColumn(wine.getColumn());
                    Integer view_line = matrix.getViewLine(wine.getLine());
                    current_position = getPosition(view_column, view_line);
                    return wine;
                }
            }

            @Override
            protected void onPostExecute(Object result) {
                Boolean enabled = DataSingleton.getInstance().isADD();
                Boolean btn_enabled = DataSingleton.getInstance().isADD() || DataSingleton.getInstance().isMENU();
                Wine wine = (Wine) result;

                ImageView newsImage = findViewById(R.id.imageView);
                newsImage.setImageBitmap(null);
                if(wine.getImage() != null) {
                    Bitmap bitmap = BitmapFactory.decodeFile(IMAGE_DIR + current_column() + " " + current_line() + ".jpg");
                    newsImage.setImageBitmap(bitmap);
                }
                newsImage.setClickable(enabled);

                TextView myAwesomeTextView = findViewById(R.id.EditText_barcode);
                myAwesomeTextView.setText(wine.getBarcode());
                myAwesomeTextView.setEnabled(enabled);
                Button btn = findViewById(R.id.Button_barcode);
                btn.setEnabled(btn_enabled);

                myAwesomeTextView = findViewById(R.id.EditText_county);
                myAwesomeTextView.setText(wine.getCountry());
                myAwesomeTextView.setEnabled(enabled);
                btn = findViewById(R.id.Button_country);
                btn.setEnabled(btn_enabled);

                myAwesomeTextView = findViewById(R.id.EditText_producer);
                myAwesomeTextView.setText(wine.getProducer());
                myAwesomeTextView.setEnabled(enabled);
                btn = findViewById(R.id.Button_producer);
                btn.setEnabled(btn_enabled);

                myAwesomeTextView = findViewById(R.id.EditText_type);
                myAwesomeTextView.setText(wine.getType());
                myAwesomeTextView.setEnabled(enabled);
                btn = findViewById(R.id.Button_type);
                btn.setEnabled(btn_enabled);

                myAwesomeTextView = findViewById(R.id.EditText_grape);
                myAwesomeTextView.setText(wine.getGrape());
                myAwesomeTextView.setEnabled(enabled);
                btn = findViewById(R.id.Button_grape);
                btn.setEnabled(btn_enabled);

                myAwesomeTextView = findViewById(R.id.EditText_vintage);
                myAwesomeTextView.setText(wine.getVintage());
                myAwesomeTextView.setEnabled(enabled);
                btn = findViewById(R.id.Button_vintage);
                btn.setEnabled(btn_enabled);

                myAwesomeTextView = findViewById(R.id.EditText_body);
                myAwesomeTextView.setText(wine.getBody());
                myAwesomeTextView.setEnabled(enabled);
                btn = findViewById(R.id.Button_body);
                btn.setEnabled(btn_enabled);

                myAwesomeTextView = findViewById(R.id.EditText_alcoholic_graduation);
                myAwesomeTextView.setText(wine.getAlcoholic_graduation());
                myAwesomeTextView.setEnabled(enabled);
                btn = findViewById(R.id.Button_alcoholic_graduation);
                btn.setEnabled(btn_enabled);

                myAwesomeTextView = findViewById(R.id.EditText_price);

                if(DataSingleton.getInstance().isMENU()){
                    myAwesomeTextView.setInputType(INPUT_TYPE_PASSWORD);
                    myAwesomeTextView.setText(null);
                } else {
                    myAwesomeTextView.setInputType(EditorInfo.TYPE_CLASS_TEXT);
                    myAwesomeTextView.setText(wine.getPrice());
                }
                myAwesomeTextView.setEnabled(btn_enabled);

                btn = findViewById(R.id.button_price);
                btn.setEnabled(btn_enabled);

                myAwesomeTextView = findViewById(R.id.EditText_description);
                myAwesomeTextView.setText(wine.getDescription());
                myAwesomeTextView.setEnabled(enabled);
            }
        };
        asyncTask.execute();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == BARCODE_SCANNER_REQUEST){
            TextView myAwesomeTextView = findViewById(R.id.EditText_barcode);
            System.out.println(DataSingleton.getInstance().getBarcode());
            myAwesomeTextView.setText(DataSingleton.getInstance().getBarcode());
        }
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK){
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            bitmap.compress(Bitmap.CompressFormat.PNG,100, os);
            ImageView newsImage = findViewById(R.id.imageView);
            newsImage.setImageBitmap(bitmap);
            saveImageToGallery(bitmap);
        }
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            if(bitmap.getHeight() < bitmap.getWidth()) {
                bitmap = RotateBitmap(bitmap, 90);
            }
            ImageView newsImage = findViewById(R.id.imageView);
            newsImage.setImageBitmap(bitmap);
            saveImageToGallery(bitmap);
        }

    }

    public boolean saveImageToGallery(Bitmap bmp) {
        // First save the picture
        String storePath = IMAGE_DIR;
        File appDir = new File(storePath);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = current_column() + " " + current_line() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            boolean isSuccess = bmp.compress(Bitmap.CompressFormat.JPEG, 60, fos);
            fos.flush();
            fos.close();

            return isSuccess;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        android.graphics.Matrix matrix = new android.graphics.Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

}
