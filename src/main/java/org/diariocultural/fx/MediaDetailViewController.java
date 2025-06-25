package org.diariocultural.fx;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.diariocultural.*;

/**
 * Controlador para a janela de diálogo que exibe os detalhes de uma mídia.
 */
public class MediaDetailViewController {

    @FXML private Label titleLabel;
    @FXML private Label originalTitleLabel;
    @FXML private Label typeLabel;
    @FXML private Label yearLabel;
    @FXML private Label creatorTitleLabel;
    @FXML private Label creatorNameLabel;
    @FXML private Label durationTitleLabel;
    @FXML private Label durationLabel;
    @FXML private Label isbnTitleLabel;
    @FXML private Label isbnLabel;
    @FXML private Label genresLabel;
    @FXML private Label castLabel;

    /**
     * Preenche a janela com os dados da mídia fornecida.
     * @param media O objeto Book, Movie ou Series a ser exibido.
     */
    public void setMedia(Media media) {
        // Campos comuns
        titleLabel.setText(media.getTitle());
        yearLabel.setText(String.valueOf(media.getReleaseYear()));
        genresLabel.setText(String.join(", ", media.getGenre()));

        // Lógica para campos específicos de cada tipo de mídia
        if (media instanceof Book book) {
            typeLabel.setText("Livro");
            originalTitleLabel.setText(book.getOriginalTitle());
            creatorTitleLabel.setText("Autor:");
            creatorNameLabel.setText(book.getAuthor());
            isbnLabel.setText(book.getISBN());
            castLabel.setVisible(false); // Esconde campos que não se aplicam
            durationLabel.setVisible(false);
            durationTitleLabel.setVisible(false);
        } else if (media instanceof Movie movie) {
            typeLabel.setText("Filme");
            originalTitleLabel.setText(movie.getOriginalTitle());
            creatorTitleLabel.setText("Diretor:");
            creatorNameLabel.setText(movie.getDirector());
            durationLabel.setText(movie.getDuration() + " minutos");
            castLabel.setText(String.join(", ", movie.getCast()));
            isbnLabel.setVisible(false); // Esconde campos que não se aplicam
            isbnTitleLabel.setVisible(false);
        } else if (media instanceof Series series) {
            typeLabel.setText("Série");
            originalTitleLabel.setText(series.getOriginalTitle());
            creatorTitleLabel.setVisible(false); // Esconde campos que não se aplicam
            creatorNameLabel.setVisible(false);
            durationLabel.setVisible(false);
            durationTitleLabel.setVisible(false);
            isbnLabel.setVisible(false);
            isbnTitleLabel.setVisible(false);
            castLabel.setText(String.join(", ", series.getCast()));
        }
    }
}