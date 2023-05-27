package com.example.androidwine;

public class Matrix {

    private Integer columns;
    private Integer lines;

    private Integer horizontal_grids;
    private Integer vertical_grids;

    private Integer horizontal_index = 1;
    private Integer vertical_index = 1;

    public Matrix(Integer columns, Integer lines, Integer horizontal_grids, Integer vertical_grids) {
        this.columns = columns;
        this.lines = lines;
        this.horizontal_grids = horizontal_grids;
        this.vertical_grids = vertical_grids;
    }

    public void left(){
        horizontal_index = horizontal_index == 1 ? horizontal_grids : horizontal_index - 1;
    }

    public void up(){
        vertical_index = vertical_index == 1 ? vertical_grids : vertical_index - 1;
    }

    public void right(){
        horizontal_index = horizontal_index == horizontal_grids ? 1 : horizontal_index + 1;
    }

    public void down(){
        vertical_index = vertical_index == vertical_grids ? 1 : vertical_index + 1;
    }

    public Integer getColumns() {
        return columns;
    }

    public Integer getLines() {
        return lines;
    }

    public Integer getHorizontal_grids() {
        return horizontal_grids;
    }

    public Integer getHorizontal_index() {
        return horizontal_index;
    }

    public Integer getVertical_grids(){
        return vertical_grids;
    }

    public Integer getVertical_index() {
        return vertical_index;
    }

    public Integer getViewColumn(Integer column){
        Integer view_column = column % columns;
        if(view_column == 0){
            view_column += 1;
        }
        return view_column;
    }

    public Integer getViewLine(Integer line){
        Integer view_line = line % lines;
        if(view_line == 0){
            view_line += 1;
        }
        return view_line;
    }

    public static boolean isNullMatrix(android.graphics.Matrix imageMatrix){
        return imageMatrix.toString().equalsIgnoreCase("Matrix{[1.0, 0.0, 0.0][0.0, 1.0, 0.0][0.0, 0.0, 1.0]}");
    }
}
