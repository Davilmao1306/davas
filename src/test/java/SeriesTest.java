import org.diariocultural.Season;
import org.diariocultural.Series;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class SeriesTest {

    /* private Series series; // Usaremos Ted Lasso como exemplo principal

    @BeforeEach
    public void setup() {
        // Dados de exemplo para a série Ted Lasso
        List<String> genres = Arrays.asList("Comedy", "Drama", "Sport");
        List<String> platforms = List.of("Apple TV+"); // Usando List.of para imutabilidade
        List<String> cast = Arrays.asList("Jason Sudeikis", "Hannah Waddingham", "Brett Goldstein", "Juno Temple");

        // Chama o construtor ATUALIZADO de Series (sem ReviewInfo)
        series = new Series(
                "Ted Lasso",
                "Ted Lasso", // Título original igual
                genres,
                2020,       // Ano de lançamento
                0,          // Ano de término (0 = em andamento ou não finalizada oficialmente)
                platforms,
                cast,
                true        // Vamos assumir que foi assistida para testes
        );
    }

    @Test
    public void testSeriesCreation() {
        // Verifica se os campos básicos foram definidos corretamente
        assertEquals("Ted Lasso", series.getTitle());
        assertEquals("Ted Lasso", series.getOriginalTitle());
        assertEquals(2020, series.getReleaseYear());
        assertEquals(0, series.getEndYear()); // Verifica status "em andamento"
        assertEquals(Arrays.asList("Comedy", "Drama", "Sport"), series.getGenre());
        assertEquals(Arrays.asList("Jason Sudeikis", "Hannah Waddingham", "Brett Goldstein", "Juno Temple"), series.getCast());
        assertEquals(List.of("Apple TV+"), series.getWhereToWatch());
        assertTrue(series.isWatchedStatus()); // Verificando o status definido no setup

        // Verifica estado inicial das temporadas e avaliação (baseada em temporadas)
        assertTrue(series.getSeasons().isEmpty(), "A série deve iniciar sem temporadas.");
        assertEquals(0.0, series.getAverageRating(), 0.001, "A média inicial (sem temporadas avaliadas) deve ser 0.0.");
        assertEquals(0, series.getRatedSeasonsCount(), "A contagem inicial de temporadas avaliadas deve ser 0.");
    }

    @Test
    public void testAddSeason() {
        // Cria uma temporada
        Season season1 = new Season(1, 10, 2020, List.of("Phil Dunster")); // Exemplo com dados da Season

        // Adiciona a temporada
        series.addSeason(season1);

        // Verifica se a temporada foi adicionada
        assertEquals(1, series.getSeasons().size(), "Deveria haver 1 temporada após adicionar.");
        // Verifica se a temporada na lista é a que foi adicionada
        assertTrue(series.getSeasons().contains(season1), "A lista de temporadas deveria conter a temporada adicionada.");
        assertEquals(season1, series.getSeasons().getFirst(), "O primeiro elemento da lista deveria ser a temporada adicionada.");
    }

    @Test
    public void testAverageRatingCalculation() {
        // 1. Nenhuma temporada adicionada
        assertEquals(0.0, series.getAverageRating(), 0.001, "Média deve ser 0.0 sem temporadas.");
        assertEquals(0, series.getRatedSeasonsCount(), "Contagem de temporadas avaliadas deve ser 0 sem temporadas.");

        // 2. Adiciona temporadas, mas nenhuma avaliada
        Season season1 = new Season(1, 10, 2020, List.of());
        Season season2 = new Season(2, 12, 2021, List.of());
        series.addSeason(season1);
        series.addSeason(season2);

        assertEquals(0.0, series.getAverageRating(), 0.001, "Média deve ser 0.0 se nenhuma temporada for avaliada.");
        assertEquals(0, series.getRatedSeasonsCount(), "Contagem de temporadas avaliadas deve ser 0 se nenhuma for avaliada.");

        // 3. Avalia apenas a primeira temporada (ex: nota 5 e 4 => média 4.5)
        season1.addReview(5, "Excelente início!");
        season1.addReview(4, "Muito bom desenvolvimento.");
        // Média da temporada 1 = (5+4)/2 = 4.5
        // Média da série = Média da temporada 1 / 1 temporada avaliada = 4.5
        assertEquals(4.5, series.getAverageRating(), 0.001, "Média da série deve ser a média da única temporada avaliada.");
        assertEquals(1, series.getRatedSeasonsCount(), "Contagem de temporadas avaliadas deve ser 1.");

        // 4. Avalia a segunda temporada também (ex: nota 5 => média 5.0)
        season2.addReview(5, "Perfeita!");
        // Média da temporada 2 = 5.0
        // Média da série = (Média T1 + Média T2) / 2 temporadas avaliadas = (4.5 + 5.0) / 2 = 9.5 / 2 = 4.75
        assertEquals(4.75, series.getAverageRating(), 0.001, "Média da série deve ser a média das médias das temporadas avaliadas.");
        assertEquals(2, series.getRatedSeasonsCount(), "Contagem de temporadas avaliadas deve ser 2.");

        // 5. Adiciona uma terceira temporada, não avaliada
        Season season3 = new Season(3, 12, 2023, List.of());
        series.addSeason(season3);

        // A média da série e a contagem de avaliadas não devem mudar
        assertEquals(4.75, series.getAverageRating(), 0.001, "Média da série não deve mudar ao adicionar temporada não avaliada.");
        assertEquals(2, series.getRatedSeasonsCount(), "Contagem de temporadas avaliadas não deve mudar ao adicionar temporada não avaliada.");

        // 6. Avalia a terceira temporada (ex: nota 3 e 4 => média 3.5)
        season3.addReview(3, "Ok.");
        season3.addReview(4, "Melhorou no final.");
        // Média da temporada 3 = (3+4)/2 = 3.5
        // Média da série = (Média T1 + Média T2 + Média T3) / 3 = (4.5 + 5.0 + 3.5) / 3 = 13.0 / 3 = 4.333...
        assertEquals(13.0 / 3.0, series.getAverageRating(), 0.001, "Média da série deve recalcular com a nova temporada avaliada.");
        assertEquals(3, series.getRatedSeasonsCount(), "Contagem de temporadas avaliadas deve ser 3.");
    }


    @Test
    public void testSetWatchedStatus() {
        // Estado inicial definido no setup como true
        assertTrue(series.isWatchedStatus());
        series.setWatchedStatus(false); // Muda para false
        assertFalse(series.isWatchedStatus()); // Verifica se mudou
        series.setWatchedStatus(true); // Muda de volta para true
        assertTrue(series.isWatchedStatus()); // Verifica se mudou
    }

    @Test
    public void testSettersAndUpdateFields() {
        // Testar setters individuais adicionados para Edição Completa

        // Título (Media)
        series.setTitle("Ted Lasso Renomeado");
        assertEquals("Ted Lasso Renomeado", series.getTitle());

        // Título Original (Series)
        series.setOriginalTitle("Ted Lasso Original Title Changed");
        assertEquals("Ted Lasso Original Title Changed", series.getOriginalTitle());

        // Gênero (Media)
        List<String> newGenres = List.of("Sitcom");
        series.setGenre(newGenres);
        assertEquals(newGenres, series.getGenre());

        // Ano Lançamento (Media)
        series.setReleaseYear(2019);
        assertEquals(2019, series.getReleaseYear());

        // Ano Término (Series)
        series.setEndYear(2023);
        assertEquals(2023, series.getEndYear());
        series.setEndYear(0); // Volta para "em andamento"
        assertEquals(0, series.getEndYear());

        // Onde Assistir (Series)
        List<String> newPlatforms = List.of("Outra Plataforma");
        series.setWhereToWatch(newPlatforms);
        assertEquals(newPlatforms, series.getWhereToWatch());

        // Elenco (Series)
        List<String> newCast = List.of("Novo Ator");
        series.setCast(newCast);
        assertEquals(newCast, series.getCast());
    }


    @Test
    public void testSetWhereToWatchDefensiveCopy() {
        // Teste específico para cópia defensiva, já que estava no original
        List<String> initialPlatforms = series.getWhereToWatch(); // Pega a lista inicial
        List<String> externalList = new ArrayList<>(initialPlatforms); // Cria cópia externa modificável
        externalList.add("Mais Uma Plataforma"); // Modifica a cópia externa

        // A lista interna da série não deve ter sido afetada
        assertNotEquals(externalList, series.getWhereToWatch(), "A lista interna não deveria ser igual à lista externa modificada.");
        assertEquals(initialPlatforms, series.getWhereToWatch(), "A lista interna deveria permanecer inalterada.");

        // Testa o setter também
        List<String> listToSet = new ArrayList<>(List.of("HBO Max", "Disney+"));
        series.setWhereToWatch(listToSet);
        assertEquals(List.of("HBO Max", "Disney+"), series.getWhereToWatch()); // Verifica se setou

        // Modifica a lista externa usada no set
        listToSet.add("Star+");
        assertNotEquals(listToSet, series.getWhereToWatch(), "Modificar a lista externa após o set não deve afetar a interna.");
        assertEquals(List.of("HBO Max", "Disney+"), series.getWhereToWatch(), "A lista interna após o set deve ser uma cópia.");
    }

    @Test
    public void testAddNullSeason() {
        int initialSeasonCount = series.getSeasons().size();
        series.addSeason(null);
        // A lista de temporadas deve continuar com o mesmo tamanho
        assertEquals(initialSeasonCount, series.getSeasons().size(), "Adicionar null não deve alterar o número de temporadas.");
    }

    @Test
    public void testSetNullWhereToWatch() {
        // Testa definir a lista de plataformas como nula
        series.setWhereToWatch(null);
        // O getter deve retornar uma lista vazia, não nula
        assertNotNull(series.getWhereToWatch(), "Getter de whereToWatch não deve retornar null.");
        assertTrue(series.getWhereToWatch().isEmpty(), "Definir whereToWatch como null deve resultar em uma lista vazia.");
    }

    @Test
    public void testSetNullCast() {
        // Testa definir a lista de elenco como nula
        series.setCast(null);
        // O getter deve retornar uma lista vazia, não nula
        assertNotNull(series.getCast(), "Getter de cast não deve retornar null.");
        assertTrue(series.getCast().isEmpty(), "Definir cast como null deve resultar em uma lista vazia.");
    }
    */
}
