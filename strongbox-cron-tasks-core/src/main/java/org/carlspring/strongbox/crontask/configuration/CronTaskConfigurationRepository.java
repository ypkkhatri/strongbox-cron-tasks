package org.carlspring.strongbox.crontask.configuration;

import com.google.common.collect.Iterables;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.carlspring.strongbox.crontask.db.DbUtils.withDatabase;

/**
 * @author Yougeshwar
 */
@Repository
public class CronTaskConfigurationRepository
{
    private static final Logger logger =
            LoggerFactory.getLogger(CronTaskConfigurationRepository.class);

    public CronTaskConfigurationRepository()
    {
        init();
    }

    public List<CronTaskConfiguration> getConfigurations()
    {
        logger.info("CronTaskConfigurationRepository.getConfigurations()");

        return withDatabase(db -> {
            List<CronTaskConfiguration> result =
                    db.query(new OSQLSynchQuery<>("select * from CronTaskConfiguration"));

            return result;
        });
    }

    public CronTaskConfiguration getConfiguration(String name)
    {
        logger.info("CronTaskConfigurationRepository.getConfiguration()");

        return withDatabase(db -> {
            List<CronTaskConfiguration> result =
                    db.query(new OSQLSynchQuery<>("select * from CronTaskConfiguration where name = %s"), name);
            CronTaskConfiguration cronTaskConfiguration =
                    !result.isEmpty() ? Iterables.getLast(result) : null;

            return db.<CronTaskConfiguration>detachAll(cronTaskConfiguration, true);
        });
    }

    public void updateConfiguration(CronTaskConfiguration cronTaskConfiguration)
    {
        logger.info("CronTaskConfigurationRepository.updateConfiguration()");

        withDatabase(db -> {
            db.save(cronTaskConfiguration);
            return true;
        });
    }

    public void deleteConfiguration(CronTaskConfiguration cronTaskConfiguration)
    {
        logger.info("CronTaskConfigurationRepository.deleteConfiguration()");

        withDatabase(db -> {
            return db.delete(cronTaskConfiguration);
        });
    }

    boolean schemaExists()
    {
        logger.info("CronTaskConfigurationRepository.schemaExists()");

        return withDatabase(db -> {
            return db.getMetadata().getSchema().existsClass("CronTaskConfiguration");
        });
    }

    public void init()
    {
        logger.info("CronTaskConfigurationRepository.init()");

        withDatabase(db -> {
            db.getEntityManager().registerEntityClass(CronTaskConfiguration.class, true);
            return true;
        });
    }
}
