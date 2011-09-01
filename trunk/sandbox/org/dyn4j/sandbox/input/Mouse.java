package org.dyn4j.sandbox.input;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

/**
 * Represents a polled Mouse input device.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class Mouse implements MouseListener, MouseMotionListener, MouseWheelListener {
	/** The map of mouse buttons */
	private Map<Integer, MouseButton> buttons = new Hashtable<Integer, MouseButton>();
	
	/** The current mouse location */
	private Point location;
	
	/** Whether the mouse has moved */
	private boolean moved;
	
	/** The scroll amount */
	private int scroll;
	
	/**
	 * Returns true if the given MouseEvent code was clicked.
	 * @param code the MouseEvent code
	 * @return boolean
	 */
	public boolean wasClicked(int code) {
		MouseButton mb = this.buttons.get(code);
		// check if the mouse button exists
		if (mb == null) {
			return false;
		}
		// return the clicked state
		return mb.wasClicked();
	}
	
	/**
	 * Returns true if the given MouseEvent code was double clicked.
	 * @param code the MouseEvent code
	 * @return boolean
	 */
	public boolean wasDoubleClicked(int code) {
		MouseButton mb = this.buttons.get(code);
		// check if the mouse button exists
		if (mb == null) {
			return false;
		}
		// return the double clicked state
		return mb.wasDoubleClicked();
	}
	
	/**
	 * Returns true if the given MouseEvent code was clicked and is waiting to be released.
	 * @param code the MouseEvent code
	 * @return boolean
	 */
	public boolean isPressed(int code) {
		MouseButton mb = this.buttons.get(code);
		// check if the mouse button exists
		if (mb == null) {
			return false;
		}
		// return the double clicked state
		return mb.isPressed();
	}
	
	/**
	 * Returns true if the given MouseEvent code was clicked and was waiting to be released
	 * but is now released.
	 * @param code the MouseEvent code
	 * @return boolean
	 */
	public boolean wasReleased(int code) {
		MouseButton mb = this.buttons.get(code);
		// check if the mouse button exists
		if (mb == null) {
			return false;
		}
		// return the double clicked state
		return mb.wasReleased();
	}
	
	/**
	 * Returns the current location of the mouse relative to
	 * the listening component.
	 * @return Point
	 */
	public synchronized Point getLocation() {
		return this.location;
	}
	
	/**
	 * Returns true if the mouse has moved.
	 * @return boolean
	 */
	public synchronized boolean hasMoved() {
		return this.moved;
	}
	
	/**
	 * Clears the state of the given MouseEvent code.
	 * @param code the MouseEvent code
	 */
	public void clear(int code) {
		MouseButton mb = this.buttons.get(code);
		// check if the mouse button exists
		if (mb == null) {
			return;
		}
		// clear the state
		mb.clear();
	}
	
	/**
	 * Clears the state of all MouseEvents.
	 */
	public void clear() {
		Iterator<MouseButton> buttons = this.buttons.values().iterator();
		while (buttons.hasNext()) {
			buttons.next().clear();
		}
		this.moved = false;
		this.scroll = 0;
	}
	
	/**
	 * Returns true if the user has scrolled the mouse wheel.
	 * @return boolean
	 */
	public boolean hasScrolled() {
		return this.scroll != 0;
	}
	
	/**
	 * Returns the number of 'clicks' the mouse wheel has scrolled.
	 * @return int
	 */
	public int getScrollAmount() {
		return this.scroll;
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
		int code = e.getButton();
		MouseButton mb = this.buttons.get(code);
		// check if the mouse event is in the map
		if (mb == null) {
			// if not, then add it
			mb = new MouseButton(code);
			this.buttons.put(code, mb);
		}
		// set the value directly (since this can be a single/double/triple etc click)
		mb.setValue(e.getClickCount());
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		// called when a mouse button is pressed and is waiting for release
		
		// set the mouse state to pressed + held for the button
		int code = e.getButton();
		MouseButton mb = this.buttons.get(code);
		// check if the mouse event is in the map
		if (mb == null) {
			// if not, then add it
			mb = new MouseButton(code);
			this.buttons.put(code, mb);
		}
		mb.setPressed(true);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		// called when a mouse button is waiting for release and was released
		
		// set the mouse state to released for the button
		int code = e.getButton();
		MouseButton mb = this.buttons.get(code);
		// check if the mouse event is in the map
		if (mb == null) {
			// if not, then add it
			mb = new MouseButton(code);
			this.buttons.put(code, mb);
		}
		mb.setPressed(false);
		mb.setWasReleased(true);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// called when a mouse button is waiting for release and the mouse is moving
		
		// set the mouse location
		synchronized (this) {
			this.moved = true;
			this.location = e.getPoint();
		}
		// set the mouse button pressed flag
		int code = e.getButton();
		MouseButton mb = this.buttons.get(code);
		// check if the mouse event is in the map
		if (mb == null) {
			// if not, then add it
			mb = new MouseButton(code);
			this.buttons.put(code, mb);
		}
		mb.setPressed(true);
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseMotionListener#mouseMoved(java.awt.event.MouseEvent)
	 */
	@Override
	public synchronized void mouseMoved(MouseEvent e) {
		this.moved = true;
		this.location = e.getPoint();
	}
	
	/* (non-Javadoc)
	 * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.MouseWheelEvent)
	 */
	@Override
	public synchronized void mouseWheelMoved(MouseWheelEvent e) {
		this.scroll += e.getWheelRotation();
	}

	// not using right now

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseEntered(MouseEvent e) {}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseExited(MouseEvent e) {}
}
