package city.generics.util;

import java.io.File;
import java.io.FileInputStream;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;

public class FieldParser
{
	public static boolean parseBoolean(String value_)
	{
		try
		{
			return Boolean.parseBoolean(value_);
		} catch (Exception e_)
		{
			e_.printStackTrace();
		}
		return DEFAULT_BOOL_VALUE;
	}
	
	public static byte parseByte(String value_)
	{
		try
		{
			return Byte.parseByte(value_);
		} catch (Exception e_)
		{
			e_.printStackTrace();
		}
		return DEFAULT_BYTE_VALUE;
	}

	public static int parseInt(String value_)
	{
		try
		{
			return Integer.parseInt(value_);
		} catch (Exception e_)
		{
			e_.printStackTrace();
		}
		return DEFAULT_INTEGER_VALUE;
	}
	
	public static double parseDouble(String value_)
	{
		try
		{
			return Double.parseDouble(value_);
		} catch (Exception e_)
		{
			e_.printStackTrace();
		}
		return DEFAULT_DOUBLE_VALUE;
	}
	
	public static Texture parseTexture(String value_)
	{
		try
		{
			return TextureLoader.getTexture("PNG", new FileInputStream(
					new File("art/" + value_)));
		} catch (Exception e_)
		{
			e_.printStackTrace();
		}

		return null;
	}
	
	private static final boolean DEFAULT_BOOL_VALUE = false;
	private static final byte DEFAULT_BYTE_VALUE = 0;
	private static final int DEFAULT_INTEGER_VALUE = 0;
	private static final double DEFAULT_DOUBLE_VALUE = 0D;
}
