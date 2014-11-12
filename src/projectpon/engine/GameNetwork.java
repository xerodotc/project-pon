package projectpon.engine;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import projectpon.engine.exceptions.NetworkException;

public final class GameNetwork {
	private static Socket socket = null;
	
	public static class Server {
		private static ServerSocket server;
		private static Thread acceptThread;
		private static AcceptListener acceptListener = null;
		private static boolean serverStarted = false;
		
		public static interface AcceptListener {
			public abstract void onAccepted(Socket remote);
		}
		
		public static void start(int port) throws NetworkException {
			try {
				server = new ServerSocket(port);
				serverStarted = true;
			} catch (IOException e) {
				throw new NetworkException(e);
			}
		}
		
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
		
		public static void stop() throws NetworkException {
			if (!serverStarted) {
				return;
			}
			serverStarted = false;
			try {
				server.close();
			} catch (IOException e) {
				throw new NetworkException(e);
			}
		}
		
		public static void addOnAcceptedListener(AcceptListener al) {
			acceptListener = al;
		}
		
		public static boolean isStarted() {
			return serverStarted;
		}
	}
	
	public static class Client {
		private static Socket remote;
		private static ConnectListener connectListener = null;
		private static boolean clientStarted = false;
		
		public static interface ConnectListener {
			public abstract void onConnected(Socket remote);
		}
		
		public static void connect(String host, int port) throws NetworkException {
			try {
				remote = new Socket(host, port);
				clientStarted = true;
				connectListener.onConnected(remote);
			} catch (IOException e) {
				throw new NetworkException(e);
			}
		}
		
		public static void disconnect() throws NetworkException {
			clientStarted = false;
			try {
				remote.close();
			} catch (IOException e) {
				throw new NetworkException(e);
			}
		}
		
		public static void addOnConnectedListener(ConnectListener cl) {
			connectListener = cl;
		}
		
		public static boolean isStarted() {
			return clientStarted;
		}
	}
	
	public static Socket getSocket() {
		return socket;
	}
	
	public static void setSocket(Socket sock) {
		socket = sock;
	}
	
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
