package helpers;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * helper for dates in any format
 */
public class DateHelper
{
    public static final String format = "yyyy-MM-dd HH:mm:ss"; // timestamp format

    /**
     * parse timestamp string to milliseconds
     * @param dateStr timestamp string
     * @return milliseconds
     */
    public static long StrToMillis(String dateStr)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try
        {
            Date date = sdf.parse(dateStr);
            return date.getTime();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
        return 0;
    }

    /**
     * make timestamp string from milliseconds
     * @param millis milliseconds
     * @return timestamp string
     */
    public static String MillisToStr(long millis)
    {
        Date date = new Date(millis);
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }
}
