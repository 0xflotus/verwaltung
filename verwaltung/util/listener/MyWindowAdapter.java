package verwaltung.util.listener;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.function.Consumer;

public class MyWindowAdapter
{
  static final Consumer<WindowEvent> nullConsumer = null;
  private Consumer<WindowEvent>      closing      = nullConsumer;
  private Consumer<WindowEvent>      closed       = nullConsumer;

  /**
   * führt die Operation aus, die bei windowClosing() ausgeführt werden soll
   * @param closing die Funktion als Lambda-Ausdruck
   * @return sich selbst
   */
  public MyWindowAdapter closing( Consumer<WindowEvent> closing )
  {
    this.closing = closing;
    return this;
  }

  /**
   * führt die Operation aus, die bei windowClosed() ausgeführt werden soll
   * @param closing die Funktion als Lambda-Ausdruck
   * @return sich selbst
   */
  public MyWindowAdapter closed( Consumer<WindowEvent> closed )
  {
    this.closed = closed;
    return this;
  }

  /**
   * schließt den build-Prozess des WindowListeners ab
   * @return einen Windowlistener mit den übergebenen Funktionen
   */
  public WindowListener build()
  {
    return new WindowAdapter()
    {
      @Override
      public void windowClosing( WindowEvent e )
      {
        closing.accept( e );
      }

      @Override
      public void windowClosed( WindowEvent e )
      {
        closed.accept( e );
      }
    };
  }
}
