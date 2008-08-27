package com.limegroup.gnutella.gui;

import java.sql.Struct;

public class AEDescStruct extends ByteArrayStruct
{

    public AEDescStruct()
    {
        super(sizeOfAEDesc);
    }

    public AEDescStruct(Struct struct, int i)
    {
        super(sizeOfAEDesc);
        byte abyte0[] = struct.getBytesAt(i, sizeOfAEDesc);
        setBytesAt(0, abyte0);
    }

    protected AEDescStruct(int i)
    {
        super(i);
    }

    public final int getDescriptorType()
    {
        return getIntAt(0);
    }

    public final void setDescriptorType(int i)
    {
        setIntAt(0, i);
    }

    public final int getDataHandle()
    {
        return getIntAt(4);
    }

    public final void setDataHandle(int i)
    {
        setIntAt(4, i);
    }

    public long getValue()
    {
        return getLongAt(0);
    }

    public static final int sizeOfAEDesc = 8;
}
