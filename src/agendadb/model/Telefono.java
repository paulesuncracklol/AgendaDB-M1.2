package agendadb.model;

public class Telefono {
    private int id;
    private int personalId;
    private String telefono;

    public Telefono(int id, int personalId, String telefono) {
        this.id = id;
        this.personalId = personalId;
        this.telefono = telefono;
    }

    public Telefono(int personalId, String telefono) {
        this.personalId = personalId;
        this.telefono = telefono;
    }

    public int getId() { return id; }
    public int getPersonaId() { return personalId; }
    public String getTelefono() { return telefono; }

    public void setId(int id) { this.id = id; }
    public void setPersonaId(int personalId) { this.personalId = personalId; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
}
