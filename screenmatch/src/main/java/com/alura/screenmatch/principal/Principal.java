package com.alura.screenmatch.principal;

import com.alura.screenmatch.model.DadosEpisodio;
import com.alura.screenmatch.model.DadosSerie;
import com.alura.screenmatch.model.DadosTemporada;
import com.alura.screenmatch.service.ConsumoAPI;
import com.alura.screenmatch.service.ConverteDados;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

public class Principal {

    private Scanner teclado = new Scanner(System.in);
    private ConsumoAPI consumoAPI = new ConsumoAPI();
    private ConverteDados converteDados = new ConverteDados();

    private final String ENDERECO = "https://www.omdbapi.com/?t=";
    private final String API_KEY = "&apikey=6585022c";

    public void exibeMenu() {


        System.out.println("Digite o nome da serie que está buscando:");
        var nomeSerie = teclado.nextLine();
        var json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + API_KEY);
        DadosSerie dados = converteDados.obterDados(json, DadosSerie.class);
        System.out.println(dados);

        List<DadosTemporada> temporadas = new ArrayList<>();
        for (int i = 1; i <= dados.totaltemporada(); i++) {
            json = consumoAPI.obterDados(ENDERECO + nomeSerie.replace(" ", "+") + "&season=" + i + API_KEY);
            DadosTemporada temporada = converteDados.obterDados(json, DadosTemporada.class);
            temporadas.add(temporada);

        }
        temporadas.forEach(System.out::println);

//        for (int i=0;i<dados.totaltemporada();i++){
//            List<DadosEpisodio> episodiosTemporada = temporadas.get(i).episodios();
//            for (int j=0;j<episodiosTemporada.size();j++){
//                System.out.println(episodiosTemporada.get(j).titulo());
//            }
//        }
        //codigo abaixo representa o mesmo de cima de forma resumida
        temporadas.forEach(t -> t.episodios().forEach(e -> System.out.println(e.titulo())));{}

        List<DadosEpisodio> dadosEpisodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream())
                .collect(Collectors.toList());

        System.out.println("\nTop 5 episodios mais bem avaliados:");
        dadosEpisodios.stream()
                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
                        .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
                        .limit(5)
                        .forEach(System.out::println);
    }
}