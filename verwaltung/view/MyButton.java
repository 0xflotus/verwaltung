package verwaltung.view;

import java.awt.Dimension;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * GUI-Klasse für angepasste Button
 * @author fthurm
 *
 */
public class MyButton extends JButton
{

  public MyButton( String text, Icon icon )
  {
    this( text );
    this.setIcon( icon );
  }

  public MyButton( String text )
  {
    super( text );
    this.setPreferredSize( new Dimension( 100, 25 ) );
    this.setMargin( new Insets( 0, 0, 0, 0 ) );
  }

}
