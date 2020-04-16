package software.fitz.easyagent.api.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    private static DateTimeFormatter FORMAT_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public static String formatYearToMillis(ZonedDateTime zonedDateTime) {
        return FORMAT_DATETIME.format(zonedDateTime);
    }

    public static ZonedDateTime currentDateTime() {
        return ZonedDateTime.now();
    }

    public static String currentDateTimeString() {
        return FORMAT_DATETIME.format(ZonedDateTime.now());
    }

    public static String currentDateTimeString(ZoneId timezone) {
        return FORMAT_DATETIME.format(ZonedDateTime.now(timezone));
    }
}
