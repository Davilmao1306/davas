import org.diariocultural.Book;
import org.diariocultural.ReviewInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class BookTest {

    private Book book; // Será "Assassinato no Expresso do Oriente" no setUp

    @BeforeEach
    void setUp() {
        // Configura o livro principal para os testes usando "Assassinato no Expresso do Oriente"
        book = new Book(
                "Assassinato no Expresso do Oriente", // title
                "Murder on the Orient Express",      // originalTitle
                Arrays.asList("Mistério", "Crime", "Ficção Policial"), // genre
                1934,                                // releaseYear
                "Agatha Christie",                   // author
                "HarperCollins Brasil",              // publisher
                "978-8595081637",                    // ISBN (Exemplo)
                true,                                // hasCopy
                true,                                // readStatus
                new Date(),                          // readDate (precisa ser não nulo se readStatus=true)
                new ReviewInfo()                     // reviewInfo (inicialmente vazio)
        );
    }

    @Test
    void testBookCreation() {
        // Verifica se os dados de "Assassinato no Expresso do Oriente" foram carregados corretamente
        assertEquals("Assassinato no Expresso do Oriente", book.getTitle());
        assertEquals("Murder on the Orient Express", book.getOriginalTitle());
        assertEquals(Arrays.asList("Mistério", "Crime", "Ficção Policial"), book.getGenre());
        assertEquals(1934, book.getReleaseYear());
        assertEquals("Agatha Christie", book.getAuthor());
        assertEquals("HarperCollins Brasil", book.getPublisher());
        assertEquals("978-8595081637", book.getISBN());
        assertTrue(book.hasCopy());
        assertTrue(book.isReadStatus());
        assertNotNull(book.getReadDate()); // Verifica se a data existe, já que foi marcado como lido
        assertNotNull(book.getReviewInfo()); // Verifica se o objeto de review foi criado
        assertEquals(0, book.getReviewInfo().getReviewCount()); // Garante que começou sem reviews
    }

    @Test
    void testUniqueBookId() {
        // Cria um segundo livro ("O Pequeno Príncipe") para comparar IDs
        Book anotherBook = new Book(
                "O Pequeno Príncipe",                   // title
                "Le Petit Prince",                     // originalTitle
                Arrays.asList("Infantojuvenil", "Fábula", "Filosofia"), // genre
                1943,                                // releaseYear
                "Antoine de Saint-Exupéry",           // author
                "Agir",                                // publisher
                "978-8522005256",                    // ISBN (Exemplo)
                false,                               // hasCopy
                false,                               // readStatus
                null,                                // readDate (nulo porque não foi lido)
                new ReviewInfo()                     // reviewInfo
        );

        // Cria um terceiro livro ("1984") para garantir a continuidade do incremento do ID
        Book thirdBook = new Book(
                "1984",                              // title
                "Nineteen Eighty-Four",              // originalTitle
                Arrays.asList("Distopia", "Ficção Científica", "Política"), // genre
                1949,                                // releaseYear
                "George Orwell",                     // author
                "Companhia das Letras",              // publisher
                "978-8535914849",                    // ISBN (Exemplo)
                true,                                // hasCopy
                false,                               // readStatus
                null,                                // readDate (nulo porque não foi lido)
                new ReviewInfo()                     // reviewInfo
        );

        // Verifica se os IDs são diferentes e se estão incrementando
        assertNotEquals(book.getBookId(), anotherBook.getBookId(), "IDs do primeiro e segundo livro não devem ser iguais.");
        assertTrue(anotherBook.getBookId() > book.getBookId(), "ID do segundo livro deve ser maior que o do primeiro.");
        assertNotEquals(anotherBook.getBookId(), thirdBook.getBookId(), "IDs do segundo e terceiro livro não devem ser iguais.");
        assertTrue(thirdBook.getBookId() > anotherBook.getBookId(), "ID do terceiro livro deve ser maior que o do segundo.");
    }

    @Test
    void testSetters() {
        // Testa os setters usando o livro do setUp ("Assassinato no Expresso do Oriente")

        // Mudar autor
        book.setAuthor("Dame Agatha Christie");
        assertEquals("Dame Agatha Christie", book.getAuthor());

        // Mudar status de leitura (era true)
        book.setReadStatus(false);
        assertFalse(book.isReadStatus());

        // Mudar posse de cópia (era true)
        book.setHasCopy(false);
        assertFalse(book.hasCopy());

        // Mudar ISBN
        book.setISBN("1234567890");
        assertEquals("1234567890", book.getISBN());

        // Mudar editora
        book.setPublisher("Outra Editora");
        assertEquals("Outra Editora", book.getPublisher());
    }

    @Test
    void testAddReviewToBook() {
        // Testa a adição de reviews ao livro do setUp

        // Adiciona uma primeira avaliação
        book.addReview(4, "Clássico do mistério, final surpreendente.");
        assertEquals(1, book.getReviewInfo().getReviewCount());
        assertEquals(0.0, book.getAverageRating(), 0.001); // Média com 1 review é a própria nota
        assertEquals(4, book.getReviewInfo().getLastRating());

        // Adiciona uma segunda avaliação (simula um tempo depois)
        try { Thread.sleep(10); } catch (InterruptedException e) { /* Ignora */ }
        book.addReview(5, "Muito bem construído, prende do início ao fim.");
        assertEquals(2, book.getReviewInfo().getReviewCount());
        assertEquals(0, book.getAverageRating(), 0.001); // Média de 4 e 5
        assertEquals(5, book.getReviewInfo().getLastRating()); // Última nota foi 5
    }
}