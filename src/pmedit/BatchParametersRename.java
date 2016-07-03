package pmedit;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.border.LineBorder;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Frame;

import net.miginfocom.swing.MigLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JTextPane;
import java.awt.SystemColor;
import javax.swing.UIManager;
import javax.swing.JScrollPane;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Insets;

public class BatchParametersRename extends BatchParametersWindow {
	private JLabel previewLabel;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					BatchParametersRename frame = new BatchParametersRename(null);
					frame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
					frame.onCloseAction(new Runnable() {
						@Override
						public void run() {
							System.exit(0);
							
						}
					});
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	public BatchParametersRename(BatchOperationParameters parameters, final Frame owner) {
		super(parameters, owner);
						
	}
	
	/**
	 * @wbp.parser.constructor
	 */
	public BatchParametersRename(BatchOperationParameters params) {
		this(params, null);
	}

	@Override
	protected void createContentPane() {
		setTitle("Batch rename parameters");
		setMinimumSize(new Dimension(640, 480));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{640, 0};
		gridBagLayout.rowHeights = new int[]{300, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
				
				JPanel panel = new JPanel();
				panel.setBorder(new TitledBorder(new LineBorder(new Color(184, 207, 229)), "Rename template",
								TitledBorder.LEADING, TitledBorder.TOP, null, new Color(51, 51, 51)));
				GridBagConstraints gbc_panel = new GridBagConstraints();
				gbc_panel.insets = new Insets(5, 5, 5, 5);
				gbc_panel.fill = GridBagConstraints.BOTH;
				gbc_panel.gridx = 0;
				gbc_panel.gridy = 0;
				getContentPane().add(panel, gbc_panel);
				GridBagLayout gbl_panel = new GridBagLayout();
				gbl_panel.columnWidths = new int[]{598, 0};
				gbl_panel.rowHeights = new int[]{27, 16, 329, 0};
				gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
				gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, Double.MIN_VALUE};
				panel.setLayout(gbl_panel);
				
				final JComboBox comboBox = new JComboBox();
				comboBox.setModel(new DefaultComboBoxModel(new String[] { "", "{basic.author} - {basic.title}.pdf",
				"{basic.author} - {basic.creationDate}.pdf" }));
				comboBox.setEditable(true);
				GridBagConstraints gbc_comboBox = new GridBagConstraints();
				gbc_comboBox.anchor = GridBagConstraints.NORTH;
				gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
				gbc_comboBox.insets = new Insets(0, 0, 5, 0);
				gbc_comboBox.gridx = 0;
				gbc_comboBox.gridy = 0;
				panel.add(comboBox, gbc_comboBox);
				
				previewLabel = new JLabel("Preview:<dynamic>");
				GridBagConstraints gbc_previewLabel = new GridBagConstraints();
				gbc_previewLabel.fill = GridBagConstraints.HORIZONTAL;
				gbc_previewLabel.anchor = GridBagConstraints.NORTH;
				gbc_previewLabel.insets = new Insets(0, 0, 5, 0);
				gbc_previewLabel.gridx = 0;
				gbc_previewLabel.gridy = 1;
				panel.add(previewLabel, gbc_previewLabel);
				
				JScrollPane scrollPane = new JScrollPane();
				GridBagConstraints gbc_scrollPane = new GridBagConstraints();
				gbc_scrollPane.fill = GridBagConstraints.BOTH;
				gbc_scrollPane.gridx = 0;
				gbc_scrollPane.gridy = 2;
				panel.add(scrollPane, gbc_scrollPane);
				
				JTextPane txtpnSupportedFieldsbasictitle_1 = new JTextPane();
				txtpnSupportedFieldsbasictitle_1.setEditable(false);
				txtpnSupportedFieldsbasictitle_1.setBackground(UIManager.getColor("Panel.background"));
				txtpnSupportedFieldsbasictitle_1.setContentType("text/html");
				txtpnSupportedFieldsbasictitle_1.setText("Supported fields:<br>\n<pre>\n<i>{basic.title}</i>      <i>{basic.producer}</i> \n<i>{basic.author}</i>     <i>{basic.trapped}</i> \n<i>{basic.subject}</i>    <i>{basic.creationDate}</i> \n<i>{basic.keywords}</i>   <i>{basic.modificationDate}</i> \n<i>{basic.creator}</i> \n\n<i>{xmpBasic.creatorTool}</i>   <i>{xmpBasic.identifiers}</i> \n<i>{xmpBasic.baseURL}</i>       <i>{xmpBasic.advisories}</i> \n<i>{xmpBasic.label}</i>         <i>{xmpBasic.modifyDate}</i> \n<i>{xmpBasic.nickname}</i>      <i>{xmpBasic.createDate}</i> \n<i>{xmpBasic.rating}</i>        <i>{xmpBasic.metadataDate}</i> \n<i>{xmpBasic.title}</i> \n\n<i>{xmpPdf.keywords}</i> \n<i>{xmpPdf.pdfVersion}</i> \n<i>{xmpPdf.producer}</i> \n\n<i>{xmpDc.title}</i>         <i>{xmpDc.creators}</i> \n<i>{xmpDc.coverage}</i>      <i>{xmpDc.contributors}</i> \n<i>{xmpDc.description}</i>   <i>{xmpDc.languages}</i> \n<i>{xmpDc.dates}</i>         <i>{xmpDc.publishers}</i> \n<i>{xmpDc.format}</i>        <i>{xmpDc.relationships}</i> \n<i>{xmpDc.identifier}</i>    <i>{xmpDc.subjects}</i> \n<i>{xmpDc.rights}</i>        <i>{xmpDc.types}</i> \n<i>{xmpDc.source}</i> \n</pre>");
				scrollPane.setViewportView(txtpnSupportedFieldsbasictitle_1);
				txtpnSupportedFieldsbasictitle_1.setCaretPosition(0);
				
				JButton button = new JButton("Close");
				button.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						setVisible(false);
						windowClosed();
					}
				});
				GridBagConstraints gbc_button = new GridBagConstraints();
				gbc_button.anchor = GridBagConstraints.EAST;
				gbc_button.gridx = 0;
				gbc_button.gridy = 1;
				getContentPane().add(button, gbc_button);
				
				final JTextComponent tcA = (JTextComponent) comboBox.getEditor().getEditorComponent();
				tcA.getDocument().addDocumentListener(new DocumentListener() {
					@Override
					public void changedUpdate(DocumentEvent arg0) {
						showPreview((String) comboBox.getEditor().getItem());
					}

					@Override
					public void insertUpdate(DocumentEvent arg0) {
						showPreview((String) comboBox.getEditor().getItem());
					}

					@Override
					public void removeUpdate(DocumentEvent arg0) {
						showPreview((String) comboBox.getEditor().getItem());
					}
				});
				showPreview((String) comboBox.getEditor().getItem());

	}
	
	public void showPreview(String template) {
		parameters.renameTemplate = template;
		TemplateString ts = new TemplateString(template);
		getPreviewLabel().setText("Preview: " + ts.process(MetadataInfo.getSampleMetadata()));
	}

	protected JLabel getPreviewLabel() {
		return previewLabel;
	}
}
