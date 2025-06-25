import org.diariocultural.Media;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class MediaTest {

    private Media media;

    @BeforeEach
    void setUp() {
        media = new Media("Interstellar", Arrays.asList("Sci-Fi", "Drama"), 2014);
    }

    @Test
    void testMediaCreation() {
        assertEquals("Interstellar", media.getTitle());
        assertEquals(Arrays.asList("Sci-Fi", "Drama"), media.getGenre());
        assertEquals(2014, media.getReleaseYear());
        assertNotNull(media.getReviewInfo());
    }

    @Test
    void testSetters() {
        media.setTitle("Oppenheimer");
        media.setGenre(Arrays.asList("Biography", "Drama"));
        media.setReleaseYear(2023);

        assertEquals("Oppenheimer", media.getTitle());
        assertEquals(Arrays.asList("Biography", "Drama"), media.getGenre());
        assertEquals(2023, media.getReleaseYear());
    }

    @Test
    void testAddReviewAndAverage() {
        media.addReview(4, "Ótimo filme!");
        media.addReview(5, "Espetacular!");

        double expectedAverage = (4 + 5) / 2.0;
        assertEquals(expectedAverage, media.getAverageRating());
    }
}
