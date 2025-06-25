package org.diariocultural.fx;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.diariocultural.*;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class SeriesFormController implements Initializable {

    private LibraryService libraryService;
    private Series seriesToEdit = null;
    private final ObservableList<Season> seasonsData = FXCollections.observableArrayList();

    // --- CAMPOS DA SÉRIE ---
    @FXML private TextField titleField;
    @FXML private TextField originalTitleField;
    @FXML private TextField creatorField;
    @FXML private TextField releaseYearField;
    @FXML private TextField endYearField;
    @FXML private TextField genreField;
    @FXML private TextArea castArea;
    @FXML private TextField whereToWatchField;
    @FXML private CheckBox watchedStatusCheckBox;
    @FXML private Button saveButton;

    // --- COMPONENTES DAS TEMPORADAS ---
    @FXML private TableView<Season> seasonsTableView;
    @FXML private TableColumn<Season, Integer> seasonNumberColumn;
    @FXML private TableColumn<Season, Integer> seasonYearColumn;
    @FXML private TableColumn<Season, Integer> episodesColumn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        seasonNumberColumn.setCellValueFactory(new PropertyValueFactory<>("seasonNumber"));
        seasonYearColumn.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));
        episodesColumn.setCellValueFactory(new PropertyValueFactory<>("episodes"));
        seasonsTableView.setItems(seasonsData);
    }

    public void setLibraryService(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    public void loadSeriesForEditing(Series series) {
        this.seriesToEdit = series;
        saveButton.setText("Salvar Alterações");

        titleField.setText(series.getTitle());
        originalTitleField.setText(series.getOriginalTitle());
        creatorField.setText(series.getCreator());
        releaseYearField.setText(String.valueOf(series.getReleaseYear()));
        endYearField.setText(String.valueOf(series.getEndYear()));
        watchedStatusCheckBox.setSelected(series.isWatchedStatus());
        if (series.getGenre() != null) genreField.setText(String.join(", ", series.getGenre()));
        if (series.getCast() != null) castArea.setText(String.join(", ", series.getCast()));
        if (series.getWhereToWatch() != null) whereToWatchField.setText(String.join(", ", series.getWhereToWatch()));

        seasonsData.clear();
        if (series.getSeasons() != null) seasonsData.addAll(series.getSeasons());
    }

    @FXML
    private void onSaveButtonClick() {
        try {
            String creator = creatorField.getText();
            String title = titleField.getText();
            int releaseYear = Integer.parseInt(releaseYearField.getText());
            int endYear = endYearField.getText().isBlank() ? 0 : Integer.parseInt(endYearField.getText());
            List<String> genres = parseList(genreField.getText());
            List<String> cast = parseList(castArea.getText());
            List<String> whereToWatch = parseList(whereToWatchField.getText());
            boolean watchedStatus = watchedStatusCheckBox.isSelected();
            List<Season> currentSeasons = new ArrayList<>(seasonsData);

            if (seriesToEdit == null) {
                // MODO CRIAÇÃO
                Series newSeries = new Series(title, originalTitleField.getText(), creator, genres, releaseYear, endYear, whereToWatch, cast, watchedStatus);
                newSeries.setSeasons(currentSeasons);
                libraryService.getSeriesController().addSeriesViaObject(newSeries);
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Série adicionada!");
            } else {
                // MODO EDIÇÃO
                seriesToEdit.setTitle(title);
                seriesToEdit.setOriginalTitle(originalTitleField.getText());
                seriesToEdit.setCreator(creator);
                seriesToEdit.setReleaseYear(releaseYear);
                seriesToEdit.setEndYear(endYear);
                seriesToEdit.setGenre(genres);
                seriesToEdit.setCast(cast);
                seriesToEdit.setWhereToWatch(whereToWatch);
                seriesToEdit.setWatchedStatus(watchedStatus);
                seriesToEdit.setSeasons(currentSeasons);
                libraryService.getSeriesController().updateSeries(seriesToEdit);
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Série atualizada!");
            }
            clearFormAndState();

        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Erro de Formato", "Os campos de ano devem ser números.");
        }
    }

    @FXML
    private void onAddSeason() {
        Dialog<Season> dialog = createSeasonDialog(null); // Cria um diálogo para uma nova temporada
        Optional<Season> result = dialog.showAndWait();
        result.ifPresent(seasonsData::add);
    }

    // Você pode conectar este método a um duplo clique na tabela ou a um botão "Editar Temporada"
    @FXML
    private void onEditSeason() {
        Season selectedSeason = seasonsTableView.getSelectionModel().getSelectedItem();
        if (selectedSeason == null) {
            showAlert(Alert.AlertType.WARNING, "Seleção Vazia", "Selecione uma temporada para editar.");
            return;
        }
        Dialog<Season> dialog = createSeasonDialog(selectedSeason); // Cria um diálogo preenchido com os dados
        Optional<Season> result = dialog.showAndWait();
        result.ifPresent(editedSeason -> {
            // A atualização do objeto na lista é um pouco mais complexa,
            // mas para o caso de um record, precisamos substituir o antigo pelo novo
            int index = seasonsData.indexOf(selectedSeason);
            if (index != -1) {
                seasonsData.set(index, editedSeason);
            }
        });
    }

    @FXML
    private void onRemoveSeason() {
        Season selectedSeason = seasonsTableView.getSelectionModel().getSelectedItem();
        if (selectedSeason != null) {
            seasonsData.remove(selectedSeason);
        } else {
            showAlert(Alert.AlertType.WARNING, "Nenhuma Temporada", "Selecione uma temporada para remover.");
        }
    }

    private Dialog<Season> createSeasonDialog(Season seasonToEdit) {
        Dialog<Season> dialog = new Dialog<>();
        dialog.setTitle(seasonToEdit == null ? "Adicionar Nova Temporada" : "Editar Temporada");

        ButtonType saveButtonType = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField seasonNumberField = new TextField();
        TextField yearField = new TextField();
        TextField episodesField = new TextField();
        TextField castField = new TextField(); // Campo para o elenco da temporada
        TextField ratingField = new TextField(); // Campo para a nota
        TextArea commentArea = new TextArea(); // Campo para o comentário

        if (seasonToEdit == null) { // Modo Adicionar
            seasonNumberField.setText(String.valueOf(seasonsData.size() + 1));
        } else { // Modo Editar
            seasonNumberField.setText(String.valueOf(seasonToEdit.getSeasonNumber()));
            yearField.setText(String.valueOf(seasonToEdit.getReleaseYear()));
            episodesField.setText(String.valueOf(seasonToEdit.getEpisodes()));
            if (seasonToEdit.getCast() != null) castField.setText(String.join(", ", seasonToEdit.getCast()));
            if(seasonToEdit.getReviewInfo() != null && !seasonToEdit.getReviewInfo().getReviews().isEmpty()) {
                Review review = seasonToEdit.getReviewInfo().getReviews().get(0);
                ratingField.setText(String.valueOf(review.rating()));
                commentArea.setText(review.comment());
            }
        }

        grid.add(new Label("Temporada Nº:"), 0, 0); grid.add(seasonNumberField, 1, 0);
        grid.add(new Label("Ano:"), 0, 1); grid.add(yearField, 1, 1);
        grid.add(new Label("Episódios:"), 0, 2); grid.add(episodesField, 1, 2);
        grid.add(new Label("Elenco Adicional:"), 0, 3); grid.add(castField, 1, 3);
        grid.add(new Label("Nota (0-5):"), 0, 4); grid.add(ratingField, 1, 4);
        grid.add(new Label("Comentário:"), 0, 5); grid.add(commentArea, 1, 5);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == saveButtonType) {
                try {
                    int seasonNum = Integer.parseInt(seasonNumberField.getText());
                    int year = Integer.parseInt(yearField.getText());
                    int episodes = Integer.parseInt(episodesField.getText());
                    List<String> cast = parseList(castField.getText());
                    ReviewInfo reviewInfo = new ReviewInfo();
                    if (!ratingField.getText().isBlank() || !commentArea.getText().isBlank()) {
                        int rating = ratingField.getText().isBlank() ? 0 : Integer.parseInt(ratingField.getText());
                        reviewInfo.evaluate(rating, commentArea.getText());
                    }
                    return new Season(seasonNum, episodes, year, cast, reviewInfo);
                } catch (NumberFormatException e) { return null; }
            }
            return null;
        });
        return dialog;
    }

    private void clearFormAndState() {
        titleField.clear(); originalTitleField.clear(); releaseYearField.clear();
        endYearField.clear(); genreField.clear(); castArea.clear(); whereToWatchField.clear();
        watchedStatusCheckBox.setSelected(false);
        creatorField.clear();
        seasonsData.clear();
        seriesToEdit = null;
        saveButton.setText("Adicionar Série");
    }

    private List<String> parseList(String text) {
        if (text == null || text.isBlank()) return new ArrayList<>();
        return Arrays.stream(text.split(",")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}