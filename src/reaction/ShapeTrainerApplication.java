package reaction;


import graphicsLib.Window;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import music.UC;


public class ShapeTrainerApplication extends Window {
  public static Shape.Trainer TRAINER = new Shape.Trainer();

  public ShapeTrainerApplication() {
    super("ShapeTrainer", UC.WINDOW_WIDTH, UC.WINDOW_HEIGHT);
  }

  public void paintComponent(Graphics g) { TRAINER.show(g); }


  public void keyTyped(KeyEvent ke) { TRAINER.keyTyped(ke.getKeyChar()); repaint(); }

  public void mousePressed(MouseEvent me) { TRAINER.dn(me.getX(), me.getY()); repaint(); }

  public void mouseDragged(MouseEvent me) { TRAINER.drag(me.getX(), me.getY()); repaint(); }

  public void mouseReleased(MouseEvent me) { TRAINER.up(me.getX(), me.getY()); repaint(); }
}
