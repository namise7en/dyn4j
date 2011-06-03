/*
 * Copyright (c) 2010, William Bittle
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted 
 * provided that the following conditions are met:
 * 
 *   * Redistributions of source code must retain the above copyright notice, this list of conditions 
 *     and the following disclaimer.
 *   * Redistributions in binary form must reproduce the above copyright notice, this list of conditions 
 *     and the following disclaimer in the documentation and/or other materials provided with the 
 *     distribution.
 *   * Neither the name of dyn4j nor the names of its contributors may be used to endorse or 
 *     promote products derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR 
 * IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND 
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, 
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER 
 * IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT 
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.dyn4j.game2d.testbed;

/**
 * Settings singleton for drawing.
 * @author William Bittle
 */
public class Draw {
	/** Whether to draw centers of mass or not */
	private boolean center = false;
	
	/** Whether to draw velocity vectors or not */
	private boolean velocity = false;
	
	/** Whether to draw contacts or not */
	private boolean contacts = false;
	
	/** Whether to draw contact forces or not */
	private boolean contactForces = false;
	
	/** Whether to draw contact pairs or not */
	private boolean contactPairs = false;
	
	/** Whether to draw contact friction forces or not */
	private boolean frictionForces = false;
	
	/** Whether to draw joints or not */
	private boolean joints = true;
	
	/** Whether to draw the bounds or not */
	private boolean bounds = false;
	
	/** Whether to draw the metrics panel or not */
	private boolean panel = true;
	
	/** Whether to fill shapes with color */
	private boolean fill = true;
	
	/** Whether to draw shape outlines */
	private boolean outline = true;
	
	/** Whether to draw edge normals */
	private boolean normals = false;
	
	/** The singleton instance */
	private static final Draw instance = new Draw();
	
	/**
	 * Returns the singleton instance.
	 * @return {@link Draw} the singleton instance
	 */
	public synchronized static Draw getInstance() {
		return instance;
	}

	/**
	 * Returns true if centers of mass should be drawn.
	 * @return boolean
	 */
	public boolean drawCenter() {
		return center;
	}

	/**
	 * Sets whether the centers of mass should be drawn.
	 * @param flag true if centers of mass should be drawn
	 */
	public synchronized void setDrawCenter(boolean flag) {
		this.center = flag;
	}

	/**
	 * Returns true if velocity vectors shoudl be drawn.
	 * @return boolean
	 */
	public boolean drawVelocityVectors() {
		return velocity;
	}

	/**
	 * Sets whether the velocity vectors should be drawn.
	 * @param flag true if velocity vectors should be drawn
	 */
	public synchronized void setDrawVelocityVectors(boolean flag) {
		this.velocity = flag;
	}

	/**
	 * Returns true if contacts should be drawn.
	 * @return boolean
	 */
	public boolean drawContacts() {
		return contacts;
	}

	/**
	 * Sets whether contacts should be drawn.
	 * @param flag true if contacts should be drawn
	 */
	public synchronized void setDrawContacts(boolean flag) {
		this.contacts = flag;
	}
	
	/**
	 * Returns true if contact forces should be drawn.
	 * @return boolean
	 */
	public boolean drawContactForces() {
		return this.contactForces;
	}
	
	/**
	 * Returns true if contact pairs should be drawn.
	 * @return boolean
	 */
	public boolean drawContactPairs() {
		return contactPairs;
	}

	/**
	 * Sets whether contact pairs should be drawn.
	 * @param flag true if contact pairs should be drawn
	 */
	public void setDrawContactPairs(boolean flag) {
		this.contactPairs = flag;
	}

	/**
	 * Sets whether contact forces should be drawn.
	 * @param flag true if contact forces should be drawn
	 */
	public synchronized void setDrawContactForces(boolean flag) {
		this.contactForces = flag;
	}
	
	/**
	 * Returns true if friction forces should be drawn.
	 * @return boolean
	 */
	public boolean drawFrictionForces() {
		return this.frictionForces;
	}
	
	/**
	 * Sets whether friction forces should be drawn.
	 * @param flag true if friction forces should be drawn
	 */
	public synchronized void setDrawFrictionForces(boolean flag) {
		this.frictionForces = flag;
	}
	
	/**
	 * Returns true if joints should be drawn.
	 * @return boolean
	 */
	public boolean drawJoints() {
		return this.joints;
	}
	
	/**
	 * Sets whether joints should be drawn.
	 * @param flag true if joints should be drawn
	 */
	public synchronized void setDrawJoints(boolean flag) {
		this.joints = flag;
	}

	/**
	 * Returns true if the bounds should be drawn.
	 * @return boolean
	 */
	public boolean drawBounds() {
		return bounds;
	}

	/**
	 * Sets whether the bounds should be drawn.
	 * @param flag true if the bounds should be drawn
	 */
	public synchronized void setDrawBounds(boolean flag) {
		this.bounds = flag;
	}

	/**
	 * Returns true if the metrics panel should be drawn.
	 * @return boolean
	 */
	public boolean drawPanel() {
		return panel;
	}

	/**
	 * Sets whether the metrics panel should be drawn.
	 * @param flag true if the metrics panel should be drawn
	 */
	public synchronized void setDrawPanel(boolean flag) {
		this.panel = flag;
	}
	
	/**
	 * Returns true if shapes should be filled with a random color.
	 * @return boolean
	 */
	public boolean drawFill() {
		return fill;
	}
	
	/**
	 * Sets whether shapes should be filled with a random color.
	 * @param flag true if shapes should be filled
	 */
	public synchronized void setDrawFill(boolean flag) {
		this.fill = flag;
	}
	
	/**
	 * Returns true if shape outlines should be drawn.
	 * @return boolean
	 */
	public boolean drawOutline() {
		return outline;
	}
	
	/**
	 * Sets whether a shapes outlines should be drawn.
	 * @param flag true if shape outlines should be drawn
	 */
	public synchronized void setDrawOutline(boolean flag) {
		this.outline = flag;
	}
	
	/**
	 * Returns true if shape edge normals should be drawn.
	 * @return boolean
	 */
	public boolean drawNormals() {
		return normals;
	}
	
	/**
	 * Sets whether a shape edge normals should be drawn.
	 * @param flag true if shape edge normals should be drawn
	 */
	public synchronized void setDrawNormals(boolean flag) {
		this.normals = flag;
	}
}