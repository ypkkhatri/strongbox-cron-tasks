package org.carlspring.strongbox.crontask.config;

import com.orientechnologies.orient.object.db.OObjectDatabaseTx;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;

@Configuration
@ComponentScan({ "org.carlspring.strongbox.crontask" })
@PropertySource(value = { "classpath:application.properties" })
public class DatabaseConfig
{

    @Autowired
    private Environment environment;

    @Bean
    public OObjectDatabaseTx objectDatabaseTx()
    {
        String location = environment.getRequiredProperty("db.location");
        String dbname = environment.getRequiredProperty("db.name");
        String username = environment.getRequiredProperty("db.username");
        String password = environment.getRequiredProperty("db.password");

        OObjectDatabaseTx db = new OObjectDatabaseTx(location + ":" + dbname);
        if (db.exists())
        {
            db = db.open(username, password);
        }
        else
        {
            db.create();
        }

        return db;
    }
}
