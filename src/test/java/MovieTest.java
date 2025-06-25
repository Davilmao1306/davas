import org.diariocultural.Movie;
import org.diariocultural.ReviewInfo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MovieTest {

    private Movie movie;

    @BeforeEach
    void setUp() {
        movie = new Movie(
                "A Origem",
                "Inception",
                Arrays.asList("Sci-Fi", "Thriller"),
                148,
                2010,
                "Christopher Nolan",
                "Um ladrão invade sonhos para roubar segredos.",
                Arrays.asList("Leonardo DiCaprio", "Joseph Gordon-Levitt"),
                Arrays.asList("Netflix", "Amazon Prime"),
                true,
                new Date(),
                new ReviewInfo()
        );
    }

    @Test
    void testMovieCreation() {
        assertEquals("A Origem", movie.getTitle());
        assertEquals("Inception", movie.getOriginalTitle());
        assertEquals(148, movie.getDuration());
        assertEquals(2010, movie.getReleaseYear());
        assertEquals("Christopher Nolan", movie.getDirector());
        assertTrue(movie.isWatchedStatus());
        assertNotNull(movie.getWatchDate());
        assertNotNull(movie.getReviewInfo());
        assertEquals(Arrays.asList("Netflix", "Amazon Prime"), movie.getWhereToWatch());
    }

    @Test
    void testSetters() {
        movie.setOriginalTitle("Inception Original");
        movie.setDuration(150);
        movie.setDirector("Nolan");
        movie.setSynopsis("Nova sinopse");
        movie.setCast(Arrays.asList("Ator 1", "Ator 2"));
        movie.setWhereToWatch(List.of("Disney+"));
        movie.setWatchedStatus(false);

        assertEquals("Inception Original", movie.getOriginalTitle());
        assertEquals(150, movie.getDuration());
        assertEquals("Nolan", movie.getDirector());
        assertEquals("Nova sinopse", movie.getSynopsis());
        assertEquals(Arrays.asList("Ator 1", "Ator 2"), movie.getCast());
        assertEquals(List.of("Disney+"), movie.getWhereToWatch());
        assertFalse(movie.isWatchedStatus());
    }

    @Test
    void testAddReview() {
        movie.addReview(5, "Um dos melhores filmes de todos os tempos.");
        assertEquals(5.0, movie.getReviewInfo().getAverageRating());
    }

    @Test
    void testMovieIdIncrement() {
        Movie movie2 = new Movie(
                "Tenet", "Tenet",
                Arrays.asList("Action", "Sci-Fi"),
                150,
                2020,
                "Christopher Nolan",
                "Agentes manipulam o tempo para evitar a Terceira Guerra Mundial.",
                List.of("John David Washington"),
                List.of("HBO Max"),
                false,
                new Date(),
                new ReviewInfo()
        );
        assertTrue(movie2.getMovieId() > movie.getMovieId());
    }
}
