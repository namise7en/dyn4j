/*
 * Copyright (c) 2010 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.game2d.testbed.test;

import org.dyn4j.game2d.collision.Bounds;
import org.dyn4j.game2d.collision.RectangularBounds;
import org.dyn4j.game2d.dynamics.BodyFixture;
import org.dyn4j.game2d.dynamics.World;
import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Geometry;
import org.dyn4j.game2d.geometry.Mass;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.testbed.ContactCounter;
import org.dyn4j.game2d.testbed.Entity;
import org.dyn4j.game2d.testbed.Test;

/**
 * Tests the distance joint in a Newton's Cradle configuration.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public class Restitution extends Test {
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#getName()
	 */
	@Override
	public String getName() {
		return "Restitution";
	}
	
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests different restitution values.";
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#initialize()
	 */
	@Override
	public void initialize() {
		// call the super method
		super.initialize();
		
		// setup the camera
		this.home();
		
		// create the world
		Bounds bounds = new RectangularBounds(Geometry.createRectangle(16.0, 15.0));
		this.world = new World(bounds);
		
		// setup the contact counter
		ContactCounter cc = new ContactCounter();
		this.world.setContactListener(cc);
		this.world.setStepListener(cc);
		
		// setup the bodies
		this.setup();
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#setup()
	 */
	@Override
	protected void setup() {
		// create the floor
		Rectangle floorRect = new Rectangle(15.0, 1.0);
		Entity floor = new Entity();
		floor.addFixture(new BodyFixture(floorRect));
		floor.setMass(Mass.Type.INFINITE);
		// move the floor up a bit
		floor.translate(0.0, -4.0);
		this.world.add(floor);
		
		final double r = 0.5;
		final double y = 3.0;
		final double min = 0.0;
		final double max = 1.0;
		final int count = 10;
		
		double increment = (max - min) / count;
		double x = -(count * (r + r + 0.25)) * 0.5;
		
		for (int i = 0; i < count; i++) {
			Circle c = new Circle(r);
			
			BodyFixture f = new BodyFixture(c);
			f.setRestitution(min + increment * i);
			
			Entity e = new Entity();
			e.addFixture(f);
			e.setMass();
			e.translate(x, y);
			x += r + r + 0.25;
			this.world.add(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 32.0;
		// set the camera offset
		this.offset.zero();
	}
}