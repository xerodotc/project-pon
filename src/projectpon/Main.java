package projectpon;

import projectpon.engine.*;
import projectpon.engine.exceptions.InvalidWindowSize;
import projectpon.game.scenes.Loader;
import projectpon.game.scenes.TitleScene;

public class Main {
	public static void main(String[] args) {
		try {
			GameEngine.setDebugOn();
			GameEngine.start(new Loader(new TitleScene(800, 600, true)), 50, "Project Pon");//*/
		} catch (InvalidWindowSize e) {
			e.printStackTrace();
		}
	}
}
