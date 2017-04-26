package verwaltung.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;

import verwaltung.config.VerwaltungConfig;

/**
 * GUI-Klasse für die Infobox weitere Informationen
 * @author fthurm
 *
 */
public class MyTextBox
{
  private Verwaltung verw;
  private JDialog    dialog;

  public MyTextBox( Verwaltung verw )
  {
    this.verw = verw;
    dialog = new JDialog();
    dialog.setTitle( "Nähere Informationen" );
    dialog.setSize( new Dimension( 250, 250 ) );
    dialog.setLocationRelativeTo( verw );
    dialog.setResizable( false );
    dialog.setLayout( new BorderLayout() );
    JScrollPane jsp = new JScrollPane();
    jsp.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
    JTextArea ta = new JTextArea();
    ta.setEditable( false );
    ta.setLineWrap( true );
    ta.setWrapStyleWord( true );
    ta.setText( "Am " + getDatum()
        + " Uhr hast du folgenden Betrag für \""
        + getText()
        + ( getBetrag() < 0 ? "\" ausgegeben: " : "\" bekommen: " )
        + "\n" + String.format( "%.2f",
            ( getBetrag() < 0 ? getBetrag() * -1 : getBetrag() ) )
        + " €" );
    if ( getBetrag() < 0 ) ta.setForeground( Color.RED );
    ta.getActionMap().put( "quit", quit() );
    ta.getInputMap().put( KeyStroke.getKeyStroke( "Q" ), "quit" );
    jsp.setViewportView( ta );
    dialog.add( jsp, BorderLayout.CENTER );
    dialog.setVisible( true );
  }

  public JDialog getDialog()
  {
    return dialog;
  }

  private Action quit()
  {
    return new AbstractAction()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        MyTextBox.this.getDialog().dispose();
      }
    };
  }

  private String getValue( int column )
  {
    int row = verw.getTable().getSelectedRow();
    Object result = new Object();
    if ( row > -1 )
    {
      try
      {
        Statement stmt = verw.getConnection().createStatement();
        String sql = "SELECT rowid, * FROM verwaltung WHERE rowid = "
            + verw.getTable().getModel().getValueAt( row, 0 );
        ResultSet rs = stmt.executeQuery( sql );

        while ( rs.next() )
        {
          result = rs.getObject( column );
        }
        rs.close();
        stmt.close();
      }
      catch ( SQLException e )
      {
        if ( VerwaltungConfig.DEBUG )
          System.out.println( e.getMessage() );
      }
    }
    return result.toString();
  }

  private String getText()
  {
    return getValue( 5 );
  }

  private double getBetrag()
  {
    return Double.parseDouble( getValue( 3 ) );
  }

  private String getDatum()
  {
    long timestamp = Long.parseLong( getValue( 2 ) );
    Date date = new Date( timestamp );
    GregorianCalendar gc = new GregorianCalendar();
    gc.setTime( date );
    return String.format( "%02d.%02d.%04d um %02d:%02d",
        gc.get( Calendar.DATE ), gc.get( Calendar.MONTH ) + 1,
        gc.get( Calendar.YEAR ), gc.get( Calendar.HOUR ),
        gc.get( Calendar.MINUTE ) );
  }

}
