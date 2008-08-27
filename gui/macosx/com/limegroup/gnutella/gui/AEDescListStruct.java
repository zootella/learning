package com.limegroup.gnutella.gui;

import java.sql.Struct;

import com.limegroup.gnutella.gui.AEDescStruct;

public class AEDescListStruct extends AEDescStruct
{

    public AEDescListStruct()
    {
        super(sizeOfAEDescList);
    }

    public AEDescListStruct(Struct struct, int i)
    {
        super(sizeOfAEDescList);
        byte abyte0[] = struct.getBytesAt(i, sizeOfAEDescList);
        setBytesAt(0, abyte0);
    }

    protected AEDescListStruct(int i)
    {
        super(i);
    }

    public static final int sizeOfAEDescList = 8;
}
