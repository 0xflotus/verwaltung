package verwaltung.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.filechooser.FileNameExtensionFilter;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import jiconfont.icons.FontAwesome;
import jiconfont.swing.IconFontSwing;
import verwaltung.config.VerwaltungConfig;
import verwaltung.util.DateUtil;
import verwaltung.util.Zeitraum;
import verwaltung.util.listener.PopupListener;

/**
 * GUI-KLasse für die Anzeige als Statistik
 * @author fthurm
 *
 */
public class Statistik extends JPanel
{
  //TODO Daten als Statistik anzeigen
  private Connection connection;
  private JLabel     lblSaldo;
  private ChartPanel pnlChartAll, cpMonth, pnlMonthOverview;
  private String     sqlStorage;
  private Date       dateStorage;
  private Verwaltung verwaltung;

  public Statistik( Verwaltung verw )
  {
    this.verwaltung = verw;
    this.connection = verw.getConnection();
    this.sqlStorage = verw.getSqlStorage();
    this.dateStorage = verw.getDateStorage();
    initComponents();
  }

  private void initComponents()
  {
    IconFontSwing.register( FontAwesome.getIconFont() );
    GregorianCalendar gc = new GregorianCalendar();
    gc.setTime( dateStorage );

    this.setLayout( null );
    this.setBackground( Color.WHITE );

    lblSaldo = new JLabel( "Saldo: " );
    lblSaldo.setBounds( 10, 10, 400, 35 );
    this.add( lblSaldo );

    JPopupMenu popup = new JPopupMenu();
    JMenuItem miPNG = new JMenuItem( "Save as PNG", IconFontSwing.buildIcon( FontAwesome.FLOPPY_O, 12f, Color.BLUE ) );
    miPNG.addActionListener( ae -> saveAsPNGAction( pnlChartAll ) );
    popup.add( miPNG );
    JMenuItem miJPG = new JMenuItem( "Save as JPG", IconFontSwing.buildIcon( FontAwesome.FLOPPY_O, 12f, Color.BLUE ) );
    miJPG.addActionListener( ae -> saveAsJPGAction( pnlChartAll ) );
    popup.add( miJPG );

    JPopupMenu popup2 = new JPopupMenu();
    JMenuItem mi2PNG = new JMenuItem( "Save as PNG", IconFontSwing.buildIcon( FontAwesome.FLOPPY_O, 12f, Color.BLUE ) );
    mi2PNG.addActionListener( ae -> saveAsPNGAction( cpMonth ) );
    popup2.add( mi2PNG );
    JMenuItem mi2JPG = new JMenuItem( "Save as JPG", IconFontSwing.buildIcon( FontAwesome.FLOPPY_O, 12f, Color.BLUE ) );
    mi2JPG.addActionListener( ae -> saveAsJPGAction( cpMonth ) );
    popup2.add( mi2JPG );

    DefaultPieDataset pieDataset = new DefaultPieDataset();
    pieDataset.setValue( "Einkommen: " + String.format( "%,.2f", getAllIncome() ), Math.round( getAllIncome() ) );
    pieDataset.setValue( "Ausgaben: " + String.format( "%,.2f", getAllOutcome() ), Math.round( getAllOutcome() * -1 ) );

    JFreeChart chartAll = ChartFactory.createPieChart( "Gesamtübersicht", pieDataset, true, false, false );
    chartAll.setAntiAlias( true );
    chartAll.removeLegend();

    pnlChartAll = new ChartPanel( chartAll );
    pnlChartAll.setBounds( 10, 50, 520, 400 );
    pnlChartAll.setPreferredSize( new Dimension( 120, 100 ) );
    for ( MouseListener l : pnlChartAll.getMouseListeners() )
      pnlChartAll.removeMouseListener( l );
    pnlChartAll.addMouseListener( new PopupListener( popup ) );
    pnlChartAll.setBackground( Color.WHITE );
    this.add( pnlChartAll );

    JFreeChart chartMonth = ChartFactory.createLineChart(
        verwaltung.isSettingYearisClicked()
            ? "Monatlich " + gc.get( Calendar.YEAR )
            : "Täglich "
                + DateUtil.getMonthName( gc.get( Calendar.MONTH ) )
                + " " + gc.get( Calendar.YEAR ),
        "Datum",
        "Betrag",
        createDataSetForLineMonth(),
        PlotOrientation.VERTICAL, true, true, false );
    cpMonth = new ChartPanel( chartMonth );
    cpMonth.setBounds( 550, 50, 500, 400 );
    cpMonth.setPreferredSize( new Dimension( 500, 400 ) );
    for ( MouseListener l : cpMonth.getMouseListeners() )
      cpMonth.removeMouseListener( l );
    cpMonth.addMouseListener( new PopupListener( popup2 ) );
    this.add( cpMonth );

    lblSaldo.setText( String.format( "Saldo: %,.2f "
        + "Einkommen: %,.2f "
        + "Ausgaben: %,.2f ", getAllSaldo(), getAllIncome(), getAllOutcome() ) );
  }

  private CategoryDataset createDataSetForLineMonth()
  {
    DefaultCategoryDataset dcds = new DefaultCategoryDataset();
    try
    {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(
          verwaltung.isSettingYearisClicked()
              ? Zeitraum.getSQLForYearPeriod( dateStorage )
              : Zeitraum.getSQLForMonthPeriod( dateStorage ) );
      GregorianCalendar gc = new GregorianCalendar();

      while ( rs.next() )
      {
        Date d = new Date( rs.getLong( 2 ) );
        gc.setTime( d );
        dcds.addValue( rs.getDouble( 3 ), "Ein/Ausgaben",
            verwaltung.isSettingYearisClicked()
                ? String.valueOf( gc.get( Calendar.MONTH ) + 1 )
                : String.valueOf( gc.get( Calendar.DATE ) ) );
      }
      rs.close();
    }
    catch ( SQLException e )
    {
      if ( VerwaltungConfig.DEBUG )
        System.out.println( e.getMessage() );
    }
    return dcds;
  }

  //TODO ähnlich verfahren wie PNG
  private void saveAsJPGAction( ChartPanel chartPanel )
  {
    JFreeChart jfc = chartPanel.getChart();
    JFileChooser chooser = new JFileChooser( new File( VerwaltungConfig.ROOTPATH ) );
    chooser.setAcceptAllFileFilterUsed( false );
    chooser.setFileFilter( new FileNameExtensionFilter( "JPEG", "jpg" ) );
    File jpgFile = null;
    if ( chooser.showSaveDialog( this ) == JFileChooser.APPROVE_OPTION )
      jpgFile = new File( chooser.getSelectedFile().getAbsolutePath() + ".jpg" );
    try
    {
      ChartUtilities.saveChartAsJPEG( jpgFile,
          jfc, chartPanel.getWidth(), chartPanel.getHeight() );
    }
    catch ( IOException e )
    {
      if ( VerwaltungConfig.DEBUG )
        System.out.println( e.getMessage() );
    }
    JOptionPane.showMessageDialog( this, jpgFile.getName() + " wurde erstellt." );
  }

  private void saveAsPNGAction( ChartPanel chartPanel )
  {
    try
    {
      chartPanel.setDefaultDirectoryForSaveAs( new File( VerwaltungConfig.ROOTPATH ) );
      chartPanel.doSaveAs();
    }
    catch ( IOException e )
    {
      if ( VerwaltungConfig.DEBUG )
        System.out.println( e.getMessage() );
    }
  }

  private double getAllIncome()
  {
    return getValueFromDatabase( "SELECT betrag FROM verwaltung WHERE betrag > 0 ORDER BY Datum" );
  }

  private double getAllOutcome()
  {
    return getValueFromDatabase( "SELECT betrag FROM verwaltung WHERE betrag < 0 ORDER BY Datum" );
  }

  private double getValueFromDatabase( String sql )
  {
    double retVal = 0.0;
    try
    {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery( sql );

      while ( rs.next() )
      {
        retVal += rs.getDouble( 1 );
      }
      rs.close();
    }
    catch ( Exception e )
    {
      System.out.println( e.getMessage() );
    }
    return retVal;
  }

  private double getMonthlySaldo()
  {
    return getValueFromDatabase( sqlStorage );
  }

  private double getAllSaldo()
  {
    return getValueFromDatabase( "SELECT betrag FROM verwaltung ORDER BY Datum" );
  }
}