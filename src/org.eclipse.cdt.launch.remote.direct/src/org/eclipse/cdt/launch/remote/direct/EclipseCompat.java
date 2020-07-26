package org.eclipse.cdt.launch.remote.direct;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;
//import org.eclipse.core.runtime.SubProgressMonitor;


public class EclipseCompat {
	/**
	 * Get the "SubProgressMonitor" 
	 * @param monitor
	 * @param ticks
	 * @return
	 */
	public static IProgressMonitor getSubMonitor(IProgressMonitor monitor, int work) {
		// One could use reflection here to choose between the newer
		// and older versions if needed and backwards compat. is desired. 
		return SubMonitor.convert(monitor, work);
		//return new SubProgressMonitor(monitor, ticks);
	}
}
