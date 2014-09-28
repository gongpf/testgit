package com.sndf.connection.connection;

public class DefaultConnectionFactory implements IConnectionFactory
{
    @Override
    public IConnection createConnection()
    {
        return new ConnectionImpl();
    }
}
