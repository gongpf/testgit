package com.sndf.connection.connection;

import com.sndf.connection.message.IMessage;
import com.sndf.connection.serializable.BytesBuffer;
import com.sndf.connection.serializable.SerializableMessageUtil;

public class MessageDecoder 
{
	private BytesBuffer mTempBuffer;
	
	private int mCureentLength = 0;
	
	public MessageDecoder(BytesBuffer buffer) 
	{
		mTempBuffer = buffer;
	}
	
	public IMessage readMessage()
	{
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
		return mCureentLength;
	}
	
	private byte[] readValue()
	{
		if (mCureentLength <= 0)
		{
			throw new RuntimeException("Don't read the length");
		}
		
		if (mCureentLength < mTempBuffer.getLength())
		{
			return null;
		}
		
		return mTempBuffer.cutBytes(0, mCureentLength);
	}

	public BytesBuffer getBytesBuffer() 
	{
		return mTempBuffer;
	}
}
