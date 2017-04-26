package verwaltung.view;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Rectangle;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JList;

import verwaltung.config.VerwaltungConfig;
import verwaltung.model.Fixwert;
import verwaltung.util.listener.MyWindowAdapter;

/**
 * GUI-Klasse für die monatlichen Fixkosten
 * @author fthurm
 *
 */
public class FixwerteOverview extends JDialog
{
  private Verwaltung                verwaltung;
  private Connection                connection;
  private JList<Fixwert>            jl;
  private DefaultListModel<Fixwert> dlm;

  public FixwerteOverview( Verwaltung verwaltung )
  {
    this.verwaltung = verwaltung;
    this.connection = verwaltung.getConnection();
    initComponents();
    this.setVisible( true );
  }

  private void initComponents()
  {
    this.setTitle( "Fixwerte" );
    this.setSize( new Dimension( 300, 420 ) );
    this.setLayout( null );
    this.setResizable( false );
    this.setLocationRelativeTo( verwaltung );
    this.addWindowListener( new MyWindowAdapter()
        .closing( e -> this.dispose() )
        .closed( e -> verwaltung.setVisible( true ) )
        .build() );

    jl = new JList<Fixwert>();
    jl.setBounds( 0, 0, 300, 330 );
    dlm = new DefaultListModel<Fixwert>();
    jl.setModel( dlm );
    this.add( jl );

    fillList();

    JButton btnDelete = new JButton( "Löschen" );
    btnDelete.addActionListener( ae -> deleteFixwert( jl.getModel().getElementAt( jl.getSelectedIndex() ) ) );

    btnDelete.setMargin( new Insets( 0, 0, 0, 0 ) );
    btnDelete.setBounds( new Rectangle( 0, 340, 100, 25 ) );
    this.add( btnDelete );
  }

  private void deleteFixwert( Fixwert fixwert )
  {
    try
    {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate( "DELETE FROM fixwerte WHERE rowid = "
          + jl.getSelectedIndex() + " ;" );
      stmt.close();

    }
    catch ( SQLException e )
    {
      if ( VerwaltungConfig.DEBUG )
        System.out.println( e.getMessage() );
    }
    System.out.println( "ausgefuehrt" );
    fillList();
    this.repaint();
  }

  private void fillList()
  {
    try
    {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery( "SELECT rowid,* FROM fixwerte;" );
      while ( rs.next() )
      {
        dlm.addElement( new Fixwert(
            rs.getDouble( 2 ),
            rs.getString( 3 ),
            rs.getString( 4 ),
            rs.getInt( 5 ) == 1 ? true : false,
            rs.getLong( 1 ) ) );
      }
      rs.close();
    }
    catch ( Exception e )
    {
      if ( VerwaltungConfig.DEBUG )
        System.out.println( e.getMessage() );
    }
  }
}
