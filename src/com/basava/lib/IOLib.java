package com.basava.lib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * @author basavar
 *
 */
public class IOLib 
{
	/**
	 * returns date and time now with default format
	 * @return
	 */
	public static String now() 
	{
		return now("yyyy/MM/dd HH:mm:ss");
	}
	
	/**
	 * Returns date and time now with the format specified
	 * @param format - example : yyyy-MM-dd HH-mm-ss
	 * @return
	 */
	public static String now(String format) 
	{
	    Calendar cal = Calendar.getInstance();
	    SimpleDateFormat sdf = new SimpleDateFormat(format);
	    return sdf.format(cal.getTime());
	}
	
	public static StringBuffer  readFileIntoBuffer(String file) throws IOException
	{
		StringBuffer buffer = new StringBuffer();
		
		BufferedReader reader = new BufferedReader(new FileReader(new File(file)));
		String line = null;
		while ( ( line = reader.readLine()) != null )
		{
			buffer.append(line);
		}
		
		return buffer;
	}

}
