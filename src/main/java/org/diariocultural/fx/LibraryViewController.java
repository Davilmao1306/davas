package org.diariocultural.fx;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.diariocultural.*;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controlador para a página do Acervo (LibraryView.fxml).
 * Gerencia a exibição e interação com a lista unificada de mídias.
 */
public class LibraryViewController implements Initializable {

    //--- DEPENDÊNCIAS ---
    private LibraryService libraryService;
    private MainViewController mainViewController;

    //--- COMPONENTES DA UI (@FXML) ---
    @FXML private TableView<Media> mediaTableView;
    @FXML private TableColumn<Media, String> typeColumn;
    @FXML private TableColumn<Media, String> titleColumn;
    @FXML private TableColumn<Media, String> creatorColumn;
    @FXML private TableColumn<Media, Integer> yearColumn;
    @FXML private TextField searchField;

    public void setLibraryService(LibraryService libraryService) {
        this.libraryService = libraryService;
        refreshMediaTable(this.libraryService.getAllMedia());
    }

    public void setMainViewController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("releaseYear"));

        mediaTableView.setOnMouseClicked(event -> {
            // Verifica se foi um duplo-clique e se uma linha foi selecionada
            if (event.getClickCount() == 2 && mediaTableView.getSelectionModel().getSelectedItem() != null) {
                Media selectedMedia = mediaTableView.getSelectionModel().getSelectedItem();
                showDetailsDialog(selectedMedia);
            }
        });

        typeColumn.setCellValueFactory(cellData -> {
            Media media = cellData.getValue();
            String type = "";
            if (media instanceof Book) type = "Livro";
            else if (media instanceof Movie) type = "Filme";
            else if (media instanceof Series) type = "Série";
            return new SimpleStringProperty(type);
        });

        creatorColumn.setCellValueFactory(cellData -> {
            Media media = cellData.getValue();
            String creator = "N/A"; // Valor padrão
            if (media instanceof Book) {
                creator = ((Book) media).getAuthor();
            } else if (media instanceof Movie) {
                creator = ((Movie) media).getDirector();
            } else if (media instanceof Series) {
                creator = ((Series) media).getCreator();
            }
            // Séries não têm um único criador, então "N/A" é apropriado.
            return new SimpleStringProperty(creator);
        });
    }

    @FXML
    private void onSearchButtonClick() {
        String criteria = searchField.getText();
        refreshMediaTable(libraryService.searchAllMedia(criteria));
    }

    @FXML
    private void onResetButtonClick() {
        searchField.clear();
        refreshMediaTable(libraryService.getAllMedia());
    }

    @FXML
    private void onEditButtonClick() {
        Media selectedMedia = mediaTableView.getSelectionModel().getSelectedItem();
        if (selectedMedia == null) {
            showAlert(Alert.AlertType.WARNING, "Nenhuma Mídia Selecionada", "Por favor, selecione um item na tabela para editar.");
            return;
        }

        // --- LÓGICA DE EDIÇÃO ATIVADA PARA FILMES ---
        if (selectedMedia instanceof Book) {
            mainViewController.showBookFormForEdit((Book) selectedMedia);
        } else if (selectedMedia instanceof Movie) {
            mainViewController.showMovieFormForEdit((Movie) selectedMedia); // <-- LÓGICA ATIVADA
        } else if (selectedMedia instanceof Series) {
            // Futuramente: mainViewController.showSeriesFormForEdit((Series) selectedMedia);
            showAlert(Alert.AlertType.INFORMATION, "Não Implementado", "A edição de séries será adicionada em breve.");
        }
    }

    @FXML
    private void onDeleteButtonClick() {
        Media selectedMedia = mediaTableView.getSelectionModel().getSelectedItem();
        if (selectedMedia == null) {
            showAlert(Alert.AlertType.WARNING, "Nenhuma Mídia Selecionada", "Por favor, selecione um item para excluir.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION, "Tem certeza que deseja excluir: " + selectedMedia.getTitle() + "?", ButtonType.YES, ButtonType.NO);
        confirmationAlert.setHeaderText("Confirmar Exclusão");

        confirmationAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.YES) {
                if (selectedMedia instanceof Book) {
                    libraryService.getBookController().removeBook((Book) selectedMedia);
                } else if (selectedMedia instanceof Movie) {
                    libraryService.getMovieController().removeMovie((Movie) selectedMedia);
                } else if (selectedMedia instanceof Series) {
                    libraryService.getSeriesController().removeSeries((Series) selectedMedia);
                }
                refreshMediaTable(libraryService.getAllMedia());
            }
        });
    }

    private void showDetailsDialog(Media media) {
        try {
            // Carrega o FXML da janela de detalhes
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MediaDetailView.fxml"));
            Parent page = loader.load();

            // Pega o controller da janela de detalhes
            MediaDetailViewController controller = loader.getController();
            // Passa o objeto de mídia selecionado para ele
            controller.setMedia(media);

            // Cria e configura o diálogo
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.initOwner(mediaTableView.getScene().getWindow()); // Faz com que o diálogo bloqueie a janela principal
            dialog.setTitle("Detalhes da Mídia");
            dialog.getDialogPane().setContent(page);
            dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

            dialog.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erro", "Não foi possível abrir a janela de detalhes.");
        }
    }

    private void refreshMediaTable(List<Media> mediaList) {
        if (mediaTableView != null) {
            mediaTableView.setItems(FXCollections.observableArrayList(mediaList));
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