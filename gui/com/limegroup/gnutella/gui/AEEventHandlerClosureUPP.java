package com.limegroup.gnutella.gui;

import com.apple.mrj.jdirect.Linker;
import com.apple.mrj.jdirect.MethodClosure;
import com.apple.mrj.macos.carbon.CarbonLock;
import com.apple.mrj.macos.frameworks.ApplicationServices;

public class AEEventHandlerClosureUPP extends MethodClosure 
    implements ApplicationServices {
    
    private static final Object linkage =
        new Linker(AEEventHandlerClosureUPP.class);
       
    private int upp;
    
    public AEEventHandlerClosureUPP(AEEventHandlerInterface handler) {
        super(handler, "AEEventHandler", "(III)S");
        
        try {
            CarbonLock.acquire();
            upp = NewAEEventHandlerUPP(super.getProc());
        } finally {
            CarbonLock.release();
        }
    }
    
    public static native int NewAEEventHandlerUPP(int upp);
    
}
