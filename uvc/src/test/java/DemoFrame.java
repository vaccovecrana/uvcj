import javax.swing.*;
import java.awt.*;

public class DemoFrame extends javax.swing.JFrame {

  private static final long serialVersionUID = 7534330708637208215L;
  private final ImagePanel imgPanel;

  public DemoFrame() {
    super(DemoFrame.class.getSimpleName());

    imgPanel = new ImagePanel();
    setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    getContentPane().setLayout(new GridLayout(1, 1));

    var gl = new GroupLayout(imgPanel);
    imgPanel.setLayout(gl);
    gl.setHorizontalGroup(
      gl.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGap(0, 576, Short.MAX_VALUE));
    gl.setVerticalGroup(
      gl.createParallelGroup(GroupLayout.Alignment.LEADING)
        .addGap(0, 445, Short.MAX_VALUE)
    );
    getContentPane().add(imgPanel);
    pack();
    setVisible(true);
    imgPanel.createBuffer();
  }

  public void showImage(Image img) {
    imgPanel.renderImage(img);
  }

}
