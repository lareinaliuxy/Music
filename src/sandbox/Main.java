package sandbox;

import graphicsLib.Window;
import reaction.ShapeTrainerApplication;

public class Main {
  public static void main(String[] args) {
//    Window.PANEL = new Paint();
//    Window.PANEL = new Squares();
//    Window.launch();
//    Window.PANEL=new PaintInk();
    Window.PANEL = new ShapeTrainerApplication();
//    Window.PANEL = new ReactionTest();
//    Window.PANEL = new MusicOne();
    Window.launch();
  }
}
