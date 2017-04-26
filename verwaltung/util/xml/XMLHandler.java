package verwaltung.util.xml;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import verwaltung.config.VerwaltungConfig;
import verwaltung.model.Entry;
import verwaltung.model.EntryListWrapper;
import verwaltung.view.Verwaltung;

public class XMLHandler
{
  private static Connection connection = null;
  private static JTable     table      = null;
  private static Verwaltung verw       = null;
  private static String     sqlStorage = "";

  /**
   * exportiert die Daten aus der DB in eine XML-Datei
   * @param verw
   */
  public static void exportToXML( Verwaltung verw )
  {
    JFileChooser chooser = new JFileChooser( new File( VerwaltungConfig.ROOTPATH ) );
    chooser.setAcceptAllFileFilterUsed( false );
    chooser.setFileFilter( new FileNameExtensionFilter( "XML", "xml" ) );
    File xmlFile = null;
    if ( chooser.showSaveDialog( verw ) == JFileChooser.APPROVE_OPTION )
      xmlFile = new File( chooser.getSelectedFile().getAbsolutePath() + ".xml" );
    else
      return;

    connection = verw.getConnection();
    sqlStorage = verw.getSqlStorage();
    EntryListWrapper wrapper = new EntryListWrapper();
    ArrayList<Entry> aey = new ArrayList<Entry>();
    try
    {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery( sqlStorage );
      while ( rs.next() )
      {
        Entry entry = new Entry(
            rs.getLong( 1 ),
            rs.getLong( 2 ),
            rs.getDouble( 3 ),
            rs.getString( 4 ),
            rs.getString( 5 ) );
        aey.add( entry );
      }
    }
    catch ( Exception e )
    {
      if ( VerwaltungConfig.DEBUG )
        System.out.println( e.getMessage() );
    }
    try
    {
      JAXBContext context = JAXBContext
          .newInstance( EntryListWrapper.class );
      Marshaller m = context.createMarshaller();
      m.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
      wrapper.setEntries( aey );
      m.marshal( wrapper, xmlFile );
    }
    catch ( Exception e )
    {
      if ( VerwaltungConfig.DEBUG )
        System.out.println( e.getMessage() );
    }
    JOptionPane.showMessageDialog( table, xmlFile.getAbsolutePath() + " wurde erstellt." );
  }

  /**
   * Importiert die Daten aus einer XML-Datei in die DB
   * @param v
   */
  public static void importFromXML( Verwaltung v )
  {
    JFileChooser chooser = new JFileChooser( new File( VerwaltungConfig.ROOTPATH ) );
    chooser.setAcceptAllFileFilterUsed( false );
    chooser.setFileFilter( new FileNameExtensionFilter( "XML", "xml" ) );
    File xmlFile = null;
    if ( chooser.showOpenDialog( v ) == JFileChooser.APPROVE_OPTION )
      xmlFile = chooser.getSelectedFile();
    else
      return;

    if ( !xmlFile.exists() )
    {
      JOptionPane.showMessageDialog( v, "Es existiert keine XML-Datei" );
      return;
    }
    connection = v.getConnection();
    verw = v;

    try ( PreparedStatement ps = connection.prepareStatement(
        "INSERT INTO verwaltung VALUES ( ?, ?, ?, ? );" ); )
    {
      JAXBContext context = JAXBContext
          .newInstance( EntryListWrapper.class );
      Unmarshaller um = context.createUnmarshaller();
      EntryListWrapper wrapper = (EntryListWrapper) um.unmarshal( xmlFile );

      ArrayList<Entry> aey = wrapper.getEntries();

      for ( Entry e : aey )
      {
        if ( isInDataBase( e.getDate() ) ) continue;
        ps.setLong( 1, e.getDate() );
        ps.setDouble( 2, e.getBetrag() );
        ps.setString( 3, e.getMarke() );
        ps.setString( 4, e.getText() );
        ps.addBatch();
      }
      connection.setAutoCommit( false );
      ps.executeBatch();
      connection.setAutoCommit( true );
      verw.showTable();
    }
    catch ( Exception e )
    {
      if ( VerwaltungConfig.DEBUG )
        System.out.println( e.getMessage() );
    }
  }

  private static boolean isInDataBase( long timestamp )
  {
    long identifier = 0;
    try
    {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery( "SELECT * FROM verwaltung WHERE"
          + " Datum = " + timestamp + ";" );
      rs.next();
      identifier = rs.getLong( 1 );
      rs.close();
    }
    catch ( Exception e )
    {
      if ( VerwaltungConfig.DEBUG )
        System.out.println( e.getMessage() );
    }
    if ( identifier == timestamp ) return true;
    return false;
  }
}
