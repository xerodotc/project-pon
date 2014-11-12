package projectpon.engine;

interface GameWindow {
	public abstract void setParentWindow(GameWindow w);
	public abstract void childWindowOpened();
	public abstract void childWindowClosed();
	public abstract void launchChildWindow(GameWindow w);
}
