package org.eclipse.cdt.launch.remote.direct;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.cdt.dsf.gdb.service.GDBBackend;
import org.eclipse.cdt.dsf.gdb.service.SessionType;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.cdt.core.parser.util.StringUtil;

public class GDBDirectRemoteBackend extends GDBBackend {
	private final ILaunchConfiguration fLaunchConfiguration;
	private DirectRemoteDebugLaunchDelegate delegate;
	public GDBDirectRemoteBackend(DsfSession session, ILaunchConfiguration lc, DirectRemoteDebugLaunchDelegate delegate) {
		super(session, lc);
		this.delegate = delegate;
		fLaunchConfiguration = lc;
	}


	protected Process launchGDBProcess(String commandLine) throws CoreException {
		
		delegate.getShell().writeToShell(commandLine);
		return delegate.getRemoteProcess();
	}

	@Override
	protected Process launchGDBProcess(String[] commandLine)
			throws CoreException {
		 String cmd  = StringUtil.join(commandLine, " "); //$NON-NLS-1$
		return launchGDBProcess(cmd);
	}

	/**
	 * Use remote work space for default working directory
	 */
	@Override
	public IPath getGDBWorkingDirectory() throws CoreException {
		
		String loc = fLaunchConfiguration.getAttribute(ICDTLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY, (String)null);
		if (loc == null) 
		{
			loc = fLaunchConfiguration.getAttribute(IDirectRemoteConnectionConfigurationConstants.ATTR_REMOTE_WORKSPACE, (String)null);
		}
		
		return Path.fromOSString(loc);
	}

	/**
	 * Overrides to escape program path validation step
	 */
	@Override
	public IPath getProgramPath() {
		String loc = null;
		try {
			loc = fLaunchConfiguration.getAttribute(ICDTLaunchConfigurationConstants.ATTR_PROGRAM_NAME, (String)null);
		} catch (CoreException e) {
			e.printStackTrace();

		}
		return Path.fromOSString(loc);
	}

	/**
	 * DSF framework sees us as local running and we create the gdb connection in a seamless way to DSF.
	 */
	@Override
	public SessionType getSessionType() {
		return SessionType.LOCAL;
	}

	@Override
	public boolean getIsAttachSession() {
		boolean retVal = false;
		try {
			retVal = fLaunchConfiguration.getAttribute(IDirectRemoteConnectionConfigurationConstants.ATTR_REMOTE_IS_ATTACH,false);
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		}
		return retVal;
		
	}
	
	
	
	

}
