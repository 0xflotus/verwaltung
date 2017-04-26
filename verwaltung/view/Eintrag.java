package verwaltung.view;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.toedter.calendar.JMonthChooser;
import com.toedter.calendar.JYearChooser;
import com.toedter.components.JSpinField;

import verwaltung.config.VerwaltungConfig;
import verwaltung.util.DateUtil;
import verwaltung.util.Validierung;

/**
 * GUI-Klasse für den Eintrag
 * @author fthurm
 *
 */
public class Eintrag extends JPanel
{
  private JTextField               tfBetrag;
  private JTextArea                ta;
  private JCheckBox                cbxEinnahme, cbxMonthly;
  JComboBox<String>                cmbxMarken;
  private Connection               connection;
  private Verwaltung               verwaltung;
  private JSpinField               jsp;
  private JMonthChooser            jmc;
  private JYearChooser             jyc;

  private static ArrayList<String> defaultMarkenListe = new ArrayList<String>(
      Arrays.asList(
          "Essen",
          "Unterhaltung",
          "Miete",
          "Strom",
          "Versicherung",
          "Gehalt",
          "Bezüge",
          "Rate",
          "Einkauf",
          "Onlineshopping",
          "Sonstiges" ) );

  private ArrayList<String>        markenListe        = new ArrayList<String>();

  public Eintrag( Verwaltung verwaltung )
  {
    this.verwaltung = verwaltung;
    this.connection = verwaltung.getConnection();
    testTableInDatabase();
    initValuesToDatabase();
    initComponents();
  }

  private void initValuesToDatabase()
  {
    Collections.sort( defaultMarkenListe, Collections.reverseOrder() );
    try
    {
      PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO marken VALUES ( ? );" );
      for ( String s : defaultMarkenListe )
      {
        ps.setString( 1, s );
        ps.addBatch();
      }
      connection.setAutoCommit( false );
      ps.executeBatch();
      connection.setAutoCommit( true );
    }
    catch ( NumberFormatException e )
    {
      JOptionPane.showMessageDialog( this, "Fehler: " + e.getMessage() );
    }
    catch ( SQLException e )
    {
      if ( VerwaltungConfig.DEBUG )
        System.out.println( e.getMessage() );
    }
  }

  private void testTableInDatabase()
  {
    try
    {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate(
          "CREATE TABLE IF NOT EXISTS marken ("
              + "Marke      TEXT DEFAULT( '' )"
              + ");"
              + "\n"
              + "CREATE UNIQUE INDEX markenUnique ON marken(Marke);" );
    }
    catch ( Exception e )
    {
      if ( VerwaltungConfig.DEBUG )
        System.out.println( e.getMessage() );
    }
  }

  /**
   * Setzt die Werte auf ihre Defaultwerte zurück.
   */
  public void initValues()
  {
    Date date = new Date();
    GregorianCalendar gc = new GregorianCalendar();
    gc.setTime( date );
    jsp.setValue( gc.get( Calendar.DATE ) );
    jmc.setMonth( gc.get( Calendar.MONTH ) );
    jyc.setYear( gc.get( Calendar.YEAR ) );
    cmbxMarken.setSelectedIndex( 0 );
    ta.setText( "" );
    tfBetrag.setText( "0" );
    cbxEinnahme.setSelected( false );
  }

  public void initComponents()
  {
    this.setBounds( 0, 0, 300, 400 );
    this.setLayout( null );

    prepareMarkenListe();

    GregorianCalendar gc = new GregorianCalendar();
    gc.setTime( new Date() );

    JPanel pnlDatum = new JPanel();
    pnlDatum.setBounds( 10, 10, 300, 30 );
    pnlDatum.setLayout( new FlowLayout() );
    jsp = new JSpinField( 1, 31 );
    jsp.setPreferredSize( new Dimension( 35, 25 ) );
    jsp.setValue( gc.get( Calendar.DATE ) );
    jsp.addPropertyChangeListener( evt -> {
      switch ( jmc.getMonth() )
      {
        case 1:
          int jahr = jyc.getYear();
          if ( ( jahr % 4 == 0 && jahr % 100 != 0 ) || jahr % 400 == 0 )
            jsp.setMaximum( 29 );
          else
            jsp.setMaximum( 28 );
          break;
        case 3:
        case 5:
        case 8:
        case 10:
          jsp.setMaximum( 30 );
          break;
        default:
          jsp.setMaximum( 31 );
      }
    } );
    pnlDatum.add( jsp );
    jmc = new JMonthChooser();
    jmc.setPreferredSize( new Dimension( 110, 25 ) );
    jmc.setMonth( gc.get( Calendar.MONTH ) );
    pnlDatum.add( jmc );
    jyc = new JYearChooser();
    jyc.setPreferredSize( new Dimension( 50, 25 ) );
    jyc.setYear( gc.get( Calendar.YEAR ) );
    pnlDatum.add( jyc );
    this.add( pnlDatum );

    JLabel lblKategorie = new JLabel( "Kategorie:" );
    lblKategorie.setBounds( 55, 45, 70, 25 );
    this.add( lblKategorie );
    cmbxMarken = new JComboBox<String>();
    for ( String s : markenListe )
      cmbxMarken.addItem( s );
    cmbxMarken.setBounds( 135, 45, 130, 25 );
    cmbxMarken.addActionListener( ae -> changeAtion() );
    this.add( cmbxMarken );
    JLabel lblBetrag = new JLabel( "Betrag:" );
    lblBetrag.setBounds( 55, 75, 70, 25 );
    this.add( lblBetrag );
    tfBetrag = new JTextField( "0" );
    tfBetrag.setBounds( 135, 75, 130, 25 );
    tfBetrag.setHorizontalAlignment( SwingConstants.RIGHT );
    this.add( tfBetrag );
    cbxEinnahme = new JCheckBox( "Einnahme" );
    cbxEinnahme.setBounds( 55, 105, 100, 25 );
    this.add( cbxEinnahme );
    cbxMonthly = new JCheckBox( "Fixkosten" ); //TODO
    cbxMonthly.setBounds( 155, 105, 150, 25 );
    this.add( cbxMonthly );
    JLabel lblText = new JLabel( "Text:" );
    lblText.setBounds( 55, 135, 70, 25 );
    this.add( lblText );
    ta = new JTextArea();
    ta.setLineWrap( true );
    JScrollPane sp = new JScrollPane( ta );
    sp.setVerticalScrollBarPolicy( JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED );
    sp.setBounds( 135, 135, 130, 90 );
    this.add( sp );
    JButton btnAbsenden = new JButton( "Absenden" );
    btnAbsenden.setBounds( 165, 230, 100, 25 );
    btnAbsenden.addActionListener( ae -> absenden() );
    this.add( btnAbsenden );
  }

  private void changeAtion()
  {
    if ( cmbxMarken.getSelectedItem().toString().equals( "Gehalt" ) ||
        cmbxMarken.getSelectedItem().toString().equals( "Bezüge" ) )
      cbxEinnahme.setSelected( true );
    else
      cbxEinnahme.setSelected( false );
  }

  private void prepareMarkenListe()
  {
    try
    {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery( "SELECT * FROM marken;" );

      while ( rs.next() )
      {
        markenListe.add( rs.getString( 1 ) );
      }
      rs.close();
    }
    catch ( Exception ex )
    {
      System.out.println( ex.getMessage() );
    }
  }

  private void absenden()
  {
    if ( cbxMonthly.isSelected() )
    {
      fuegFixWertHinzu();
      return;
    }

    if ( ta.getText().length() < 2 )
    {
      JOptionPane.showMessageDialog( this, "Der Text muss mindestens zwei Zeichen lang sein." );
      return;
    }

    try
    {
      PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO verwaltung VALUES ( ?, ?, ?, ? );" );

      ps.setLong( 1, DateUtil.getTimeStamp( jsp.getValue(), jmc.getMonth(), jyc.getYear() ) );
      if ( Validierung.isValidNumber( tfBetrag.getText() ) )
      {
        if ( Double.parseDouble( tfBetrag.getText() ) > 0.0 )
        {
          if ( cbxEinnahme.isSelected() )
            ps.setDouble( 2, Double.parseDouble( tfBetrag.getText() ) );
          else
            ps.setDouble( 2, Double.parseDouble( tfBetrag.getText() ) * -1 );
        }
        else
        {
          JOptionPane.showMessageDialog( this, "Der Betrag muss größer als 0 sein." );
          return;
        }
      }
      else
      {
        JOptionPane.showMessageDialog( this, "Keine Valide Zahl" );
        return;
      }
      ps.setString( 3, cmbxMarken.getSelectedItem().toString() );
      ps.setString( 4, ta.getText() );
      ps.addBatch();

      connection.setAutoCommit( false );
      ps.executeBatch();
      connection.setAutoCommit( true );
    }
    catch ( NumberFormatException | SQLException e )
    {
      if ( VerwaltungConfig.DEBUG )
        JOptionPane.showMessageDialog( this, "Fehler: " + e.getMessage() );
    }
    JOptionPane.showMessageDialog( this, "Eintrag wurde erfolgreich hinzugefügt." );
    initValues();
  }

  private void fuegFixWertHinzu()
  {

    try
    {
      PreparedStatement ps = connection.prepareStatement(
          "INSERT INTO fixwerte VALUES ( ?, ?, ?, ? );" );

      ps.setDouble( 1, Double.parseDouble( tfBetrag.getText() ) );
      ps.setString( 2, cmbxMarken.getSelectedItem().toString() );
      ps.setString( 3, ta.getText() );
      ps.setInt( 4, cbxEinnahme.isSelected() ? 1 : 0 );
      ps.addBatch();

      connection.setAutoCommit( false );
      ps.executeBatch();
      connection.setAutoCommit( true );

    }
    catch ( SQLException e )
    {
      if ( VerwaltungConfig.DEBUG )
        JOptionPane.showMessageDialog( this, "Der Wert " + ta.getText() + " ist bereits vorhanden." );
    }

  }

  /**
   * Gibt die default Markenliste zurück
   * @return
   */
  public static ArrayList<String> getDefaultMarkenListe()
  {
    return defaultMarkenListe;
  }

}
