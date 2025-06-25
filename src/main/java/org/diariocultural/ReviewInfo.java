package org.diariocultural;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Importe esta anotação
import com.fasterxml.jackson.annotation.JsonProperty; // Import para desserialização se necessário para a lista
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Classe do modelo (Model) que gerencia uma lista de avaliações (Review).
 * Permite adicionar novas avaliações, recuperar estatísticas como
 * última nota e média de avaliações, além de acessar a lista completa.
 */
@JsonIgnoreProperties(ignoreUnknown = true) //
public class ReviewInfo {

    /** Lista interna para armazenar as avaliações. */
    private List<Review> reviews;

    /**
     * Construtor padrão que inicializa a lista de avaliações.
     * Necessário para Jackson se não houver outro construtor anotado.
     */
    public ReviewInfo() {
        this.reviews = new ArrayList<>();
    }

    /**
     * Construtor para desserialização pelo Jackson, se você preferir em vez de setter.
     *
     * @param reviews A lista de reviews lida do JSON.
     */
    /*
    @JsonCreator // Se você quiser usar construtor para desserializar 'reviews'
    public ReviewInfo(@JsonProperty("reviews") List<Review> reviews) {
        this.reviews = (reviews != null) ? new ArrayList<>(reviews) : new ArrayList<>();
    }
    */


    /**
     * Cria uma nova avaliação (Review) com a nota e comentário informados,
     * usando a data atual como referência, e adiciona-a à lista.
     *
     * @param rating  Nota dada à mídia (será ajustada para o intervalo 0-5 se necessário).
     * @param comment Comentário textual da avaliação.
     */
    public void evaluate(int rating, String comment) {
        if (this.reviews == null) { // Garante que a lista exista
            this.reviews = new ArrayList<>();
        }
        // Validação da nota
        if (rating < 0) rating = 0;
        if (rating > 5) rating = 5; // Assumindo escala 0-5

        Date currentDate = new Date();
        Review newReview = new Review(rating, comment, currentDate);
        this.reviews.add(newReview);
    }

    /**
     * Retorna uma cópia da lista de avaliações registradas.
     * Este getter é usado pelo Jackson para SERIALIZAR a lista de reviews.
     * @return Nova lista contendo os objetos {@code Review} adicionados.
     */
    public List<Review> getReviews() {
        // Retorna uma cópia para proteger a lista interna de modificações externas
        // se não for pelo Jackson
        return (this.reviews != null) ? new ArrayList<>(this.reviews) : new ArrayList<>();
    }

    /**
     * Define a lista de reviews.
     * Este setter é usado pelo Jackson para DESSERIALIZAR a lista de reviews do JSON.
     * @param reviews A lista de reviews.
     */
    public void setReviews(List<Review> reviews) {
        this.reviews = (reviews != null) ? new ArrayList<>(reviews) : new ArrayList<>();
    }


    /**
     * Obtém a nota (avaliação) da última avaliação registrada.
     * Assume que a última avaliação da lista é a mais recente.
     *
     * @return Nota da última avaliação ou 0 caso nenhuma avaliação tenha sido registrada.
     */
    public int getLastRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0;
        } else {
            // Não há garantia de que a última adicionada é a "mais recente"
            // a menos que você sempre adicione em ordem cronológica.
            // Para pegar a mais recente de verdade, precisaria ordenar por data.
            // Por simplicidade, pegamos a última da lista:
            int lastIndex = reviews.size() - 1;
            Review lastReview = reviews.get(lastIndex);
            return lastReview.rating();
        }
    }

    /**
     * Retorna a quantidade total de avaliações feitas.
     * Este método é CALCULADO e NÃO deve ser um campo no JSON.
     * O Jackson NÃO tentará ler "reviewCount" do JSON por causa de
     * @JsonIgnoreProperties(ignoreUnknown = true) na classe.
     *
     * @return Número de avaliações contidas na lista.
     */
    public int getReviewCount() {
        return (reviews != null) ? reviews.size() : 0;
    }

    /**
     * Retorna uma representação textual da última avaliação e
     * o número total de avaliações.
     *
     * @return String formatada com a última nota e total de avaliações,
     *         ou mensagem padrão caso nenhuma avaliação tenha sido feita.
     */
    @Override
    public String toString() {
        if (reviews == null || reviews.isEmpty()) {
            return "Nenhuma avaliação";
        }
        return String.format("Última Nota: %d/5 (%d avaliações)", getLastRating(), getReviewCount());
    }

    /**
     * Adiciona uma avaliação já existente à lista de avaliações.
     * Pode ser útil para testes, importações ou cópias.
     *
     * @param review Objeto {@code Review} a ser adicionado. Ignorado se for {@code null}.
     */
    public void addReview(Review review) {
        if (this.reviews == null) {
            this.reviews = new ArrayList<>();
        }
        if (review != null) {
            reviews.add(review);
        }
    }

    /**
     * Calcula e retorna a média das notas (avaliações) de todas as avaliações.
     * Você mencionou que usa isso para séries. Isso não é um problema.
     *
     * @return Valor médio das notas, ou {@code 0.0} se não houver avaliações.
     */
    public double getAverageRating() {
        if (reviews == null || reviews.isEmpty()) {
            return 0.0;
        }

        double total = 0;
        for (Review r : reviews) {
            total += r.rating();
        }

        return total / reviews.size();
    }
}