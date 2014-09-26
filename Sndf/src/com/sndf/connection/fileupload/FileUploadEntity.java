package com.sndf.connection.fileupload;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.http.entity.FileEntity;

public class FileUploadEntity extends FileEntity
{
    private long mFileSize;
    private long mUploadedSize = 0;
    private IProgressCallback mProgressCallback;

    public FileUploadEntity(File file, String contentType)
    {
        super(file, contentType);
        mFileSize = file.length();
    }
    
    public void setProgressCallBack(IProgressCallback callback)
    {
        mProgressCallback = callback;
    }

    /**
     * Writes the entity content to the output stream.  
     * @param outstream the output stream to write entity content to
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void writeTo(OutputStream outStream) throws IOException
    {
        if (outStream == null)
        {
            throw new IllegalArgumentException("Output stream may not be null");
        }

        BufferedInputStream inStream = null;
        try
        {
            inStream = new BufferedInputStream(new FileInputStream(this.file));
            byte[] tmp = new byte[4096];
            int len = 0;

            while ((len = inStream.read(tmp)) != -1)
            {
                outStream.write(tmp, 0, len);
                mUploadedSize += len;

                if (mProgressCallback != null)
                {
                    mProgressCallback.updateProcess(mFileSize, mUploadedSize);
                }
            }
            outStream.flush();
        }
        finally
        {
            inStream.close();
        }
    }
}