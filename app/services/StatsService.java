package services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Singleton
public class StatsService {

    private static final Logger logger = LoggerFactory.getLogger(StatsService.class);

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