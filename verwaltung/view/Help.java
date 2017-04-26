package verwaltung.view;

import java.awt.BorderLayout;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;

/**
 * GUI-Klasse f¸r die Help-Infobox
 * @author fthurm
 *
 */
public class Help
{
  private JDialog   dialog;
  private JFrame    owner;
  private JTextArea ta;
  private boolean   hinweis;
  private String[]  erklaerungen;

  public Help( Verwaltung owner )
  {
    this.owner = owner;
    initComponents();
    showDialog();
  }

  private void initComponents()
  {
    dialog = new JDialog( owner, "Hilfe" );
    dialog.setSize( 300, 200 );
    dialog.setLocationRelativeTo( owner );
    dialog.setModal( true );
    dialog.setLayout( new BorderLayout() );
    ta = new JTextArea();
    ta.setEditable( false );
    ta.setLineWrap( true );
    String[] erklaerungen = {
        "r -> l‰dt die Seite neu",
        "Pfeil nach links springt 10 Zeilen weiter",
        "Pfeil nach rechts springt 10 Zeilen zur¸ck oder zum Anfang",
        "Pfeil nach unten/oben springt jeweils eine Zeile vor/zur¸ck",
        "q -> schlieﬂt die Infobox", };
    for ( String s : erklaerungen )
      ta.append( s + "\n" );
    dialog.add( ta );
  }

  private void showDialog()
  {
    dialog.setVisible( true );
  }

}
