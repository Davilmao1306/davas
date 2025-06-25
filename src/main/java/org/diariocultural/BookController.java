package org.diariocultural;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.DeserializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.*; // Para Comparator, Collections, Date, Optional, etc.
import java.util.stream.Collectors;

/**
 * Controlador responsável por gerenciar as operações CRUD (Criar, Ler, Atualizar, Deletar),
 * busca e listagem avançada para a entidade {@link Book}.
 * Utiliza {@link MovieView} para interações específicas de livros e {@link MediaView}
 * para mensagens genéricas.
 *
 * @see Book
 * @see BookView
 * @see MediaView
 */
public class BookController {

    private List<Book> books;
    private final BookView bookView;
    private static final String DATA_DIRECTORY = "data";
    private static final String FILE_NAME = "books.json";
    private static final String FILE_PATH = DATA_DIRECTORY + File.separator + FILE_NAME;

    public BookController() {
        this.bookView = new BookView();
        loadData();
        if (this.books == null) {
            this.books = new ArrayList<>();
        }
        Book.updateBookCounterBasedOnLoadedData(this.books);
    }

    public void addBook() {
        Book book = bookView.getBookDetails();
        if (book != null) {
            books.add(book);
            System.out.println(" Livro '" + book.getTitle() + "' adicionado com sucesso!");
            saveData();
        } else {
            System.out.println(" Cadastro de livro cancelado ou falhou.");
        }
    }

    public void addBookViaObject(Book book) {
        if (book != null) {
            // Validação opcional: Verificar se um livro com o mesmo ID ou título/autor já existe
            // para evitar duplicatas, dependendo da sua regra de negócio.
            // Por simplicidade, vamos apenas adicionar.
            this.books.add(book);
            System.out.println("📖 Livro '" + book.getTitle() + "' adicionado via UI/Objeto!");
            saveData(); // Persiste a adição
        } else {
            System.out.println("❌ Tentativa de adicionar um objeto Book nulo.");
            // Ou lançar uma exceção, ou registrar um log.
        }
    }

    public void updateBook(Book updatedBook) {
        // A lógica de encontrar e substituir pode variar, mas esta é uma abordagem simples
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getBookId() == updatedBook.getBookId()) {
                books.set(i, updatedBook); // Substitui o livro antigo pelo novo
                saveData();
                System.out.println("Livro '" + updatedBook.getTitle() + "' atualizado.");
                return;
            }
        }
    }

    private void displayReviewHistory(Book book) {
        // (Seu método displayReviewHistory, como já definido antes)
        if (book == null || book.getReviewInfo() == null) return;
        ReviewInfo ri = book.getReviewInfo();
        List<Review> reviews = ri.getReviews();
        if (reviews.isEmpty()) {
            bookView.displayMessage("  Nenhuma avaliação registrada anteriormente para este livro.");
        } else {
            bookView.displayMessage("  Total de avaliações: " + ri.getReviewCount());
            bookView.displayMessage("  Histórico (mais recentes primeiro):");
            List<Review> reversedReviews = new ArrayList<>(reviews);
            Collections.reverse(reversedReviews);
            int limit = Math.min(reversedReviews.size(), 5);
            for (int i = 0; i < limit; i++) {
                bookView.displayMessage("    - " + reversedReviews.get(i).toString());
            }
            if (reversedReviews.size() > limit) {
                bookView.displayMessage("    ... (e mais " + (reversedReviews.size() - limit) + " outras avaliações)");
            }
        }
    }

    public void removeBook(Book bookToRemove) {
        if (bookToRemove != null && books.contains(bookToRemove)) {
            books.remove(bookToRemove);
            System.out.println("Livro '" + bookToRemove.getTitle() + "' removido com sucesso!");
            saveData();
        } else {
            System.err.println("Tentativa de remover um livro nulo ou que não existe na lista.");
        }
    }

    public Optional<Book> findBookByTitle(String title) {
        return findBookByTitleInternal(title);
    }

    private Optional<Book> findBookByTitleInternal(String title) {
        // (Seu método findBookByTitleInternal, como já definido antes)
        if (title == null || title.isBlank()) {
            return Optional.empty();
        }
        String lowerTitle = title.toLowerCase().trim();
        Optional<Book> found = books.stream()
                .filter(b -> b.getTitle().equalsIgnoreCase(lowerTitle))
                .findFirst();
        if (found.isEmpty()) {
            found = books.stream()
                    .filter(b -> b.getOriginalTitle() != null && b.getOriginalTitle().equalsIgnoreCase(lowerTitle))
                    .findFirst();
        }
        return found;
    }

    public void listAllBooks() {
        // (Seu método listAllBooks, com applyBookFilters e applyBookSorting, como já definido antes)
        if (books.isEmpty()) {
            bookView.displayMessage(" Nenhum livro cadastrado no momento.");
            return;
        }
        List<Book> displayList = new ArrayList<>(this.books);
        boolean wasFilteredOrSorted = false;

        List<Book> filteredList = applyBookFilters(displayList);
        if (filteredList.size() != displayList.size()) {
            wasFilteredOrSorted = true;
        }
        displayList = filteredList;

        if (!displayList.isEmpty()) {
            List<Book> sortedList = applyBookSorting(displayList);
            if (sortedList != displayList || (wasFilteredOrSorted && !displayList.isEmpty())) { // Correção na lógica da flag
                wasFilteredOrSorted = true;
            }
            displayList = sortedList;
        }

        if (displayList.isEmpty()) {
            if (wasFilteredOrSorted) {
                bookView.displayMessage(" Nenhum livro encontrado após aplicar os filtros/ordenação selecionados.");
            }
            // Não precisa do else, pois o primeiro if de books.isEmpty() já trataria.
        } else {
            String listHeader = "\n=== LISTA DE LIVROS (" + displayList.size() + ")";
            if (wasFilteredOrSorted) {
                listHeader += " (Resultados filtrados/ordenados)";
            }
            listHeader += " ===";
            bookView.displayMessage(listHeader);
            // Loop para exibir cada livro (já que BookView não tem displayBooks(List<Book>) mas sim displayBook(Book))
            for (Book book : displayList) {
                bookView.displayBook(book);
                bookView.displayMessage("--------------------");
            }
        }
    }

    private List<Book> applyBookFilters(List<Book> currentList) {
        // (Seu método applyBookFilters, como já definido antes)
        List<Book> filteredList = new ArrayList<>(currentList);
        if (bookView.askToFilterByGenre()) {
            String genreFilter = bookView.getGenreFilterInput();
            if (genreFilter != null && !genreFilter.isBlank()) {
                String lowerGenreFilter = genreFilter.toLowerCase().trim();
                filteredList = filteredList.stream()
                        .filter(book -> book.getGenre() != null && book.getGenre().stream()
                                .anyMatch(g -> g.toLowerCase().contains(lowerGenreFilter)))
                        .collect(Collectors.toList());
                if (filteredList.isEmpty()) {
                    bookView.displayMessage("Nenhum livro encontrado para o gênero: '" + genreFilter + "'.");
                }
            }
        }
        if (!filteredList.isEmpty() && bookView.askToFilterByYear()) {
            int yearFilter = bookView.getYearFilterInput();
            if (yearFilter > 0) {
                filteredList = filteredList.stream()
                        .filter(book -> book.getReleaseYear() == yearFilter)
                        .collect(Collectors.toList());
                if (filteredList.isEmpty()) {
                    bookView.displayMessage("Nenhum livro encontrado para o ano: " + yearFilter + ".");
                }
            }
        }
        return filteredList;
    }

    private List<Book> applyBookSorting(List<Book> currentList) {
        // (Seu método applyBookSorting, ajustado para as opções da BookView, como já definido antes)
        if (currentList.isEmpty()) {
            return currentList;
        }
        int sortOption = bookView.getSortOptionBook();

        if (sortOption == 0) {
            return currentList;
        }

        List<Book> sortedList = new ArrayList<>(currentList);
        Comparator<Book> comparator; // Declarado fora do switch para ser acessível depois

        switch (sortOption) {
            case 1: // Melhor Avaliados
                comparator = Comparator.comparingDouble(
                        (Book b) -> b.isReadStatus() && b.getReviewInfo() != null ? b.getAverageRating() : -1.0
                ).reversed();
                break;
            case 2: // Pior Avaliados
                comparator = Comparator.comparingDouble(
                        (Book b) -> b.isReadStatus() && b.getReviewInfo() != null ? b.getAverageRating() : Double.MAX_VALUE
                );
                break;
            default:
                bookView.displayMessage("Opção de ordenação não aplicada ou inválida.");
                return currentList;
        }
        // Removido 'if (comparator != null)' pois o default já retorna. Se chegar aqui, comparator foi setado.
        sortedList.sort(comparator);
        return sortedList;
    }

    // Adicione este método ao seu BookController/BookService
    public List<Book> getAllBooks() {
        return new ArrayList<>(this.books); // Retorna uma cópia da lista
    }

    public List<Book> searchBooks(String criteria) {
        if (criteria == null || criteria.isBlank()) {
            return new ArrayList<>(this.books); // Retorna todos os livros
        }

        String lowerCriteria = criteria.toLowerCase().trim();
        List<Book> results = books.stream()
                .filter(book ->
                        book.getTitle().toLowerCase().contains(lowerCriteria) ||
                                book.getAuthor().toLowerCase().contains(lowerCriteria) ||
                                book.getISBN().toLowerCase().contains(lowerCriteria) ||
                                (book.getOriginalTitle() != null && book.getOriginalTitle().toLowerCase().contains(lowerCriteria)) ||
                                book.getGenre().stream().anyMatch(genre -> genre.toLowerCase().contains(lowerCriteria)) ||
                                String.valueOf(book.getReleaseYear()).contains(lowerCriteria)
                )
                .collect(Collectors.toList());

        // A lógica de imprimir no console foi removida, pois agora é responsabilidade da GUI
        return results;
    }

    public void saveData() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            File dataDir = new File(DATA_DIRECTORY);
            if (!dataDir.exists()) {
                if (!dataDir.mkdirs()) {
                    System.err.println(" Falha ao criar diretório: " + dataDir.getAbsolutePath());
                    return;
                }
            }
            objectMapper.writeValue(new File(FILE_PATH), books);
        } catch (IOException e) {
            System.err.println("Erro crítico ao salvar dados de livros em " + FILE_PATH + ": " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadData() {
        // (Seu método loadData, como já definido antes)
        // ...
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        File file = new File(FILE_PATH);

        if (file.exists() && file.isFile() && file.length() > 0) {
            try {
                this.books = objectMapper.readValue(file, new TypeReference<List<Book>>() {});
                System.out.println("Dados de livros carregados com sucesso de " + FILE_PATH);
            } catch (IOException e) {
                System.err.println("Erro ao ler ou desserializar o arquivo " + FILE_PATH + ": " + e.getMessage());
                e.printStackTrace();
                this.books = new ArrayList<>();
            }
        } else {
            if (!file.exists()) {
                System.out.println("Arquivo " + FILE_PATH + " não encontrado. Será criado ao salvar.");
            } else {
                System.out.println("Arquivo " + FILE_PATH + " vazio ou inválido. Iniciando com catálogo vazio.");
            }
            this.books = new ArrayList<>();
        }
    }
}