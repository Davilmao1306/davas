package org.diariocultural.fx;

import org.diariocultural.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Serviço responsável por unificar as operações de busca e listagem
 * entre diferentes tipos de mídia (Livros, Filmes, etc.).
 */
public class LibraryService {

    private final BookController bookController;
    private final MovieController movieController;
    private final SeriesController seriesController;

    // Atualize o construtor
    public LibraryService(BookController bookController, MovieController movieController, SeriesController seriesController) {
        this.bookController = bookController;
        this.movieController = movieController;
        this.seriesController = seriesController; // <-- Adicione esta linha
    }

    // Atualize o getAllMedia
    public List<Media> getAllMedia() {
        return Stream.of(
                bookController.getAllBooks().stream(),
                movieController.getAllMovies().stream(),
                seriesController.getAllSeries().stream() // <-- Adicione esta linha
        ).flatMap(s -> s).collect(Collectors.toList());
    }

    // Atualize o searchAllMedia
    public List<Media> searchAllMedia(String criteria) {
        if (criteria == null || criteria.isBlank()) {
            return getAllMedia();
        }
        List<Book> booksFound = bookController.searchBooks(criteria);
        List<Movie> moviesFound = movieController.searchMovies(criteria);
        List<Series> seriesFound = seriesController.searchSeries(criteria); // <-- Adicione esta linha

        return Stream.of(booksFound.stream(), moviesFound.stream(), seriesFound.stream())
                .flatMap(s -> s).collect(Collectors.toList());
    }

    public SeriesController getSeriesController(){ return seriesController;}

    public BookController getBookController() {
        return bookController;
    }

    public MovieController getMovieController() {
        return movieController;
    }
}