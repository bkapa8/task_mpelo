package com.myhabits.view;

import com.myhabits.MyHabitsApp;
import com.myhabits.dao.UtilizadorDAO;
import com.myhabits.model.Utilizador;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class LoginView extends BorderPane {

    private UtilizadorDAO utilizadorDAO;
    private Stage stage;
    private Label lblFeedback;

    public LoginView(Stage stage) {
        this.stage = stage;
        this.utilizadorDAO = new UtilizadorDAO();

        // --- Fundo com gradiente: centrar o cartão de login
        this.setStyle("-fx-background-color: #03191e;");

        VBox card = new VBox(18);
        card.getStyleClass().add("login-card");
        card.setMaxWidth(420);
        card.setAlignment(Pos.CENTER);

        // Título
        Label lblTitulo = new Label("MyHabits");
        lblTitulo.getStyleClass().add("label-title");
        lblTitulo.setTextAlignment(TextAlignment.CENTER);

        Label lblSub = new Label("Rastreador de Hábitos");
        lblSub.getStyleClass().add("label-subtitle");

        // Feedback
        lblFeedback = new Label("");
        lblFeedback.setWrapText(true);
        lblFeedback.setMaxWidth(340);
        lblFeedback.setTextAlignment(TextAlignment.CENTER);

        // --- Formulário
        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);

        Label lblUser = new Label("Utilizador:");
        TextField txtUser = new TextField();
        txtUser.setPromptText("Nome de utilizador");
        txtUser.setPrefWidth(240);
        form.add(lblUser, 0, 0);
        form.add(txtUser, 1, 0);

        Label lblPw = new Label("Password:");
        PasswordField txtPw = new PasswordField();
        txtPw.setPromptText("Palavra-passe");
        form.add(lblPw, 0, 1);
        form.add(txtPw, 1, 1);


        // --- Botões
        Button btnEntrar = new Button("Entrar");
        btnEntrar.setPrefWidth(115);

        Button btnRegistar = new Button("Registar");
        btnRegistar.getStyleClass().add("button-secondary");
        btnRegistar.setPrefWidth(115);

        HBox hbBotoes = new HBox(12, btnEntrar, btnRegistar);
        hbBotoes.setAlignment(Pos.CENTER_LEFT);

        card.getChildren().addAll(lblTitulo, lblSub, new Separator(), form, hbBotoes, lblFeedback);

        StackPane center = new StackPane(card);
        this.setCenter(center);

        // ---- Ações/eventos ----
        btnEntrar.setOnAction(e -> {
            String user = txtUser.getText().trim();
            String pwd = txtPw.getText();

            if (user.isEmpty() || pwd.isEmpty()) {
                setFeedback("Preencha todos os campos!", false);
                return;
            }

            Utilizador loggedUser = utilizadorDAO.loginUtilizador(user, pwd);
            if (loggedUser != null) {
                MyHabitsApp.utilizadorLogado = loggedUser;
                // Redirecionar conforme tipo
                if ("admin".equalsIgnoreCase(loggedUser.getUserTipo())) {
                    AdminView adminView = new AdminView(stage);
                    javafx.scene.Scene scene = new javafx.scene.Scene(adminView, 900, 650);
                    scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("MyHabits - Painel de Administração");
                } else {
                    MainView mainView = new MainView(stage);
                    javafx.scene.Scene scene = new javafx.scene.Scene(mainView, 900, 650);
                    scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
                    stage.setScene(scene);
                    stage.setTitle("MyHabits - " + loggedUser.getUsername());
                }
                stage.centerOnScreen();
            } else {
                setFeedback("Utilizador ou password incorretos!", false);
            }
        });

        btnRegistar.setOnAction(e -> {
            String user = txtUser.getText().trim();
            String pwd = txtPw.getText();

            if (user.isEmpty() || pwd.isEmpty()) {
                setFeedback("Preencha utilizador e password!", false);
                return;
            }

            Utilizador novo = new Utilizador(user, pwd, user + "@myhabits.com", "normal");
            if (utilizadorDAO.registarUtilizador(novo)) {
                setFeedback("Conta criada com sucesso! Pode iniciar sessão.", true);
            } else {
                setFeedback("Erro ao registar. O utilizador já existe.", false);
            }
        });
    }

    private void setFeedback(String msg, boolean sucesso) {
        lblFeedback.setText(msg);
        lblFeedback.getStyleClass().removeAll("label-success", "label-error");
        lblFeedback.getStyleClass().add(sucesso ? "label-success" : "label-error");
    }
}
