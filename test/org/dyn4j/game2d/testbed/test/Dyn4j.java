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
import org.dyn4j.game2d.geometry.Geometry;
import org.dyn4j.game2d.geometry.Mass;
import org.dyn4j.game2d.geometry.Polygon;
import org.dyn4j.game2d.geometry.Rectangle;
import org.dyn4j.game2d.geometry.Vector2;
import org.dyn4j.game2d.testbed.ContactCounter;
import org.dyn4j.game2d.testbed.Entity;
import org.dyn4j.game2d.testbed.Test;

/**
 * Tests composite bodies generated by the Decompose test in the form of the
 * name of this library.
 * @author William Bittle
 * @version 2.2.1
 * @since 2.2.1
 */
public class Dyn4j extends Test {
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.testbed.Test#getName()
	 */
	@Override
	public String getName() {
		return "Dyn4j";
	}
	
	/* (non-Javadoc)
	 * @see test.Test#getDescription()
	 */
	@Override
	public String getDescription() {
		return "Tests composite bodies generated by the Decompose test where " +
				"each body is a letter of the library name: dyn4j";
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
		floor.translate(0.0, 0.0);
		this.world.add(floor);
		
		// D
		Entity d = new Entity();
		Polygon d1 = new Polygon(new Vector2[] {new Vector2(-0.703125, 0.859375),
				                                new Vector2(-0.828125, 0.625), 
				                                new Vector2(-0.875, 0.25), 
				                                new Vector2(-0.625, 0.0625), 
				                                new Vector2(-0.359375, 0.046875), 
				                                new Vector2(-0.1875, 0.1875)});
		Polygon d2 = new Polygon(new Vector2[] {new Vector2(0.0, -0.0), 
				                                new Vector2(-0.015625, 1.625), 
				                                new Vector2(-0.171875, 1.640625), 
				                                new Vector2(-0.171875, 0.671875)});
		Polygon d3 = new Polygon(new Vector2[] {new Vector2(0.0, -0.0), 
				                                new Vector2(-0.1875, 0.1875), 
				                                new Vector2(-0.21875, 0.0)});
		Polygon d4 = new Polygon(new Vector2[] {new Vector2(0.0, -0.0), 
				                                new Vector2(-0.171875, 0.671875), 
				                                new Vector2(-0.375, 0.84375), 
				                                new Vector2(-0.703125, 0.859375), 
				                                new Vector2(-0.1875, 0.1875)});
		d.addFixture(d1);
		d.addFixture(d2);
		d.addFixture(d3);
		d.addFixture(d4);
		d.setMass();
		d.translate(d.getWorldCenter().getNegative());
		d.translate(-2.3, 1.0);
		this.world.add(d);
		
		// Y
		Entity y = new Entity();
		Polygon y1 = new Polygon(new Vector2[] {new Vector2(1.015625, -0.34375),
				                                new Vector2(0.703125, 0.28125),
				                                new Vector2(0.4375, 0.265625),
				                                new Vector2(0.8125, -0.484375)});
		// i manually split this polygon so that the bottom portion of the 'y' sinks
		// below the floor
//		Polygon y2 = new Polygon(new Vector2[] {new Vector2(0.53125, -1.390625),
//				                                new Vector2(1.703125, 0.296875),
//				                                new Vector2(1.421875, 0.3125),
//				                                new Vector2(1.015625, -0.34375)});
		Polygon y2 = new Polygon(new Vector2[] {new Vector2(1.703125, 0.296875),
				                                new Vector2(1.421875, 0.3125),
				                                new Vector2(1.015625, -0.34375),
				                                new Vector2(1.1171875, -0.546875)});
		Polygon y3 = new Polygon(new Vector2[] {new Vector2(1.1171875, -0.546875),
				                                new Vector2(1.015625, -0.34375),
				                                new Vector2(0.53125, -1.390625)});
		Polygon y4 = new Polygon(new Vector2[] {new Vector2(1.015625, -0.34375),
				                                new Vector2(0.8125, -0.484375),
				                                new Vector2(0.25, -1.1875),
				                                new Vector2(0.53125, -1.390625)});
		y.addFixture(y1);
		y.addFixture(y2);
		// set the bottom two pieces of the y to sensors
		// so that those collisions are not resolved thereby making
		// them sink through the floor
		BodyFixture bfy3 = y.addFixture(y3);
		bfy3.setSensor(true);
		BodyFixture bfy4 = y.addFixture(y4);
		bfy4.setSensor(true);
		y.setMass();
		y.translate(y.getWorldCenter().getNegative());
		y.translate(-1.3, 1.0);
		this.world.add(y);
		
		// N
		Entity n = new Entity();
		Polygon n1 = new Polygon(new Vector2[] {new Vector2(-0.03125, 0.984375),
				                                new Vector2(-0.015625, -0.015625),
				                                new Vector2(0.140625, -0.015625),
				                                new Vector2(0.15625, 0.375)});
		Polygon n2 = new Polygon(new Vector2[] {new Vector2(0.15625, 0.375),
				                                new Vector2(0.34375, 0.578125),
				                                new Vector2(0.1875, 0.734375)});
		Polygon n3 = new Polygon(new Vector2[] {new Vector2(0.1875, 0.734375),
				                                new Vector2(0.1875, 0.984375),
				                                new Vector2(-0.03125, 0.984375),
				                                new Vector2(0.15625, 0.375)});
		Polygon n4 = new Polygon(new Vector2[] {new Vector2(0.296875, 0.84375),
				                                new Vector2(0.1875, 0.734375),
				                                new Vector2(0.34375, 0.578125),
				                                new Vector2(0.5, 0.71875)});
		Polygon n5 = new Polygon(new Vector2[] {new Vector2(0.78125, 0.96875),
				                                new Vector2(0.5, 0.96875),
				                                new Vector2(0.296875, 0.84375),
				                                new Vector2(0.5, 0.71875),
				                                new Vector2(0.6875, 0.671875)});
		Polygon n6 = new Polygon(new Vector2[] {new Vector2(0.765625, 0.5625),
				                                new Vector2(0.734375, -0.0),
				                                new Vector2(1.015625, -0.015625),
				                                new Vector2(1.046875, 0.640625)});
		Polygon n7 = new Polygon(new Vector2[] {new Vector2(1.046875, 0.640625),
				                                new Vector2(0.953125, 0.84375),
				                                new Vector2(0.78125, 0.96875),
				                                new Vector2(0.6875, 0.671875),
				                                new Vector2(0.765625, 0.5625)});
		n.addFixture(n1);
		n.addFixture(n2);
		n.addFixture(n3);
		n.addFixture(n4);
		n.addFixture(n5);
		n.addFixture(n6);
		n.addFixture(n7);
		n.setMass();
		n.translate(n.getWorldCenter().getNegative());
		n.translate(-0.0, 1.0);
		this.world.add(n);
		
		// 4
		Entity four = new Entity();
		Polygon four1 = new Polygon(new Vector2[] {new Vector2(0.859375, 1.828125),
				                                   new Vector2(-0.21875, 0.8125),
				                                   new Vector2(0.8125, 0.8125)});
		Polygon four2 = new Polygon(new Vector2[] {new Vector2(0.8125, 0.8125),
				                                   new Vector2(0.828125, 0.046875),
				                                   new Vector2(1.125, 0.046875),
				                                   new Vector2(1.125, 1.828125),
				                                   new Vector2(0.859375, 1.828125)});
		four.addFixture(four1);
		four.addFixture(four2);
		four.setMass();
		four.translate(four.getWorldCenter().getNegative());
		four.translate(1.0, 2.0);
		this.world.add(four);
		
		// J
		Entity j = new Entity();
		Polygon j1 = new Polygon(new Vector2[] {new Vector2(0.90625, 0.203125),
				                                new Vector2(0.96875, 0.34375),
				                                new Vector2(1.0, 1.21875),
				                                new Vector2(0.78125, 1.265625),
				                                new Vector2(0.734375, 0.34375)});
		Polygon j2 = new Polygon(new Vector2[] {new Vector2(0.734375, 0.109375),
				                                new Vector2(0.90625, 0.203125),
				                                new Vector2(0.734375, 0.34375),
				                                new Vector2(0.5625, 0.296875)});
		Polygon j3 = new Polygon(new Vector2[] {new Vector2(0.203125, 0.109375),
				                                new Vector2(0.46875, 0.046875),
				                                new Vector2(0.734375, 0.109375),
				                                new Vector2(0.5625, 0.296875),
				                                new Vector2(0.34375, 0.296875)});
		Polygon j4 = new Polygon(new Vector2[] {new Vector2(0.234375, 0.4375),
				                                new Vector2(0.234375, 0.65625),
				                                new Vector2(0.046875, 0.65625),
				                                new Vector2(0.046875, 0.5)});
		Polygon j5 = new Polygon(new Vector2[] {new Vector2(0.046875, 0.5),
				                                new Vector2(0.078125, 0.28125),
				                                new Vector2(0.203125, 0.109375),
				                                new Vector2(0.34375, 0.296875),
				                                new Vector2(0.234375, 0.4375)});
		j.addFixture(j1);
		j.addFixture(j2);
		j.addFixture(j3);
		j.addFixture(j4);
		j.addFixture(j5);
		j.setMass();
		j.translate(j.getWorldCenter().getNegative());
		j.translate(1.5, 0.5);
		this.world.add(j);
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.Test#home()
	 */
	@Override
	public void home() {
		// set the scale
		this.scale = 64.0;
		// set the camera offset
		this.offset.set(0.0, -2.0);
	}
}
