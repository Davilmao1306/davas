package org.diariocultural;

import java.util.List;

/**
 * Representa mídia genérica com título, gêneros, ano de lançamento e avaliações.
 */
public class Media {
    /** Título principal da mídia */
    private String title;
    /** Lista de gêneros associados */
    private List<String> genre;
    /** Ano de lançamento */
    private int releaseYear;
    /** Informações de avaliação (criada ao instanciar) */
    private final ReviewInfo reviewInfo;

    /**
     * Constrói mídia com dados básicos e inicializa avaliações.
     * @param title título da mídia
     * @param genre lista de gêneros
     * @param releaseYear ano de lançamento
     */
    public Media(String title, List<String> genre, int releaseYear) {
        this.title = title;
        this.genre = genre;
        this.releaseYear = releaseYear;
        this.reviewInfo = new ReviewInfo();
    }

    // --- Getters e Setters básicos ---
    public String getTitle()                         { return title; }
    public void setTitle(String title)               { this.title = title; }
    public List<String> getGenre()                   { return genre; }
    public void setGenre(List<String> genre)         { this.genre = genre; }
    public int getReleaseYear()                      { return releaseYear; }
    public void setReleaseYear(int releaseYear)      { this.releaseYear = releaseYear; }

    // --- Métodos de avaliação ---
    /** @return objeto que gerencia avaliações desta mídia */
    public ReviewInfo getReviewInfo()                { return reviewInfo; }
    /** Adiciona nova avaliação com nota e comentário */
    public void addReview(int rating, String comment){ reviewInfo.evaluate(rating, comment); }
    /** Retorna média das avaliações registradas */
    public double getAverageRating()                 { return reviewInfo.getAverageRating(); }
}
