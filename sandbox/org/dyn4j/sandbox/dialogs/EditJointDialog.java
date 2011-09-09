package org.dyn4j.sandbox.dialogs;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;

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
import org.dyn4j.sandbox.SandboxBody;
import org.dyn4j.sandbox.panels.AngleJointPanel;
import org.dyn4j.sandbox.panels.DistanceJointPanel;
import org.dyn4j.sandbox.panels.FrictionJointPanel;
import org.dyn4j.sandbox.panels.JointPanel;
import org.dyn4j.sandbox.panels.MouseJointPanel;
import org.dyn4j.sandbox.panels.PrismaticJointPanel;
import org.dyn4j.sandbox.panels.PulleyJointPanel;
import org.dyn4j.sandbox.panels.RevoluteJointPanel;
import org.dyn4j.sandbox.panels.RopeJointPanel;
import org.dyn4j.sandbox.panels.WeldJointPanel;
import org.dyn4j.sandbox.panels.WheelJointPanel;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Dialog used to create a joint.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class EditJointDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = 2762180698167078099L;

	/** The dialog canceled flag */
	private boolean canceled = true;
	
	/** The joint panel */
	private JointPanel pnlJoint;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 * @param joint the joint
	 */
	private EditJointDialog(Window owner, Joint joint) {
		super(owner, ModalityType.APPLICATION_MODAL);
		
		SandboxBody b1 = (SandboxBody)joint.getBody1();
		SandboxBody b2 = (SandboxBody)joint.getBody2();
		SandboxBody[] bodies = new SandboxBody[]{b1, b2};
		
		if (joint instanceof AngleJoint) {
			this.pnlJoint = new AngleJointPanel((AngleJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_ANGLE_JOINT.getImage());
			this.setTitle("Edit Angle Joint");
		} else if (joint instanceof DistanceJoint) {
			this.pnlJoint = new DistanceJointPanel((DistanceJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_DISTANCE_JOINT.getImage());
			this.setTitle("Edit Distance Joint");
		} else if (joint instanceof FrictionJoint) {
			this.pnlJoint = new FrictionJointPanel((FrictionJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_FRICTION_JOINT.getImage());
			this.setTitle("Edit Friction Joint");
		} else if (joint instanceof MouseJoint) {
			this.pnlJoint = new MouseJointPanel((MouseJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_MOUSE_JOINT.getImage());
			this.setTitle("Edit Mouse Joint");
		} else if (joint instanceof PrismaticJoint) {
			this.pnlJoint = new PrismaticJointPanel((PrismaticJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_PRISMATIC_JOINT.getImage());
			this.setTitle("Edit Prismatic Joint");
		} else if (joint instanceof PulleyJoint) {
			this.pnlJoint = new PulleyJointPanel((PulleyJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_PULLEY_JOINT.getImage());
			this.setTitle("Edit Pulley Joint");
		} else if (joint instanceof RevoluteJoint) {
			this.pnlJoint = new RevoluteJointPanel((RevoluteJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_REVOLUTE_JOINT.getImage());
			this.setTitle("Edit Revolute Joint");
		} else if (joint instanceof RopeJoint) {
			this.pnlJoint = new RopeJointPanel((RopeJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_ROPE_JOINT.getImage());
			this.setTitle("Edit Rope Joint");
		} else if (joint instanceof WeldJoint) {
			this.pnlJoint = new WeldJointPanel((WeldJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_WELD_JOINT.getImage());
			this.setTitle("Edit Weld Joint");
		} else if (joint instanceof WheelJoint) {
			this.pnlJoint = new WheelJointPanel((WheelJoint)joint, bodies, true);
			this.setIconImage(Icons.EDIT_WHEEL_JOINT.getImage());
			this.setTitle("Edit Wheel Joint");
		}
		
		JTextPane pneInfo = new JTextPane();
		pneInfo.setContentType("text/html");
		pneInfo.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		pneInfo.setText("<html>" + this.pnlJoint.getDescription() + "</html>");
		pneInfo.setPreferredSize(new Dimension(400, 200));
		pneInfo.setEditable(false);
		
		JPanel pnlInfo = new JPanel();
		pnlInfo.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		pnlInfo.setLayout(new BorderLayout(5, 5));
		pnlInfo.add(pneInfo);
		
		JTabbedPane tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Joint", this.pnlJoint);
		tabbedPane.addTab("Information", pnlInfo);
		
		JButton btnCancel = new JButton("Cancel");
		JButton btnCreate = new JButton("Save");
		btnCancel.setActionCommand("cancel");
		btnCreate.setActionCommand("save");
		btnCancel.addActionListener(this);
		btnCreate.addActionListener(this);
		
		Container container = this.getContentPane();
		
		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(tabbedPane)
						.addGroup(layout.createSequentialGroup()
								.addComponent(btnCancel)
								.addComponent(btnCreate))));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(tabbedPane)
				.addGroup(layout.createParallelGroup()
						.addComponent(btnCancel)
						.addComponent(btnCreate)));
		
		this.pack();
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		// check the action command
		if ("cancel".equals(event.getActionCommand())) {
			// if its canceled then set the canceled flag and
			// close the dialog
			this.setVisible(false);
			this.canceled = true;
		} else {
			// check the joint input
			if (this.pnlJoint.isValidInput()) {
				// if its valid then close the dialog
				this.canceled = false;
				this.setVisible(false);
			} else {
				this.pnlJoint.showInvalidInputMessage(this);
			}
		}
	}
	
	/**
	 * Shows a create joint dialog with the given joint panel and returns a new Joint
	 * object if the user entered valid input and clicked the create button.
	 * <p>
	 * Returns null if the dialog was canceled.
	 * @param owner the dialog owner
	 * @param joint the joint to edit
	 */
	public static void show(Window owner, Joint joint) {
		EditJointDialog dialog = new EditJointDialog(owner, joint);
		dialog.setVisible(true);
		
		if (!dialog.canceled) {
			// set the properties
			dialog.pnlJoint.setJoint(joint);
		}
	}
}