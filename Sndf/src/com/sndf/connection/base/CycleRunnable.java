package com.sndf.connection.base;

import java.io.IOException;

public interface CycleRunnable extends Runnable
{
    public void doAction(int timeout) throws IOException;
    
    /** Start a new thread that calls {@link #run()}. */
    public void start ();

    /** Close this cycle and causes {@link #run()} to return. */
    public void stop ();

    public void close ();
}
