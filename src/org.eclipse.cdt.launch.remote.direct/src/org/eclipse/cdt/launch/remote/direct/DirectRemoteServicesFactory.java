package org.eclipse.cdt.launch.remote.direct;

import java.security.InvalidParameterException;

import org.eclipse.cdt.dsf.debug.service.IProcesses;
import org.eclipse.cdt.dsf.debug.service.command.ICommandControl;
import org.eclipse.cdt.dsf.debug.service.command.ICommandListener;
import org.eclipse.cdt.dsf.debug.service.command.ICommandResult;
import org.eclipse.cdt.dsf.debug.service.command.ICommandToken;
import org.eclipse.cdt.dsf.gdb.service.GdbDebugServicesFactory;
import org.eclipse.cdt.dsf.gdb.service.command.GDBControl;
import org.eclipse.cdt.dsf.mi.service.IMIBackend;
import org.eclipse.cdt.dsf.mi.service.command.AbstractMIControl;
import org.eclipse.cdt.dsf.mi.service.command.commands.MIBreakInsert;
import org.eclipse.cdt.dsf.mi.service.command.commands.MIInferiorTTYSet;
import org.eclipse.cdt.dsf.mi.service.command.output.MIInfo;
import org.eclipse.cdt.dsf.service.DsfSession;
import org.eclipse.debug.core.ILaunchConfiguration;

public class DirectRemoteServicesFactory extends GdbDebugServicesFactory {
	
	private DirectRemoteDebugLaunchDelegate directRemoteDelegate = null;
	public DirectRemoteServicesFactory(String version, DirectRemoteDebugLaunchDelegate directRemoteDelegate,ILaunchConfiguration config) {
		super(version, config);
		this.directRemoteDelegate = directRemoteDelegate;
	}
	
	protected IMIBackend createBackendGDBService(DsfSession session, ILaunchConfiguration lc) {
		return new GDBDirectRemoteBackend(session, lc, directRemoteDelegate);
	}	
	
	@Override
	protected ICommandControl createCommandControl(DsfSession session,
			ILaunchConfiguration config) {
		AbstractMIControl gdbControl = (GDBControl)super.createCommandControl(session, config);
		gdbControl.addCommandListener(new ICommandListener() {
			
			@Override
			public void commandSent(ICommandToken token) {
			//	String str = token.getCommand().toString();
			//	System.out.println(str);
				
			}
			
			@Override
			public void commandRemoved(ICommandToken token) {
				//String str = token.getCommand().toString();
				//System.out.println(str);
				
			}
			
			@Override
			public void commandQueued(ICommandToken token) {
				/*if (token.getCommand() instanceof MIInferiorTTYSet)
				{
					throw new IllegalArgumentException(" Direct remote debug doesn't allow MIInferiorTTYSet command");
				}
				if (token.getCommand() instanceof MIBreakInsert)
				{
					throw new IllegalArgumentException(" Direct remote debug doesn't allow MIInferiorTTYSet command");
				}
				*/
			//	String str = token.getCommand().toString();
			//	System.out.println(str);
				
			}
			
			@Override
			public void commandDone(ICommandToken token, ICommandResult result) {
				if (result instanceof MIInfo) {
					if(((MIInfo)result).isExit()) {
						Process remoteProcess = directRemoteDelegate.getRemoteProcess();
						if (remoteProcess != null) {
							remoteProcess.destroy();
						}
					}
				}
				
			}
		});
	    
		return gdbControl;
	}
	@Override
	protected IProcesses createProcessesService(DsfSession session) {
		// TODO Auto-generated method stub
		return DiretRemoteDebugProcessFactory.createProcess(super.createProcessesService(session));
	}
}
