package reaction;

import graphicsLib.G;
import java.awt.Graphics;
import java.io.Serializable;
import java.util.ArrayList;
import music.I.Area;
import music.I.Show;
import music.UC;

public class Ink implements Show {
  public static Buffer BUFFER = new Buffer(); //Singleton design pattern
  public static final int K = UC.NORM_SAMPLE_SIZE; // the num of points we contain in the Ink
//  public static G.VS TEMP = new G.VS(100, 100, 100, 100);
  public Norm norm;
  public G.VS vs;

  public Ink() {
    norm = new Norm();
    vs = BUFFER.bBox.getNewVS();
  }

//  public Ink(){
//    super(K);
//    for (int i = 0; i < K; i++) {
//      points[i].set(BUFFER.points[i * (BUFFER.n - i) / (K -1)]);
//      G.V.T.set(BUFFER.bBox, TEMP);
//      this.transform();
//      G.V.T.set(TEMP, BUFFER.bBox.getNewVS());
//      this.transform();
//    }
//  }



  @Override
  public void show(Graphics g) {
    g.setColor(UC.INK_COLOR);
    norm.drawAt(g, vs);
  }

  // ---------------------------------Norm-------------------------------------
  public static class Norm extends G.PL implements Serializable {
    public static final int K = UC.NORM_SAMPLE_SIZE, MAX = UC.NORM_COORD_MAX;
    public static final G.VS normBox = new G.VS(0,0,MAX, MAX);

    public Norm() {
      super(K);
//      if (BUFFER.n == 0) {
//        System.out.println("Empty Buffer");
//        BUFFER.n = 1;
//      }
      for (int i = 0; i < K; i++) {
        points[i].set(BUFFER.points[i * (BUFFER.n - 1) / (K - 1)]);
      }
      G.V.T.set(BUFFER.bBox, normBox);
      this.transform();
    }

    public Norm(Ink.Norm norm) {
      super(K);
      for (int i = 0; i < K; i++) {points[i].set(norm.points[i]);}
    }

    public void drawAt(Graphics g, G.VS vs) {
      G.V.T.set(normBox, vs);
      for (int i = 1; i < K; i++) {
        g.drawLine(points[i-1].tx(), points[i-1].ty(), points[i].tx(), points[i].ty());
      }
    }

    /**
     * @param norm
     * @return the square of the distance between this and input norm (no need to calculate root since
     * the aim is to compare the distance with other distances
     */
    public int dist(Norm norm) {
      int res = 0;
      for (int i = 0; i < K; i++) {
        int dx = points[i].x - norm.points[i].x, dy = points[i].y - norm.points[i].y;
        res += dx * dx + dy * dy;
      }
      return res;
    }

    public void blend(Norm norm, int nBlend) {
      for (int i = 0; i < K; i++) {
        points[i].blend(norm.points[i], nBlend);
      }
    }

  }

  // ---------------------------------List--------------------------------------
  public static class List extends ArrayList<Ink> implements Show {

    @Override
    public void show(Graphics g) {
      for (Ink ink: this) {
        ink.show(g);
      }
    }
  }

  // ----------------------------------Buffer----------------------------------
  public static class Buffer extends G.PL implements Show, Area {
    public int n; //num of points in the buffer
    public G.BBox bBox = new G.BBox();
    public static  final int MAX = UC.INK_BUFFER_MAX;


    private Buffer() {
      super(MAX);
    }

    public void add(int x, int y) {
      if (n < MAX) {
        points[n++].set(x, y);
        bBox.add(x, y);
      }
    }

    public void clear() {
      n = 0;
    }

    @Override
    public boolean hit(int x, int y) {
      return true;
    }

    @Override
    public void dn(int x, int y) {
      clear();
      bBox.set(x, y);
      add(x, y);
    }

    @Override
    public void drag(int x, int y) {
      add(x, y);
    }

    @Override
    public void up(int x, int y) {}

    @Override
    public void show(Graphics g) {
      drawN(g, n);
//      bBox.draw(g);
    }
  }

}
