package com.sndf.connection.serializable;

public class BytesConverteUtil 
{
	public static byte[] int2bytes(int value)
	{
		byte[] result = new byte[4];

		result[0] = (byte) (value & 0xff);

		result[1] = (byte) ((value >> 8) & 0xff);

		result[2] = (byte) ((value >> 16) & 0xff);

		result[3] = (byte) (value >>> 24);

		return result;
	}
	
	public static byte[] int2bytesOrderBy(int value)
	{
		byte[] result = new byte[4];

		result[3] = (byte) (value & 0xff);

		result[2] = (byte) ((value >> 8) & 0xff);

		result[1] = (byte) ((value >> 16) & 0xff);

		result[0] = (byte) (value >>> 24);

		return result;
	}
	
	public static byte[] float2bytes(float value)
	{
		byte[] result = new byte[4];
		int temp = Float.floatToIntBits(value);

		for (int i = 0; i < 4; i++)
		{
			result[i] = new Integer(temp).byteValue();
			temp = temp >> 8;
		}

		return result;
	}

	public static int bytes2int(byte[] bytes, int index)
	{
		if (null == bytes || bytes.length < (4 + index))
		{
			throw new RuntimeException("bytes of int error");
		}

		return (int) ((((bytes[index + 3] & 0xff) << 24) | ((bytes[index + 2] & 0xff) << 16) | ((bytes[index + 1] & 0xff) << 8) | ((bytes[index] & 0xff) << 0)));
	}

	public static float bytes2float(byte[] bytes, int index)
	{
		if (null == bytes || bytes.length < (4 + index))
		{
			throw new RuntimeException("bytes of float error");
		}

		int temp = bytes[index];
		temp &= 0xff;

		temp |= ((long) bytes[index + 1] << 8);
		temp &= 0xffff;

		temp |= ((long) bytes[index + 2] << 16);
		temp &= 0xffffff;

		temp |= ((long) bytes[index + 3] << 24);
		return Float.intBitsToFloat(temp);
	}

	public static String bytes2string(byte[] bytes, int index, int count)
	{
		byte[] result = new byte[count];
		System.arraycopy(bytes, index, result, 0, count);

		return new String(result);
	}
}
