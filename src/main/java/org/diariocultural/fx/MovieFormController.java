package org.diariocultural.fx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.diariocultural.Movie;
import org.diariocultural.ReviewInfo;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class MovieFormController {

    private LibraryService libraryService;
    private Movie movieToEdit = null;

    @FXML private TextField titleField;
    @FXML private TextField originalTitleField;
    @FXML private TextField directorField;
    @FXML private TextField yearField;
    @FXML private TextField durationField;
    @FXML private TextField genreField;
    @FXML private TextArea castArea;
    @FXML private TextArea synopsisArea;
    @FXML private TextField whereToWatchField;
    @FXML private CheckBox watchedStatusCheckBox;
    @FXML private DatePicker watchDatePicker;
    @FXML private TextField ratingField;
    @FXML private TextArea reviewCommentArea;
    @FXML private Button saveButton;

    public void setLibraryService(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    /**
     * Prepara o formulário para editar um filme existente.
     * @param movie O filme cujos dados preencherão o formulário.
     */
    public void loadMovieForEditing(Movie movie) {
        this.movieToEdit = movie;
        saveButton.setText("Salvar Alterações");

        titleField.setText(movie.getTitle());
        originalTitleField.setText(movie.getOriginalTitle());
        directorField.setText(movie.getDirector());
        yearField.setText(String.valueOf(movie.getReleaseYear()));
        durationField.setText(String.valueOf(movie.getDuration()));
        synopsisArea.setText(movie.getSynopsis());

        if (movie.getGenre() != null) {
            genreField.setText(String.join(", ", movie.getGenre()));
        }
        if (movie.getCast() != null) {
            castArea.setText(String.join(", ", movie.getCast()));
        }
        if (movie.getWhereToWatch() != null) {
            whereToWatchField.setText(String.join(", ", movie.getWhereToWatch()));
        }

        watchedStatusCheckBox.setSelected(movie.isWatchedStatus());
        if (movie.getWatchDate() != null) {
            watchDatePicker.setValue(movie.getWatchDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        }
    }

    @FXML
    private void onSaveButtonClick() {
        try {
            if (titleField.getText().isBlank() || directorField.getText().isBlank()) {
                showAlert(Alert.AlertType.ERROR, "Erro de Validação", "Título e Diretor são campos obrigatórios.");
                return;
            }

            if (movieToEdit == null) {
                // --- MODO CRIAÇÃO ---
                Movie newMovie = createMovieFromFormData();
                libraryService.getMovieController().addMovieViaObject(newMovie);
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Filme '" + newMovie.getTitle() + "' foi adicionado com sucesso!");
            } else {
                // --- MODO EDIÇÃO ---
                updateMovieFromFormData();
                libraryService.getMovieController().updateMovie(movieToEdit);
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Filme '" + movieToEdit.getTitle() + "' foi atualizado com sucesso!");
            }
            clearInputFieldsAndState();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Formato", "Os campos 'Ano' e 'Duração' devem ser números válidos.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro Inesperado", "Ocorreu um erro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Atualiza o objeto movieToEdit com os dados atuais do formulário.
     */
    private void updateMovieFromFormData() throws NumberFormatException {
        movieToEdit.setTitle(titleField.getText());
        movieToEdit.setOriginalTitle(originalTitleField.getText());
        movieToEdit.setDirector(directorField.getText());
        movieToEdit.setReleaseYear(Integer.parseInt(yearField.getText()));
        movieToEdit.setDuration(Integer.parseInt(durationField.getText()));
        movieToEdit.setSynopsis(synopsisArea.getText());
        movieToEdit.setGenre(parseList(genreField.getText()));
        movieToEdit.setCast(parseList(castArea.getText()));
        movieToEdit.setWhereToWatch(parseList(whereToWatchField.getText()));
        movieToEdit.setWatchedStatus(watchedStatusCheckBox.isSelected());

        Date watchDate = null;
        if (watchedStatusCheckBox.isSelected() && watchDatePicker.getValue() != null) {
            watchDate = Date.from(watchDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        movieToEdit.setWatchDate(watchDate);
    }

    /**
     * Lê todos os campos do formulário e cria um novo objeto Movie.
     */
    private Movie createMovieFromFormData() throws NumberFormatException {
        String title = titleField.getText();
        String originalTitle = originalTitleField.getText().isBlank() ? title : originalTitleField.getText();
        String director = directorField.getText();
        String synopsis = synopsisArea.getText();
        int year = yearField.getText().isBlank() ? 0 : Integer.parseInt(yearField.getText());
        int duration = durationField.getText().isBlank() ? 0 : Integer.parseInt(durationField.getText());
        List<String> genres = parseList(genreField.getText());
        List<String> cast = parseList(castArea.getText());
        List<String> whereToWatch = parseList(whereToWatchField.getText());
        boolean watched = watchedStatusCheckBox.isSelected();

        Date watchDate = null;
        if (watched && watchDatePicker.getValue() != null) {
            watchDate = Date.from(watchDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        ReviewInfo reviewInfo = new ReviewInfo();
        if (watched && (!ratingField.getText().isBlank() || !reviewCommentArea.getText().isBlank())) {
            int rating = ratingField.getText().isBlank() ? 0 : Integer.parseInt(ratingField.getText());
            String comment = reviewCommentArea.getText();
            reviewInfo.evaluate(rating, comment);
        }

        return new Movie(title, originalTitle, genres, duration, year, director, synopsis, cast, whereToWatch, watched, watchDate, reviewInfo);
    }

    private List<String> parseList(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(text.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    private void clearInputFieldsAndState() {
        titleField.clear(); originalTitleField.clear(); directorField.clear();
        yearField.clear(); durationField.clear(); genreField.clear();
        castArea.clear(); synopsisArea.clear(); whereToWatchField.clear();
        ratingField.clear(); reviewCommentArea.clear();
        watchedStatusCheckBox.setSelected(false);
        watchDatePicker.setValue(null);

        movieToEdit = null;
        if (saveButton != null) {
            saveButton.setText("Adicionar Filme");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}