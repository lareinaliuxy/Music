package music;

import java.awt.Graphics;
import java.util.ArrayList;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

public class Head extends Mass {

  public Staff staff;
  public int line;
  public Time time;
  public Glyph forcedGlyph = null;
  public Stem stem = null;
  public boolean wrongSide = false;


  public Head(Staff staff, int x, int y) {
    super("NOTE");  //note layer
    this.staff = staff;
    this.time = staff.sys.getTime(x);
    time.heads.add(this);
    line = staff.lineOfY(y);

    addReaction(new Reaction("S-S") { // stem or unstem heads
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        int w = Head.this.w(), yH = Head.this.y();
        if (y1 > yH || y2 < yH) { return UC.NO_BID; }
        int hL = Head.this.time.x, hR = hL + w;
        if (x < hL - w || x > hR + w) { return UC.NO_BID; }
        if (x < hL + w / 2) {return hL - x;}
        if (x > hR - w / 2) {return x - hR;}
        return UC.NO_BID;
      }

      @Override
      public void act(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        Staff staff = Head.this.staff;
        Time t = Head.this.time;
        int w = Head.this.w();
        boolean up = x > t.x + w / 2;
        if (Head.this.stem == null) {
          t.stemHead(staff, up, y1, y2);
        } else {
          t.unStemHead(y1, y2);
        }
      }
    });
  }

  public void show(Graphics g) {
    int h = staff.H();
    (forcedGlyph != null ? forcedGlyph : normalGlyph())
        .showAt(g, h, time.x, staff.yTop() + line * h);
  }

  public void unStem() {
    if (stem != null) {
      stem.heads.remove(this);
      if (stem.heads.size() == 0) {
        stem.deleteStem();
      }
      stem = null;
      wrongSide = false;
    }
  }

  public void  joinStem(Stem s) {
    if (stem != null) {
      unStem();
    }
    s.heads.add(this);
    stem = s;
  }

  public Glyph normalGlyph() {
    return Glyph.HEAD_Q; // this is a stub
  }

  public int y() {return staff.yLine(line);}

  public int x() {return time.x;} // this is a stub

  public void deleteMass() {time.heads.remove(this);} // this is a stub

  public int w() {
    return 24 * staff.H() / 10;
  }

  // ---------------------------------------List---------------------------------------------------
  public static class List extends ArrayList<Head> {

  }


}
