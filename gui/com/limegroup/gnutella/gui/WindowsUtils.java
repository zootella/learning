package com.limegroup.gnutella.gui;

import com.limegroup.gnutella.util.CommonUtils;
import java.io.File;

/**
 * A collection of Windows-related GUI utility methods.
 */
public class WindowsUtils {
    
    private WindowsUtils() {}

    /**
     * Determines if we know how to set the login status.
     */    
    public static boolean isLoginStatusAvailable() {
        return CommonUtils.isWindows2000orXP();
    }
    
    
    /**
     * Sets the login status.  Only available on W2k+.
     */
    public static void setLoginStatus(boolean allow) {
        if(!isLoginStatusAvailable())
            return;
        
        
        File src = new File("LimeWire On Startup.lnk");
        File homeDir = CommonUtils.getUserHomeDir();
        File startup = new File(homeDir, "Start Menu\\Programs\\Startup");
        File dst = new File(startup, "LimeWire On Startup.lnk");
        
        if(allow)
            CommonUtils.copy(src, dst);
        else
            dst.delete();
    }
}