package com.sndf.connection.decode;

import java.util.List;

public interface IStreamDecoderEx<T>
{
    public List<T> decode(byte[] buffer, int offset, int length);
}
