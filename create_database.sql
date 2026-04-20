CREATE DATABASE IF NOT EXISTS myhabits_db;
USE myhabits_db;

CREATE TABLE IF NOT EXISTS utilizadores (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL,
    user_tipo ENUM('admin', 'normal') DEFAULT 'normal'
);

CREATE TABLE IF NOT EXISTS tarefas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    nome_habito VARCHAR(255) NOT NULL,
    descricao TEXT,
    frequencia ENUM('diario', 'semanal', 'mensal') NOT NULL,
    prioridade INT,
    data_criacao DATE NOT NULL,
    streak INT DEFAULT 0,
    ultima_completacao DATE,
    FOREIGN KEY (user_id) REFERENCES utilizadores(id)
);

INSERT INTO utilizadores (username, password, email, user_tipo) VALUES
('Marja', 'Senha123!', 'joao@uor.com', 'normal'),
('Joel', 'Senha123!', 'marcolino@uor.com', 'normal'),
('Marcos', 'Admin123!', 'admin1@uor.com', 'admin'),
('Arafat', 'Admin123!', 'admin2@uor.com', 'admin'),
('Arthur', 'Senha123!', 'santos@uor.com', 'normal'),
('Bruce', 'Senha123!', 'wayne@uor.com', 'normal'),
('Peter', 'Senha123!', 'parker@uor.com', 'normal');



INSERT INTO tarefas (user_id, nome_habito, descricao, frequencia, prioridade, data_criacao, streak, ultima_completacao) VALUES
(2, 'Beber água', 'Beber pelo menos 2 litros de água', 'diario', 1, CURDATE(), 0, NULL),
(2, 'Fazer exercícios', '30 minutos de academia', 'diario', 2, CURDATE(), 0, NULL),
(2, 'Ler um livro', 'Ler pelo menos 10 páginas', 'diario', 3, CURDATE(), 0, NULL),
(2, 'Reunião de equipa', 'Reunião com a equipa de projeto', 'semanal', 2, CURDATE(), 0, NULL),
(2, 'Pagar contas', 'Pagar todas as contas do mês', 'mensal', 1, CURDATE(), 0, NULL), 

(3, 'Beber água', 'Beber pelo menos 2 litros de água', 'diario', 1, CURDATE(), 0, NULL),
(3, 'Fazer exercícios', '30 minutos de academia', 'diario', 2, CURDATE(), 0, NULL),
(3, 'Ler um livro', 'Ler pelo menos 10 páginas', 'diario', 3, CURDATE(), 0, NULL),
(3, 'Reunião de equipa', 'Reunião com a equipa de projeto', 'semanal', 2, CURDATE(), 0, NULL),
(3, 'Pagar contas', 'Pagar todas as contas do mês', 'mensal', 1, CURDATE(), 0, NULL),

(4, 'Beber água', 'Beber pelo menos 2 litros de água', 'diario', 1, CURDATE(), 0, NULL),
(4, 'Fazer exercícios', '30 minutos de academia', 'diario', 2, CURDATE(), 0, NULL),
(4, 'Ler um livro', 'Ler pelo menos 10 páginas', 'diario', 3, CURDATE(), 0, NULL),
(4, 'Reunião de equipa', 'Reunião com a equipa de projeto', 'semanal', 2, CURDATE(), 0, NULL),
(4, 'Pagar contas', 'Pagar todas as contas do mês', 'mensal', 1, CURDATE(), 0, NULL),

(5, 'Beber água', 'Beber pelo menos 2 litros de água', 'diario', 1, CURDATE(), 0, NULL),
(5, 'Fazer exercícios', '30 minutos de academia', 'diario', 2, CURDATE(), 0, NULL),
(5, 'Ler um livro', 'Ler pelo menos 10 páginas', 'diario', 3, CURDATE(), 0, NULL),
(5, 'Reunião de equipa', 'Reunião com a equipa de projeto', 'semanal', 2, CURDATE(), 0, NULL),
(5, 'Pagar contas', 'Pagar todas as contas do mês', 'mensal', 1, CURDATE(), 0, NULL);
