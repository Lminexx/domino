package com.example.dominofx;

import com.example.dominofx.Controllers.MainGameController;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import org.w3c.dom.ls.LSOutput;

import java.util.ArrayList;
import java.util.List;

public class PlayerHandComponent extends Pane {
    private ObservableList<Tile> hand;
    private List<DominoTileComponent> tiles = new ArrayList<>();
    public int wasSelected = -1;
    private MainGameController mainGameController;
    public PlayerHandComponent(ObservableList<Tile> hand, MainGameController game) {
        mainGameController = game;
         this.hand = hand;
         setPrefSize(689, 137);
         HBox hbox = new HBox(25);
         hbox.setPadding(new Insets(5));
         getChildren().add(hbox);
         updateHand(hbox, game);
    }

    private int getClickedTileIndex(MouseEvent event) {
        double mouseX = event.getX();
        double mouseY = event.getY();
        double x = 10;

        for (int i = 0; i < tiles.size(); i++) {
            double size = tiles.get(i).getLayoutBounds().getWidth();
            if (mouseX >= x && mouseX <= x + size && mouseY >= 5 && mouseY <= 105) {
                return i;
            }
            x += size + 10;
        }
        return -1;
    }
    public void rotateSelectedTile(int value) {
        if (wasSelected != -1) {
            DominoTileComponent selectedTile = tiles.get(wasSelected);
            selectedTile.setDegree(selectedTile.getDegree() + value);
            selectedTile.rotateImage(value);

            // Обновляем degree у соответствующего Tile в списке hand
            Tile correspondingTile = hand.get(wasSelected);
            int newDegree = (correspondingTile.getDegree() + value) % 360;
            correspondingTile.setDegree(newDegree);
            // Обновляем значения first и second в зависимости от угла поворота
            if(newDegree == 0){
                int temp = correspondingTile.getFirst();
                correspondingTile.setFirst(temp);
                correspondingTile.setSecond(correspondingTile.getSecond());
            }else if(newDegree == 90){
                int temp = correspondingTile.getFirst();
                correspondingTile.setFirst(correspondingTile.getSecond());
                correspondingTile.setSecond(temp);
            }else if(newDegree == 180){
                int temp = correspondingTile.getFirst();
                correspondingTile.setFirst(temp);
                correspondingTile.setSecond(correspondingTile.getSecond());
            }else if(newDegree == 270){
                int temp = correspondingTile.getFirst();
                correspondingTile.setFirst(correspondingTile.getSecond());
                correspondingTile.setSecond(temp);
            }else if(newDegree == -90){
                int temp = correspondingTile.getFirst();
                correspondingTile.setFirst(temp);
                correspondingTile.setSecond(correspondingTile.getSecond());
            }else if(newDegree == -180){
                int temp = correspondingTile.getFirst();
                correspondingTile.setFirst(correspondingTile.getSecond());
                correspondingTile.setSecond(temp);
            }else if(newDegree == -270){
                int temp = correspondingTile.getFirst();
                correspondingTile.setFirst(temp);
                correspondingTile.setSecond(correspondingTile.getSecond());
            }
        }
    }


    public void updateHand(HBox hbox, MainGameController game) {
        hbox.getChildren().clear();
        tiles.clear();
        for (int i = 0; i < hand.size(); i++) {
            final int clickedIndex = i;
            Tile tile = hand.get(i);
            DominoTileComponent temp = new DominoTileComponent(tile, false);

            temp.setOnMouseClicked(e -> {
                // Добавим проверку на валидность индекса
                if (clickedIndex >= 0 && clickedIndex < tiles.size()) {
                    if (wasSelected != -1 && wasSelected != clickedIndex) {
                        if(wasSelected > hand.size()-1){
                            wasSelected = hand.size()-1;
                        }
                        tiles.get(wasSelected).setSelected(false);
                    }

                    boolean state = temp.getSelected();
                    if (!state) {
                        game.dominoChosen = true;
                        game.dominoIndex = clickedIndex;
                        wasSelected = clickedIndex;
                        temp.requestFocus();
                    } else {
                        game.dominoChosen = false;
                        game.dominoIndex = -1;
                        wasSelected = -1;
                    }

                    temp.setSelected(!state);
                    redraw(hbox);
                }
            });

            // Добавляем обработчик нажатия клавиши "E" и "Q" для поворота выбранной плитки
            temp.setOnKeyPressed(event -> {
                if (event.getCode() == KeyCode.E && temp.getSelected()) {
                    rotateSelectedTile(90);

                }
                if (event.getCode() == KeyCode.Q && temp.getSelected()) {
                    rotateSelectedTile(-90);
                }
            });

            hbox.getChildren().add(temp);
            tiles.add(temp);
        }
        redraw(hbox);
    }


    public List<Tile> getHand() {
        return hand;
    }

    public void redraw(HBox hbox) {
        hbox.layout();
    }


}