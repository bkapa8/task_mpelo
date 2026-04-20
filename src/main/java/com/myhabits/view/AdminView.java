package com.myhabits.view;

import com.myhabits.MyHabitsApp;
import com.myhabits.dao.HabitoDAO;
import com.myhabits.dao.UtilizadorDAO;
import com.myhabits.model.Habito;
import com.myhabits.model.Utilizador;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.util.List;
import java.util.Optional;

public class AdminView extends BorderPane {

    private Stage stage;
    private UtilizadorDAO utilizadorDAO;
    private ObservableList<Utilizador> utilizadoresObs;
    private ObservableList<Habito> habitosObs;
    private TableView<Utilizador> tabelaUsers;
    private TableView<Habito> tabelaHabitos;
    private Label lblStatusBar;

    public AdminView(Stage stage) {
        this.stage = stage;
        this.utilizadorDAO = new UtilizadorDAO();
        this.utilizadoresObs = FXCollections.observableArrayList();
        this.habitosObs = FXCollections.observableArrayList();

        carregarUtilizadores();

        // MenuBar
        MenuBar menuBar = criarMenuBar();

        // Top Bar
        HBox topBar = new HBox();
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);

        Label lblTitulo = new Label("⚙  Painel de Administração");
        lblTitulo.getStyleClass().add("label-title");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("Terminar Sessão");
        btnLogout.getStyleClass().add("button-danger");
        btnLogout.setOnAction(e -> terminarSessao());

        topBar.getChildren().addAll(lblTitulo, spacer, btnLogout);

        // Abas
        TabPane tabPane = new TabPane();

        Tab tabUtilizadores = new Tab("👥  Utilizadores", criarAbaUtilizadores());
        tabUtilizadores.setClosable(false);

        Tab tabTodosHabitos = new Tab("📋  Todos os Hábitos", criarAbaTodosHabitos());
        tabTodosHabitos.setClosable(false);

        Tab tabEstatisticas = new Tab("📊  Estatísticas Globais", criarAbaEstatisticas());
        tabEstatisticas.setClosable(false);

        tabPane.getTabs().addAll(tabUtilizadores, tabTodosHabitos, tabEstatisticas);

        // Status bar
        lblStatusBar = new Label("Painel de Admin activo.");
        lblStatusBar.setStyle("-fx-text-fill: #59f8e8; -fx-padding: 4px 12px; -fx-background-color: #0a2128;");
        HBox statusBar = new HBox(lblStatusBar);
        statusBar.setStyle("-fx-background-color: #0a2128; -fx-border-color: #20a4f3; -fx-border-width: 1px 0 0 0;");

        VBox top = new VBox(menuBar, topBar);
        this.setTop(top);
        this.setCenter(tabPane);
        this.setBottom(statusBar);
    }

    // ========================= MENU BAR =========================
    private MenuBar criarMenuBar() {
        MenuBar menuBar = new MenuBar();

        Menu menuFicheiro = new Menu("Ficheiro");
        MenuItem miSair = new MenuItem("Sair");
        miSair.setOnAction(e -> System.exit(0));
        menuFicheiro.getItems().addAll(new MenuItem("Terminar Sessão"), new SeparatorMenuItem(), miSair);
        ((MenuItem) menuFicheiro.getItems().get(0)).setOnAction(e -> terminarSessao());

        Menu menuAjuda = new Menu("Ajuda");
        MenuItem miSobre = new MenuItem("Sobre");
        miSobre.setOnAction(e -> new Alert(Alert.AlertType.INFORMATION, "MyHabits Admin v1.0").showAndWait());
        menuAjuda.getItems().add(miSobre);

        menuBar.getMenus().addAll(menuFicheiro, menuAjuda);
        return menuBar;
    }

    // ========================= ABA UTILIZADORES =========================
    private VBox criarAbaUtilizadores() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        Label lblTit = new Label("Lista de Utilizadores");
        lblTit.getStyleClass().add("label-subtitle");

        tabelaUsers = new TableView<>();
        tabelaUsers.setItems(utilizadoresObs);
        VBox.setVgrow(tabelaUsers, Priority.ALWAYS);

        TableColumn<Utilizador, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Utilizador, String> colUsername = new TableColumn<>("Username");
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colUsername.setPrefWidth(150);

        TableColumn<Utilizador, String> colEmail = new TableColumn<>("Email");
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colEmail.setPrefWidth(200);

        TableColumn<Utilizador, String> colTipo = new TableColumn<>("Tipo");
        colTipo.setCellValueFactory(new PropertyValueFactory<>("userTipo"));
        colTipo.setPrefWidth(100);

        tabelaUsers.getColumns().addAll(colId, colUsername, colEmail, colTipo);

        // Ao selecionar utilizador, mostra seus hábitos na aba correspondente
        tabelaUsers.getSelectionModel().selectedItemProperty().addListener((obs, ant, sel) -> {
            if (sel != null) carregarHabitosDeUtilizador(sel.getId());
        });

        Button btnAtualizar = new Button("🔄 Atualizar");
        btnAtualizar.getStyleClass().add("button-secondary");
        btnAtualizar.setOnAction(e -> { carregarUtilizadores(); setStatus("Lista atualizada.", true); });

        Button btnEliminar = new Button("🗑 Eliminar Utilizador");
        btnEliminar.getStyleClass().add("button-danger");
        btnEliminar.setOnAction(e -> {
            Utilizador sel = tabelaUsers.getSelectionModel().getSelectedItem();
            if (sel == null) { setStatus("Selecione um utilizador.", false); return; }
            if ("admin".equalsIgnoreCase(sel.getUserTipo())) {
                setStatus("Não é possível eliminar um administrador.", false); return;
            }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                    "Eliminar utilizador \"" + sel.getUsername() + "\"?");
            Optional<ButtonType> res = confirm.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                // Eliminar via SQL direto (adicione método ao DAO se quiser)
                setStatus("Utilizador " + sel.getUsername() + " eliminado (simulado).", true);
                carregarUtilizadores();
            }
        });

        HBox botoes = new HBox(10, btnAtualizar, btnEliminar);
        vbox.getChildren().addAll(lblTit, tabelaUsers, botoes);
        return vbox;
    }

    // ========================= ABA TODOS OS HÁBITOS =========================
    private VBox criarAbaTodosHabitos() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        Label lblTit = new Label("Todos os hábitos (selecione utilizador na aba anterior)");
        lblTit.getStyleClass().add("label-subtitle");

        tabelaHabitos = new TableView<>();
        tabelaHabitos.setItems(habitosObs);
        tabelaHabitos.setPlaceholder(new Label("Selecione um utilizador para ver os seus hábitos."));
        VBox.setVgrow(tabelaHabitos, Priority.ALWAYS);

        TableColumn<Habito, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setPrefWidth(50);

        TableColumn<Habito, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nomeHabito"));
        colNome.setPrefWidth(180);

        TableColumn<Habito, String> colFreq = new TableColumn<>("Frequência");
        colFreq.setCellValueFactory(new PropertyValueFactory<>("frequencia"));
        colFreq.setPrefWidth(100);

        TableColumn<Habito, Integer> colStreak = new TableColumn<>("🔥 Streak");
        colStreak.setCellValueFactory(new PropertyValueFactory<>("streak"));
        colStreak.setPrefWidth(80);

        TableColumn<Habito, Object> colData = new TableColumn<>("Data Criação");
        colData.setCellValueFactory(new PropertyValueFactory<>("dataCriacao"));
        colData.setPrefWidth(130);

        tabelaHabitos.getColumns().addAll(colId, colNome, colFreq, colStreak, colData);

        vbox.getChildren().addAll(lblTit, tabelaHabitos);
        return vbox;
    }

    // ========================= ABA ESTATÍSTICAS =========================
    private ScrollPane criarAbaEstatisticas() {
        List<Utilizador> users = utilizadorDAO.obterTodosUtilizadores();
        HabitoDAO habitoDAO = new HabitoDAO();

        int totalUsers = users.size();
        int totalHabitos = users.stream()
                .mapToInt(u -> habitoDAO.obterTodosHabitosPorUtilizador(u.getId()).size())
                .sum();
        long admins = users.stream().filter(u -> "admin".equalsIgnoreCase(u.getUserTipo())).count();

        Label lblTit = new Label("Estatísticas Globais");
        lblTit.getStyleClass().add("label-subtitle");

        HBox cards = new HBox(20,
                criarStatCard("Utilizadores", String.valueOf(totalUsers), "#20a4f3"),
                criarStatCard("Hábitos Totais", String.valueOf(totalHabitos), "#59f8e8"),
                criarStatCard("Admins", String.valueOf(admins), "#941c2f")
        );
        cards.setAlignment(Pos.CENTER_LEFT);

        VBox wrapper = new VBox(20, lblTit, new Separator(), cards);
        wrapper.setPadding(new Insets(25));

        ScrollPane sp = new ScrollPane(wrapper);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #03191e; -fx-background-color: #03191e;");
        return sp;
    }

    // ========================= UTILITÁRIOS =========================
    private VBox criarStatCard(String titulo, String valor, String cor) {
        Label lblValor = new Label(valor);
        lblValor.setStyle("-fx-text-fill: " + cor + "; -fx-font-size: 30px; -fx-font-weight: bold;");
        Label lblTit = new Label(titulo);
        lblTit.setStyle("-fx-text-fill: #c1cfda; -fx-font-size: 12px;");

        VBox card = new VBox(5, lblValor, lblTit);
        card.getStyleClass().add("stat-card");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(160);
        return card;
    }

    private void carregarUtilizadores() {
        utilizadoresObs.clear();
        utilizadoresObs.addAll(utilizadorDAO.obterTodosUtilizadores());
    }

    private void carregarHabitosDeUtilizador(int userId) {
        HabitoDAO dao = new HabitoDAO();
        habitosObs.clear();
        habitosObs.addAll(dao.obterTodosHabitosPorUtilizador(userId));
    }

    private void terminarSessao() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION, "Deseja terminar a sessão de administrador?");
        Optional<ButtonType> res = confirm.showAndWait();
        if (res.isPresent() && res.get() == ButtonType.OK) {
            MyHabitsApp.utilizadorLogado = null;
            LoginView loginView = new LoginView(stage);
            javafx.scene.Scene scene = new javafx.scene.Scene(loginView, 700, 500);
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            stage.setScene(scene);
            stage.setTitle("MyHabits - Login");
            stage.centerOnScreen();
        }
    }

    private void setStatus(String msg, boolean ok) {
        if (lblStatusBar != null) {
            lblStatusBar.setText(msg);
            lblStatusBar.setStyle(ok
                    ? "-fx-text-fill: #59f8e8; -fx-padding: 4px 12px; -fx-background-color: #0a2128;"
                    : "-fx-text-fill: #941c2f; -fx-padding: 4px 12px; -fx-background-color: #0a2128;");
        }
    }
}
