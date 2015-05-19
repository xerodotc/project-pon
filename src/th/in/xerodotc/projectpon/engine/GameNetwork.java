/**
 * GameNetwork.java
 * 
 * A class for managing network system
 * 
 * @author Visatouch Deeying <xerodotc@gmail.com>
 */

package th.in.xerodotc.projectpon.engine;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import th.in.xerodotc.projectpon.engine.exceptions.NetworkException;

public final class GameNetwork {
	// a socket object to be used in game
	private static Socket socket = null;
	
	// default timeout for socket
	public static final int DEFAULT_TIMEOUT = 10000;
	
	/**
	 * Prevent instance initialization
	 */
	private GameNetwork() {
	}
	
	/**
	 * A class for manager server subsystem
	 */
	public static class Server {
		private static ServerSocket server; // server socket
		private static Thread acceptThread; // thread for accepting client
		private static AcceptListener acceptListener = null; // accept listener
		private static boolean serverStarted = false; // is server started
		
		/**
		 * Prevent instance initialization
		 */
		private Server() {
		}
		
		/**
		 * AcceptListener interface
		 */
		public static interface AcceptListener {
			public abstract void onAccepted(Socket remote);
		}
		
		/**
		 * Start the server
		 * 
		 * @param port		Server port
		 * @throws NetworkException
		 */
		public static void start(int port) throws NetworkException {
			try {
				server = new ServerSocket(port);
				serverStarted = true;
			} catch (IOException e) {
				throw new NetworkException(e);
			}
		}
		
		/**
		 * Start accepting new client
		 */
		public static void acceptClient() {
			acceptThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						Socket remote = server.accept();
						if (acceptListener != null) {
							acceptListener.onAccepted(remote);
						}
					} catch (IOException e) {
						if (serverStarted) {
							e.printStackTrace();
							serverStarted = false;
						}
					}
				}
			});
			acceptThread.start();
		}
		
		/**
		 * Stop the server
		 * 
		 * @throws NetworkException
		 */
		public static void stop() throws NetworkException {
			if (!serverStarted) {
				return;
			}
			serverStarted = false;
			acceptThread = null;
			try {
				server.close();
			} catch (IOException e) {
				throw new NetworkException(e);
			}
		}
		
		/**
		 * Bind an AcceptListener
		 * 
		 * @param al	Accept listener
		 */
		public static void addOnAcceptedListener(AcceptListener al) {
			acceptListener = al;
		}
		
		/**
		 * Is server started
		 * 
		 * @return True if server is started
		 */
		public static boolean isStarted() {
			return serverStarted;
		}
	}
	
	/**
	 * A class for managing client subsystem
	 */
	public static class Client {
		private static Socket remote; // socket connected to server
		private static ConnectListener connectListener = null; // connect listener
		private static Thread connectThread; // thread for connecting to server
		
		/**
		 * Prevent instance initialization
		 */
		private Client() {
		}
		
		/**
		 * ConnectListener interface
		 */
		public static interface ConnectListener {
			public abstract void onConnected(Socket remote);
			public abstract void onFailed(NetworkException e);
		}
		
		/**
		 * Connect to server
		 * 
		 * @param host		Server hostname/IP
		 * @param port		Server port
		 */
		public static void connect(final String host, final int port) {
			connectThread = new Thread(new Runnable() {
				@Override
				public void run() {
					try {
						remote = new Socket();
						remote.connect(new InetSocketAddress(host, port),
								DEFAULT_TIMEOUT);
						connectListener.onConnected(remote);
					} catch (IOException e) {
						connectListener.onFailed(new NetworkException(e));
					}
				}
			});
			connectThread.start();
		}
		
		/**
		 * Disconnect from server
		 * 
		 * @throws NetworkException
		 */
		public static void disconnect() throws NetworkException {
			try {
				if (remote != null) {
					remote.close();
				}
				connectThread = null;
			} catch (IOException e) {
				throw new NetworkException(e);
			}
		}
		
		/**
		 * Bind a ConnectListener
		 * 
		 * @param cl	Connect listener
		 */
		public static void addOnConnectedListener(ConnectListener cl) {
			connectListener = cl;
		}
		
		/**
		 * Is client connected?
		 * 
		 * @return		True if connected to server
		 */
		public static boolean isConnected() {
			if (remote == null) {
				return false;
			}
			return remote.isConnected() && !remote.isClosed();
		}
	}
	
	/**
	 * Get a socket object
	 * 
	 * @return	A socket object
	 */
	public static Socket getSocket() {
		return socket;
	}
	
	/**
	 * Set a socket object
	 * 
	 * @param sock		A socket object
	 */
	public static void setSocket(Socket sock) {
		socket = sock;
	}
	
	/**
	 * Disconnect and clear the socket
	 * 
	 * @throws NetworkException
	 */
	public static void clearSocket() throws NetworkException {
		if (socket != null) {
			try {
				socket.close();
			} catch (IOException e) {
				throw new NetworkException(e);
			}
		}
		socket = null;
	}
}
