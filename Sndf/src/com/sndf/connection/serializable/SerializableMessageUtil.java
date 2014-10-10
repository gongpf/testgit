package com.sndf.connection.serializable;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.SocketAddress;

import com.sndf.connection.message.IMessage;

public class SerializableMessageUtil
{
    /**
     * Writes an IMessage object to the bytes stream.
     * 
     * @param message
     *            the IMessage object to write to the target stream.
     */
    public static byte[] wirteMessage(IMessage message)
    {
        ByteArrayOutputStream bytesOutput = null;
        ObjectOutputStream objectOutput = null;
        byte[] result = null;

        try
        {
            bytesOutput = new ByteArrayOutputStream();
            objectOutput = new ObjectOutputStream(bytesOutput);

            objectOutput.writeObject(message);
            result = bytesOutput.toByteArray();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != objectOutput)
            {
                try
                {
                    objectOutput.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }

            if (null != bytesOutput)
            {
                try
                {
                    bytesOutput.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
        
        return result;
    }

    public static IMessage readMessage(byte[] byteArray)
    {
        return readMessage(byteArray, 0, byteArray.length);
    }

    /**
     * Reads an IMessage object from the bytes stream.
     * 
     * @param byteArray
     *            the bytes stream to read.
     */
    public static IMessage readMessage(byte[] byteArray, int index, int length)
    {
        ByteArrayInputStream input = null;
        ObjectInputStream in = null;
        IMessage message = null;

        try
        {
            input = new ByteArrayInputStream(byteArray, index, length);
            in = new ObjectInputStream(input);
            message = (IMessage) in.readObject();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if (null != in)
            {
                try
                {
                    in.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
            
            if (null != input)
            {
                try
                {
                    input.close();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }

        return message;
    }
    
    /**
     * Reads an IMessage object from the bytes stream.
     * 
     * @param byteArray
     *            the bytes stream to read.
     * @param address
     *            which the message is from.
     */
    public static IMessage readMessage(byte[] byteArray, SocketAddress address)
    {
        IMessage result = readMessage(byteArray);
        
        if (null != result)
        {
            result.setRemoteAddress(address);
        }

        return result;
    }
}
