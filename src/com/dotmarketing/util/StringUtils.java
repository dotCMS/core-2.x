package com.dotmarketing.util;

public class StringUtils {
	public static String formatPhoneNumber(String phoneNumber){
		try{
			String s = phoneNumber.replaceAll("\\(|\\)|:|-|\\.", "");;
			s = s.replaceAll("(\\d{3})(\\d{3})(\\d{4})(\\d{3})*", "($1) $2-$3x$4");
			
			if (s.endsWith("x"))
				s = s.substring(0,s.length()-1);
			return s;
		}
		catch(Exception ex){
			return "";
		}
	}
}
