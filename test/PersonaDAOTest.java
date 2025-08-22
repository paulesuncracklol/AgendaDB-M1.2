package agendadb.test;

import agendadb.dao.PersonaDAO;
import agendadb.model.Persona;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class PersonaDAOTest {

    static PersonaDAO dao;

    @BeforeAll
    static void setup() {
        dao = new PersonaDAO();
    }

    @Test
    @Order(1)
    void testInsert() {
        Persona p = new Persona("Test User", "Calle Test 123");
        dao.insert(p);

        List<Persona> personas = dao.getAll();
        assertTrue(personas.stream().anyMatch(x -> x.getNombre().equals("Test User")));
    }

    @Test
    @Order(2)
    void testUpdate() {
        List<Persona> personas = dao.getAll();
        Persona p = personas.stream().filter(x -> x.getNombre().equals("Test User")).findFirst().orElse(null);
        assertNotNull(p);

        p.setDireccion("Nueva Dirección");
        dao.update(p);

        Persona updated = dao.getAll().stream().filter(x -> x.getId() == p.getId()).findFirst().orElse(null);
        assertEquals("Nueva Dirección", updated.getDireccion());
    }

    @Test
    @Order(3)
    void testDelete() {
        List<Persona> personas = dao.getAll();
        Persona p = personas.stream().filter(x -> x.getNombre().equals("Test User")).findFirst().orElse(null);
        assertNotNull(p);

        dao.delete(p.getId());
        personas = dao.getAll();
        assertFalse(personas.stream().anyMatch(x -> x.getNombre().equals("Test User")));
    }
}
