package agendadb.dao;

import agendadb.model.Telefono;
import agendadb.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TelefonoDAO {

    public void insertar(Telefono tel) throws SQLException {
        String sql = "INSERT INTO Telefonos (personalId, telefono) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, tel.getPersonaId());
            stmt.setString(2, tel.getTelefono());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                tel.setId(rs.getInt(1));
            }
        }
    }

    public void eliminarPorPersona(int personalId) throws SQLException {
        String sql = "DELETE FROM Telefonos WHERE personalId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, personalId);
            stmt.executeUpdate();
        }
    }

    public List<Telefono> obtenerPorPersona(int personalId) throws SQLException {
        List<Telefono> lista = new ArrayList<>();
        String sql = "SELECT * FROM Telefonos WHERE personalId = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, personalId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                lista.add(new Telefono(rs.getInt("id"), rs.getInt("personalId"), rs.getString("telefono")));
            }
        }
        return lista;
    }
}
