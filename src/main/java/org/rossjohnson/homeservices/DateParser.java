package org.rossjohnson.homeservices;

import org.apache.commons.logging.LogFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateParser {

    public Date parseDate(String date) {
        try {
            return date.equalsIgnoreCase("today") ? getToday() :
                    new SimpleDateFormat("MM-dd-yyyy").parse(date);
        } catch (ParseException e) {
            LogFactory.getLog(DateParser.class).error("Can't parse date " + date);
        }
        return null;
    }

    public Date getToday() {
        Date startDate;Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        startDate = cal.getTime();
        return startDate;
    }

    public Date getDayAfter(Date startDate) {
        Calendar c = Calendar.getInstance();
        c.setTime(startDate);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }
}
