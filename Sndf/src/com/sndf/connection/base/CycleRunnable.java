package com.sndf.connection.base;

import java.io.IOException;

import com.sndf.connection.connection.ConnectionDirector;
import com.sndf.connection.discovery.HostDiscovererServer;

public interface CycleRunnable extends Runnable
{
    /**
     * @see ConnectionDirector 
     * @see HostDiscovererServer 
     */
    public void doAction(int timeout) throws IOException;
    
    /** Continually updates this cycle until {@link #stop()} is called. */
    public void run ();

    /** Start a new thread that calls {@link #run()}. */
    public void start ();

    /** Close this cycle and causes {@link #run()} to return. */
    public void stop ();

    /**
     * @see ConnectionDirector 
     * @see HostDiscovererServer 
     */
    public void close ();
}
