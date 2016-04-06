package tr.org.liderahenk.liderconsole.core.dialogs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DateTime;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tr.org.liderahenk.liderconsole.core.constants.LiderConstants;
import tr.org.liderahenk.liderconsole.core.i18n.Messages;
import tr.org.liderahenk.liderconsole.core.model.Policy;
import tr.org.liderahenk.liderconsole.core.model.Profile;
import tr.org.liderahenk.liderconsole.core.rest.enums.RestDNType;
import tr.org.liderahenk.liderconsole.core.rest.requests.PolicyExecutionRequest;
import tr.org.liderahenk.liderconsole.core.rest.requests.PolicyRequest;
import tr.org.liderahenk.liderconsole.core.rest.utils.PolicyUtils;
import tr.org.liderahenk.liderconsole.core.rest.utils.ProfileUtils;
import tr.org.liderahenk.liderconsole.core.utils.SWTResourceManager;
import tr.org.liderahenk.liderconsole.core.widgets.Notifier;

/**
 * This class provides policy execution dialog. All plugin profiles will be
 * automatically added to this dialog if they contribute to
 * {@link LiderConstants.EXTENSION_POINTS.POLICY_MENU} extension point in their
 * plugin.xml files.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
public class PolicyExecutionNewDialog extends DefaultLiderDialog {

	private static final Logger logger = LoggerFactory.getLogger(PolicyExecutionNewDialog.class);

	private Combo cmbDnType;
	private Text txtLabel;
	private Text txtDesc;
	private DateTime dtActivationDate;
	private Button btnEnableDate;

	private List<Combo> comboList = null;
	private Set<String> dnSet;
	private final String[] dnTypeArr = new String[] { "ONLY_USER", "ONLY_AGENT", "ONLY_GROUP", "ALL" };
	private final Integer[] dnTypeValueArr = new Integer[] { RestDNType.USER.getId(), RestDNType.AHENK.getId(),
			RestDNType.GROUP.getId(), RestDNType.ALL.getId() };

	public PolicyExecutionNewDialog(Shell parentShell, Set<String> dnSet) {
		super(parentShell);
		this.dnSet = dnSet;
	}

	/**
	 * Create policy input widgets
	 */
	@Override
	protected Control createDialogArea(Composite parent) {

		parent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		parent.setLayout(new GridLayout(1, false));

		Composite composite = (Composite) super.createDialogArea(parent);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		composite.setLayout(new GridLayout(2, false));

		// Policy label
		Label lblLabel = new Label(composite, SWT.NONE);
		lblLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblLabel.setText(Messages.getString("POLICY_LABEL"));

		txtLabel = new Text(composite, SWT.BORDER);
		txtLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// Policy description
		Label lblDesc = new Label(composite, SWT.NONE);
		lblDesc.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblDesc.setText(Messages.getString("DESCRIPTION"));

		txtDesc = new Text(composite, SWT.BORDER);
		txtDesc.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		// DN type label
		Label lblDnType = new Label(composite, SWT.NONE);
		lblDnType.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblDnType.setText(Messages.getString("DN_TYPE_LABEL"));

		// DN type
		cmbDnType = new Combo(composite, SWT.DROP_DOWN | SWT.READ_ONLY);
		cmbDnType.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		for (int i = 0; i < dnTypeArr.length; i++) {
			String i18n = Messages.getString(dnTypeArr[i]);
			if (i18n != null && !i18n.isEmpty()) {
				cmbDnType.add(i18n);
				cmbDnType.setData(i + "", dnTypeValueArr[i]);
			}
		}
		cmbDnType.select(3); // by default, select 'ALL'

		Composite cmpDate = new Composite(composite, SWT.NONE);
		cmpDate.setLayout(new GridLayout(2, false));
		cmpDate.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		// Activation date enable/disable checkbox
		btnEnableDate = new Button(cmpDate, SWT.CHECK);
		btnEnableDate.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		btnEnableDate.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				dtActivationDate.setEnabled(btnEnableDate.getSelection());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		// Activation date label
		Label lblActivationDate = new Label(cmpDate, SWT.NONE);
		lblActivationDate.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblActivationDate.setText(Messages.getString("ACTIVATION_DATE_LABEL"));

		// Activation date
		dtActivationDate = new DateTime(composite, SWT.CALENDAR | SWT.BORDER);
		dtActivationDate.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		dtActivationDate.setEnabled(btnEnableDate.getSelection());

		Label lblProfiles = new Label(parent, SWT.BOLD);
		lblProfiles.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		lblProfiles.setText(Messages.getString("PROFILE_DEFINITION"));

		// Create child composite for policy
		Composite childComposite = new Composite(parent, SWT.BORDER);
		childComposite.setLayout(new GridLayout(3, false));
		childComposite.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

		// Find policy contributions
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		IExtensionPoint extensionPoint = registry.getExtensionPoint(LiderConstants.EXTENSION_POINTS.POLICY_MENU);
		IConfigurationElement[] config = extensionPoint.getConfigurationElements();

		if (config != null) {

			// Command service will be used to trigger handler class related to
			// specified 'profileCommandId'
			final ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);

			// Init combo list. This will be used to iterate over combo widgets
			// in order to collect selected IDs.
			comboList = new ArrayList<Combo>();

			// Iterate over each extension point provided by plugins
			for (IConfigurationElement e : config) {
				try {
					// Read extension point attributes
					String label = e.getAttribute("label");
					final String pluginName = e.getAttribute("pluginName");
					final String pluginVersion = e.getAttribute("pluginVersion");
					final String profileCommandId = e.getAttribute("profileCommandId");

					// Plugin label
					Label pluginLabel = new Label(childComposite, SWT.NONE);
					pluginLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
					pluginLabel.setText(label);

					// Profiles combo
					final Combo combo = new Combo(childComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
					combo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
					// Populate combo with active profiles
					List<Profile> profiles = ProfileUtils.list(pluginName, pluginVersion, null, true);
					populateCombo(combo, profiles);
					comboList.add(combo);

					// Add profile button
					Button btnAdd = new Button(childComposite, SWT.NONE);
					btnAdd.setText(Messages.getString("ADD"));
					btnAdd.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
					btnAdd.setImage(SWTResourceManager.getImage(LiderConstants.PLUGIN_IDS.LIDER_CONSOLE_CORE,
							"icons/16/add.png"));
					btnAdd.addSelectionListener(new SelectionListener() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							// Trigger profile handler class in order to allow
							// user add/create new profiles
							Command command = commandService.getCommand(profileCommandId);
							try {
								command.executeWithChecks(new ExecutionEvent());
								// Refresh profile combo
								List<Profile> profiles = ProfileUtils.list(pluginName, pluginVersion, null, true);
								populateCombo(combo, profiles);
							} catch (Exception e1) {
								logger.error(e1.getMessage(), e1);
							}
						}

						@Override
						public void widgetDefaultSelected(SelectionEvent e) {
						}
					});

				} catch (Exception e1) {
					logger.error(e1.getMessage(), e1);
				}
			}
		}

		applyDialogFont(composite);
		return composite;
	}

	/**
	 * Populate combo with specified profiles.
	 * 
	 * @param combo
	 * @param profiles
	 */
	private void populateCombo(Combo combo, List<Profile> profiles) {
		if (profiles != null) {
			// Clear combo first!
			combo.clearSelection();
			combo.removeAll();
			for (int i = 0; i < profiles.size(); i++) {
				Profile profile = profiles.get(i);
				combo.add(profile.getLabel() + " " + profile.getCreateDate());
				combo.setData(i + "", profile);
			}
			combo.select(0); // select first profile by default
		}
	}

	/**
	 * Handle OK button press
	 */
	@Override
	protected void okPressed() {

		setReturnCode(OK);

		// Check if label is empty
		if (txtLabel.getText().isEmpty()) {
			Notifier.warning(null, Messages.getString("FILL_LABEL_FIELD"));
			return;
		}

		// Create save-policy request
		PolicyRequest policy = new PolicyRequest();
		policy.setActive(true);
		policy.setDescription(txtDesc.getText());
		policy.setLabel(txtLabel.getText());
		policy.setProfileIdList(getSelectedProfileIds());
		logger.debug("Policy request: {}", policy);

		boolean success = false;
		Policy savedPolicy = null;
		try {
			savedPolicy = PolicyUtils.add(policy);
			success = true;
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			Notifier.error(null, Messages.getString("ERROR_ON_SAVE"));
		}

		if (success) {
			// Create execute-policy request
			PolicyExecutionRequest policyExec = new PolicyExecutionRequest();
			policyExec.setId(savedPolicy.getId());
			policyExec.setDnType(getSelectedDnType());
			policyExec.setDnList(new ArrayList<String>(this.dnSet));
			policyExec.setActivationDate(btnEnableDate.getSelection() ? convertDate(dtActivationDate) : null);
			logger.debug("Policy request: {}", policy);

			try {
				PolicyUtils.execute(policyExec);
			} catch (Exception e) {
				logger.error(e.getMessage(), e);
				Notifier.error(null, Messages.getString("ERROR_ON_EXECUTE"));
			}
		}

		close();
	}

	/**
	 * Convert DateTime instance to java.util.Date instance
	 * 
	 * @param dtActivationDate2
	 * @return
	 */
	private Date convertDate(DateTime dateTime) {
		Calendar instance = Calendar.getInstance();
		instance.set(Calendar.DAY_OF_MONTH, dateTime.getDay());
		instance.set(Calendar.MONTH, dateTime.getMonth());
		instance.set(Calendar.YEAR, dateTime.getYear());
		return instance.getTime();
	}

	/**
	 * Iterave over all combo widgets and collects selected profile IDs.
	 * 
	 * @return selected profile ID list.
	 */
	private List<Long> getSelectedProfileIds() {
		List<Long> idList = null;
		if (comboList != null) {
			idList = new ArrayList<Long>();
			for (Combo combo : comboList) {
				Long profileId = getSelectedProfileId(combo);
				if (profileId != null) {
					idList.add(profileId);
				}
			}
		}
		logger.debug("Selected profile IDs: {}", idList);
		return idList;
	}

	/**
	 * 
	 * @param combo
	 * @return profile ID if selected, otherwise null
	 */
	private Long getSelectedProfileId(Combo combo) {
		int selectionIndex = combo.getSelectionIndex();
		if (selectionIndex > -1 && combo.getItem(selectionIndex) != null
				&& combo.getData(selectionIndex + "") != null) {
			Profile profile = (Profile) combo.getData(selectionIndex + "");
			return profile.getId();
		}
		return null;
	}

	/**
	 * 
	 * @return DN type if selected, otherwise 'ALL'
	 */
	private RestDNType getSelectedDnType() {
		int selectionIndex = cmbDnType.getSelectionIndex();
		if (selectionIndex > -1 && cmbDnType.getItem(selectionIndex) != null
				&& cmbDnType.getData(selectionIndex + "") != null) {
			Integer id = (Integer) cmbDnType.getData(selectionIndex + "");
			return RestDNType.getType(id);
		}
		return RestDNType.ALL;
	}

}
