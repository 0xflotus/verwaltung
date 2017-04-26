package verwaltung.util;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

public class WinUtil
{

  public enum MenuItemType
  {
    ITEM_PLAIN, ITEM_CHECK, ITEM_RADIO
  }

  private WinUtil()
  {
    // privater parameterloser Konstruktor
    // Wir verhindern, dass ein Objekt dieser Klasse gebildet werden kann
  }

  /**
   * <li><b><i>createMenu</i></b>
   * <br>
   * <br>
   * public JMenu createMenu(
   * JMenuBar menubar, String text, String name, int shortKey)
   * <br>
   * <br>
   * Erstellt einen Men�.
   * <br>
   * <br>
   * 
   * @param menubar
   *          - Die Men�leiste, zu dem dieses Men� geh�rt.
   * @param text
   *          - Der Text des Men�s.
   * @param name
   *          - Optionaler Name des Men�s oder <b>null</b>.
   * @param shortKey
   *          - Optionales Tastaturk�rzel oder <b>0</b>.
   * @return
   *         Men�.
   */
  public static JMenu createMenu( JMenuBar menubar, String text, String name, int shortKey, Icon icon )
  {

    JMenu menu = new JMenu();

    // Beschriftung / Text
    menu.setText( text );

    // Name f�r die Logik
    menu.setName( name );

    if ( icon != null )
      menu.setIcon( icon );

    // optionales Tastenk�rzel
    if ( shortKey > 0 )
      menu.setMnemonic( shortKey );

    menubar.add( menu );

    return menu;

  }

  /**
   * <li><b><i>createSubMenu</i></b>
   * <br>
   * <br>
   * public JMenu createSubMenu(
   * JMenu mainMenu, String menuText, String menuName, int shortKey)
   * <br>
   * <br>
   * Erstellt ein Untermen�.
   * <br>
   * <br>
   * 
   * @param menu
   *          - Das Men�, zu dem das Untermen� hinzugef�gt werden soll.
   * @param text
   *          - Der Text des Men�s.
   * @param name
   *          - Optionaler Name des Untermen�s oder <b>null</b>.
   * @param shortKey
   *          - Optionales Tastaturk�rzel oder <b>0</b>.
   * @return
   *         - Untermen�.
   */
  public static JMenu createSubMenu(
      JMenu menu, String text, String name, int shortKey, Icon icon )
  {
    JMenu submenu = new JMenu();

    submenu.setText( text );
    submenu.setName( name );

    if ( icon != null )
      submenu.setIcon( icon );

    if ( shortKey > 0 )
      submenu.setMnemonic( shortKey );

    menu.add( submenu );

    return submenu;

  }

  /**
   * <li><b><i>createMenuItem</i></b>
   * <br>
   * <br>
   * public JMenuItem createMenuItem(
   * JMenu menu, String miName, MenuItemType miType,
   * ActionListener actionListener,<br>
   * &nbsp String sText,
   * String imageFile, int shortKey, String sToolTip)
   * <br>
   * <br>
   * Erstellt einen Men�eintrag.
   * <br>
   * <br>
   * 
   * @param menu
   *          - Das Men�, zu dem dieser Men�eintrag geh�rt.
   * @param miText
   *          - Der Text des Men�eintrags.
   * @param miName
   *          - Optionaler Name des Men�eintrags oder <b>null</b>.
   * @param miType
   *          - Der Typ des Men�eintrags <b>MenuItemType</b>.
   * @param actionListener
   *          - Der ActionListener, der verwendet werden soll,
   *          wenn der Men�eintrag ausgew�hlt wurde oder <b>null</b>.
   * @param image
   *          - Symbol, welches links vor dem Text angezeigt werden soll oder
   *          <b>null</b>.
   * @param shortKey
   *          - Optionales Tastaturk�rzel oder <b>0</b>.
   * @param miTooltip
   *          - Optionaler Text f�r den Tooltip oder <b>null</b>.
   * @return
   *         Men�eintrag.
   */
  public static JMenuItem createMenuItem(
      JMenu menu, String miText, String miName,
      MenuItemType miType, ActionListener actionListener,
      Icon image, int shortKey, String miTooltip )
  {

    JMenuItem menuItem = new JMenuItem();

    switch ( miType )
    {
      case ITEM_CHECK:
        menuItem = new JCheckBoxMenuItem();
        break;

      case ITEM_RADIO:
        menuItem = new JRadioButtonMenuItem();
        break;

      case ITEM_PLAIN:
        break;
    }

    // Men� Text hinzuf�gen
    menuItem.setText( miText );

    // Name des Men�eintrags (optional)
    menuItem.setName( miName );

    // Optionales Image hinzuf�gen
    menuItem.setIcon( image );

    // Optionales Tastaturk�rzel hinzuf�gen
    if ( shortKey > 0 )
      menuItem.setMnemonic( shortKey );

    // Optionalen Tooltip hinzuf�gen
    menuItem.setToolTipText( miTooltip );

    // ActionListener hinzuf�gen
    menuItem.addActionListener( actionListener );

    // Men�eintrag zum Men� hinzuf�gen
    menu.add( menuItem );

    // R�ckgabe des Men�eintrags
    return menuItem;
  }

  /**
   * 
   * @param menu
   * @param miText
   * @param miName
   * @param miType
   * @param actionListener
   * @param image
   * @param shortKey
   * @param keyStroke
   * @param miTooltip
   * @return
   */
  public static JMenuItem createMenuItem(
      JMenu menu, String miText, String miName,
      MenuItemType miType, ActionListener actionListener,
      Icon image, int shortKey, int virtualKey, String miTooltip )
  {
    JMenuItem menuItem = createMenuItem( menu, miText, miName,
        miType, actionListener, image, shortKey, miTooltip );
    menuItem.setAccelerator( KeyStroke.getKeyStroke( virtualKey,
        ActionEvent.CTRL_MASK ) );

    return menuItem;
  }
}
