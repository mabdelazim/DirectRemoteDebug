package org.eclipse.cdt.launch.remote.direct;

import java.util.Map;

import org.eclipse.cdt.dsf.concurrent.DataRequestMonitor;
import org.eclipse.cdt.dsf.concurrent.DsfExecutor;
import org.eclipse.cdt.dsf.concurrent.RequestMonitor;
import org.eclipse.cdt.dsf.concurrent.Sequence;
import org.eclipse.cdt.dsf.concurrent.ReflectionSequence.Execute;
import org.eclipse.cdt.dsf.debug.service.IProcesses;
import org.eclipse.cdt.dsf.debug.service.IRunControl.IContainerDMContext;
import org.eclipse.cdt.dsf.gdb.service.GDBProcesses_7_4;
import org.eclipse.cdt.dsf.gdb.service.GDBProcesses_7_2_1;
import org.eclipse.cdt.dsf.gdb.service.GDBProcesses_7_3;
import org.eclipse.cdt.dsf.gdb.service.GDBProcesses_7_2;
import org.eclipse.cdt.dsf.gdb.service.GDBProcesses_7_1;
import org.eclipse.cdt.dsf.gdb.service.GDBProcesses_7_10;
import org.eclipse.cdt.dsf.gdb.service.GDBProcesses_7_0;
import org.eclipse.cdt.dsf.gdb.service.StartOrRestartProcessSequence_7_0;
import org.eclipse.cdt.dsf.gdb.service.StartOrRestartProcessSequence_7_10;
import org.eclipse.cdt.dsf.gdb.service.StartOrRestartProcessSequence_7_3;



public class DiretRemoteDebugProcessFactory
{
	
	public static class StartOrRestartProcessSequence_7_0_Wrapper extends StartOrRestartProcessSequence_7_0
	{

		public StartOrRestartProcessSequence_7_0_Wrapper(DsfExecutor executor,
				IContainerDMContext containerDmc,
				Map<String, Object> attributes, boolean restart,
				DataRequestMonitor<IContainerDMContext> rm) {
			super(executor, containerDmc, attributes, restart, rm);
			// TODO Auto-generated constructor stub
		}

		@Execute
		public void stepInitializeInputOutput(RequestMonitor rm) {
			rm.done();
		}
		
		
	}
	
	
	public static class StartOrRestartProcessSequence_7_3_Wrapper extends StartOrRestartProcessSequence_7_3
	{

		public StartOrRestartProcessSequence_7_3_Wrapper(DsfExecutor executor,
				IContainerDMContext containerDmc,
				Map<String, Object> attributes, boolean restart,
				DataRequestMonitor<IContainerDMContext> rm) {
			super(executor, containerDmc, attributes, restart, rm);
			// TODO Auto-generated constructor stub
		}

		@Execute
		public void stepInitializeInputOutput(RequestMonitor rm) {
			rm.done();
		}
		
		
	}
	
	public static class StartOrRestartProcessSequence_7_10_Wrapper extends StartOrRestartProcessSequence_7_10
	{

		public StartOrRestartProcessSequence_7_10_Wrapper(DsfExecutor executor,
				IContainerDMContext containerDmc,
				Map<String, Object> attributes, boolean restart,
				DataRequestMonitor<IContainerDMContext> rm) {
			super(executor, containerDmc, attributes, restart, rm);
			// TODO Auto-generated constructor stub
		}

		@Execute
		public void stepInitializeInputOutput(RequestMonitor rm) {
			rm.done();
		}
		
		
	}
	
	public static Sequence getSequeence(Sequence seq, DsfExecutor executor,
			IContainerDMContext containerDmc,
			Map<String, Object> attributes, boolean restart,
			DataRequestMonitor<IContainerDMContext> rm)
	{

		System.out.println("Incomming sequecne class" + seq.getClass());
		if (seq.getClass().equals(StartOrRestartProcessSequence_7_0.class))
		{
			return new StartOrRestartProcessSequence_7_0_Wrapper(executor, containerDmc, attributes, restart, rm);
		}
		
		if (seq.getClass().equals(StartOrRestartProcessSequence_7_3.class))
		{
			return new StartOrRestartProcessSequence_7_3_Wrapper(executor, containerDmc, attributes, restart, rm);
		}

		if (seq.getClass().equals(StartOrRestartProcessSequence_7_10.class))
		{
			return new StartOrRestartProcessSequence_7_10_Wrapper(executor, containerDmc, attributes, restart, rm);
		}
		
	    return seq;
		
	}
	/*
	 * 		if (GDB_7_4_VERSION.compareTo(fVersion) <= 0) {
			return new GDBProcesses_7_4(session);
		}
		if (GDB_7_2_1_VERSION.compareTo(fVersion) <= 0) {
			return new GDBProcesses_7_2_1(session);
		}
		if (GDB_7_2_VERSION.compareTo(fVersion) <= 0) {
			return new GDBProcesses_7_2(session);
		}
		if (GDB_7_1_VERSION.compareTo(fVersion) <= 0) {
			return new GDBProcesses_7_1(session);
		}
		if (GDB_7_0_VERSION.compareTo(fVersion) <= 0) {
			return new GDBProcesses_7_0(session);
		}
		return new GDBProcesses(session);
	 */
	

	public static IProcesses createProcess(IProcesses process)
	{
		System.out.println("Incomming process class" + process.getClass());
		
		if (process.getClass().equals(GDBProcesses_7_0.class))
		{
			return new GDBProcesses_7_0(process.getSession())
			{
				  @Override
				protected Sequence getStartOrRestartProcessSequence(
						DsfExecutor executor, IContainerDMContext containerDmc,
						Map<String, Object> attributes, boolean restart,
						DataRequestMonitor<IContainerDMContext> rm) {
						return getSequeence(super.getStartOrRestartProcessSequence(executor, containerDmc, attributes , restart, rm), 
								executor, containerDmc, attributes, restart, rm);
					
				}	
			};
		
		}
		
		if (process.getClass().equals(GDBProcesses_7_1.class))
		{
			return new GDBProcesses_7_1(process.getSession())
			{
				  @Override
				protected Sequence getStartOrRestartProcessSequence(
						DsfExecutor executor, IContainerDMContext containerDmc,
						Map<String, Object> attributes, boolean restart,
						DataRequestMonitor<IContainerDMContext> rm) {
						return getSequeence(super.getStartOrRestartProcessSequence(executor, containerDmc, attributes , restart, rm), 
								executor, containerDmc, attributes, restart, rm);
					
				}	
			};
		
		}
		if (process.getClass().equals(GDBProcesses_7_2.class))
		{
			return new GDBProcesses_7_2(process.getSession())
			{
				  @Override
				protected Sequence getStartOrRestartProcessSequence(
						DsfExecutor executor, IContainerDMContext containerDmc,
						Map<String, Object> attributes, boolean restart,
						DataRequestMonitor<IContainerDMContext> rm) {
						return getSequeence(super.getStartOrRestartProcessSequence(executor, containerDmc, attributes , restart, rm), 
								executor, containerDmc, attributes, restart, rm);
					
				}	
			};
		
		}
		if (process.getClass().equals(GDBProcesses_7_2_1.class))
		{
			return new GDBProcesses_7_2_1(process.getSession())
			{
				  @Override
				protected Sequence getStartOrRestartProcessSequence(
						DsfExecutor executor, IContainerDMContext containerDmc,
						Map<String, Object> attributes, boolean restart,
						DataRequestMonitor<IContainerDMContext> rm) {
						return getSequeence(super.getStartOrRestartProcessSequence(executor, containerDmc, attributes , restart, rm), 
								executor, containerDmc, attributes, restart, rm);
					
				}	
			};
		
		}
		
		if (process.getClass().equals(GDBProcesses_7_3.class))
		{
			return new GDBProcesses_7_3(process.getSession())
			{
				  @Override
				protected Sequence getStartOrRestartProcessSequence(
						DsfExecutor executor, IContainerDMContext containerDmc,
						Map<String, Object> attributes, boolean restart,
						DataRequestMonitor<IContainerDMContext> rm) {
						return getSequeence(super.getStartOrRestartProcessSequence(executor, containerDmc, attributes , restart, rm), 
								executor, containerDmc, attributes, restart, rm);
					
				}	
			};
		
		}
		
		
		if (process.getClass().equals(GDBProcesses_7_4.class))
		{
			return new GDBProcesses_7_4(process.getSession())
			{
				  @Override
				protected Sequence getStartOrRestartProcessSequence(
						DsfExecutor executor, IContainerDMContext containerDmc,
						Map<String, Object> attributes, boolean restart,
						DataRequestMonitor<IContainerDMContext> rm) {
					return getSequeence(super.getStartOrRestartProcessSequence(executor, containerDmc, attributes , restart, rm), 
										executor, containerDmc, attributes, restart, rm);
				}	
			};
		
		}

		
		if (process.getClass().equals(GDBProcesses_7_10.class))
		{
			return new GDBProcesses_7_10(process.getSession())
			{
				  @Override
				protected Sequence getStartOrRestartProcessSequence(
						DsfExecutor executor, IContainerDMContext containerDmc,
						Map<String, Object> attributes, boolean restart,
						DataRequestMonitor<IContainerDMContext> rm) {
					return getSequeence(super.getStartOrRestartProcessSequence(executor, containerDmc, attributes , restart, rm), 
										executor, containerDmc, attributes, restart, rm);
				}	
			};
		
		}

		return process;
	}
	
	
}
