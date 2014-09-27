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
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream out = null;

        try
        {
            out = new ObjectOutputStream(output);
            out.writeObject(message);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }

        return output.toByteArray();
    }

    /**
     * Reads an IMessage object from the bytes stream.
     * 
     * @param byteArray
     *            the bytes stream to read.
     */
    public static IMessage readMessage(byte[] byteArray)
    {
        ByteArrayInputStream input = new ByteArrayInputStream(byteArray);
        ObjectInputStream in;
        IMessage message = null;

        try
        {
            in = new ObjectInputStream(input);
            message = (IMessage) in.readObject();
        }
        catch (Exception e)
        {
            e.printStackTrace();
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
        ByteArrayInputStream input = new ByteArrayInputStream(byteArray);
        ObjectInputStream in;
        IMessage message = null;

        try
        {
            in = new ObjectInputStream(input);
            message = (IMessage) in.readObject();
            message.setRemoteAddress(address);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return message;
    }
}
