package org.eclipse.cdt.launch.remote.direct;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.eclipse.cdt.debug.core.ICDTLaunchConfigurationConstants;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.osgi.util.NLS;
import org.eclipse.rse.core.RSECorePlugin;
import org.eclipse.rse.core.model.IHost;
import org.eclipse.rse.core.subsystems.ISubSystem;
import org.eclipse.rse.services.IService;
import org.eclipse.rse.services.shells.IHostShell;
import org.eclipse.rse.services.shells.IShellService;

public class RSEHelper {
	/**
	 * Throws a core exception with an error status object built from the given
	 * message, lower level exception, and error code.
	 * 
	 * @param message
	 *            the status message
	 * @param exception
	 *            lower level exception associated with the error, or
	 *            <code>null</code> if none
	 * @param code
	 *            error code
	 */
	
	
	public static void abort(String message, Throwable exception, int code) throws CoreException {
		IStatus status;
		if (exception != null) {
			MultiStatus multiStatus = new MultiStatus(Activator.PLUGIN_ID, code, message, exception);
			multiStatus.add(new Status(IStatus.ERROR, Activator.PLUGIN_ID, code, exception.getLocalizedMessage(), exception));
			status= multiStatus;
		} else {
			status= new Status(IStatus.ERROR, Activator.PLUGIN_ID, code, message, null);
		}
		throw new CoreException(status);
	}
	
	public static String spaceEscapify(String inputString) {
		if (inputString == null)
			return null;

		return inputString.replaceAll(" ", "\\\\ "); //$NON-NLS-1$ //$NON-NLS-2$
	}
	
	/**
	 * Find the first IShellServiceSubSystem service associated with the host.
	 * 
	 * @param host
	 *            the connection
	 * @return shell service subsystem, or <code>null</code> if not found.
	 */
	public static ISubSystem getSubSystemWithShellService(IHost host) {
		if (host == null)
			return null;
		ISubSystem[] subSystems = host.getSubSystems();
		IShellService ssvc = null;
		for (int i = 0; subSystems != null && i < subSystems.length; i++) {
			IService svc = subSystems[i].getSubSystemConfiguration()
					.getService(host);
			if (svc != null) {
				ssvc = (IShellService) svc.getAdapter(IShellService.class);
				if (ssvc != null) {
					return subSystems[i];
				}
			}
		}
		return null;
	}
	
	public static IHost getRemoteConnectionByName(String remoteConnection) {
		if (remoteConnection == null)
			return null;
		IHost[] connections = RSECorePlugin.getTheSystemRegistry().getHosts();
		for (int i = 0; i < connections.length; i++)
			if (connections[i].getAliasName().equals(remoteConnection))
				return connections[i];
		return null; // TODO Connection is not found in the list--need to react
		// somehow, throw the exception?

	}
	
	public static IHost getCurrentConnection(ILaunchConfiguration config)
			throws CoreException {
		String remoteConnection = config.getAttribute(
				IDirectRemoteConnectionConfigurationConstants.ATTR_REMOTE_CONNECTION,
				""); //$NON-NLS-1$
		IHost connection = getRemoteConnectionByName(remoteConnection);
		if (connection == null) {
			abort(Messages.RSEHelper_0, null,
					ICDTLaunchConfigurationConstants.ERR_INTERNAL_ERROR);
		}
		return connection;
	}

	public static IService getConnectedRemoteShellService(
			IHost currentConnection, IProgressMonitor monitor) throws Exception {
		ISubSystem subsystem = getSubSystemWithShellService(currentConnection);
		if (subsystem != null) {
			try {
				subsystem.connect(monitor, false);
			} catch (CoreException e) {
				throw e;
			} catch (OperationCanceledException e) {
				throw new CoreException(Status.CANCEL_STATUS);
			}
			if (!subsystem.isConnected())
				throw new Exception(Messages.RSEHelper_1);

			return (IShellService) subsystem.getSubSystemConfiguration()
					.getService(currentConnection).getAdapter(
							IShellService.class);
		} else {
			throw new Exception(Messages.RSEHelper_2);
		}
	}
	
	public static IHostShell execCmdInRemoteShell(ILaunchConfiguration config,
			String prelaunchCmd, String remoteCommandPath, String arguments,
			IProgressMonitor monitor) throws Exception {
		// The exit command is called to force the remote shell to close after
		// our command
		// is executed. This is to prevent a running process at the end of the
		// debug session.
		// See Bug 158786.
		monitor.beginTask(NLS.bind(Messages.RSEHelper_3,
				remoteCommandPath, arguments), 10);
		String realRemoteCommand = arguments == null ? spaceEscapify(remoteCommandPath)
				: spaceEscapify(remoteCommandPath) + " " + arguments; //$NON-NLS-1$

		String remoteCommand = realRemoteCommand;

		if (!prelaunchCmd.trim().equals("")) //$NON-NLS-1$
			remoteCommand = prelaunchCmd + ";" + remoteCommand; //$NON-NLS-1$

		IShellService shellService = null;
		shellService = (IShellService) getConnectedRemoteShellService(getCurrentConnection(config),
				EclipseCompat.getSubMonitor(monitor, 7));

		// This is necessary because runCommand does not actually run the
		// command right now.
		String env[] = new String[0];
		IHostShell hostShell = null;
		if (shellService != null) {
			hostShell = shellService.launchShell(
					"", env, EclipseCompat.getSubMonitor(monitor, 3)); //$NON-NLS-1$
			hostShell.writeToShell(remoteCommand);
			
		}
		monitor.done();
		return hostShell;
	}
	
	public static IHost[] getSuitableConnections() {
		ArrayList<IHost> shellConnections = new ArrayList<IHost>(Arrays.asList(RSECorePlugin.getTheSystemRegistry()
				.getHostsBySubSystemConfigurationCategory("shells"))); //$NON-NLS-1$
		ArrayList<IHost> terminalConnections = new ArrayList<IHost>(Arrays.asList(RSECorePlugin.getTheSystemRegistry()
		.getHostsBySubSystemConfigurationCategory("terminals")));//$NON-NLS-1$

		Iterator<IHost> iter = terminalConnections.iterator();
		while(iter.hasNext()){
			IHost terminalConnection = iter.next();
			if(!shellConnections.contains(terminalConnection)){
				shellConnections.add(terminalConnection);
			}
		}
		
		return (IHost[]) shellConnections.toArray(new IHost[shellConnections.size()]);
	}
	

}
