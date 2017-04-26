package verwaltung.util.listener;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import verwaltung.view.MyTextBox;
import verwaltung.view.Verwaltung;

public class MyMouseListener extends MouseAdapter
{
  private Verwaltung verw;

  public MyMouseListener( Verwaltung verwaltung )
  {
    this.verw = verwaltung;
  }

  @Override
  public void mouseClicked( MouseEvent e )
  {
    if ( e.getClickCount() > 1 )
      new MyTextBox( verw );
  }

}
