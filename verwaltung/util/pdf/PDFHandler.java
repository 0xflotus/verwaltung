package verwaltung.util.pdf;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.JOptionPane;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import verwaltung.config.VerwaltungConfig;
import verwaltung.model.Entry;
import verwaltung.util.DateUtil;
import verwaltung.view.Verwaltung;

public class PDFHandler
{
  private static final Font SUBFONT = new Font( Font.TIMES_ROMAN, 12, Font.BOLD );
  private ArrayList<Entry>  ale     = new ArrayList<Entry>();
  private Verwaltung        parent;
  private Connection        connection;

  /**
   * Erstellt ein neues PDFHandler-Objekt
   * @param verwaltung
   */
  public PDFHandler( Verwaltung verwaltung )
  {
    this.parent = verwaltung;
    fill();
  }

  /**
   * füllt das ale-Objekt mit den Daten aus der Datenbank
   */
  private void fill()
  {
    loadDBDriver();
    initDataBase();
    try
    {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery(
          parent.getSqlStorage() );

      while ( rs.next() )
      {
        ale.add( new Entry(
            rs.getLong( 2 ),
            rs.getDouble( 3 ),
            rs.getString( 4 ),
            rs.getString( 5 ) ) );
      }
      rs.close();
    }
    catch ( Exception ex )
    {
      System.out.println( ex.getMessage() );
    }

  }

  private void initDataBase()
  {
    try
    {
      if ( connection != null )
        return;
      connection = DriverManager.getConnection( "jdbc:sqlite:" + VerwaltungConfig.DB_FILE );
    }
    catch ( Exception ex )
    {
      System.out.println( ex.getMessage() );
    }
  }

  private void loadDBDriver()
  {
    try
    {
      Class.forName( "org.sqlite.JDBC" );
    }
    catch ( Exception ex )
    {
      System.out.println( ex.getMessage() );
    }
  }

  /**
   * Erstellt das PDF
   */
  public void createPdf()
  {
    Document document = new Document();
    GregorianCalendar gc = new GregorianCalendar();
    gc.setTime( parent.getDateStorage() );
    String title = parent.getSqlStorage()
        .equals( "SELECT rowid, * FROM verwaltung ORDER BY Datum" )
            ? "Tabelleübersicht Gesamt"
            : "Tabellenübersicht "
                + ( parent.isSettingYearisClicked()
                    ? gc.get( Calendar.YEAR )
                    : DateUtil.getMonthName( gc.get( Calendar.MONTH ) ) + " " + gc.get( Calendar.YEAR ) );
    try
    {
      PdfWriter.getInstance( document,
          new FileOutputStream( new File( VerwaltungConfig.ROOTPATH + title.replaceAll( " ", "_" ) + ".pdf" ) ) );

      document.setPageSize( PageSize.A4 );
      document.addTitle( title );
      document.addCreationDate();

      HeaderFooter footer = new HeaderFooter( new Phrase( "Seite " ), true );
      document.setFooter( footer );

      document.open();

      Paragraph para = new Paragraph( title );
      para.setAlignment( Element.ALIGN_CENTER );
      para.setFont( new Font( Font.TIMES_ROMAN, 18, Font.BOLD ) );
      document.add( para );
      document.add( new Chunk( "\n" ) );
      document.add( new Chunk( "\n" ) );

      PdfPTable header = new PdfPTable( 4 );
      PdfPTable table = new PdfPTable( 4 );
      table.setTotalWidth( 300f );
      table.setWidthPercentage( 100f );

      Paragraph p1 = new Paragraph( "Datum", SUBFONT );
      Paragraph p2 = new Paragraph( "Betrag", SUBFONT );
      Paragraph p3 = new Paragraph( "Marke", SUBFONT );
      Paragraph p4 = new Paragraph( "Text", SUBFONT );

      PdfPCell head1 = new PdfPCell( p1 );
      head1.setHorizontalAlignment( Element.ALIGN_CENTER );
      PdfPCell head2 = new PdfPCell( p2 );
      head2.setHorizontalAlignment( Element.ALIGN_CENTER );
      PdfPCell head3 = new PdfPCell( p3 );
      head3.setHorizontalAlignment( Element.ALIGN_CENTER );
      PdfPCell head4 = new PdfPCell( p4 );
      head4.setHorizontalAlignment( Element.ALIGN_CENTER );

      table.addCell( head1 );
      table.addCell( head2 );
      table.addCell( head3 );
      table.addCell( head4 );

      Calendar cal = new GregorianCalendar();
      for ( Entry e : ale )
      {
        cal.setTime( new Date( e.getDate() ) );
        PdfPCell cell1 = new PdfPCell( new Paragraph(
            String.format( "%02d.%02d.%04d %02d:%02d Uhr",
                cal.get( Calendar.DAY_OF_MONTH ),
                cal.get( Calendar.MONTH ) + 1,
                cal.get( Calendar.YEAR ),
                cal.get( Calendar.HOUR_OF_DAY ),
                cal.get( Calendar.MINUTE ) ) ) );
        cell1.setHorizontalAlignment( Element.ALIGN_CENTER );
        cell1.setNoWrap( true );

        Font f2 = new Font( Font.TIMES_ROMAN, Font.DEFAULTSIZE, Font.NORMAL,
            e.getBetrag() < 0 ? Color.RED : Color.BLACK );
        Paragraph pc2 = new Paragraph( String.format( "%.2f", e.getBetrag() ), f2 );
        PdfPCell cell2 = new PdfPCell( pc2 );
        cell2.setHorizontalAlignment( Element.ALIGN_RIGHT );
        PdfPCell cell3 = new PdfPCell( new Paragraph( e.getMarke() ) );
        cell3.setHorizontalAlignment( Element.ALIGN_CENTER );
        PdfPCell cell4 = new PdfPCell( new Paragraph( e.getText() ) );
        cell4.setHorizontalAlignment( Element.ALIGN_CENTER );

        table.addCell( cell1 );
        table.addCell( cell2 );
        table.addCell( cell3 );
        table.addCell( cell4 );

      }
      document.add( header );
      document.add( table );

      document.close();
    }
    catch ( Exception e )
    {
      System.out.println( e.getMessage() );
    }

    JOptionPane.showMessageDialog( parent,
        VerwaltungConfig.ROOTPATH
            + title.replaceAll( " ", "_" )
            + ".pdf wurde erstellt." );
  }
}
