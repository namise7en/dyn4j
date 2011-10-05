/*
 * Copyright (c) 2011 William Bittle  http://www.dyn4j.org/
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
package org.dyn4j.sandbox.dialogs;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;

import org.dyn4j.dynamics.BodyFixture;
import org.dyn4j.geometry.Convex;
import org.dyn4j.geometry.Geometry;
import org.dyn4j.geometry.Vector2;
import org.dyn4j.sandbox.panels.ConvexHullPolygonPanel;
import org.dyn4j.sandbox.panels.FixturePanel;
import org.dyn4j.sandbox.panels.TransformPanel;
import org.dyn4j.sandbox.utilities.Icons;

/**
 * Dialog to add a new fixture to an existing body.
 * @author William Bittle
 * @version 1.0.0
 * @since 1.0.0
 */
public class AddConvexHullFixtureDialog extends JDialog implements ActionListener {
	/** The version id */
	private static final long serialVersionUID = 1665773996550270370L;

	/** The dialog canceled flag */
	private boolean canceled = true;

	/** The fixture config panel */
	private FixturePanel pnlFixture;
	
	/** The transform config panel */
	private TransformPanel pnlTransform;
	
	/** The convex hull panel */
	private ConvexHullPolygonPanel pnlPolygon;

	/** The fixture used during configuration */
	private BodyFixture fixture;
	
	/**
	 * Full constructor.
	 * @param owner the dialog owner
	 */
	private AddConvexHullFixtureDialog(Window owner) {
		super(owner, "Add Convex Hull Fixture", ModalityType.APPLICATION_MODAL);
		
		this.setIconImage(Icons.ADD_CONVEX_HULL.getImage());
		
		this.pnlPolygon = new ConvexHullPolygonPanel();
		
		Container container = this.getContentPane();
		
		GroupLayout layout = new GroupLayout(container);
		container.setLayout(layout);
		
		// create a text pane for the local transform tab
		JTextPane lblText = new JTextPane();
		lblText = new JTextPane();
		lblText.setContentType("text/html");
		lblText.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEtchedBorder(), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		lblText.setText(
				"<html>The local transform is used to move and rotate a fixture within " +
				"body coordinates, i.e. relative to the body's center of mass.  Unlike " +
				"the transform on the body, this transform is applied directly to the fixture's " +
				"shape data and therefore not 'saved' directly.</html>");
		lblText.setEditable(false);
		lblText.setPreferredSize(new Dimension(350, 120));
		
		// have to create it with an arbitrary shape
		this.fixture = new BodyFixture(Geometry.createCircle(1.0));
		this.fixture.setUserData("Fixture" + AddConvexFixtureDialog.N);
		this.pnlFixture = new FixturePanel(this, this.fixture);
		this.pnlTransform = new TransformPanel(lblText);
		
		JTabbedPane tabs = new JTabbedPane();
		
		tabs.addTab("Shape", this.pnlPolygon);
		tabs.addTab("Fixture", this.pnlFixture);
		tabs.addTab("Local Transform", this.pnlTransform);
		
		JButton btnCancel = new JButton("Cancel");
		JButton btnAdd = new JButton("Add");
		btnCancel.setActionCommand("cancel");
		btnAdd.setActionCommand("add");
		btnCancel.addActionListener(this);
		btnAdd.addActionListener(this);
		
		layout.setAutoCreateContainerGaps(true);
		layout.setAutoCreateGaps(true);
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(tabs)
						.addGroup(layout.createSequentialGroup()
								.addComponent(btnCancel)
								.addComponent(btnAdd))));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(tabs)
				.addGroup(layout.createParallelGroup()
						.addComponent(btnCancel)
						.addComponent(btnAdd)));
		
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
			// check the shape panel's input
			if (this.pnlPolygon.isValidInput()) {
				// check the fixture input
				if (this.pnlFixture.isValidInput()) {
					// check the transform input
					if (this.pnlTransform.isValidInput()) {
						// if its valid then close the dialog
						this.canceled = false;
						this.setVisible(false);
					} else {
						this.pnlTransform.showInvalidInputMessage(this);
					}
				} else {
					this.pnlFixture.showInvalidInputMessage(this);
				}
			} else {
				// if its not valid then show an error message
				this.pnlPolygon.showInvalidInputMessage(this);
			}
		}
	}
	
	/**
	 * Shows a convex hull dialog.
	 * <p>
	 * Returns null if the user canceled or closed the dialog.
	 * @param owner the dialog owner
	 * @return BodyFixture
	 */
	public static final BodyFixture show(Window owner) {
		AddConvexHullFixtureDialog dialog = new AddConvexHullFixtureDialog(owner);
		dialog.setLocationRelativeTo(owner);
		dialog.setVisible(true);
		// control returns to this method when the dialog is closed
		
		// check the canceled flag
		if (!dialog.canceled) {
			// get the list of convex shapes
			Convex convex = dialog.pnlPolygon.getShape();
			
			// get the general fixture (properties will be copied into all fixtures created)
			BodyFixture fixture = dialog.fixture;
			
			// apply any local transform
			Vector2 tx = dialog.pnlTransform.getTranslation();
			double a = dialog.pnlTransform.getRotation();
			if (!tx.isZero()) {
				convex.translate(tx);
			}
			if (a != 0.0) {
				convex.rotate(a);
			}
			
			// create the fixture so we can set the shape
			BodyFixture newFixture = new BodyFixture(convex);
			// copy the other properties over
			newFixture.setUserData(fixture.getUserData());
			newFixture.setDensity(fixture.getDensity());
			newFixture.setFilter(fixture.getFilter());
			newFixture.setFriction(fixture.getFriction());
			newFixture.setRestitution(fixture.getRestitution());
			newFixture.setSensor(fixture.isSensor());
			
			// increment the fixture number
			synchronized (AddConvexFixtureDialog.class) {
				AddConvexFixtureDialog.N++;
			}
			
			return newFixture;
		}
		
		// if it was canceled then return null
		return null;
	}
}
