package agendadb.dao;

import agendadb.model.Persona;
import agendadb.model.Telefono;
import agendadb.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonaDAO {

    public void insertar(Persona persona) throws SQLException {
        String sql = "INSERT INTO Personas (nombre, direccion) VALUES (?, ?)"; // mantiene columna legacy para compatibilidad
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, persona.getNombre());
            stmt.setString(2, persona.getDireccion());
            stmt.executeUpdate();

            ResultSet rs = stmt.getGeneratedKeys();
            if (rs.next()) {
                persona.setId(rs.getInt(1));
            }

            for (Telefono tel : persona.getTelefonos()) {
                insertarTelefono(conn, persona.getId(), tel.getTelefono());
            }

            // vincular direcciones (muchos-a-muchos)
            agendadb.dao.DireccionDAO ddao = new agendadb.dao.DireccionDAO();
            for (agendadb.model.Direccion d : persona.getDirecciones()) {
                agendadb.model.Direccion real = ddao.crearSiNoExiste(d.getDireccion());
                if (real != null) ddao.vincular(persona.getId(), real.getId());
            }
        }
    }

    private void insertarTelefono(Connection conn, int personalId, String telefono) throws SQLException {
        String sql = "INSERT INTO Telefonos (personalId, telefono) VALUES (?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, personalId);
            stmt.setString(2, telefono);
            stmt.executeUpdate();
        }
    }

    public List<Persona> obtenerTodos() throws SQLException {
        List<Persona> lista = new ArrayList<>();
        String sql = "SELECT * FROM Personas";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Persona persona = new Persona(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("direccion")
                );
                persona.setTelefonos(obtenerTelefonos(conn, persona.getId()));
                // cargar direcciones
                agendadb.dao.DireccionDAO ddao = new agendadb.dao.DireccionDAO();
                persona.setDirecciones(ddao.obtenerPorPersona(persona.getId()));
                // set legacy direccion como concatenación para mostrar en tabla
                String legacy = String.join(" | ", persona.getDirecciones().stream().map(agendadb.model.Direccion::getDireccion).toList());
                persona.setDireccion(legacy);
                lista.add(persona);
            }
        }
        return lista;
    }

    private List<Telefono> obtenerTelefonos(Connection conn, int personalId) throws SQLException {
        List<Telefono> telefonos = new ArrayList<>();
        String sql = "SELECT * FROM Telefonos WHERE personalId=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, personalId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                telefonos.add(new Telefono(
                        rs.getInt("id"),
                        rs.getInt("personalId"),
                        rs.getString("telefono")
                ));
            }
        }
        return telefonos;
    }

    public void actualizar(Persona persona) throws SQLException {
        String sql = "UPDATE Personas SET nombre=?, direccion=? WHERE id=?"; // direccion guarda la primera dirección para compatibilidad
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, persona.getNombre());
            stmt.setString(2, persona.getDireccion());
            stmt.setInt(3, persona.getId());
            stmt.executeUpdate();

            // eliminar teléfonos viejos
            try (PreparedStatement del = conn.prepareStatement("DELETE FROM Telefonos WHERE personalId=?")) {
                del.setInt(1, persona.getId());
                del.executeUpdate();
            }

            // insertar nuevos
            for (Telefono tel : persona.getTelefonos()) {
                insertarTelefono(conn, persona.getId(), tel.getTelefono());
            }

            // vincular direcciones (muchos-a-muchos)
            agendadb.dao.DireccionDAO ddao = new agendadb.dao.DireccionDAO();
            for (agendadb.model.Direccion d : persona.getDirecciones()) {
                agendadb.model.Direccion real = ddao.crearSiNoExiste(d.getDireccion());
                if (real != null) ddao.vincular(persona.getId(), real.getId());
            }
        }
    }

    public void eliminar(int personalId) throws SQLException {
        String sql = "DELETE FROM Personas WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, personalId);
            stmt.executeUpdate();
        }
    }
}
