package com.myhabits.dao;

import com.myhabits.model.Utilizador;
import com.myhabits.util.DatabaseConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para a entidade Utilizador.
 * Responsável por separar a lógica de negócio do acesso direto à base de dados.
 */
public class UtilizadorDAO {

    /**
     * Regista um novo utilizador na abse de dados.
     * @param u Entidade Utilizador com as informações a registar
     * @return boolean Sucesso da inserção
     */
    public boolean registarUtilizador(Utilizador u) {
        String sql = "INSERT INTO utilizadores (username, password, email, user_tipo) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, u.getUsername());
            stmt.setString(2, u.getPassword());
            stmt.setString(3, u.getEmail());
            // Por norma, registos na UI definem 'normal'
            stmt.setString(4, u.getUserTipo() != null ? u.getUserTipo() : "normal");
            
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Erro ao registar utilizador: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Autentica o utilizador através de username e password.
     * @param username O username do utilizador
     * @param password A palavra-passe
     * @return O Utilizador autenticado em caso de sucesso, senão devolve null
     */
    public Utilizador loginUtilizador(String username, String password) {
        String sql = "SELECT * FROM utilizadores WHERE username = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Utilizador(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("email"),
                            rs.getString("user_tipo")
                    );
                }
            }
        } catch (SQLException e) {
            System.err.println("Erro ao efetuar login: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Método auxiliar de painel admin para obter todos os utilizadores (apresentação O(N)).
     * @return List de utilizadores contendo a estrutura integral.
     */
    public List<Utilizador> obterTodosUtilizadores() {
        List<Utilizador> lista = new ArrayList<>();
        String sql = "SELECT * FROM utilizadores";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Utilizador u = new Utilizador(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("email"),
                        rs.getString("user_tipo")
                );
                lista.add(u);
            }
        } catch (SQLException e) {
            System.err.println("Erro ao carregar utilizadores: " + e.getMessage());
            e.printStackTrace();
        }
        return lista;
    }
}
