package verwaltung.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;

import com.toedter.calendar.JMonthChooser;
import com.toedter.calendar.JYearChooser;

import jiconfont.icons.Elusive;
import jiconfont.icons.Entypo;
import jiconfont.icons.FontAwesome;
import jiconfont.icons.GoogleMaterialDesignIcons;
import jiconfont.icons.Iconic;
import jiconfont.icons.Typicons;
import jiconfont.swing.IconFontSwing;
import verwaltung.admin.Anmeldung;
import verwaltung.config.VerwaltungConfig;
import verwaltung.model.Fixwert;
import verwaltung.test.TestDB;
import verwaltung.util.DateUtil;
import verwaltung.util.TableAdapter;
import verwaltung.util.WinUtil;
import verwaltung.util.WinUtil.MenuItemType;
import verwaltung.util.json.JSONHandler;
import verwaltung.util.listener.MyKeyListener;
import verwaltung.util.listener.MyMouseListener;
import verwaltung.util.listener.MyMouseMotionAdapter;
import verwaltung.util.listener.MyWindowAdapter;
import verwaltung.util.listener.PopupListener;
import verwaltung.util.pdf.PDFHandler;
import verwaltung.util.renderer.MyCellRenderer;
import verwaltung.util.xml.XMLHandler;

/**
 * GUI-KLasse für das Hauptprogramm
 * @author fthurm
 *
 */
public class Verwaltung extends JFrame
{
  private final String      DB_FILE              = VerwaltungConfig.DB_FILE;
  private final String      SQL_FOR_ALL_DATA     = "SELECT rowid, * FROM verwaltung ORDER BY Datum";
  private Connection        connection;
  private String            sqlStorage           = "";
  private Date              dateStorage          = new Date();

  private JPanel            pnlMain, pnlStatusbar, pnlButtonbar, pnlTableControl, pnlTable;
  private JLabel            lblZeitAnzeige, lblStatus = new JLabel();
  private MyButton          btnExit, btnTabelle, btnStatistik,
      btnEintrag;
  private JButton           btnForward, btnBackward;
  private DefaultTableModel dtm;
  private JTable            table;
  private JMenuBar          menubar;
  private JMenu             menuFile, menuEdit, menuFileSubXML, menuFileSubJSON, menuSetting, menuHelp;
  private JMenuItem         miExit, miTable, miNew, miRestart, miDelete,
      miScreenshot, miStatistik, miExportXML, miImportXML, miExportPdf, miImportJSON, miExportJSON, miSteuerung,
      miSettingYear, miSettingMinus;
  private JPopupMenu        popupTable;

  private SimpleDateFormat  sdf                  = new SimpleDateFormat( "dd. MMMM yyyy HH:mm" );
  GregorianCalendar         gcmin, gcmax;

  private JMonthChooser     jmc;
  private JYearChooser      jyc;

  private boolean           settingYearisClicked = false, settingMinusisClicked = false;

  public Verwaltung()
  {
    //TODO Debug-Funktion
    if ( VerwaltungConfig.DEBUG )
      test();
    loadDBDriver();
    initDataBase();
    testTableInDatabase();
    initComponents();
    initFrame();
    showFrame();
  }

  /**
   * Methode um Testdatensätze in die DB zu schreiben
   */
  private void test()
  {
    TestDB test = new TestDB();
    //test.clearTabelle();
    test.fuellTabelle( 5 );
    System.out.println( "Geendet um " + new Date() );
  }

  private void showAllInTable()
  {
    if ( settingMinusisClicked )
      showDataInTable( "SELECT rowid, * FROM verwaltung WHERE Betrag < 0 ORDER BY Datum" );
    else
      showDataInTable( SQL_FOR_ALL_DATA );
  }

  public void showTable()
  {
    pnlTable.setVisible( true );
    showDataInTable( this.getSqlStorage() );
  }

  public void showStatistics()
  {
    JDialog dialogStatistik = new JDialog();
    dialogStatistik.setTitle( "Statistik" );
    dialogStatistik.setLayout( new BorderLayout() );
    dialogStatistik.setSize( new Dimension( (int) this.getSize().getWidth() + 100, (int) this.getSize().getHeight() ) );
    dialogStatistik.setLocation( this.getLocation() );
    dialogStatistik.setModal( true );
    dialogStatistik.add( new Statistik( this ) );
    dialogStatistik.setVisible( true );
  }

  public void showEintrag()
  {
    JDialog dialogEintrag = new JDialog();
    dialogEintrag.setLayout( new BorderLayout() );
    dialogEintrag.setSize( 300, 300 );
    dialogEintrag.setLocationRelativeTo( this );
    dialogEintrag.add( new Eintrag( this ) );
    this.setVisible( false );
    dialogEintrag.addWindowListener( new MyWindowAdapter()
        .closing( e -> {
          showTable();
          this.setVisible( true );
          showDataInTable( this.getSqlStorage() );
        } )
        .build() );
    dialogEintrag.setVisible( true );
  }

  private void initComponents()
  {
    this.setTitle( "Verwaltung 0.01" );
    this.setDefaultCloseOperation( DO_NOTHING_ON_CLOSE );
    this.addWindowListener( new MyWindowAdapter()
        .closing( e -> {
          this.dispose();
          closeDatabase();
        } )
        .closed( e -> closed() )
        .build() );
    this.setSize( 1000, 700 );
    this.setLayout( new BorderLayout() );

    IconFontSwing.register( Typicons.getIconFont() );
    IconFontSwing.register( FontAwesome.getIconFont() );
    IconFontSwing.register( GoogleMaterialDesignIcons.getIconFont() );
    IconFontSwing.register( Elusive.getIconFont() );
    IconFontSwing.register( Iconic.getIconFont() );
    IconFontSwing.register( Entypo.getIconFont() );

    menubar = new JMenuBar();
    menuFile = WinUtil.createMenu( menubar, "Datei", "Datei", 0, IconFontSwing.buildIcon( FontAwesome.FILE, 12f ) );
    menuEdit = WinUtil.createMenu( menubar, "Bearbeiten", "Bearbeiten", 0,
        IconFontSwing.buildIcon( Elusive.COGS, 12f ) );
    menuHelp = WinUtil.createMenu( menubar, "Hilfe", "help", 0,
        IconFontSwing.buildIcon( GoogleMaterialDesignIcons.HEALING, 12f ) );

    menubar.add( Box.createHorizontalGlue() );
    menuSetting = WinUtil.createMenu( menubar, "Einstellungen", "Setting", 0,
        IconFontSwing.buildIcon( FontAwesome.WRENCH, 12f ) );
    miRestart = WinUtil.createMenuItem( menuFile, "Restart", "Restart", MenuItemType.ITEM_PLAIN, ae -> newAction(),
        IconFontSwing.buildIcon( FontAwesome.PAPER_PLANE, 12f ), 0, KeyEvent.VK_N, "oeffnet ein neues Fenster" );
    miTable = WinUtil.createMenuItem( menuEdit, "Gesamtübersicht", "Tabelle",
        MenuItemType.ITEM_PLAIN, ae -> showAllInTable(),
        IconFontSwing.buildIcon( GoogleMaterialDesignIcons.VIEW_LIST, 12f ), 0,
        KeyEvent.VK_T, "Zeigt die Tabelle an." );
    miNew = WinUtil.createMenuItem( menuEdit, "Neu", "Neu",
        MenuItemType.ITEM_PLAIN, ae -> showEintrag(),
        IconFontSwing.buildIcon( FontAwesome.CALENDAR_PLUS_O, 12f ), 0,
        KeyEvent.VK_N, "Erstelle einen neuen Eintrag" );
    menuFileSubXML = WinUtil.createSubMenu( menuFile, "XML Import/Export", "ImEx", 0,
        IconFontSwing.buildIcon( FontAwesome.FILE_EXCEL_O, 12f ) );
    miImportXML = WinUtil.createMenuItem( menuFileSubXML, "Datei von XML importieren", "import",
        MenuItemType.ITEM_PLAIN, ae -> XMLHandler.importFromXML( this ),
        IconFontSwing.buildIcon( GoogleMaterialDesignIcons.CLOUD_DOWNLOAD, 12f ), 0, KeyEvent.VK_I,
        "Importiere aus einer XML-Datei" );
    miExportXML = WinUtil.createMenuItem( menuFileSubXML, "Datei in XML exportieren", "export",
        MenuItemType.ITEM_PLAIN, ae -> XMLHandler.exportToXML( this ),
        IconFontSwing.buildIcon( GoogleMaterialDesignIcons.CLOUD_UPLOAD, 12f ), 0, KeyEvent.VK_E,
        "Exportiere die Tabelle in eine XML-Datei" );
    menuFileSubJSON = WinUtil.createSubMenu( menuFile, "JSON Import/Export", "jsonsub", 0,
        IconFontSwing.buildIcon( FontAwesome.FILE_CODE_O, 12f ) );
    miImportJSON = WinUtil.createMenuItem( menuFileSubJSON, "Datei von JSON importieren", "imJson",
        MenuItemType.ITEM_PLAIN,
        ae -> importJSONAction(), IconFontSwing.buildIcon( GoogleMaterialDesignIcons.CLOUD_DOWNLOAD, 12f ), 0,
        "Importiert von JSON" );
    miImportJSON.setAccelerator( KeyStroke.getKeyStroke( "ctrl alt I" ) );
    miExportJSON = WinUtil.createMenuItem( menuFileSubJSON, "Datei in JSON exportieren", "exJson",
        MenuItemType.ITEM_PLAIN,
        ae -> exportJsonAction(), IconFontSwing.buildIcon( GoogleMaterialDesignIcons.CLOUD_UPLOAD, 12f ), 0,
        "Exportiert nach JSON" );
    miExportJSON.setAccelerator( KeyStroke.getKeyStroke( "ctrl alt E" ) );
    miExportPdf = WinUtil.createMenuItem( menuFile, "Erstelle PDF", "PDFcreate", MenuItemType.ITEM_PLAIN,
        ae -> pdfAction(), IconFontSwing.buildIcon( FontAwesome.FILE_PDF_O, 12f ), 0, KeyEvent.VK_P,
        "Erstellt eine PDF Datei der Tabelle" );
    miExit = WinUtil.createMenuItem( menuFile, "Exit", "Exit",
        MenuItemType.ITEM_PLAIN, ae -> closed(), IconFontSwing.buildIcon( Typicons.DELETE, 12f ), 0,
        "Beendet das Programm" );
    miExit.setAccelerator( KeyStroke.getKeyStroke( "F2" ) );
    miSteuerung = WinUtil.createMenuItem( menuHelp, "Steuerung", "control", MenuItemType.ITEM_PLAIN,
        ae -> new Help( this ),
        IconFontSwing.buildIcon( GoogleMaterialDesignIcons.INFO, 12f ), 0, "Zeigt die Steuerung als Hilfe an." );
    miSteuerung.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_H, ActionEvent.ALT_MASK ) );
    miStatistik = WinUtil.createMenuItem( menuEdit, "Statistik", "Statistik", MenuItemType.ITEM_PLAIN,
        ae -> showStatistics(),
        IconFontSwing.buildIcon( FontAwesome.AREA_CHART, 12f ), 0, KeyEvent.VK_Q, "Zeigt die Statistiken an." );
    miScreenshot = WinUtil.createMenuItem( menuEdit, "Screenshot", "capture", MenuItemType.ITEM_PLAIN,
        ae -> screenshotAction(), IconFontSwing.buildIcon( FontAwesome.CAMERA_RETRO, 12f ),
        0, "Macht einen Screenshot von der Tabelle." );
    miScreenshot.setAccelerator( KeyStroke.getKeyStroke( KeyEvent.VK_F1, ActionEvent.ALT_MASK ) );
    JMenuItem miAdmin = WinUtil.createMenuItem( menuSetting, "Admin", "Admin", MenuItemType.ITEM_PLAIN,
        ae -> new Anmeldung( this ),
        null, 0, "Öffnet die Adminanmeldung" );
    miSettingYear = WinUtil.createMenuItem( menuSetting, "Nur das Jahr beachten", "year", MenuItemType.ITEM_PLAIN,
        ae -> settingYearAction(), IconFontSwing.buildIcon( FontAwesome.UNLOCK, 12f ),
        0, null );
    miSettingMinus = WinUtil.createMenuItem( menuSetting, "Nur die Ausgaben beachten", "year", MenuItemType.ITEM_PLAIN,
        ae -> settingMinusAction(),
        IconFontSwing.buildIcon( FontAwesome.UNLOCK, 12f ),
        0, null );
    this.setJMenuBar( menubar );

    pnlButtonbar = new JPanel();
    pnlButtonbar.setPreferredSize( new Dimension( 120, 0 ) );
    pnlButtonbar.setLayout( null );
    pnlButtonbar.setBackground( Color.RED );
    if ( VerwaltungConfig.DEBUG ) //TODO werden die Buttons noch gebraucht?
      this.add( pnlButtonbar, BorderLayout.WEST );

    btnExit = new MyButton( "Beenden", IconFontSwing.buildIcon( Typicons.DELETE, 16f ) );
    btnExit.addActionListener( ae -> System.exit( 0 ) );
    btnExit.setBounds( 0, 0, 120, 25 );
    pnlButtonbar.add( btnExit );

    btnTabelle = new MyButton( "Tabelle", IconFontSwing.buildIcon( FontAwesome.BARS, 16f ) );
    btnTabelle.setBounds( 0, 30, 120, 25 );
    btnTabelle.addActionListener( ae -> thisMonthAction() );
    pnlButtonbar.add( btnTabelle );

    btnEintrag = new MyButton( "Neuer Eintrag", IconFontSwing.buildIcon( FontAwesome.CALENDAR_PLUS_O, 16f ) );
    btnEintrag.setBounds( 0, 60, 120, 25 );
    btnEintrag.addActionListener( ae -> showEintrag() );
    pnlButtonbar.add( btnEintrag );

    btnStatistik = new MyButton( "Statistik", IconFontSwing.buildIcon( FontAwesome.AREA_CHART, 16f ) );
    btnStatistik.setBounds( 0, 90, 120, 25 );
    btnStatistik.addActionListener( ae -> showStatistics() );
    pnlButtonbar.add( btnStatistik );

    pnlMain = new JPanel();
    pnlMain.setLayout( new BorderLayout() );

    pnlTable = new JPanel();
    pnlTable.setLayout( new BorderLayout() );

    pnlTableControl = new JPanel();
    pnlTableControl.setLayout( new BorderLayout() );
    pnlTableControl.setBackground( Color.YELLOW );
    pnlTableControl.setPreferredSize( new Dimension( 0, 50 ) );

    btnBackward = new JButton( IconFontSwing.buildIcon( Typicons.ARROW_BACK, 32f ) );
    btnBackward.addActionListener( ae -> backwardAction() );
    pnlTableControl.add( btnBackward, BorderLayout.LINE_START );
    btnForward = new JButton( IconFontSwing.buildIcon( Typicons.ARROW_FORWARD, 32f ) );
    btnForward.addActionListener( ae -> forwardAction() );
    pnlTableControl.add( btnForward, BorderLayout.LINE_END );

    JPanel pnlFilter = new JPanel();
    pnlFilter.setLayout( new GridLayout( 1, 0 ) );

    JButton btnThisMonth = new JButton( "Dieser Monat" );
    btnThisMonth.addActionListener( ae -> thisMonthAction() );
    pnlFilter.add( btnThisMonth );

    JButton btnApply = new JButton( "Apply" );
    btnApply.addActionListener( ae -> applyAction() );
    pnlFilter.add( btnApply );

    pnlTableControl.add( pnlFilter, BorderLayout.SOUTH );

    JPanel pnlDateChooser = new JPanel();
    pnlDateChooser.setLayout( new FlowLayout() );

    jmc = new JMonthChooser();
    pnlDateChooser.add( jmc );
    jyc = new JYearChooser();
    pnlDateChooser.add( jyc );
    pnlTableControl.add( pnlDateChooser, BorderLayout.CENTER );

    pnlTable.add( pnlTableControl, BorderLayout.NORTH );

    popupTable = new JPopupMenu();
    JMenuItem miMehr = new JMenuItem( "Mehr", IconFontSwing.buildIcon( Entypo.HELP, 12f ) );
    miMehr.addActionListener( ae -> new MyTextBox( this ) );
    popupTable.add( miMehr );
    popupTable.addSeparator();
    miDelete = new JMenuItem( "Loeschen", IconFontSwing.buildIcon( Iconic.CHECK, 12f ) );
    miDelete.addActionListener( ae -> deleteRow() );
    popupTable.add( miDelete );
    MouseListener popupListener = new PopupListener( popupTable );

    dtm = new DefaultTableModel();
    table = new JTable( dtm );
    table.setAutoResizeMode( JTable.AUTO_RESIZE_ALL_COLUMNS );
    table.setColumnSelectionAllowed( false );
    table.setFont( this.getContentPane().getFont() );
    table.addMouseListener( popupListener );
    table.addKeyListener( new MyKeyListener( this ) );
    table.addMouseListener( new MyMouseListener( this ) );
    table.getActionMap().put( "reload", reloadAction() );
    table.getInputMap().put( KeyStroke.getKeyStroke( "R" ), "reload" );
    table.setDefaultEditor( Object.class, null );
    table.setDefaultRenderer( Object.class, new MyCellRenderer( this ) );
    table.getTableHeader().addMouseMotionListener(
        new MyMouseMotionAdapter()
            .mouseDragged( e -> {
              if ( table.getTableHeader().getDraggedColumn() != null )
                table.getTableHeader().setDraggedColumn( null );
            } )
            .build() );
    JScrollPane sp = new JScrollPane( table,
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED );
    pnlTable.add( sp, BorderLayout.CENTER );
    pnlMain.add( pnlTable, BorderLayout.CENTER );
    this.add( pnlMain, BorderLayout.CENTER );

    pnlStatusbar = new JPanel();
    pnlStatusbar.setPreferredSize( new Dimension( 0, 40 ) );
    pnlStatusbar.setLayout( new BorderLayout() );
    //pnlStatusbar.setBackground( Color.BLUE );
    this.add( pnlStatusbar, BorderLayout.SOUTH );

    lblZeitAnzeige = new JLabel();
    lblZeitAnzeige.setIcon( IconFontSwing.buildIcon( GoogleMaterialDesignIcons.ACCESS_TIME, 16f ) );
    lblZeitAnzeige.setPreferredSize( new Dimension( 100, 40 ) );
    lblZeitAnzeige.setForeground( Color.BLACK );
    lblZeitAnzeige.setBackground( Color.WHITE );
    lblZeitAnzeige.setHorizontalAlignment( SwingConstants.CENTER );
    pnlStatusbar.add( lblZeitAnzeige, BorderLayout.WEST );

    lblStatus.setPreferredSize( new Dimension( 888880, 25 ) );
    lblStatus.setOpaque( true );
    lblStatus.setBackground( Color.DARK_GRAY );
    lblStatus.setForeground( Color.WHITE );
    lblStatus.setBorder( new EmptyBorder( 0, 5, 0, 0 ) );
    pnlStatusbar.add( lblStatus, BorderLayout.CENTER );

    sqlStorage = "SELECT rowid, * FROM verwaltung WHERE Datum > "
        + getMinimum( new Date() )
        + " AND Datum < " + getMaximum( new Date() )
        + " ORDER BY Datum";
    thisMonthAction();
  }

  private void initDataBase()
  {
    try
    {
      if ( connection != null )
        return;
      lblStatus.setText( "Verbindung wird hergestellt..." );
      connection = DriverManager.getConnection( "jdbc:sqlite:" + DB_FILE );
      lblStatus.setText( "Verbindung wurde hergestellt." );
    }
    catch ( Exception ex )
    {
      lblStatus.setText( ex.getMessage() );
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
      lblStatus.setText( ex.getMessage() );
    }
  }

  private void testTableInDatabase()
  {
    try
    {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate(
          "CREATE TABLE IF NOT EXISTS verwaltung ("
              + "Datum LONG NOT NULL  UNIQUE  DEFAULT 0, "
              + "Betrag DOUBLE NOT NULL  DEFAULT 0, "
              + "Marke TEXT NOT NULL DEFAULT '',"
              + "Text TEXT NOT NULL DEFAULT '');" );
    }
    catch ( Exception ex )
    {
      lblStatus.setText( ex.getMessage() );
    }
  }

  private void initFrame()
  {
    SimpleDateFormat sdf = new SimpleDateFormat( "HH:mm" );
    Thread t = new Thread( () -> aktualisiereZeit( sdf ) );
    t.start();
  }

  private void showFrame()
  {
    this.setLocationRelativeTo( null );
    this.setVisible( true );
  }

  private void showDataInTable( String sql )
  {
    this.sqlStorage = sql;
    String[] columnNames = null;
    dtm = new DefaultTableModel();
    try
    {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery( sql );

      if ( !rs.isBeforeFirst() )
      {
        JOptionPane.showMessageDialog( this,
            "Es liegen keine Daten für diesen Zeitraum vor." );
      }

      ResultSetMetaData rsmd = rs.getMetaData();
      columnNames = new String[rsmd.getColumnCount()];

      for ( int i = 1; i <= rsmd.getColumnCount(); i++ )
        columnNames[i - 1] = rsmd.getColumnLabel( i );

      dtm.setColumnIdentifiers( columnNames );

      while ( rs.next() )
      {
        Date d = new Date( rs.getLong( 2 ) );
        dtm.addRow( new Object[] {
            rs.getLong( 1 ),
            String.format( "%s Uhr", sdf.format( d ) ),
            String.format( "%.2f", rs.getDouble( 3 ) ),
            rs.getString( 4 ),
            rs.getString( 5 )
        } );
      }

      rs.close();
    }
    catch ( Exception ex )
    {
      lblStatus.setText( ex.getMessage() );
    }

    table.setModel( dtm );
    table.getSelectionModel().setSelectionInterval( table.getSelectedRow() + 1, table.getSelectedRow() + 1 );
    setTableColumnWidth( table, 0, 120 );
    setTableColumnInvisible( table, 0 );
  }

  private void closeDatabase()
  {
    try
    {
      if ( connection != null && !connection.isClosed() )
      {
        connection.close();
        if ( connection.isClosed() )
          System.out.println( "Datenbank getrennt." );
      }
    }
    catch ( Exception e )
    {
      if ( VerwaltungConfig.DEBUG )
        System.out.println( e.getMessage() );
    }
  }

  private void aktualisiereZeit( SimpleDateFormat sdf )
  {
    while ( true )
    {
      lblZeitAnzeige.setText( sdf.format( new Date() ) );
      try
      {
        Thread.sleep( 1000 );
      }
      catch ( Exception e )
      {
        if ( VerwaltungConfig.DEBUG )
          System.out.println( e.getMessage() );
      }
    }

  }

  private void setTableColumnWidth( JTable t, int c, int w )
  {
    t.getColumnModel().getColumn( c ).setMinWidth( w / 2 );
    t.getColumnModel().getColumn( c ).setMaxWidth( w * 2 );
    t.getColumnModel().getColumn( c ).setPreferredWidth( w );
  }

  private void setTableColumnInvisible( JTable t, int c )
  {
    t.getColumnModel().getColumn( c ).setWidth( 0 );
    t.getColumnModel().getColumn( c ).setMaxWidth( 0 );
    t.getColumnModel().getColumn( c ).setMinWidth( 0 );
    t.getColumnModel().getColumn( c ).setPreferredWidth( 0 );
    t.getColumnModel().getColumn( c ).setResizable( false );
  }

  private void importJSONAction()
  {
    JSONHandler jsh = new JSONHandler( this );
    jsh.importFromJSON();
  }

  private void exportJsonAction()
  {
    JSONHandler jsh = new JSONHandler( this );
    jsh.exportToJSON();
  }

  private void pdfAction()
  {
    PDFHandler pdfh = new PDFHandler( this );
    pdfh.createPdf();
  }

  private void settingMinusAction()
  {
    this.settingMinusisClicked = !this.settingMinusisClicked;
    if ( this.settingMinusisClicked )
      miSettingMinus.setIcon( IconFontSwing.buildIcon( FontAwesome.LOCK, 12f ) );
    else
      miSettingMinus.setIcon( IconFontSwing.buildIcon( FontAwesome.UNLOCK, 12f ) );
  }

  private void settingYearAction()
  {
    this.settingYearisClicked = !this.settingYearisClicked;
    if ( this.settingYearisClicked )
    {
      miSettingYear.setIcon( IconFontSwing.buildIcon( FontAwesome.LOCK, 12f ) );
      this.jmc.setEnabled( false );
    }
    else
    {
      miSettingYear.setIcon( IconFontSwing.buildIcon( FontAwesome.UNLOCK, 12f ) );
      this.jmc.setEnabled( true );
    }
  }

  private void newAction()
  {
    this.setVisible( false );
    new Verwaltung();
  }

  private void backwardAction()
  {
    table.getSelectionModel().setSelectionInterval( table.getSelectedRow() - 1, table.getSelectedRow() - 1 );
  }

  private void forwardAction()
  {
    if ( table.getSelectedRow() == table.getRowCount() - 1 )
      return;
    table.getSelectionModel().setSelectionInterval( table.getSelectedRow() + 1, table.getSelectedRow() + 1 );
  }

  private void deleteRow()
  {
    int i = TableAdapter.deleteSelectedRow( table, connection );
    showDataInTable( getSqlStorage() );
    table.getSelectionModel().setSelectionInterval( i, i );
  }

  private Action reloadAction()
  {
    return new AbstractAction()
    {
      @Override
      public void actionPerformed( ActionEvent e )
      {
        showDataInTable( getSqlStorage() );
      }
    };
  }

  private void applyAction()
  {
    Date date = DateUtil.localDateToUtilDate(
        LocalDate.of( jyc.getYear(), jmc.getMonth() + 1, 1 ) );
    dateStorage = date;
    filterDateInTable( date );
  }

  private void thisMonthAction()
  {
    fillFixwerteForThisMonth();
    filterDateInTable( new Date() );
  }

  private void fillFixwerteForThisMonth()
  {
    try
    {
      Statement stmt = connection.createStatement();
      stmt.executeUpdate(
          "CREATE TABLE IF NOT EXISTS fixwerte ("
              + "Wert DOUBLE NOT NULL  DEFAULT 0,"
              + "Marke TEXT NOT NULL  DEFAULT '',"
              + "Text TEXT NOT NULL DEFAULT '',"
              + "Einnahme INTEGER NOT NULL DEFAULT 0);"
              + "\n"
              + "CREATE UNIQUE INDEX fixwertUnique ON fixwerte(Text);" );
    }
    catch ( Exception ex )
    {
      if ( VerwaltungConfig.DEBUG )
        System.out.println( ex.getMessage() );
    }

    ArrayList<Fixwert> alfw = new ArrayList<Fixwert>();
    try
    {
      Statement stmt = connection.createStatement();
      ResultSet rs = stmt.executeQuery( "SELECT rowid,* FROM fixwerte;" );

      while ( rs.next() )
      {
        alfw.add( new Fixwert(
            rs.getDouble( 2 ),
            rs.getString( 3 ),
            rs.getString( 4 ),
            rs.getInt( 5 ) == 1 ? true : false,
            rs.getLong( 1 ) ) );
      }
      rs.close();
    }
    catch ( Exception ex )
    {
      if ( VerwaltungConfig.DEBUG )
        System.out.println( ex.getMessage() );
    }

    GregorianCalendar gc = new GregorianCalendar();
    gc.setTime( new Date() );
    for ( Fixwert fw : alfw )
    {
      try
      {
        PreparedStatement ps = connection.prepareStatement(
            "INSERT INTO verwaltung VALUES ( ?, ?, ?, ? );" );

        ps.setLong( 1, DateUtil.getTimeStamp( 1, gc.get( Calendar.MONTH ), gc.get( Calendar.YEAR ) ) + fw.getRowid() );
        ps.setDouble( 2, fw.isEinnahme() ? fw.getWert() : fw.getWert() * -1 );
        ps.setString( 3, fw.getMarke() );
        ps.setString( 4, fw.getText() );
        ps.addBatch();

        connection.setAutoCommit( false );
        ps.executeBatch();
        connection.setAutoCommit( true );
      }
      catch ( SQLException e )
      {
        if ( VerwaltungConfig.DEBUG )
          System.out.println( e.getMessage() );
      }
    }
  }

  private void screenshotAction()
  {
    Robot r;
    try
    {
      r = new Robot();
      BufferedImage bi = r
          .createScreenCapture( new Rectangle(
              (int) this.getBounds().getX() + 8 + pnlButtonbar.getWidth(),
              (int) this.getBounds().getY() + menubar.getHeight() + 32 + pnlTableControl.getHeight(),
              (int) this.getBounds().getWidth() - 17 - pnlButtonbar.getWidth(),
              (int) this.getBounds().getHeight() - pnlStatusbar.getHeight() - 39 - menubar.getHeight()
                  - pnlTableControl.getHeight() ) );
      JFileChooser chooser = new JFileChooser( new File( VerwaltungConfig.ROOTPATH ) );
      chooser.setAcceptAllFileFilterUsed( false );
      chooser.setFileFilter( new FileNameExtensionFilter( "PNG", "png" ) );
      File pngFile = null;
      if ( chooser.showSaveDialog( this ) == JFileChooser.APPROVE_OPTION )
      {
        pngFile = new File( chooser.getSelectedFile().getAbsolutePath() + ".png" );
        ImageIO.write( bi, "png", pngFile );
        JOptionPane.showMessageDialog( this,
            "Screenshot wurde unter " + VerwaltungConfig.SCREEN_CAPTURE_PATH + " erstellt." );
      }
    }
    catch ( Exception e )
    {
      if ( VerwaltungConfig.DEBUG )
        System.out.println( e.getMessage() );
    }
  }

  private long getMinimum( Date date )
  {
    return getMinMax( date, 0 );
  }

  private long getMaximum( Date date )
  {
    return getMinMax( date, 1 );
  }

  private long getMinMax( Date date, int flag )
  {
    GregorianCalendar gc = new GregorianCalendar();
    gc.setTime( date );
    int maxDay = gc.getActualMaximum( Calendar.DAY_OF_MONTH );
    int month = gc.get( Calendar.MONTH );
    int year = gc.get( Calendar.YEAR );

    GregorianCalendar gcmax = new GregorianCalendar( year, month, maxDay, 23, 59, 59 );
    if ( this.settingYearisClicked )
      gcmax = new GregorianCalendar( year, 11, 31, 23, 59, 59 );
    long lmax = gcmax.getTimeInMillis();

    GregorianCalendar gcmin = new GregorianCalendar( year, month, gc.getActualMinimum( Calendar.DAY_OF_MONTH ) );
    if ( this.settingYearisClicked )
      gcmin = new GregorianCalendar( year, 0, 1, 0, 0, 0 );
    long lmin = gcmin.getTimeInMillis();

    if ( flag > 0 )
      return lmax;
    else
      return lmin;
  }

  private void filterDateInTable( Date date )
  {
    showDataInTable( "SELECT rowid, * FROM verwaltung WHERE Datum > "
        + getMinimum( date )
        + " AND Datum < " + getMaximum( date )
        + ( this.settingMinusisClicked ? " AND Betrag < 0 " : "" )
        + " ORDER BY Datum" );
  }

  private void closed()
  {
    //TODO Programm sauberer beenden
    System.exit( 0 );
  }

  public JPanel getPnlMain()
  {
    return pnlMain;
  }

  public JPanel getPnlTable()
  {
    return pnlTable;
  }

  public String getSqlStorage()
  {
    return sqlStorage;
  }

  public Date getDateStorage()
  {
    return dateStorage;
  }

  public JTable getTable()
  {
    return table;
  }

  public boolean isSettingYearisClicked()
  {
    return settingYearisClicked;
  }

  public boolean isSettingMinusisClicked()
  {
    return settingMinusisClicked;
  }

  public JLabel getLblStatus()
  {
    return lblStatus;
  }

  public Connection getConnection()
  {
    return this.connection;
  }
}
