package com.example.dominofx.Controllers;

import java.io.File;
import java.io.IOException;

import com.example.dominofx.Habitat;
import com.example.dominofx.Server.Client;
import com.example.dominofx.Server.Server;
import com.example.dominofx.Init;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.sound.sampled.*;

public class HabitatController {

    @FXML
    private Button backButton;

    @FXML
    private Button connectLobby;

    @FXML
    private Button createLobby;

    @FXML
    private Button exitButton;

    @FXML
    private Button settings;

    @FXML
    private Slider sliderMusic;

    @FXML
    private Label volumeGR;
    @FXML
    private Button backToMenuSecond;
    @FXML
    private Button connectedButton;
    @FXML
    private TextField inputIp;
    @FXML
    private Label textForIP;
    private Client client;

    private boolean playMusic = true;
    private boolean isPlayMusic = false;
    private String[] music;
    private FloatControl volumeControl;
    private int volume = 100;
    private Server server;

    @FXML
    void initialize() {
        backButton.setVisible(false);
        volumeGR.setVisible(false);
        sliderMusic.setVisible(false);
        inputIp.setVisible(false);
        connectedButton.setVisible(false);
        backToMenuSecond.setVisible(false);
        textForIP.setVisible(false);
        // Устанавливаем начальное значение ползунка на максимум
        sliderMusic.setValue(100);

        sliderMusic.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (volumeControl != null) {
                volumeControl.setValue((float) (volumeControl.getMinimum() +
                                        (volumeControl.getMaximum() - volumeControl.getMinimum()) * newValue.doubleValue() / 100.0));
            }
        });
    }
    @FXML
    private void onCreateLobbyClick(ActionEvent event) {
        try {
            // Загрузка FXML для лобби
            createServer();
            FXMLLoader fxmlLoader = new FXMLLoader(Habitat.class.getResource("mainGame.fxml"));
            Parent root = fxmlLoader.load();
            // Получение текущего Stage

            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            // Установка новой сцены
            stage.setScene(new Scene(root));
            stage.setWidth(1000);   //1503
            stage.setHeight(600);   //800
            ipWindow();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void createServer(){
        new Thread(() -> {
            try {
                server = new Server(); // Создаем сервер домино
                server.startServer(this); // Запускаем сервер
            } catch (IOException e) {
                e.printStackTrace(); // Выводим ошибку в случае неудачи
            }
        }).start();
    }
    public void ipWindow(){
        showIpAddress(Init.getClient().getIpAddress());
    }
    private void showIpAddress(String ip) {
        Platform.runLater(() -> {
            try {
                FXMLLoader loader = new FXMLLoader(Habitat.class.getResource("windowServer.fxml"));
                Parent root = loader.load();

                serverWindowController controller = loader.getController();
                controller.setIpText(ip);

                Stage stage = new Stage();
                stage.setScene(new Scene(root));
                stage.setTitle("Создание сервера");
                stage.initModality(Modality.APPLICATION_MODAL); // Устанавливаем модальность
                stage.showAndWait(); // Отображаем окно и ожидаем его закрытия
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
    public void joinGame(String ipAddress){
        client = new Client(ipAddress);
        client.start();
        Init.setClient(client);
    }

    @FXML
    void onConnectToServer(ActionEvent event) throws IOException, InterruptedException {
        String ip = inputIp.getText();
        joinGame(ip);
        Thread.sleep(1000);
        if(client.getConnection()!=null){
            // Загрузка FXML для лобби
            FXMLLoader fxmlLoader = new FXMLLoader(Habitat.class.getResource("mainGame.fxml"));
            Parent root = fxmlLoader.load();
            // Получение текущего Stage
            Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
            // Установка новой сцены
            stage.setScene(new Scene(root));
            stage.setWidth(1000);
            stage.setHeight(600);
        }
    }

    @FXML
    void onConnectLobbyClick() {
        createLobby.setVisible(false);
        connectLobby.setVisible(false);
        settings.setVisible(false);
        exitButton.setVisible(false);
        textForIP.setVisible(true);
        connectedButton.setVisible(true);
        backToMenuSecond.setVisible(true);
        inputIp.setVisible(true);
    }

    @FXML
    void onSettingButtonClick(){
        createLobby.setVisible(false);
        connectLobby.setVisible(false);
        settings.setVisible(false);
        exitButton.setVisible(false);
        backButton.setVisible(true);
        volumeGR.setVisible(true);
        sliderMusic.setVisible(true);
    }

    @FXML
    void onExitButtonClick(){
        System.exit(0);
    }

    @FXML
    void onBackButtonClick() {
        createLobby.setVisible(true);
        connectLobby.setVisible(true);
        settings.setVisible(true);
        exitButton.setVisible(true);
        backButton.setVisible(false);
        volumeGR.setVisible(false);
        sliderMusic.setVisible(false);
        backButton.setVisible(false);
        volumeGR.setVisible(false);
        sliderMusic.setVisible(false);
        inputIp.setVisible(false);
        connectedButton.setVisible(false);
        backToMenuSecond.setVisible(false);
        textForIP.setVisible(false);
    }

    public void getAllMusic() {
        int error = 0, count = 0; // Количество музыкальных треков
        while (error != 1) {
            String audioFilePath = "src/main/resources/com/example/dominofx/Music\\track" + count + ".wav"; // В папке проходятся все файлы, которые называются track + число
            File audioFile = new File(audioFilePath); // Проверяется существование этого файла
            if (audioFile.exists()) { // Если файл существует
                count++;
            } else {
                error = 1;
            }
        }
        music = new String[count];
        for (int i = 0; i < count; i++) { // Проходится каждый музыкальный трек и сохраняется в массив
            music[i] = "src/main/resources/com/example/dominofx/Music\\track" + i + ".wav";
        }
    }

    public void startMusic() {
        if(!isPlayMusic)
        {
            isPlayMusic = true;
            new Thread(() -> { // Создаётся новый поток, который будет запускать музыку
                int i = 0;
                while (playMusic) { // Пока boolean переменная true
                    if (i >= music.length) i = 0; // Зацикленность музыки
                    String audioFilePath = music[i];
                    playAudioTrack(audioFilePath); // Запуск музыки из массива всех треков
                    i++; // Переход к следующей песне
                }
            }).start();
        }
    }

    public void playAudioTrack(String audioFilePath) {
        try {
            // Открываем аудиофайл
            File audioFile = new File(audioFilePath);
            if (!audioFile.exists()) {
                System.err.println("Аудиофайл не найден: " + audioFilePath);
                return;
            }

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile);

            // Получаем формат аудиофайла
            AudioFormat audioFormat = audioInputStream.getFormat();

            // Создаем объект DataLine.Info для получения данных о звуковой карте
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);

            // Получаем звуковую карту
            SourceDataLine sourceDataLine = (SourceDataLine) AudioSystem.getLine(info);

            // Открываем звуковую карту для воспроизведения
            sourceDataLine.open(audioFormat);

            // Получаем контроллер громкости
            if (sourceDataLine.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) sourceDataLine.getControl(FloatControl.Type.MASTER_GAIN);
                // Устанавливаем громкость в зависимости от значения ползунка
                volumeControl.setValue((float) (volumeControl.getMinimum() +
                                        (volumeControl.getMaximum() - volumeControl.getMinimum()) * sliderMusic.getValue() / 100.0));
            }

            // Начинаем воспроизведение
            sourceDataLine.start();

            // Читаем данные из аудиофайла и записываем их в звуковую карту
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = audioInputStream.read(buffer, 0, buffer.length)) != -1) {
                sourceDataLine.write(buffer, 0, bytesRead);
            }

            // Завершаем воспроизведение
            sourceDataLine.drain();
            sourceDataLine.close();
            audioInputStream.close();
        } catch (UnsupportedAudioFileException ex) {
            System.err.println("Неподдерживаемый аудиофайл: " + ex.getMessage());
        } catch (LineUnavailableException ex) {
            System.err.println("Аудиолиния недоступна: " + ex.getMessage());
        } catch (IOException ex) {
            System.err.println("Ошибка ввода-вывода: " + ex.getMessage());
        }
    }

    public Client getClient() {
        return client;
    }
}
