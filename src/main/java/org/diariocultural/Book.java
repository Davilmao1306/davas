package org.diariocultural;

import com.fasterxml.jackson.annotation.JsonCreator; // Import para desserialização
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty; // Import para desserialização
import java.util.Date;
import java.util.List;
import java.util.Objects; // Para null check

/**
 * Representa um livro com dados bibliográficos, estado de leitura e avaliações.
 * Herda de Media e implementa lógica de ID específica para livros.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Book extends Media {
    private static int bookCounter = 0; // Contador estático para gerar IDs únicos
    private final int bookId;          // ID único final para cada livro

    private String originalTitle;
    private String author;
    private String publisher;
    private String ISBN;
    private boolean hasCopy;
    private boolean readStatus;
    private Date readDate;
    private ReviewInfo reviewInfo; // Objeto para gerenciar avaliações deste livro

    /**
     * Construtor principal usado para criar NOVOS livros via código (ex: BookView).
     * Incrementa o contador estático para gerar um novo ID único.
     *
     * @param title Título no idioma local.
     * @param originalTitle Título original (pode ser igual ao title).
     * @param genre Lista de gêneros literários.
     * @param releaseYear Ano de publicação.
     * @param author Nome do autor.
     * @param publisher Editora.
     * @param ISBN Código ISBN.
     * @param hasCopy Indica se o usuário possui uma cópia física.
     * @param readStatus Indica se o livro já foi lido pelo usuário.
     * @param readDate Data em que a leitura foi concluída (null se não lido ou data desconhecida).
     * @param collectedReviewInfo Objeto ReviewInfo com avaliações iniciais coletadas (pode estar vazio).
     */
    public Book(String title, String originalTitle, List<String> genre, int releaseYear,
                String author, String publisher, String ISBN,
                boolean hasCopy, boolean readStatus, Date readDate, ReviewInfo collectedReviewInfo) {
        // Chama o construtor da classe pai (Media)
        super(title, genre, releaseYear);
        // Atribui campos específicos do Livro
        this.originalTitle = originalTitle;
        this.author = author;
        this.publisher = publisher;
        this.ISBN = ISBN;
        this.hasCopy = hasCopy;
        this.readStatus = readStatus;
        // Garante que a data só seja atribuída se o livro foi lido (consistência)
        this.readDate = readStatus ? readDate : null;
        // Usa o ReviewInfo coletado, ou cria um novo se nenhum foi passado
        this.reviewInfo = Objects.requireNonNullElseGet(collectedReviewInfo, ReviewInfo::new);
        // --- Geração de ID para NOVOS livros ---
        this.bookId = ++bookCounter; // Incrementa o contador E atribui o novo valor ao ID
    }

    /**
     * Construtor secundário ANOTADO para uso exclusivo pelo Jackson durante a DESSERIALIZAÇÃO (leitura do JSON).
     * Este construtor NÃO incrementa o bookCounter, pois o ID já existe no JSON.
     * Ele recebe todos os campos, incluindo o 'bookId' lido do arquivo.
     * As anotações @JsonCreator e @JsonProperty instruem o Jackson a usar este construtor.
     *
     * @param title Título lido do JSON.
     * @param genre Lista de gêneros lida do JSON.
     * @param releaseYear Ano lido do JSON.
     * @param bookId O ID específico lido do JSON.  <- Importante!
     * @param originalTitle Título original lido do JSON.
     * @param author Autor lido do JSON.
     * @param publisher Editora lida do JSON.
     * @param isbn ISBN lido do JSON.
     * @param hasCopy Status de cópia física lido do JSON.
     * @param readStatus Status de leitura lido do JSON.
     * @param readDate Data de leitura lida do JSON.
     * @param reviewInfo Objeto ReviewInfo lido/construído do JSON.
     */
    @JsonCreator // Indica ao Jackson para usar este construtor ao ler JSON
    public Book(
            @JsonProperty("title") String title, // Mapeia campo "title" do JSON para o parâmetro title
            @JsonProperty("genre") List<String> genre,
            @JsonProperty("releaseYear") int releaseYear,
            @JsonProperty("bookId") int bookId, // Mapeia campo "bookId" do JSON
            @JsonProperty("originalTitle") String originalTitle,
            @JsonProperty("author") String author,
            @JsonProperty("publisher") String publisher,
            @JsonProperty("isbn") String isbn, // Nome da propriedade no JSON pode ser diferente do campo
            @JsonProperty("hasCopy") boolean hasCopy,
            @JsonProperty("readStatus") boolean readStatus,
            @JsonProperty("readDate") Date readDate,
            @JsonProperty("reviewInfo") ReviewInfo reviewInfo)
    {
        super(title, genre, releaseYear); // Chama construtor Media
        this.bookId = bookId; // ATRIBUI o ID lido do JSON, NÃO gera um novo
        this.originalTitle = originalTitle;
        this.author = author;
        this.publisher = publisher;
        this.ISBN = isbn; // Atribui o ISBN lido
        this.hasCopy = hasCopy;
        this.readStatus = readStatus;
        this.readDate = readDate;
        // Se o reviewInfo do JSON for null, inicializa um novo (segurança)
        this.reviewInfo = Objects.requireNonNullElseGet(reviewInfo, ReviewInfo::new);
        // NÃO FAZ ++bookCounter AQUI!
    }


    /**
     * Retorna o valor atual do contador estático de livros.
     * Usado principalmente para depuração ou referência.
     * @return O último ID gerado (ou o valor máximo que o contador atingiu).
     */
    public static int getBookCounter() {
        return bookCounter;
    }

    // --- NOVO MÉTODO ESTÁTICO ---
    /**
     * Atualiza o contador estático 'bookCounter' da classe Book.
     * Deve ser chamado após carregar a lista de livros da persistência (ex: JSON).
     * Define o próximo ID a ser gerado como o maior ID encontrado na lista carregada + 1.
     * Garante que novos livros adicionados não colidam com IDs existentes.
     *
     * @param loadedBooks A lista de livros carregada do arquivo de persistência.
     */
    public static void updateBookCounterBasedOnLoadedData(List<Book> loadedBooks) {
        if (loadedBooks != null && !loadedBooks.isEmpty()) {
            // Encontra o maior bookId na lista carregada usando Streams API
            int maxId = loadedBooks.stream()
                    .mapToInt(Book::getBookId) // Extrai o bookId de cada livro
                    .max() // Encontra o valor máximo (retorna OptionalInt)
                    .orElse(0); // Se lista vazia ou sem IDs, usa 0 como base

            // Define o contador estático para o valor do maior ID encontrado.
            // O construtor principal fará ++bookCounter, então o próximo ID será maxId + 1.
            bookCounter = maxId;
            System.out.println("Contador de ID de Livro ajustado. Próximo ID será: " + (maxId + 1));
        } else {
            // Se não havia dados carregados, garante que o contador comece do zero.
            bookCounter = 0;
            System.out.println("Nenhum dado de livro carregado, contador de ID iniciado em 0.");
        }
    }
    // --- FIM DO NOVO MÉTODO ---


    // --- Getters e Setters existentes ---

    // Getters
    public int getBookId() { return bookId; } // Essencial que este getter exista para Jackson serializar
    public String getOriginalTitle() { return originalTitle; }
    public String getAuthor() { return author; }
    public String getPublisher() { return publisher; }

    @Override
    public int getReleaseYear() {
        return super.getReleaseYear();
    }

    public String getISBN() { return ISBN; }
    public boolean hasCopy() { return hasCopy; }
    public boolean isReadStatus() { return readStatus; }
    public Date getReadDate() { return readDate; }

    /**
     * Retorna o objeto ReviewInfo associado a este livro.
     * Garante que nunca retorne null.
     * @return O objeto ReviewInfo do livro.
     */
    @Override // Sobrescreve o de Media, se existir
    public ReviewInfo getReviewInfo() {
        if (this.reviewInfo == null) {
            this.reviewInfo = new ReviewInfo(); // Inicialização defensiva
        }
        return this.reviewInfo;
    }


    // Setters (usados para atualização)
    public void setOriginalTitle(String originalTitle) { this.originalTitle = originalTitle; }
    public void setAuthor(String author) { this.author = author; }
    public void setPublisher(String publisher) { this.publisher = publisher; }
    public void setISBN(String ISBN) { this.ISBN = ISBN; }
    public void setHasCopy(boolean hasCopy) { this.hasCopy = hasCopy; }
    public void setReadStatus(boolean readStatus) {
        this.readStatus = readStatus;
        // Se marcar como não lido, talvez limpar a data?
        if (!readStatus) {
            this.readDate = null;
        }
    }

    public void setReadDate(Date readDate) {
        // Só permite definir data se estiver marcado como lido
        if (this.readStatus) {
            this.readDate = readDate;
        } else {
            System.err.println("AVISO: Tentativa de definir data de leitura para livro não lido: " + getTitle());
            this.readDate = null; // Garante que fique nulo
        }
    }

    public void setReviewInfo(ReviewInfo reviewInfo) {
        this.reviewInfo = reviewInfo;
    }

    /**
     * Adiciona uma nova avaliação a este livro, delegando ao objeto ReviewInfo.
     * @param rating A nota atribuída (ex: 0-5).
     * @param comment O comentário textual da avaliação.
     */
    @Override // Sobrescreve o de Media, se existir
    public void addReview(int rating, String comment) {
        getReviewInfo().evaluate(rating, comment); // Usa o getter seguro
    }

    // toString para facilitar depuração
    @Override
    public String toString() {
        return "Book [ID=" + bookId + ", Title='" + getTitle() + "', Author='" + author + "', Read=" + readStatus + "]";
    }
}