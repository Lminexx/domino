package com.example.dominofx.Server;

import com.example.dominofx.Controllers.HabitatController;
import com.example.dominofx.Habitat;
import com.example.dominofx.Init;
import com.example.dominofx.Tile;
import org.w3c.dom.ls.LSOutput;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.BindException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Server {
    public static final int PORT = 12345; // Порт, который будет использовать сервер
    private final List<Connection> clients = Collections.synchronizedList(new ArrayList<>()); // Список клиентов, подключенных к серверу
    private int currentPlayerIndex = 0; // Индекс текущего игрока
    private int anotherPlayer = 1;
    public String ipAddress; // IP-адрес сервера
    private ServerSocket serverSocket; // Серверный сокет для принятия соединений
    private HabitatController game; // Объект игры, представляющий игровую среду
    private ArrayList<Tile> deck = new ArrayList<>();

    // Метод для запуска сервера
    public void startServer(HabitatController habitat) throws IOException {
        try {
            serverSocket = new ServerSocket(PORT); // Создание серверного сокета на указанном порту
            ipAddress = InetAddress.getLocalHost().getHostAddress(); // Получение IP-адреса сервера
            habitat.joinGame(ipAddress); // Автоматически подключаемся к игре как клиент
            habitat.getClient().setHost(true);
            game = habitat; // Сохранение объекта игры
            System.out.println("Сервер запущен на IP: " + ipAddress);
            System.out.println("Ожидаем подключения игроков...");

            // Бесконечный цикл для ожидания подключения новых клиентов
            while (true) {
                Socket socket = serverSocket.accept(); // Принятие нового подключения
                System.out.println("Новый игрок подключился");
                ClientHandler clientHandler = new ClientHandler(socket); // Создание обработчика для нового клиента
                clientHandler.start(); // Запуск нового потока для обработки клиента
            }
        } catch (BindException e) {
            e.printStackTrace(); // Обработка исключения, если порт уже занят
        }
    }

    private void startGame() throws IOException {
        clients.get(currentPlayerIndex).send(new Message(MessageType.YOUR_TURN)); // Уведомление текущего игрока о его ходе
        clients.get(anotherPlayer).send(new Message(MessageType.TURN));
    }
    private void disconnectClient(ClientHandler client) {
        try {
            clients.get(1).close(); // Закрытие сокета клиента
            clients.remove(1);
        } catch (IOException e) {
            e.printStackTrace(); // Обработка исключения при закрытии сокета
        }
    }

    // Внутренний класс для обработки клиента
    private class ClientHandler extends Thread {
        private Socket socket;
        private boolean running;

        // Конструктор класса ClientHandler
        public ClientHandler(Socket socket) throws IOException {
            this.socket = socket; // Сохранение сокета клиента
            running = true; // Флаг для обозначения, что обработчик работает
        }
        private void userConnecting(Connection connection) throws IOException {
            clients.add(connection);
        }

        private void serverMainLoop(Connection connection) throws IOException, ClassNotFoundException {
            while (true) {
                Message gettedMessage = connection.receive();
                    switch (gettedMessage.getType()){
                        case QUIT_HOST -> serverSocket.close();
                        case QUIT ->{
                            disconnectClient(this);
                            clients.get(0).send(new Message(MessageType.RELOAD));
                        }
                        case CONNECT_TO_GAME -> {
                            if(clients.size() == 1){
                                clients.get(currentPlayerIndex).send(new Message(MessageType.HOST));
                            }
                            if(clients.size()==2){
                                startGame();
                            }
                        }
                        case LAST_CLICK, START_CLICK -> clients.get(anotherPlayer).send(gettedMessage);
                        case SWITCH_TURN -> {
                            int temp = currentPlayerIndex;
                            currentPlayerIndex = anotherPlayer;
                            anotherPlayer = temp;
                            startGame();
                        }
                        case BOARD -> {
                            clients.get(anotherPlayer).send(gettedMessage);
                            System.out.println("отправил борд клиентам");
                        }
                        case YOU_LOSE-> clients.get(anotherPlayer).send(gettedMessage);
                    }
            }
        }



        // Метод, который выполняется в отдельном потоке для обработки сообщений от клиента
        @Override
        public void run() {
            System.out.println("Установлено новое соединение с " + socket.getRemoteSocketAddress());
            try(Connection connection = new Connection(socket)) {
                userConnecting(connection);
                serverMainLoop(connection);
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
