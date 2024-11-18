package io.bcn.springConference.view;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.router.Route;
import io.bcn.springConference.model.Book;
import io.bcn.springConference.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

@Route("books")
public class BookView extends VerticalLayout {

    private final BookRepository bookRepository;
    private final Grid<Book> grid = new Grid<>(Book.class);
    private final ComboBox<String> authorComboBox = new ComboBox<>("Filter by Author");

    // Form components
    private TextField titleField = new TextField("Title");
    private TextField authorField = new TextField("Author");
    private TextField isbnField = new TextField("ISBN");
    private Button saveButton = new Button("Save");
    private Button cancelButton = new Button("Cancel");

    private Book bookToEdit = null;

    @Autowired
    public BookView(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        createAuthorComboBox(); // Crear el ComboBox de autores
        configureGrid();
        configureForm();

        // Add components to the layout
        add(authorComboBox, grid, titleField, authorField, isbnField, saveButton, cancelButton);
        updateList(); // Inicializar el listado de libros
    }

    // Crear el ComboBox de autores y configurar su comportamiento
    private void createAuthorComboBox() {
        List<String> authors = bookRepository.findAllAuthors(); // Necesitarás implementar este método en el repositorio
        authorComboBox.setItems(authors);  // Poner los autores en el ComboBox
        authorComboBox.addValueChangeListener(event -> updateList());  // Filtrar cuando se seleccione un autor
    }

    // Configurar la tabla de libros
    private void configureGrid() {
        grid.setColumns("title", "author", "isbn"); // Mostrar estos campos en la tabla
        grid.getColumnByKey("title").setHeader("Title");
        grid.getColumnByKey("author").setHeader("Author");
        grid.getColumnByKey("isbn").setHeader("ISBN");
        grid.setHeight("400px");

        // Agregar acción de edición y eliminación
        grid.asSingleSelect().addValueChangeListener(event -> editBook(event.getValue()));

        // Agregar botón para eliminar
        grid.addComponentColumn(book -> new Button("Delete", click -> deleteBook(book)))
                .setHeader("Actions");
    }

    // Configurar el formulario de edición
    private void configureForm() {
        saveButton.addClickListener(event -> saveBook());
        cancelButton.addClickListener(event -> clearForm());

        cancelButton.setVisible(false);
    }

    // Método para actualizar la lista de libros
    private void updateList() {
        if (authorComboBox.getValue() != null) {
            // Filtrar libros por el autor seleccionado en el ComboBox
            grid.setItems(bookRepository.findByAuthor(authorComboBox.getValue()));
        } else {
            // Si no se selecciona ningún autor, mostrar todos los libros
            grid.setItems(bookRepository.findAll());
        }
    }

    // Método para editar un libro
    private void editBook(Book book) {
        if (book == null) {
            clearForm();
        } else {
            bookToEdit = book;
            titleField.setValue(book.getTitle());
            authorField.setValue(book.getAuthor());
            isbnField.setValue(book.getIsbn());

            saveButton.setText("Update");
            cancelButton.setVisible(true);
        }
    }

    // Método para guardar un libro (crear o actualizar)
    private void saveBook() {
        if (bookToEdit == null) {
            bookToEdit = new Book();
        }

        bookToEdit.setTitle(titleField.getValue());
        bookToEdit.setAuthor(authorField.getValue());
        bookToEdit.setIsbn(isbnField.getValue());

        bookRepository.save(bookToEdit);
        clearForm();
        updateList();
    }

    // Método para eliminar un libro
    private void deleteBook(Book book) {
        bookRepository.delete(book);
        updateList();
    }

    // Limpiar el formulario
    private void clearForm() {
        titleField.clear();
        authorField.clear();
        isbnField.clear();
        bookToEdit = null;
        saveButton.setText("Save");
        cancelButton.setVisible(false);
    }
}