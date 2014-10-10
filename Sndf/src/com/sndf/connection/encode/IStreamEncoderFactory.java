package com.sndf.connection.encode;

public interface IStreamEncoderFactory<T>
{
    public IStreamEncoder<T> createSteamEncoder();
}
