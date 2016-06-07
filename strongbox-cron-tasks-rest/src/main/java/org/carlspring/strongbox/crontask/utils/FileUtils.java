package org.carlspring.strongbox.crontask.utils;

import org.carlspring.strongbox.crontask.exceptions.CronTaskException;

import java.io.*;

/**
 * @author Yougeshwar
 */
public class FileUtils
{
    public static void writeToFile(InputStream uploadedInputStream,
                             String uploadedFileLocation)
            throws CronTaskException
    {
        File path = new File(uploadedFileLocation);

        if(!path.exists())
            path.mkdirs();

        try (OutputStream out = new FileOutputStream(path))
        {
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = uploadedInputStream.read(bytes)) != -1)
            {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        }
        catch (IOException e)
        {
            throw new CronTaskException(e);
        }
    }
}
