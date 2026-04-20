package com.myhabits.service;

import com.myhabits.dao.HabitoDAO;
import com.myhabits.model.Habito;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class HabitoService {

    private Queue<Habito> habitosPendentes; // Hábitos a fazer hoje (LinkedList)
    private Stack<Habito> pilhaDesfazer;    // Desfazer a última conclusão
    private HabitoDAO habitoDAO;

    public HabitoService() {
        this.habitosPendentes = new LinkedList<>();
        this.pilhaDesfazer = new Stack<>();
        this.habitoDAO = new HabitoDAO();
    }

    /**
     * Carrega atributos do utilizador selecionando quem ainda não está concluído hoje.
     * Complexidade Caso Melhor: O(N) onde N é os hábitos na DB.
     * Complexidade Pior Caso: O(N). Linear devido à leitura da DB.
     */
    public void carregarHabitosDoDAO(int userId) {
        habitosPendentes.clear();
        pilhaDesfazer.clear();
        
        List<Habito> habitosDoUsuario = habitoDAO.obterTodosHabitosPorUtilizador(userId);
        LocalDate hoje = LocalDate.now();
        
        for (Habito h : habitosDoUsuario) {
            if (h.getUltimaCompletacao() == null || !h.getUltimaCompletacao().equals(hoje)) {
                habitosPendentes.add(h);
            }
        }
    }

    /**
     * Adiciona hábito na base de dados e lista pendente.
     * Melhor/Pior caso na ED: O(1) considerando que add numa L.List / Queue é O(1). Consulta DB também.
     */
    public void adicionarHabito(Habito h, int userId) {
        if (habitoDAO.adicionarHabito(h, userId)) {
            habitosPendentes.add(h);
        }
    }

    /**
     * Conclui um Hábito específico, retira da fila e coloca na fila de undo.
     * Completa em DB usando id.
     * Melhor caso: O(1) se h estiver no início da fila de pendentes.
     * Pior caso: O(N) devido à remoção de objeto por value no meio da Queue(LinkedList). Stack PUSH é O(1).
     */
    public void concluirHabito(Habito h) {
        if (habitoDAO.marcarHabitoComoConcluido(h.getId())) {
            habitosPendentes.remove(h); 
            pilhaDesfazer.push(h);
        }
    }

    /**
     * Reverte conclusão a retornar último stack POP.
     * Melhor/Pior Caso: O(1) no Stack operation (POP) e O(1) Queue (ADD/OFFER). O(1) base de dados. Total O(1) constante.
     */
    public void desfazerUltimaConclusao() {
        if (!pilhaDesfazer.isEmpty()) {
            Habito h = pilhaDesfazer.pop();
            // desfazerConclusao subtrai -1 streak devolvendo ultima_completacao=null neste mockup.
            if (habitoDAO.desfazerConclusao(h.getId())) {
                habitosPendentes.add(h);
            }
        }
    }

    public Queue<Habito> obterFilaPendentes() {
        return habitosPendentes;
    }

    public Stack<Habito> obterPilhaConcluidos() {
        return pilhaDesfazer;
    }
}
