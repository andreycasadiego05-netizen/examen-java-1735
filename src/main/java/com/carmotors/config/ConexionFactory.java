package com.carmotors.config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class ConexionFactory {

    private static Connection connection;

    private ConexionFactory() {
    }

    public static synchronized Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                Properties props = cargarPropiedades();
                String defaultUrl = props.getProperty("db.url", "jdbc:mysql:" + "/" + "/localhost:3306/carmotors_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC");
                String url = System.getProperty("db.url", System.getenv().getOrDefault("DB_URL", defaultUrl));
                String user = System.getProperty("db.user", System.getenv().getOrDefault("DB_USER", props.getProperty("db.user", "root")));
                String password = System.getProperty("db.password", System.getenv().getOrDefault("DB_PASSWORD", props.getProperty("db.password", "root")));

                connection = DriverManager.getConnection(url, user, password);
            }
            return connection;
        } catch (SQLException e) {
            throw new RuntimeException("Error al conectar con la base de datos: " + e.getMessage(), e);
        }
    }

    public static synchronized void setConnection(Connection nuevaConexion) {
        connection = nuevaConexion;
    }

    public static synchronized void cerrarConexion() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                throw new RuntimeException("Error al cerrar la conexión con la base de datos", e);
            } finally {
                connection = null;
            }
        }
    }

    private static Properties cargarPropiedades() {
        Properties properties = new Properties();
        try (InputStream input = ConexionFactory.class.getClassLoader().getResourceAsStream("database.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException ignored) {
        }
        return properties;
    }
}
