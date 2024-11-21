package com.example.dominofx;

import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import org.w3c.dom.ls.LSOutput;

public class DominoTileComponent extends Pane {
    private Tile tile;
    private boolean selected;
    private int degree;
    private ImageView imageView;
    private double baseWidth = 60; // Базовая ширина плитки
    private double baseHeight = 80; // Базовая высота плитки

    public DominoTileComponent(Tile tile, boolean state) {
        this.tile = tile;
        this.selected = state;
        this.degree = tile.getDegree();
        imageView = new ImageView();
        drawTile();
        getChildren().add(imageView);
    }
    private void updateOpacity() {
        double baseOpacity = 1.0; // Базовая прозрачность плитки
        double selectedOpacity = 0.5; // Прозрачность при выборе
        double opacity = selected ? selectedOpacity : baseOpacity;
        imageView.setOpacity(opacity);
    }
    public void rotateImage(double angle) {
        double prevWidth = imageView.getFitWidth();
        double prevHeight = imageView.getFitHeight();

        imageView.setRotate(imageView.getRotate() + angle);

        double newWidth = imageView.getFitWidth();
        double newHeight = imageView.getFitHeight();

        double deltaX = (newWidth - prevWidth) / 2;
        double deltaY = (newHeight - prevHeight) / 2;

        // Корректировка позиции плитки
        setLayoutX(getLayoutX() - deltaX);
        setLayoutY(getLayoutY() - deltaY);

        // Обновление размеров плитки
        setPrefWidth(getPrefWidth() + deltaX * 2);
        setPrefHeight(getPrefHeight() + deltaY * 2);
    }


    public void setSelected(boolean state) {
        this.selected = state;
        updateOpacity();
    }

    public boolean getSelected() {
        return selected;
    }

    public void setDegree(int value) {
        this.degree += value;
        if (degree >= 360) degree -= 360;
        if (degree <= -360) degree += 360;
    }

    private void drawTile() {
        // Использование ImageView для отображения изображени
        imageView.setPreserveRatio(false);
        imageView.setImage(ImageLoader.getTileImage(tile.getFirst(), tile.getSecond()));

        // Установка вращения
        //я в таких афигах, почему так то але.
        if(degree == -90){
            degree = 270;
        }else if(degree == 90){
            degree = -90;
        }else if(degree == 180){
            degree = 0;
        }

        imageView.setRotate(degree);

        // Установка размеров ImageView
        imageView.setFitWidth(60);
        imageView.setFitHeight(80);
    }

    public int getDegree() {
        return degree;
    }
}
