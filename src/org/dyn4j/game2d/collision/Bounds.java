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
package org.dyn4j.game2d.collision;

import org.dyn4j.game2d.dynamics.BodyFixture;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Transformable;
import org.dyn4j.game2d.geometry.Vector2;

/**
 * Represents the {@link Bounds} of the simulation/world.
 * @author William Bittle
 * @version 2.0.0
 * @since 1.0.0
 */
public interface Bounds extends Transformable {
	/** 
	 * The default bounds object.
	 * <p>
	 * This bounds object will always return false from the {@link #isOutside(Collidable)}
	 * method and returns null for the {@link #getTransform()} method.
	 * <p>
	 * Take care in using this bounds object since overflow can happen.
	 */
	public static final Bounds UNBOUNDED = new Bounds() {
		/* (non-Javadoc)
		 * @see org.dyn4j.game2d.geometry.Transformable#translate(org.dyn4j.game2d.geometry.Vector)
		 */
		@Override
		public void translate(Vector2 vector) {
			throw new UnsupportedOperationException("Cannot modify the Bounds.UNBOUNDED object.");
		}
		
		/* (non-Javadoc)
		 * @see org.dyn4j.game2d.geometry.Transformable#translate(double, double)
		 */
		@Override
		public void translate(double x, double y) {
			throw new UnsupportedOperationException("Cannot modify the Bounds.UNBOUNDED object.");
		}
		
		/* (non-Javadoc)
		 * @see org.dyn4j.game2d.geometry.Transformable#rotate(double, double, double)
		 */
		@Override
		public void rotate(double theta, double x, double y) {
			throw new UnsupportedOperationException("Cannot modify the Bounds.UNBOUNDED object.");
		}
		
		/* (non-Javadoc)
		 * @see org.dyn4j.game2d.geometry.Transformable#rotate(double, org.dyn4j.game2d.geometry.Vector)
		 */
		@Override
		public void rotate(double theta, Vector2 point) {
			throw new UnsupportedOperationException("Cannot modify the Bounds.UNBOUNDED object.");
		}
		
		/* (non-Javadoc)
		 * @see org.dyn4j.game2d.geometry.Transformable#rotate(double)
		 */
		@Override
		public void rotate(double theta) {
			throw new UnsupportedOperationException("Cannot modify the Bounds.UNBOUNDED object.");
		}
		
		/* (non-Javadoc)
		 * @see org.dyn4j.game2d.collision.Bounds#isOutside(org.dyn4j.game2d.collision.Collidable)
		 */
		@Override
		public boolean isOutside(Collidable collidable) {
			return false;
		}
		
		/* (non-Javadoc)
		 * @see org.dyn4j.game2d.collision.Bounds#getTransform()
		 */
		@Override
		public Transform getTransform() {
			return null;
		}
	};
	
	/**
	 * Returns the {@link Bounds} {@link Transform}.
	 * @return {@link Transform}
	 */
	public abstract Transform getTransform();
	
	/**
	 * Returns true if the given {@link Collidable} is outside the bounds.
	 * <p>
	 * If the {@link Collidable} contains zero {@link BodyFixture}s then 
	 * {@link Collidable} is considered to be outside the bounds.
	 * @param collidable the {@link Collidable} to test
	 * @return boolean true if outside the bounds
	 */
	public abstract boolean isOutside(Collidable collidable);
}
