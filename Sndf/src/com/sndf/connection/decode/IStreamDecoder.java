package com.sndf.connection.decode;


public interface IStreamDecoder<T> 
{
    public T decode(byte[] buffer, int offset, int length);
}
