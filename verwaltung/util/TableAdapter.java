package verwaltung.util;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.swing.JTable;

public class TableAdapter
{
  /**
   * Löscht einen Wert in der DB
   * @param table Die Tabelle auf die sich bezogen wird
   * @param conn die Connection zur DB
   */
  public static int deleteSelectedRow( JTable table, Connection conn )
  {
    int row = table.getSelectedRow();
    if ( row > -1 )
    {
      try
      {
        Statement stmt = conn.createStatement();
        stmt.executeUpdate( "DELETE FROM verwaltung WHERE rowid = '"
            + table.getModel().getValueAt( row, 0 ) + "'" + ";" );
        stmt.close();
        return row - 1;
      }
      catch ( SQLException e )
      {
        System.out.println( e.getMessage() );
      }
    }
    return 0;
  }
}
