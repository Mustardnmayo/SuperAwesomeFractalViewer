package RenderWindow;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;

//------------------------------------------------------------------------------------------------------------
public class Window extends Frame {
	private static final long serialVersionUID = 1L;

	private GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
	private GraphicsDevice gd;

	private final Graphics g;

	public MouseHandling mouse_handler = null;
	public KeyboardHandling key_handeler = null;

	public int width, height;
	public boolean hasDimentions = true;

//------------------------------------------------------------------------------------------------------------
	public Window(boolean fullscreen, int device) {
		super("AMAZING FRACTAL VISUALIZATIONS!!!");
		this.gd = ge.getDefaultScreenDevice();
		if (this.gd != null) {
			this.width = gd.getDefaultConfiguration().getBounds().width;
			this.height = gd.getDefaultConfiguration().getBounds().height;
		} else {
			hasDimentions = false;
		}

		setVisible(true);
		setSize(width, height);
		// ------------------------------------------------------
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.out.println(e);
				System.exit(0);
			}
		});
		// ------------------------------------------------------
		this.mouse_handler = new MouseHandling(this);
		this.addMouseListener(this.mouse_handler);
		// ------------------------------------------------------
		this.key_handeler = new KeyboardHandling(this);
		this.addKeyListener(this.key_handeler);
		// ------------------------------------------------------
		this.g = this.getGraphics();
		// ------------------------------------------------------
		if (fullscreen)
			this.gd.setFullScreenWindow(this); // only works on m1 idk why
	}

//------------------------------------------------------------------------------------------------------------
	public void display(BufferedImage img) {
		g.drawImage(img, 0, 0, null);
	}

//------------------------------------------------------------------------------------------------------------
	public void close() {
		System.exit(0);
	}
//------------------------------------------------------------------------------------------------------------
}
