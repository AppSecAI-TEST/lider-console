package tr.org.liderahenk.liderconsole.core.dialogs;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.model.Agent;
import tr.org.liderahenk.liderconsole.core.model.AgentProperty;
import tr.org.liderahenk.liderconsole.core.model.UserSession;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;

/**
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class AgentDetailDialog extends DefaultLiderDialog {

	private static final Logger logger = LoggerFactory.getLogger(AgentDetailDialog.class);

	private Agent selectedAgent;

	public AgentDetailDialog(Shell parentShell, Agent selectedAgent) {
		super(parentShell);
		this.selectedAgent = selectedAgent;
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		parent.setLayout(new GridLayout(1, false));

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		CTabFolder tabFolder = createTabFolder(composite);

		// Agent properties tab
		Composite propContainer = createInputTab(tabFolder, Messages.getString("AGENT_PROPERTIES"));
		createPropTable(propContainer);

		// User sessions tab
		Composite sessionContainer = createInputTab(tabFolder, Messages.getString("USER_SESSIONS"));
		createSessionTable(sessionContainer);

		logger.debug("Created dialog area");

		applyDialogFont(composite);
		return composite;
	}

	/**
	 * Create properties table
	 * 
	 * @param tabContainer
	 */
	private void createPropTable(Composite tabContainer) {
		Composite composite = new Composite(tabContainer, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
		TableViewer tableViewer = SWTResourceManager.createTableViewer(composite);
		createPropTableColumns(tableViewer);
		populatePropTable(tableViewer);
	}

	/**
	 * Create properties table columns
	 * 
	 * @param tableViewer
	 */
	private void createPropTableColumns(TableViewer tableViewer) {
		// Property name
		TableViewerColumn propNameColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("PROPERTY_NAME"), 200);
		propNameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof AgentProperty) {
					return ((AgentProperty) element).getPropertyName();
				}
				return Messages.getString("UNTITLED");
			}
		});

		// Property value
		TableViewerColumn propValColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("PROPERTY_VALUE"), 400);
		propValColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof AgentProperty) {
					return ((AgentProperty) element).getPropertyValue();
				}
				return Messages.getString("UNTITLED");
			}
		});
	}

	/**
	 * Populate agent properties table
	 * 
	 * @param tableViewer
	 */
	private void populatePropTable(TableViewer tableViewer) {
		if (selectedAgent != null && selectedAgent.getProperties() != null) {
			tableViewer.setInput(selectedAgent.getProperties());
			tableViewer.refresh();
		}
	}

	/**
	 * Create user sessions table
	 * 
	 * @param tabContainer
	 */
	private void createSessionTable(Composite tabContainer) {
		Composite composite = new Composite(tabContainer, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));
		composite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, true));
		TableViewer tableViewer = SWTResourceManager.createTableViewer(composite);
		createSessionTableColumns(tableViewer);
		populateSessionTable(tableViewer);
	}

	/**
	 * Create sessions table columns
	 * 
	 * @param tableViewer
	 */
	private void createSessionTableColumns(TableViewer tableViewer) {
		// Username
		TableViewerColumn usernameColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("USERNAME"), 150);
		usernameColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof UserSession) {
					return ((UserSession) element).getUsername();
				}
				return Messages.getString("UNTITLED");
			}
		});
		// Session Event
		TableViewerColumn eventColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("SESSION_EVENT"), 150);
		eventColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof UserSession) {
					return Messages.getString(((UserSession) element).getSessionEvent().toString());
				}
				return Messages.getString("UNTITLED");
			}
		});
		// Create date
		TableViewerColumn createDateColumn = SWTResourceManager.createTableViewerColumn(tableViewer,
				Messages.getString("CREATE_DATE"), 150);
		createDateColumn.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof UserSession) {
					return ((UserSession) element).getCreateDate() != null
							? ((UserSession) element).getCreateDate().toString() : Messages.getString("UNTITLED");
				}
				return Messages.getString("UNTITLED");
			}
		});
	}

	/**
	 * Populate user sessions table
	 * 
	 * @param tableViewer
	 */
	private void populateSessionTable(TableViewer tableViewer) {
		if (selectedAgent != null && selectedAgent.getSessions() != null) {
			tableViewer.setInput(selectedAgent.getSessions());
			tableViewer.refresh();
		}
	}

	/**
	 * Create new tab item for specified tab folder
	 * 
	 * @param tabFolder
	 * @param label
	 * @return
	 */
	private Composite createInputTab(CTabFolder tabFolder, String label) {
		CTabItem tab = new CTabItem(tabFolder, SWT.NONE);
		tab.setText(label);
		Composite tabContainer = new Composite(tabFolder, SWT.NONE);
		tabContainer.setLayout(new GridLayout(1, false));
		tab.setControl(tabContainer);
		return tabContainer;
	}

	/**
	 * Create new tab folder instance that can be used to contain tabs
	 * 
	 * @param composite
	 * @return
	 */
	private CTabFolder createTabFolder(final Composite composite) {
		CTabFolder tabFolder = new CTabFolder(composite, SWT.BORDER);
		GridData gdTabFolder = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		tabFolder.setLayoutData(gdTabFolder);
		tabFolder.setSelectionBackground(
				Display.getCurrent().getSystemColor(SWT.COLOR_TITLE_INACTIVE_BACKGROUND_GRADIENT));
		return tabFolder;
	}

}