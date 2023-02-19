import java.awt.*;
import java.awt.image.*;
import java.lang.reflect.InvocationTargetException;
import javax.swing.JPanel;

public class ImagePanel extends JPanel {

  private static final long serialVersionUID = -7523388842376668141L;

  private Canvas        canvas = null;
  private double        initialScale = 1.0;
  private Color         color = null;
  private Image         image = null;
  private BufferedImage buffer = null;

  public ImagePanel() {
    canvas = new Canvas() {
      private static final long serialVersionUID = -4832292154872445719L;

      @Override public void paint(Graphics g) {
        try {
          BufferStrategy strategy = canvas.getBufferStrategy();
          do {
            do {
              g = strategy.getDrawGraphics();
              if (color != null) {
                g.setColor(color);
                g.fillRect(0, 0, getWidth(), getHeight());
              }
              if (image != null) {
                g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
              }
              if (buffer != null) {
                g.drawImage(buffer, 0, 0, getWidth(), getHeight(), null);
              }
              g.dispose();
            } while (strategy.contentsRestored());
            strategy.show();
          } while (strategy.contentsLost());
        } catch (NullPointerException | IllegalStateException e) {
          // can ignore
        }
      }

      @Override public void update(Graphics g) {
        paint(g);
      }
    };
    canvas.setSize(getSize());
    add(canvas);
  }

  public Dimension getCanvasSize() {
    return canvas.getSize();
  }

  public void setCanvasSize(final int width, final int height) {
    var d = getCanvasSize();
    if (d.width == width && d.height == height) {
      return;
    }
    Runnable r = () -> canvas.setSize(width, height);
    if (EventQueue.isDispatchThread()) {
      r.run();
    } else {
      try {
        EventQueue.invokeAndWait(r);
      } catch (InterruptedException | InvocationTargetException ex) {
        // can ignore
      }
    }
  }

  public void renderImage(Image image) {
    if (image == null) {
      return;
    }
    int w = (int) Math.round(image.getWidth(null) * initialScale);
    int h = (int) Math.round(image.getHeight(null) * initialScale);
    setCanvasSize(w, h);
    this.color = null;
    this.image = image;
    canvas.paint(null);
  }

  public void createBuffer() {
    this.canvas.createBufferStrategy(2);
  }

}
