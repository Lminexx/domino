module com.example.dominofx {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;


    opens com.example.dominofx to javafx.fxml;
    exports com.example.dominofx;
    exports com.example.dominofx.Controllers;
    opens com.example.dominofx.Controllers to javafx.fxml;
}