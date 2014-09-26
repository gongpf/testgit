package com.sndf.connection.base;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import android.os.Looper;

public class DefaultExecutor
{
    private static Executor mDefaultExecutor = Executors.newFixedThreadPool(10);
    
    /**
     * Execute the runnable in non-main thread.
     * @param runnable the runnable to execute.
     */
    public static void executeInNonMainThread(Runnable runnable)
    {
        if (Looper.myLooper() != Looper.getMainLooper())
        {
            runnable.run();
        }
        else 
        {
            mDefaultExecutor.execute(runnable);
        }
    }
    
    /**
     * Execute the runnable in new thread.
     * @param runnable the runnable to execute.
     */
    public static void executeInNewThread(Runnable runnable)
    {
        mDefaultExecutor.execute(runnable);
    }
}
