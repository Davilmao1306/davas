package org.diariocultural;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*; // For Scanner, List, ArrayList, Date, Calendar, Comparator
import java.util.stream.Collectors; // For stream operations on lists

// Assuming dependency classes exist
// import com.example.model.Movie;
// import com.example.model.common.Review;
// import com.example.model.common.ReviewInfo;
// import com.example.view.common.MediaView;

/**
 * View especializada para interações com o usuário relacionadas a {@link Movie}.
 * Estende {@link MediaView} e fornece métodos para obter detalhes do filme,
 * exibir informações do filme (completa e resumida) e lidar com entradas específicas.
 *
 * @see MediaView
 * @see Movie
 */
public class MovieView extends MediaView {

    /** Formatador para entrada/saída de datas (dd/MM/yyyy) e Scanner para input. */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    static { dateFormat.setLenient(false); } // Garante análise estrita de datas
    private final Scanner scanner;

    /**
     * Inicializa a view e o Scanner para leitura da entrada do usuário.
     */
    public MovieView() {
        super(); // Chama construtor da superclasse, se houver inicializações lá
        this.scanner = new Scanner(System.in);
    }

    /**
     * Coleta todos os detalhes necessários do usuário para criar um novo objeto {@link Movie}.
     * Inclui título, gênero, duração, ano, diretor, sinopse, elenco, plataformas,
     * status de visualização, data e avaliação inicial (se aplicável).
     *
     * @return Um novo objeto {@link Movie} preenchido com os dados inseridos.
     */
    public Movie getMovieDetails() {
        displayMessage("\n=== CADASTRO DE FILME ===");

        String title = getInput("Título do filme:");
        String originalTitle = getInput("Título original (deixe em branco se igual):");
        if (originalTitle.isBlank()) originalTitle = title; // Usa título principal se original estiver vazio

        List<String> genre = getListInput("Gêneros (separados por vírgula):");
        int duration = getValidatedInt("Duração (minutos):", 1, 900); // Ex: valida entre 1 min e 15h
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int releaseYear = getValidatedInt("Ano de lançamento:", 1888, currentYear + 5); // Valida ano (ex: 1888 a atual+5)
        String director = getInput("Diretor:");
        String synopsis = getInput("Sinopse:"); // Permite sinopse vazia
        List<String> cast = getListInput("Elenco principal (separado por vírgula):");
        List<String> whereToWatch = getListInput("Onde assistir (plataformas, separado por vírgula):");

        boolean watchedStatus = getYesNoInput("Você já assistiu este filme? (s/n)");
        Date watchDate = null;
        ReviewInfo collectedReviewInfo = new ReviewInfo(); // Instancia para coletar avaliação

        // Coleta dados adicionais se o filme já foi assistido
        if (watchedStatus) {
            watchDate = getWatchDateInput("Data de visualização (dd/MM/yyyy, deixe em branco se não souber):");
            if (getYesNoInput("Deseja adicionar uma avaliação para este filme? (s/n)")) {
                int rating = getRatingInput("Nota (0-5):"); // Usa método específico para nota
                String comment = getInput("Comentário (opcional):");
                collectedReviewInfo.evaluate(rating, comment); // Adiciona a avaliação coletada
            }
        }

        // Cria e retorna o objeto Movie com todos os dados coletados
        return new Movie(
                title, originalTitle, genre, duration, releaseYear, director,
                synopsis, cast, whereToWatch, watchedStatus, watchDate, collectedReviewInfo
        );
    }

    /**
     * Exibe os detalhes formatados de um {@link Movie} específico no console.
     *
     * @param movie O filme a ser exibido. Se null, exibe uma mensagem de erro.
     */
    public void displayMovieDetails(Movie movie) {
        if (movie == null) {
            displayMessage(" Filme inválido ou não encontrado.");
            return;
        }
        displayMessage("\n=== DETALHES DO FILME [ID: " + movie.getMovieId() + "] ===");
        displayMessage("Título: " + movie.getTitle());
        // Mostra título original apenas se for diferente do principal
        if (movie.getOriginalTitle() != null && !movie.getTitle().equalsIgnoreCase(movie.getOriginalTitle())) {
            displayMessage("Título Original: " + movie.getOriginalTitle());
        }
        List<String> genres = movie.getGenre();
        displayMessage("Gêneros: " + ((genres != null && !genres.isEmpty()) ? String.join(", ", genres) : "N/A"));
        displayMessage("Ano: " + movie.getReleaseYear());
        displayMessage("Duração: " + movie.getDuration() + " min");
        displayMessage("Diretor: " + movie.getDirector());
        displayMessage("Sinopse: " + movie.getSynopsis());
        List<String> cast = movie.getCast();
        displayMessage("Elenco: " + ((cast != null && !cast.isEmpty()) ? String.join(", ", cast) : "N/A"));
        List<String> platforms = movie.getWhereToWatch();
        displayMessage("Onde Assistir: " + ((platforms != null && !platforms.isEmpty()) ? String.join(", ", platforms) : "N/A"));
        displayMessage("Status: " + (movie.isWatchedStatus() ? "✅ Assistido" : "⬜ Não assistido"));

        // Exibe data e avaliação apenas se o filme foi assistido
        if (movie.isWatchedStatus()) {
            if (movie.getWatchDate() != null) {
                displayMessage("Assistido em: " + formatDate(movie.getWatchDate())); // Reusa formatador
            } else {
                displayMessage("Assistido em: (Data não registrada)");
            }
            displayReviewInfo(movie.getReviewInfo()); // Mostra detalhes da avaliação
        }
    }

    /**
     * Formata um objeto {@link Date} para uma string "dd/MM/yyyy".
     *
     * @param date A data a ser formatada.
     * @return A string formatada ou "N/A" se a data for nula.
     */
    public String formatDate(Date date) {
        if (date == null) return "N/A";
        return dateFormat.format(date);
    }

    /**
     * Exibe uma lista de filmes, mostrando os detalhes completos de cada um.
     * Usado para a listagem principal após filtros/ordenação.
     *
     * @param movies A lista de {@link Movie} a ser exibida. Ignorado se for nula ou vazia.
     */
    public void displayMovies(List<Movie> movies) {
        if (movies == null || movies.isEmpty()) {
            // A mensagem "Nenhum filme encontrado..." geralmente é tratada no Controller
            return;
        }
        displayMessage("\n=== LISTA DE FILMES (" + movies.size() + ") ===");
        movies.forEach(this::displayMovieDetails); // Chama displayMovieDetails para cada filme
        displayMessage("--- Fim da Lista ---");
    }

    /**
     * Exibe uma lista de filmes em formato resumido, ideal para resultados de busca.
     * Mostra ID, título, ano, diretor e nota média (se assistido).
     *
     * @param movies A lista de {@link Movie} a ser exibida resumidamente. Ignorado se for nula ou vazia.
     */
    public void displayMoviesBrief(List<Movie> movies) {
        if (movies == null || movies.isEmpty()) return;
        displayMessage("\n--- Resultados da Busca ---");
        movies.forEach(m -> displayMessage(String.format("- [%d] %s (%d) - Dir: %s - Nota: %.1f",
                m.getMovieId(),
                m.getTitle(),
                m.getReleaseYear(),
                m.getDirector(),
                (m.isWatchedStatus() && m.getReviewInfo() != null) ? m.getAverageRating() : 0.0 // Mostra média se assistido/avaliado
        )));
        displayMessage("----------------------");
    }

    /**
     * Exibe as informações de avaliação (média, contagem, histórico) de um {@link ReviewInfo}.
     * Formata a saída de forma legível, ordenando o histórico por data (mais recente primeiro).
     *
     * @param reviewInfo O objeto contendo as informações de avaliação.
     */
    private void displayReviewInfo(ReviewInfo reviewInfo) {
        displayMessage("\n--- Avaliação(ões) ---");
        if (reviewInfo == null || reviewInfo.getReviewCount() == 0) {
            displayMessage("Nenhuma avaliação registrada.");
            return;
        }

        displayMessage(String.format("Média: %.1f/5 (%d avaliações)",
                reviewInfo.getAverageRating(),
                reviewInfo.getReviewCount()));

        // Exibe histórico ordenado pela data da avaliação (mais recentes primeiro)
        List<Review> sortedReviews = new ArrayList<>(reviewInfo.getReviews());
        sortedReviews.sort(Comparator.comparing(Review::reviewDate, Comparator.nullsLast(Comparator.reverseOrder()))); // nullsLast para datas nulas

        displayMessage("Histórico:");
        for (Review review : sortedReviews) {
            String reviewDateStr = formatDate(review.reviewDate()); // Reutiliza formatDate
            displayMessage(String.format("  - [%d/5] (%s): \"%s\"",
                    review.rating(),
                    reviewDateStr,
                    review.comment()));
        }
    }

    // --- Métodos de Input Genéricos e Validados ---

    /**
     * Solicita e lê uma data do usuário no formato "dd/MM/yyyy".
     * Repete a solicitação até que uma data válida seja inserida ou a entrada seja deixada em branco.
     *
     * @param prompt A mensagem a ser exibida ao usuário.
     * @return A {@link Date} inserida ou {@code null} se a entrada for deixada em branco.
     */
    public Date getWatchDateInput(String prompt) {
        while (true) {
            try {
                String dateStr = getInput(prompt); // Usa getInput local
                if (dateStr == null || dateStr.isBlank()) {
                    return null; // Permite entrada em branco
                }
                return dateFormat.parse(dateStr.trim()); // Tenta converter
            } catch (ParseException e) {
                displayMessage(" Data inválida! Use o formato dd/MM/yyyy ou deixe em branco.");
            }
        }
    }

    /**
     * Lê uma data opcionalmente, útil para atualizações.
     * Reutiliza {@link #getWatchDateInput(String)}, retornando null se a entrada for em branco.
     *
     * @param prompt A mensagem a ser exibida ao usuário.
     * @return A {@link Date} inserida ou {@code null} se a entrada for deixada em branco (indicando manter valor atual).
     */
    public Date getOptionalWatchDateInput(String prompt) {
        return getWatchDateInput(prompt); // Reutiliza a lógica existente
    }

    /**
     * Lê uma linha de entrada contendo itens separados por vírgula e os retorna como uma lista de strings.
     * Remove espaços extras e itens vazios. Retorna {@code null} se a entrada inicial for em branco (útil para atualizações).
     * Sobrescreve o método da superclasse para adicionar a lógica de retorno nulo.
     *
     * @param prompt A mensagem a ser exibida ao usuário.
     * @return Uma {@link List} de strings ou {@code null} se a entrada for em branco.
     */
    @Override
    public List<String> getListInput(String prompt) {
        String input = getInput(prompt);
        if (input == null || input.isBlank()) {
            return null; // Sinaliza "sem alteração" para o controller em caso de atualização
        }
        // Processa a string: divide por vírgula, remove espaços, filtra vazios e coleta em lista imutável
        return Arrays.stream(input.split("\\s*,\\s*"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    /**
     * Solicita e lê um número inteiro do usuário, validando se está dentro de um intervalo especificado.
     * Repete a solicitação até que um número válido seja inserido. Não permite entrada em branco.
     *
     * @param prompt A mensagem a ser exibida ao usuário.
     * @param min O valor mínimo permitido (inclusivo).
     * @param max O valor máximo permitido (inclusivo).
     * @return O número inteiro validado inserido pelo usuário.
     */
    public int getValidatedInt(String prompt, int min, int max) {
        while (true) {
            try {
                String inputStr = getInput(prompt);
                if (inputStr == null || inputStr.isBlank()) {
                    displayMessage(" Entrada não pode ser vazia.");
                    continue;
                }
                int value = Integer.parseInt(inputStr.trim());
                if (value >= min && value <= max) {
                    return value;
                } else {
                    displayMessage(String.format(" Valor fora do intervalo! Insira um número entre %d e %d.", min, max));
                }
            } catch (NumberFormatException e) {
                displayMessage(" Entrada inválida! Por favor, insira um número inteiro.");
            }
        }
    }

    /**
     * Lê um inteiro opcionalmente, útil para atualizações.
     * Permite entrada em branco, que retorna -1 para indicar "manter valor atual".
     * Valida se o número inserido (se não estiver em branco) está dentro do intervalo.
     *
     * @param prompt A mensagem a ser exibida ao usuário.
     * @param min O valor mínimo permitido (inclusivo).
     * @param max O valor máximo permitido (inclusivo).
     * @return O inteiro validado inserido, ou -1 se a entrada for deixada em branco.
     */
    public int getOptionalValidatedInt(String prompt, int min, int max) {
        while (true) {
            try {
                String inputStr = getInput(prompt);
                if (inputStr == null || inputStr.isBlank()) {
                    return -1; // Sinaliza para não alterar o valor existente
                }
                int value = Integer.parseInt(inputStr.trim());
                if (value >= min && value <= max) {
                    return value;
                }
                displayMessage(String.format(" Valor fora do intervalo! (%d-%d).", min, max));
            } catch (NumberFormatException e) {
                displayMessage(" Número inválido. Digite um número ou deixe em branco para manter.");
            }
        }
    }

    /**
     * Solicita e lê uma resposta 's' (sim) ou 'n' (não) do usuário.
     * Repete a solicitação até que uma resposta válida seja inserida (case-insensitive).
     * Sobrescreve o método da superclasse.
     *
     * @param prompt A mensagem a ser exibida ao usuário.
     * @return {@code true} para 's', {@code false} para 'n'.
     */
    @Override
    public boolean getYesNoInput(String prompt) {
        while (true) {
            String resp = getInput(prompt);
            if (resp != null) {
                resp = resp.toLowerCase().trim();
                if (resp.equals("s")) return true;
                if (resp.equals("n")) return false;
            }
            displayMessage(" Resposta inválida. Por favor, digite 's' ou 'n'.");
        }
    }

    /**
     * Lê uma resposta 's' (sim) ou 'n' (não) opcionalmente, útil para atualizações.
     * Permite entrada em branco, que retorna {@code null} para indicar "manter valor atual".
     *
     * @param prompt A mensagem a ser exibida ao usuário.
     * @return {@code Boolean.TRUE} para 's', {@code Boolean.FALSE} para 'n', ou {@code null} se a entrada for em branco.
     */
    public Boolean getOptionalYesNoInput(String prompt) {
        while (true) {
            String response = getInput(prompt);
            if (response == null || response.isBlank()) {
                return null; // Sinaliza para não alterar o valor existente
            }
            response = response.toLowerCase().trim();
            if (response.equals("s")) return Boolean.TRUE;
            if (response.equals("n")) return Boolean.FALSE;
            displayMessage(" Resposta inválida! Digite 's', 'n' ou deixe em branco para manter.");
        }
    }

    /**
     * Solicita e lê uma nota (rating) do usuário, validando se está entre 0 e 5.
     *
     * @param prompt A mensagem a ser exibida ao usuário (ex: "Nota (0-5):").
     * @return A nota validada (0 a 5).
     */
    public int getRatingInput(String prompt) {
        return getValidatedInt(prompt, 0, 5); // Reutiliza getValidatedInt com range 0-5
    }

    /**
     * Obtém uma linha de texto da entrada padrão usando o Scanner local,
     * removendo espaços no início e fim.
     * Sobrescreve o método da superclasse para usar o Scanner desta View.
     *
     * @param prompt A mensagem a ser exibida ao usuário antes de ler a entrada.
     * @return A string lida do usuário, sem espaços nas pontas.
     */
    @Override
    public String getInput(String prompt) {
        System.out.println(prompt); // Exibe a mensagem
        return scanner.nextLine().trim(); // Lê a linha e remove espaços extras
    }

    // --- Métodos Auxiliares para Listagem Avançada (Filtro/Ordenação) ---

    /** Pergunta ao usuário se deseja filtrar a lista de filmes por gênero. */
    public boolean askToFilterByGenre() {
        return getYesNoInput("Deseja filtrar por gênero? (s/n)");
    }

    /** Obtém do usuário o termo de gênero para usar no filtro. */
    public String getGenreFilterInput() {
        return getInput("Digite o gênero para filtrar:");
    }

    /** Pergunta ao usuário se deseja filtrar a lista de filmes por ano de lançamento. */
    public boolean askToFilterByYear() {
        return getYesNoInput("Deseja filtrar por ano de lançamento? (s/n)");
    }

    /** Obtém do usuário o ano para usar no filtro (0 ou negativo para ignorar). */
    public int getYearFilterInput() {
        // Valida o ano, permitindo 0 como "não filtrar"
        return getValidatedInt("Digite o ano de lançamento para filtrar (ou 0 para ignorar):", 0, 9999);
    }

    /**
     * Apresenta as opções de ordenação para filmes e obtém a escolha do usuário.
     *
     * @return O código da opção de ordenação escolhida (1: Melhor, 2: Pior, 0: Nenhuma).
     */
    public int getSortOptionMovie() {
        displayMessage("\nOpções de Ordenação:");
        displayMessage("1 - Melhor Avaliados (Maior média primeiro)");
        displayMessage("2 - Pior Avaliados (Menor média primeiro)");
        displayMessage("0 - Sem ordenação específica (padrão de cadastro)");
        return getValidatedInt("Escolha a opção de ordenação:", 0, 2);
    }
}