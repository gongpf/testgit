package com.sndf.connection.connection;

import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.ClosedByInterruptException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.List;

import com.sndf.connection.message.IMessage;

/**
 * Represents a tcp connection between a Client and a Server.
 */
public interface IConnection
{
    /**
     * Returns the assigned ID.
     */
    public int getId();
    
    /**
     * Returns true if this connection is connected to the remote end. Note that
     * a connection can become disconnected at any time.
     */
    public boolean isConnected();

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
    public void connect(Selector selector, SocketAddress remoteAddress, int timeout) throws IOException;

    /**
     * Accepts a connection to this server-socket channel.
     * This method just set some attributes and register OP_READ option of the new 
     * socketchannel to the selector.
     */
    public void onAccepted(Selector selector, SocketChannel socketChannel) throws IOException;

    /**
     * Closes the connection. It is not possible to reconnect or rebind to this
     * socket thereafter which means a new socket instance has to be created.
     * 
     * @throws IOException
     *             if an error occurs while closing the socket.
     */
    public void close();

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
    public List<IMessage> readMessage() throws IOException;

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
    public void sendMessage(IMessage msg);
    
    /**
     * Writes the buffer to this socket channel. The
     * <p>
     * The call may block if other threads are also attempting to write to the
     * same channel.
     */
    public void write() throws IOException;
}