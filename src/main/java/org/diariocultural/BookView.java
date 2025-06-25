package org.diariocultural;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;



/**
 * View especializada para interações relacionadas a {@link Book}.
 * Estende {@link MediaView} e fornece métodos para obter detalhes do livro,
 * exibir informações do livro e manipular entradas específicas de livros (como ISBN, situação de leitura).
 *
 * @see MediaView
 * @see Book
 */
public class BookView extends MediaView {

    private final Scanner scanner;
    /** Formato padrão para entrada e saída de datas (dd/MM/yyyy), com análise estrita. */
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    static { dateFormat.setLenient(false); } // Garante que datas inválidas como 31/02/2023 sejam rejeitadas

    /**
     * Construtor padrão que inicializa o Scanner.
     */
    public BookView() {
        super(); // Chama construtor da MediaView, se houver inicialização lá
        this.scanner = new Scanner(System.in);
    }

    /**
     * Coleta todos os detalhes necessários do usuário para criar um novo objeto {@link Book}.
     * Inclui título, autor, ISBN, situação de leitura, data e avaliação inicial (se aplicável).
     *
     * @return Um novo objeto {@link Book} preenchido com os dados inseridos pelo usuário.
     */
    public Book getBookDetails() {
        displayMessage("\n=== CADASTRO DE LIVRO ===");
        String title = getInput("Título:");
        String originalTitle = getInput("Título original (deixe em branco se igual):");
        if (originalTitle.isBlank()) originalTitle = title; // Usa o título principal se o original for deixado em branco
        List<String> genre = getListInput("Gêneros (separados por vírgula):");
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        int releaseYear = getValidatedInt("Ano de publicação:", 0, currentYear + 5); // Valida ano (0 a atual+5)
        String author = getInput("Autor:");
        String publisher = getInput("Editora:");
        String ISBN = getInput("ISBN:");
        boolean hasCopy = getYesNoInput("Tem cópia física? (s/n)");
        boolean readStatus = getYesNoInput("Já foi lido? (s/n)");
        Date readDate = null;
        ReviewInfo collectedReviewInfo = new ReviewInfo(); // Cria instância para coletar avaliação

        // Coleta dados adicionais se o livro já foi lido
        if (readStatus) {
            readDate = getWatchDateInput("Data de leitura (dd/MM/yyyy, deixe em branco se não souber):");
            if (getYesNoInput("Deseja adicionar uma avaliação para este livro? (s/n)")) {
                int rating = getValidatedInt("Nota (0-5):", 0, 5);
                String comment = getInput("Comentário (opcional):");
                collectedReviewInfo.evaluate(rating, comment); // Adiciona a primeira avaliação
            }
        }
        // Cria e retorna o objeto Book
        return new Book(title, originalTitle, genre, releaseYear,
                author, publisher, ISBN, hasCopy,
                readStatus, readDate, collectedReviewInfo);
    }

    /**
     * Exibe os detalhes formatados de um {@link Book} no console.
     * Inclui informações básicas, situação de leitura, data e detalhes da avaliação (se aplicável).
     *
     * @param book O livro a ser exibido. Se null, exibe mensagem de erro.
     */
    public void displayBook(Book book) {
        if (book == null) {
            displayMessage(" Livro inválido ou não encontrado.");
            return;
        }
        displayMessage("\n--- DETALHES DO LIVRO ---");
        displayMessage("ID: " + book.getBookId());
        displayMessage("Título: " + book.getTitle());
        // Mostra título original apenas se for diferente do principal
        if (book.getOriginalTitle() != null && !book.getTitle().equalsIgnoreCase(book.getOriginalTitle())) {
            displayMessage("Título Original: " + book.getOriginalTitle());
        }
        displayMessage("Autor: " + book.getAuthor());
        displayMessage("Editora: " + book.getPublisher());
        displayMessage("ISBN: " + book.getISBN());
        List<String> genres = book.getGenre();
        displayMessage("Gêneros: " + ((genres != null && !genres.isEmpty()) ? String.join(", ", genres) : "N/A"));
        displayMessage("Ano Publicação: " + book.getReleaseYear());
        displayMessage("Cópia Física: " + (book.hasCopy() ? "Sim" : "Não"));
        displayMessage("Status Leitura: " + (book.isReadStatus() ? " Lido" : " Não lido"));

        // Mostra detalhes de leitura e avaliação apenas se marcado como lido
        if (book.isReadStatus()) {
            if (book.getReadDate() != null) {
                displayMessage("Data de Leitura: " + formatDate(book.getReadDate()));
            } else {
                displayMessage("Data de Leitura: (Não registrada)");
            }
            displayReviewInfo(book.getReviewInfo()); // Exibe informações de avaliação
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
     * Solicita e lê uma data do usuário no formato "dd/MM/yyyy".
     * Repete a solicitação até que uma data válida seja inserida ou a entrada, seja deixada em branco.
     *
     * @param prompt A mensagem a ser exibida ao usuário.
     * @return A {@link Date} inserida ou {@code null} se a entrada for deixada em branco.
     */
    public Date getWatchDateInput(String prompt) {
        while (true) {
            try {
                String dateStr = getInput(prompt);
                if (dateStr.isBlank()) return null; // Permite entrada em branco para indicar "não sei" ou "não quero informar"
                return dateFormat.parse(dateStr.trim()); // Tenta converter a string para Date
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
        return getWatchDateInput(prompt); // A lógica de retornar null se em branco já está em getWatchDateInput
    }

    /**
     * Lê uma linha de entrada contendo itens separados por vírgula e os retorna como uma lista de strings.
     * Remove espaços extras e itens vazios. Retorna {@code null} se a entrada inicial for em branco.
     * Sobrescreve o método da superclasse para adicionar a lógica de retorno nulo.
     *
     * @param prompt A mensagem a ser exibida ao usuário.
     * @return Uma {@link List} de strings ou {@code null} se a entrada for em branco.
     */
    @Override
    public List<String> getListInput(String prompt) {
        String input = getInput(prompt);
        if (input.isBlank()) return null; // Retorna null para indicar "sem mudança" em atualizações
        // Processa a string: divide por vírgula, remove espaços, filtra vazios e coleta em lista
        return Arrays.stream(input.split("\\s*,\\s*"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList(); // Retorna lista imutável
    }

    /**
     * Solicita e lê um número inteiro do usuário, validando se está num intervalo especificado.
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
                if (inputStr.isBlank()) {
                    displayMessage(" Entrada não pode ser vazia.");
                    continue; // Pede novamente
                }
                int value = Integer.parseInt(inputStr.trim());
                if (value >= min && value <= max) {
                    return value; // Retorna o valor válido
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
     * Permite entrada em branco, que retorna −1 para indicar "manter valor atual".
     * Valida se o número inserido (se não estiver em branco) está dentro do intervalo.
     *
     * @param prompt A mensagem a ser exibida ao usuário.
     * @param min O valor mínimo permitido (inclusivo).
     * @param max O valor máximo permitido (inclusivo).
     * @return O inteiro validado inserido, ou −1 se a entrada for deixada em branco.
     */
    public int getOptionalValidatedInt(String prompt, int min, int max) {
        while (true) {
            try {
                String inputStr = getInput(prompt);
                if (inputStr.isBlank()) {
                    return -1; // Sinaliza para não alterar o valor existente
                }
                int value = Integer.parseInt(inputStr.trim());
                if (value >= min && value <= max) {
                    return value; // Retorna o valor válido
                }
                displayMessage(String.format(" Valor fora do intervalo! (%d-%d).", min, max));
            } catch (NumberFormatException e) {
                // Permite deixar em branco, então a mensagem é um pouco diferente
                displayMessage(" Número inválido. Digite um número entre " + min + " e " + max + " ou deixe em branco para manter.");
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
            // Se chegou aqui, a resposta foi inválida ou nula
            displayMessage(" Resposta inválida. Por favor, digite 's' para sim ou 'n' para não.");
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
            String resp = getInput(prompt);
            if (resp.isBlank()) {
                return null; // Sinaliza para não alterar o valor existente
            }
            resp = resp.toLowerCase().trim();
            if (resp.equals("s")) return true;
            if (resp.equals("n")) return false;
            displayMessage(" Resposta inválida! Digite 's', 'n' ou deixe em branco para manter.");
        }
    }

    /**
     * Exibe as informações de avaliação (média, contagem, histórico) de um {@link ReviewInfo}.
     * Formata a saída de forma legível.
     *
     * @param reviewInfo O objeto contendo as informações de avaliação.
     */
    private void displayReviewInfo(ReviewInfo reviewInfo) {
        displayMessage("\n--- Avaliação(ões) ---");
        int count = (reviewInfo != null) ? reviewInfo.getReviewCount() : 0;
        if (count == 0) {
            displayMessage("Nenhuma avaliação registrada.");
        } else {
            // Exibe a média e o total de avaliações
            displayMessage(String.format("Média das Avaliações: %.1f/5", reviewInfo.getAverageRating()));
            displayMessage(String.format("Total de Avaliações: %d", count));

            // Ordena as avaliações pela data (mais recentes primeiro) para exibir o histórico
            List<Review> sortedReviews = new ArrayList<>(reviewInfo.getReviews());
            // Comparator.nullsLast garante que reviews sem data apareçam por último
            sortedReviews.sort(Comparator.comparing(Review::reviewDate, Comparator.nullsLast(Comparator.reverseOrder())));

            displayMessage("Histórico:");
            for (Review r : sortedReviews) {
                String dateStr = formatDate(r.reviewDate()); // Reutiliza o formatador de data
                displayMessage(String.format("  - [%d/5] (%s): \"%s\"", r.rating(), dateStr, r.comment()));
            }
        }
    }

    /**
     * Obtém uma linha de texto da entrada padrão, removendo espaços no início e fim.
     * Sobrescreve o método da superclasse (assumindo que existe).
     *
     * @param prompt A mensagem a ser exibida ao usuário antes de ler a entrada.
     * @return A string lida do usuário, sem espaços nas pontas.
     */
    @Override
    public String getInput(String prompt) {
        System.out.println(prompt); // Exibe a mensagem
        return scanner.nextLine().trim(); // Lê a linha e remove espaços extras
    }

    /**
     * Exibe uma mensagem no console.
     * Sobrescreve o método da superclasse (assumindo que existe).
     *
     * @param message A mensagem a ser exibida.
     */
    @Override
    public void displayMessage(String message) {
        System.out.println(message);
    }

    // --- Métodos Auxiliares para Listagem Avançada (Filtro/Ordenação) ---

    /** Pergunta ao usuário se deseja filtrar a lista de livros por gênero. */
    public boolean askToFilterByGenre() {
        return getYesNoInput("Deseja filtrar por gênero? (s/n)");
    }

    /** Obtém do usuário o termo de gênero para usar no filtro. */
    public String getGenreFilterInput() {
        return getInput("Digite o gênero para filtrar:");
    }

    /** Pergunta ao usuário se deseja filtrar a lista de livros por ano de publicação. */
    public boolean askToFilterByYear() {
        return getYesNoInput("Deseja filtrar por ano de publicação? (s/n)");
    }

    /** Obtém do usuário o ano para usar no filtro (0 para ignorar). */
    public int getYearFilterInput() {
        // Usa getValidatedInt para garantir que seja um número razoável
        return getValidatedInt("Digite o ano de publicação para filtrar (ou 0 para ignorar):", 0, 9999);
    }

    /**
     * Apresenta as opções de ordenação para livros e obtém a escolha do usuário.
     *
     * @return O código da opção de ordenação escolhida (1: Melhor, 2: Pior, 0: Nenhuma).
     */
    public int getSortOptionBook() {
        displayMessage("\nOpções de Ordenação:");
        displayMessage("1 - Melhor Avaliados (Maior média primeiro)");
        displayMessage("2 - Pior Avaliados (Menor média primeiro)");
        displayMessage("0 - Sem ordenação específica (padrão de cadastro)");
        return getValidatedInt("Escolha a opção de ordenação:", 0, 2);
    }
}