package software.fitz.easyagent.api.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static DateTimeFormatter FORMAT_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static String currentDateTime() {
        return FORMAT_DATETIME.format(ZonedDateTime.now());
    }

    public static String currentDateTime(ZoneId timezone) {
        return FORMAT_DATETIME.format(ZonedDateTime.now(timezone));
    }
}
