package org.diariocultural.fx;

import org.diariocultural.BookController;
import org.diariocultural.MovieController;
import org.diariocultural.SeriesController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class MainApp extends Application {

    private static final LibraryService libraryService = new LibraryService(new BookController(), new MovieController(), new SeriesController());
    private Stage primaryStage;

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("Diário Cultural");
        showWelcomeScreen();
    }

    /**
     * Carrega e exibe a tela inicial de boas-vindas.
     */
    public void showWelcomeScreen() {
        try {
            // Caminho para o FXML
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/view/WelcomeScreen.fxml")));
            Parent page = loader.load();
            WelcomeScreenController controller = loader.getController();

            // A ação do botão "Acessar" chama o método para mostrar a tela principal
            controller.setOnAccessAction(this::showMainView);

            Scene scene = new Scene(page);
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Carrega e exibe a interface principal da aplicação.
     */
    public void showMainView() {
        try {
            FXMLLoader loader = new FXMLLoader(Objects.requireNonNull(getClass().getResource("/view/MainView.fxml")));
            Parent page = loader.load();

            MainViewController controller = loader.getController();
            controller.setLibraryService(libraryService);

            Scene scene = new Scene(page, 1080, 768);
            primaryStage.setScene(scene);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}