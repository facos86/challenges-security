package services;

import com.google.common.collect.Sets;
import models.CardStatus;
import models.CardType;
import models.cards.MetricCard;
import models.requesters.CardRequester;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Singleton
public class StatsService {

    public Optional<Stats> retrieveStats(String type) {
        Class<? extends Stats> statsType = Stats.getRealClass();
        String theQuery = "SELECT DISTINCT v FROM Stats s" +
                        " WHERE s.date > NOW() " +
                        " AND s.type = " + type,
            statsType.getSimpleName());
        try {
            List<? extends Stats> result = this.jpa.em().createQuery(theQuery, statsType)
                     .getResultList();
            return result;
        } catch (Exception e) {
            logger.error("StatsService", e);
            return Collections.emptyList();
        }
    }
}