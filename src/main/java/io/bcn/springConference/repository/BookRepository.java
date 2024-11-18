package io.bcn.springConference.repository;

import io.bcn.springConference.model.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    @Query("SELECT DISTINCT b.author FROM Book b")
    List<String> findAllAuthors();

    List<Book> findByAuthor(String author);
}