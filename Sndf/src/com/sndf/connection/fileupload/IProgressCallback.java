package com.sndf.connection.fileupload;

public interface IProgressCallback
{
    public void updateProcess(long total, long current);
}
