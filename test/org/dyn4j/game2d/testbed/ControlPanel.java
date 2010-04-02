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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.naming.ConfigurationException;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.dyn4j.game2d.collision.broadphase.BroadphaseDetector;
import org.dyn4j.game2d.collision.manifold.ManifoldSolver;
import org.dyn4j.game2d.collision.narrowphase.NarrowphaseDetector;
import org.dyn4j.game2d.dynamics.Settings;

/**
 * The JFrame that controls the TestBed.
 * @author William Bittle
 */
public class ControlPanel extends JFrame {
	/** the version id */
	private static final long serialVersionUID = -461371622259288105L;
	
	/** The class logger */
	private static final Logger LOGGER = Logger.getLogger(ControlPanel.class.getName());
	
	/** Resource bundle containing the tests to load */
	private static ResourceBundle TESTS_BUNDLE = ResourceBundle.getBundle("org.dyn4j.game2d.testbed.tests");
	
	/** The controls for all tests */
	private static final String[][] CONTROLS = new String[][] {
		{"Esc or e", "Exits simulation"},
		{"Pause/Break or p", "Pauses simulation"},
		{"+ or Mouse Wheel Up", "Zooms in"},
		{"- or Mouse Wheel Down", "Zooms out"},
		{"Left", "Pans left"},
		{"Right", "Pans right"},
		{"Up", "Pans up"},
		{"Down", "Pans down"},
		{"Home or h", "Centers the camera"},
		{"c", "Opens the Test Bed Control Panel"},
		{"r", "Resets the current test"},
		{"Space", "Toggles step mode"},
		{"s", "Performs 1 step when in step mode"},
		{"Left Mouse Button", "Click and hold to select a shape."},
		{"Move Mouse", "Move to translate the selected shape."},
		{"z", "Hold to rotate the selected shape."},
		{"o", "Outputs all the bodies current state to std out."},
		{"b", "Launches a bomb from the left side."}
		};
	
	/** Map of available test to run */
	private Map<String, Test> tests;
	
	/** The current test */
	private Test test;
	
	/** The panel containing the test controls */
	private JPanel pnlTestControls = null;
	
	/** The panel for the controls tab */
	private JPanel pnlControls = null;
	
	/** The combo box for selecting a test */
	private JComboBox cmbTests = null;
	
	/** The description field for the selected test */
	private JTextPane panTestDescription = null;

	/** The combo box for selecting a broad-phase CD algorithm */
	private JComboBox cmbBPCDAlgo = null;
		
	/** The combo box for selecting a narrow-phase CD algorithm */
	private JComboBox cmbNPCDAlgo = null;

	/** The combo box for selecting a manifold solving algorithm */
	private JComboBox cmbMSAlgo = null;
	
	/** The selected broad-phase collision detection algorithm */
	private String selectedBPCDAlgo = "Sap";
	
	/** The selected narrow-phase collision detection algorithm */
	private String selectedNPCDAlgo = "Gjk";
	
	/** The selected manifold solving algorithm */
	private String selectedMSAlgo = "Clip";
	
	/** The image icon to show for help */
	private Icon helpIcon = null;
	
	/**
	 * Default constructor.
	 * @throws ConfigurationException if the tests.properties is missing or not configured
	 */
	public ControlPanel() throws ConfigurationException {
		super("Test Bed Control Panel");
		
		// load the help icon
		helpIcon = new ImageIcon(this.getClass().getResource("/help.gif"), "Hover for help");
		
		// initialize the map
		this.tests = new HashMap<String, Test>();
		// read in all the tests
		Enumeration<String> keys = TESTS_BUNDLE.getKeys();
		// loop through the keys (test names)
		while (keys.hasMoreElements()) {
			// get the key
			String key = keys.nextElement();
			// skip keys with "."s in them
			if (key.contains(".")) continue;
			// get the value (the test class name)
			String className = TESTS_BUNDLE.getString(key);
			try {
				// attempt to load the class
				Class<?> clazz = Class.forName(className);
				// attempt to create an instance of it
				Test test = (Test) clazz.newInstance();
				// initialize the test
				test.initialize();
				// set the test name
				test.name = key;
				// add it to the test map
				this.tests.put(key, test);
			} catch (ClassNotFoundException e) {
				// log the exception but ignore it
				LOGGER.throwing("TestBed", "constructor", e);
			} catch (ClassCastException e) {
				// log the exception but ignore it
				LOGGER.throwing("TestBed", "constructor", e);
			} catch (IllegalAccessException e) {
				// log the exception but ignore it
				LOGGER.throwing("TestBed", "constructor", e);
			} catch (InstantiationException e) {
				// log the exception but ignore it
				LOGGER.throwing("TestBed", "constructor", e);
			}
		}
		// make sure the map has at least one test
		if (this.tests.size() == 0) {
			throw new ConfigurationException("At least one test must be configured in the tests.properties file.");
		}
		// default to the test named default
		String defaultTest = TESTS_BUNDLE.getString("default.test");
		// attempt to find the test
		Test test = this.tests.get(defaultTest);
		// verify the test was found
		if (test != null) {
			this.setTest(test);
		} else {
			// otherwise set it to the first test
			Test[] tests = new Test[this.tests.size()];
			this.tests.values().toArray(tests);
			this.setTest(tests[0]);
		}
		
		// create the GUI
		this.createGUI();
	}
	
	/**
	 * Sets the current test.
	 * @param test the test to run
	 */
	private void setTest(Test test) {
		// set the new test
		this.test = test;
	}
	
	/**
	 * Sets the current test.
	 * @param testName the test to run
	 */
	private void setTest(String testName) {
		// get the test
		Test test = this.tests.get(testName);
		// verify its not null
		if (test != null) {
			// set the test
			this.setTest(test);
		}
	}
	
	/**
	 * Returns the current {@link Test}.
	 * @return {@link Test} the current {@link Test}
	 */
	public Test getTest() {
		return this.test;
	}
	
	/**
	 * Returns the current {@link NarrowphaseDetector} name.
	 * @return String the current {@link NarrowphaseDetector} name
	 */
	public String getNPCDAlgorithm() {
		return this.selectedNPCDAlgo;
	}
	
	/**
	 * Returns the current {@link BroadphaseDetector} name.
	 * @return String the current {@link BroadphaseDetector} name
	 */
	public String getBPCDAlgorithm() {
		return this.selectedBPCDAlgo;
	}
	
	/**
	 * Returns the current {@link ManifoldSolver} name.
	 * @return String the current {@link ManifoldSolver} name
	 */
	public String getMSAlgorithm() {
		return this.selectedMSAlgo;
	}
	
	/**
	 * Creates the GUI for all configuration.
	 */
	private void createGUI() {
		// create the frame
		this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

		// create a tabbed pane
		JTabbedPane tabs = new JTabbedPane();
		tabs.setBorder(new EmptyBorder(5, 5, 5, 5));

		// create the container for the control listing tab
		this.pnlControls = this.createControlsPanel();
		// create a tab from the panel
		tabs.addTab(" Controls ", null, this.pnlControls, "View the list of controls.");
		
		// create a container for the tests selection tab
		JPanel pnlTest = this.createSelectTestPanel();
		// create the tab from the panel
		tabs.addTab(" Tests ", null, pnlTest, "Select the test to run.");
		
		// create a container for the tests selection tab
		JPanel pnlDraw = this.createDrawingOptionsPanel();
		// create the tab from the panel
		tabs.addTab(" Drawing Options ", null, pnlDraw, "Select drawing options.");

		JPanel pnlSettings = this.createSimulationSettingsPanel();
		// create a tab from the panel
		tabs.addTab(" Simulation Settings ", null, pnlSettings, "Set simulation settings.");

		// add the tabs to the frame
		this.add(tabs, BorderLayout.CENTER);
		
		// set the preferred width
		this.setPreferredSize(new Dimension(450, 580));
		
		// pack the layout
		this.pack();
	}
	
	/**
	 * Creates the panel for the controls tab.
	 * @return JPanel the controls tab panel
	 */
	private JPanel createControlsPanel() {
		// create the container for the control listing tab
		JPanel panel = new JPanel();
		// create border
		Border border = new EmptyBorder(5, 5, 5, 5);
		// set the layout to null so we can absolutely position the labels
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		// set the panel border and background color
		panel.setBorder(border);

		// create some insets for all the panels
		Insets insets = new Insets(2, 2, 2, 2);
		
		//////////////////////////////////////////////////
		// controls group
		//////////////////////////////////////////////////
		
		// create the panel
		JPanel pnlControls = new JPanel();
		pnlControls.setBorder(new TitledBorder("Controls"));
		pnlControls.setLayout(new GridBagLayout());
		
		int size = CONTROLS.length;
		int row = 0;
		// create all the labels for the standard controls
		for (String[] control : CONTROLS) {
			// create the labels
			JLabel lblKey = new JLabel(control[0]); // key
			JLabel lblDes = new JLabel(control[1]); // description
			
			// add them to the panel
			pnlControls.add(lblKey, new GridBagConstraints(
					0, row, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
					GridBagConstraints.NONE, insets, 0, 0));
			pnlControls.add(lblDes, new GridBagConstraints(
					1, row, 1, 1, 1, (row + 1 == size ? 1 : 0), GridBagConstraints.FIRST_LINE_START, 
					GridBagConstraints.NONE, insets, 0, 0));
			row++;
		}
		
		panel.add(pnlControls);
		
		//////////////////////////////////////////////////
		// test controls group
		//////////////////////////////////////////////////
		// create the panel
		this.pnlTestControls = new JPanel();
		this.pnlTestControls.setBorder(new TitledBorder("Test Specific Controls"));
		this.pnlTestControls.setLayout(new GridBagLayout());
		
		// check for controls
		if (this.test.getControls().length > 0) {
			// add the controls to it
			this.addTestControls(this.pnlTestControls, this.test.getControls());
		}
		// add it to the panel
		panel.add(this.pnlTestControls);

		// return the panel
		return panel;
	}
	
	/**
	 * Creates the panel where the user selects the current test.
	 * @return JPanel where the user selects the current test
	 */
	private JPanel createSelectTestPanel() {
		// create a container for the tests selection tab
		JPanel panel = new JPanel();
		// create border
		Border border = new EmptyBorder(5, 5, 5, 5);
		// set the layout to null so we can absolutely position the labels
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		// set the panel border and background color
		panel.setBorder(border);

		// create some insets for all the panels
		Insets insets = new Insets(2, 2, 2, 2);
		
		//////////////////////////////////////////////////
		// test group
		//////////////////////////////////////////////////
		
		// create the panel
		JPanel pnlTest = new JPanel();
		pnlTest.setBorder(new TitledBorder("Test Selection"));
		pnlTest.setLayout(new GridBagLayout());

		JLabel lblTest = new JLabel("Tests", this.helpIcon, JLabel.LEFT);
		lblTest.setToolTipText("After selecting a test and clicking Run, check the controls tab for any test specific controls.");
		pnlTest.add(lblTest, new GridBagConstraints(
				0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// get the test list
		Collection<String> keySet = tests.keySet();
		List<String> keys = new ArrayList<String>();
		for (String key : keySet) {
			keys.add(key);
		}
		Collections.sort(keys);
		// create a combo box for the test selection
		cmbTests = new JComboBox(keys.toArray());
		// set the selected item
		cmbTests.setSelectedItem(test.getName());
		// add it to the panel
		pnlTest.add(cmbTests, new GridBagConstraints(
				1, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create a button to save the setting
		JButton btnT = new JButton("Run");
		// add a listener to it to save the setting
		btnT.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// set the selected test
				setTest((String) cmbTests.getSelectedItem());
				// remove all the controls
				pnlTestControls.removeAll();
				// update the test specific controls panel
				if (test.getControls().length > 0) {
					// add all the new ones
					addTestControls(pnlTestControls, test.getControls());
				}
			}
		});
		// add the button to the panel
		pnlTest.add(btnT, new GridBagConstraints(
				2, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		JLabel lblDesc = new JLabel("Test description:");
		pnlTest.add(lblDesc, new GridBagConstraints(
				0, 1, 3, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create a description field
		panTestDescription = new JTextPane();
		panTestDescription.setBorder(new CompoundBorder(new LineBorder(Color.black), border));
		panTestDescription.setEditable(false);
		panTestDescription.setText(test.getDescription());
		// once the label is created set the action listener for the combo box
		cmbTests.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String tName = ((String) ((JComboBox) e.getSource()).getSelectedItem());
				// find the respective test
				Test t = tests.get(tName);
				// set the description
				panTestDescription.setText(t.getDescription());
			}
		});
		// add the label to the panel
		pnlTest.add(panTestDescription, new GridBagConstraints(
				0, 2, 3, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.BOTH, insets, 0, 0));
		
		panel.add(pnlTest);
		
		return panel;
	}
	
	/**
	 * Creates the panel for the drawing options tab.
	 * @return JPanel the panel for the drawing options tab
	 */
	private JPanel createDrawingOptionsPanel() {
		// create a container for the drawing tab
		JPanel panel = new JPanel();
		// create border
		Border border = new EmptyBorder(5, 5, 5, 5);
		// set the layout to null so we can absolutely position the labels
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		// set the panel border and background color
		panel.setBorder(border);

		// create some insets for all the panels
		Insets insets = new Insets(2, 2, 2, 2);
		
		// get the drawing settings instance
		Draw draw = Draw.getInstance();
		
		//////////////////////////////////////////////////
		// drawing options group
		//////////////////////////////////////////////////
		
		// create the panel
		JPanel pnlDraw = new JPanel();
		pnlDraw.setBorder(new TitledBorder("Drawing Options"));
		pnlDraw.setLayout(new GridBagLayout());

		// draw centers of mass
		JLabel lblCenter = new JLabel("Center of Mass");
		pnlDraw.add(lblCenter, new GridBagConstraints(
				0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkCenter = new JCheckBox();
		chkCenter.setSelected(draw.drawCenter());
		chkCenter.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawCenter(!draw.drawCenter());
			}
		});
		pnlDraw.add(chkCenter, new GridBagConstraints(
				1, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// draw velocity vectors
		JLabel lblVelocity = new JLabel("Velocity Vector");
		pnlDraw.add(lblVelocity, new GridBagConstraints(
				0, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkVelocity = new JCheckBox();
		chkVelocity.setSelected(draw.drawVelocityVectors());
		chkVelocity.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawVelocityVectors(!draw.drawVelocityVectors());
			}
		});
		pnlDraw.add(chkVelocity, new GridBagConstraints(
				1, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// draw contact points
		JLabel lblContacts = new JLabel("Contact Points");
		pnlDraw.add(lblContacts, new GridBagConstraints(
				0, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkContacts = new JCheckBox();
		chkContacts.setSelected(draw.drawContacts());
		chkContacts.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawContacts(!draw.drawContacts());
			}
		});
		pnlDraw.add(chkContacts, new GridBagConstraints(
				1, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// draw contact forces
		JLabel lblContactForces = new JLabel("Contact Forces");
		pnlDraw.add(lblContactForces, new GridBagConstraints(
				0, 3, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkContactForces = new JCheckBox();
		chkContactForces.setSelected(draw.drawContactForces());
		chkContactForces.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawContactForces(!draw.drawContactForces());
			}
		});
		pnlDraw.add(chkContactForces, new GridBagConstraints(
				1, 3, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// draw contact pairs
		JLabel lblContactPairs = new JLabel("Contact Pairs");
		pnlDraw.add(lblContactPairs, new GridBagConstraints(
				0, 4, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkContactPairs = new JCheckBox();
		chkContactPairs.setSelected(draw.drawContactPairs());
		chkContactPairs.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawContactPairs(!draw.drawContactPairs());
			}
		});
		pnlDraw.add(chkContactPairs, new GridBagConstraints(
				1, 4, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// draw friction forces
		JLabel lblFrictionForces = new JLabel("Friction Forces");
		pnlDraw.add(lblFrictionForces, new GridBagConstraints(
				0, 5, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkFrictionForces = new JCheckBox();
		chkFrictionForces.setSelected(draw.drawFrictionForces());
		chkFrictionForces.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawFrictionForces(!draw.drawFrictionForces());
			}
		});
		pnlDraw.add(chkFrictionForces, new GridBagConstraints(
				1, 5, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// draw joints
		JLabel lblJoints = new JLabel("Joints");
		pnlDraw.add(lblJoints, new GridBagConstraints(
				0, 6, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkJoints = new JCheckBox();
		chkJoints.setSelected(draw.drawJoints());
		chkJoints.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawJoints(!draw.drawJoints());
			}
		});
		pnlDraw.add(chkJoints, new GridBagConstraints(
				1, 6, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// draw world bounds
		JLabel lblBounds = new JLabel("World Bounds");
		pnlDraw.add(lblBounds, new GridBagConstraints(
				0, 7, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkBounds = new JCheckBox();
		chkBounds.setSelected(draw.drawBounds());
		chkBounds.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawBounds(!draw.drawBounds());
			}
		});
		pnlDraw.add(chkBounds, new GridBagConstraints(
				1, 7, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// draw text
		JLabel lblText = new JLabel("Text");
		pnlDraw.add(lblText, new GridBagConstraints(
				0, 8, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkText = new JCheckBox();
		chkText.setSelected(draw.drawText());
		chkText.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawText(!draw.drawText());
			}
		});
		pnlDraw.add(chkText, new GridBagConstraints(
				1, 8, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));

		// fill shapes?
		JLabel lblFill = new JLabel("Shape Fill");
		pnlDraw.add(lblFill, new GridBagConstraints(
				0, 9, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkFill = new JCheckBox();
		chkFill.setSelected(draw.drawFill());
		chkFill.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawFill(!draw.drawFill());
			}
		});
		pnlDraw.add(chkFill, new GridBagConstraints(
				1, 9, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// draw outlines?
		JLabel lblOutline = new JLabel("Shape Outlines");
		pnlDraw.add(lblOutline, new GridBagConstraints(
				0, 10, 1, 1, 0, 1, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		JCheckBox chkOutline = new JCheckBox();
		chkOutline.setSelected(draw.drawOutline());
		chkOutline.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// toggle the checkbox
				Draw draw = Draw.getInstance();
				draw.setDrawOutline(!draw.drawOutline());
			}
		});
		pnlDraw.add(chkOutline, new GridBagConstraints(
				1, 10, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		panel.add(pnlDraw);
		
		return panel;
	}
	
	/**
	 * Creates the panel for the simulation settings tab.
	 * @return JPanel the simulation settings panel
	 */
	private JPanel createSimulationSettingsPanel() {
		// get the current settings
		Settings settings = Settings.getInstance();
		
		// create a container for the settings tab
		JPanel panel = new JPanel();
		// create a border
		Border border = new EmptyBorder(5, 5, 5, 5);
		
		// set the layout
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBorder(border);
		
		// create some insets for all the panels
		Insets insets = new Insets(2, 2, 2, 2);
		
		//////////////////////////////////////////////////
		// general group
		//////////////////////////////////////////////////
		
		// create the panel
		JPanel pnlGeneral = new JPanel();
		pnlGeneral.setBorder(new TitledBorder("General Settings"));
		// set the layout
		pnlGeneral.setLayout(new GridBagLayout());
		
		// broad-phase
		JLabel lblBPCDAlgo = new JLabel("Broad-phase Collision Detection Algorithm", this.helpIcon, JLabel.LEFT);
		lblBPCDAlgo.setToolTipText("Specifies the algorithm used to handle broad-phase collision detection.");
		pnlGeneral.add(lblBPCDAlgo, new GridBagConstraints(
				0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// create the drop down for the collision detection algorithm
		cmbBPCDAlgo = new JComboBox(new String[] {"Sap"});
		cmbBPCDAlgo.setSelectedItem(this.selectedBPCDAlgo);
		// add it to the panel
		pnlGeneral.add(cmbBPCDAlgo, new GridBagConstraints(
				1, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the button to save the setting
		JButton btnBPCDAlgo = new JButton("Set");
		btnBPCDAlgo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// set the selected item
				selectedBPCDAlgo = (String) cmbBPCDAlgo.getSelectedItem();
			}
		});
		// add the button to the panel
		pnlGeneral.add(btnBPCDAlgo, new GridBagConstraints(
				2, 0, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// narrow-phase
		JLabel lblCDAlgo = new JLabel("Narrow-phase Collision Detection Algorithm", this.helpIcon, JLabel.LEFT);
		lblCDAlgo.setToolTipText("Specifies the algorithm used to handle narrow-phase collision detection.");
		pnlGeneral.add(lblCDAlgo, new GridBagConstraints(
				0, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// create the drop down for the collision detection algorithm
		cmbNPCDAlgo = new JComboBox(new String[] {"Gjk", "Sat"});
		cmbNPCDAlgo.setSelectedItem(this.selectedNPCDAlgo);
		// add it to the panel
		pnlGeneral.add(cmbNPCDAlgo, new GridBagConstraints(
				1, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the button to save the setting
		JButton btnCDAlgo = new JButton("Set");
		btnCDAlgo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// set the selected item
				selectedNPCDAlgo = (String) cmbNPCDAlgo.getSelectedItem();
			}
		});
		// add the button to the panel
		pnlGeneral.add(btnCDAlgo, new GridBagConstraints(
				2, 1, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// manifold
		JLabel lblMSAlgo = new JLabel("Manifold Solver", this.helpIcon, JLabel.LEFT);
		lblMSAlgo.setToolTipText("Specifies the algorithm used to create collision manifolds.");
		pnlGeneral.add(lblMSAlgo, new GridBagConstraints(
				0, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// create the drop down for the collision detection algorithm
		cmbMSAlgo = new JComboBox(new String[] {"Clip"});
		cmbMSAlgo.setSelectedItem(this.selectedMSAlgo);
		// add it to the panel
		pnlGeneral.add(cmbMSAlgo, new GridBagConstraints(
				1, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the button to save the setting
		JButton btnMSAlgo = new JButton("Set");
		btnMSAlgo.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// set the selected item
				selectedMSAlgo = (String) cmbMSAlgo.getSelectedItem();
			}
		});
		// add the button to the panel
		pnlGeneral.add(btnMSAlgo, new GridBagConstraints(
				2, 2, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		JLabel lblStep = new JLabel("Step Fequency", this.helpIcon, JLabel.LEFT);
		lblStep.setToolTipText("Specifies the number of updates the dynamics engine will attempt to perform per second.");
		pnlGeneral.add(lblStep, new GridBagConstraints(
				0, 3, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		JSpinner spnStep = new JSpinner(new SpinnerNumberModel(1.0 / settings.getStepFrequency(), 30.0, null, 5.0));
		spnStep.setEditor(new JSpinner.NumberEditor(spnStep, "##0"));
		spnStep.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double hz = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setStepFrequency(hz);
			}
		});
		// add the spinner to the layout
		pnlGeneral.add(spnStep, new GridBagConstraints(
				1, 3, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the unit label
		JLabel lblStepUnit = new JLabel("<html>second<sup>-1</sup></html>");
		pnlGeneral.add(lblStepUnit, new GridBagConstraints(
				2, 3, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// max velocity
		JLabel lblMaxV = new JLabel("Maximum Velocity", this.helpIcon, JLabel.LEFT);
		lblMaxV.setToolTipText("Specifies the maximum velocity a body can have.");
		pnlGeneral.add(lblMaxV, new GridBagConstraints(
				0, 4, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		JSpinner spnMaxV = new JSpinner(new SpinnerNumberModel(settings.getMaxVelocity(), 0.0, null, 1.0));
		spnMaxV.setEditor(new JSpinner.NumberEditor(spnMaxV, "    0"));
		spnMaxV.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double v = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setMaxVelocity(v);
			}
		});
		// add the spinner to the layout
		pnlGeneral.add(spnMaxV, new GridBagConstraints(
				1, 4, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the unit label
		JLabel lblMaxVUnit = new JLabel("meters / second");
		pnlGeneral.add(lblMaxVUnit, new GridBagConstraints(
				2, 4, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// max angular velocity
		JLabel lblMaxAv = new JLabel("Maximum Angular Velocity", this.helpIcon, JLabel.LEFT);
		lblMaxAv.setToolTipText("Specifies the maximum angular velocity a body can have.");
		pnlGeneral.add(lblMaxAv, new GridBagConstraints(
				0, 5, 1, 1, 0, 1, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		JSpinner spnMaxAv = new JSpinner(new SpinnerNumberModel(Math.toDegrees(settings.getMaxAngularVelocity()), 0.0, null, 1.0));
		spnMaxAv.setEditor(new JSpinner.NumberEditor(spnMaxAv, "    0"));
		spnMaxAv.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double v = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setMaxAngularVelocity(Math.toRadians(v));
			}
		});
		// add the spinner to the layout
		pnlGeneral.add(spnMaxAv, new GridBagConstraints(
				1, 5, 1, 1, 0, 1, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the unit label
		JLabel lblMaxAvUnit = new JLabel("degrees / second");
		pnlGeneral.add(lblMaxAvUnit, new GridBagConstraints(
				2, 5, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// add the panel to the overall panel
		panel.add(pnlGeneral);
		
		//////////////////////////////////////////////////
		// sleep group
		//////////////////////////////////////////////////
		
		// create the sleep panel
		JPanel pnlSleep = new JPanel();
		// create the sleep panel border
		pnlSleep.setBorder(new TitledBorder("Sleep Settings"));
		// set the layout
		pnlSleep.setLayout(new GridBagLayout());
		
		JLabel lblAllowSleep = new JLabel("Allow bodies to sleep?", this.helpIcon, JLabel.LEFT);
		lblAllowSleep.setToolTipText("Sleeping allows the physics system to save cycles by avoiding unnecessary work.");
		pnlSleep.add(lblAllowSleep, new GridBagConstraints(
				0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		JCheckBox chkAllowSleep = new JCheckBox();
		chkAllowSleep.setSelected(settings.canSleep());
		chkAllowSleep.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Settings settings = Settings.getInstance();
				settings.setSleep(!settings.canSleep());
			}
		});
		// add the checkbox to the panel
		pnlSleep.add(chkAllowSleep, new GridBagConstraints(
				1, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// sleep time
		JLabel lblSleepTime = new JLabel("Sleep time", this.helpIcon, JLabel.LEFT);
		lblSleepTime.setToolTipText("Specifies the required amount of time a body must be at rest before being put to sleep.");
		// add the label to the layout
		pnlSleep.add(lblSleepTime, new GridBagConstraints(
				0, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the spinner
		JSpinner spnSleepTime = new JSpinner(new SpinnerNumberModel(settings.getSleepTime(), 0.0, null, 0.1));
		spnSleepTime.setEditor(new JSpinner.NumberEditor(spnSleepTime, "0.0"));
		spnSleepTime.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double time = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setSleepTime(time);
			}
		});
		// add the spinner to the layout
		pnlSleep.add(spnSleepTime, new GridBagConstraints(
				1, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the unit label
		JLabel lblSleepTimeUnit = new JLabel("seconds");
		pnlSleep.add(lblSleepTimeUnit, new GridBagConstraints(
				2, 1, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// sleep max velocity
		JLabel lblSleepMaxV = new JLabel("Maximum velocity", this.helpIcon, JLabel.LEFT);
		lblSleepMaxV.setToolTipText("Specifies the maximum velocity used to determine whether a body is at rest or not.");
		// add the label to the layout
		pnlSleep.add(lblSleepMaxV, new GridBagConstraints(
				0, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the spinner
		JSpinner spnSleepMaxV = new JSpinner(new SpinnerNumberModel(settings.getSleepVelocity(), 0.0, null, 0.01));
		spnSleepMaxV.setEditor(new JSpinner.NumberEditor(spnSleepMaxV, "0.00"));
		spnSleepMaxV.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double v = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setSleepVelocity(v);
			}
		});
		// add the spinner to the layout
		pnlSleep.add(spnSleepMaxV, new GridBagConstraints(
				1, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the unit label
		JLabel lblSleepMaxVUnit = new JLabel("meters / second");
		pnlSleep.add(lblSleepMaxVUnit, new GridBagConstraints(
				2, 2, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// sleep max av
		JLabel lblSleepMaxAv = new JLabel("Maximum angular velocity", this.helpIcon, JLabel.LEFT);
		lblSleepMaxAv.setToolTipText("Specifies the maximum angular velocity used to determine whether a body is at rest or not.");
		// add the label to the layout
		pnlSleep.add(lblSleepMaxAv, new GridBagConstraints(
				0, 3, 1, 1, 0, 1, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the spinner
		JSpinner spnSleepMaxAv = new JSpinner(new SpinnerNumberModel(Math.toDegrees(settings.getSleepAngularVelocity()), 0.0, null, 0.5));
		spnSleepMaxAv.setEditor(new JSpinner.NumberEditor(spnSleepMaxAv, "0.0"));
		spnSleepMaxAv.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double v = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setSleepAngularVelocity(Math.toRadians(v));
			}
		});
		// add the spinner to the layout
		pnlSleep.add(spnSleepMaxAv, new GridBagConstraints(
				1, 3, 1, 1, 0, 1, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		// create the unit label
		JLabel lblSleepMaxAvUnit = new JLabel("degrees / second");
		pnlSleep.add(lblSleepMaxAvUnit, new GridBagConstraints(
				2, 3, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// add the sleep panel to the over all panel
		panel.add(pnlSleep);
		
		//////////////////////////////////////////////////
		// Constraint solver group
		//////////////////////////////////////////////////
		
		// create the constraint panel
		JPanel pnlConstraint = new JPanel();
		// create the sleep panel border
		pnlConstraint.setBorder(new TitledBorder("Constraint Solver Settings"));
		// set the layout
		pnlConstraint.setLayout(new GridBagLayout());
		
		// si solver iterations
		JLabel lblSiIter = new JLabel("Iterations", this.helpIcon, JLabel.LEFT);
		lblSiIter.setToolTipText("Specifies the accuracy of the contraint solver.  Increasing this value increases the accuracy but lowers performance.");
		pnlConstraint.add(lblSiIter, new GridBagConstraints(
				0, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// create the slider for the si solver iterations
		JSpinner spnSiIter = new JSpinner(new SpinnerNumberModel(settings.getSiSolverIterations(), 5, null, 1));
		spnSiIter.setEditor(new JSpinner.NumberEditor(spnSiIter, "   0"));
		spnSiIter.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				int iter = ((SpinnerNumberModel) spnr.getModel()).getNumber().intValue();
				Settings settings = Settings.getInstance();
				settings.setSiSolverIterations(iter);
			}
		});
		// add it to the panel
		pnlConstraint.add(spnSiIter, new GridBagConstraints(
				1, 0, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// baumgarte
		JLabel lblBaum = new JLabel("Baumgarte", this.helpIcon, JLabel.LEFT);
		lblBaum.setToolTipText("Specifies the rate at which the position constraints are solved.");
		pnlConstraint.add(lblBaum, new GridBagConstraints(
				0, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		JSpinner spnBaum = new JSpinner(new SpinnerNumberModel(settings.getBaumgarte(), 0.0, 1.0, 0.05));
		spnBaum.setEditor(new JSpinner.NumberEditor(spnBaum, "0.00"));
		spnBaum.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double baum = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setBaumgarte(baum);
			}
		});
		pnlConstraint.add(spnBaum, new GridBagConstraints(
				1, 1, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// warm start distance
		JLabel lblWarm = new JLabel("Warm start distance", this.helpIcon, JLabel.LEFT);
		lblWarm.setToolTipText("Specifies the distance between two iteration's contact points to determine whether to warm start or not.  " +
				"Set this value to to zero to turn off warm starting.  Warm starting provides better performance and accuracy.");
		pnlConstraint.add(lblWarm, new GridBagConstraints(
				0, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));

		JSpinner spnWarm = new JSpinner(new SpinnerListModel(new String[] {
				"0.0", "1.0E-9", "1.0E-8", "1.0E-7", "1.0E-6", "1.0E-5", "1.0E-4", "1.0E-3", "1.0E-2", "1.0E-1", "1.0"  
				}));
		DecimalFormat fmtWarm = new DecimalFormat("0.0E0");
		spnWarm.setValue(fmtWarm.format(settings.getWarmStartDistance()));
		spnWarm.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double warm = Double.valueOf((String) ((SpinnerListModel) spnr.getModel()).getValue());
				Settings settings = Settings.getInstance();
				settings.setWarmStartDistance(warm);
			}
		});
		pnlConstraint.add(spnWarm, new GridBagConstraints(
				1, 2, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		JLabel lblWarmUnit = new JLabel("<html>meters<sup>2</sup></html>");
		pnlConstraint.add(lblWarmUnit, new GridBagConstraints(
				2, 2, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// allowed penetration
		JLabel lblPene = new JLabel("Linear tolerance", this.helpIcon, JLabel.LEFT);
		lblPene.setToolTipText("Specifies the amount of penetration allowed between bodies. This setting is used to control jitter.");
		pnlConstraint.add(lblPene, new GridBagConstraints(
				0, 3, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		JSpinner spnPene = new JSpinner(new SpinnerNumberModel(settings.getLinearTolerance(), 0.0, 0.1, 0.005));
		spnPene.setEditor(new JSpinner.NumberEditor(spnPene, "0.000"));
		spnPene.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double pene = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setLinearTolerance(pene);
			}
		});
		pnlConstraint.add(spnPene, new GridBagConstraints(
				1, 3, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		JLabel lblPeneUnit = new JLabel("meters");
		pnlConstraint.add(lblPeneUnit, new GridBagConstraints(
				2, 3, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// linear correction
		JLabel lblLinear = new JLabel("Maximum linear correction", this.helpIcon, JLabel.LEFT);
		lblLinear.setToolTipText("Specifies the maximum amount of linear correction to perform in position solving.  This is used to avoid large position corrections.");
		pnlConstraint.add(lblLinear, new GridBagConstraints(
				0, 4, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		JSpinner spnLinear = new JSpinner(new SpinnerNumberModel(settings.getMaxLinearCorrection(), 0.0, 1.0, 0.05));
		spnLinear.setEditor(new JSpinner.NumberEditor(spnLinear, "0.00"));
		spnLinear.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double lin = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setMaxLinearCorrection(lin);
			}
		});
		pnlConstraint.add(spnLinear, new GridBagConstraints(
				1, 4, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		JLabel lblLinearUnit = new JLabel("meters");
		pnlConstraint.add(lblLinearUnit, new GridBagConstraints(
				2, 4, 1, 1, 1, 0, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// restitution velocity
		JLabel lblRest = new JLabel("Restitution velocity", this.helpIcon, JLabel.LEFT);
		lblRest.setToolTipText("Specifies at what relative velocity objects should bounce or attempt to come to rest.");
		pnlConstraint.add(lblRest, new GridBagConstraints(
				0, 5, 1, 1, 0, 1, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		JSpinner spnRest = new JSpinner(new SpinnerNumberModel(settings.getRestitutionVelocity(), 0.0, null, 0.1));
		spnRest.setEditor(new JSpinner.NumberEditor(spnRest, "0.0"));
		spnRest.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JSpinner spnr = (JSpinner) e.getSource();
				double r = ((SpinnerNumberModel) spnr.getModel()).getNumber().doubleValue();
				Settings settings = Settings.getInstance();
				settings.setRestitutionVelocity(r);
			}
		});
		pnlConstraint.add(spnRest, new GridBagConstraints(
				1, 5, 1, 1, 0, 1, GridBagConstraints.FIRST_LINE_END, 
				GridBagConstraints.NONE, insets, 0, 0));
		JLabel lblRestUnit = new JLabel("meters / second");
		pnlConstraint.add(lblRestUnit, new GridBagConstraints(
				2, 5, 1, 1, 1, 1, GridBagConstraints.FIRST_LINE_START, 
				GridBagConstraints.NONE, insets, 0, 0));
		
		// add the sleep panel to the over all panel
		panel.add(pnlConstraint);

		return panel;
	}
	
	/**
	 * Adds all the test specific controls to the given panel.
	 * @param panel the panel to add the test controls to
	 * @param controls the test specific controls
	 */
	private void addTestControls(JPanel panel, String[][] controls) {
		// create some insets for all the labels
		Insets insets = new Insets(2, 2, 2, 2);

		int size = controls.length;
		int row = 0;
		for (String[] control : controls) {
			// create the labels
			JLabel lblKey = new JLabel(control[0]); // key
			JLabel lblDes = new JLabel(control[1]); // description
			
			// add them to the panel
			panel.add(lblKey, new GridBagConstraints(
					0, row, 1, 1, 0, 0, GridBagConstraints.FIRST_LINE_START, 
					GridBagConstraints.NONE, insets, 0, 0));
			panel.add(lblDes, new GridBagConstraints(
					1, row, 1, 1, 1, (row + 1 == size ? 1 : 0), GridBagConstraints.FIRST_LINE_START, 
					GridBagConstraints.NONE, insets, 0, 0));
			row++;
		}
	}
	
	/**
	 * A main method for testing the settings frame stand alone.
	 * @param args command line arguments - none
	 */
	public static void main(String[] args) {
		ControlPanel sf;
		try {
			try {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			} catch (ClassNotFoundException e) {
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			} catch (UnsupportedLookAndFeelException e) {
			}
			sf = new ControlPanel();
			sf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			sf.setVisible(true);
		} catch (ConfigurationException e) {
			e.printStackTrace();
		}
	}
}
