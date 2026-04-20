package com.myhabits;

import com.myhabits.model.Utilizador;
// import com.myhabits.*;
import com.myhabits.view.LoginView;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class MyHabitsApp extends Application {

    public static Utilizador utilizadorLogado = null;

    @Override
    public void start(Stage primaryStage) {
        LoginView loginView = new LoginView(primaryStage);
        Scene scene = new Scene(loginView, 700, 500);

        // Aplicar stylesheet global
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

        // Ícone da aplicaçã 
        try {
            Image icon = new Image(getClass().getResourceAsStream("/icon.png"));
            primaryStage.getIcons().add(icon);
        } catch (Exception e) {
            // Ícone não encontrado - continua sem ícone
        }

        primaryStage.setTitle("MyHabits - Inicie Sessão");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(700);
        primaryStage.setMinHeight(450);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
