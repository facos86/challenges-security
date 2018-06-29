package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import services.StatsService;

public class Statistics extends Controller {

    private final StatsService service;

    public Statistics(StatsService service) {
        this.service = service;
    }

    /**
     * @return 200 (ok)
     */
    public Result getStatistics(String statType) {
        Collection<Stats> cards = service.retrieveStats(statType);
        List<CardJson> jStats = cards.stream()
                .map(StatsTransformer)
                .collect(Collectors.toList());
        return ok(Json.toJson(new StatsJson(jStats)));
    }
}