package org.carlspring.strongbox.crontask.utils;

import org.carlspring.strongbox.crontask.exceptions.CronTaskException;

import java.io.*;

/**
 * @author Yougeshwar
 */
public class FileUtils
{
    public static void writeToFile(InputStream is,
                             String dirPath, String fileName)
            throws CronTaskException
    {
        File dir = new File(dirPath);

        if(!dir.exists())
            dir.mkdirs();

        File file = new File(dirPath + "/" + fileName);

        try (OutputStream out = new FileOutputStream(file))
        {
            int read = 0;
            byte[] bytes = new byte[1024];

            while ((read = is.read(bytes)) != -1)
            {
                out.write(bytes, 0, read);
            }
            out.flush();
        }
        catch (IOException e)
        {
            throw new CronTaskException(e);
        }
    }

    public static void deleteFile(String url) {
        File file = new File(url);
        if(file.exists())
            file.delete();
    }
}
