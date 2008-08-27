package com.limegroup.gnutella.gui;

import java.sql.Struct;

import com.limegroup.gnutella.gui.AEDescListStruct;

public class AERecordStruct extends AEDescListStruct
{
    public AERecordStruct()
    {
        super(sizeOfAERecord);
    }

    public AERecordStruct(Struct struct, int i)
    {
        super(sizeOfAERecord);
        byte abyte0[] = struct.getBytesAt(i, sizeOfAERecord);
        setBytesAt(0, abyte0);
    }

    protected AERecordStruct(int i)
    {
        super(i);
    }

    public static final int sizeOfAERecord = 8;
}
