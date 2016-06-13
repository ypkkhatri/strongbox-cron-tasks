package org.carlspring.strongbox.crontask.configuration;

import com.orientechnologies.orient.core.sql.OCommandSQL;
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

    private static final Logger logger = LoggerFactory.getLogger(CronTaskConfigurationRepository.class);

    public CronTaskConfigurationRepository()
    {
        init();
    }

    public List<CronTaskConfiguration> getConfigurations()
    {
        logger.debug("CronTaskConfigurationRepository.getConfigurations()");

        return withDatabase(db -> {
            List<CronTaskConfiguration> result =
                    db.query(new OSQLSynchQuery<>("SELECT * FROM CronTaskConfiguration"));

            return result;
        });
    }

    public CronTaskConfiguration getConfiguration(String name)
    {
        logger.debug("CronTaskConfigurationRepository.getConfiguration()");

        return withDatabase(db -> {
            List<CronTaskConfiguration> result =
                    db.query(new OSQLSynchQuery<CronTaskConfiguration>("SELECT * FROM CronTaskConfiguration" +
                                                                       " WHERE name = '" + name + "'"));
            CronTaskConfiguration cronTaskConfiguration = !result.isEmpty() ? result.get(0) : null;

            return db.<CronTaskConfiguration>detachAll(cronTaskConfiguration, true);
        });
    }

    public void saveConfiguration(CronTaskConfiguration cronTaskConfiguration)
    {
        logger.debug("CronTaskConfigurationRepository.saveConfiguration()");

        withDatabase(db -> {
            db.save(cronTaskConfiguration);
            return true;
        });
    }

    public void deleteConfiguration(CronTaskConfiguration cronTaskConfiguration)
    {
        logger.debug("CronTaskConfigurationRepository.deleteConfiguration()");

        withDatabase(db -> {
            return db.delete(cronTaskConfiguration);
        });
    }

    public void deleteConfiguration(Object id)
    {
        logger.debug("CronTaskConfigurationRepository.deleteConfiguration()");

        withDatabase(db -> {
            return db.command(new OCommandSQL("DELETE FROM CronTaskConfiguration WHERE id = '" + id + "'")).execute();
        });
    }

    boolean schemaExists()
    {
        logger.debug("CronTaskConfigurationRepository.schemaExists()");

        return withDatabase(db -> {
            return db.getMetadata().getSchema().existsClass("CronTaskConfiguration");
        });
    }

    public void init()
    {
        logger.debug("CronTaskConfigurationRepository.init()");

        withDatabase(db -> {
            db.getEntityManager().registerEntityClass(CronTaskConfiguration.class, true);
            return true;
        });
    }

}
