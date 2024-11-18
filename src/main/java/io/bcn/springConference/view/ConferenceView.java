package io.bcn.springConference.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.router.Route;
import io.bcn.springConference.model.Conference;
import io.bcn.springConference.model.Book;
import io.bcn.springConference.model.Speaker;
import io.bcn.springConference.repository.ConferenceRepository;
import io.bcn.springConference.repository.BookRepository;
import io.bcn.springConference.repository.SpeakerRepository;

@Route("conferences")
public class ConferenceView extends VerticalLayout {

    private final ConferenceRepository conferenceRepository;
    private final BookRepository bookRepository;
    private final SpeakerRepository speakerRepository;
    private final Grid<Conference> grid;
    private final DatePicker filterDatePicker;
    private final Button addButton;
    private final Dialog form;
    private final Binder<Conference> binder;

    // Campos del formulario
    private DatePicker datePicker;
    private TextField linkField;
    private TextField titleField;
    private TextField conferenceNameField;
    private TextArea contentArea;
    private IntegerField durationField;
    private TextField roomField;
    private ComboBox<Book> bookComboBox;
    private ComboBox<Speaker> speakerComboBox;

    public ConferenceView(ConferenceRepository conferenceRepository, BookRepository bookRepository, SpeakerRepository speakerRepository) {
        this.conferenceRepository = conferenceRepository;
        this.bookRepository = bookRepository;
        this.speakerRepository = speakerRepository;
        this.grid = new Grid<>(Conference.class);
        this.filterDatePicker = new DatePicker("Filter by Date");
        this.addButton = new Button("Add Conference");
        this.form = new Dialog();
        this.binder = new Binder<>(Conference.class);

        configureGrid();
        configureForm();

        HorizontalLayout toolbar = new HorizontalLayout(filterDatePicker, addButton);
        add(toolbar, grid);

        filterDatePicker.addValueChangeListener(e -> updateList());
        addButton.addClickListener(e -> {
            grid.asSingleSelect().clear();
            openForm(new Conference());
        });

        updateList();
    }

    private void configureGrid() {
        grid.setColumns("date", "linkToYoutubeVideo", "title", "conferenceName", "content", "duration", "room");
        grid.addColumn(conference -> conference.getBook().getId()).setHeader("Book");
        grid.addColumn(conference -> conference.getSpeaker().getId()).setHeader("Speaker");
        grid.setHeight("300px");
        grid.asSingleSelect().addValueChangeListener(e -> openForm(e.getValue()));
    }

    private void configureForm() {
        datePicker = new DatePicker("Date");
        linkField = new TextField("YouTube Video Link");
        titleField = new TextField("Title");
        conferenceNameField = new TextField("Conference Name");
        contentArea = new TextArea("Content");
        durationField = new IntegerField("Duration");
        roomField = new TextField("Room");
        bookComboBox = new ComboBox<>("Book");
        speakerComboBox = new ComboBox<>("Speaker");

        bookComboBox.setItems(bookRepository.findAll());
        bookComboBox.setItemLabelGenerator(Book::getTitle); // Asumiendo que Book tiene un método getTitle()

        speakerComboBox.setItems(speakerRepository.findAll());
        speakerComboBox.setItemLabelGenerator(Speaker::getName); // Asumiendo que Speaker tiene un método getName()

        binder.forField(datePicker).asRequired("Date is required").bind(Conference::getDate, Conference::setDate);
        binder.forField(linkField).asRequired("YouTube link is required").bind(Conference::getLinkToYoutubeVideo, Conference::setLinkToYoutubeVideo);
        binder.forField(titleField).asRequired("Title is required").bind(Conference::getTitle, Conference::setTitle);
        binder.forField(conferenceNameField).asRequired("Conference name is required").bind(Conference::getConferenceName, Conference::setConferenceName);
        binder.forField(contentArea).bind(Conference::getContent, Conference::setContent);
        binder.forField(durationField).asRequired("Duration is required").bind(Conference::getDuration, Conference::setDuration);
        binder.forField(roomField).asRequired("Room is required").bind(Conference::getRoom, Conference::setRoom);
        binder.forField(bookComboBox).asRequired("Book is required").bind(Conference::getBook, Conference::setBook);
        binder.forField(speakerComboBox).asRequired("Speaker is required").bind(Conference::getSpeaker, Conference::setSpeaker);

        Button saveButton = new Button("Save", e -> saveConference());
        Button deleteButton = new Button("Delete", e -> deleteConference());
        Button cancelButton = new Button("Cancel", e -> closeForm());

        form.add(new VerticalLayout(
                datePicker, linkField, titleField, conferenceNameField, contentArea,
                durationField, roomField, bookComboBox, speakerComboBox,
                new HorizontalLayout(saveButton, deleteButton, cancelButton)
        ));
    }

    private void openForm(Conference conference) {
        if (conference == null) {
            closeForm();
        } else {
            binder.setBean(conference);
            form.open();
        }
    }

    private void closeForm() {
        form.close();
        grid.asSingleSelect().clear();
    }

    private void saveConference() {
        Conference conference = binder.getBean();
        conferenceRepository.save(conference);
        updateList();
        closeForm();
    }

    private void deleteConference() {
        Conference conference = binder.getBean();
        conferenceRepository.delete(conference);
        updateList();
        closeForm();
    }

    private void updateList() {
        if (filterDatePicker.getValue() != null) {
            grid.setItems(conferenceRepository.findByDate(filterDatePicker.getValue()));
        } else {
            grid.setItems(conferenceRepository.findAll());
        }
    }
}