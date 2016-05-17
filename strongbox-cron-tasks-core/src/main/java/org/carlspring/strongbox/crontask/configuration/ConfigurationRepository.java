package org.carlspring.strongbox.crontask.configuration;

import com.google.common.collect.Iterables;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.carlspring.strongbox.crontask.db.DbUtils.withDatabase;

/**
 * @author Yougeshwar
 */
@Repository
public class ConfigurationRepository {
    private static final Logger logger = LoggerFactory.getLogger(ConfigurationRepository.class);

    public ConfigurationRepository() {
        init();
    }

    public Configuration getConfiguration()
    {
        return withDatabase(db -> {
            List<Configuration> result = db.query(new OSQLSynchQuery<>("select * from Configuration"));
            Configuration configuration = !result.isEmpty() ? Iterables.getLast(result) : null;

            return db.<Configuration>detachAll(configuration, true);
        });
    }

    public void updateConfiguration(Configuration configuration) {
        withDatabase(db -> {
            db.save(configuration);
        });
    }

    boolean schemaExists() {
        return withDatabase(db -> {
            return db.getMetadata().getSchema().existsClass("Configuration");
        });
    }

    public void init()
    {
        logger.info("ConfigurationRepository.init()");

        withDatabase(db -> {
            db.getEntityManager().registerEntityClass(Configuration.class, true);
        });
    }
}
