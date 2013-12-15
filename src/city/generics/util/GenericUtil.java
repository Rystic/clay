package city.generics.util;

public class GenericUtil
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
	
	private static final boolean DEFAULT_BOOL_VALUE = false;
	private static final int DEFAULT_INTEGER_VALUE = 0;
	private static final double DEFAULT_DOUBLE_VALUE = 0D;
}
