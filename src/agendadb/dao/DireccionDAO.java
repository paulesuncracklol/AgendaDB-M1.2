package agendadb.dao;

import agendadb.model.Direccion;
import agendadb.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DireccionDAO {

    public Direccion obtenerPorTexto(String texto) throws SQLException {
        String sql = "SELECT id, direccion FROM Direcciones WHERE direccion = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, texto);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Direccion(rs.getInt("id"), rs.getString("direccion"));
            }
        }
        return null;
    }

    public Direccion crearSiNoExiste(String texto) throws SQLException {
        Direccion existente = obtenerPorTexto(texto);
        if (existente != null) return existente;

        String sql = "INSERT INTO Direcciones (direccion) VALUES (?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, texto);
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                return new Direccion(rs.getInt(1), texto);
            }
        }
        // fallback por si no hay generated keys
        return obtenerPorTexto(texto);
    }

    public List<Direccion> obtenerPorPersona(int personalId) throws SQLException {
        List<Direccion> lista = new ArrayList<>();
        String sql = "SELECT d.id, d.direccion FROM Direcciones d " +
                "JOIN Persona_Direccion pd ON pd.direccionId = d.id " +
                "WHERE pd.personalId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, personalId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(new Direccion(rs.getInt("id"), rs.getString("direccion")));
            }
        }
        return lista;
    }

    public void vincular(int personalId, int direccionId) throws SQLException {
        String sql = "INSERT IGNORE INTO Persona_Direccion (personalId, direccionId) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, personalId);
            stmt.setInt(2, direccionId);
            stmt.executeUpdate();
        }
    }

    public void desvincularTodas(int personalId) throws SQLException {
        String sql = "DELETE FROM Persona_Direccion WHERE personalId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, personalId);
            stmt.executeUpdate();
        }
    }
}
