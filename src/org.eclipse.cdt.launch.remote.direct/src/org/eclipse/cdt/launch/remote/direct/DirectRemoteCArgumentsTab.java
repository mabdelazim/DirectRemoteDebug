package org.eclipse.cdt.launch.remote.direct;

import org.eclipse.cdt.dsf.gdb.internal.ui.launching.CArgumentsTab;
import org.eclipse.cdt.dsf.gdb.internal.ui.launching.WorkingDirectoryBlock;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.swt.widgets.Composite;

public class DirectRemoteCArgumentsTab extends CArgumentsTab {

	public DirectRemoteCArgumentsTab() {
		fWorkingDirectoryBlock = new RemoteWorkingDirectory();
		
	}
	
	protected class RemoteWorkingDirectory extends WorkingDirectoryBlock
	{
		@Override
		protected void setDefaultWorkingDir() {

			// Local directory
			try {
				fWorkingDirText.setText(getLaunchConfiguration().getAttribute(IDirectRemoteConnectionConfigurationConstants.ATTR_REMOTE_WORKSPACE, "")); //$NON-NLS-1$
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
				
		@Override
		public void createControl(Composite parent) {

			super.createControl(parent);
			fVariablesButton.dispose();
			fWorkspaceButton.dispose();
		}
		
		/**
		 * The default working dir check box has been toggled.
		 */
		protected void handleUseDefaultWorkingDirButtonSelected() {
			boolean def = isDefaultWorkingDirectory();
			if (def) {
				setDefaultWorkingDir();
			}
			fWorkingDirText.setEnabled(!def);
			fFileSystemButton.setEnabled(!def);
		}

		@Override
		protected void handleWorkingDirBrowseButtonSelected() {
			DirectRemoteCMainTab main = (DirectRemoteCMainTab)(getLaunchConfigurationDialog().getTabs())[0];
			String text = main.handleBrowseButtonSelected(Messages.DirectRemoteCArgumentsTab_1, true);
			if (text != null) {
				fWorkingDirText.setText(text);
			}
		}

		@Override
		public boolean isValid(ILaunchConfiguration config) {
			
			// escape checking for working directory
 			return true;
		}
	}
}
