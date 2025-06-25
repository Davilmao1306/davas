package org.diariocultural.fx; // <-- PACOTE CORRIGIDO

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.layout.AnchorPane;
import org.diariocultural.Book;
import org.diariocultural.Media;
import org.diariocultural.Movie;
import org.diariocultural.Series;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controlador da "moldura" principal da aplicação (MainView.fxml).
 * Sua principal responsabilidade é gerenciar a navegação, carregando
 * as diferentes páginas na área de conteúdo central.
 */
public class MainViewController implements Initializable {

    @FXML
    private AnchorPane contentArea;

    // A única dependência de backend necessária é o serviço unificado.
    private LibraryService libraryService;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // A carga da página inicial é feita no método setLibraryService
        // para garantir que o serviço não seja nulo.
    }

    /**
     * Recebe a instância do serviço principal vinda da MainApp.
     * Este é o ponto de entrada para a lógica do controlador.
     * @param libraryService O serviço que gerencia todas as mídias.
     */
    public void setLibraryService(LibraryService libraryService) {
        this.libraryService = libraryService;
        showLibraryView(); // Carrega a página inicial do acervo.
    }

    // --- MÉTODOS DE NAVEGAÇÃO ---

    @FXML
    private void showLibraryView() {
        loadPage("/view/LibraryView.fxml", null);
    }

    @FXML
    private void showBookFormView() {
        loadPage("/view/BookFormView.fxml", null);
    }

    @FXML
    private void showMovieFormView() {
        // Lógica para carregar a página de cadastro de filme, agora ATIVADA.
        loadPage("/view/MovieFormView.fxml", null);
    }

    /**
     * Prepara e exibe o formulário para edição de um Livro.
     * @param book O livro a ser editado.
     */
    public void showBookFormForEdit(Book book) {
        loadPage("/view/BookFormView.fxml", book);
    }

    /**
     * Prepara e exibe o formulário para edição de um Filme.
     * @param movie O filme a ser editado.
     */
    public void showMovieFormForEdit(Movie movie) {
        // Lógica para edição de filmes, agora ATIVADA.
        loadPage("/view/MovieFormView.fxml", movie);
    }

    @FXML
    private void showSeriesFormView() {
        loadPage("/view/SeriesFormView.fxml", null);
    }

    // --- MÉTODO AUXILIAR UNIFICADO PARA CARREGAR PÁGINAS ---

    private void loadPage(String fxmlPath, Media mediaToEdit) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            if (loader.getLocation() == null) {
                throw new IOException("Não foi possível encontrar o arquivo FXML: " + fxmlPath);
            }
            Parent page = loader.load();

            Object controller = loader.getController();

            // Injeta as dependências necessárias em cada tipo de controlador de página
            if (controller instanceof LibraryViewController) {
                LibraryViewController libraryController = (LibraryViewController) controller;
                libraryController.setLibraryService(this.libraryService);
                libraryController.setMainViewController(this);

            } else if (controller instanceof BookFormController) {
                BookFormController bookFormController = (BookFormController) controller;
                bookFormController.setLibraryService(this.libraryService);
                if (mediaToEdit instanceof Book) {
                    bookFormController.loadBookForEditing((Book) mediaToEdit);
                }

            } else if (controller instanceof MovieFormController) {
                MovieFormController movieFormController = (MovieFormController) controller;
                movieFormController.setLibraryService(this.libraryService);
                if (mediaToEdit instanceof Movie) {
                    movieFormController.loadMovieForEditing((Movie) mediaToEdit);
                }
            }

            if (controller instanceof SeriesFormController) {
                ((SeriesFormController) controller).setLibraryService(this.libraryService);
                if (mediaToEdit instanceof Series) {
                    // ((SeriesFormController) controller).loadSeriesForEditing((Series) mediaToEdit);
                }
            }

            contentArea.getChildren().setAll(page);

        } catch (IOException e) {
            System.err.println("Falha ao carregar a página: " + fxmlPath);
            e.printStackTrace();
        }
    }
}