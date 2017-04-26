package verwaltung.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Validierung
{
  /**
   * Prüft ob ein String eine valide Nummer ist
   * @param s der zu prüfende String
   * @return true, wenn der String eine valide Nummer ist
   */
  public static boolean isValidNumber( String s )
  {
    String text = s;
    String regex = "([0-9])+(\\.([0-9])+){0,1}";
    Pattern p = Pattern.compile( regex );
    Matcher m = p.matcher( text );
    return m.matches();
  }
}
