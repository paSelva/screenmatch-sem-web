package com.alura.screenmatch.principal;

import com.alura.screenmatch.model.DadosEpisodio;
import com.alura.screenmatch.model.DadosSerie;
import com.alura.screenmatch.model.DadosTemporada;
import com.alura.screenmatch.model.Episodio;
import com.alura.screenmatch.service.ConsumoAPI;
import com.alura.screenmatch.service.ConverteDados;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

//        System.out.println("\nTop 10 episodios mais bem avaliados:");
//        dadosEpisodios.stream()
//                .filter(e -> !e.avaliacao().equalsIgnoreCase("N/A"))
//                .peek(e -> System.out.println("Primeiro filtro(N/A) - " + e))
//                        .sorted(Comparator.comparing(DadosEpisodio::avaliacao).reversed())
//                .peek(e -> System.out.println("Ordenação - " + e))
//                        .limit(10)
//                .peek(e -> System.out.println("Limite - " + e))
//                        .map(e -> e.titulo().toUpperCase() + " - " + e.avaliacao())
//                .peek(e -> System.out.println("Mapeamento - " + e))
//                        .forEach(System.out::println);

        List<Episodio> episodios = temporadas.stream()
                .flatMap(t -> t.episodios().stream()
                        .map(d -> new Episodio(t.numero(), d))
                ).collect(Collectors.toList());

        episodios.forEach(System.out::println);


//        System.out.println("\nQual episodio deseja ver?");
//        var trechoTitulo = teclado.nextLine();
//        Optional<Episodio> episodioBuscado = episodios.stream()
//                .filter(e -> e.getTitulo().toUpperCase().contains(trechoTitulo.toUpperCase()))
//                .findFirst();
//        if (episodioBuscado.isPresent()) {
//            System.out.println("Episodio encontrado! ");
//            System.out.println("Temporada: " + episodioBuscado.get().getTemporada());
//        }else {
//            System.out.println("Episodio nao encontrado");
//        }

//        System.out.println("A partir de que ano voce quer ver os episodios?");
//        int ano = teclado.nextInt();
//        teclado.nextLine();
//
//        //formatador de datas
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//
//        LocalDate dataBusca = LocalDate.of(ano, 1, 1);
//
//        episodios.stream()
//                .filter(e -> e.getDataLancamento() !=null && e.getDataLancamento().isAfter(dataBusca))
//                .forEach(e -> System.out.println(
//                        "Temporada: " + e.getTemporada() + " - " + " Episodio: " + e.getTitulo() + " - " + "Data Lançamento: " + e.getDataLancamento().format(formatter)
//                ));

        Map<Integer, Double> avaliacoesPorTemporada = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.groupingBy(Episodio::getTemporada, Collectors.averagingDouble(Episodio::getAvaliacao)));
        System.out.println("Avaliacoes por Temporadas: " + avaliacoesPorTemporada);

        DoubleSummaryStatistics stats = episodios.stream()
                .filter(e -> e.getAvaliacao() > 0.0)
                .collect(Collectors.summarizingDouble(Episodio::getAvaliacao));
        System.out.println("media: " + stats.getAverage());
        System.out.println("min: " + stats.getMin());
        System.out.println("max: " + stats.getMax());
        System.out.println("Quantidade: " + stats.getCount());
    }

}