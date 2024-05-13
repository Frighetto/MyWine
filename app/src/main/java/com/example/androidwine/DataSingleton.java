package com.example.androidwine;

class DataSingleton {

    private static final DataSingleton ourInstance = new DataSingleton();

    static DataSingleton getInstance() {
        return ourInstance;
    }

    private DataSingleton() {
    }

    public enum WineHouseMode {
        MENU, ADD, REMOVE
    }

    private WineHouseMode wineHouseMode = null;
    private String barcode = null;

    public WineHouseMode getWineHouseMode() {
        return wineHouseMode;
    }

    public void setWineHouseMode(WineHouseMode wineHouseMode) {
        this.wineHouseMode = wineHouseMode;
    }

    public void setBarcode(String barcode){
        this.barcode = barcode;
    }

    public String getBarcode() {
        return barcode;
    }

    public Boolean isMENU(){
        return wineHouseMode == WineHouseMode.MENU;
    }

    public Boolean isADD(){
        return wineHouseMode == WineHouseMode.ADD;
    }

    public Boolean isREMOVE(){
        return wineHouseMode == WineHouseMode.REMOVE;
    }
}
