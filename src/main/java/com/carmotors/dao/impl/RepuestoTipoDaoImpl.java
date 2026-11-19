package com.carmotors.DAO.impl;

import com.carmotors.config.ConexionFactory;
import com.carmotors.DAO.RepuestoTipoDao;
import com.carmotors.modelo.RepuestoTipo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RepuestoTipoDaoImpl implements RepuestoTipoDao {

    @Override
    public void guardar(RepuestoTipo repuestoTipo) {
        String sql = "INSERT INTO repuesto_tipos (nombre) VALUES (?)";
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, repuestoTipo.getNombre());
            stmt.executeUpdate();
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    repuestoTipo.setId(generatedKeys.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al guardar tipo de repuesto", e);
        }
    }

    @Override
    public Optional<RepuestoTipo> buscarPorId(int id) {
        String sql = "SELECT id, nombre FROM repuesto_tipos WHERE id = ?";
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearRepuestoTipo(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar tipo de repuesto por ID", e);
        }
        return Optional.empty();
    }

    @Override
    public Optional<RepuestoTipo> buscarPorNombre(String nombre) {
        String sql = "SELECT id, nombre FROM repuesto_tipos WHERE LOWER(nombre) = LOWER(?)";
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, nombre);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapearRepuestoTipo(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar tipo de repuesto por nombre", e);
        }
        return Optional.empty();
    }

    @Override
    public List<RepuestoTipo> listarTodos() {
        String sql = "SELECT id, nombre FROM repuesto_tipos";
        List<RepuestoTipo> lista = new ArrayList<>();
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(mapearRepuestoTipo(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al listar tipos de repuestos", e);
        }
        return lista;
    }

    @Override
    public void eliminar(int id) {
        String sql = "DELETE FROM repuesto_tipos WHERE id = ?";
        try (Connection conn = ConexionFactory.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar tipo de repuesto", e);
        }
    }

    private RepuestoTipo mapearRepuestoTipo(ResultSet rs) throws SQLException {
        RepuestoTipo tipo = new RepuestoTipo();
        tipo.setId(rs.getInt("id"));
        tipo.setNombre(rs.getString("nombre"));
        return tipo;
    }
}
