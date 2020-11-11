package music;

import graphicsLib.G;
import graphicsLib.G.LoHi;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import music.Sys.Fmt;
import reaction.Gesture;
import reaction.Mass;
import reaction.Reaction;

public class Page extends Mass {
  public static Page PAGE;
  public G.LoHi xMargin, yMargin;
  public Sys.Fmt sysFmt = new Sys.Fmt();
  public int sysGap; // size of space between 2 systems
  public ArrayList<Sys> sysList = new ArrayList<>();
  public static Reaction r1;

  public Page(int y) {
    super("BACK");
    PAGE = this;
    int mm = 50;
    xMargin = new LoHi(mm, UC.WINDOW_WIDTH - mm);
    yMargin = new LoHi(y, UC.WINDOW_HEIGHT - mm);
    // TODO: disable initial reaction, add staff & system, create two new reactions
    Reaction.initialReactions.get(0).disable();
    addNewStaffFmtToSysFmt(y);
    addNewSys();

    addReaction(r1 = new Reaction("E-W") { // add a new staff to sys format
      @Override
      public int bid(Gesture g) { return g.vs.yM() < PAGE.allSysBot() ? UC.NO_BID:0; }

      @Override
      public void act(Gesture g) { addNewStaffFmtToSysFmt(g.vs.yM()); }
    });

    addReaction(new Reaction("W-W") { // add a system
      @Override
      public int bid(Gesture g) { return g.vs.yM() < PAGE.allSysBot() ? UC.NO_BID:0; }

      @Override
      public void act(Gesture g) {
        if (PAGE.sysList.size() == 1) {
          PAGE.sysGap = g.vs.yM() - PAGE.allSysBot();
          r1.disable();
        }
        addNewSys();
      }
    });
  }

  public void show(Graphics g) {
    g.setColor(Color.RED);
    g.drawLine(0, yMargin.lo, 30, yMargin.lo);
    for (int i = 0; i < sysList.size(); i++) {
      sysFmt.showAt(g, sysTop(i), this);
    }
  }

  public void addNewStaffFmtToSysFmt(int y) {
    int yOff = y - yMargin.lo;
    int iStaff = sysFmt.size();
    sysFmt.addNew(yOff);
    for (Sys sys : sysList) {  // update all systems to match sys format
      sys.addNewStaff(iStaff);
    }
  }

  public void addNewSys() {
    Sys sys = new Sys(this, sysList.size());
    sysList.add(sys);
    for (int i = 0; i < sysFmt.size(); i++) {
      sys.addNewStaff(i);
    }
  }

  public int sysTop(int iSys) {
    return yMargin.lo + iSys * (sysFmt.height() + sysGap);
  }

  public int allSysBot() {
    int n = sysList.size();
    return yMargin.lo + n * sysFmt.height() + (n - 1) * sysGap;
  }
}
