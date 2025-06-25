package org.diariocultural;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SeriesController {

    private List<Series> seriesList;
    private static final String DATA_DIRECTORY = "data";
    private static final String FILE_NAME = "series.json";
    private static final String FILE_PATH = DATA_DIRECTORY + File.separator + FILE_NAME;

    public SeriesController() {
        loadData();
        if (this.seriesList == null) {
            this.seriesList = new ArrayList<>();
        }
        Series.updateNextIdBasedOnLoadedData(this.seriesList);
    }

    // --- MÉTODOS PARA A GUI ---

    public void addSeriesViaObject(Series series) {
        if (series != null) {
            this.seriesList.add(series);
            saveData();
        }
    }

    public void removeSeries(Series seriesToRemove) {
        if (seriesToRemove != null) {
            seriesList.remove(seriesToRemove);
            saveData();
        }
    }

    public void updateSeries(Series updatedSeries) {
        for (int i = 0; i < seriesList.size(); i++) {
            if (seriesList.get(i).getSeriesId() == updatedSeries.getSeriesId()) {
                seriesList.set(i, updatedSeries);
                saveData();
                return;
            }
        }
    }

    public List<Series> getAllSeries() {
        return new ArrayList<>(this.seriesList);
    }

    public List<Series> searchSeries(String criteria) {
        if (criteria == null || criteria.isBlank()) {
            return getAllSeries();
        }
        String lowerCriteria = criteria.toLowerCase().trim();
        return seriesList.stream()
                .filter(series -> series.getTitle().toLowerCase().contains(lowerCriteria) ||
                        String.valueOf(series.getReleaseYear()).contains(lowerCriteria) ||
                        series.getGenre().stream().anyMatch(g -> g.toLowerCase().contains(lowerCriteria)) ||
                        series.getCast().stream().anyMatch(a -> a.toLowerCase().contains(lowerCriteria)))
                .collect(Collectors.toList());
    }

    // --- Métodos de Persistência JSON ---

    private void saveData() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT); // JSON formatado
        // objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // Se tiver Datas

        try {
            File dataDir = new File(DATA_DIRECTORY);
            if (!dataDir.exists()) {
                if (!dataDir.mkdirs()) {
                    System.err.println(" Falha ao criar diretório de dados: " + dataDir.getAbsolutePath());
                    return;
                }
            }
            objectMapper.writeValue(new File(FILE_PATH), seriesList);
            // System.out.println("Dados de séries salvos em " + FILE_PATH);
        } catch (IOException e) {
            System.err.println(" Erro ao salvar dados de séries: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void loadData() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Ignora campos extras
        // objectMapper.setDateFormat(new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")); // Se usar datas

        File file = new File(FILE_PATH);
        if (file.exists() && file.isFile() && file.length() > 0) {
            try {
                this.seriesList = objectMapper.readValue(file, new TypeReference<List<Series>>() {});
                System.out.println(" Dados de séries carregados de " + FILE_PATH);
            } catch (IOException e) {
                System.err.println(" Erro ao carregar dados de séries: " + e.getMessage());
                e.printStackTrace();
                this.seriesList = new ArrayList<>(); // Começa vazio se houver erro
            }
        } else {
            if (!file.exists()) System.out.println("Arquivo " + FILE_PATH + " não encontrado. Será criado ao salvar.");
            else System.out.println("Arquivo " + FILE_PATH + " vazio ou é um diretório. Iniciando com lista vazia.");
            this.seriesList = new ArrayList<>(); // Lista vazia se arquivo não existe/vazio
        }
    }
}