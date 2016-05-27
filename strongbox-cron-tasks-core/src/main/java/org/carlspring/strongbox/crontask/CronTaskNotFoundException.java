package org.carlspring.strongbox.crontask;

/**
 * @author Yougeshwar
 */
public class CronTaskNotFoundException
        extends Exception
{

    public CronTaskNotFoundException()
    {
    }

    public CronTaskNotFoundException(String msg)
    {
        super(msg);
    }

    public CronTaskNotFoundException(String msg,
                                     Throwable cause)
    {
        super(msg, cause);
    }

    public CronTaskNotFoundException(Throwable cause)
    {
        super(cause);
    }

}
