package org.diariocultural.fx;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.diariocultural.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para a página de formulário de Livro (BookFormView.fxml).
 * Responsável por criar novos livros e editar existentes.
 */
public class BookFormController {

    private LibraryService libraryService;
    private Book bookToEdit = null; // Se for nulo, estamos criando. Se não, estamos editando.

    @FXML private TextField titleField;
    @FXML private TextField authorField;
    @FXML private TextField yearField;
    @FXML private TextField originalTitleField;
    @FXML private TextField publisherField;
    @FXML private TextField isbnField;
    @FXML private TextField genreField;
    @FXML private CheckBox hasCopyCheckBox;
    @FXML private CheckBox readStatusCheckBox;
    @FXML private DatePicker readDatePicker;
    @FXML private TextField ratingField;
    @FXML private TextArea reviewCommentArea;
    @FXML private Button saveButton;

    /**
     * Injeta a dependência do serviço principal.
     */
    public void setLibraryService(LibraryService libraryService) {
        this.libraryService = libraryService;
    }

    /**
     * Prepara o formulário para editar um livro existente.
     * @param book O livro cujos dados preencherão o formulário.
     */
    public void loadBookForEditing(Book book) {
        this.bookToEdit = book;
        saveButton.setText("Salvar Alterações");

        // Preenche todos os campos com os dados do livro
        titleField.setText(book.getTitle());
        authorField.setText(book.getAuthor());
        yearField.setText(String.valueOf(book.getReleaseYear()));
        originalTitleField.setText(book.getOriginalTitle());
        publisherField.setText(book.getPublisher());
        isbnField.setText(book.getISBN());
        if (book.getGenre() != null) {
            genreField.setText(String.join(", ", book.getGenre()));
        }
        hasCopyCheckBox.setSelected(book.hasCopy());
        readStatusCheckBox.setSelected(book.isReadStatus());

        if (book.getReadDate() != null) {
            readDatePicker.setValue(book.getReadDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
        } else {
            readDatePicker.setValue(null);
        }

        // Preenche a última avaliação, se houver
        if (book.getReviewInfo() != null && !book.getReviewInfo().getReviews().isEmpty()) {
            Review lastReview = book.getReviewInfo().getReviews().get(book.getReviewInfo().getReviews().size() - 1);
            ratingField.setText(String.valueOf(lastReview.rating()));
            reviewCommentArea.setText(lastReview.comment());
        }
    }

    @FXML
    private void onSaveButtonClick() {
        try {
            if (bookToEdit == null) {
                // --- MODO CRIAÇÃO ---
                Book newBook = createBookFromFormData();
                libraryService.getBookController().addBookViaObject(newBook);
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Livro Adicionado!");
            } else {
                // --- MODO EDIÇÃO ---
                updateBookFromFormData();
                libraryService.getBookController().updateBook(bookToEdit);
                showAlert(Alert.AlertType.INFORMATION, "Sucesso", "Livro Atualizado!");
            }
            clearInputFieldsAndState();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erro", "Ocorreu um erro ao salvar o livro.");
            e.printStackTrace();
        }
    }

    /**
     * Atualiza o objeto 'bookToEdit' existente com os dados atuais do formulário.
     * Usa os métodos "setters" da classe Book.
     */
    private void updateBookFromFormData() throws NumberFormatException {
        // Atualiza o objeto com os dados dos campos do formulário
        bookToEdit.setTitle(titleField.getText());
        bookToEdit.setAuthor(authorField.getText());
        bookToEdit.setReleaseYear(yearField.getText().isBlank() ? 0 : Integer.parseInt(yearField.getText()));
        bookToEdit.setOriginalTitle(originalTitleField.getText().isBlank() ? bookToEdit.getTitle() : originalTitleField.getText());
        bookToEdit.setPublisher(publisherField.getText());
        bookToEdit.setISBN(isbnField.getText());
        bookToEdit.setGenre(parseList(genreField.getText()));
        bookToEdit.setHasCopy(hasCopyCheckBox.isSelected());
        bookToEdit.setReadStatus(readStatusCheckBox.isSelected());

        // Lógica para atualizar a data de leitura
        Date readDate = null;
        if (readStatusCheckBox.isSelected() && readDatePicker.getValue() != null) {
            readDate = Date.from(readDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        }
        bookToEdit.setReadDate(readDate);

        // Ao editar, adicionamos uma nova avaliação se os campos forem preenchidos,
        // mantendo o histórico de avaliações antigas.
        if (readStatusCheckBox.isSelected() && (!ratingField.getText().isBlank() || !reviewCommentArea.getText().isBlank())) {
            int rating = ratingField.getText().isBlank() ? 0 : Integer.parseInt(ratingField.getText());
            String comment = reviewCommentArea.getText();
            bookToEdit.addReview(rating, comment);
        }
    }

    /**
     * Lê todos os campos do formulário para criar uma instância totalmente nova de Book.
     * @return um novo objeto Book.
     * @throws NumberFormatException se os campos de ano ou nota forem inválidos.
     */
    private Book createBookFromFormData() throws NumberFormatException {
        // Coleta dados dos campos de texto
        String title = titleField.getText();
        String author = authorField.getText();
        String originalTitle = originalTitleField.getText().isBlank() ? title : originalTitleField.getText();
        String publisher = publisherField.getText();
        String isbn = isbnField.getText();

        // Coleta e converte campos numéricos
        int year = yearField.getText().isBlank() ? 0 : Integer.parseInt(yearField.getText());

        // Coleta e processa a lista de gêneros
        List<String> genres = parseList(genreField.getText());

        // Coleta dados dos CheckBoxes
        boolean hasCopy = hasCopyCheckBox.isSelected();
        boolean readStatus = readStatusCheckBox.isSelected();

        // Coleta e converte a data
        Date readDate = null;
        if (readStatus && readDatePicker.getValue() != null) {
            readDate = Date.from(readDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
        }

        // Coleta e cria as informações de avaliação
        ReviewInfo reviewInfo = new ReviewInfo();
        if (readStatus && (!ratingField.getText().isBlank() || !reviewCommentArea.getText().isBlank())) {
            int rating = ratingField.getText().isBlank() ? 0 : Integer.parseInt(ratingField.getText());
            String comment = reviewCommentArea.getText();
            reviewInfo.evaluate(rating, comment);
        }

        // Retorna um novo objeto Book usando o construtor completo
        return new Book(title, originalTitle, genres, year, author, publisher, isbn, hasCopy, readStatus, readDate, reviewInfo);
    }

    /**
     * Método auxiliar para converter uma string separada por vírgulas em uma lista de strings.
     * @param text O texto do campo de entrada.
     * @return Uma lista de strings.
     */
    private List<String> parseList(String text) {
        if (text == null || text.isBlank()) {
            return new ArrayList<>();
        }
        return Arrays.stream(text.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private void clearInputFieldsAndState() {
        // Limpa todos os TextFields e TextAreas
        titleField.clear(); authorField.clear(); yearField.clear(); originalTitleField.clear(); publisherField.clear(); isbnField.clear(); genreField.clear(); ratingField.clear(); reviewCommentArea.clear();
        // Reseta os CheckBoxes
        hasCopyCheckBox.setSelected(false);
        readStatusCheckBox.setSelected(false);
        // Limpa o seletor de data
        readDatePicker.setValue(null);
        // Reseta o estado para o modo "criação"
        this.bookToEdit = null;
        if (saveButton != null) { // Adiciona verificação para segurança
            saveButton.setText("Adicionar Livro");
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