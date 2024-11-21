package com.example.dominofx;

import com.example.dominofx.Controllers.HabitatController;
import com.example.dominofx.Controllers.MainGameController;
import com.example.dominofx.Server.Client;

public class Init {
    private static Client client;
    private static HabitatController habitatController;
    private static MainGameController mainGameController;

    public static Client getClient() {
        return client;
    }

    public static void setClient(Client client) {
        Init.client = client;
    }

    public static HabitatController getHabitatController() {
        return habitatController;
    }

    public static void setHabitatController(HabitatController habitatController) {
        Init.habitatController = habitatController;
    }

    public static MainGameController getMainGameController() {
        return mainGameController;
    }

    public static void setMainGameController(MainGameController mainGameController) {
        Init.mainGameController = mainGameController;
    }
}
