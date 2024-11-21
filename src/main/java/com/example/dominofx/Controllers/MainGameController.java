package com.example.dominofx.Controllers;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import com.example.dominofx.*;
import com.example.dominofx.Server.Client;
import com.example.dominofx.Server.Message;
import com.example.dominofx.Server.MessageType;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.transform.Rotate;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class MainGameController{

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private Button addFishk;

    @FXML
    private Button backMenuButton;

    @FXML
    private Button rotateButton;

    @FXML
    private Pane handPane;

    @FXML
    private Label waitButton;
    @FXML
    private Pane borderPane;
    public boolean dominoChosen = false;
    public int dominoIndex = -1;
    private ArrayList<Tile> deck = new ArrayList<>();
    private ObservableList<Tile> hand = FXCollections.observableArrayList();
    private ArrayList<Tile> board = new ArrayList<>();
    private List<Marker> markers = new ArrayList<>();
    private boolean myTurn = false;
    private Tile selectedTile;
    private boolean Host = false;
    private boolean isFirst = true;
    private int lastClickEnd = 0;
    private int lastClickStart=0;
    @FXML
    void initialize() throws IOException {
        Init.setMainGameController(this);
        Init.getClient().getConnection().send(new Message(MessageType.CONNECT_TO_GAME));
        iniz();
    }
    public void iniz(){
        lastClickEnd = 0;
        lastClickStart = 0;
        board.clear();
        handPane.getChildren().clear();
        readAllTiles();
        getPlayerTilesHand();
        borderPane.setOnMouseClicked(event -> onMouseClickedOnBoard(event.getX(), event.getY()));
        // Убедимся, что начальный маркер добавляется после инициализации окна
        Platform.runLater(this::addInitialMarker);
    }
    public void updateTurn(boolean myTurn) {
        this.myTurn = myTurn;
        Platform.runLater(()->{
            if (myTurn) {
                waitButton.setText("Ваш ход!");
            }else{
                waitButton.setText("Ход противника");
            }
        });
    }
    private void addInitialMarker() {
        double centerX = borderPane.getWidth() / 2;
        double centerY = borderPane.getHeight() / 2;
        markers.add(new Marker(centerX, centerY, -1, true, 0, 0)); // -1 значение для первого маркера
        drawMarkers();
    }

    // Обработчик щелчка мыши для установки плитки
    private void onMouseClickedOnBoard(double x, double y) {
        if (myTurn) {
            if (dominoIndex != -1) {
                if (selectedTile == null) {
                    selectedTile = hand.get(dominoIndex);
                }
                if (selectedTile != null) {
                    if (board.isEmpty()) {
                        Tile tile = selectedTile;
                        double centerX = borderPane.getWidth() / 2;
                        double centerY = borderPane.getHeight() / 2;
                        tile.setX(centerX);
                        tile.setY(centerY);
                        board.add(tile);
                        removeTileHand();
                        selectedTile = null;
                        updateBoard();
                        addMarkersForTile(tile, centerX, centerY, true, 0);
                        drawMarkers();
                        try {
                            Init.getClient().getConnection().send(new Message(MessageType.BOARD, board));
                            Init.getClient().getConnection().send(new Message(MessageType.SWITCH_TURN));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        Marker marker = getClickedMarker(x, y);
                        if (marker != null && addTileToBoard(selectedTile, marker)) {
                            removeTileHand();
                            selectedTile = null;
                            updateBoard();
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.BOARD, board));
                                Init.getClient().getConnection().send(new Message(MessageType.SWITCH_TURN));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else{
                            selectedTile = null;
                        }
                    }
                }
            }
        }
    }
    // Метод для проверки и добавления плитки на игровое поле
    private boolean addTileToBoard(Tile tile, Marker marker) {
        Tile referenceTile;
        if (marker.isStart()) {
            referenceTile = board.get(0);
        } else {
            referenceTile = board.get(board.size() - 1);
        }
        if(lastClickEnd == 1){
            marker.setValue(tile.getFirst());
        }
        if(lastClickStart == 3){
            marker.setValue(tile.getSecond());
        }
        if(marker.getValue() == tile.getFirst() || marker.getValue() ==  tile.getSecond()){
            double x = referenceTile.getX();
            double y = referenceTile.getY();
            if(!marker.isStart()){
                if(tile.isVertical()){
                    if(lastClickEnd == 1){
                        if(marker.getWho() == 1){
                            y-=80;
                            tile.setLastclick(1);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.LAST_CLICK, 1));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if (marker.getWho() == 3) {
                            x-=10;
                            y+=70;
                            tile.setLastclick(3);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.LAST_CLICK, 3));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }else  if(lastClickEnd == 0 || lastClickEnd == 2){
                        if(marker.getWho() == 1){
                            y-=70;
                            x+=20;
                            tile.setLastclick(1);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.LAST_CLICK, 1));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else if(marker.getWho()==2){
                            x+=80;
                            tile.setLastclick(2);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.LAST_CLICK, 2));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else if(marker.getWho() == 3){
                            y += 70;
                            x+= 20;
                            tile.setLastclick(3);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.LAST_CLICK, 3));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }else if(lastClickEnd == 3){
                        if(marker.getWho() == 1){
                            y-=70;
                            x-=10;
                            tile.setLastclick(1);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.LAST_CLICK, 1));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else if(marker.getWho() == 3){
                            y += 80;
                            tile.setLastclick(3);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.LAST_CLICK, 3));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }if(tile.isGorizont()){
                    if(lastClickEnd == 1){
                        if(marker.getWho() == 2){
                            x += 70;
                            y-=15;
                            tile.setLastclick(2);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.LAST_CLICK, 2));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if(marker.getWho() == 3){
                            x-=70;
                            y-=10;
                            tile.setLastclick(3);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.LAST_CLICK, 3));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }else if(lastClickEnd == 0 || lastClickEnd == 2){
                        if(marker.getWho() == 1){
                            tile.setLastclick(1);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.LAST_CLICK, 1));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if(marker.getWho()==2){
                            x += 80;
                            tile.setLastclick(2);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.LAST_CLICK, 2));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if(marker.getWho() == 3){
                            y -= 70;
                            x+= marker.isStart() ?  -20 :  20;
                            tile.setLastclick(3);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.LAST_CLICK, 3));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }else if(lastClickEnd == 3){
                        if(marker.getWho() == 1){
                            if(marker.getWho() == 1){
                                x-=70;
                                y+=20;
                            }
                            tile.setLastclick(1);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.LAST_CLICK, 1));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if(marker.getWho()==2){
                            x += 70;
                            y+=20;
                            tile.setLastclick(2);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.LAST_CLICK, 2));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if(marker.getWho() == 3){
                            y -= 70;
                            x+= 20;
                            tile.setLastclick(3);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.LAST_CLICK, 3));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
            if(marker.isStart()){
                if(tile.isGorizont()){
                    if(lastClickStart ==1){
                        if(marker.getWho() == 2){
                            x -= 70;
                            y-=15;
                            tile.setLastclick(2);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.START_CLICK, 2));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if(marker.getWho()==3){
                            x += 70;
                            y-=15;
                            tile.setLastclick(3);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.START_CLICK, 3));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                    }else if(lastClickStart == 0 || lastClickStart == 2){
                        if(marker.getWho() == 2){
                            x-=80;
                            tile.setLastclick(2);
                        }
                        try {
                            Init.getClient().getConnection().send(new Message(MessageType.START_CLICK, 2));
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }else if(lastClickStart == 3){
                        if(marker.getWho() == 2){
                            x-=70;
                            y+=15;
                            tile.setLastclick(2);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.START_CLICK, 2));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
                if(tile.isVertical()){
                    if(lastClickStart == 1){
                        if(marker.getWho()==1){
                            y-=80;
                            tile.setLastclick(1);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.START_CLICK, 1));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }else if(lastClickStart == 0 || lastClickStart == 2){
                        if(marker.getWho() == 1){
                            y-=70;
                            x-=20;
                            tile.setLastclick(1);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.START_CLICK, 1));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }else if(marker.getWho()==3){
                            y +=70;
                            x-=10;
                            tile.setLastclick(3);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.START_CLICK, 3));  //ЗАПОМНИ
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }else if(lastClickStart == 3){
                        if(marker.getWho() == 1){
                            y-=70;
                            x+=10;
                            tile.setLastclick(1);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.START_CLICK, 1));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                        if(marker.getWho() == 3){
                            y += 80;
                            tile.setLastclick(3);
                            try {
                                Init.getClient().getConnection().send(new Message(MessageType.START_CLICK, 3));
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    }
                }
            }
            tile.setX(x);
            tile.setY(y);
            if (marker.isStart()) {
                board.add(0, tile);
            } else {
                board.add(tile);
            }
            return true;
        }
        return false;
    }
    // Метод для обновления маркеров после добавления плитки
    //смартфон vivo
    private void updateMarkers() {
        markers.clear();
        if (!board.isEmpty()) {
            Tile firstTile = board.get(0);              //берет левую и правую.
            Tile lastTile = board.get(board.size() - 1);
            double xTopStart = firstTile.getX();
            double yTopStart = firstTile.getY();
            double xCenterStart = firstTile.getX();
            double yCenterStart = firstTile.getY();           //их координаты
            double xBottomStart = firstTile.getX();
            double yBottomStart = firstTile.getY();
            double xTopEnd = lastTile.getX();
            double yTopEnd = lastTile.getY();
            double xBottomEnd =lastTile.getX();
            double yBottomEnd = lastTile.getY();
            double xCenterEnd = lastTile.getX();
            double yCenterEnd = lastTile.getY();
            if(firstTile.isGorizont()){
                if(lastClickStart == 0){
                    xTopStart +=15;
                    yTopStart -=23;
                    xCenterStart+=1;
                    yCenterStart+=1;
                    xBottomStart +=15;
                    yBottomStart +=25;
                }
                if(firstTile.getLastclick() ==  2){
                    xTopStart +=15;
                    yTopStart -=23;
                    xCenterStart+=1;
                    yCenterStart+=1;
                    xBottomStart +=15;
                    yBottomStart +=25;
                }
                if(firstTile.getLastclick() == 3){
                    xTopStart +=60;
                    yTopStart -=23;
                    xCenterStart+=75;
                    yCenterStart+=1;
                    xBottomStart +=60;
                    yBottomStart +=25;
                }
            }
            if(firstTile.isVertical()){
                if(firstTile.getLastclick() == 1){
                    xTopStart +=33; //33
                    yTopStart +=25; //25
                    xCenterStart +=5; //53
                    yCenterStart +=47;  //47
                    xBottomStart +=53;
                    yBottomStart +=47;
                }
                if(firstTile.getLastclick()  == 3){
                    xTopStart += 53;
                    yTopStart +=75;
                    xCenterStart +=7;
                    yCenterStart +=70;
                    xBottomStart +=27;
                    yBottomStart +=90;
                }
            }
            if(lastTile.isGorizont()){
                if(lastTile.getLastclick() == 0){
                    xTopEnd -= 5;
                    yTopEnd -=23;
                    xCenterEnd +=1;
                    yCenterEnd +=1;
                    xBottomEnd -=5;
                    yBottomEnd +=25;
                }
                if(lastTile.getLastclick() == 1){
                    xTopEnd -= 40;
                    yTopEnd -=27;
                    xCenterEnd -=70;
                    yCenterEnd -=1;
                    xBottomEnd -=40;
                    yBottomEnd +=30;
                }
                if(lastTile.getLastclick() == 2){
                    xTopEnd -= 5;
                    yTopEnd -=23;
                    xCenterEnd +=1;
                    yCenterEnd +=1;
                    xBottomEnd -=5;
                    yBottomEnd +=25;
                }
                if(lastTile.getLastclick()==3){
                    xTopEnd -= 40;
                    yTopEnd -=27;
                    xCenterEnd -=70;
                    yCenterEnd -=1;
                    xBottomEnd -=40;
                    yBottomEnd +=30;
                }

            }
            if(lastTile.isVertical()){
                if(lastTile.getLastclick() == 1){
                    xTopEnd +=33;
                    yTopEnd -=13;
                    xCenterEnd +=53;
                    yCenterEnd +=15;
                    xBottomEnd +=5;
                    yBottomEnd +=13;
                }
                if(lastTile.getLastclick()==3){
                    xTopEnd += 7;
                    yTopEnd +=50;
                    xCenterEnd +=53;
                    yCenterEnd +=50;
                    xBottomEnd +=27;
                    yBottomEnd +=63;
                }
            }
            addMarkersForTile(firstTile, xTopStart, yTopStart , true , 1);
            addMarkersForTile(firstTile, xCenterStart, yCenterStart, true, 2);
            addMarkersForTile(firstTile, xBottomStart, yBottomStart, true, 3);
            addMarkersForTile(lastTile, xTopEnd, yTopEnd, false, 1);
            addMarkersForTile(lastTile, xCenterEnd, yCenterEnd, false, 2);
            addMarkersForTile(lastTile, xBottomEnd, yBottomEnd, false, 3);
        }
        drawMarkers();
    }
    private void addMarkersForTile(Tile tile, double x, double y, boolean start, int who) {
        double degree = tile.getDegree();
        int value = start ? tile.getFirst() : tile.getSecond();
        double markerX = x;
        double markerY = y;
        if (tile.isGorizont()) {
            markerX += start ? -10 : 60;
            markerY +=40;
        } else {
            markerY += start ? -15 : 15;
        }

        Marker marker = new Marker(markerX, markerY, value, start, degree, who);
        markers.add(marker);
    }

    private Marker getClickedMarker(double x, double y) {
        for (Marker marker : markers) {
            if (marker.isClicked(x, y)) {
                return marker;
            }
        }
        return null;
    }
    // Метод для рисования маркеров
    private void drawMarkers() {
        borderPane.getChildren().removeIf(node -> node instanceof Circle);
        for (Marker marker : markers) {
            Circle markerView = new Circle(marker.getX(), marker.getY(), 5);
            markerView.setFill(Color.RED);
            markerView.getTransforms().add(new Rotate(marker.getDegree(), marker.getX(), marker.getY()));
            borderPane.getChildren().add(markerView);
        }
    }

    // Метод для обновления визуального отображения игрового поля
    private void updateBoard() {
        borderPane.getChildren().clear();
        if (board.isEmpty()) {
            return;
        }
        for (Tile tile : board) {
            DominoTileComponent tileView = new DominoTileComponent(tile, false);
            tileView.setLayoutX(tile.getX());
            tileView.setLayoutY(tile.getY());
            borderPane.getChildren().add(tileView);
        }
        updateMarkers();
    }

    public void readAllTiles(){
        deck.clear();
        for (int i = 0; i <=6; i++) {
            for (int j = i; j <=6; j++) {
                Tile tile;
                if(i == j){
                    tile = new Tile(i,j,0,true);
                }else{
                    tile = new Tile(i,j, 0, false);
                }
                deck.add(tile);
            }
        }
    }
    public void giveTile() {
        if(!deck.isEmpty()){
            hand.add(deck.get(0));
            deck.remove(0);
            // Получение экземпляра PlayerHandComponent из handPane
            PlayerHandComponent playerHandComponent = (PlayerHandComponent) handPane.getChildren().get(0);
            // Получение панели руки из playerHandComponent
            HBox handHBox = (HBox) playerHandComponent.getChildren().get(0);
            // Обновление панели руки
            playerHandComponent.updateHand(handHBox, this);
        }else{
            System.out.println("Плитки закончились");
        }

    }

    public void removeTileHand(){
        hand.remove(selectedTile);
        dominoIndex =-1;
        // Получение экземпляра PlayerHandComponent из handPane
        PlayerHandComponent playerHandComponent = (PlayerHandComponent) handPane.getChildren().get(0);
        // Получение панели руки из playerHandComponent
        HBox handHBox = (HBox) playerHandComponent.getChildren().get(0);
        // Обновление панели руки
        playerHandComponent.updateHand(handHBox, this);
        if(hand.isEmpty()){
            try {
                Init.getClient().getConnection().send(new Message(MessageType.YOU_LOSE));
                winGame();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void winGame() {
        board.clear();
        markers.clear();
        Platform.runLater(this::iniz);
        showResultDialog("Вы выиграли!");

    }

    public void loseGame() {
        board.clear();
        Platform.runLater(this::iniz);
        showResultDialog("Вы проиграли!");
    }
    private void showResultDialog(String message) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(Habitat.class.getResource("winWindow.fxml"));
                Parent root = loader.load();

                winWindowController controller = loader.getController();
                controller.setResultText(message);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Результат игры");
                stage.initModality(Modality.APPLICATION_MODAL); // Устанавливаем модальность
                stage.showAndWait(); // Отображаем окно и ожидаем его закрытия
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void getPlayerTilesHand(){
        hand.clear();
        Collections.shuffle(deck);
        for (int i = 0; i < 7; i++) {
            hand.add(deck.get(i));
            deck.remove(i);
        }
        PlayerHandComponent playerHandComponent = new PlayerHandComponent(hand, this);
        handPane.getChildren().add(playerHandComponent);
    }
    @FXML
    void onClickBackMenu(ActionEvent event){
        try {
            // Загрузка FXML для меню
            FXMLLoader fxmlLoader = new FXMLLoader(Habitat.class.getResource("hello-view.fxml"));
            Parent root = fxmlLoader.load();
            // Получение текущего Stage
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            if(Init.getClient().isHost()){
                Init.getClient().getConnection().send(new Message(MessageType.QUIT_HOST));
            }else{
                Init.getClient().getConnection().send(new Message(MessageType.QUIT));
            }
            Init.getClient().getConnection().close();
            // Установка новой сцены
            stage.setScene(new Scene(root));
            stage.setWidth(620);
            stage.setHeight(430);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @FXML
    void onClickAddTileButton(){
        if(myTurn) giveTile();
    }

    public void setBoard(ArrayList<Tile> board) {
        this.board = board;
        Platform.runLater(this::updateBoard);
        Platform.runLater(this::updateMarkers);
    }

    public void setHost(boolean host) {
        Host = host;
    }

    public void setDeck(ArrayList<Tile> deck) {
        this.deck = deck;
        System.out.println("дека изменилась");
    }
    public void setLastClickEnd(int lastClickEnd) {
        this.lastClickEnd = lastClickEnd;
    }

    public void setLastClickStart(int lastClickStart) {
        this.lastClickStart = lastClickStart;
    }
}

