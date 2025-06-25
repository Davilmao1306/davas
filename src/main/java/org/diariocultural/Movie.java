package org.diariocultural;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
/**
 * Representa um filme de mídia, estendendo {@link Media}.
 */

@JsonIgnoreProperties(ignoreUnknown = true) // Para ignorar campos JSON não mapeados
public class Movie extends Media {
    private static int nextId = 1; // Auto incremento para movieId
    private final int movieId;
    private String originalTitle;
    private int duration;
    private String director;
    private String synopsis;
    private List<String> cast;
    private List<String> whereToWatch;
    private boolean watchedStatus;
    private Date watchDate;
    private ReviewInfo reviewInfo;

    /**
     * Construtor principal para criar NOVOS filmes via código.
     */
    public Movie(String title, String originalTitle, List<String> genre, int duration, int releaseYear,
                 String director, String synopsis, List<String> cast, List<String> whereToWatch,
                 boolean watchedStatus, Date watchDate, ReviewInfo reviewInfoParameter) {
        super(title, genre, releaseYear);
        this.movieId = nextId++; // Gera um ID único para NOVOS filmes
        this.originalTitle = originalTitle;
        this.duration = duration;
        this.director = director;
        this.synopsis = synopsis;
        this.cast = (cast != null) ? new ArrayList<>(cast) : new ArrayList<>();
        this.whereToWatch = (whereToWatch != null) ? new ArrayList<>(whereToWatch) : new ArrayList<>();
        this.watchedStatus = watchedStatus;
        this.watchDate = watchedStatus ? watchDate : null;
        this.reviewInfo = Objects.requireNonNullElseGet(reviewInfoParameter, ReviewInfo::new);
    }

    /**
     * Construtor para DESSERIALIZAÇÃO pelo Jackson.
     * Recebe todos os campos, incluindo o ID, do JSON.
     * NÃO incrementa o nextId.
     */
    @JsonCreator
    public Movie(
            @JsonProperty("title") String title,
            @JsonProperty("genre") List<String> genre,
            @JsonProperty("releaseYear") int releaseYear,
            @JsonProperty("movieId") int movieId, // Recebe o ID do JSON
            @JsonProperty("originalTitle") String originalTitle,
            @JsonProperty("duration") int duration,
            @JsonProperty("director") String director,
            @JsonProperty("synopsis") String synopsis,
            @JsonProperty("cast") List<String> cast,
            @JsonProperty("whereToWatch") List<String> whereToWatch,
            @JsonProperty("watchedStatus") boolean watchedStatus,
            @JsonProperty("watchDate") Date watchDate,
            @JsonProperty("reviewInfo") ReviewInfo reviewInfo
    ) {
        super(title, genre, releaseYear);
        this.movieId = movieId; // Usa o ID do JSON
        this.originalTitle = originalTitle;
        this.duration = duration;
        this.director = director;
        this.synopsis = synopsis;
        this.cast = (cast != null) ? new ArrayList<>(cast) : new ArrayList<>();
        this.whereToWatch = (whereToWatch != null) ? new ArrayList<>(whereToWatch) : new ArrayList<>();
        this.watchedStatus = watchedStatus;
        this.watchDate = watchDate; // Pode ser null
        this.reviewInfo = Objects.requireNonNullElseGet(reviewInfo, ReviewInfo::new);
        // NÃO FAZ nextId++ AQUI!
    }

    // --- Getters (Essenciais para Jackson serializar) ---
    public int getMovieId() { return movieId; }
    public String getOriginalTitle() { return originalTitle; }
    public int getDuration() { return duration; }
    public String getDirector() { return director; }
    public String getSynopsis() { return synopsis; }
    public List<String> getCast() { return (cast != null) ? new ArrayList<>(cast) : new ArrayList<>(); }
    public List<String> getWhereToWatch() { return (whereToWatch != null) ? new ArrayList<>(whereToWatch) : new ArrayList<>();}
    public boolean isWatchedStatus() { return watchedStatus; }
    public Date getWatchDate() { return watchDate; }

    @Override // Para garantir que pegamos o ReviewInfo deste filme
    public ReviewInfo getReviewInfo() {
        if (this.reviewInfo == null) {
            this.reviewInfo = new ReviewInfo();
        }
        return this.reviewInfo;
    }

    // --- Setters (Usados para atualização e possivelmente por Jackson se não usar construtor anotado para tudo) ---
    public void setOriginalTitle(String originalTitle) { this.originalTitle = originalTitle; }
    public void setDuration(int duration) { this.duration = duration; }
    public void setDirector(String director) { this.director = director; }
    public void setSynopsis(String synopsis) { this.synopsis = synopsis; }
    public void setCast(List<String> cast) { this.cast = (cast != null) ? new ArrayList<>(cast) : new ArrayList<>();}
    public void setWhereToWatch(List<String> whereToWatch) { this.whereToWatch = (whereToWatch != null) ? new ArrayList<>(whereToWatch) : new ArrayList<>();}
    public void setWatchedStatus(boolean watchedStatus) {
        this.watchedStatus = watchedStatus;
        if (!watchedStatus) this.watchDate = null;
    }
    public void setWatchDate(Date watchDate) { this.watchDate = this.watchedStatus ? watchDate : null;}
    public void setReviewInfo(ReviewInfo reviewInfo) { this.reviewInfo = reviewInfo;}


    /**
     * Atualiza o contador estático 'nextId' da classe Movie.
     * Deve ser chamado após carregar a lista de filmes da persistência.
     *
     * @param loadedMovies A lista de filmes carregada.
     */
    public static void updateNextIdBasedOnLoadedData(List<Movie> loadedMovies) {
        if (loadedMovies != null && !loadedMovies.isEmpty()) {
            int maxId = loadedMovies.stream()
                    .mapToInt(Movie::getMovieId)
                    .max()
                    .orElse(0);
            nextId = maxId + 1; // O construtor principal fará this.movieId = nextId++;
            System.out.println(" Contador de ID de Filme ajustado. Próximo ID será: " + nextId);
        } else {
            nextId = 1; // Se não há dados, começa do 1
            System.out.println(" Nenhum dado de filme carregado, contador de ID iniciado em 1.");
        }
        if (loadedMovies != null && !loadedMovies.isEmpty()) {
            int maxId = loadedMovies.stream().mapToInt(Movie::getMovieId).max().orElse(0);
            nextId = maxId + 1; // Se o construtor faz movieId = nextId++
        } else {
            nextId = 1;
        }
    }


    // ... (addReview, getAverageRating, toString) ...
    @Override
    public void addReview(int rating, String comment) {
        getReviewInfo().evaluate(rating, comment);
    }

    @Override
    public double getAverageRating() {
        return getReviewInfo().getAverageRating();
    }

    @Override
    public String toString() {
        return "Movie [ID=" + movieId +
                ", Title='" + getTitle() +
                "', Year=" + getReleaseYear() +
                ", Watched=" + watchedStatus +
                ']';
    }
}