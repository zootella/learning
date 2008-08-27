package com.limegroup.gnutella.gui;

import java.sql.Struct;

import com.limegroup.gnutella.gui.AERecordStruct;

public class AppleEventStruct extends AERecordStruct
{

    public AppleEventStruct()
    {
        super(sizeOfAppleEvent);
    }

    public AppleEventStruct(Struct struct, int i)
    {
        super(sizeOfAppleEvent);
        byte abyte0[] = struct.getBytesAt(i, sizeOfAppleEvent);
        setBytesAt(0, abyte0);
    }

    protected AppleEventStruct(int i)
    {
        super(i);
    }

    public static final int sizeOfAppleEvent = 8;
}
