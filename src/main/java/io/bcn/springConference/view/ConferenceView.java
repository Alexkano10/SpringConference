package io.bcn.springConference.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.bcn.springConference.model.Conference;
import io.bcn.springConference.repository.ConferenceRepository;
import com.vaadin.flow.component.datepicker.DatePicker;

@Route("conferences")
public class ConferenceView extends VerticalLayout {

    private final ConferenceRepository conferenceRepository;
    private final Grid<Conference> grid = new Grid<>(Conference.class);
    private DatePicker datePicker;  // Declarar datePicker como atributo de la clase

    public ConferenceView(ConferenceRepository conferenceRepository) {
        this.conferenceRepository = conferenceRepository;
        createDatePicker();  // Crear el DatePicker
        configureGrid();
        add(datePicker, grid);  // Añadir el DatePicker y el grid
        updateList();  // Inicializar el listado de conferencias
    }

    // Crear el DatePicker y configurar su comportamiento
    private void createDatePicker() {
        datePicker = new DatePicker("Select a Date");  // Asignar el DatePicker a la variable de clase
        datePicker.addValueChangeListener(event -> updateList());  // Filtrar cuando se selecciona una fecha
    }

    private void configureGrid() {
        // Configuración de las columnas del Grid
        grid.setColumns("id", "date", "linkToYoutubeVideo", "title", "conferenceName", "content", "duration", "room", "book", "speaker");

        // Personalización de los encabezados de columna
        grid.getColumnByKey("id").setHeader("ID");
        grid.getColumnByKey("date").setHeader("Date");
        grid.getColumnByKey("linkToYoutubeVideo").setHeader("YouTube Video Link");
        grid.getColumnByKey("title").setHeader("Title");
        grid.getColumnByKey("conferenceName").setHeader("Conference Name");
        grid.getColumnByKey("content").setHeader("Content");
        grid.getColumnByKey("duration").setHeader("Duration");
        grid.getColumnByKey("room").setHeader("Room");
        grid.getColumnByKey("book").setHeader("Book ID");
        grid.getColumnByKey("speaker").setHeader("Speaker ID");

        grid.setHeight("400px");
    }

    // Método para actualizar la lista de conferencias
    private void updateList() {
        if (datePicker.getValue() != null) {
            // Filtrar conferencias por la fecha seleccionada en el DatePicker
            grid.setItems(conferenceRepository.findByDate(datePicker.getValue()));
        } else {
            // Si no hay fecha seleccionada, mostrar todas las conferencias
            grid.setItems(conferenceRepository.findAll());
        }
    }
}