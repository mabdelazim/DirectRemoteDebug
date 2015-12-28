package org.eclipse.cdt.launch.remote.direct;

import org.eclipse.cdt.dsf.gdb.internal.ui.launching.CMainTab;
import org.eclipse.cdt.dsf.gdb.launching.LaunchMessages;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.rse.core.IRSESystemType;
import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.files.ui.dialogs.SystemRemoteFileDialog;
import org.eclipse.rse.subsystems.files.core.subsystems.IRemoteFile;
import org.eclipse.rse.ui.actions.SystemNewConnectionAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class DirectRemoteCMainTab extends CMainTab {
	/* Labels and Error Messages */
	private static final String REMOTE_PROG_LABEL_TEXT = Messages.DirectRemoteCMainTab_0;
	private static final String REMOTE_WORK_SPACE_LABEL_TEXT = Messages.DirectRemoteCMainTab_1;
	private static final String REMOTE_WORKSPAPCE_TEXT_ERROR = Messages.DirectRemoteCMainTab_2;
	private static final String CONNECTION_TEXT_ERROR = Messages.DirectRemoteCMainTab_3;
	private static final String PRE_RUN_LABEL_TEXT = Messages.DirectRemoteCMainTab_4;

	/* Defaults */
	private static final String REMOTE_PATH_DEFAULT = EMPTY_STRING;
	
	/* SystemConnectionPropertyPage id*/
	private static final String SYSTEM_PAGE_ID = "org.eclipse.rse.SystemPropertyPage"; //$NON-NLS-1$

	protected Button newRemoteConnectionButton;
	protected Button editRemoteConnectionButton;
	protected Button remoteBrowseButton;
	protected Label connectionLabel;
	protected Combo connectionCombo;
	protected Text remoteWorkSpaceText;
	protected Label remoteWorkSpaceLabel;
	protected Button remoteWorkSpaceBrowseButton;
	protected Text preRunText;
	protected Label preRunLabel;
	protected Button attachButton;


	SystemNewConnectionAction action = null;

	
	public DirectRemoteCMainTab() {
		super(CMainTab.INCLUDE_BUILD_SETTINGS | CMainTab.DONT_CHECK_PROGRAM);
	}

	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		super.createControl(parent);
        fSearchButton.dispose();
       
		fProgLabel.setText(REMOTE_PROG_LABEL_TEXT);
		Composite comp = (Composite) getControl();
		createAttachButton(comp);
		createRemoteWorkSpacePath(comp);
		createPreRunText(comp);
		/* The RSE Connection dropdown with New button. */
		createVerticalSpacer(comp, 1);
		createRemoteConnectionGroup(comp, 4);
		/* The remote binary location and skip download option */
		createVerticalSpacer(comp, 1);
		PlatformUI
				.getWorkbench()
				.getHelpSystem()
				.setHelp(getControl(),
						Activator.PLUGIN_ID + ".launchgroup"); //$NON-NLS-1$

	}

	/*
	 * isValid
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#isValid
	 */
	@Override
	public boolean isValid(ILaunchConfiguration config) {
		boolean retVal = super.isValid(config);
		if (retVal == true) {
			setErrorMessage(null);
			int currentSelection = connectionCombo.getSelectionIndex();
			String connection_name = currentSelection >= 0 ? connectionCombo
					.getItem(currentSelection) : ""; //$NON-NLS-1$
			if (connection_name.equals("")) { //$NON-NLS-1$
				setErrorMessage(CONNECTION_TEXT_ERROR);
				retVal = false;
			}
			
			if (retVal == true && (!attachButton.getSelection())) {
				if (fProgText.getText().equals(EMPTY_STRING)) {
					setErrorMessage(LaunchMessages.getString("CMainTab.Program_does_not_exist"));
					retVal = false;
				}
			}

			if (retVal == true) {
				if (remoteWorkSpaceText.getText().equals(EMPTY_STRING)) {
					setErrorMessage(REMOTE_WORKSPAPCE_TEXT_ERROR);
					retVal = false;
				}
			}
		}
		return retVal;
	}

	protected void createRemoteConnectionGroup(Composite parent, int colSpan) {
		Composite projComp = new Composite(parent, SWT.NONE);
		GridLayout projLayout = new GridLayout();
		projLayout.numColumns = 5;
		projLayout.marginHeight = 0;
		projLayout.marginWidth = 0;
		projComp.setLayout(projLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = colSpan;
		projComp.setLayoutData(gd);

		connectionLabel = new Label(projComp, SWT.NONE);
		connectionLabel.setText(Messages.DirectRemoteCMainTab_6);
		gd = new GridData();
		gd.horizontalSpan = 1;
		connectionLabel.setLayoutData(gd);

		connectionCombo = new Combo(projComp, SWT.DROP_DOWN | SWT.READ_ONLY);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		connectionCombo.setLayoutData(gd);
		connectionCombo.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				//useDefaultsFromConnection();
				updateConnectionButtons();
				setDirty(true);
				updateLaunchConfigurationDialog();
			}
		});

		newRemoteConnectionButton = createPushButton(projComp,
				Messages.DirectRemoteCMainTab_7, null);
		newRemoteConnectionButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent evt) {
				handleNewRemoteConnectionSelected();
				updateLaunchConfigurationDialog();
				updateConnectionPulldown();
			}
		});
		
		editRemoteConnectionButton = createPushButton(projComp,
				Messages.DirectRemoteCMainTab_8, null);
		editRemoteConnectionButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent evt) {
				handleEditRemoteConnectionSelected();
			}
		});

		updateConnectionPulldown();
	}
	/*
	 * createDownloadOption This creates the skip download check button.
	 */
	protected void createAttachButton(Composite parent) {
		Composite mainComp = new Composite(parent, SWT.NONE);
		GridLayout mainLayout = new GridLayout();
		mainLayout.marginHeight = 0;
		mainLayout.marginWidth = 0;
		mainComp.setLayout(mainLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		mainComp.setLayoutData(gd);

		attachButton = createCheckButton(mainComp,
				Messages.DirectRemoteCMainTab_9);
		attachButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent evt) {
				updateLaunchConfigurationDialog();
			}
		});
		attachButton.setEnabled(true);
	}
	protected void createRemoteWorkSpacePath(Composite parent) {
		Composite mainComp = new Composite(parent, SWT.NONE);
		GridLayout mainLayout = new GridLayout();
		mainLayout.numColumns = 2;
		mainLayout.marginHeight = 0;
		mainLayout.marginWidth = 0;
		mainComp.setLayout(mainLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		mainComp.setLayoutData(gd);

		remoteWorkSpaceLabel = new Label(mainComp, SWT.NONE);
		remoteWorkSpaceLabel.setText(REMOTE_WORK_SPACE_LABEL_TEXT);
		gd = new GridData();
		gd.horizontalSpan = 2;
		remoteWorkSpaceLabel.setLayoutData(gd);

		remoteWorkSpaceText = new Text(mainComp, SWT.SINGLE | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 1;
		remoteWorkSpaceText.setLayoutData(gd);
		remoteWorkSpaceText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent evt) {
				updateLaunchConfigurationDialog();
			}
		});

		remoteWorkSpaceBrowseButton = createPushButton(mainComp,
				Messages.DirectRemoteCMainTab_10, null);
		remoteWorkSpaceBrowseButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent evt) {
				String newText= handleBrowseButtonSelected(Messages.DirectRemoteCMainTab_11, true);
				if (newText != null) {
					remoteWorkSpaceText.setText(newText);
					updateLaunchConfigurationDialog();	
				}
				
			}
		});

	}

	/*
	 * createTargetExePath This creates the remote path user-editable textfield
	 * on the Main Tab.
	 */
	protected void createPreRunText(Composite parent) {
		Composite mainComp = new Composite(parent, SWT.NONE);
		GridLayout mainLayout = new GridLayout();
		mainLayout.numColumns = 2;
		mainLayout.marginHeight = 0;
		mainLayout.marginWidth = 0;
		mainComp.setLayout(mainLayout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		mainComp.setLayoutData(gd);

				// Commands to run before execution
		preRunLabel = new Label(mainComp, SWT.NONE);
		preRunLabel.setText(PRE_RUN_LABEL_TEXT);
		gd = new GridData();
		gd.horizontalSpan = 2;
		preRunLabel.setLayoutData(gd);

		preRunText = new Text(mainComp, SWT.MULTI | SWT.BORDER);
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 2;
		preRunText.setLayoutData(gd);
		preRunText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent evt) {
				updateLaunchConfigurationDialog();
			}
		});

	}

	
	
	protected void handleNewRemoteConnectionSelected() {
		if (action == null) {
			action = new SystemNewConnectionAction(getControl().getShell(),
					false, false, null);
		}

		try {
			action.run();
		} catch (Exception e) {
			// Ignore
		}
	}
	
	/**
	 * Opens the <code>SystemConnectionPropertyPage</code> page for the selected connection.
	 */
	protected void handleEditRemoteConnectionSelected() {
		IHost currentConnectionSelected = getCurrentConnection();
		PreferenceDialog dialog = PreferencesUtil.createPropertyDialogOn(getControl().getShell(), currentConnectionSelected, SYSTEM_PAGE_ID, null, null);
		if (dialog != null) {
			dialog.open();
		}
	}

	protected IHost getCurrentConnection() {
		int currentSelection = connectionCombo.getSelectionIndex();
		String remoteConnection = currentSelection >= 0 ? connectionCombo
				.getItem(currentSelection) : null;
		return RSEHelper.getRemoteConnectionByName(remoteConnection);
	}

	@Override
	protected String handleBrowseButtonSelected(String title) {
		return handleBrowseButtonSelected(title, false);
	}
	
	protected String handleBrowseButtonSelected(String title, boolean isDir) {
		IHost currentConnectionSelected = getCurrentConnection();
		SystemRemoteFileDialog dlg = new SystemRemoteFileDialog(getControl()
				.getShell(),
				Messages.DirectRemoteCMainTab_12,
				currentConnectionSelected);
		dlg.setBlockOnOpen(true);
		if (dlg.open() == Window.OK) {
			Object retObj = dlg.getSelectedObject();
			if (retObj instanceof IRemoteFile) {
				IRemoteFile selectedFile = (IRemoteFile) retObj;
				String absPath = selectedFile.getAbsolutePath();
				if (selectedFile.isDirectory() && !isDir) {
					// The user selected a destination folder to upload the binary
					// Append the Program name as the default file destination
					IPath appPath = new Path(fProgText.getText().trim());
					String lastSegment = appPath.lastSegment();
					if (lastSegment != null && lastSegment.trim().length() > 0) {
						IPath remotePath = new Path(selectedFile.getAbsolutePath()).append(lastSegment.trim());
						absPath = remotePath.toPortableString();
					}
				}
				return absPath;
			}
		}
		return null;
	}


	
	protected void updateConnectionPulldown() {
		if (!RSECorePlugin.isInitComplete(RSECorePlugin.INIT_MODEL))
			try {
				RSECorePlugin.waitForInitCompletion(RSECorePlugin.INIT_MODEL);
			} catch (InterruptedException e) {
				return;
			}
		// already initialized
		connectionCombo.removeAll();
		IHost[] connections = RSEHelper.getSuitableConnections();
		for (int i = 0; i < connections.length; i++) {
			IRSESystemType sysType = connections[i].getSystemType();
			if (sysType != null && sysType.isEnabled()) {
				connectionCombo.add(connections[i].getAliasName());
			}
		}

		if (connections.length > 0) {
			connectionCombo.select(connections.length - 1);
		}
		updateConnectionButtons();
	}

	private void updateConnectionButtons() {

		if ((editRemoteConnectionButton == null)
				|| editRemoteConnectionButton.isDisposed()) {
			return;
		}
		boolean bEnable = false;
		IHost currentConnectionSelected = getCurrentConnection();
		if (currentConnectionSelected != null) {
			IRSESystemType sysType = currentConnectionSelected.getSystemType();
			if (sysType != null && sysType.isEnabled() && !sysType.isLocal()) {
				bEnable = true;
			}
		}
		editRemoteConnectionButton.setEnabled(bEnable);
	}





	
	@Override
	public void initializeFrom(ILaunchConfiguration config) {
		String remoteConnection = null;
		try {
			attachButton.setSelection(config.getAttribute(IDirectRemoteConnectionConfigurationConstants.ATTR_REMOTE_IS_ATTACH, false));
			remoteWorkSpaceText.setText(config.getAttribute(IDirectRemoteConnectionConfigurationConstants.ATTR_REMOTE_WORKSPACE, EMPTY_STRING));
			preRunText.setText(config.getAttribute(IDirectRemoteConnectionConfigurationConstants.ATTR_PRERUN_COMMANDS, EMPTY_STRING));
			remoteConnection = config
					.getAttribute(
							IDirectRemoteConnectionConfigurationConstants.ATTR_REMOTE_CONNECTION,
							""); //$NON-NLS-1$
		} catch (CoreException ce) {
			// Ignore
		}

		String[] items = connectionCombo.getItems();
		int i = 0;
		for (i = 0; i < items.length; i++)
			if (items[i].equals(remoteConnection))
				break;
		/*
		 * Select the last used connection in the connecion pulldown if it still
		 * exists.
		 */
		if (i < items.length)
			connectionCombo.select(i);
		else if (items.length > 0)
			connectionCombo.select(0);

		super.initializeFrom(config);

		
		updateConnectionButtons();
	}

	/*
	 * performApply
	 * 
	 * @see org.eclipse.debug.ui.ILaunchConfigurationTab#performApply
	 */
	@Override
	public void performApply(ILaunchConfigurationWorkingCopy config) {

		config.setAttribute(IDirectRemoteConnectionConfigurationConstants.ATTR_REMOTE_IS_ATTACH, attachButton.getSelection());
		config.setAttribute(IDirectRemoteConnectionConfigurationConstants.ATTR_REMOTE_WORKSPACE, remoteWorkSpaceText.getText());
		int currentSelection = connectionCombo.getSelectionIndex();
		config.setAttribute(
				IDirectRemoteConnectionConfigurationConstants.ATTR_REMOTE_CONNECTION,
				currentSelection >= 0 ? connectionCombo
						.getItem(currentSelection) : null);

		config.setAttribute(
				IDirectRemoteConnectionConfigurationConstants.ATTR_PRERUN_COMMANDS,
				preRunText.getText());
		super.performApply(config);
	}

	@Override
	public String getId() {
		return "org.eclipse.cdt.launch.remote.dsf.mainTab"; //$NON-NLS-1$
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy config) {
		super.setDefaults(config);
		config.setAttribute(
				IDirectRemoteConnectionConfigurationConstants.ATTR_REMOTE_CONNECTION,
				EMPTY_STRING);;
		config.setAttribute(
				IDirectRemoteConnectionConfigurationConstants.ATTR_PRERUN_COMMANDS,
				EMPTY_STRING);
	}

}
