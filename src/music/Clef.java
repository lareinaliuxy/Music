package music;

import java.awt.Graphics;

public class Clef {
  public static Clef G = new Clef(Glyph.CLEF_G), F = new Clef(Glyph.CLEF_F);
  public Glyph clef;

  private Clef(Glyph glyph) {
    this.clef = glyph;
  }

  public void showAt(Graphics g, int x, int yTop, int H) {
    clef.showAt(g, H, x, yTop + 4 * H);
  }

}
