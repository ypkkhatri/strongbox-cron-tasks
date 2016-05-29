package org.carlspring.strongbox.crontask.api.jobs;

import java.io.File;
import java.io.IOException;

import groovy.lang.GroovyShell;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author carlspring
 */
public class GroovyCronJob
        extends AbstractCronJob
{

    private static final Logger logger = LoggerFactory.getLogger(GroovyCronJob.class);

    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext)
            throws JobExecutionException
    {
        GroovyShell groovyShell = new GroovyShell();
        try
        {
            groovyShell.parse(new File(getScriptPath())).invokeMethod("execute", null);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            logger.error("IOException: ", e);
        }
    }

    public String getScriptPath()
    {
        return (String) getConfiguration().getProperties().get("script.path");
    }

    public void setScriptPath(String scriptPath)
    {
        getConfiguration().getProperties().put("script.path", scriptPath);
    }

}
