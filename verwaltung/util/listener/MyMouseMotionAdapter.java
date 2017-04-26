package verwaltung.util.listener;

import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseMotionListener;
import java.util.function.Consumer;

public class MyMouseMotionAdapter
{
  private static final Consumer<MouseEvent> nullConsumer = null;
  private Consumer<MouseEvent>              mouseDragged = nullConsumer;

  /**
   * führt die Operation aus, die bei mouseDragged() passieren soll
   * @param mouseDragged eine Funktion, die ausgeführt werden soll als Lambda-ausdrucken
   * @return sich selbst
   */
  public MyMouseMotionAdapter mouseDragged( Consumer<MouseEvent> mouseDragged )
  {
    this.mouseDragged = mouseDragged;
    return this;
  }

  /**
   * Schließt den build-Prozess ab
   * @return einen MouseMotionListener mit den Funktionen
   */
  public MouseMotionListener build()
  {
    return new MouseMotionAdapter()
    {
      @Override
      public void mouseDragged( MouseEvent e )
      {
        mouseDragged.accept( e );
      }
    };
  }
}
