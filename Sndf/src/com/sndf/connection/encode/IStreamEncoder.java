package com.sndf.connection.encode;

public interface IStreamEncoder<T>
{
    public byte[] encode(T msg);
}
