package reaction;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;
import music.I;
import music.I.Show;

public class Layer extends ArrayList<Show> implements I.Show {

  public String name;
  public static HashMap<String, Layer> byName = new HashMap<>();
  public static Layer ALL = new Layer("ALL");

  public Layer(String name) {
    this.name = name;
    if (!name.equals("ALL")) {
      ALL.add(this);
    }
    byName.put(name, this);
  }

  public static void nuke() {
    for (I.Show lay : ALL) {
      ((Layer) lay).clear();
    }
  }

  public void show(Graphics g) {
    for (I.Show item : this) {
      item.show(g);
    }
  }


}
