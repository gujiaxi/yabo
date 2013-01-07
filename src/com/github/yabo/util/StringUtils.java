package com.github.yabo.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

public class StringUtils 
{
	public static final String[] EMPTY_STRING = new String[ 0 ];
	
	public static boolean isBlank( String input ) 
	{
		if ( input == null || "".equals( input ) )
			return true;
		
		for ( int i = 0; i < input.length(); i++ ) 
		{
			char c = input.charAt( i );
			if ( c != ' ' && c != '\t' && c != '\r' && c != '\n' )
			{
				return false;
			}
		}
		return true;
	}
	
	public static String getNotNull( String input ) 
	{
		return ( input == null ? "" : input );
	}
	
	public static String getNotNull( String input, String defaultValue ) 
	{
		return ( isBlank(input) ? defaultValue : input );
	}
	
	public static String trim( String input ) 
	{
		if(isBlank(input))	return "";
		return input.trim();
	}
	
	public static String[] split( String input, String separator ) 
	{
		if ( input == null )
			return null;
		if ( input.equals( "" ) )
			return EMPTY_STRING;
		if ( separator == null || "".equals( separator ) )
			return new String[] { input };

		int cursor = 0;
		int lastPos = 0;
		ArrayList<String> list = new ArrayList<String>();

		while ( ( cursor = input.indexOf( separator, cursor ) ) != -1 ) {

			if ( cursor > lastPos ) {
				String token = input.substring( lastPos, cursor );
				list.add( token );
			}

			lastPos = cursor + separator.length();

			cursor = lastPos;
		}

		if ( lastPos < input.length() )
			list.add( input.substring( lastPos ) );

		return ( String[] ) list.toArray( new String[ list.size() ] );
	}
	
	public static String replaceString(String source, String oldstring, String newstring, boolean caseInsensive)
	{
		Matcher matcher = null;
		
		if(caseInsensive)
		{
			matcher = Pattern.compile(oldstring, Pattern.CASE_INSENSITIVE).matcher(source);
		}
		else
		{
			matcher = Pattern.compile(oldstring).matcher(source);
		}
		
		return matcher.replaceAll(newstring); 
	}	
	
	public static String trimTail( StringBuffer sb, char tail ) 
	{
		if ( sb.length() > 0 && sb.charAt( sb.length() - 1 ) == tail )
			sb.deleteCharAt( sb.length() - 1 );
		return sb.toString();
	}

	public static Integer[] splitInt( String input, String separator ) 
	{
		if ( input == null )
			return null;
		if ( input.equals( "" ) )
			return null;
		if ( separator == null || "".equals( separator ) )
			return null;

		int cursor = 0;
		int lastPos = 0;
		ArrayList<Integer> list = new ArrayList<Integer>();

		while ( ( cursor = input.indexOf( separator, cursor ) ) != -1 ) {

			if ( cursor > lastPos ) {
				int token = Integer.parseInt(input.substring( lastPos, cursor ));
				list.add( token );
			}

			lastPos = cursor + separator.length();

			cursor = lastPos;
		}

		if ( lastPos < input.length() )
			list.add( Integer.parseInt(input.substring( lastPos )) );
		
		Integer[] iStrToI = new Integer[list.size()];
		for(int i=0;i<list.size();i++){
			iStrToI[i] = Integer.parseInt(list.get(i).toString());
		}
		return iStrToI;
	}
	
	 public static String StringToString(String input)
     {
         StringBuilder sb = new StringBuilder();
         for (int i = 0; i < input.length(); i++)
         {
             char c = input.toCharArray()[i];
             switch (c)
             {
             case '\'':
            	 sb.append("\\\'");break;
                 case '\"':
                     sb.append("\\\""); break;
                 case '\\':
                     sb.append("\\\\"); break;
                 case '/':
                     sb.append("\\/"); break;
                 case '\b':
                     sb.append("\\b"); break;
                 case '\f':
                     sb.append("\\f"); break;
                 case '\n':
                     sb.append("\\n"); break;
                 case '\r':
                     sb.append("\\r"); break;
                 case '\t':
                     sb.append("\\t"); break;
                 default:
                     sb.append(c); break;
             }
         }
         return sb.toString();
     }
	 
	 public static String listToString(ArrayList<String> list,String flag){
		 String strMsg = "";
		 int listSize = list.size();
		 if(listSize>0){
			 for(int i=0;i<listSize;i++){
				 if(i==listSize-1){
					 strMsg = strMsg+list.get(i).toString();
				 }else{
					 strMsg = strMsg+list.get(i).toString()+flag;
				 }
			 }
		 }else{
			 strMsg = "";
		 }
		 return strMsg;
	 }
	 
	public static ArrayList<Object> strToList(String strList){
		
		JSONObject oJsonObj = null ;
		ArrayList<Object> listStr = new ArrayList<Object>();
		int iStriList = strList.length();
		String strDemoList = strList.substring(1,iStriList-1);
		String [] arrInfo = StringUtils.split(strDemoList, "},");
		int iArrInfo = arrInfo.length;
		for(int i=0;i<iArrInfo;i++){
			HashMap<String,String> paperMsg = new HashMap<String,String>();
			try {
				if(!arrInfo[i].endsWith("}")){
					oJsonObj = new JSONObject(arrInfo[i]+"}");
				}else{
					oJsonObj = new JSONObject(arrInfo[i]);
				}							
				Iterator<?> iter2 = oJsonObj.keys();
				while(iter2.hasNext()){
					String key = iter2.next().toString();
					paperMsg.put(key, oJsonObj.get(key).toString());
				}
				listStr.add(paperMsg);
			} catch (JSONException e) {
				
				listStr.clear();
				listStr.add(0,"Exception");
			}						
		}
		return listStr;
	}
	
	public static String toString(InputStream in) throws IOException {
		StringBuffer out = new StringBuffer();
		byte[] buffer = new byte[1024];
		for (int i; (i = in.read(buffer)) != -1;) {
			out.append(new String(buffer, 0, i));
		}
		return out.toString();
	}
	
	public static String encodeUrl(String inputUrl) {  
		
		if( isBlank(inputUrl) )	return inputUrl;
		
        char[] charArray = inputUrl.toCharArray();  
        for (int i = 0; i < charArray.length; i++) {  
            if ((charArray[i] >= 0x4e00) && (charArray[i] <= 0x9fbb)) {  
            	inputUrl = inputUrl.replaceFirst(String.valueOf(charArray[i]), URLEncoder.encode(String.valueOf(charArray[i])));
            	
            }  
        }  
        return inputUrl;  
    } 
}