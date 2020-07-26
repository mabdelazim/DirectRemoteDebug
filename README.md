# DirectRemoteDebug


## Development

### Setup
* Download/install "Eclipse IDE for eclipse committers"
* Download/install "Eclipse CDT 2018-12"
* Set up the target platform to point to the installation above. 

Older version is needed because:

* org.eclipse.cdt.dsf.gdb.internal.ui.launching.CArgumentsTab;
* org.eclipse.cdt.dsf.gdb.internal.ui.launching.WorkingDirectoryBlock;

are gone in newer versions. An update is required to get this code working  This throws an exception in the newer versions, but the UI is able to recover, so this can still run on newer versions even missing the above 