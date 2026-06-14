package com.example.auth;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class AuthServiceApplication {
    static {
        ensureDatabaseExists();
    }

    public static void main(String[] args) {
        SpringApplication.run(AuthServiceApplication.class, args);
    }

    private static void ensureDatabaseExists() {
        String url = "jdbc:postgresql://localhost:5432/postgres";
        String user = "postgres";
        String password = "123";
        String dbName = "omni_gym";

        try {
            Class.forName("org.postgresql.Driver");
            try (Connection conn = DriverManager.getConnection(url, user, password);
                 Statement stmt = conn.createStatement()) {
                
                String checkSql = "SELECT 1 FROM pg_database WHERE datname = '" + dbName + "'";
                try (ResultSet rs = stmt.executeQuery(checkSql)) {
                    if (!rs.next()) {
                        System.out.println("Banco de dados '" + dbName + "' nao existe. Criando...");
                        stmt.executeUpdate("CREATE DATABASE " + dbName);
                        System.out.println("Banco de dados '" + dbName + "' criado com sucesso.");
                    } else {
                        System.out.println("Banco de dados '" + dbName + "' ja existe.");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao verificar/criar banco de dados: " + e.getMessage());
        }
    }
}

