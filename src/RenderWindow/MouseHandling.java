package RenderWindow;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public final class MouseHandling implements MouseListener{
	public static Window w = null;
//------------------------------------------------------------------------------------------------------------
	MouseHandling(Window w){
		MouseHandling.w = w;
	}
//------------------------------------------------------------------------------------------------------------
	@Override
	public void mouseClicked(MouseEvent e) {
		System.out.println("Mouse clicked");
		MousePos.cycle(e.getX(), e.getY());
	}
//------------------------------------------------------------------------------------------------------------
	@Override
	public void mousePressed(MouseEvent e) {
		System.out.println("Mouse pressed");
	}
//------------------------------------------------------------------------------------------------------------
	@Override
	public void mouseReleased(MouseEvent e) {
		System.out.println("Mouse released");
		MousePos.cycle(e.getX(), e.getY());
	}
//------------------------------------------------------------------------------------------------------------
	@Override
	public void mouseEntered(MouseEvent e) {
		//System.out.println("Mouse entered");
	}
//------------------------------------------------------------------------------------------------------------
	@Override
	public void mouseExited(MouseEvent e) {
		//System.out.println("Mouse exited");
	}

}
