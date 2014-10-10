package com.sndf.connection.base;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public abstract class AbstractCycleRunnable implements CycleRunnable
{
    private static final int STATE_IDLE = 1;
    private static final int STATE_RUNNING = 2;
    private static final int STATE_STOPED = 3;
    private int mState = STATE_IDLE;

    private static Executor mDefaultExecutor = Executors.newFixedThreadPool(10);
    
    @Override
    public void run()
    {
        while (STATE_RUNNING == mState)
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
        switch (mState)
        {
            case STATE_IDLE:
            case STATE_STOPED:
                {
                    mDefaultExecutor.execute(this);
                    mState = STATE_RUNNING;
                }
                break;
                
            case STATE_RUNNING:
                throw new RuntimeException("the runnable is running");
            
            default:
                break;
        }
    }

    @Override
    public void stop()
    {
        switch (mState)
        {
            case STATE_IDLE:
            case STATE_STOPED:
                break;
                
            case STATE_RUNNING:
                {
                    close();
                    mState = STATE_STOPED;
                }
                break;
            
            default:
                break;
        }
    }
    
    @Override
    public void close()
    {
    }
}
