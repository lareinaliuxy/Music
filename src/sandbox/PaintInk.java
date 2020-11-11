package sandbox;

import graphicsLib.G;
import graphicsLib.Window;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import reaction.Ink;
import reaction.Ink.List;
import music.UC;
import reaction.Shape;
import reaction.Shape.Prototype;

public class PaintInk extends Window {

  public static Ink.List inkList = new List();
  public static Shape.Prototype.List pList = new Prototype.List();
//  static { inkList.add(new Ink());}
  public static String recognized = "";


  public PaintInk(){
    super("PaintInk", UC.WINDOW_WIDTH,UC.WINDOW_HEIGHT);
  }

  @Override
  public void paintComponent(Graphics g){
    G.fillBack(g);
    inkList.show(g);
    Ink.BUFFER.show(g);
    if (inkList.size() > 1) {
      int last = inkList.size() - 1;
      int dist = inkList.get(last).norm.dist(inkList.get(last-1).norm);
      g.setColor(dist > UC.NO_MATCH_DIST ? Color.RED : Color.BLACK);
      g.drawString("Distance: " + dist, 600, 60);
    }
    pList.show(g);
    g.drawString(recognized, 700, 40);
  }

  public void mousePressed(MouseEvent me) {
    Ink.BUFFER.dn(me.getX(), me.getY());
    repaint();
  }

  public void mouseDragged(MouseEvent me) {
    Ink.BUFFER.drag(me.getX(), me.getY());
    repaint();
  }

  public void mouseReleased(MouseEvent me) {
    Ink ink = new Ink();
    Shape s = Shape.recognize(ink);
    recognized = "recognized: " + ((s != null) ? s.name : "unrecognized");
    Shape.Prototype proto;

    inkList.add(ink);
    if (pList.bestDist(ink.norm) < UC.NO_MATCH_DIST) {
      proto = pList.bestMatch;
      proto.blend(ink.norm);
    } else{
      proto = new Shape.Prototype(ink.norm);
      pList.add(proto);
    }
    ink.norm = proto;
    repaint();
  }
}
