package org.carlspring.strongbox.crontask.api.jobs;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * @author carlspring
 */
public class GroovyCronJob extends AbstractCronJob
{

    private String scriptPath;


    @Override
    protected void executeInternal(JobExecutionContext jobExecutionContext)
            throws JobExecutionException
    {
        // TODO: Invoke the Groovy script
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
