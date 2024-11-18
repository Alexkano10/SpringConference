package io.bcn.springConference.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.confirmdialog.ConfirmDialog;
import io.bcn.springConference.model.Speaker;
import io.bcn.springConference.repository.SpeakerRepository;
import org.springframework.transaction.annotation.Transactional;

@Route("speakers")
public class SpeakerView extends VerticalLayout {
    private final SpeakerRepository speakerRepository;
    private final Grid<Speaker> grid = new Grid<>(Speaker.class);
    private final Dialog form = new Dialog();
    private final Binder<Speaker> binder = new Binder<>(Speaker.class);
    private Speaker speaker;

    public SpeakerView(SpeakerRepository speakerRepository) {
        this.speakerRepository = speakerRepository;

        configureGrid();
        configureForm();

        Button addButton = new Button("Add Speaker", new Icon(VaadinIcon.PLUS));
        addButton.addClickListener(e -> {
            speaker = new Speaker();
            binder.readBean(speaker);
            form.open();
        });

        add(addButton, grid);
        updateList();
    }

    private void configureGrid() {
        grid.setColumns("name", "email", "bio");

        grid.addComponentColumn(this::createAvatar)
                .setHeader("Avatar")
                .setWidth("100px")
                .setFlexGrow(0);

        grid.addComponentColumn(speaker -> {
            HorizontalLayout actions = new HorizontalLayout();

            Button editButton = new Button(new Icon(VaadinIcon.EDIT));
            editButton.addClickListener(e -> editSpeaker(speaker));

            Button deleteButton = new Button(new Icon(VaadinIcon.TRASH));
            deleteButton.addClickListener(e -> confirmDelete(speaker));

            actions.add(editButton, deleteButton);
            return actions;
        }).setHeader("Actions").setWidth("120px").setFlexGrow(0);
    }

    private void configureForm() {
        form.setHeaderTitle("Speaker Details");

        TextField nameField = new TextField("Name");
        TextField emailField = new TextField("Email");
        TextArea bioArea = new TextArea("Bio");

        binder.forField(nameField)
                .asRequired("Name is required")
                .bind(Speaker::getName, Speaker::setName);

        binder.forField(emailField)
                .asRequired("Email is required")
                .bind(Speaker::getEmail, Speaker::setEmail);

        binder.forField(bioArea)
                .bind(Speaker::getBio, Speaker::setBio);

        Button saveButton = new Button("Save", e -> saveSpeaker());
        Button cancelButton = new Button("Cancel", e -> form.close());

        VerticalLayout formLayout = new VerticalLayout();
        formLayout.add(nameField, emailField, bioArea, new HorizontalLayout(saveButton, cancelButton));
        form.add(formLayout);
    }

    private Avatar createAvatar(Speaker speaker) {
        Avatar avatar = new Avatar();
        if (speaker.getName() != null && !speaker.getName().isEmpty()) {
            avatar.setAbbreviation(speaker.getName().substring(0, 1).toUpperCase());
        }
        return avatar;
    }

    private void editSpeaker(Speaker speaker) {
        this.speaker = speaker;
        binder.readBean(speaker);
        form.open();
    }

    private void confirmDelete(Speaker speaker) {
        ConfirmDialog dialog = new ConfirmDialog();
        dialog.setHeader("Confirm Delete");
        dialog.setText("Are you sure you want to delete this speaker? This action cannot be undone.");

        dialog.setCancelable(true);
        dialog.setConfirmText("Delete");
        dialog.setConfirmButtonTheme("error primary");

        dialog.addConfirmListener(event -> deleteSpeaker(speaker));

        dialog.open();
    }

    @Transactional
    private void deleteSpeaker(Speaker speaker) {
        try {
            speakerRepository.delete(speaker);
            speakerRepository.flush(); // Forzar la operación
            updateList();
            Notification.show("Speaker deleted successfully",
                            3000,
                            Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Error deleting speaker: " + e.getMessage(),
                            5000,
                            Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }

    private void saveSpeaker() {
        try {
            binder.writeBean(speaker);
            speakerRepository.save(speaker);
            form.close();
            updateList();
            Notification.show("Speaker saved successfully",
                            3000,
                            Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_SUCCESS);
        } catch (Exception e) {
            Notification.show("Error saving speaker: " + e.getMessage(),
                            5000,
                            Notification.Position.BOTTOM_START)
                    .addThemeVariants(NotificationVariant.LUMO_ERROR);
            e.printStackTrace();
        }
    }

    private void updateList() {
        grid.setItems(speakerRepository.findAll());
    }
}