package verwaltung.admin;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import verwaltung.util.listener.MyWindowAdapter;
import verwaltung.view.Verwaltung;

public class Anmeldung extends JFrame
{
  private Verwaltung     verwaltung;
  private Connection     connection;
  private JPasswordField jpwf;
  private JTextField     tfUser;

  private final String   USERNAME = "user";
  private final String   PASSWORD = "password";
  private final String   SALT     = "salt";

  public Anmeldung( Verwaltung verwaltung )
  {
    this.connection = verwaltung.getConnection();
    this.verwaltung = verwaltung;
    this.verwaltung.setVisible( false );
    initUserTable();
    initComponents();
    this.setVisible( true );
  }

  private void initUserTable()
  {
    try
    {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate(
          "CREATE TABLE IF NOT EXISTS users ("
              + "user TEXT NOT NULL UNIQUE DEFAULT '', "
              + "hash TEXT NOT NULL  DEFAULT '')" );
    }
    catch ( Exception ex )
    {
      System.out.println( ex.getMessage() );
    }

    boolean isVorhanden = false;
    try
    {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery( "SELECT user FROM users WHERE user = '"
          + USERNAME + "';" );

      while ( rs.next() )
      {
        isVorhanden = true;
      }
      rs.close();
    }
    catch (

    Exception ex )
    {
      System.out.println( ex.getMessage() );
    }

    if ( !isVorhanden )
    {
      try
      {
        PreparedStatement ps = connection.prepareStatement(
            "INSERT INTO users VALUES ( ?, ?);" );
        ps.setString( 1, USERNAME );
        ps.setString( 2, Anmeldung.getHash( PASSWORD + SALT ) );
        ps.addBatch();
        connection.setAutoCommit( false );
        ps.executeBatch();
        connection.setAutoCommit( true );
      }
      catch ( SQLException e )
      {
        System.out.println( e.getMessage() );
      }
    }
  }

  private void initComponents()
  {
    this.setTitle( "Admin" );
    this.setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
    this.setLayout( new FlowLayout() );
    this.setSize( new Dimension( 300, 100 ) );
    this.setLocationRelativeTo( null );
    this.addWindowListener( new MyWindowAdapter()
        .closing( e -> this.dispose() )
        .closed( e -> this.verwaltung.setVisible( true ) )
        .build() );

    tfUser = new JTextField( "user" );
    tfUser.setPreferredSize( new Dimension( 75, 25 ) );
    this.add( tfUser );

    jpwf = new JPasswordField();
    jpwf.setPreferredSize( new Dimension( 125, 25 ) );
    this.add( jpwf );

    JButton btnLogin = new JButton( "Login" );
    btnLogin.addActionListener( ae -> loginAction() );
    this.add( btnLogin );
  }

  private void loginAction()
  {
    if ( getHash( String.valueOf( jpwf.getPassword() ) + SALT ).equals( getHashForUser( tfUser.getText() ) ) )
    {
      this.setVisible( false );
      new Admin( this.verwaltung );
    }
    else
    {
      JOptionPane.showMessageDialog( this, "Falscher User oder Passwort" );
      this.dispose();
    }

  }

  /**
   * berechnet den Hash-Wert
   * @param password das zu hashende Passwort
   * @return den Hashwert
   */
  public static String getHash( String password )
  {
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
    return hash;
  }

  private String getHashForUser( String user )
  {
    String sql = "SELECT hash from users WHERE user = '"
        + user + "';";
    String retVal = "";
    try
    {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery( sql );

      while ( rs.next() )
      {
        retVal += rs.getString( 1 );
      }
      rs.close();
    }
    catch ( Exception e )
    {
      System.out.println( e.getMessage() );
    }
    return retVal;
  }

}
