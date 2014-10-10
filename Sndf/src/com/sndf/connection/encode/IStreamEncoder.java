package com.sndf.connection.encode;

import java.nio.ByteBuffer;


public interface IStreamEncoder<T>
{
    public boolean encode(T msg, ByteBuffer toBuffer);
}
