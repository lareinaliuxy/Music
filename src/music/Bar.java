package music;

import graphicsLib.G;
import java.awt.Color;
import java.awt.Graphics;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

public class Bar extends Mass {
  private static final int FAT = 2, RIGHT = 8, LEFT = 8; // These are bits in bar type
  public Sys sys;
  public int x, barType = 0;
  /*
  barType
  0 - normal - single line
  1 - double - thin line  : use for key changes
  2 - thin - fat line : fine line
  4 thru 7 : fat: thin : dots   repeat bar on the right
  8 thru 11 : dots : thin : fat repeat bar on the left
  12: dots : thin :  fat : thin : fat  double repeat bar
   */

  public Bar(Sys sys, int x) {
    super("Back");
    this.sys = sys;
    this.x = x;
    barType = 0;

    addReaction(new Reaction("S-S") { // cycle an existing bar
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM();
        if (Math.abs(x - Bar.this.x) > UC.BAR_TO_MARGIN_SNAP * 2) {
          return UC.NO_BID;
        }
        int y1 = g.vs.yL(), y2 = g.vs.yH();
        int sysTop = Bar.this.sys.yTop(), sysBot = Bar.this.sys.yBot();
        if (y1 < sysTop - 10 || y2 > sysBot + 10) { return UC.NO_BID; }
        G.LoHi margin = Page.PAGE.xMargin;
        if (x < margin.lo || x > margin.hi + 10) {return UC.NO_BID;}
        int d = Math.abs(x - Bar.this.x);
        return (d > UC.BAR_TO_MARGIN_SNAP) ? UC.NO_BID : d;
      }

      @Override
      public void act(Gesture g) {
        Bar.this.cycleType();
      }
    });

    addReaction(new Reaction("DOT") { //Dot the barlines
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y = g.vs.yM();
        if (y < Bar.this.sys.yTop() || y > Bar.this.sys.yBot()) {return UC.NO_BID;}
        int dist = Math.abs(x - Bar.this.x);
        if (dist > 3 * Page.PAGE.sysFmt.maxH) {return UC.NO_BID;}
        return dist;
      }

      @Override
      public void act(Gesture g) {
        if (g.vs.xM() < Bar.this.x)  {Bar.this.toggleLeft();}
        else {Bar.this.toggleRight();}

      }
    });
  }

  public void show(Graphics g) {
    g.setColor(Color.RED);
    int sysTop = sys.yTop(), y1 = 0, y2 = 0;
    boolean justSawBreak = true;
    for (Staff staff : sys.staffs) {
      int staffTop = staff.yTop();
      if (justSawBreak) { y1 = staffTop; }
      y2 = staff.yBot();
      if (!staff.fmt.barContinues) {
        drawLines(g, y1, y2);
      }
      justSawBreak = !staff.fmt.barContinues;
      if (barType > 3) {
        drawDots(g, x, staffTop);
      }
    }
  }

  public void drawLines(Graphics g, int y1, int y2) {
    int H = sys.page.sysFmt.maxH;
    if (barType == 0) {thinBar(g,x, y1, y2);}
    if (barType == 1) {
      thinBar(g, x, y1, y2);
      thinBar(g, x- H, y1, y2);
    }
    if (barType == 2) {
      fatBar(g, x - H, y1, y2, H);
      thinBar(g, x - 2 * H, y1, y2);
    }
    if (barType >= 4) {
      if ((barType & LEFT) != 0) { thinBar(g, x - 2, y1, y2);wings(g, x - 2 * H, y1, y2, -H, H); }
      if ((barType & RIGHT) != 0) {thinBar(g, x + H, y1, y2);wings(g, x + H, y1, y2, H, H);}
      fatBar(g, x - H, y1, y2, H);
    }
  }

  public static void fatBar(Graphics g, int x, int y1, int y2, int dx) {
    g.fillRect(x, y1, dx, y2 - y1);
  }

  public static void thinBar(Graphics g, int x, int y1, int y2) {
    g.fillRect(x, y1, x, y2);
  }

  public static void wings(Graphics g, int x, int y1, int y2, int dx, int dy) {
    g.drawLine(x, y1, x + dx, y1 - dy );
    g.drawLine(x, y2, x + dx, y2 + dy);
  }

  public void drawDots(Graphics g, int x, int top) {
    int H = sys.page.sysFmt.maxH;
    if ((barType & LEFT) != 0) {
      g.fillOval(x - 3 * H, top + 11 * H / 4, H / 2, H / 2);
      g.fillOval(x - 3 * H, top + 19 * H / 4, H / 2, H / 2);
    }
    if ((barType & RIGHT) != 0) {
      g.fillOval(x + 3 * H / 2, top + 11 * H / 4, H / 2, H / 2);
      g.fillOval(x + 3 * H / 2, top + 19 * H / 4, H / 2, H / 2);
    }
  }

  public void cycleType() {
    barType++;
    if (barType > 2) {barType = 0;}
  }

  public void toggleLeft() { barType ^= LEFT;}

  public void toggleRight() { barType ^= RIGHT;}
}
