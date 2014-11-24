/**
 * GameEngine.java
 * 
 * Main game engine class
 * 
 * @author Visatouch Deeying [5631083121]
 */
package projectpon.engine;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Date;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public final class GameEngine {
	private static GameScene scene = null; // current scene
	private static GameScene nextScene = null; // next scene
	private static MainWindow frame; // engine game window
	private static MainComponent component; // internal component
	
	public static int windowWidth = 800; // store current window width
	public static int windowHeight = 600; // store current window height
	
	private static boolean gameExit = false; // game exit flag
	
	// disable exit confirmation on closing window?
	private static boolean gameExitOnCloseDisabled = false;
	
	private static int updatesPerSecond = 0; // updates per second
	
	// default background color
	public static final Color DEFAULT_BACKGROUND_COLOR = Color.BLACK;
	private static boolean debug = false; // debug mode flag
	
	protected static long startTime; // store game engine start time
	
	private static boolean paused = false; // pause flag
	private static Object pausedLock = new Object(); // pause flag lock object
	
	/**
	 * Main window class for game engine
	 */
	private static final class MainWindow extends JFrame implements GameWindow {
		private static final long serialVersionUID = -8516159275209621968L;
		private GameDialog childDialog;

		public MainWindow(String title) {
			super(title);
			
			this.setResizable(false);
			this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			
			component = new MainComponent();
			
			this.addWindowListener(new WindowAdapter() {
				@Override
				public void windowActivated(WindowEvent arg0) {
					if (childDialog != null) {
						component.requestFocus();
					}
				}
				
				@Override
				public void windowClosing(WindowEvent arg0) {
					// confirm exit, if exit on close is enabled
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
			});
			
			this.addFocusListener(new FocusAdapter() {
				@Override
				public void focusGained(FocusEvent arg0) {
					component.requestFocus();
				}
			});
			
			this.add(component);
			this.pack();
			component.requestFocus();
		}
		
		@Override
		public void childWindowOpened() {
			pause(); // pause the game on child window opened
		}
		
		@Override
		public void childWindowClosed() {
			this.childDialog = null;
			unpause(); // unpause the game on child window closed
		}

		@Override
		public void setParentWindow(GameWindow w) {
			// shouldn't be happening
			return;
		}

		@Override
		public void launchChildWindow(GameWindow w) {
			if (w instanceof GameDialog) {
				this.childDialog = (GameDialog) w;
				GameWindowUtil.launchChildWindow(this, w);
			}
		}
		
		public void launchChildDialog(GameDialog w) {
			launchChildWindow(w);
		}
	}
	
	/**
	 * Internal component for rendering main game
	 */
	private static class MainComponent extends JComponent {
		private static final long serialVersionUID = -4001078712599671771L;
		
		public MainComponent() {
			this.setPreferredSize(new Dimension(windowWidth, windowHeight));
			this.validate();
			this.setDoubleBuffered(true);
			this.setOpaque(true);
		}
		
		@Override
		protected void paintComponent(Graphics canvas) {
			/*
			 * Render the scene, if applicable
			 * otherwise, render black screen
			 */
			
			super.paintComponent(canvas);
			if (scene != null) {
				scene.paintComponent(canvas);
			} else {
				this.setBackground(Color.BLACK);
				((Graphics2D) canvas).clearRect(0, 0, 
						windowWidth, windowHeight);
			}
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
		frame = new MainWindow(title);
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
		GameInput.bindLocalInput(component); // bind local input to the game
		startTime = new Date().getTime(); // set start time
		update(); // start updating
	}
	
	/**
	 * Game engine update loop
	 */
	public static void update() {
		while (!gameExit) {
			synchronized (pausedLock) {
				while (paused) {
					try {
						pausedLock.wait(); // wait for unpause
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			
			GameInput.updateAllInputs(); // update input data
			
			scene.objectsUpdate(); // update scene
			component.repaint(); // draw scene
			
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
				GameInput.clearAllInputs(); // clear all input if paused
			}
		}
		
		if (scene != null) {
			scene.exit(); // call exit scene event
		}
		
		System.exit(0); // quit the program
	}
	
	/**
	 * Launch dialog
	 */
	public static void launchDialog(GameDialog w) {
		frame.launchChildDialog(w);
	}
	
	/**
	 * Get main game frame
	 */
	public static JFrame getFrame() {
		return frame;
	}
	
	/**
	 * Set main window resolution
	 */
	public static void setResolution(int width, int height) {
		if (width > 0 && height > 0) {
			windowWidth = width;
			windowHeight = height;
		}
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
	private static void replaceScene(GameScene newScene) {
		if (scene != null) {
			scene.exit(); // call the exit scene event
		}
		scene = newScene;
		scene.initialize();
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
		synchronized (pausedLock) {
			pausedLock.notifyAll(); // notify the waited game loop
		}
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
		 * Prevent instance initialization
		 */
		private Util() {
		}
		
		/**
		 * Convert updates count to milliseconds
		 * 
		 * @param updates	Updates count
		 * @return			Time elapsed in milliseconds
		 */
		public static int updatesToMs(int updates) {
			if (updatesPerSecond <= 0) {
				return 0;
			}
			return updates * 1000 / updatesPerSecond;
		}
	}
}
