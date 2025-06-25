package org.diariocultural;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Representa uma avaliação individual feita por um usuário usando um Record.
 * É uma forma moderna e imutável de armazenar dados.
 * Contém a nota (avaliação), um comentário opcional e a data da avaliação.
 */
public record Review(
        int rating,
        String comment,
        Date reviewDate
) {

    /**
     * Este é o construtor canônico do record, chamado tanto na criação de novos objetos
     * quanto pela biblioteca Jackson ao ler o JSON.
     * Incluímos uma validação para a nota e garantimos que o comentário não seja nulo.
     */
    @JsonCreator // Ajuda a Jackson a identificar o construtor a ser usado
    public Review(
            @JsonProperty("rating") int rating,
            @JsonProperty("comment") String comment,
            @JsonProperty("reviewDate") Date reviewDate) {

        // Validação para garantir que a nota está dentro do intervalo esperado
        if (rating >= 0 && rating <= 5) {
            this.rating = rating;
        } else {
            this.rating = 0; // Um valor padrão caso a nota seja inválida
        }

        this.comment = (comment != null) ? comment : ""; // Garante que o comentário nunca seja nulo
        this.reviewDate = reviewDate;
    }

    // --- MÉTODOS GETTER ---
    // Os métodos a seguir são gerados automaticamente pelo record, não é preciso escrevê-los:
    // public int rating() { ... }
    // public String comment() { ... }
    // public Date reviewDate() { ... }


    /**
     * Gera uma representação textual da avaliação.
     * @return String formatada representando a avaliação.
     */
    @Override
    public String toString() {
        SimpleDateFormat simpleFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String formattedDate = (reviewDate != null) ? simpleFormat.format(reviewDate) : "Sem data";
        return "Nota: " + rating + " (" + formattedDate + ") - Comentário: \"" + comment + "\"";
    }
}