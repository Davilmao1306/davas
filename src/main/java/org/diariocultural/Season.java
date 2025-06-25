package org.diariocultural;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Representa uma temporada de uma Série.
 */
@JsonIgnoreProperties(ignoreUnknown = true) // Para ignorar campos JSON não mapeados na desserialização
public class Season {
    private int seasonNumber;
    private int episodes;
    private int releaseYear;
    private List<String> cast;
    private ReviewInfo reviewInfo; // Avaliações específicas desta temporada

    /**
     * Construtor padrão (sem argumentos) necessário para desserialização pelo Jackson,
     * se não houver um construtor @JsonCreator abrangente.
     */
    public Season() {
        this.cast = new ArrayList<>();
        this.reviewInfo = new ReviewInfo(); // Sempre inicializa para evitar NullPointerExceptions
    }

    /**
     * Construtor principal para criar NOVAS temporadas via código (ex: quando o usuário adiciona).
     */
    public Season(int seasonNumber, int episodes, int releaseYear, List<String> cast) {
        this(); // Chama o construtor padrão para inicializar listas/objetos
        this.seasonNumber = seasonNumber;
        this.episodes = episodes;
        this.releaseYear = releaseYear;
        // Cria uma nova lista baseada na lista recebida, para segurança
        this.cast = (cast != null) ? new ArrayList<>(cast) : new ArrayList<>();
        // ReviewInfo já foi inicializado pelo this()
    }

    /**
     * Construtor para DESSERIALIZAÇÃO pelo Jackson.
     * Usado quando o Jackson lê dados do arquivo JSON para criar objetos Season.
     */
    @JsonCreator
    public Season(
            @JsonProperty("seasonNumber") int seasonNumber,
            @JsonProperty("episodes") int episodes,
            @JsonProperty("releaseYear") int releaseYear,
            @JsonProperty("cast") List<String> cast,
            @JsonProperty("reviewInfo") ReviewInfo reviewInfo) {
        this.seasonNumber = seasonNumber;
        this.episodes = episodes;
        this.releaseYear = releaseYear;
        this.cast = (cast != null) ? new ArrayList<>(cast) : new ArrayList<>();
        // Usa o ReviewInfo do JSON, ou cria um novo se for null
        this.reviewInfo = Objects.requireNonNullElseGet(reviewInfo, ReviewInfo::new);
    }

    /**
     * Adiciona uma avaliação para esta temporada específica.
     * @param rating Nota dada.
     * @param comment Comentário feito.
     */
    public void addReview(int rating, String comment) {
        getReviewInfo().evaluate(rating, comment); // Usa o getter seguro
    }

    // --- Getters (Jackson usa para SERIALIZAR para JSON) ---
    public int getSeasonNumber() { return seasonNumber; }
    public int getEpisodes() { return episodes; }
    public int getReleaseYear() { return releaseYear; }
    public List<String> getCast() { return (cast != null) ? new ArrayList<>(cast) : new ArrayList<>(); } // Retorna cópia

    public ReviewInfo getReviewInfo() {
        // Garante que reviewInfo nunca seja null
        if (this.reviewInfo == null) {
            this.reviewInfo = new ReviewInfo();
        }
        return reviewInfo;
    }

    // --- Setters (Jackson usa para DESSERIALIZAR do JSON) ---
    public void setSeasonNumber(int seasonNumber) { this.seasonNumber = seasonNumber; }
    public void setEpisodes(int episodes) { this.episodes = episodes; }
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }
    public void setCast(List<String> cast) { this.cast = (cast != null) ? new ArrayList<>(cast) : new ArrayList<>(); }
    public void setReviewInfo(ReviewInfo reviewInfo) { this.reviewInfo = reviewInfo; }

    @Override
    public String toString() {
        String castString = (cast != null && !cast.isEmpty()) ? String.join(", ", cast) : "N/A";
        // Usa o getter seguro para reviewInfo
        String reviewSummary = (getReviewInfo() != null) ? getReviewInfo().toString() : "Sem avaliação";

        return String.format("  Temporada %d (%d): %d episódios, Elenco Adicional: [%s], Avaliação: %s",
                seasonNumber, releaseYear, episodes, castString, reviewSummary);
    }
}