package com.sndf.connection.connection;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import com.sndf.connection.message.IMessage;
import com.sndf.connection.receiver.DefaultMessageReceiverFactory;
import com.sndf.connection.receiver.IMessageReceiver;
import com.sndf.connection.receiver.IMessageReceiverFactory;
import com.sndf.connection.transmitter.DefaultMessageTransmitterFactory;
import com.sndf.connection.transmitter.IMessageTransmitter;
import com.sndf.connection.transmitter.IMessageTransmitterFactory;

/**
 * Represents a tcp connection between a Client and a Server.
 */
public class ConnectionImpl implements IConnection
{
    private static int generationId = 0;
    private int mConnectionId = generationId++;

    private final IMessageReceiverFactory mReceiverFactory;
    private IMessageReceiver mMessageReceiver;

    private final IMessageTransmitterFactory mTransmitterFactory;
    private IMessageTransmitter mMessageTransmitter;

    private SocketChannel mSocketChannel;
    private SelectionKey mSelectionKey;

    private boolean mIsConnected = false;

    public ConnectionImpl(IMessageReceiverFactory receiverFactory, IMessageTransmitterFactory transmitterFactory)
    {
        mReceiverFactory = receiverFactory;
        mTransmitterFactory = transmitterFactory;
    }
    
    public ConnectionImpl()
    {
        this(new DefaultMessageReceiverFactory(), new DefaultMessageTransmitterFactory());
    }

    @Override
    public int getId()
    {
        return mConnectionId;
    }

    @Override
    public boolean isConnected()
    {
        return mIsConnected;
    }

    /**
     * Connects this socket to the given remote host address and port specified
     * by the SocketAddress {@code remoteAddr} with the specified timeout. The
     * connecting method will block until the connection is established or an
     * error occurred.
     */
    @Override
    public void connect(Selector selector, SocketAddress remoteAddress, int timeout) throws IOException
    {
        close();

        try
        {
            SocketChannel socketChannel = selector.provider().openSocketChannel();
            Socket socket = socketChannel.socket();
            socket.setTcpNoDelay(true);
            socket.setKeepAlive(true);
            socketChannel.socket().connect(remoteAddress, timeout);
            socketChannel.configureBlocking(false);

            mSelectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
            mSelectionKey.attach(this);
            mSocketChannel = socketChannel;

            mMessageReceiver = mReceiverFactory.createMessageReceiver(socketChannel); 
            mMessageTransmitter = mTransmitterFactory.createMessageTransmitter(socketChannel); 

            mIsConnected = true;
        }
        catch (IOException e)
        {
            close();
            e.printStackTrace();
            throw e;
        }
    }

    /**
     * Accepts a connection to this server-socket channel.
     * This method just set some attributes and register OP_READ option of the new 
     * socketchannel to the selector.
     */
    @Override
    public void onAccepted(Selector selector, SocketChannel socketChannel) throws IOException
    {
        mSocketChannel = socketChannel;
        mMessageReceiver = mReceiverFactory.createMessageReceiver(socketChannel); 
        mMessageTransmitter = mTransmitterFactory.createMessageTransmitter(socketChannel);

        try
        {
            socketChannel.configureBlocking(false);
            socketChannel.socket().setTcpNoDelay(true);
            socketChannel.socket().setKeepAlive(true);

            mSelectionKey = socketChannel.register(selector, SelectionKey.OP_READ);
            mSelectionKey.attach(this);
            mIsConnected = true;
        }
        catch (IOException e)
        {
            close();
            throw e;
        }
    }

    /**
     * Closes the connection. It is not possible to reconnect or rebind to this
     * socket thereafter which means a new socket instance has to be created.
     * 
     * @throws IOException
     *             if an error occurs while closing the socket.
     */
    @Override
    public void close()
    {
        if (null != mSocketChannel)
        {
            try
            {
                mIsConnected = false;
                mSocketChannel.close();
                mSocketChannel = null;

                if (null != mSelectionKey)
                {
                    mSelectionKey.selector().wakeup();
                }
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    /**
     * Reads message from this socket channel.
     * @return the message actually read.
     */
    @Override
    public IMessage readMessage() throws IOException
    {
        if (null == mMessageReceiver)
        {
            return null;
        }

        try
        {
            return mMessageReceiver.receiveMessage();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            close();
            throw e;
        }
    }
    
    /**
     * Writes message to this socket channel. The
     * The call may block if other threads are also attempting to write to the
     * same channel.
     * @param msg the message to be written.
    */
    @Override
    public void sendMessage(IMessage msg)
    {
        if (null == mSocketChannel && !mIsConnected)
        {
            return;
        }
        
        if (null != mMessageTransmitter)
        {
            mMessageTransmitter.sendMessage(msg);
        }
    }
}
