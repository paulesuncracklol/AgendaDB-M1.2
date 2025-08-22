package agendadb.ui;

import agendadb.dao.PersonaDAO;
import agendadb.model.Persona;
import agendadb.model.Telefono;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.util.Arrays;

public class MainApp extends Application {
    private TableView<Persona> table;
    private ObservableList<Persona> data;
    private PersonaDAO dao = new PersonaDAO();

    private TextField nombreField = new TextField();
    private TextField direccionField = new TextField();
    private TextField telefonosField = new TextField(); // teléfonos separados por coma

    @Override
    public void start(Stage stage) {
        table = new TableView<>();
        data = FXCollections.observableArrayList();
        table.setItems(data);

        TableColumn<Persona, String> colNombre = new TableColumn<>("Nombre");
        colNombre.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getNombre()));

        TableColumn<Persona, String> colDireccion = new TableColumn<>("Dirección");
        colDireccion.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(c.getValue().getDireccion()));

        TableColumn<Persona, String> colTelefonos = new TableColumn<>("Teléfonos");
        colTelefonos.setCellValueFactory(c -> new javafx.beans.property.SimpleStringProperty(
                String.join(", ", c.getValue().getTelefonos().stream().map(Telefono::getTelefono).toList())
        ));

        table.getColumns().addAll(colNombre, colDireccion, colTelefonos);

        Button btnAgregar = new Button("Agregar");
        btnAgregar.setOnAction(e -> agregar());

        Button btnEditar = new Button("Modificar");
        btnEditar.setOnAction(e -> modificar());

        Button btnEliminar = new Button("Eliminar");
        btnEliminar.setOnAction(e -> eliminar());

        GridPane form = new GridPane();
        form.setPadding(new Insets(10));
        form.setHgap(10);
        form.setVgap(10);
        form.add(new Label("Nombre:"), 0, 0);
        form.add(nombreField, 1, 0);
        form.add(new Label("Dirección:"), 0, 1);
        form.add(direccionField, 1, 1);
        form.add(new Label("Teléfonos (coma):"), 0, 2);
        form.add(telefonosField, 1, 2);

        HBox botones = new HBox(10, btnAgregar, btnEditar, btnEliminar);
        form.add(botones, 1, 3);

        GridPane root = new GridPane();
        root.add(form, 0, 0);
        root.add(table, 0, 1);

        cargarDatos();

        Scene scene = new Scene(root, 600, 400);
        stage.setTitle("Agenda Personas");
        stage.setScene(scene);
        stage.show();
    }

    private void cargarDatos() {
        try {
            data.setAll(dao.obtenerTodos());
        } catch (SQLException ex) {
            mostrarError("Error al cargar datos: " + ex.getMessage());
        }
    }

    private void agregar() {
        try {
            Persona p = new Persona(nombreField.getText(), direccionField.getText());
            Arrays.stream(telefonosField.getText().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(t -> p.getTelefonos().add(new Telefono(p.getId(), t)));

            dao.insertar(p);
            cargarDatos();
            limpiarCampos();
        } catch (SQLException ex) {
            mostrarError("Error al agregar: " + ex.getMessage());
        }
    }

    private void modificar() {
        Persona sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        try {
            sel.setNombre(nombreField.getText());
            sel.setDireccion(direccionField.getText());
            sel.getTelefonos().clear();
            Arrays.stream(telefonosField.getText().split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .forEach(t -> sel.getTelefonos().add(new Telefono(sel.getId(), t)));

            dao.actualizar(sel);
            cargarDatos();
            limpiarCampos();
        } catch (SQLException ex) {
            mostrarError("Error al modificar: " + ex.getMessage());
        }
    }

    private void eliminar() {
        Persona sel = table.getSelectionModel().getSelectedItem();
        if (sel == null) return;

        try {
            dao.eliminar(sel.getId());
            cargarDatos();
        } catch (SQLException ex) {
            mostrarError("Error al eliminar: " + ex.getMessage());
        }
    }

    private void limpiarCampos() {
        nombreField.clear();
        direccionField.clear();
        telefonosField.clear();
    }

    private void mostrarError(String msg) {
        new Alert(Alert.AlertType.ERROR, msg).showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
