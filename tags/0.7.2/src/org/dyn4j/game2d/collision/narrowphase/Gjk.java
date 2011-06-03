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
package org.dyn4j.game2d.collision.narrowphase;

import java.util.ArrayList;
import java.util.List;

import org.dyn4j.game2d.collision.Collidable;
import org.dyn4j.game2d.geometry.Circle;
import org.dyn4j.game2d.geometry.Convex;
import org.dyn4j.game2d.geometry.Segment;
import org.dyn4j.game2d.geometry.Shape;
import org.dyn4j.game2d.geometry.Transform;
import org.dyn4j.game2d.geometry.Vector;

/**
 * Implementation of the {@link Gjk} algorithm.
 * <p>
 * The {@link Gjk} algorithm is an algorithm used to find minimum distance from 
 * one {@link Convex} {@link Shape} to another, but can also be used to determine 
 * whether or not they intersect.
 * <p>
 * {@link Gjk} uses a specific mathematical construct called the {@link MinkowskiSum}.  The 
 * {@link MinkowskiSum} of two {@link Convex} {@link Shape}s create another {@link Convex} 
 * shape.  The new {@link Convex} {@link Shape} is defined as adding every point in A to 
 * every point in B.
 * <p>
 * This is useful if we do the opposite operation in the {@link MinkowskiSum}, the difference.  
 * If we take the difference of every point in A and every point in B, we still end up with a 
 * {@link Convex} {@link Shape}, however with an interesting property:  If the two {@link Convex} 
 * {@link Shape}s are penetrating one another then the {@link MinkowskiSum} (using the difference
 * operator) will contain the origin.  It is not necessary to compute the {@link MinkowskiSum} however.
 * <p>
 * To determine whether the origin is contained in the {@link MinkowskiSum} we must iteratively 
 * create a {@link Shape} inside the {@link MinkowskiSum} that encloses the origin.  This is called
 * the simplex and for 2D its a triangle.
 * <p>
 * To create a triangle in the {@link MinkowskiSum}, we will use what is called a support function.
 * <p>
 * The support function's purpose is to return a point on the {@link MinkowskiSum} farthest in a 
 * given direction.  This can be obtained by taking the farthest point in object A minus the farthest 
 * point in object B in the opposite direction.
 * <p>
 * If the {@link MinkowskiSum} is:
 * <pre>
 * A - B
 * </pre>
 * the support would be:
 * <pre>
 * (farthest point in direction D in A) - (farthest point in direction -D in B)
 * </pre>
 * Using this we can obtain a point which are on the edge of the {@link MinkowskiSum} shape in
 * any direction.  Next we need a way to iteratively create these points so that we enclose the
 * origin.
 * <p>
 * The algorithm:
 * <pre>
 * // get a point farthest in the direction
 * // choose some random direction (selection of the initial direction can
 * // determine the speed at which the algorithm terminates)
 * Point p = support(A, B, direction);
 * // add it to the simplex
 * simplex.addPoint(p);
 * // negate the direction
 * direction = -direction;
 * // make sure the point we are about to add is actually past the origin
 * // if its not past the origin then that means we can never enclose the origin
 * // therefore its not in the Minkowski sum and therefore there is no penetration.
 * while (p = support(A, B, direction).dot(direction) > 0) {
 * 	// if the p is past the origin then add it to the simplex
 * 	simplex.add(p);
 *	// then check to see if the simplex contains the origin
 *	// passing back a new search direction if it does not
 * 	if (check(simplex, direction)) {
 * 		return true;
 * 	}
 * }
 * return false;
 * </pre>
 * The last method to discuss is the method, check.  This method can be implemented in
 * any fashion, however, if the simplex is stored in a way that we always know what point
 * was added last, many optimizations can be done.  For these optimizations please refer
 * to the source documentation on {@link Gjk#checkSimplex(List, Vector)}.
 * <p>
 * Once {@link Gjk} has found that the two {@link Collidable}s are penetrating it will exit 
 * and hand off the resulting simplex to a {@link MinkowskiPenetrationSolver}.
 * <p>
 * {@link Gjk}'s default {@link MinkowskiPenetrationSolver} is {@link Epa}.
 * <p>
 * {@link Gjk} original intent was to find the minimum distance between two {@link Convex}
 * {@link Shape}s.  Refer to {@link Gjk#distance(Convex, Transform, Convex, Transform, Separation)}
 * for details on the implementation.
 * @author William Bittle
 */
public class Gjk extends AbstractNarrowphaseDetector implements NarrowphaseDetector {
	/** The origin point */
	protected static final Vector ORIGIN = new Vector();
	
	/** The default {@link Gjk} maximum iterations */
	public static final int DEFAULT_GJK_MAX_ITERATIONS = 100;
	
	/** The default {@link Gjk} distance epsilon in meters */
	public static final double DEFAULT_GJK_DISTANCE_EPSILON = 1.0e-9;
	
	/** The penetration solver; defaults to {@link Epa} */
	protected MinkowskiPenetrationSolver mps = new Epa();
	
	/** The maximum number of {@link Gjk} iterations */
	protected int gjkMaxIterations = Gjk.DEFAULT_GJK_MAX_ITERATIONS;
	
	/** The {@link Gjk} distance epsilon in meters */
	protected double gjkDistanceEpsilon = Gjk.DEFAULT_GJK_DISTANCE_EPSILON;
	
	/** The {@link Gjk} distance epsilon squared in meters<sup>2</sup> */
	protected double gjkDistanceEpsilonSquared = Gjk.DEFAULT_GJK_DISTANCE_EPSILON * Gjk.DEFAULT_GJK_DISTANCE_EPSILON;
	
	/**
	 * Default constructor.
	 */
	public Gjk() {}
	
	/**
	 * Optional constructor.
	 * @param mps the {@link MinkowskiPenetrationSolver} to use
	 */
	public Gjk(MinkowskiPenetrationSolver mps) {
		this.mps = mps;
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.narrowphase.NarrowphaseDetector#detect(org.dyn4j.game2d.geometry.Convex, org.dyn4j.game2d.geometry.Transform, org.dyn4j.game2d.geometry.Convex, org.dyn4j.game2d.geometry.Transform, org.dyn4j.game2d.collision.narrowphase.Penetration)
	 */
	@Override
	public boolean detect(Convex s1, Transform t1, Convex s2, Transform t2, Penetration p) {
		// check for circles
		if (s1.isType(Circle.TYPE) && s2.isType(Circle.TYPE)) {
			// if its a circle - circle collision use the faster method
			return super.detect((Circle) s1, t1, (Circle) s2, t2, p);
		}
		// define the simplex
		List<Vector> simplex = new ArrayList<Vector>(3);
		// create a Minkowski sum
		MinkowskiSum ms = new MinkowskiSum(s1, t1, s2, t2);
		// transform into world space if transform is not null
		Vector c1 = t1.getTransformed(s1.getCenter());
		Vector c2 = t2.getTransformed(s2.getCenter());
		// choose some search direction
		Vector d = c1.to(c2);
		// check for a zero direction vector
		if (d.isZero()) d.set(1.0, 0.0);
		// add the first point
		simplex.add(ms.support(d));
		// negate the search direction
		d.negate();
		// start the loop
		while (true) {
			// always add another point to the simplex at the beginning of the loop
			simplex.add(ms.support(d));
			// make sure that the last point we added was past the origin
			if (simplex.get(simplex.size() - 1).dot(d) <= 0) {
				// a is not past the origin so therefore the shapes do not intersect
				// here we treat the origin on the line as no intersection
				// immediately return with null indicating no penetration
				return false;
			} else {
				// if it is past the origin, then test whether the simplex contains the origin
				if (this.checkSimplex(simplex, d)) {
					// if the simplex contains the origin then we know that there is an intersection.
					// if we broke out of the loop then we know there was an intersection
					// perform epa to get the penetration vector
					this.mps.getPenetration(simplex, ms, p);
					return true;
				}
				// if the simplex does not contain the origin then we need to loop using the new
				// search direction and simplex
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see org.dyn4j.game2d.collision.narrowphase.NarrowphaseDetector#test(org.dyn4j.game2d.geometry.Convex, org.dyn4j.game2d.geometry.Transform, org.dyn4j.game2d.geometry.Convex, org.dyn4j.game2d.geometry.Transform)
	 */
	@Override
	public boolean detect(Convex s1, Transform t1, Convex s2, Transform t2) {
		// check for circles
		if (s1.isType(Circle.TYPE) && s2.isType(Circle.TYPE)) {
			// if its a circle - circle collision use the faster method
			return super.detect((Circle) s1, t1, (Circle) s2, t2);
		}
		// define the simplex
		List<Vector> simplex = new ArrayList<Vector>(3);
		// create a Minkowski sum
		MinkowskiSum ms = new MinkowskiSum(s1, t1, s2, t2);
		// transform into world space if transform is not null
		Vector c1 = t1.getTransformed(s1.getCenter());
		Vector c2 = t2.getTransformed(s2.getCenter());
		// choose some search direction
		Vector d = c1.to(c2);
		// check for a zero direction vector
		if (d.isZero()) d.set(1.0, 0.0);
		// add the first point
		simplex.add(ms.support(d));
		// negate the search direction
		d.negate();
		// start the loop
		while (true) {
			// always add another point to the simplex at the beginning of the loop
			simplex.add(ms.support(d));
			// make sure that the last point we added was past the origin
			if (simplex.get(simplex.size() - 1).dot(d) <= 0) {
				// a is not past the origin so therefore the shapes do not intersect
				// here we treat the origin on the line as no intersection
				// immediately return with null indicating no penetration
				return false;
			} else {
				// if it is past the origin, then test whether the simplex contains the origin
				if (this.checkSimplex(simplex, d)) {
					// if the simplex contains the origin then we know that there is an intersection.
					// if we broke out of the loop then we know there was an intersection
					return true;
				}
				// if the simplex does not contain the origin then we need to loop using the new
				// search direction and simplex
			}
		}
	}
	
	/**
	 * Determines whether the given simplex contains the origin.  If it does contain the origin,
	 * then this method will return true.  If it does not, this method will update both the given
	 * simplex and also the given search direction.
	 * <p>
	 * This method only handles the line segment and triangle simplex cases, however, these two cases
	 * should be the only ones needed for 2 dimensional {@link Gjk}.
	 * <p>
	 * This method also assumes that the last point in the simplex is the most recently added point.
	 * This matters because optimizations are available when you know this information.
	 * @param simplex the simplex
	 * @param direction the search direction
	 * @return boolean true if the simplex contains the origin
	 */
	protected boolean checkSimplex(List<Vector> simplex, Vector direction) {
		// this method should never be supplied anything other than 2 or 3 points for the simplex
		// get the last point added (a)
		Vector a = simplex.get(simplex.size() - 1);
		// this is the same as a.to(ORIGIN);
		Vector ao = a.getNegative();
		// check to see what type of simplex we have
		if (simplex.size() == 3) {
			// then we have a triangle
			Vector b = simplex.get(1);
			Vector c = simplex.get(0);
			// get the edges
			Vector ab = a.to(b);
			Vector ac = a.to(c);
			// get the edge normals
			Vector abPerp = Vector.tripleProduct(ac, ab, ab);
			Vector acPerp = Vector.tripleProduct(ab, ac, ac);
			// see where the origin is at
			if (acPerp.dot(ao) > 0) {
				// the origin lies on the right side of A->C
				// because of the condition for the gjk loop to continue the origin 
				// must lie between A and C so remove B and set the
				// new search direction to A->C perpendicular vector
				simplex.remove(1);
				// this used to be direction.set(Vector.tripleProduct(ac, ao, ac));
				// but was changed since the origin may lie on the segment created
				// by a -> c in which case would produce a zero vector normal
				// calculating ac's normal using b is more robust
				direction.set(acPerp);
			} else {
				// the origin lies on the left side of A->C
				if (abPerp.dot(ao) <= 0) {
					// the origin lies on the right side of A->B and therefore in the
					// triangle, we have an intersection
					return true;
				} else {
					// the origin lies between A and B so remove C and set the
					// search direction to A->B perpendicular vector
					simplex.remove(0);
					// this used to be direction.set(Vector.tripleProduct(ab, ao, ab));
					// but was changed since the origin may lie on the segment created
					// by a -> b in which case would produce a zero vector normal
					// calculating ab's normal using c is more robust
					direction.set(abPerp);
				}
			}
		} else {
			// get the b point
			Vector b = simplex.get(0);
			Vector ab = a.to(b);
			// otherwise we have 2 points (line segment)
			// because of the condition for the gjk loop to continue the origin 
			// must lie in between A and B, so keep both points in the simplex and
			// set the direction to the perp of the line segment towards the origin
			direction.set(Vector.tripleProduct(ab, ao, ab));
			// check for degenerate cases where the origin lies on the segment
			// created by a -> b which will yield a zero edge normal
			if (direction.isZero()) {
				// in this case just choose either normal (left or right)
				direction.set(ab.left());
			}
		}
		return false;
	}
	
	/**
	 * Returns true if the two shapes are separated and fills the given
	 * {@link Separation} object with the minimum distance vector.
	 * @param s1 the first {@link Shape}
	 * @param t1 the first {@link Shape}'s {@link Transform}
	 * @param s2 the second {@link Shape}
	 * @param t2 the second {@link Shape}'s {@link Transform}
	 * @param s the {@link Separation} object to fill
	 * @return boolean
	 */
	public boolean distance(Convex s1, Transform t1, Convex s2, Transform t2, Separation s) {
		// check for circles
		if (s1.isType(Circle.TYPE) && s2.isType(Circle.TYPE)) {
			// if its a circle - circle collision use the faster method
			return super.distance((Circle) s1, t1, (Circle) s2, t2, s);
		}
		// create a Minkowski sum
		MinkowskiSum ms = new MinkowskiSum(s1, t1, s2, t2);
		// create some Minkowski points
		MinkowskiSum.Point a = new MinkowskiSum.Point();
		MinkowskiSum.Point b = new MinkowskiSum.Point();
		MinkowskiSum.Point c = new MinkowskiSum.Point();
		// transform into world space if transform is not null
		Vector c1 = t1.getTransformed(s1.getCenter());
		Vector c2 = t2.getTransformed(s2.getCenter());
		// choose some search direction
		Vector d = c1.to(c2);
		// check for a zero direction vector
		// a zero direction vector indicates that the center's are coincident
		// which guarantees that the convex shapes are overlapping
		if (d.isZero()) return false;
		// add the first point 
		ms.support(d, a);
		// negate the direction
		d.negate();
		// get a second support point
		ms.support(d, b);
		// start the loop
		for (int i = 0; i < this.gjkMaxIterations; i++) {
			// find the point on the simplex (segment) closest to the origin
			d = Segment.getPointOnSegmentClosestToPoint(ORIGIN, b.p, a.p);
			// the vector from the point we found to the origin is the new search direction
			d.negate().normalize();
			// check if d is zero
			if (d.isZero()) {
				// if the closest point is the origin then the shapes are not separated
				return false;
			}
			
			// get the farthest point along d
			ms.support(d, c);
			
			// test if the triangle made by a, b, and c contains the origin
			if (this.containsOrigin(a.p, b.p, c.p)){
				// if it does then return false;
				return false;
			}
			
			// see if the new point is far enough along d
			double projection = c.p.dot(d);
			if ((projection - a.p.dot(d)) < this.gjkDistanceEpsilon) {
				// then the new point we just made is not far enough
				// in the direction of n so we can stop now
				s.normal = d;
				s.distance = -projection;
				// get the closest points
				this.findClosestPoints(a, b, s);
				// return true to indicate separation
				return true;
			}
			
			// find the point of a and b that is closest to the origin
			// and replace the one that is not the closest with the
			// point we just found
			if (a.p.getMagnitudeSquared() < b.p.getMagnitudeSquared()) {
				// a was closest so replace b with c
				b.set(c);
			} else {
				// b was closest so replace a with c
				a.set(c);
			}
		}
		// if we made it here then we know that we hit the maximum number of iterations
		// this is really a catch all termination case
		s.normal = d;
		s.distance = -c.p.dot(d);
		// get the closest points
		this.findClosestPoints(a, b, s);
		// return true to indicate separation
		return true;
	}
	
	/**
	 * Finds the closest points on A and B given the termination simplex and places 
	 * them into point1 and point2 of the given {@link Separation} object.
	 * <p>
	 * NOTE: The support points used to obtain A and B are not good enough since the support
	 * methods of {@link Convex} {@link Shape}s only return the farthest vertex, not
	 * the farthest point.
	 * <p>
	 * This method is a 2D implementation of Johnson's sub algorithm.
	 * <p>
	 * A convex hull is defined as:
	 * <pre> CH(S) = &sum;<sub>i=1&hellip;n</sub> &lambda;<sub>i</sub>P<sub>i</sub> = &lambda;<sub>1</sub>P<sub>1</sub> + &hellip; + &lambda;<sub>n</sub>P<sub>n</sub>
	 * where P<sub>i</sub>&isin;S, &lambda;<sub>i</sub>&isin;R
	 * and &sum;<sub>i&hellip;n</sub> &lambda;<sub>i</sub> = 1
	 * where &lambda;<sub>i</sub> >= 0</pre>
	 * Meaning that any point in the convex hull can be defined by all the points
	 * that make up the convex hull multiplied by some &lambda; value.
	 * <pre> Q = &lambda;<sub>1</sub>P<sub>1</sub> + &hellip; + &lambda;<sub>n</sub>P<sub>n</sub></pre>
	 * From here we will focus on the 2D case.
	 * <p>
	 * Let Q be the closest point to the origin on the termination simplex S.
	 * Q will be closest to the origin if the vector from the origin to
	 * Q is perpendicular to the edge made by the simplex points A and B.
	 * <pre> Let L = (B - A)
	 * Q &middot; L = 0
	 * where a,b &isin; S</pre>
	 * If we substitute for Q we obtain
	 * <pre> (&lambda;<sub>1</sub>P<sub>1</sub> + &hellip; + &lambda;<sub>n</sub>P<sub>n</sub>) &middot; L = 0</pre>
	 * Since the termination simplex only contains 2 points we can reduce to
	 * <pre> (&lambda;<sub>1</sub>A + &lambda;<sub>2</sub>B) &middot; L = 0</pre>
	 * We need to solve for &lambda;<sub>1</sub> and &lambda;<sub>2</sub> which requires two
	 * equations.  We can use the equation above and also the other equation for the 
	 * definition of a convex hull.
	 * <pre> (&lambda;<sub>1</sub>A + &lambda;<sub>2</sub>B) &middot; L = 0
	 * &lambda;<sub>1</sub> + &lambda;<sub>2</sub> = 1</pre>
	 * Solving this system of linear equations yeilds
	 * <pre> &lambda;<sub>2</sub> = -L &middot; A / (L &middot; L)
	 * &lambda;<sub>1</sub> = 1 - &lambda;<sub>2</sub></pre>
	 * Using &lambda;<sub>1</sub> and &lambda;<sub>2</sub> and the same convex hull
	 * definition we can find the closest points CP<sub>1</sub> and CP<sub>2</sub> on 
	 * the convex shapes C<sub>1</sub> and C<sub>2</sub>
	 * <pre> CP<sub>1</sub> = &lambda;<sub>1</sub>A<sub>1</sub> + &lambda;<sub>2</sub>B<sub>1</sub>
	 * CP<sub>2</sub> = &lambda;<sub>1</sub>A<sub>2</sub> + &lambda;<sub>2</sub>B<sub>2</sub>
	 * where A,B &isin; S</pre>
	 * Where A<sub>1</sub> and A<sub>2</sub> are the support points used to create the
	 * point A in the simplex S and where B<sub>1</sub> and B<sub>2</sub> are the support
	 * points used to create the point B in the simplex S.
	 * <pre> A<sub>1</sub>,A<sub>2</sub> &isin; C<sub>1</sub>
	 * B<sub>1</sub>,B<sub>2</sub> &isin; C<sub>2</sub></pre>
	 * If &lambda;<sub>1</sub> or &lambda;<sub>2</sub> is less than zero then this indicates
	 * that the closest points lie at a vertex.  In the case where &lambda;<sub>1</sub> is negative
	 * the closest points are the points, p1 and p2 that made the {@link MinkowskiSum.Point} B.
	 * In the case where &lambda;<sub>2</sub> is negative the closest points are the points, p1
	 * and p2 that made the {@link MinkowskiSum.Point} A.
	 * @param a the first simplex point
	 * @param b the second simplex point
	 * @param s the {@link Separation} object to populate
	 */
	protected void findClosestPoints(MinkowskiSum.Point a, MinkowskiSum.Point b, Separation s) {
		Vector p1 = new Vector();
		Vector p2 = new Vector();
		
		// find lambda1 and lambda2
		Vector l = a.p.to(b.p);
		
		// check if a and b are the same point
		if (l.isZero()) {
			// then the closest points are a or b support points
			p1.set(a.p1);
			p2.set(a.p2);
		} else {
			// otherwise compute lambda1 and lambda2
			double ll = l.dot(l);
			double l2 = -l.dot(a.p) / ll;
			double l1 = 1 - l2;
			
			// check if either lambda1 or lambda2 is less than zero
			if (l1 < 0) {
				// if lambda1 is less than zero then that means that
				// the support points of the Minkowski point B are
				// the closest points
				p1.set(b.p1);
				p2.set(b.p2);
			} else if (l2 < 0) {
				// if lambda2 is less than zero then that means that
				// the support points of the Minkowski point A are
				// the closest points
				p1.set(a.p1);
				p2.set(a.p2);
			} else {
				// compute the closest points using lambda1 and lambda2
				// this is the expanded version of
				// p1 = a.p1.multiply(l1).add(b.p1.multiply(l2));
				// p2 = a.p2.multiply(l1).add(b.p2.multiply(l2));
				p1.x = a.p1.x * l1 + b.p1.x * l2;
				p1.y = a.p1.y * l1 + b.p1.y * l2;
				p2.x = a.p2.x * l1 + b.p2.x * l2;
				p2.y = a.p2.y * l1 + b.p2.y * l2;
			}
		}
		// set the new points in the separation object
		s.point1 = p1;
		s.point2 = p2;
	}
	
	/**
	 * Returns true if the origin is within the triangle given by
	 * a, b, and c.
	 * <p>
	 * If the origin lies on the same side of all the points then we
	 * know that the origin is in the triangle.
	 * <pre> sign(location(origin, a, b)) == sign(location(origin, b, c)) == sign(location(origin, c, a))</pre>
	 * The {@link Segment#getLocation(Vector, Vector, Vector)} method 
	 * can be simplified because we are using the origin as the search point:
	 * <pre> = (b.x - a.x) * (origin.y - a.y) - (origin.x - a.x) * (b.y - a.y)
	 * = (b.x - a.x) * (-a.y) - (-a.x) * (b.y - a.y)
	 * = -a.y * b.x + a.y * a.x + a.x * b.y - a.x * a.y
	 * = -a.y * b.x + a.x * b.y
	 * = a.x * b.y - a.y * b.x
	 * = a.cross(b)</pre>
	 * @param a the first point
	 * @param b the second point
	 * @param c the thrid point
	 * @return boolean
	 */
	protected boolean containsOrigin(Vector a, Vector b, Vector c) {
		double sa = a.cross(b);
		double sb = b.cross(c);
		double sc = c.cross(a);
		// this is sufficient (we do not need to test sb * sc)
		return (sa * sb  > 0 && sa * sc > 0);
	}
	
	/**
	 * Returns the maximum number of iterations the {@link Gjk} algorithm will perform when
	 * computing the distance between two separated bodies.
	 * @return int the number of {@link Gjk} distance iterations
	 * @see #setGjkMaxIterations(int)
	 */
	public int getGjkMaxIterations() {
		return gjkMaxIterations;
	}

	/**
	 * Sets the maximum number of iterations the {@link Gjk} algorithm will perform when
	 * computing the distance between two separated bodies.
	 * <p>
	 * Valid values are in the range [5, &infin;].
	 * @param gjkMaxIterations the maximum number of {@link Gjk} iterations
	 */
	public void setGjkMaxIterations(int gjkMaxIterations) {
		if (gjkMaxIterations < 5) throw new IllegalArgumentException("The GJK distance algorithm requires 5 or more iterations.");
		this.gjkMaxIterations = gjkMaxIterations;
	}

	/**
	 * Returns the {@link Gjk} distance epsilon.
	 * @return double the {@link Gjk} distance epsilon
	 * @see #setGjkDistanceEpsilon(double)
	 */
	public double getGjkDistanceEpsilon() {
		return gjkDistanceEpsilon;
	}

	/**
	 * The minimum distance between two iterations of the {@link Gjk} distance algorithm.
	 * <p>
	 * Valid values are in the range (0, &infin;].
	 * @param gjkDistanceEpsilon the {@link Gjk} distance epsilon
	 */
	public void setGjkDistanceEpsilon(double gjkDistanceEpsilon) {
		if (gjkDistanceEpsilon <= 0) throw new IllegalArgumentException("The GJK distance epsilon must be larger than zero.");
		this.gjkDistanceEpsilon = gjkDistanceEpsilon;
		this.gjkDistanceEpsilonSquared = gjkDistanceEpsilon * gjkDistanceEpsilon;
	}
	
	/**
	 * Returns the {@link MinkowskiPenetrationSolver} used to obtain the
	 * penetration vector and depth.
	 * @return {@link MinkowskiPenetrationSolver}
	 */
	public MinkowskiPenetrationSolver getMinkowskiPenetrationSolver() {
		return mps;
	}
	
	/**
	 * Sets the {@link MinkowskiPenetrationSolver} used to obtain the 
	 * penetration vector and depth.
	 * @param minkowskiPenetrationSolver the {@link MinkowskiPenetrationSolver}
	 */
	public void setMinkowskiPenetrationSolver(MinkowskiPenetrationSolver minkowskiPenetrationSolver) {
		this.mps = minkowskiPenetrationSolver;
	}
}