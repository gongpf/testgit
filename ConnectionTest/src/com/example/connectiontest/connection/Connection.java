package com.example.connectiontest.connection;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import com.example.connectiontest.message.IMessage;
import com.example.connectiontest.serializable.BytesBuffer;
import com.example.connectiontest.serializable.SerializableMessageUtil;

/**
 * Represents a tcp connection between a Client and a Server.
 */
public class Connection
{
    private static int generationId = 0;
    private int mConnectionId = generationId++;

    private ByteBuffer mReadBuffer;
    private ByteBuffer mWriteBuffer;

    private SocketChannel mSocketChannel;
    private SelectionKey mSelectionKey;

    private boolean mIsConnected = false;

    public Connection()
    {
        mReadBuffer = ByteBuffer.allocate(1024);
        mWriteBuffer = ByteBuffer.allocate(1024);
    }

    /**
     * Returns the assigned ID.
     */
    public int getId()
    {
        return mConnectionId;
    }

    /**
     * Returns true if this connection is connected to the remote end. Note that
     * a connection can become disconnected at any time.
     */
    public boolean isConnected()
    {
        return mIsConnected;
    }

    /**
     * Connects this socket to the given remote host address and port specified
     * by the SocketAddress {@code remoteAddr} with the specified timeout. The
     * connecting method will block until the connection is established or an
     * error occurred.
     * 
     * @param remoteAddr
     *            the address and port of the remote host to connect to.
     * @param timeout
     *            the timeout value in milliseconds or {@code 0} for an infinite
     *            timeout.
     * @throws IllegalArgumentException
     *             if the given SocketAddress is invalid or not supported or the
     *             timeout value is negative.
     * @throws IOException
     *             if the socket is already connected or an error occurs while
     *             connecting.
     */
    public void connect(Selector selector, SocketAddress remoteAddress, int timeout) throws IOException
    {
        close();
        mWriteBuffer.clear();
        mReadBuffer.clear();

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
    public void onAccepted(Selector selector, SocketChannel socketChannel) throws IOException
    {
        mWriteBuffer.clear();
        mReadBuffer.clear();
        mSocketChannel = socketChannel;

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
     * 
     * @return the message actually read.
     * @throws AsynchronousCloseException
     *             if another thread closes the channel during the read.
     * @throws NotYetConnectedException
     *             if this channel is not yet connected.
     * @throws ClosedByInterruptException
     *             if another thread interrupts the calling thread while this
     *             operation is in progress. The interrupt state of the calling
     *             thread is set and the channel is closed.
     * @throws ClosedChannelException
     *             if this channel is closed.
     * @throws IOException
     *             if another I/O error occurs.
     */
    public IMessage readMessage() throws IOException
    {
        if (null == mSocketChannel)
        {
            return null;
        }

        try
        {
            ByteBuffer readBuffer = mReadBuffer;
            BytesBuffer bytesBuffer = new BytesBuffer();
            int bytesRead = mSocketChannel.read(readBuffer);
            SocketAddress address = mSocketChannel.socket().getRemoteSocketAddress();

            if (-1 == bytesRead)
            {
                throw new IOException("the connection is closed");
            }

            while (bytesRead > 0)
            {
                readBuffer.flip();

                while (readBuffer.hasRemaining())
                {
                    bytesBuffer.appendByte(readBuffer.get());
                }

                readBuffer.clear();
                bytesRead = mSocketChannel.read(readBuffer);
            }

            return SerializableMessageUtil.readMessage(bytesBuffer.getBytes(), address);
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
     * <p>
     * The call may block if other threads are also attempting to write to the
     * same channel.
     * 
     * @param msg
     *            the message to be written.
     * @throws AsynchronousCloseException
     *             if another thread closes the channel during the write.
     * @throws ClosedByInterruptException
     *             if another thread interrupts the calling thread while this
     *             operation is in progress. The interrupt state of the calling
     *             thread is set and the channel is closed.
     * @throws ClosedChannelException
     *             if the channel was already closed.
     * @throws IOException
     *             if another I/O error occurs.
     * @throws NotYetConnectedException
     *             if this channel is not connected yet.
     */
    public void sendMessage(IMessage msg)
    {
        if (null == mSocketChannel && !mIsConnected)
        {
            return;
        }

        byte[] result = SerializableMessageUtil.wirteMessage(msg);

        if (null == result)
        {
            return;
        }

        ByteBuffer writeBuffer = mWriteBuffer;
        writeBuffer.clear();
        writeBuffer.put(result);
        writeBuffer.flip();

        try
        {
            mSocketChannel.write(writeBuffer);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
