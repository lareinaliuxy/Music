package music;

import graphicsLib.G;
import java.awt.Color;
import java.awt.Graphics;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

public class Staff extends Mass {

  public Sys sys;
  public int iStaff;
  public Staff.Fmt fmt;
  public Clef initialClef;

  public Staff(Sys sys, int iStaff) {
    super("BACK");
    this.sys = sys;
    this.iStaff = iStaff;
    fmt = sys.page.sysFmt.get(iStaff);


    addReaction(new Reaction("S-S") { // adding bar
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        G.LoHi margin = Page.PAGE.xMargin;
        if (x < margin.lo || x > (margin.hi + UC.BAR_TO_MARGIN_SNAP)) { return UC.NO_BID; }
        int d = Math.abs(y1 - Staff.this.yTop()) + Math.abs(y2 - Staff.this.yBot());
        return (d < 30) ? d + UC.BAR_TO_MARGIN_SNAP : UC.NO_BID; // To indicate the bias to prefer a bar cycle gesture
      }

      @Override
      public void act(Gesture g) {
        int rightMargin = Page.PAGE.xMargin.hi;
        int x = g.vs.xM();
        if (x > rightMargin - UC.BAR_TO_MARGIN_SNAP) { x = rightMargin;}
        new Bar(Staff.this.sys, x);

      }
    });

    addReaction(new Reaction("S-S") {   //TOGGLE BAR CONTINUES
      @Override
      public int bid(Gesture g) {
        if (Staff.this.sys.iSys != 0) { return UC.NO_BID;}
        int y1 = g.vs.yL(), y2 = g.vs.yH();
        if (iStaff == Page.PAGE.sysFmt.size() - 1) {return UC.NO_BID;}
        if (Math.abs(y1 - Staff.this.yBot())  > 20) {return UC.NO_BID;}
        Staff nextStaff = Staff.this.sys.staffs.get(iStaff + 1);
        if (Math.abs(y2 - nextStaff.yTop()) > 20) {return UC.NO_BID;}
        return 10;
      }

      @Override
      public void act(Gesture g) {
        Page.PAGE.sysFmt.get(Staff.this.iStaff).toggleBarContinues();
      }
    });

    addReaction(new Reaction("SE-SW") {  // F Clef
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        G.LoHi m = Page.PAGE.xMargin;
        if (x < m.lo || x > m.hi) {return UC.NO_BID;}
        int d = Math.abs(y1 - Staff.this.yTop()) + Math.abs(y2 - Staff.this.yBot());
        if (d > 50) {return UC.NO_BID;}
        return d;
      }

      @Override
      public void act(Gesture g) {
        initialClef = Clef.F;
      }
    });

    addReaction(new Reaction("SW-SE") {  // G Clef
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y1 = g.vs.yL(), y2 = g.vs.yH();
        G.LoHi m = Page.PAGE.xMargin;
        if (x < m.lo || x > m.hi) {return UC.NO_BID;}
        int d = Math.abs(y1 - Staff.this.yTop()) + Math.abs(y2 - Staff.this.yBot());
        if (d > 50) {return UC.NO_BID;}
        return d;
      }

      @Override
      public void act(Gesture g) {
        initialClef = Clef.G;
      }
    });

    addReaction(new Reaction("SW-SW") { // adding a note head
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xM(), y = g.vs.yM();
        if (x < Page.PAGE.xMargin.lo || x > Page.PAGE.xMargin.hi) {return UC.NO_BID;}
        int h = Staff.this.H(), top = Staff.this.yTop() - h, bot = Staff.this.yBot();
        if (y < top || y > bot) {return UC.NO_BID;}
        return 10;
      }

      @Override
      public void act(Gesture g) {
        new Head(Staff.this, g.vs.xM(), g.vs.yM());

      }
    });

    addReaction(new Reaction("W-S") { // add quarter rest
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xL(), y = g.vs.yM();
        if (x < Page.PAGE.xMargin.lo || x > Page.PAGE.xMargin.hi) {return UC.NO_BID;}
        int h = Staff.this.H(), top = Staff.this.yTop() - h, bot = Staff.this.yBot() + h;
        if (y < top|| y > bot) {return UC.NO_BID;}
        return 10;
      }

      @Override
      public void act(Gesture g) {
        Time t = Staff.this.sys.getTime(g.vs.xL());
        new Rest(Staff.this, t);
      }
    });

    addReaction(new Reaction("E-S") { // add eight rest
      @Override
      public int bid(Gesture g) {
        int x = g.vs.xL(), y = g.vs.yM();
        if (x < Page.PAGE.xMargin.lo || x > Page.PAGE.xMargin.hi) {return UC.NO_BID;}
        int h = Staff.this.H(), top = Staff.this.yTop() - h, bot = Staff.this.yBot() + h;
        if (y < top|| y > bot) {return UC.NO_BID;}
        return 10;
      }

      @Override
      public void act(Gesture g) {
        Time t = Staff.this.sys.getTime(g.vs.xL());
        new Rest(Staff.this, t).nFlag = 1;

      }
    });



  }


  public int sysOff() {return sys.page.sysFmt.staffOffsets.get(iStaff);}

  public int yTop() {return sys.yTop() + sysOff();}

  public int yBot() {return sys.yTop() + fmt.height();}

  public int H() {return fmt.H;}

  public int yLine(int line) {return yTop() + line * H();}

  public int lineOfY(int y) {
    int h = H();
    int BIAS = 100;
    int top = yTop() - h * BIAS;
    return (y - top + h / 2 ) / h - BIAS;

  }

  public void show(Graphics g) {
    g.setColor(Color.BLUE);
    if (initialClef != null) {initialClef.showAt(g, Page.PAGE.xMargin.lo + 3 * fmt.H, yTop(), fmt.H);}
  }

  // -----------------------------------Staff.Fmt---------------------------------------------------

  public static class Fmt {  // keeping track of the info to draw a staff
    public int nLines = 5;
    public int H = 0; // half the spacing between two lines
    public boolean barContinues = false;

    public void toggleBarContinues() {
      barContinues = !barContinues;
    }

    public int height() {return 2 * H * (nLines - 1);}

    public void showAt(Graphics g, int y, Page page) {
      g.setColor(Color.GRAY);
      int x1 = page.xMargin.lo, x2 = page.xMargin.hi, h = 2 * H;
      for (int i = 0; i < nLines; i++) {
        g.drawLine(x1, y, x2, y);
        y += h;
      }
    }

  }

}
