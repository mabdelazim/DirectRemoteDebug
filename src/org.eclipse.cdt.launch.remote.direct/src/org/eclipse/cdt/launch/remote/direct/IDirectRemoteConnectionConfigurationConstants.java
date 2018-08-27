package org.eclipse.cdt.launch.remote.direct;

//import org.eclipse.cdt.debug.mi.core.IGDBServerMILaunchConfigurationConstants;
import org.eclipse.debug.core.DebugPlugin;

public interface IDirectRemoteConnectionConfigurationConstants /*extends IGDBServerMILaunchConfigurationConstants*/ {
	public static final String ATTR_REMOTE_CONNECTION = 
			DebugPlugin.getUniqueIdentifier() + ".REMOTE_TCP"; //$NON-NLS-1$
	public static final String ATTR_PRERUN_COMMANDS = DebugPlugin.getUniqueIdentifier() + ".ATTR_PRERUN_CMDS";
	public static final String ATTR_REMOTE_WORKSPACE = DebugPlugin.getUniqueIdentifier() + ".ATTR_REMOTE_WORKSPACE_APTH";
	public static final String ATTR_REMOTE_IS_ATTACH = DebugPlugin.getUniqueIdentifier() + ".ATTR_REMOTE_IS_ATTACH_TO_PRCESSS";
}
