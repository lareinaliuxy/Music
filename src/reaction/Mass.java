package reaction;

import java.awt.Graphics;
import music.I;

public abstract class Mass extends Reaction.List implements I.Show {
  public Layer layer;

  public Mass(String layerName) {
    this.layer = Layer.byName.get(layerName);
    if (layer != null) {
      layer.add(this);
    } else {
      System.out.println("Bad Layer Name: " + layerName);
    }
  }

  public void deleteMass() {
    clearAll(); // clears all the reaction both in this list and the byShape map
    layer.remove(this);
  }

  public void show(Graphics g) {}

}
