package com.limegroup.gnutella.gui.download;

import com.limegroup.gnutella.gui.GUIMediator;

/**
 * Holder for the priorities column, writing 'Active' for active
 * downloads, 'Paused' for paused ones, nothing for complete ones,
 * and the priority of inactive ones.
 */
public final class PriorityHolder implements Comparable {
    
    /**
     * A priority holder to be used for active downloads.
     */
    public static final Object ACTIVE_P = new PriorityHolder(0);
    
    /**
     * A priority holder to be used for paused downloads.
     */
    public static final Object PAUSED_P =
        new PriorityHolder(Integer.MAX_VALUE - 1);
    
    /**"
     * A priority holder to be used for complete downloads.
     */
    public static final Object COMPLETE_P =
        new PriorityHolder(Integer.MAX_VALUE);
    
    /**
     * The 'active' string.
     */
    private static final String ACTIVE =
        GUIMediator.getStringResource("DOWNLOAD_PRIORITY_ACTIVE");
    
    /**
     * The 'paused' string.
     */
    private static final String PAUSED =
        GUIMediator.getStringResource("DOWNLOAD_STATUS_PAUSED");
    
    /**
     * The priority of this item.
     */
    private final int p;
    
    PriorityHolder(int priority) {
        p = priority;
    }
    
    public String toString() {
        switch(p) {
        case 0: return ACTIVE;
        case Integer.MAX_VALUE - 1: return PAUSED;
        case Integer.MAX_VALUE: return "";
        default: return p + "";
        }
    }
    
    public int compareTo(Object other) {
        int op = ((PriorityHolder)other).p;
        return p < op ? -1 : p > op ? 1 : 0;
    }
}
               