package it.unibz.inf.ontop.protege.panels;

/*
 * #%L
 *
 * ontop-protege
 * %%
 * Copyright (C) 2009 - 2013 KRDB Research Centre. Free University of Bozen Bolzano.
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import com.google.common.collect.ImmutableList;
import it.unibz.inf.ontop.answering.reformulation.generation.dialect.SQLAdapterFactory;
import it.unibz.inf.ontop.answering.reformulation.generation.dialect.SQLDialectAdapter;
import it.unibz.inf.ontop.answering.reformulation.generation.dialect.impl.SQLServerSQLDialectAdapter;
import it.unibz.inf.ontop.dbschema.*;
import it.unibz.inf.ontop.exception.DuplicateMappingException;
import it.unibz.inf.ontop.injection.OntopStandaloneSQLSettings;
import it.unibz.inf.ontop.model.atom.TargetAtom;
import it.unibz.inf.ontop.model.atom.TargetAtomFactory;
import it.unibz.inf.ontop.model.term.*;
import it.unibz.inf.ontop.model.term.functionsymbol.FunctionSymbol;
import it.unibz.inf.ontop.model.term.functionsymbol.Predicate;
import it.unibz.inf.ontop.model.vocabulary.OWL;
import it.unibz.inf.ontop.spec.mapping.PrefixManager;
import it.unibz.inf.ontop.spec.mapping.pp.SQLPPTriplesMap;
import it.unibz.inf.ontop.spec.mapping.pp.impl.OntopNativeSQLPPTriplesMap;
import it.unibz.inf.ontop.protege.core.OBDADataSource;
import it.unibz.inf.ontop.spec.mapping.SQLMappingFactory;
import it.unibz.inf.ontop.protege.core.impl.RDBMSourceParameterConstants;
import it.unibz.inf.ontop.spec.mapping.impl.SQLMappingFactoryImpl;
import it.unibz.inf.ontop.protege.core.OBDAModel;
import it.unibz.inf.ontop.protege.core.OntopConfigurationManager;
import it.unibz.inf.ontop.protege.gui.IconLoader;
import it.unibz.inf.ontop.protege.gui.MapItem;
import it.unibz.inf.ontop.protege.gui.PredicateItem;
import it.unibz.inf.ontop.protege.gui.SQLResultSetTableModel;
import it.unibz.inf.ontop.protege.gui.component.AutoSuggestComboBox;
import it.unibz.inf.ontop.protege.gui.component.PropertyMappingPanel;
import it.unibz.inf.ontop.protege.gui.component.SQLResultTable;
import it.unibz.inf.ontop.protege.gui.treemodels.IncrementalResultSetTableModel;
import it.unibz.inf.ontop.protege.utils.*;
import it.unibz.inf.ontop.spec.mapping.serializer.TargetQueryRenderer;
import org.apache.commons.rdf.api.IRI;
import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import javax.swing.plaf.metal.MetalComboBoxButton;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static it.unibz.inf.ontop.protege.gui.PredicateItem.PredicateType.CLASS;

public class MappingAssistantPanel extends javax.swing.JPanel implements DatasourceSelectorListener {

	private static final long serialVersionUID = 1L;

	private final OBDAModel obdaModel;
	private final OntopConfigurationManager configurationManager;
	private final OWLModelManager owlModelManager;

	private final PrefixManager prefixManager;

	private OBDADataSource selectedSource;

	private MapItem predicateSubjectMap;

	private boolean isSubjectClassValid = true;

	private static final SQLMappingFactory MAPPING_FACTORY = SQLMappingFactoryImpl.getInstance();

	private static final String EMPTY_TEXT = "";

	private static final Color DEFAULT_TEXTFIELD_BACKGROUND = UIManager.getDefaults().getColor("TextField.background");
	private static final Color ERROR_TEXTFIELD_BACKGROUND = new Color(255, 143, 143);
    private final TermFactory termFactory;
	private final JdbcTypeMapper jdbcTypeMapper;
	private final TargetAtomFactory targetAtomFactory;

	public MappingAssistantPanel(OBDAModel model, OntopConfigurationManager configurationManager,
								 OWLModelManager owlModelManager) {
		obdaModel = model;
		this.configurationManager = configurationManager;
		this.owlModelManager = owlModelManager;
		prefixManager = obdaModel.getMutablePrefixManager();
        termFactory = obdaModel.getTermFactory();
        targetAtomFactory = obdaModel.getTargetAtomFactory();
        jdbcTypeMapper = obdaModel.getJDBCTypeMapper();
		initComponents();

		if (obdaModel.getSources().size() > 0) {

			datasourceChanged(selectedSource, obdaModel.getSources().get(0));
		}
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        splMainSplitter = new javax.swing.JSplitPane();
        pnlDataBrowser = new javax.swing.JPanel();
        splSqlQuery = new javax.swing.JSplitPane();
        pnlEditor = new javax.swing.JPanel();
        pnlDataSet = new javax.swing.JPanel();
        lblDataSet = new javax.swing.JLabel();
        cboDataSet = new javax.swing.JComboBox();
        cmdExecute = new javax.swing.JButton();
        pnlQueryEditor = new javax.swing.JPanel();
        scrQueryEditor = new javax.swing.JScrollPane();
        txtQueryEditor = new javax.swing.JEditorPane();
        pnlResultFilter = new javax.swing.JPanel();
        lblShow = new javax.swing.JLabel();
        txtRowCount = new javax.swing.JTextField();
        lblRows = new javax.swing.JLabel();
        pnlResult = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        scrQueryResult = new javax.swing.JScrollPane();
        pnlOntologyBrowser = new javax.swing.JPanel();
        pnlConcept = new javax.swing.JPanel();
        pnlFocusURI = new javax.swing.JPanel();
        lblFocusOnURI = new javax.swing.JLabel();
        txtClassUriTemplate = new javax.swing.JTextField();
        pnlClassMap = new javax.swing.JPanel();
        pnlClassSeachComboBox = new javax.swing.JPanel();
        lblClass = new javax.swing.JLabel();
        pnlProperties = new javax.swing.JPanel();
        pnlPropertiesLabel = new javax.swing.JPanel();
        lblProperties = new javax.swing.JLabel();
        pnlPropertyList = new javax.swing.JPanel();
        pnlCommandButtons = new javax.swing.JPanel();
        cmdClearAll = new javax.swing.JButton();
        cmdCreateMapping = new javax.swing.JButton();

        setAlignmentX(5.0F);
        setAlignmentY(5.0F);
        setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        setName("panel_master"); // NOI18N
        setPreferredSize(new java.awt.Dimension(640, 480));
        setLayout(new java.awt.BorderLayout());

        splMainSplitter.setDividerLocation(0.75);
        splMainSplitter.setResizeWeight(0.75);

        pnlDataBrowser.setName("panel_databrowser"); // NOI18N
        pnlDataBrowser.setLayout(new java.awt.BorderLayout());

        splSqlQuery.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        splSqlQuery.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        splSqlQuery.setResizeWeight(0.25);
        splSqlQuery.setMinimumSize(new java.awt.Dimension(550, 400));
        splSqlQuery.setPreferredSize(new java.awt.Dimension(550, 400));

        pnlEditor.setMinimumSize(new java.awt.Dimension(156, 100));
        pnlEditor.setPreferredSize(new java.awt.Dimension(156, 100));
        pnlEditor.setLayout(new java.awt.BorderLayout());

        pnlDataSet.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        lblDataSet.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblDataSet.setForeground(new java.awt.Color(53, 113, 163));
        lblDataSet.setText("Data Set:");
        pnlDataSet.add(lblDataSet);

        cboDataSet.setMinimumSize(new java.awt.Dimension(23, 23));
        cboDataSet.setPreferredSize(new java.awt.Dimension(240, 23));
        cboDataSet.setRenderer(new DataSetItemRenderer());
        cboDataSet.addActionListener(this::cboDataSetActionPerformed);
        pnlDataSet.add(cboDataSet);

        cmdExecute.setToolTipText("Execute query");
        cmdExecute.setIcon(IconLoader.getImageIcon("images/execute.png"));
        cmdExecute.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        cmdExecute.setContentAreaFilled(false);
        cmdExecute.setPreferredSize(new java.awt.Dimension(25, 25));
        cmdExecute.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdExecuteActionPerformed(evt);
            }
        });
        pnlDataSet.add(cmdExecute);

        pnlEditor.add(pnlDataSet, java.awt.BorderLayout.PAGE_START);

        pnlQueryEditor.setLayout(new java.awt.BorderLayout());

        txtQueryEditor.setBorder(null);
        txtQueryEditor.setFont(new java.awt.Font("Courier New", 0, 12)); // NOI18N
        txtQueryEditor.setMinimumSize(new java.awt.Dimension(100, 20));
        txtQueryEditor.setPreferredSize(new java.awt.Dimension(100, 20));
        txtQueryEditor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtQueryEditorKeyPressed(evt);
            }
        });
        scrQueryEditor.setViewportView(txtQueryEditor);

        pnlQueryEditor.add(scrQueryEditor, java.awt.BorderLayout.CENTER);

        pnlEditor.add(pnlQueryEditor, java.awt.BorderLayout.CENTER);

        pnlResultFilter.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        lblShow.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblShow.setText("Show");
        pnlResultFilter.add(lblShow);

        txtRowCount.setText("20");
        txtRowCount.setToolTipText("Insert the limit for data preview or 0 to present all data");
        txtRowCount.setMinimumSize(new java.awt.Dimension(25, 18));
        txtRowCount.setPreferredSize(new java.awt.Dimension(25, 18));
        txtRowCount.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRowCountFocusLost(evt);
            }
        });
        txtRowCount.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtRowCountKeyPressed(evt);
            }
        });
        pnlResultFilter.add(txtRowCount);

        lblRows.setText("rows");
        pnlResultFilter.add(lblRows);

        pnlEditor.add(pnlResultFilter, java.awt.BorderLayout.PAGE_END);

        splSqlQuery.setLeftComponent(pnlEditor);

        pnlResult.setLayout(new java.awt.BorderLayout());

        jLabel1.setFont(new java.awt.Font("Lucida Grande", Font.BOLD, 13)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(53, 113, 163));
        jLabel1.setText("SQL Query Result");
        jLabel1.setToolTipText("Double click the column headers to add them to the active templates on the right");
        jLabel1.setPreferredSize(new java.awt.Dimension(108, 30));
        pnlResult.add(jLabel1, java.awt.BorderLayout.PAGE_START);

        tblQueryResult = new SQLResultTable();
        tblQueryResult.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        scrQueryResult.setViewportView(tblQueryResult);
        scrQueryResult.setBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1));
        pnlResult.add(scrQueryResult, java.awt.BorderLayout.CENTER);

        splSqlQuery.setRightComponent(pnlResult);

        pnlDataBrowser.add(splSqlQuery, java.awt.BorderLayout.CENTER);

        splMainSplitter.setLeftComponent(pnlDataBrowser);

        pnlOntologyBrowser.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 3, 0, 3));
        pnlOntologyBrowser.setName("panel_ontologybrowser"); // NOI18N
        pnlOntologyBrowser.setLayout(new java.awt.BorderLayout(0, 2));

        pnlConcept.setLayout(new java.awt.BorderLayout(0, 2));

        pnlFocusURI.setLayout(new java.awt.BorderLayout());

        lblFocusOnURI.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblFocusOnURI.setForeground(new java.awt.Color(53, 113, 163));
        lblFocusOnURI.setText("Subject IRI template:");
        pnlFocusURI.add(lblFocusOnURI, java.awt.BorderLayout.NORTH);

        txtClassUriTemplate.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        txtClassUriTemplate.setText(defaultUriTemplate());
        txtClassUriTemplate.setMinimumSize(new java.awt.Dimension(240, 23));
        txtClassUriTemplate.setPreferredSize(new java.awt.Dimension(240, 23));
        txtClassUriTemplate.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                txtClassUriTemplateFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtClassUriTemplateFocusLost(evt);
            }
        });
        txtClassUriTemplate.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtClassUriTemplateKeyPressed(evt);
            }
        });
        pnlFocusURI.add(txtClassUriTemplate, java.awt.BorderLayout.SOUTH);

        pnlConcept.add(pnlFocusURI, java.awt.BorderLayout.NORTH);

        pnlClassMap.setBackground(new Color(240, 245, 240));
        pnlClassMap.setLayout(new java.awt.BorderLayout());

        pnlClassSeachComboBox.setLayout(new java.awt.BorderLayout());
		Vector<Object> v = new Vector<>();
		for (IRI c : obdaModel.getCurrentVocabulary().classes()) {
			v.addElement(new PredicateItem(c, CLASS, prefixManager));
        }
        cboClassAutoSuggest = new AutoSuggestComboBox(v);
		cboClassAutoSuggest.setRenderer(new ClassListCellRenderer());
        cboClassAutoSuggest.setMinimumSize(new java.awt.Dimension(195, 23));
        cboClassAutoSuggest.setPreferredSize(new java.awt.Dimension(195, 23));
        cboClassAutoSuggest.addItemListener(new java.awt.event.ItemListener () {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cboClassAutoSuggestItemStateChanged(evt);
            }
        });
        JTextField txtComboBoxEditor = (JTextField) cboClassAutoSuggest.getEditor().getEditorComponent();
        txtComboBoxEditor.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cboClassAutoSuggestKeyPressed(evt);
            }
        });
        pnlClassSeachComboBox.add(cboClassAutoSuggest, java.awt.BorderLayout.CENTER);
        pnlClassMap.add(pnlClassSeachComboBox, java.awt.BorderLayout.SOUTH);

        lblClass.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblClass.setForeground(new java.awt.Color(53, 113, 163));
        lblClass.setText("rdf:type (optional):");
        lblClass.setOpaque(true);
        pnlClassMap.add(lblClass, java.awt.BorderLayout.NORTH);

        pnlConcept.add(pnlClassMap, java.awt.BorderLayout.CENTER);

        pnlOntologyBrowser.add(pnlConcept, java.awt.BorderLayout.NORTH);

        pnlProperties.setLayout(new java.awt.BorderLayout(0, 3));

        pnlPropertiesLabel.setMinimumSize(new java.awt.Dimension(63, 30));
        pnlPropertiesLabel.setLayout(new java.awt.BorderLayout());

        lblProperties.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        lblProperties.setForeground(new java.awt.Color(53, 113, 163));
        lblProperties.setText("Add new property mapping:");
        lblProperties.setBorder(javax.swing.BorderFactory.createEmptyBorder(2, 2, 2, 2));
        lblProperties.setOpaque(true);
        lblProperties.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        pnlPropertiesLabel.add(lblProperties, java.awt.BorderLayout.CENTER);

        pnlProperties.add(pnlPropertiesLabel, java.awt.BorderLayout.NORTH);

        pnlPropertyList.setLayout(new java.awt.BorderLayout());

        pnlPropertyEditorList = new PropertyMappingPanel(obdaModel);
        pnlPropertyList.add(pnlPropertyEditorList);

        pnlProperties.add(pnlPropertyList, java.awt.BorderLayout.CENTER);

        pnlOntologyBrowser.add(pnlProperties, java.awt.BorderLayout.CENTER);

        pnlCommandButtons.setBorder(javax.swing.BorderFactory.createEmptyBorder(5, 0, 0, 0));
        pnlCommandButtons.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        cmdClearAll.setText("Clear All");
        cmdClearAll.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        cmdClearAll.setContentAreaFilled(false);
        cmdClearAll.setPreferredSize(new java.awt.Dimension(70, 25));
        cmdClearAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdClearAllActionPerformed(evt);
            }
        });
        pnlCommandButtons.add(cmdClearAll);

        cmdCreateMapping.setText("Create Mapping");
        cmdCreateMapping.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        cmdCreateMapping.setContentAreaFilled(false);
        cmdCreateMapping.setPreferredSize(new java.awt.Dimension(120, 25));
        cmdCreateMapping.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmdCreateMappingActionPerformed(evt);
            }
        });
        pnlCommandButtons.add(cmdCreateMapping);

        pnlOntologyBrowser.add(pnlCommandButtons, java.awt.BorderLayout.SOUTH);

        splMainSplitter.setRightComponent(pnlOntologyBrowser);

        add(splMainSplitter, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

	private void cboDataSetActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cboDataSetActionPerformed
		txtQueryEditor.setText(EMPTY_TEXT); // clear the text editor
		JComboBox cb = (JComboBox) evt.getSource();
		if (cb.getSelectedIndex() != -1) {
			RelationDefinition dd = (RelationDefinition) cb.getSelectedItem();
			if (dd != null) {
				String sql = generateSQLString(dd);
				txtQueryEditor.setText(sql);
			}
			executeQuery();
			txtClassUriTemplate.requestFocus();
		}
	}// GEN-LAST:event_cboDataSetActionPerformed

	private void txtQueryEditorKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txtQueryEditorKeyPressed
		// If users typed CTRL+E
		if ((evt.getKeyCode() == KeyEvent.VK_E) && ((evt.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
			executeQuery();
			txtClassUriTemplate.requestFocus();
		}
	}// GEN-LAST:event_txtQueryEditorKeyPressed

	private void txtRowCountFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtRowCountFocusLost
		if (selectedSource != null && !txtQueryEditor.getText().isEmpty()) {
			executeQuery();
			txtClassUriTemplate.requestFocus();
		}
	}// GEN-LAST:event_txtRowCountFocusLost

	private void txtRowCountKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txtRowCountKeyPressed
		int code = evt.getKeyCode();
		if (code == KeyEvent.VK_ENTER) {
			txtRowCount.transferFocus();
		}
	}// GEN-LAST:event_txtRowCountKeyPressed

	private void cmdExecuteActionPerformed(java.awt.event.ActionEvent evt) {
		if (selectedSource == null) {
			DialogUtils.showQuickErrorDialog(this, new Exception("Data source has not been defined."));
		}
		else {
			String sqlString = txtQueryEditor.getText();
			if (sqlString.isEmpty()) {

				JOptionPane.showMessageDialog(this, "SQL query cannot be blank", "Warning", JOptionPane.WARNING_MESSAGE);
			}
			else
			{
				executeQuery();
				txtClassUriTemplate.requestFocus();
			}
		}

	}

	private void txtClassUriTemplateFocusGained(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtClassUriTemplateFocusGained
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				String uriTemplate = txtClassUriTemplate.getText();
				int pos = 0;
				if (uriTemplate.contains(":")) {
					pos = uriTemplate.indexOf(":") + 1;
				} else {
					pos = uriTemplate.length();
				}
				txtClassUriTemplate.setCaretPosition(pos);
			}
		});
	}// GEN-LAST:event_txtClassUriTemplateFocusGained

	private void txtClassUriTemplateFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_txtClassUriTemplateFocusLost
		String uriTemplate = txtClassUriTemplate.getText();
		if (predicateSubjectMap == null) {
			predicateSubjectMap = createPredicateSubjectMap();
		}
		predicateSubjectMap.setTargetMapping(uriTemplate);
		validateClassUri();
	}// GEN-LAST:event_txtClassUriTemplateFocusLost

	private void txtClassUriTemplateKeyPressed(java.awt.event.KeyEvent evt) {// GEN-FIRST:event_txtClassUriKeyPressed
		int code = evt.getKeyCode();
		if (code == KeyEvent.VK_ESCAPE) {
			txtClassUriTemplate.setText(defaultUriTemplate());
		} else if (code == KeyEvent.VK_ENTER) {
			cboClassAutoSuggest.requestFocus();
		}
	}// GEN-LAST:event_txtClassUriKeyPressed

	private void cboClassAutoSuggestItemStateChanged(java.awt.event.ItemEvent evt) {
		Object item = evt.getItem(); // Get the affected item
		if (item instanceof PredicateItem) {

			if (evt.getStateChange() == ItemEvent.DESELECTED ){
				predicateSubjectMap = createPredicateSubjectMap();
				predicateSubjectMap.setTargetMapping(txtClassUriTemplate.getText());
				isSubjectClassValid = true;
			}
			else {
				PredicateItem selectedItem = (PredicateItem) item;
				predicateSubjectMap = new MapItem(selectedItem);
				predicateSubjectMap.setTargetMapping(txtClassUriTemplate.getText());
				isSubjectClassValid = true;
			}

		} else if (item instanceof String) {
			String className = item.toString();
			if (!className.isEmpty()) {
				isSubjectClassValid = false;
			}
			isSubjectClassValid = true;
		}
		validateSubjectClass();
	}

	private void cboClassAutoSuggestKeyPressed(KeyEvent evt) {
		int code = evt.getKeyCode();
		if (code == KeyEvent.VK_ESCAPE) {
			cboClassAutoSuggest.setSelectedIndex(-1);
		}
	}

	private void cmdClearAllActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmdClearAllActionPerformed
		clearForm();
	}// GEN-LAST:event_cmdClearAllActionPerformed

	private void cmdCreateMappingActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cmdCreateMappingActionPerformed
		// Stop any editing action and save whatever in the text field
		if (!pnlPropertyEditorList.isEmpty() && pnlPropertyEditorList.isEditing()) {
			pnlPropertyEditorList.stopCellEditing();
		}
		try {
			// Prepare the mapping source
			String source = txtQueryEditor.getText();

			if (source.isEmpty()) {
				JOptionPane.showMessageDialog(this, "ERROR: The SQL source cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			String subjectTargetString = predicateSubjectMap.getTargetMapping();
			if(subjectTargetString.equals(":")){
				JOptionPane.showMessageDialog(this, "ERROR: Class IRI template cannot be empty", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			// Prepare the mapping target
			List<MapItem> predicateObjectMapsList = pnlPropertyEditorList.getPredicateObjectMapsList();
			ImmutableList<TargetAtom> target = prepareTargetQuery(predicateSubjectMap, predicateObjectMapsList);

			if (target.isEmpty()) {
				JOptionPane.showMessageDialog(this, "ERROR: The target cannot be empty. Add a class or a property", "Error", JOptionPane.ERROR_MESSAGE);
				return;
			}
			// Create the mapping axiom
			SQLPPTriplesMap mappingAxiom = new OntopNativeSQLPPTriplesMap(MAPPING_FACTORY.getSQLQuery(source), target);
			obdaModel.addTriplesMap(mappingAxiom, false);

			final String targetString = TargetQueryRenderer.encode(target, prefixManager);
			JOptionPane.showMessageDialog(this,
                    String.format("Mapping created and added to the Mapping Manager: \n target: %s\n source: %s", targetString, source),
                    "Mapping created",
                    JOptionPane.INFORMATION_MESSAGE);

			// Clear the form afterwards
			clearForm();
		} catch (DuplicateMappingException e) {
			DialogUtils.showQuickErrorDialog(null, e, "Duplicate mapping identification.");
			return;
		} catch (NullPointerException e) {
			DialogUtils.showQuickErrorDialog(null, new Exception("Data source has not been defined."));
			return;
		} catch (RuntimeException e) {
			DialogUtils.showQuickErrorDialog(null, e, "Empty property mapping.");
			return;
		}
	}// GEN-LAST:event_cmdCreateMappingActionPerformed

	private ImmutableList<TargetAtom> prepareTargetQuery(MapItem predicateSubjectMap, List<MapItem> predicateObjectMapsList) {
		// Create the body of the CQ
		ImmutableList.Builder<TargetAtom> bodyBuilder = ImmutableList.builder();

		// Store concept in the body, if any
		ImmutableTerm subjectTerm = createSubjectTerm(predicateSubjectMap);
		if (!predicateSubjectMap.getPredicateIRI().equals(OWL.THING)) {
			TargetAtom concept = targetAtomFactory.getTripleTargetAtom(subjectTerm, predicateSubjectMap.getPredicateIRI());
			bodyBuilder.add(concept);
		}

		// Store attributes and roles in the body
		//List<Term> distinguishVariables = new ArrayList<Term>();
		for (MapItem predicateObjectMap : predicateObjectMapsList) {
			if (predicateObjectMap.isObjectMap()) { // if an attribute
				ImmutableTerm objectTerm = createObjectTerm(getColumnName(predicateObjectMap), predicateObjectMap.getDataType());
				TargetAtom attribute = targetAtomFactory.getTripleTargetAtom(subjectTerm, predicateObjectMap.getPredicateIRI(), objectTerm);
				bodyBuilder.add(attribute);
				//distinguishVariables.add(objectTerm);
			} else if (predicateObjectMap.isRefObjectMap()) { // if a role
				ImmutableFunctionalTerm objectRefTerm = createRefObjectTerm(predicateObjectMap);
				TargetAtom role = targetAtomFactory.getTripleTargetAtom(subjectTerm, predicateObjectMap.getPredicateIRI(), objectRefTerm);
				bodyBuilder.add(role);
			}
		}
		// Create the head
		//int arity = distinguishVariables.size();
		//Function head = termFactory.getFunction(termFactory.getTermType(OBDALibConstants.QUERY_HEAD, arity), distinguishVariables);

		// Create and return the conjunctive query
		return bodyBuilder.build();
	}

	private ImmutableFunctionalTerm createSubjectTerm(MapItem predicateSubjectMap) {
		String subjectTargetString = predicateSubjectMap.getTargetMapping();
		String subjectUriTemplate = "";
		if (writtenInFullUri(subjectTargetString)) {
			subjectUriTemplate = subjectTargetString.substring(1, subjectTargetString.length()-1);
		} else {
			subjectUriTemplate = prefixManager.getExpandForm(subjectTargetString, false);
		}
		return getUriFunctionTerm(subjectUriTemplate);
	}

	private ImmutableTerm createObjectTerm(String column, FunctionSymbol datatype) {
		ImmutableTerm object;
		if(column.matches("\"([\\w.]+)?\"")){
			object = termFactory.getConstantLiteral(column.substring(1, column.length()-1));
		}
		else {
			List<FormatString> columnStrings = parse(column);
			if (columnStrings.size() > 1) {
				throw new RuntimeException("Unsupported column mapping: " + column);
			}
			String columnName = columnStrings.get(0).toString();
			object = termFactory.getVariable(columnName);
		}
		if (datatype == null) {
			return object;
		} else {
			return termFactory.getImmutableFunctionalTerm(datatype, object);
		}
	}

	private ImmutableFunctionalTerm createRefObjectTerm(MapItem predicateObjectMap) {
		String predicateTargetString = predicateObjectMap.getTargetMapping();
		String objectUriTemplate = "";
		if (writtenInFullUri(predicateTargetString)) {
			objectUriTemplate = predicateTargetString.substring(1, predicateTargetString.length()-1);
		} else {
			objectUriTemplate = prefixManager.getExpandForm(predicateTargetString, false);
		}
		return  getUriFunctionTerm(objectUriTemplate);
	}

	private boolean writtenInFullUri(String input) {
		return input.startsWith("<") && input.endsWith(">");
	}

	private String getColumnName(MapItem predicateObjectMap) {
		String columnName = predicateObjectMap.getTargetMapping();
		if (columnName.isEmpty()) {
			throw new RuntimeException("Property mapping should not be empty: " + predicateObjectMap.getName());
		}
		return columnName;
	}

	private String getDefaultNamespace(boolean usePrefix) {
		String defaultNamespace = prefixManager.getDefaultPrefix();
		if (usePrefix) {
			defaultNamespace = prefixManager.getShortForm(defaultNamespace, false);
		}
		return defaultNamespace;
	}

	private String defaultUriTemplate() {
		return String.format("%s", getDefaultNamespace(true));
	}

	private MapItem createPredicateSubjectMap() {
		// Create a default subject map using owl:Thing as the subject
		return new MapItem(new PredicateItem(OWL.THING, CLASS, prefixManager));
	}

	private ImmutableFunctionalTerm getUriFunctionTerm(String text) {
		final String PLACEHOLDER = "{}";
		List<ImmutableTerm> terms = new LinkedList<>();
		List<FormatString> tokens = parse(text);
		StringBuilder sb = new StringBuilder();
		for (FormatString token : tokens) {
			if (token instanceof FixedString) { // if part of URI template
				sb.append(token.toString());
			} else if (token instanceof ColumnString) {
				sb.append(PLACEHOLDER);
				Variable column = termFactory.getVariable(token.toString());
				terms.add(column);
			}
		}
		ValueConstant uriTemplate = termFactory.getConstantLiteral(sb.toString()); // complete URI template
		terms.add(0, uriTemplate);
		return termFactory.getImmutableUriTemplate(ImmutableList.copyOf(terms));
	}

	// Column placeholder pattern
	private static final String formatSpecifier = "\\{([\\w.]+)?\\}";
	private static final Pattern chPattern = Pattern.compile(formatSpecifier);

	private List<FormatString> parse(String text) {
		List<FormatString> toReturn = new ArrayList<FormatString>();
		Matcher m = chPattern.matcher(text);
		int i = 0;
		while (i < text.length()) {
			if (m.find(i)) {
				if (m.start() != i) {
					toReturn.add(new FixedString(text.substring(i, m.start())));
				}
				String value = m.group(1);
				toReturn.add(new ColumnString(value));
				i = m.end();
			} else {
				toReturn.add(new FixedString(text.substring(i)));
				break;
			}
		}
		return toReturn;
	}


	private interface FormatString {
		int index();
		String toString();
	}

	private class FixedString implements FormatString {
		private final String s;
		FixedString(String s) { this.s = s; }
		@Override public int index() { return -1; } // flag code for fixed string
		@Override public String toString() { return s; }
	}

	private class ColumnString implements FormatString {
		private final String s;
		ColumnString(String s) { this.s = s; }
		@Override public int index() { return 0; } // flag code for column string
		@Override public String toString() { return s;}
	}

	private void clearForm() {
		cboDataSet.setSelectedIndex(-1);
		txtQueryEditor.setText(EMPTY_TEXT);
		tblQueryResult = new SQLResultTable();
		tblQueryResult.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrQueryResult.setViewportView(tblQueryResult);
		cboClassAutoSuggest.setSelectedIndex(-1);
		txtClassUriTemplate.setText(defaultUriTemplate());
		pnlPropertyEditorList.clear();
		predicateSubjectMap = null;
	}

	private String generateSQLString(RelationDefinition table) {
		StringBuilder sb = new StringBuilder("select");
		boolean needComma = false;
		for (Attribute attr : table.getAttributes()) {
			if (needComma) {
				sb.append(",");
			}
			sb.append(" ");
			sb.append(attr.getID());
			needComma = true;
		}
		sb.append(" ");
		sb.append("from");
		sb.append(" ");
		sb.append(table.getID());
		return sb.toString();
	}

	private void executeQuery() {
		try {
			releaseResultset();
			OBDAProgressMonitor progMonitor = new OBDAProgressMonitor("Executing query...", this);
			CountDownLatch latch = new CountDownLatch(1);
			ExecuteSQLQueryAction action = new ExecuteSQLQueryAction(latch);
			progMonitor.addProgressListener(action);
			progMonitor.start();
			action.run();
			latch.await();
			progMonitor.stop();
			ResultSet rs = action.getResult();
			if (rs != null) {
				SQLResultSetTableModel model = new SQLResultSetTableModel(rs, fetchSize());
				tblQueryResult.setModel(model);
			}
		} catch (Exception e) {
			DialogUtils.showQuickErrorDialog(this, e);
		}
	}

	private int fetchSize() {
		return Integer.parseInt(txtRowCount.getText());
	}

	private void releaseResultset() {
		TableModel model = tblQueryResult.getModel();
		if (model == null) {
			return;
		}
		if (!(model instanceof IncrementalResultSetTableModel)) {
			return;
		}
		IncrementalResultSetTableModel imodel = (IncrementalResultSetTableModel) model;
		imodel.close();
	}

	@Override
	public void datasourceChanged(OBDADataSource oldSource, OBDADataSource newSource) {

		selectedSource = newSource;
		addDatabaseTableToDataSetComboBox();
		releaseResultset();
		clearForm();
	}

	private void addDatabaseTableToDataSetComboBox() {
		DefaultComboBoxModel<RelationDefinition> relationList = new DefaultComboBoxModel<>();
		try {
			Connection conn = ConnectionTools.getConnection(selectedSource);
			RDBMetadata md = RDBMetadataExtractionTools.createMetadata(conn, obdaModel.getTypeFactory(), jdbcTypeMapper);
			// this operation is EXPENSIVE -- only names are needed + a flag for table/view
			RDBMetadataExtractionTools.loadMetadata(md, conn, null);
			for (DatabaseRelationDefinition relation : md.getDatabaseRelations()) {
				relationList.addElement(relation);
			}
		}
		catch (SQLException e) {
			// NO-OP
		}
		cboDataSet.setModel(relationList);
		cboDataSet.setSelectedIndex(-1);
	}

	//
	// Methods for validation
	//

	private void validateClassUri() {
		if (predicateSubjectMap.isValid()) {
			setNormalBackground(txtClassUriTemplate);
		} else {
			setErrorBackground(txtClassUriTemplate);
		}
	}

	private void validateSubjectClass() {
		if (isSubjectClassValid) {
			setNormalBackground(cboClassAutoSuggest);
		} else {
			setErrorBackground(cboClassAutoSuggest);
		}
	}

	//
	// Methods for GUI changes
	//

	private void setNormalBackground(JComboBox comboBox) {
		JTextField text = ((JTextField) comboBox.getEditor().getEditorComponent());
		text.setBackground(DEFAULT_TEXTFIELD_BACKGROUND);
	}

	private void setErrorBackground(JComboBox comboBox) {
		JTextField text = ((JTextField) comboBox.getEditor().getEditorComponent());
		text.setBackground(ERROR_TEXTFIELD_BACKGROUND);
		Component[] comp = comboBox.getComponents();
		for (int i = 0; i < comp.length; i++) {// hack valid only for Metal L&F
			if (comp[i] instanceof MetalComboBoxButton) {
				MetalComboBoxButton coloredArrowsButton = (MetalComboBoxButton) comp[i];
				coloredArrowsButton.setBackground(null);
				break;
			}
		}
		comboBox.requestFocus();
	}

	private void setNormalBackground(JTextField textField) {
		textField.setBackground(DEFAULT_TEXTFIELD_BACKGROUND);
	}

	private void setErrorBackground(JTextField textField) {
		textField.setBackground(ERROR_TEXTFIELD_BACKGROUND);
		textField.requestFocus();
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox cboDataSet;
    private javax.swing.JButton cmdClearAll;
    private javax.swing.JButton cmdCreateMapping;
    private javax.swing.JButton cmdExecute;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel lblClass;
    private javax.swing.JLabel lblDataSet;
    private javax.swing.JLabel lblFocusOnURI;
    private javax.swing.JLabel lblProperties;
    private javax.swing.JLabel lblRows;
    private javax.swing.JLabel lblShow;
    private javax.swing.JPanel pnlClassMap;
    private javax.swing.JPanel pnlClassSeachComboBox;
    private AutoSuggestComboBox cboClassAutoSuggest;
    private javax.swing.JPanel pnlCommandButtons;
    private javax.swing.JPanel pnlConcept;
    private javax.swing.JPanel pnlDataBrowser;
    private javax.swing.JPanel pnlDataSet;
    private javax.swing.JPanel pnlEditor;
    private javax.swing.JPanel pnlFocusURI;
    private javax.swing.JPanel pnlOntologyBrowser;
    private javax.swing.JPanel pnlProperties;
    private javax.swing.JPanel pnlPropertiesLabel;
    private javax.swing.JPanel pnlPropertyList;
    private PropertyMappingPanel pnlPropertyEditorList;
    private javax.swing.JPanel pnlQueryEditor;
    private javax.swing.JPanel pnlResult;
    private javax.swing.JPanel pnlResultFilter;
    private javax.swing.JScrollPane scrQueryEditor;
    private javax.swing.JScrollPane scrQueryResult;
    private SQLResultTable tblQueryResult;
    private javax.swing.JSplitPane splMainSplitter;
    private javax.swing.JSplitPane splSqlQuery;
    private javax.swing.JTextField txtClassUriTemplate;
    private javax.swing.JEditorPane txtQueryEditor;
    private javax.swing.JTextField txtRowCount;
    // End of variables declaration//GEN-END:variables

	/**
	 * Renderer class to present the table list.
	 */
	private class DataSetItemRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			final int tableSize = list.getModel().getSize();
			if (tableSize == 0) {
				label.setText("<No table found>");
				return label;
			} else {
				if (index == -1) {
					label.setText("<Select database table>");
					return label;
				} else {
					if (value instanceof DatabaseRelationDefinition) {
						DatabaseRelationDefinition td = (DatabaseRelationDefinition) value;
						ImageIcon icon = IconLoader.getImageIcon("images/db_table.png");
						label.setIcon(icon);
						label.setText(td.getID().getSQLRendering());
					} else if (value instanceof ParserViewDefinition) {
						// ROMAN (7 Oct 2015): I'm not sure we need "views" -- they are
						// created by SQLQueryParser for complex queries that cannot be parsed
						ParserViewDefinition vd = (ParserViewDefinition) value;
						ImageIcon icon = IconLoader.getImageIcon("images/db_view.png");
						label.setIcon(icon);
						label.setText(vd.getID().getSQLRendering());
					}
					return label;
				}
			}
		}
	}

	/**
	 * Renderer class to present the table list.
	 */
	private class ClassListCellRenderer extends DefaultListCellRenderer {
		private static final long serialVersionUID = 1L;
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (value instanceof PredicateItem) {
				PredicateItem predicate = (PredicateItem) value;
				ImageIcon icon = IconLoader.getImageIcon("images/class_primitive.png");
				label.setIcon(icon);
				label.setText(predicate.getQualifiedName());
			}
			return label;
		}
	}

	/**
	 * Utility class to listen to the plugin progress bar
	 */
	private class ExecuteSQLQueryAction implements OBDAProgressListener {
		CountDownLatch latch = null;
		Thread thread = null;
		ResultSet result = null;
		Statement statement = null;
		private boolean isCancelled = false;
		private boolean errorShown = false;

		private ExecuteSQLQueryAction(CountDownLatch latch) {
			this.latch = latch;
		}

		@Override
		public void actionCanceled() throws SQLException {
			this.isCancelled = true;
			if (thread != null) {
				thread.interrupt();
			}
			if (statement != null && !statement.isClosed()) {
				statement.close();
			}
			result = null;
			latch.countDown();
		}

		public ResultSet getResult() {
			return result;
		}

		public void run() {

			thread = new Thread() {
				@Override
				public void run() {
					// Execute the sql query
					try {
						// Construct the sql query
						final String dbType = selectedSource.getParameter(RDBMSourceParameterConstants.DATABASE_DRIVER);

						OWLOntology activeOntology = owlModelManager.getActiveOntology();
						OntopStandaloneSQLSettings settings = configurationManager.buildOntopSQLOWLAPIConfiguration(activeOntology)
								.getSettings();

						SQLDialectAdapter sqlDialect = SQLAdapterFactory.getSQLDialectAdapter(dbType, "",
								settings);
						String sqlString = txtQueryEditor.getText();

						int rowCount = fetchSize();
						if (rowCount >= 0) { // add the limit filter
							if (sqlDialect instanceof SQLServerSQLDialectAdapter) {
								SQLServerSQLDialectAdapter sqlServerDialect = (SQLServerSQLDialectAdapter) sqlDialect;
								sqlString = sqlServerDialect.sqlLimit(sqlString, rowCount);
							} else {
								sqlString = String.format("%s %s", sqlString, sqlDialect.sqlSlice(rowCount, 0));
							}
						} else {
							throw new RuntimeException("Invalid limit number.");
						}
						Connection c = ConnectionTools.getConnection(selectedSource);
						Statement s = c.createStatement();
						result = s.executeQuery(sqlString);
						latch.countDown();
					} catch (Exception e) {
						latch.countDown();
						errorShown = true;
						DialogUtils.showQuickErrorDialog(null, e);
					}
				}
			};

			thread.start();


		}

		@Override
		public boolean isCancelled() {
			return this.isCancelled;
		}

		@Override
		public boolean isErrorShown() {
			return this.errorShown;
		}
	}
}
