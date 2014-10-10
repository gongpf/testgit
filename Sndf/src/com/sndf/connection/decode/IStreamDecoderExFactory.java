package com.sndf.connection.decode;


public interface IStreamDecoderExFactory<T>
{
    public IStreamDecoderEx<T> createStreamDecoder();
}
