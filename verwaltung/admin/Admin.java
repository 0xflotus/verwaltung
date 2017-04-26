package verwaltung.admin;

import java.awt.FlowLayout;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;

import verwaltung.util.listener.MyWindowAdapter;
import verwaltung.view.FixwerteOverview;
import verwaltung.view.Verwaltung;

public class Admin extends JFrame
{

  private Verwaltung verwaltung;
  private Connection conn;

  public Admin( Verwaltung verwaltung )
  {
    this.conn = verwaltung.getConnection();
    this.verwaltung = verwaltung;
    initComponents();
    this.setVisible( true );
  }

  private void initComponents()
  {
    this.setTitle( "AdminPanel" );
    this.setLayout( new FlowLayout() );
    this.setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
    this.addWindowListener( new MyWindowAdapter()
        .closing( e -> this.dispose() )
        .closed( e -> {
          this.verwaltung.setVisible( true );
          this.verwaltung.showTable();
        } )
        .build() );
    this.setSize( 300, 300 );
    this.setLocationRelativeTo( null );

    JButton btnCreatePW = new JButton( "createPW" );
    btnCreatePW.addActionListener( ae -> createPassWord() );
    this.add( btnCreatePW );

    JButton btnClearDataBase = new JButton( "Clear Database" );
    btnClearDataBase.addActionListener( ae -> clearDatabase( "verwaltung" ) );
    this.add( btnClearDataBase );

    JButton btnFixwerte = new JButton( "Verwaltung Fixwerte" );
    btnFixwerte.addActionListener( ae -> {
      this.setVisible( false );
      new FixwerteOverview( verwaltung );
    } );
    this.add( btnFixwerte );
  }

  /**
   * Löscht alle Einträge in der Tabelle
   * @param tabelle die zu leerende Tabelle
   */
  private void clearDatabase( String tabelle )
  {
    try
    {
      Statement stmt = conn.createStatement();
      stmt.executeUpdate( "DELETE FROM " + tabelle + ";" );
      stmt.close();
    }
    catch ( SQLException e )
    {
      System.out.println( e.getMessage() );
    }
  }

  private String createPassWord()
  {
    String password = "r00t";
    String hash = "";
    try
    {
      MessageDigest md = MessageDigest.getInstance( "SHA-256" );
      md.update( password.getBytes() );
      BigInteger bi = new BigInteger( 1, md.digest() );
      hash = bi.toString( 16 ).toUpperCase();
    }
    catch ( NoSuchAlgorithmException e1 )
    {
      System.out.println( "Diesen Algorithmus gibt es nicht." );
    }
    System.out.println( hash );
    return hash;
  }

}
