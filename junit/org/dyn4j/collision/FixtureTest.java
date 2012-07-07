/*
 * Copyright (c) 2010-2012 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.collision;

import junit.framework.TestCase;

import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.junit.Test;

/**
 * Test case for the {@link Fixture} class.
 * @author William Bittle
 * @version 3.1.1
 * @since 3.1.1
 */
public class FixtureTest {
	/**
	 * Tests the successful creation of a fixture.
	 */
	@Test
	public void createSuccess() {
		new Fixture(Geometry.createCircle(1.0));
	}
	
	/**
	 * Tests the failed creation of a fixture by passing a null
	 * shape.
	 */
	@Test(expected = NullPointerException.class)
	public void createNullFixture() {
		new Fixture(null);
	}
	
	/**
	 * Tests that an id is created for a fixture.
	 */
	@Test
	public void getId() {
		Fixture f = new Fixture(Geometry.createCircle(1.0));
		TestCase.assertNotNull(f.getId());
	}
	
	/**
	 * Tests the getShape method.
	 */
	@Test
	public void getShape() {
		Convex c = Geometry.createCircle(1.0);
		Fixture f = new Fixture(c);
		
		TestCase.assertNotNull(f.getShape());
		TestCase.assertEquals(c, f.getShape());
	}
	
	/**
	 * Tests setting the filter.
	 */
	@Test
	public void setFilter() {
		Fixture fixture = new Fixture(Geometry.createCircle(1.0));
		Filter f = new CategoryFilter();
		fixture.setFilter(f);
		TestCase.assertEquals(f, fixture.getFilter());
	}

	/**
	 * Tests setting a null filter.
	 */
	@Test(expected = NullPointerException.class)
	public void setNullFilter() {
		Fixture fixture = new Fixture(Geometry.createCircle(1.0));
		fixture.setFilter(null);
	}
	
	/**
	 * Tests the set/get sensor methods.
	 */
	@Test
	public void setSensor() {
		Fixture fixture = new Fixture(Geometry.createCircle(1.0));
		// by default it should be false
		TestCase.assertFalse(fixture.isSensor());
		
		fixture.setSensor(true);
		TestCase.assertTrue(fixture.isSensor());
		
		fixture.setSensor(false);
		TestCase.assertFalse(fixture.isSensor());
	}
	
	/**
	 * Make sure storage of user data is working.
	 */
	@Test
	public void setUserData() {
		Fixture fixture = new Fixture(Geometry.createCircle(1.0));
		// should be initial null
		TestCase.assertNull(fixture.getUserData());
		
		String obj = "hello";
		fixture.setUserData(obj);
		TestCase.assertNotNull(fixture.getUserData());
		TestCase.assertSame(obj, fixture.getUserData());
	}
}
