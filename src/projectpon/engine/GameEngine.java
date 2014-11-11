package projectpon.engine;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.swing.JOptionPane;

public final class GameEngine {
	private static GameScene scene = null; // current scene
	private static GameScene nextScene = null; // next scene
	private static GameWindow frame; // engine game window
	
	public static int windowWidth = 0; // store current window width
	public static int windowHeight = 0; // store current window height
	
	private static boolean gameExit = false; // game exit flag
	// disable exit confirmation on closing window?
	private static boolean gameExitOnCloseDisabled = false;
	private static int updatesPerSecond = 0; // updates per second
	
	// default background color
	public static final Color DEFAULT_BACKGROUND_COLOR = Color.BLACK;
	private static boolean debug = false; // debug mode flag
	
	protected static long startTime; // store game engine start time
	
	private static boolean paused = false; // pause flag
	
	/**
	 * Main window class for game engine
	 */
	public static class GameEngineMainWindow extends GameWindow {
		private static final long serialVersionUID = -8516159275209621968L;

		public GameEngineMainWindow(String title) {
			super(title, DO_NOTHING_ON_CLOSE);
		}
		
		@Override
		public void windowActivated(WindowEvent arg0) {
			super.windowActivated(arg0);
			if (childWindow != null && scene != null) {
				scene.requestFocus();
			}
		}
		
		@Override
		public void windowClosing(WindowEvent arg0) {
			if (!gameExitOnCloseDisabled) {
				boolean gameWasPaused = paused;
				if (!gameWasPaused) {
					pause();
				}
				int status = JOptionPane.showConfirmDialog(frame,
						"Exit game?", "Exit game?", 
						JOptionPane.YES_NO_OPTION);
				if (status == JOptionPane.YES_OPTION) {
					exit();
				}
				if (!gameWasPaused) {
					unpause();
				}
			}
		}
		
		@Override
		public void childWindowOpened() {
			pause();
		}
		
		@Override
		public void childWindowClosed() {
			unpause();
		}
	}
	
	/**
	 * Prevent instance initialization
	 */
	private GameEngine() {
	}
	
	/**
	 * Start game engine
	 * 
	 * @param startScene		Starting scene
	 * @param updatesPerSec		Updates per second
	 */
	public static void start(GameScene startScene, int updatesPerSec) {
		start(startScene, updatesPerSec, "");
	}
	
	/**
	 * Start game engine
	 * 
	 * @param startScene		Starting scene
	 * @param updatesPerSec		Updates per second
	 * @param title				Window title
	 */
	public static void start(GameScene startScene, int updatesPerSec, String title) {
		updatesPerSecond = updatesPerSec;
		frame = new GameEngineMainWindow(title);
		GameImage.init();
		GameSound.init();
		GameFont.init();
		replaceScene(startScene);
		EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				frame.setVisible(true);
			}
		});
		GameInput.bindLocalInput(frame);
		startTime = new Date().getTime(); // set start time
		update(); // start updating
	}
	
	/**
	 * Game engine update loop
	 */
	public static void update() {
		while (!gameExit) {
			if (paused) {
				try {
					Thread.sleep(Math.round(1000.0 / updatesPerSecond));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			
			GameInput.updateAllInputs(); // update input data
			
			scene.objectsUpdate(); // update scene
			scene.repaint(); // draw scene
			
			try {
				Thread.sleep(Math.round(1000.0 / updatesPerSecond));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			if (nextScene != null) {
				replaceScene(nextScene); // go to next scene, if next scene is set
				nextScene = null;
			}
			
			if (paused) {
				GameInput.clearAllInputs();
			}
		}
		
		if (scene != null) {
			scene.exit();
		}
		System.exit(0);
	}
	
	/**
	 * Launch window
	 */
	public static void launchWindow(GameWindow w) {
		frame.launchChildWindow(w);
	}
	
	/**
	 * Exit the game
	 */
	public static void exit() {
		gameExit = true;
	}
	
	/**
	 * Replace the game scene
	 * 
	 * @param newScene	New game scene	
	 */
	public static void replaceScene(GameScene newScene) {
		if (scene != null) {
			scene.exit();
			frame.remove(scene);
		}
		scene = newScene;
		scene.initialize();
		if (scene != null) {
			frame.add(scene);
			frame.pack();
		}
	}
	
	/**
	 * Set next (after update) game scene
	 * 
	 * @param newScene		Next game scene
	 */
	public static void setScene(GameScene newScene) {
		nextScene = newScene;
	}
	
	/**
	 * Pause the game engine
	 */
	public static void pause() {
		paused = true;
	}
	
	/**
	 * Unpause the game engine
	 */
	public static void unpause() {
		paused = false;
	}
	
	/**
	 * Is the game engine paused
	 */
	public static boolean paused() {
		return paused;
	}
	
	/**
	 * Disable game exit on window close
	 */
	public static void disableExitOnClose() {
		gameExitOnCloseDisabled = true;
	}
	
	/**
	 * Enable game exit on window close
	 */
	public static void enableExitOnClose() {
		gameExitOnCloseDisabled = false;
	}
	
	/**
	 * Is game exit on window close
	 */
	public static boolean isExitOnClose() {
		return !gameExitOnCloseDisabled;
	}
	
	/**
	 * Get updates per second
	 * 
	 * @return		Updates per second
	 */
	public static int getUpdatesPerSec() {
		return updatesPerSecond;
	}
	
	/**
	 * Get game engine elapsed time in milliseconds
	 * 
	 * @return		Elapsed time
	 */
	public static long getElapsedTimeMs() {
		return new Date().getTime() - startTime;
	}
	
	/**
	 * Turn on debug flag
	 */
	public static void setDebugOn() {
		debug = true;
	}
	
	/**
	 * Is debug flag set?
	 * 
	 * @return		True if debug flag is set
	 */
	public static boolean isDebugOn() {
		return debug;
	}
	
	/**
	 * Some utility class
	 */
	public static class Util {
		/**
		 * Convert updates count to milliseconds
		 * 
		 * @param updates	Updates count
		 * @return			Time elapsed in milliseconds
		 */
		public static int updatesToMs(int updates) {
			if (GameEngine.getUpdatesPerSec() <= 0) {
				return 0;
			}
			return updates * 1000 / GameEngine.getUpdatesPerSec();
		}
	}
}
