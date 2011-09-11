package org.dyn4j.sandbox.persist;

import org.dyn4j.collision.CategoryFilter;
import org.dyn4j.collision.Filter;
import org.dyn4j.collision.RectangularBounds;
import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.dynamics.World;
import org.dyn4j.dynamics.joint.AngleJoint;
import org.dyn4j.dynamics.joint.DistanceJoint;
import org.dyn4j.dynamics.joint.FrictionJoint;
import org.dyn4j.dynamics.joint.Joint;
import org.dyn4j.dynamics.joint.MouseJoint;
import org.dyn4j.dynamics.joint.PrismaticJoint;
import org.dyn4j.dynamics.joint.PulleyJoint;
import org.dyn4j.dynamics.joint.RevoluteJoint;
import org.dyn4j.dynamics.joint.RopeJoint;
import org.dyn4j.dynamics.joint.WeldJoint;
import org.dyn4j.dynamics.joint.WheelJoint;
import org.dyn4j.geometry.Circle;
import org.dyn4j.geometry.Mass;
import org.dyn4j.geometry.Polygon;
import org.dyn4j.geometry.Rectangle;
import org.dyn4j.geometry.Segment;
import org.dyn4j.geometry.Shape;
import org.dyn4j.geometry.Transform;
import org.dyn4j.geometry.Triangle;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.SandboxBody;

/**
 * Class used to export the world to xml.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class XmlGenerator {
	/**
	 * Returns the xml for the given world object.
	 * @param world the world
	 * @return String
	 */
	public static final String toXml(World world) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		sb.append("<World xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:noNamespaceSchemaLocation=\"http://www.dyn4j.org/Sandbox/sandbox.xsd\">");
		
		// bounds
		if (world.getBounds() instanceof RectangularBounds) {
			RectangularBounds bounds = (RectangularBounds)world.getBounds();
			Rectangle r = bounds.getBounds();
			sb.append("<Bounds>");
			sb.append("<Rectangle Id=\"").append(r.getId()).append("\">")
			.append(XmlGenerator.toXml(r.getCenter(), "LocalCenter"))
			.append("<Width>").append(r.getWidth()).append("</Width>")
			.append("<Height>").append(r.getHeight()).append("</Height>")
			.append("<LocalRotation>").append(r.getRotation()).append("</LocalRotation>")
			.append("</Rectangle>");
			sb.append(XmlGenerator.toXml(bounds.getTransform()));
			sb.append("</Bounds>");
		}
		
		// bodies
		sb.append("<Bodies>");
		int bSize = world.getBodyCount();
		for (int i = 0; i < bSize; i++) {
			SandboxBody body = (SandboxBody)world.getBody(i);
			sb.append(XmlGenerator.toXml(body));
		}
		sb.append("</Bodies>");
		
		// joints
		sb.append("<Joints>");
		int jSize = world.getJointCount();
		for (int i = 0; i < jSize; i++) {
			Joint joint = world.getJoint(i);
			sb.append(XmlGenerator.toXml(joint));
		}
		sb.append("</Joints>");
		
		sb.append("</World>");
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for the given vector with the given element name.
	 * @param v the vector
	 * @param name the element name
	 * @return String
	 */
	private static final String toXml(Vector2 v, String name) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<")
		.append(name)
		.append(" x=\"")
		.append(v.x)
		.append("\" y=\"")
		.append(v.y)
		.append("\" />");
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for the given transform object.
	 * @param t the transform
	 * @return String
	 */
	private static final String toXml(Transform t) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<Transform>")
		.append(XmlGenerator.toXml(t.getTranslation(), "Translation"))
		.append("<Rotation>")
		.append(t.getRotation())
		.append("</Rotation>")
		.append("</Transform>");
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for the given shape object.
	 * @param shape the shape
	 * @return String
	 */
	private static final String toXml(Shape shape) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<Shape Id=\"")
		.append(shape.getId())
		.append("\" xsi:type=\"");
		String c = XmlGenerator.toXml(shape.getCenter(), "LocalCenter");
		if (shape instanceof Circle) {
			sb.append("Circle\">")
			.append(c)
			.append("<Radius>")
			.append(((Circle)shape).getRadius())
			.append("</Radius>");
		} else if (shape instanceof Rectangle) {
			Rectangle r = (Rectangle)shape;
			sb.append("Rectangle\">")
			.append(c)
			.append("<Width>").append(r.getWidth()).append("</Width>")
			.append("<Height>").append(r.getHeight()).append("</Height>")
			.append("<LocalRotation>").append(r.getRotation()).append("</LocalRotation>");
		} else if (shape instanceof Triangle) {
			Triangle t = (Triangle)shape;
			Vector2[] vs = t.getVertices();
			sb.append("Triangle\">")
			.append(c);
			for (Vector2 v : vs) {
				sb.append(XmlGenerator.toXml(v, "Vertex"));
			}
		} else if (shape instanceof Polygon) {
			Polygon p = (Polygon)shape;
			Vector2[] vs = p.getVertices();
			sb.append("Polygon\">")
			.append(c);
			for (Vector2 v : vs) {
				sb.append(XmlGenerator.toXml(v, "Vertex"));
			}
		} else if (shape instanceof Segment) {
			Segment s = (Segment)shape;
			Vector2[] vs = s.getVertices();
			sb.append("Segment\">")
			.append(c);
			for (Vector2 v : vs) {
				sb.append(XmlGenerator.toXml(v, "Vertex"));
			}
		} else {
			throw new UnsupportedOperationException(shape.getClass().getName() + " is not currently implemented in the sandbox xml persist module.");
		}
		
		sb.append("</Shape>");
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for the given filter object.
	 * @param filter the filter
	 * @return String
	 */
	private static final String toXml(Filter filter) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<Filter xsi:type=\"");
		
		if (filter == Filter.DEFAULT_FILTER) {
			sb.append("DefaultFilter\" />");
		} else if (filter instanceof CategoryFilter) {
			CategoryFilter cf = (CategoryFilter)filter;
			sb.append("<PartOfGroups>");
			sb.append(XmlGenerator.toXml(cf.getCategory()));
			sb.append("</PartOfGroups>");
			sb.append("<CollideWithGroups>");
			sb.append(XmlGenerator.toXml(cf.getMask()));
			sb.append("</CollideWithGroups>");
			sb.append("</Filter>");
		} else {
			throw new UnsupportedOperationException(filter.getClass().getName() + " is not currently implemented in the sandbox xml persist module.");
		}
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for all the categories for the given cateories int.
	 * @param categories the categories
	 * @return String
	 */
	private static final String toXml(int categories) {
		StringBuilder sb = new StringBuilder();
		
		int mask = 1;
		for (int i = 1; i < 31; i++) {
			if ((categories & mask) == mask) {
				// append the group
				sb.append("<Group" + i + " Value=\"" + mask + "\" />");
			}
			mask *= 2;
		}
		
		if ((categories & Integer.MAX_VALUE) == Integer.MAX_VALUE) {
			// append the group
			sb.append("<All" + " Value=\"" + Integer.MAX_VALUE + "\" />");
		}
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for a fixture object.
	 * @param fixture the fixture
	 * @return String
	 */
	private static final String toXml(BodyFixture fixture) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<Fixture Id=\"")
		.append(fixture.getId())
		.append("\" Name=\"")
		.append(fixture.getUserData())
		.append("\">")
		.append(XmlGenerator.toXml(fixture.getShape()))
		.append(XmlGenerator.toXml(fixture.getFilter()))
		.append("<Sensor>").append(fixture.isSensor()).append("</Sensor>")
		.append("<Density>").append(fixture.getDensity()).append("</Density>")
		.append("<Friction>").append(fixture.getFriction()).append("</Friction>")
		.append("<Restitution>").append(fixture.getRestitution()).append("</Restitution>")
		.append("</Fixture>");
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for the given mass object.
	 * @param mass the mass
	 * @param explicit true if the mass was set explicitly
	 * @return String
	 */
	private static final String toXml(Mass mass, boolean explicit) {
		StringBuilder sb = new StringBuilder();
		
		// create a temporary mass from the passed in one and change the mass type
		// so that we can get the mass and inertia values for masses with infinite
		// or fixed types
		Mass m = new Mass(mass);
		m.setType(Mass.Type.NORMAL);
		
		sb.append("<Mass>")
		.append(XmlGenerator.toXml(mass.getCenter(), "LocalCenter"))
		.append("<Type>").append(mass.getType()).append("</Type>")
		.append("<Mass>").append(m.getMass()).append("</Mass>")
		.append("<Inertia>").append(m.getInertia()).append("</Inertia>")
		.append("<Explicit>").append(explicit).append("</Explicit>")
		.append("</Mass>");
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for the given body object.
	 * @param body the body
	 * @return String
	 */
	private static final String toXml(SandboxBody body) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<Body Id=\"")
		.append(body.getId())
		.append("\" Name=\"")
		.append(body.getUserData())
		.append("\">");
		
		// output fixtures
		sb.append("<Fixtures>");
		int fSize = body.getFixtureCount();
		for (int i = 0; i < fSize; i++) {
			BodyFixture bf = body.getFixture(i);
			sb.append(XmlGenerator.toXml(bf));
		}
		sb.append("</Fixtures>");
		
		// output transform
		sb.append(XmlGenerator.toXml(body.getTransform()));
		
		// output mass
		sb.append(XmlGenerator.toXml(body.getMass(), body.isMassExplicit()));
		
		sb.append(XmlGenerator.toXml(body.getVelocity(), "Velocity"));
		sb.append("<AngularVelocity>").append(body.getAngularVelocity()).append("</AngularVelocity>");
		sb.append("<AutoSleep>").append(body.isAutoSleepingEnabled()).append("</AutoSleep>");
		sb.append("<Asleep>").append(body.isAsleep()).append("</Asleep>");
		sb.append("<Active>").append(body.isActive()).append("</Active>");
		sb.append("<Bullet>").append(body.isBullet()).append("</Bullet>");
		sb.append("<LinearDamping>").append(body.getLinearDamping()).append("</LinearDamping>");
		sb.append("<AngularDamping>").append(body.getAngularDamping()).append("</AngularDamping>");
		sb.append("<GravityScale>").append(body.getGravityScale()).append("</GravityScale>");
		
		sb.append("</Body>");
		
		return sb.toString();
	}
	
	/**
	 * Returns the xml for the given joint.
	 * @param joint the joint
	 * @return String
	 */
	private static final String toXml(Joint joint) {
		StringBuilder sb = new StringBuilder();
		
		sb.append("<Joint Id=\"")
		.append(joint.getId())
		.append("\" Name=\"")
		.append(joint.getUserData())
		.append("\" xsi:type=\"")
		.append(joint.getClass().getSimpleName())
		.append("\">");
		
		sb.append("<BodyId1>").append(joint.getBody1().getId()).append("</BodyId1>");
		sb.append("<BodyId2>").append(joint.getBody2().getId()).append("</BodyId2>");
		sb.append("<CollisionAllowed>").append(joint.isCollisionAllowed()).append("</CollisionAllowed>");
		
		if (joint instanceof AngleJoint) {
			AngleJoint aj = (AngleJoint)joint;
			sb.append("<LowerLimit>").append(aj.getLowerLimit()).append("</LowerLimit>");
			sb.append("<UpperLimit>").append(aj.getUpperLimit()).append("</UpperLimit>");
			sb.append("<LimitEnabled>").append(aj.isLimitEnabled()).append("</LimitEnabled>");
			sb.append("<ReferenceAngle>").append(aj.getReferenceAngle()).append("</ReferenceAngle>");
		} else if (joint instanceof DistanceJoint) {
			DistanceJoint dj = (DistanceJoint)joint;
			sb.append(XmlGenerator.toXml(dj.getAnchor1(), "Anchor1"));
			sb.append(XmlGenerator.toXml(dj.getAnchor2(), "Anchor2"));
			sb.append("<Frequency>").append(dj.getFrequency()).append("</Frequency>");
			sb.append("<DampingRatio>").append(dj.getDampingRatio()).append("</DampingRatio>");
			sb.append("<Distance>").append(dj.getDistance()).append("</Distance>");
		} else if (joint instanceof FrictionJoint) {
			FrictionJoint fj = (FrictionJoint)joint;
			sb.append(XmlGenerator.toXml(fj.getAnchor1(), "Anchor"));
			sb.append("<MaximumForce>").append(fj.getMaximumForce()).append("</MaximumForce>");
			sb.append("<MaximumTorque>").append(fj.getMaximumTorque()).append("</MaximumTorque>");
		} else if (joint instanceof MouseJoint) {
			MouseJoint mj = (MouseJoint)joint;
			sb.append(XmlGenerator.toXml(mj.getAnchor2(), "Anchor"));
			sb.append(XmlGenerator.toXml(mj.getAnchor1(), "Target"));
			sb.append("<Frequency>").append(mj.getFrequency()).append("</Frequency>");
			sb.append("<DampingRatio>").append(mj.getDampingRatio()).append("</DampingRatio>");
			sb.append("<MaximumForce>").append(mj.getMaximumForce()).append("</MaximumForce>");
		} else if (joint instanceof PrismaticJoint) {
			PrismaticJoint pj = (PrismaticJoint)joint;
			sb.append(XmlGenerator.toXml(pj.getAnchor1(), "Anchor"));
			sb.append(XmlGenerator.toXml(pj.getAxis(), "Axis"));
			sb.append("<LowerLimit>").append(pj.getLowerLimit()).append("</LowerLimit>");
			sb.append("<UpperLimit>").append(pj.getUpperLimit()).append("</UpperLimit>");
			sb.append("<LimitEnabled>").append(pj.isLimitEnabled()).append("</LimitEnabled>");
			sb.append("<MotorSpeed>").append(pj.getMotorSpeed()).append("</MotorSpeed>");
			sb.append("<MaximumMotorForce>").append(pj.getMaximumMotorForce()).append("</MaximumMotorForce>");
			sb.append("<MotorEnabled>").append(pj.isMotorEnabled()).append("</MotorEnabled>");
			sb.append("<ReferenceAngle>").append(pj.getReferenceAngle()).append("</ReferenceAngle>");
		} else if (joint instanceof PulleyJoint) {
			PulleyJoint pj = (PulleyJoint)joint;
			sb.append(XmlGenerator.toXml(pj.getPulleyAnchor1(), "PulleyAnchor1"));
			sb.append(XmlGenerator.toXml(pj.getPulleyAnchor2(), "PulleyAnchor2"));
			sb.append(XmlGenerator.toXml(pj.getAnchor1(), "BodyAnchor1"));
			sb.append(XmlGenerator.toXml(pj.getAnchor2(), "BodyAnchor2"));
			sb.append("<Ratio>").append(pj.getRatio()).append("</Ratio>");
		} else if (joint instanceof RevoluteJoint) {
			RevoluteJoint rj = (RevoluteJoint)joint;
			sb.append(XmlGenerator.toXml(rj.getAnchor1(), "Anchor"));
			sb.append("<LowerLimit>").append(rj.getLowerLimit()).append("</LowerLimit>");
			sb.append("<UpperLimit>").append(rj.getUpperLimit()).append("</UpperLimit>");
			sb.append("<LimitEnabled>").append(rj.isLimitEnabled()).append("</LimitEnabled>");
			sb.append("<MotorSpeed>").append(rj.getMotorSpeed()).append("</MotorSpeed>");
			sb.append("<MaximumMotorTorque>").append(rj.getMaximumMotorTorque()).append("</MaximumMotorTorque>");
			sb.append("<MotorEnabled>").append(rj.isMotorEnabled()).append("</MotorEnabled>");
			sb.append("<ReferenceAngle>").append(rj.getReferenceAngle()).append("</ReferenceAngle>");
		} else if (joint instanceof RopeJoint) {
			RopeJoint rj = (RopeJoint)joint;
			sb.append(XmlGenerator.toXml(rj.getAnchor1(), "Anchor1"));
			sb.append(XmlGenerator.toXml(rj.getAnchor2(), "Anchor2"));
			sb.append("<LowerLimit>").append(rj.getLowerLimit()).append("</LowerLimit>");
			sb.append("<UpperLimit>").append(rj.getUpperLimit()).append("</UpperLimit>");
			sb.append("<LowerLimitEnabled>").append(rj.isLowerLimitEnabled()).append("</LowerLimitEnabled>");
			sb.append("<UpperLimitEnabled>").append(rj.isUpperLimitEnabled()).append("</UpperLimitEnabled>");
		} else if (joint instanceof WeldJoint) {
			WeldJoint wj = (WeldJoint)joint;
			sb.append(XmlGenerator.toXml(wj.getAnchor1(), "Anchor"));
			sb.append("<ReferenceAngle>").append(wj.getReferenceAngle()).append("</ReferenceAngle>");
		} else if (joint instanceof WheelJoint) {
			WheelJoint wj = (WheelJoint)joint;
			sb.append(XmlGenerator.toXml(wj.getAnchor1(), "Anchor"));
			sb.append(XmlGenerator.toXml(wj.getAxis(), "Axis"));
			sb.append("<MotorSpeed>").append(wj.getMotorSpeed()).append("</MotorSpeed>");
			sb.append("<MaximumMotorTorque>").append(wj.getMaximumMotorTorque()).append("</MaximumMotorTorque>");
			sb.append("<MotorEnabled>").append(wj.isMotorEnabled()).append("</MotorEnabled>");
			sb.append("<Frequency>").append(wj.getFrequency()).append("</Frequency>");
			sb.append("<DampingRatio>").append(wj.getDampingRatio()).append("</DampingRatio>");
		} else {
			throw new UnsupportedOperationException(joint.getClass().getName() + " is not currently implemented in the sandbox xml persist module.");
		}
		
		sb.append("</Joint>");
		
		return sb.toString();
	}
}
