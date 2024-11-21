package com.example.dominofx.Server;

import com.example.dominofx.Init;

import java.io.IOException;
import java.net.Socket;

public class Client {
    private Connection connection;
    private String ipAddress;
    private final int PORT = 12345;
    private boolean isHost = false;
    private boolean myTurn = false;
    public Client(String ipAddress){
        this.ipAddress = ipAddress;
    }


    public class SocketThread extends Thread {
        @Override
        public void run() {
            String serverAddress = getIpAddress();
            int serverPort = getPORT();
            try(Socket socket = new Socket(serverAddress, serverPort)) {
                Connection connection = new Connection(socket);
                Client.this.connection = connection;
                clientMainLoop();
            } catch (IOException e){
                System.out.println("Ошибка подключения клиента к серверу");
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }

        protected void clientMainLoop() throws IOException, ClassNotFoundException {
            while (true) {
                Message message = connection.receive();
                switch (message.getType()) {
                    case HOST:
                        Init.getMainGameController().setHost(true);
                        break;
                    case YOUR_TURN:
                        Init.getMainGameController().updateTurn(true);
                        break;
                    case TURN:
                        Init.getMainGameController().updateTurn(false);
                        break;
                    case BOARD:
                        Init.getMainGameController().setBoard(message.getList());
                        break;
                    case YOU_LOSE:
                        Init.getMainGameController().loseGame();
                        break;
                    case RELOAD:
                        Init.getMainGameController().iniz();
                        break;
                    case DECK:
                        Init.getMainGameController().setDeck(message.getList());
                        System.out.println("дека отправилась противнику");
                        break;
                    case LAST_CLICK:
                        Init.getMainGameController().setLastClickEnd(message.getLastlick());
                        break;
                    case START_CLICK:
                        Init.getMainGameController().setLastClickStart(message.getLastlick());
                        break;
                    default:
                        throw new IOException("Unexpected MessageType");
                }
            }
        }
    }

    public void start() {
        SocketThread socketThread = new SocketThread();
        socketThread.setDaemon(true);
        socketThread.start();
    }

    public Connection getConnection() {
        return connection;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getPORT() {
        return PORT;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }

}