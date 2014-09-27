package com.sndf.connection.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;

import com.sndf.connection.base.CycleRunnable;
import com.sndf.connection.base.Debug;
import com.sndf.connection.base.DefaultExecutor;
import com.sndf.connection.message.IMessage;

@SuppressLint("HandlerLeak")
public class ConnectionDirector implements CycleRunnable
{
    private static final String TAG = "ConnectionServer";
    
    private static Executor mDefaultExecutor = Executors.newFixedThreadPool(10);

    private Selector mSelector;
    
    private final List<IConnection> mConnectionList;

    private ServerSocketChannel mServerSocketChannel;
    
    private int mEmptySelects = 0;
    
    private Listener mListener;
    
    private boolean mShutdown = false;
    
    private Object mSelectorLock = new Object();
    
    private static final int MSG_CONNECTION_CONNECTED = 1;
    private static final int MSG_CONNECTION_DISCONNECTED = 2;
    private static final int MSG_CONNECTION_CONNECT_FAILED = 3;
    private static final int MSG_CONNECTION_RECEIVED = 4;
    
    private Handler mConnectionHandler = new Handler() 
    {
        @Override
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case MSG_CONNECTION_CONNECTED:
                {
                    if (null != mListener)
                    {
                        mListener.onConnected(msg.arg1);
                    }
                }
                break;

                case MSG_CONNECTION_CONNECT_FAILED:
                {
                    if (null != mListener)
                    {
                        mListener.onConnectFailed(msg.arg1);
                    }
                }
                break;

                case MSG_CONNECTION_DISCONNECTED:
                {
                    if (null != mListener)
                    {
                        mListener.disconnected(msg.arg1);
                    }
                }
                break;

                case MSG_CONNECTION_RECEIVED:
                {
                    if (null != mListener)
                    {
                        mListener.onReceived(msg.arg1, (IMessage)msg.obj);
                    }
                }
                break;

                default:
                    break;
            }
        };
    };
    
    /**
     * Create a selector and connection list.
     */
    public ConnectionDirector()
    {
        try
        {
            mSelector = Selector.open();
        }
        catch (IOException e)
        {
            throw new RuntimeException("Error opening selector.", e);
        }
        
        mConnectionList = new ArrayList<IConnection>();
    }
    
    /**
     * Binds this server-channel to the given local socket address.Then the
     * Server-channel can accept remote connect request. 
     *
     * @param localAddr
     *            the local address and port to bind on.
     */
    public void bind(InetSocketAddress addr)
    {
        close();

        synchronized (mSelectorLock)
        {
            mSelector.wakeup();
            try
            {
                mServerSocketChannel = mSelector.provider().openServerSocketChannel();
                mServerSocketChannel.socket().bind(addr);
                mServerSocketChannel.configureBlocking(false);
                mServerSocketChannel.register(mSelector, SelectionKey.OP_ACCEPT);
            }
            catch (IOException e)
            {
                close();
            }
        }
    }
    
    /**
     * Gets the local socket address of this server socket channel or {@code null} if
     * the socket is unbound. This is useful on multihomed hosts.
     *
     * @return the local socket address and port this server socket channel is bound to.
     */
    public SocketAddress getServerAddress()
    {
        if (null != mServerSocketChannel && mServerSocketChannel.isOpen())
        {
            return mServerSocketChannel.socket().getLocalSocketAddress();
        }
        
        return null;
    }
    
    @Override
    public void run()
    {
        while(!mShutdown)
        {
            try
            {
                doAction(250);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                stop();
            }
        }
    }

    @Override
    public void start()
    {
        mDefaultExecutor.execute(this);
    }

    @Override
    public void stop()
    {
        if (mShutdown)
        {
            return ;
        }

        close();
        mShutdown = true;
    }

    /**
     * Closes and clear all connections and the server socket channel. 
     */
    @Override
    public void close()
    {
        List<IConnection> connectionList = this.mConnectionList;
        for (IConnection connection : connectionList)
        {
            connection.close();
        }
        connectionList.clear();
        
        ServerSocketChannel serverSocketChannel = this.mServerSocketChannel;
        if (null != serverSocketChannel)
        {
            try
            {
                serverSocketChannel.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            this.mServerSocketChannel = null;
        }
        
        synchronized (mSelectorLock)
        {
            mSelector.wakeup();
            
            try
            {
                mSelector.selectNow();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void doAction(int timeout) throws IOException
    {
        synchronized (mSelectorLock)
        {
        }

        int select = timeout > 0 ? mSelector.select(timeout) : mSelector.selectNow();
        long startTime = System.currentTimeMillis();
        
        if (0 == select)
        {
            handleEmptySelect(startTime);
        }
        else 
        {
            mEmptySelects = 0;
            Set<SelectionKey> keys = mSelector.selectedKeys();

            for (Iterator<SelectionKey> iterator = keys.iterator(); iterator.hasNext(); )
            {
                SelectionKey key = iterator.next();
                iterator.remove();
                handleSelectionKey(key);
            }
        }
    }
    
    public void setListener(Listener listener)
    {
        mListener = listener;
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
     */
    public int connect(final SocketAddress remoteAddress, final int timeout)
    {
        final IConnection connection = new ConnectionImpl();
        DefaultExecutor.executeInNewThread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    synchronized (mSelectorLock)
                    {
                        mSelector.wakeup();
                        connection.connect(mSelector, remoteAddress, timeout);
                    }
                    mConnectionList.add(connection);
                    Message.obtain(mConnectionHandler, MSG_CONNECTION_CONNECTED, connection.getId(), 0).sendToTarget();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    Message.obtain(mConnectionHandler, MSG_CONNECTION_CONNECT_FAILED, connection.getId(), 0).sendToTarget();
                }
            }
        });

        return connection.getId();
    }
    
    /**
     * NIO freaks and returns immediately with 0 sometimes, so try to keep from 
     * hogging the CPU. 
     * @param startTime
     */
    private void handleEmptySelect(long startTime)
    {
        if (mEmptySelects++ >= 100)
        {
            mEmptySelects = 0;
            long elapsedTime = System.currentTimeMillis() - startTime;

            try
            {
                if (elapsedTime < 25)
                {
                    Thread.sleep(25 - elapsedTime);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * The selector received a event and handle the event by selection key.
     * @param key
     */
    private void handleSelectionKey(SelectionKey key)
    {
        int ops = key.readyOps();
        ConnectionImpl connection = (ConnectionImpl)key.attachment();
        
        if ((ops & SelectionKey.OP_ACCEPT) == SelectionKey.OP_ACCEPT)
        {
            handleAcceptEvent();
        }
        
        if ((ops & SelectionKey.OP_READ) == SelectionKey.OP_READ)
        {
            handleReadEvent(connection);
        }
    }
    
    /**
     * Handle the accept event to create a new connection. 
     */
    private void handleAcceptEvent()
    {
        ServerSocketChannel serverSocketChannel = this.mServerSocketChannel;
        Selector selector = mSelector;

        if (null == serverSocketChannel)
        {
            Debug.e(TAG, "Server socketchannel is null, cann't accept new connection.");
        }

        try
        {
            SocketChannel socketChannel = serverSocketChannel.accept();
            if (null != socketChannel)
            {
                ConnectionImpl connection = new ConnectionImpl();
                connection.onAccepted(selector, socketChannel);
                Message.obtain(mConnectionHandler, MSG_CONNECTION_CONNECTED, connection.getId(), 0).sendToTarget();
                mConnectionList.add(connection);
            }
        }
        catch (IOException e)
        {
            Debug.e(TAG, "Unable to accept new connection.");
            e.printStackTrace();
        }
    }
    
    /**
     * Handle the read event, if catch a IOException remove and close
     * this connection.
     * @param connection
     */
    private void handleReadEvent(ConnectionImpl connection)
    {
        IMessage message = null;
        try
        {
            message = connection.readMessage();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            mConnectionList.remove(connection);
            Message.obtain(mConnectionHandler, MSG_CONNECTION_DISCONNECTED, connection.getId(), 0).sendToTarget();
            return ;
        }

        if (null == message)
        {
        	return ;
        }
        
		Message.obtain(mConnectionHandler, MSG_CONNECTION_RECEIVED,	connection.getId(), 0, message).sendToTarget();
		handleReadEvent(connection);
    }
    
    /**
     * Send message to all connections.
     * @param msg the message to send.
     */
    public void sendMessageToAll(final IMessage msg)
    {
        List<IConnection> connectionList = this.mConnectionList;
        
        for (IConnection connection : connectionList)
        {
            sendMessage(connection, msg);
        }
    }
    
    /**
     * Send message to special connection.
     * @param connection the connection to send.
     * @param msg the message to send.
     */
    private void sendMessage(final IConnection connection, final IMessage msg)
    {
        if (null != connection)
        {
            Runnable runnable = new Runnable()
            {
                @Override
                public void run()
                {
                    connection.sendMessage(msg);
                }
            };
            
            DefaultExecutor.executeInNonMainThread(runnable);
        }
    }

    /**
     * Send message to special connection by connection id.
     * @param connectionId the connection id to send.
     * @param msg the message to send.
     */
    public void sendMessage(int connectionId, final IMessage msg)
    {
        final IConnection connection = findConnection(connectionId);
        sendMessage(connection, msg);
    }
    
    /**
     * Closes the special connection by connection id.
     * @param connectionId the connection to close.
     */
    public void closeConnection(int connectionId)
    {
        IConnection connection = findConnection(connectionId);

        if (null != connection)
        {
            connection.close();
            mConnectionList.remove(connection);
        }
    }
    
    /**
     * Closes all connections and close the selector, can't reused if destoryed. 
     */
    public void destory()
    {
        close();

        try
        {
            mSelector.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Return the count of current connections.
     * @return
     */
    public int getConnectionCount()
    {
        return mConnectionList.size();
    }
    
    /**
     * Find the connection by conneciton id.
     * @param connectionId the target connection id.
     * @return the target connection.
     */
    private IConnection findConnection(int connectionId)
    {
        List<IConnection> connectionList = this.mConnectionList;
        
        for (IConnection connection : connectionList)
        {
            if (connectionId == connection.getId())
            {
                return connection;
            }
        }
        
        return null;
    }
}
