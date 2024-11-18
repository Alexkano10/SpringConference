package io.bcn.springConference.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.router.Route;
import io.bcn.springConference.model.Book;
import io.bcn.springConference.repository.BookRepository;
import java.util.List;

@Route("books")
public class BookView extends VerticalLayout {

    private final BookRepository bookRepository;
    private final Grid<Book> grid = new Grid<>(Book.class);
    private final ComboBox<String> authorComboBox = new ComboBox<>("Filter by Author");

    public BookView(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
        createAuthorComboBox();  // Crear el ComboBox de autores
        configureGrid();
        add(authorComboBox, grid);  // Añadir el ComboBox y el grid
        updateList();  // Inicializar el listado de libros
    }

    // Crear el ComboBox de autores y configurar su comportamiento
    private void createAuthorComboBox() {
        // Obtener la lista de autores únicos de la base de datos
        List<String> authors = bookRepository.findAllAuthors(); // Necesitarás implementar este método en el repositorio

        // Configurar el ComboBox
        authorComboBox.setItems(authors);  // Poner los autores en el ComboBox
        authorComboBox.addValueChangeListener(event -> updateList());  // Filtrar cuando se seleccione un autor
    }

    private void configureGrid() {
        grid.setColumns("title", "author", "isbn"); // Mostrar estos campos en la tabla

        // Personalización de los encabezados de columna
        grid.getColumnByKey("title").setHeader("Title");
        grid.getColumnByKey("author").setHeader("Author");
        grid.getColumnByKey("isbn").setHeader("ISBN");

        grid.setHeight("400px");
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
}