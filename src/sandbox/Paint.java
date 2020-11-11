package sandbox;

import graphicsLib.G;
import graphicsLib.Window;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

public class Paint extends Window {
  public static int clickes = 0;

  public static Path thePath = new Path();

  public static Path.Pic thePic = new Path.Pic();

  public Paint(){
    super("paint", 1000, 600);
  }


  @Override
  public void paintComponent(Graphics g){
    G.fillBack(g);
    g.setColor(G.rndColor());
    g.fillOval(100,100, 200,300);
    g.setColor(Color.BLACK);
    g.drawLine(100, 500, 500, 100);
    int x = 400, y = 200;
    String str = "dude this is totally sweet " + clickes;
    g.drawString(str, x, y);
    g.setColor(Color.RED);
    g.drawOval(x,y, 2,2);
    FontMetrics fm = g.getFontMetrics();
    int a = fm.getAscent(), d = fm.getDescent();
    int w = fm.stringWidth(str);
    g.drawRect(x, y-a, w, a+d);
    g.setColor(Color.BLACK);
    thePic.draw(g);
    g.setColor(Color.RED);
    thePath.draw(g);
  }

  @Override
  public void mousePressed(MouseEvent me) {
    clickes++;
    thePath = new Path();
    thePath.add(me.getPoint());
    thePic.add(thePath);
    repaint();
  }

  @Override
  public void mouseDragged(MouseEvent me) {
    thePath.add(me.getPoint());
    repaint();
  }

  // ------------------------------Path---------------------------------------

  public static class Path extends ArrayList<Point> {
    public void draw(Graphics g) {
      for(int i = 1; i < size(); i++) {
        Point p = get(i-1), n = get(i);
        g.drawLine(p.x, p.y, n.x, n.y);

      }
    }
    // ----------------------------List----------------------------------------
    public static class Pic extends ArrayList<Path> {
      public void draw(Graphics g){
        for(Path p:this) {
          p.draw(g);
        } // draw a pic to the screen
      }
    }
  }
}
