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
   * Erstellt einen Menü.
   * <br>
   * <br>
   * 
   * @param menubar
   *          - Die Menüleiste, zu dem dieses Menü gehört.
   * @param text
   *          - Der Text des Menüs.
   * @param name
   *          - Optionaler Name des Menüs oder <b>null</b>.
   * @param shortKey
   *          - Optionales Tastaturkürzel oder <b>0</b>.
   * @return
   *         Menü.
   */
  public static JMenu createMenu( JMenuBar menubar, String text, String name, int shortKey, Icon icon )
  {

    JMenu menu = new JMenu();

    // Beschriftung / Text
    menu.setText( text );

    // Name für die Logik
    menu.setName( name );

    if ( icon != null )
      menu.setIcon( icon );

    // optionales Tastenkürzel
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
   * Erstellt ein Untermenü.
   * <br>
   * <br>
   * 
   * @param menu
   *          - Das Menü, zu dem das Untermenü hinzugefügt werden soll.
   * @param text
   *          - Der Text des Menüs.
   * @param name
   *          - Optionaler Name des Untermenüs oder <b>null</b>.
   * @param shortKey
   *          - Optionales Tastaturkürzel oder <b>0</b>.
   * @return
   *         - Untermenü.
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
   * Erstellt einen Menüeintrag.
   * <br>
   * <br>
   * 
   * @param menu
   *          - Das Menü, zu dem dieser Menüeintrag gehört.
   * @param miText
   *          - Der Text des Menüeintrags.
   * @param miName
   *          - Optionaler Name des Menüeintrags oder <b>null</b>.
   * @param miType
   *          - Der Typ des Menüeintrags <b>MenuItemType</b>.
   * @param actionListener
   *          - Der ActionListener, der verwendet werden soll,
   *          wenn der Menüeintrag ausgewählt wurde oder <b>null</b>.
   * @param image
   *          - Symbol, welches links vor dem Text angezeigt werden soll oder
   *          <b>null</b>.
   * @param shortKey
   *          - Optionales Tastaturkürzel oder <b>0</b>.
   * @param miTooltip
   *          - Optionaler Text für den Tooltip oder <b>null</b>.
   * @return
   *         Menüeintrag.
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

    // Menü Text hinzufügen
    menuItem.setText( miText );

    // Name des Menüeintrags (optional)
    menuItem.setName( miName );

    // Optionales Image hinzufügen
    menuItem.setIcon( image );

    // Optionales Tastaturkürzel hinzufügen
    if ( shortKey > 0 )
      menuItem.setMnemonic( shortKey );

    // Optionalen Tooltip hinzufügen
    menuItem.setToolTipText( miTooltip );

    // ActionListener hinzufügen
    menuItem.addActionListener( actionListener );

    // Menüeintrag zum Menü hinzufügen
    menu.add( menuItem );

    // Rückgabe des Menüeintrags
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
