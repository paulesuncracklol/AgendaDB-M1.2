package agendadb.model;

public class Direccion {
    private int id;
    private String direccion; // texto completo de la dirección

    public Direccion(int id, String direccion) {
        this.id = id;
        this.direccion = direccion;
    }

    public Direccion(String direccion) {
        this.direccion = direccion;
    }

    public int getId() { return id; }
    public String getDireccion() { return direccion; }

    public void setId(int id) { this.id = id; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    @Override
    public String toString() {
        return direccion;
    }
}
