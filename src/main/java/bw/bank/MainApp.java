package bw.bank;

import bw.bank.view.LoginView;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("BAC Integrated Banking System");
        new LoginView(primaryStage).show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}