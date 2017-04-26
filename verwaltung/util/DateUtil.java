package verwaltung.util;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtil
{
  /**
   * Konvertiert ein LocalDate-Objekt(Java1.8) in ein java.util.Date-Objekt (vor Java1.8)
   * @param localDate
   * @return
   */
  public static Date localDateToUtilDate( LocalDate localDate )
  {
    return Date.from( localDate.atStartOfDay().atZone( ZoneId.systemDefault() ).toInstant() );
  }

  /**
   * Gibt den long-Wert eines Datums zurück
   * @param day Tag
   * @param month Monat
   * @param year Jahr
   * @return long-Representation des Datums
   */
  public static long getTimeStamp( int day, int month, int year )
  {
    Date d = new Date();
    GregorianCalendar cal = new GregorianCalendar();
    cal.setTime( d );
    GregorianCalendar gc = new GregorianCalendar( year, month, day, cal.get( Calendar.HOUR_OF_DAY ),
        cal.get( Calendar.MINUTE ), cal.get( Calendar.SECOND ) );
    gc.set( Calendar.MILLISECOND, cal.get( Calendar.MILLISECOND ) );
    return gc.getTimeInMillis();
  }

  /**
   * Gibt den Namen eines Monats zurück.
   * @param month Monat (0=Januar)
   * @return den deutschen Monatsnamen
   */
  public static String getMonthName( int month )
  {
    switch ( month )
    {
      case 0:
        return "Januar";
      case 1:
        return "Februar";
      case 2:
        return "März";
      case 3:
        return "April";
      case 4:
        return "Mai";
      case 5:
        return "Juni";
      case 6:
        return "Juli";
      case 7:
        return "August";
      case 8:
        return "September";
      case 9:
        return "Oktober";
      case 10:
        return "November";
      case 11:
        return "Dezember";
      default:
        return "";
    }
  }
}
