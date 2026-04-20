package com.myhabits.dao;

import com.myhabits.model.Habito;
import com.myhabits.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HabitoDAO {

    public boolean adicionarHabito(Habito h, int userId) {
        String sql = "INSERT INTO tarefas (user_id, nome_habito, descricao, frequencia, prioridade, data_criacao, streak, ultima_completacao) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setString(2, h.getNomeHabito());
            stmt.setString(3, h.getDescricao());
            stmt.setString(4, h.getFrequencia());
            stmt.setInt(5, h.getPrioridade());
            stmt.setDate(6, Date.valueOf(h.getDataCriacao()));
            stmt.setInt(7, h.getStreak());
            if (h.getUltimaCompletacao() != null) {
                stmt.setDate(8, Date.valueOf(h.getUltimaCompletacao()));
            } else {
                stmt.setNull(8, java.sql.Types.DATE);
            }
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Habito> obterTodosHabitosPorUtilizador(int userId) {
        List<Habito> habitos = new ArrayList<>();
        String sql = "SELECT * FROM tarefas WHERE user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    habitos.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return habitos;
    }

    /** Hábitos que já foram concluídos pelo menos uma vez (ultima_completacao NOT NULL). */
    public List<Habito> obterHabitosConcluidos(int userId) {
        List<Habito> habitos = new ArrayList<>();
        String sql = "SELECT * FROM tarefas WHERE user_id = ? AND ultima_completacao IS NOT NULL ORDER BY ultima_completacao DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    habitos.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return habitos;
    }

    public boolean marcarHabitoComoConcluido(int habitId) {
        String sql = "UPDATE tarefas SET ultima_completacao = CURDATE(), streak = streak + 1 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, habitId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean desfazerConclusao(int habitId) {
        String sql = "UPDATE tarefas SET ultima_completacao = NULL, streak = GREATEST(streak - 1, 0) WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, habitId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean eliminarHabito(int habitId) {
        String sql = "DELETE FROM tarefas WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, habitId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean atualizarStreak(int habitId) {
        String sql = "UPDATE tarefas SET streak = streak + 1 WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, habitId);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /** Atualiza nome, descrição, frequência e prioridade de um hábito existente. */
    public boolean atualizarHabito(Habito h) {
        String sql = "UPDATE tarefas SET nome_habito = ?, descricao = ?, frequencia = ?, prioridade = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, h.getNomeHabito());
            stmt.setString(2, h.getDescricao());
            stmt.setString(3, h.getFrequencia());
            stmt.setInt(4, h.getPrioridade());
            stmt.setInt(5, h.getId());
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Método auxiliar para mapear uma linha do ResultSet
    private Habito mapRow(ResultSet rs) throws SQLException {
        return new Habito(
                rs.getInt("id"),
                rs.getInt("user_id"),
                rs.getString("nome_habito"),
                rs.getString("descricao"),
                rs.getString("frequencia"),
                rs.getInt("prioridade"),
                rs.getDate("data_criacao").toLocalDate(),
                rs.getInt("streak"),
                rs.getDate("ultima_completacao") != null ? rs.getDate("ultima_completacao").toLocalDate() : null
        );
    }
}
