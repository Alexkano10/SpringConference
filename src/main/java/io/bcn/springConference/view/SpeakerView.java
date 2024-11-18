package io.bcn.springConference.view;

import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import io.bcn.springConference.model.Speaker;
import io.bcn.springConference.repository.SpeakerRepository;

@Route("speakers")
public class SpeakerView extends VerticalLayout {

    private final SpeakerRepository speakerRepository;
    private final Grid<Speaker> grid = new Grid<>(Speaker.class);

    public SpeakerView(SpeakerRepository speakerRepository) {
        this.speakerRepository = speakerRepository;
        configureGrid();
        add(grid);
        updateList();
    }

    private void configureGrid() {
        // Configurar las columnas que se mostrarán en la tabla
        grid.setColumns("name", "bio");  // Mostrar nombre y biografía

        // Añadir una columna personalizada para mostrar el avatar con las iniciales
        grid.addComponentColumn(speaker -> createAvatar(speaker)) // Crear una columna de avatar con iniciales
                .setHeader("Avatar");  // Título de la columna
    }

    // Método para crear un Avatar con las iniciales del nombre
    private Avatar createAvatar(Speaker speaker) {
        Avatar avatar = new Avatar();
        // Usar las iniciales del nombre como abreviatura
        avatar.setAbbreviation(speaker.getName().substring(0, 1).toUpperCase());
        return avatar;
    }
    private void updateList() {
        grid.setItems(speakerRepository.findAll());
    }
}