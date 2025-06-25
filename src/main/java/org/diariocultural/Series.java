package org.diariocultural;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


/**
 * Representa uma série de mídia, estendendo {@link Media}.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Series extends Media {

    private static int nextId = 1;
    private final int seriesId;
    private String originalTitle;
    private String creator;
    private int endYear;
    private List<String> whereToWatch;
    private List<String> cast;
    private List<Season> seasons;
    private boolean watchedStatus;


    /**
     * Construtor principal para criar NOVAS séries via código.
     */
    public Series(String title, String originalTitle,String creator, List<String> genre, int releaseYear, int endYear, List<String> whereToWatch, List<String> cast, boolean watchedStatus) {
        super(title, genre, releaseYear);
        this.seriesId = nextId++; // Gera ID único para novas séries
        this.originalTitle = originalTitle;
        this.creator = creator;
        // Validação simples para endYear
        this.endYear = (endYear != 0 && endYear < releaseYear) ? releaseYear : endYear;
        this.watchedStatus = watchedStatus;
        this.whereToWatch = (whereToWatch != null) ? new ArrayList<>(whereToWatch) : new ArrayList<>();
        this.cast = (cast != null) ? new ArrayList<>(cast) : new ArrayList<>();
        this.seasons = new ArrayList<>(); // Começa com lista de temporadas vazia
    }

    /**
     * Construtor para DESSERIALIZAÇÃO pelo Jackson.
     * Recebe todos os campos do JSON, incluindo ID e temporadas.
     */
    @JsonCreator
    public Series(
            @JsonProperty("title") String title,
            @JsonProperty("genre") List<String> genre,
            @JsonProperty("releaseYear") int releaseYear,
            @JsonProperty("seriesId") int seriesId, // Recebe o ID do JSON
            @JsonProperty("originalTitle") String originalTitle,
            @JsonProperty("creator") String creator,
            @JsonProperty("endYear") int endYear,
            @JsonProperty("whereToWatch") List<String> whereToWatch,
            @JsonProperty("cast") List<String> cast,
            @JsonProperty("seasons") List<Season> seasons, // Recebe a lista de temporadas
            @JsonProperty("watchedStatus") boolean watchedStatus) {
        super(title, genre, releaseYear);
        this.seriesId = seriesId; // Usa o ID do JSON
        this.originalTitle = originalTitle;
        this.endYear = endYear;
        this.watchedStatus = watchedStatus;
        this.whereToWatch = (whereToWatch != null) ? new ArrayList<>(whereToWatch) : new ArrayList<>();
        this.cast = (cast != null) ? new ArrayList<>(cast) : new ArrayList<>();
        // Usa a lista de temporadas do JSON, ou cria uma nova se for null
        this.seasons = (seasons != null) ? new ArrayList<>(seasons) : new ArrayList<>();
        // NÃO FAZ nextId++ AQUI!
        this.creator = creator;
    }

    // --- Getters (Jackson usa para SERIALIZAR) ---
    public int getSeriesId() { return seriesId; }
    public String getOriginalTitle() { return originalTitle; }
    public int getEndYear() { return endYear; }
    public boolean isWatchedStatus() { return watchedStatus; }
    public List<String> getWhereToWatch() { return (whereToWatch != null) ? new ArrayList<>(whereToWatch) : new ArrayList<>(); }
    public List<String> getCast() { return (cast != null) ? new ArrayList<>(cast) : new ArrayList<>(); }
    public List<Season> getSeasons() { return (seasons != null) ? new ArrayList<>(seasons) : new ArrayList<>(); }
    public String getCreator() { return creator; }

    // --- Setters (Jackson usa para DESSERIALIZAR, e para atualizações) ---
    public void setOriginalTitle(String originalTitle) { this.originalTitle = originalTitle; }
    public void setEndYear(int endYear) { this.endYear = (endYear != 0 && endYear < getReleaseYear()) ? getReleaseYear() : endYear;}
    public void setWhereToWatch(List<String> whereToWatch) { this.whereToWatch = (whereToWatch != null) ? new ArrayList<>(whereToWatch) : new ArrayList<>(); }
    public void setCast(List<String> cast) { this.cast = (cast != null) ? new ArrayList<>(cast) : new ArrayList<>(); }
    public void setCreator(String creator) { this.creator = creator; }
    public void setWatchedStatus(boolean watchedStatus) { this.watchedStatus = watchedStatus; }
    public void setSeasons(List<Season> seasons) {
        this.seasons = (seasons != null) ? new ArrayList<>(seasons) : new ArrayList<>();
    }


    /**
     * Adiciona uma {@link Season} à lista de temporadas da série.
     * Se a temporada já existir (mesmo número), ela pode ser substituída ou ignorada.
     * (Implementação atual adiciona, não verifica duplicidade de número).
     */
    public void addSeason(Season season) {
        if (this.seasons == null) {
            this.seasons = new ArrayList<>();
        }
        if (season != null) {
            this.seasons.add(season);
        }
    }

    /**
     * Calcula a média das avaliações de todas as temporadas que foram avaliadas.
     */
    @Override
    public double getAverageRating() {
        if (seasons == null || seasons.isEmpty()) {
            return 0.0;
        }
        List<Season> ratedSeasons = seasons.stream()
                .filter(s -> s.getReviewInfo() != null && s.getReviewInfo().getReviewCount() > 0)
                .toList(); // Java 16+ .toList(), senão .collect(Collectors.toList())
        if (ratedSeasons.isEmpty()) {
            return 0.0;
        }
        double totalRatingSum = ratedSeasons.stream()
                .mapToDouble(s -> s.getReviewInfo().getAverageRating())
                .sum();
        return totalRatingSum / ratedSeasons.size();
    }

    /**
     * Conta quantas temporadas desta série possuem pelo menos uma avaliação.
     */
    public long getRatedSeasonsCount() {
        if (seasons == null) return 0;
        return seasons.stream()
                .filter(s -> s.getReviewInfo() != null && s.getReviewInfo().getReviewCount() > 0)
                .count();
    }

    /**
     * Atualiza o contador estático 'nextId' da classe Series.
     * Deve ser chamado após carregar a lista de séries da persistência (ex: JSON).
     */
    public static void updateNextIdBasedOnLoadedData(List<Series> loadedSeries) {
        if (loadedSeries != null && !loadedSeries.isEmpty()) {
            int maxId = loadedSeries.stream()
                    .mapToInt(Series::getSeriesId)
                    .max()
                    .orElse(0);
            // O construtor principal faz nextId++, então aqui definimos nextId como maxId + 1
            // para que o próximo novo ID seja realmente maxId + 1 após o incremento.
            nextId = maxId + 1;
            System.out.println(" Contador de ID de Série ajustado. Próximo ID será: " + nextId);
        } else {
            nextId = 1; // Se não há dados, o próximo ID gerado (após o ++) será 1.
            System.out.println(" Nenhum dado de série carregado, contador de ID para começar do 1.");
        }
    }

    @Override
    public String toString() {
        return String.format("Series [ID=%d, Title='%s', Year=%d, Seasons=%d, AvgRating=%.1f]",
                seriesId, getTitle(), getReleaseYear(), (seasons != null ? seasons.size() : 0), getAverageRating());
    }
}