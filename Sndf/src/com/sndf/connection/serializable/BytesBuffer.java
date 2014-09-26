package com.sndf.connection.serializable;


public class BytesBuffer
{
    private byte[] mBytes = null;

    /**
     * The number of bytes.
     */
    private int mSize = 0;

    /**
     * How many bytes should be added to the vector when it is detected that it
     * needs to grow to accommodate extra entries. If this value is zero or
     * negative the size will be doubled if an increase is needed.
     */
    private int mCapacityIncrement = 0;

    private static final int DEFAULT_SIZE = 25;

    /**
     * Constructs a new vector using the default capacity.
     */
    public BytesBuffer()
    {
        this(DEFAULT_SIZE);
    }

    /**
     * Constructs a new buffer using the specified capacity.
     * 
     * @param capacity
     *            the initial capacity of the new buffer.
     * @throws IllegalArgumentException
     *             if {@code capacity} is negative.
     */
    public BytesBuffer(int capacity)
    {
        this(capacity, 0);
    }

    /**
     * Constructs a new buffer using the specified capacity and capacity
     * increment.
     * 
     * @param capacity
     *            the initial capacity of the new buffer.
     * @param capacityIncrement
     *            the amount to increase the capacity when this buffer is full.
     * @throws IllegalArgumentException
     *             if {@code capacity} is negative.
     */
    public BytesBuffer(int capacity, int capacityIncrement)
    {
        if (capacity < 0)
        {
            throw new IllegalArgumentException();
        }

        mBytes = new byte[capacity];

        mSize = 0;

        mCapacityIncrement = capacityIncrement;
    }

    public byte[] getBytes()
    {
        return mBytes;
    }

    /**
     * Returns the number of bytes in buffer.
     * 
     * @return the size of this vector.
     */
    public int getLength()
    {
        return mSize;
    }

    private void capacityGrowByMin(int minAdd)
    {
        if (minAdd <= 0)
        {
            throw new RuntimeException("minAdd need more than 0");
        }

        int add = mCapacityIncrement <= 0 ? mBytes.length : mCapacityIncrement;

        add = Math.max(minAdd, add);

        byte[] bytes = new byte[mBytes.length + add];

        System.arraycopy(mBytes, 0, bytes, 0, mSize);

        mBytes = bytes;
    }

    /**
     * append int to the end of buffer.
     * 
     * @param value
     *            of Int
     */
    public void appendInt(int value)
    {
        byte[] bytes = BytesConverteUtil.int2bytes(value);
        appendBytes(bytes, 0, 4);
    }

    /**
     * append byte to the end of buffer.
     * 
     * @param value
     *            of Byte
     */
    public void appendByte(byte value)
    {
        byte[] bytes = new byte[1];
        bytes[0] = value;
        appendBytes(bytes, 0, 1);
    }

    /**
     * append string to the end of buffer.
     * 
     * @param value
     *            of String
     */
    public void appendString(String value)
    {
        byte[] bytes = value.getBytes();
        appendBytes(bytes, 0, value.length());
    }

    /**
     * append float to the end of buffer.
     * 
     * @param value
     *            of float
     */
    public void appendFloat(float value)
    {
        byte[] bytes = BytesConverteUtil.float2bytes(value);
        appendBytes(bytes, 0, 4);
    }

    /**
     * append bytes to the end of buffer.
     * 
     * @param value
     *            of byte[]
     */
    public void appendBytes(byte[] bytes, int index, int count)
    {
        if (null == bytes || (index + count) > bytes.length)
        {
            throw new RuntimeException("append bytes error");
            
        }

        if (mBytes.length < mSize + count)
        {
            capacityGrowByMin(mSize + count - mBytes.length);
        }

        System.arraycopy(bytes, index, mBytes, mSize, count);
        mSize += count;
    }

    /**
     * append bytesBuffer to the end of buffer.
     * 
     * @param value
     *            of BytesBuffer
     */
    public BytesBuffer appendBytesBuffer(BytesBuffer vector)
    {
        appendBytes(vector.getBytes(), 0, vector.getLength());

        return this;
    }

}
