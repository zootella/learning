package com.limegroup.gnutella.settings;

/**
 * Settings for Statistics related stuff.
 */ 

public class StatisticsSettings extends LimeProps {
    
    public static BooleanSetting RECORD_VM_STATS = 
        FACTORY.createBooleanSetting("RECORD_VM_STATS", false);
}
