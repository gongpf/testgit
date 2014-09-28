package com.sndf.connection.connection;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.sndf.connection.base.Debug;
import com.sndf.connection.message.IMessage;
import com.sndf.connection.serializable.BytesBuffer;
import com.sndf.connection.serializable.SerializableMessageUtil;

public class DefaultMessageDecoder extends AbstractMessageDecoder 
{
    private final BytesBuffer mTempBuffer;
    private int mCureentLength = 0;

    public DefaultMessageDecoder(SocketChannel socketChannel)
    {
        super(socketChannel);
        mTempBuffer = new BytesBuffer();
    }
    
    /**
     * 
     * @throws IOException
     */
    private void readSocketBuffer() throws IOException
    {
        final BytesBuffer bytesBuffer = mTempBuffer;
        final SocketChannel socketChannel = mSocketChannel;

        mSocketBuffer.clear();
        int bytesRead = socketChannel.read(mSocketBuffer);

        if (bytesRead > 0) 
        {
            mSocketBuffer.flip();

            while (mSocketBuffer.hasRemaining())
            {
                bytesBuffer.appendByte(mSocketBuffer.get());
            }

            readSocketBuffer();
        }
        else if(0 == bytesRead)
        {
            return ;
        }
        else
        {
            throw new IOException("the connection is closed");
        }
    }

    @Override
    public IMessage readMessage() throws IOException
    {
        readSocketBuffer();

        if (mCureentLength <= 0 && readLength() <= 0)
        {
            return null;
        }

        byte[] bytes = readValue();

        if (null == bytes)
        {
            return null;
        }

        return SerializableMessageUtil.readMessage(bytes);
    }

    /**
     * 
     * @return
     */
    private int readLength()
    {
        if (mCureentLength > 0)
        {
            throw new RuntimeException("Already read the length");
        }

        if (mTempBuffer.getLength() < 4)
        {
            return 0;
        }

        mCureentLength = mTempBuffer.readInt(0);
        mTempBuffer.cutBytes(0, 4);
        
        Debug.e("decode", "len:" + mCureentLength);

        return mCureentLength;
    }

    /**
     * 
     * @return
     */
    private byte[] readValue()
    {
        if (mCureentLength <= 0)
        {
            throw new RuntimeException("Don't read the length");
        }

        if (mCureentLength > mTempBuffer.getLength())
        {
            return null;
        }

        byte[] result = mTempBuffer.cutBytes(0, mCureentLength);
        mCureentLength = 0;

        return result;
    }
}
