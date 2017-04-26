package verwaltung.util;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Zeitraum
{
  /**
   * Gibt die SQL-Anweisung für einen ganzen Monat zurück
   * @param date
   * @return
   */
  public static String getSQLForMonthPeriod( Date date )
  {
    GregorianCalendar gc = new GregorianCalendar();
    gc.setTime( date );
    int maxDay = gc.getActualMaximum( Calendar.DAY_OF_MONTH );
    int month = gc.get( Calendar.MONTH );
    int year = gc.get( Calendar.YEAR );
    GregorianCalendar gcmax = new GregorianCalendar( year, month, maxDay, 23, 59, 59 );
    long lmax = gcmax.getTimeInMillis();
    GregorianCalendar gcmin = new GregorianCalendar( year, month, gc.getActualMinimum( Calendar.DAY_OF_MONTH ) );
    long lmin = gcmin.getTimeInMillis();
    return "SELECT rowid,* FROM verwaltung WHERE Datum < "
        + lmax + " AND Datum > " + lmin + " ORDER BY Datum";
  }

  /**
   * Gibt die SQL-Anweisung für ein ganzes Jahr zurück.
   * @param date
   * @return
   */
  public static String getSQLForYearPeriod( Date date )
  {
    GregorianCalendar gc = new GregorianCalendar();
    gc.setTime( date );
    int year = gc.get( Calendar.YEAR );
    GregorianCalendar gcmax = new GregorianCalendar( year, 11, 31, 23, 59, 59 );
    long lmax = gcmax.getTimeInMillis();
    GregorianCalendar gcmin = new GregorianCalendar( year, 0, 1, 0, 0, 0 );
    long lmin = gcmin.getTimeInMillis();
    return "SELECT rowid,* FROM verwaltung WHERE Datum < "
        + lmax + " AND Datum > " + lmin + " ORDER BY Datum";
  }

}
