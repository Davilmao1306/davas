package org.diariocultural;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MovieController {

    private List<Movie> movies;
    private static final String DATA_DIRECTORY = "data";
    private static final String FILE_NAME = "movies.json";
    private static final String FILE_PATH = DATA_DIRECTORY + File.separator + FILE_NAME;

    public MovieController() {
        loadData();
        if (this.movies == null) {
            this.movies = new ArrayList<>();
        }
        Movie.updateNextIdBasedOnLoadedData(this.movies);
    }

    // --- MÉTODOS PARA A GUI ---

    public void addMovieViaObject(Movie movie) {
        if (movie != null) {
            this.movies.add(movie);
            saveData();
        }
    }

    public void removeMovie(Movie movieToRemove) {
        if (movieToRemove != null) {
            movies.remove(movieToRemove);
            saveData();
        }
    }

    public void updateMovie(Movie updatedMovie) {
        for (int i = 0; i < movies.size(); i++) {
            if (movies.get(i).getMovieId() == updatedMovie.getMovieId()) {
                movies.set(i, updatedMovie);
                saveData();
                return;
            }
        }
    }

    public List<Movie> getAllMovies() {
        return new ArrayList<>(this.movies);
    }

    public List<Movie> searchMovies(String criteria) {
        if (criteria == null || criteria.isBlank()) {
            return getAllMovies();
        }
        String lowerCriteria = criteria.toLowerCase().trim();
        return movies.stream()
                .filter(movie -> movie.getTitle().toLowerCase().contains(lowerCriteria) ||
                        movie.getDirector().toLowerCase().contains(lowerCriteria) ||
                        String.valueOf(movie.getReleaseYear()).contains(lowerCriteria) ||
                        movie.getGenre().stream().anyMatch(g -> g.toLowerCase().contains(lowerCriteria)) ||
                        movie.getCast().stream().anyMatch(a -> a.toLowerCase().contains(lowerCriteria)))
                .collect(Collectors.toList());
    }


    // --- MÉTODOS DE PERSISTÊNCIA (sem alterações) ---

    private void saveData() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            File dataDir = new File(DATA_DIRECTORY);
            if (!dataDir.exists()) {
                dataDir.mkdirs();
            }
            objectMapper.writeValue(new File(FILE_PATH), movies);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        File file = new File(FILE_PATH);
        if (file.exists() && file.length() > 0) {
            try {
                this.movies = objectMapper.readValue(file, new TypeReference<>() {});
            } catch (IOException e) {
                this.movies = new ArrayList<>();
                e.printStackTrace();
            }
        } else {
            this.movies = new ArrayList<>();
        }
    }
}