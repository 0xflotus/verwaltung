package verwaltung.util.listener;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.Connection;

import javax.swing.JTable;

import verwaltung.util.TableAdapter;
import verwaltung.view.Verwaltung;

public class MyKeyListener extends KeyAdapter
{
  private Connection connection;
  private Verwaltung verw;

  public MyKeyListener( Verwaltung verw )
  {
    this.connection = verw.getConnection();
    this.verw = verw;
  }

  @Override
  public void keyPressed( KeyEvent e )
  {
    if ( e.getSource() instanceof JTable )
    {
      JTable table = (JTable) e.getSource();
      if ( e.getKeyCode() == KeyEvent.VK_DELETE )
      {
        TableAdapter.deleteSelectedRow( table, connection );
        verw.showTable();
      }
      if ( e.getKeyCode() == KeyEvent.VK_RIGHT )
        table.getSelectionModel().setSelectionInterval( table.getSelectedRow() + 10, table.getSelectedRow() + 10 );
      if ( e.getKeyCode() == KeyEvent.VK_LEFT )
      {
        if ( table.getSelectedRow() < 9 )
          table.getSelectionModel().setSelectionInterval( 0, 0 );
        else
          table.getSelectionModel().setSelectionInterval( table.getSelectedRow() - 10, table.getSelectedRow() - 10 );
      }
    }
  }
}
