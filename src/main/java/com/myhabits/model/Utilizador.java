package com.myhabits.model;

/**
 * Representa um utilizador no sistema.
 * Segue o paradigma da Orientação a Objetos (OOP) ao encapsular as propriedades,
 * protegendo-as de acesso direto através do uso de modificadores private e
 * providenciando acesso seguro via getters e setters.
 */
public class Utilizador {
    private int id;
    private String username;
    private String password;
    private String email;
    private String userTipo; // 'admin' ou 'normal'

    public Utilizador() {
    }

    public Utilizador(int id, String username, String password, String email, String userTipo) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.email = email;
        this.userTipo = userTipo;
    }

    public Utilizador(String username, String password, String email, String userTipo) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.userTipo = userTipo;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getUserTipo() { return userTipo; }
    public void setUserTipo(String userTipo) { this.userTipo = userTipo; }

    @Override
    public String toString() {
        return "Utilizador{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", userTipo='" + userTipo + '\'' +
                '}';
    }
}
