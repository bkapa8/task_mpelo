package com.myhabits.view;

import com.myhabits.MyHabitsApp;
import com.myhabits.dao.HabitoDAO;
import com.myhabits.model.Habito;
import com.myhabits.service.HabitoService;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class MainView extends BorderPane {

    private Stage stage;
    private HabitoService habitoService;
    private HabitoDAO habitoDAO;
    private ObservableList<Habito> habitosPendentesObs;
    private ObservableList<Habito> habitosConcluidosObs;
    private TableView<Habito> tableViewPendentes;
    private TableView<Habito> tableViewConcluidos;
    private Label lblStatusBar;

    // Referências aos labels do Dashboard para atualização em tempo real
    private Label lblDashPendente;
    private Label lblDashConcluidos;
    private Label lblDashStreak;

    // Referências aos labels de Estatísticas para atualização em tempo real
    private Label lblStatTotal;
    private Label lblStatDiarios;
    private Label lblStatSemanais;
    private Label lblStatMensais;
    private Label lblStatStreak;

    public MainView(Stage stage) {
        this.stage = stage;
        this.habitoService = new HabitoService();
        this.habitoDAO = new HabitoDAO();
        this.habitosPendentesObs = FXCollections.observableArrayList();
        this.habitosConcluidosObs = FXCollections.observableArrayList();

        if (MyHabitsApp.utilizadorLogado != null) {
            atualizarTodasListas();
        }

        // --- MenuBar ---
        MenuBar menuBar = criarMenuBar();

        // --- Top Bar ---
        HBox topBar = new HBox();
        topBar.getStyleClass().add("top-bar");
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setSpacing(10);

        String username = MyHabitsApp.utilizadorLogado != null ? MyHabitsApp.utilizadorLogado.getUsername() : "Utilizador";
        Label lblUser = new Label("Utilizador: " + username);
        lblUser.getStyleClass().add("label-subtitle");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button btnLogout = new Button("Terminar Sessao");
        btnLogout.getStyleClass().add("button-danger");
        btnLogout.setOnAction(e -> terminarSessao());

        topBar.getChildren().addAll(lblUser, spacer, btnLogout);

        // --- Abas ---
        TabPane tabPane = new TabPane();

        Tab tabDashboard = new Tab("Dashboard", criarAbaDashboard());
        tabDashboard.setClosable(false);

        Tab tabMeusHabitos = new Tab("Meus Habitos", criarAbaMeusHabitos());
        tabMeusHabitos.setClosable(false);

        Tab tabConcluidos = new Tab("Concluidos", criarAbaConcluidos());
        tabConcluidos.setClosable(false);

        Tab tabAdicionar = new Tab("Adicionar Habito", criarAbaAdicionar());
        tabAdicionar.setClosable(false);

        Tab tabEstatisticas = new Tab("Estatisticas", criarAbaEstatisticas());
        tabEstatisticas.setClosable(false);

        tabPane.getTabs().addAll(tabDashboard, tabMeusHabitos, tabConcluidos, tabAdicionar, tabEstatisticas);

        // Atualiza Dashboard e Estatísticas sempre que se muda de aba
        tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
            if (newTab != null) {
                atualizarTodasListas();
                atualizarStatCards();
            }
        });

        // --- Status Bar ---
        lblStatusBar = new Label("Pronto.");
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
        MenuItem miTerminarSessao = new MenuItem("Terminar Sessao");
        MenuItem miSair = new MenuItem("Sair");
        miTerminarSessao.setOnAction(e -> terminarSessao());
        miSair.setOnAction(e -> System.exit(0));
        menuFicheiro.getItems().addAll(miTerminarSessao, new SeparatorMenuItem(), miSair);

        Menu menuAjuda = new Menu("Ajuda");
        MenuItem miSobre = new MenuItem("Sobre o MyHabits");
        miSobre.setOnAction(e -> showAlert(Alert.AlertType.INFORMATION, "Sobre",
                "MyHabits v1.0\nRastreador de habitos pessoais.\n\nDesenvolvido com JavaFX + MySQL.\nTodos direitos Reservados a nos"));
        menuAjuda.getItems().add(miSobre);

        menuBar.getMenus().addAll(menuFicheiro, menuAjuda);
        return menuBar;
    }

    // ========================= DASHBOARD =========================
    private ScrollPane criarAbaDashboard() {
        String username = MyHabitsApp.utilizadorLogado != null ? MyHabitsApp.utilizadorLogado.getUsername() : "Utilizador";

        Label lblBemVindo = new Label("Bem-vindo(a), " + username + "!");
        lblBemVindo.getStyleClass().add("label-title");

        Label lblSub = new Label("Resumo do dia");
        lblSub.getStyleClass().add("label-subtitle");

        // Labels dos stat-cards (referências de instância para poder atualizar)
        lblDashPendente  = new Label(String.valueOf(habitosPendentesObs.size()));
        lblDashConcluidos = new Label(String.valueOf(habitosConcluidosObs.size()));
        lblDashStreak    = new Label(calcularMaiorStreakPendentes());

        VBox cardPendente   = montarStatCard("Pendentes hoje",   lblDashPendente,  "#20a4f3");
        VBox cardConcluidos = montarStatCard("Concluidos",       lblDashConcluidos,"#59f8e8");
        VBox cardStreak     = montarStatCard("Maior Streak",     lblDashStreak,    "#941c2f");

        HBox cards = new HBox(20, cardPendente, cardConcluidos, cardStreak);
        cards.setAlignment(Pos.CENTER_LEFT);

        VBox content = new VBox(20, lblBemVindo, lblSub, new Separator(), cards);
        content.setPadding(new Insets(25));

        ScrollPane sp = new ScrollPane(content);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #03191e; -fx-background-color: #03191e;");
        return sp;
    }

    /** Monta um stat-card reutilizável com Label de valor externo. */
    private VBox montarStatCard(String titulo, Label lblValor, String cor) {
        lblValor.setStyle("-fx-text-fill: " + cor + "; -fx-font-size: 30px; -fx-font-weight: bold;");
        Label lblTit = new Label(titulo);
        lblTit.setStyle("-fx-text-fill: #c1cfda; -fx-font-size: 12px;");
        VBox card = new VBox(5, lblValor, lblTit);
        card.getStyleClass().add("stat-card");
        card.setAlignment(Pos.CENTER);
        card.setPrefWidth(160);
        return card;
    }

    /** Stat-card simples apenas com texto (para Estatísticas). */
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

    // ========================= MEUS HABITOS (PENDENTES) =========================
    private VBox criarAbaMeusHabitos() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        Label lblTitulo = new Label("Habitos pendentes — clique duplo para concluir");
        lblTitulo.getStyleClass().add("label-subtitle");

        tableViewPendentes = new TableView<>();
        tableViewPendentes.setItems(habitosPendentesObs);
        tableViewPendentes.setPlaceholder(new Label("Sem habitos pendentes para hoje"));
        VBox.setVgrow(tableViewPendentes, Priority.ALWAYS);

        TableColumn<Habito, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nomeHabito"));
        colNome.setPrefWidth(180);

        TableColumn<Habito, String> colDesc = new TableColumn<>("Descricao");
        colDesc.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colDesc.setPrefWidth(200);
        colDesc.setCellFactory(tc -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item.length() > 25 ? item.substring(0, 22) + "..." : item));
            }
        });

        TableColumn<Habito, String> colFreq = new TableColumn<>("Frequencia");
        colFreq.setCellValueFactory(new PropertyValueFactory<>("frequencia"));
        colFreq.setPrefWidth(100);

        TableColumn<Habito, Integer> colPri = new TableColumn<>("Prio.");
        colPri.setCellValueFactory(new PropertyValueFactory<>("prioridade"));
        colPri.setPrefWidth(60);

        TableColumn<Habito, Integer> colStreak = new TableColumn<>("Streak");
        colStreak.setCellValueFactory(new PropertyValueFactory<>("streak"));
        colStreak.setPrefWidth(70);

        tableViewPendentes.getColumns().addAll(colNome, colDesc, colFreq, colPri, colStreak);

        // Duplo clique para concluir
        tableViewPendentes.setRowFactory(tv -> {
            TableRow<Habito> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    concluirSelecionado(row.getItem());
                }
            });
            return row;
        });

        Button btnConcluir = new Button("Concluir");
        Button btnEditar   = new Button("Editar");
        btnEditar.getStyleClass().add("button-secondary");
        Button btnEliminar = new Button("Eliminar");
        btnEliminar.getStyleClass().add("button-danger");
        Button btnDesfazer = new Button("Desfazer Ultima");
        btnDesfazer.getStyleClass().add("button-secondary");
        Button btnAtualizar = new Button("Atualizar");
        btnAtualizar.getStyleClass().add("button-secondary");

        btnConcluir.setOnAction(e -> {
            Habito h = tableViewPendentes.getSelectionModel().getSelectedItem();
            if (h != null) concluirSelecionado(h);
            else setStatus("Selecione um habito primeiro.", false);
        });

        btnEditar.setOnAction(e -> {
            Habito h = tableViewPendentes.getSelectionModel().getSelectedItem();
            if (h != null) abrirDialogoEdicao(h);
            else setStatus("Selecione um habito para editar.", false);
        });

        btnEliminar.setOnAction(e -> {
            Habito h = tableViewPendentes.getSelectionModel().getSelectedItem();
            if (h == null) { setStatus("Selecione um habito para eliminar.", false); return; }
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmacao");
            confirm.setHeaderText("Eliminar habito?");
            confirm.setContentText("Tem a certeza que deseja eliminar \"" + h.getNomeHabito() + "\"?");
            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                if (habitoDAO.eliminarHabito(h.getId())) {
                    atualizarTodasListas();
                    atualizarStatCards();
                    setStatus("Habito eliminado com sucesso.", true);
                } else {
                    setStatus("Erro ao eliminar o habito.", false);
                }
            }
        });

        btnDesfazer.setOnAction(e -> {
            habitoService.desfazerUltimaConclusao();
            atualizarTodasListas();
            atualizarStatCards();
            setStatus("Ultima conclusao desfeita.", true);
        });

        btnAtualizar.setOnAction(e -> { atualizarTodasListas(); atualizarStatCards(); setStatus("Lista atualizada.", true); });

        HBox actionBox = new HBox(10, btnConcluir, btnEditar, btnEliminar, btnDesfazer, btnAtualizar);
        actionBox.setAlignment(Pos.CENTER_LEFT);

        vbox.getChildren().addAll(lblTitulo, tableViewPendentes, actionBox);
        return vbox;
    }

    // ========================= CONCLUIDOS =========================
    private VBox criarAbaConcluidos() {
        VBox vbox = new VBox(15);
        vbox.setPadding(new Insets(20));

        Label lblTitulo = new Label("Historico de habitos concluidos");
        lblTitulo.getStyleClass().add("label-subtitle");

        tableViewConcluidos = new TableView<>();
        tableViewConcluidos.setItems(habitosConcluidosObs);
        tableViewConcluidos.setPlaceholder(new Label("Nenhum habito foi concluido ainda."));
        VBox.setVgrow(tableViewConcluidos, Priority.ALWAYS);

        TableColumn<Habito, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nomeHabito"));
        colNome.setPrefWidth(200);

        TableColumn<Habito, String> colFreq = new TableColumn<>("Frequencia");
        colFreq.setCellValueFactory(new PropertyValueFactory<>("frequencia"));
        colFreq.setPrefWidth(100);

        TableColumn<Habito, Integer> colStreak = new TableColumn<>("Streak");
        colStreak.setCellValueFactory(new PropertyValueFactory<>("streak"));
        colStreak.setPrefWidth(80);

        TableColumn<Habito, LocalDate> colUltima = new TableColumn<>("Ultima Conclusao");
        colUltima.setCellValueFactory(new PropertyValueFactory<>("ultimaCompletacao"));
        colUltima.setPrefWidth(160);

        tableViewConcluidos.getColumns().addAll(colNome, colFreq, colStreak, colUltima);

        Button btnAtualizar = new Button("Atualizar");
        btnAtualizar.getStyleClass().add("button-secondary");
        btnAtualizar.setOnAction(e -> { atualizarTodasListas(); setStatus("Lista de concluidos atualizada.", true); });

        vbox.getChildren().addAll(lblTitulo, tableViewConcluidos, btnAtualizar);
        return vbox;
    }

    // ========================= ADICIONAR HABITO =========================
    private ScrollPane criarAbaAdicionar() {
        Label lblTitulo = new Label("Novo Habito");
        lblTitulo.getStyleClass().add("label-subtitle");

        GridPane form = new GridPane();
        form.setHgap(15);
        form.setVgap(15);
        form.setPadding(new Insets(20));

        Label lblNome = new Label("Nome do Habito:");
        TextField txtNome = new TextField();
        txtNome.setPromptText("Ex: Beber agua, Meditar...");
        txtNome.setPrefWidth(280);
        form.add(lblNome, 0, 0);
        form.add(txtNome, 1, 0);

        Label lblDesc = new Label("Descricao:");
        TextArea txtDesc = new TextArea();
        txtDesc.setPrefRowCount(3);
        txtDesc.setPrefWidth(280);
        txtDesc.setPromptText("Descreva o habito...");
        form.add(lblDesc, 0, 1);
        form.add(txtDesc, 1, 1);

        Label lblFreq = new Label("Frequencia:");
        ComboBox<String> cbFreq = new ComboBox<>();
        cbFreq.getItems().addAll("diario", "semanal", "mensal");
        cbFreq.getSelectionModel().selectFirst();
        cbFreq.setPrefWidth(280);
        form.add(lblFreq, 0, 2);
        form.add(cbFreq, 1, 2);

        Label lblPri = new Label("Prioridade:");
        ToggleGroup tgPrioridade = new ToggleGroup();
        RadioButton rbBaixa = new RadioButton("Baixa");
        RadioButton rbMedia = new RadioButton("Media");
        RadioButton rbAlta  = new RadioButton("Alta");
        rbBaixa.setToggleGroup(tgPrioridade); rbBaixa.setUserData(1);
        rbMedia.setToggleGroup(tgPrioridade); rbMedia.setUserData(2);
        rbAlta.setToggleGroup(tgPrioridade);  rbAlta.setUserData(3);
        rbMedia.setSelected(true);
        HBox hbRadio = new HBox(15, rbBaixa, rbMedia, rbAlta);
        form.add(lblPri, 0, 3);
        form.add(hbRadio, 1, 3);

        Label lblFeedback = new Label("");
        form.add(lblFeedback, 1, 4);

        Button btnAdicionar = new Button("Adicionar");
        Button btnLimpar = new Button("Limpar");
        btnLimpar.getStyleClass().add("button-secondary");
        HBox hbBotoes = new HBox(12, btnAdicionar, btnLimpar);
        form.add(hbBotoes, 1, 5);

        btnLimpar.setOnAction(e -> {
            txtNome.clear(); txtDesc.clear();
            cbFreq.getSelectionModel().selectFirst();
            rbMedia.setSelected(true);
            lblFeedback.setText("");
        });

        btnAdicionar.setOnAction(e -> {
            String nome = txtNome.getText().trim();
            if (nome.isEmpty()) {
                lblFeedback.setText("O nome do habito e obrigatorio.");
                lblFeedback.getStyleClass().removeAll("label-success", "label-error");
                lblFeedback.getStyleClass().add("label-error");
                return;
            }
            int prioridade = tgPrioridade.getSelectedToggle() != null
                    ? (int) tgPrioridade.getSelectedToggle().getUserData() : 2;
            Habito h = new Habito(MyHabitsApp.utilizadorLogado.getId(), nome,
                    txtDesc.getText(), cbFreq.getValue(), prioridade, LocalDate.now(), 0, null);
            habitoService.adicionarHabito(h, MyHabitsApp.utilizadorLogado.getId());
            atualizarTodasListas();
            atualizarStatCards();
            lblFeedback.setText("Habito \"" + nome + "\" adicionado com sucesso!");
            lblFeedback.getStyleClass().removeAll("label-success", "label-error");
            lblFeedback.getStyleClass().add("label-success");
            btnLimpar.fire();
            setStatus("Novo habito adicionado: " + nome, true);
        });

        VBox wrapper = new VBox(15, lblTitulo, new Separator(), form);
        wrapper.setPadding(new Insets(20));
        ScrollPane sp = new ScrollPane(wrapper);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #03191e; -fx-background-color: #03191e;");
        return sp;
    }

    // ========================= ESTATISTICAS =========================
    private ScrollPane criarAbaEstatisticas() {
        Label lblTitulo = new Label("Estatisticas Gerais");
        lblTitulo.getStyleClass().add("label-subtitle");

        // Labels reutilizáveis para tempo real
        lblStatTotal    = new Label("0");
        lblStatDiarios  = new Label("0");
        lblStatSemanais = new Label("0");
        lblStatMensais  = new Label("0");
        lblStatStreak   = new Label("0");

        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(20));

        grid.add(montarStatCard("Total de Habitos", lblStatTotal,    "#20a4f3"), 0, 0);
        grid.add(montarStatCard("Diarios",          lblStatDiarios,  "#59f8e8"), 1, 0);
        grid.add(montarStatCard("Semanais",         lblStatSemanais, "#c1cfda"), 2, 0);
        grid.add(montarStatCard("Mensais",          lblStatMensais,  "#20a4f3"), 0, 1);
        grid.add(montarStatCard("Maior Streak",     lblStatStreak,   "#941c2f"), 1, 1);

        // Populate initial values
        atualizarStatCards();

        VBox wrapper = new VBox(15, lblTitulo, new Separator(), grid);
        wrapper.setPadding(new Insets(20));
        ScrollPane sp = new ScrollPane(wrapper);
        sp.setFitToWidth(true);
        sp.setStyle("-fx-background: #03191e; -fx-background-color: #03191e;");
        return sp;
    }

    // ========================= DIALOGO EDICAO =========================
    private void abrirDialogoEdicao(Habito h) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Habito");
        dialog.setHeaderText("Editar: " + h.getNomeHabito());
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        GridPane form = new GridPane();
        form.setHgap(12);
        form.setVgap(12);
        form.setPadding(new Insets(20));

        TextField txtNome = new TextField(h.getNomeHabito());
        TextArea txtDesc  = new TextArea(h.getDescricao());
        txtDesc.setPrefRowCount(3);

        ComboBox<String> cbFreq = new ComboBox<>();
        cbFreq.getItems().addAll("diario", "semanal", "mensal");
        cbFreq.setValue(h.getFrequencia());

        ToggleGroup tg = new ToggleGroup();
        RadioButton rbBaixa = new RadioButton("Baixa");
        RadioButton rbMedia = new RadioButton("Media");
        RadioButton rbAlta  = new RadioButton("Alta");
        rbBaixa.setToggleGroup(tg); rbBaixa.setUserData(1);
        rbMedia.setToggleGroup(tg); rbMedia.setUserData(2);
        rbAlta.setToggleGroup(tg);  rbAlta.setUserData(3);
        if      (h.getPrioridade() == 1) rbBaixa.setSelected(true);
        else if (h.getPrioridade() == 3) rbAlta.setSelected(true);
        else                              rbMedia.setSelected(true);

        form.add(new Label("Nome:"),       0, 0); form.add(txtNome,                    1, 0);
        form.add(new Label("Descricao:"),  0, 1); form.add(txtDesc,                    1, 1);
        form.add(new Label("Frequencia:"), 0, 2); form.add(cbFreq,                     1, 2);
        form.add(new Label("Prioridade:"), 0, 3); form.add(new HBox(10, rbBaixa, rbMedia, rbAlta), 1, 3);

        dialog.getDialogPane().setContent(form);
        dialog.getDialogPane().setStyle("-fx-background-color: #0d2d35;");

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String novoNome = txtNome.getText().trim();
            if (novoNome.isEmpty()) { setStatus("O nome nao pode estar vazio.", false); return; }
            int prio = tg.getSelectedToggle() != null ? (int) tg.getSelectedToggle().getUserData() : 2;
            h.setNomeHabito(novoNome);
            h.setDescricao(txtDesc.getText());
            h.setFrequencia(cbFreq.getValue());
            h.setPrioridade(prio);
            if (habitoDAO.atualizarHabito(h)) {
                atualizarTodasListas();
                atualizarStatCards();
                setStatus("Habito \"" + novoNome + "\" actualizado.", true);
            } else {
                setStatus("Erro ao actualizar habito.", false);
            }
        }
    }

    // ========================= UTILITARIOS =========================
    private void atualizarTodasListas() {
        int uid = MyHabitsApp.utilizadorLogado.getId();
        habitoService.carregarHabitosDoDAO(uid);
        habitosPendentesObs.clear();
        habitosPendentesObs.addAll(habitoService.obterFilaPendentes());
        habitosConcluidosObs.clear();
        habitosConcluidosObs.addAll(habitoDAO.obterHabitosConcluidos(uid));
    }

    /** Atualiza os valores dos stat-cards do Dashboard e da aba Estatísticas em tempo real. */
    private void atualizarStatCards() {
        // Dashboard
        if (lblDashPendente != null)  lblDashPendente.setText(String.valueOf(habitosPendentesObs.size()));
        if (lblDashConcluidos != null) lblDashConcluidos.setText(String.valueOf(habitosConcluidosObs.size()));
        if (lblDashStreak != null)    lblDashStreak.setText(calcularMaiorStreakPendentes());

        // Estatísticas (todos os hábitos, incluindo concluídos)
        if (lblStatTotal == null) return;
        List<Habito> todos = habitoDAO.obterTodosHabitosPorUtilizador(MyHabitsApp.utilizadorLogado.getId());
        long diarios  = todos.stream().filter(h -> "diario".equals(h.getFrequencia())).count();
        long semanais = todos.stream().filter(h -> "semanal".equals(h.getFrequencia())).count();
        long mensais  = todos.stream().filter(h -> "mensal".equals(h.getFrequencia())).count();
        int  maxStreak = todos.stream().mapToInt(Habito::getStreak).max().orElse(0);
        lblStatTotal.setText(String.valueOf(todos.size()));
        lblStatDiarios.setText(String.valueOf(diarios));
        lblStatSemanais.setText(String.valueOf(semanais));
        lblStatMensais.setText(String.valueOf(mensais));
        lblStatStreak.setText(String.valueOf(maxStreak));
    }

    private String calcularMaiorStreakPendentes() {
        int max = habitosPendentesObs.stream().mapToInt(Habito::getStreak).max().orElse(0);
        // Também considera os já concluídos
        int maxC = habitosConcluidosObs.stream().mapToInt(Habito::getStreak).max().orElse(0);
        return String.valueOf(Math.max(max, maxC));
    }

    private void concluirSelecionado(Habito h) {
        habitoService.concluirHabito(h);
        atualizarTodasListas();
        atualizarStatCards();
        setStatus("Habito \"" + h.getNomeHabito() + "\" marcado como concluido!", true);
    }

    private void terminarSessao() {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Terminar Sessao");
        confirm.setHeaderText("Deseja realmente terminar sessao?");
        Optional<ButtonType> result = confirm.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
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

    private void showAlert(Alert.AlertType type, String titulo, String msg) {
        Alert a = new Alert(type);
        a.setTitle(titulo);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
