package com.dotmarketing.util;

import java.io.StringWriter;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

import com.dotmarketing.exception.DotRuntimeException;
import com.liferay.util.SystemProperties;

public class VelocityUtil {

	private static VelocityEngine ve = null;
	private static String dotResourceLoaderClassName = null;
	
	private synchronized static void init(){
		if(ve != null)
			return;
		ve = new VelocityEngine();
		try{
			ve.init(SystemProperties.getProperties());
			dotResourceLoaderClassName = SystemProperties.get(SystemProperties.get("resource.loader") + ".resource.loader.class");
			Logger.info(VelocityUtil.class, SystemProperties.getProperties().toString());
		}catch (Exception e) {
			Logger.error(VelocityUtil.class,e.getMessage(),e);
		}
	}
	
	public static VelocityEngine getEngine(){
		if(ve == null){
			init();
			if(ve == null){
				Logger.fatal(VelocityUtil.class,"Velocity Engine unable to initialize : THIS SHOULD NEVER HAPPEN");
				throw new DotRuntimeException("Velocity Engine unable to initialize : THIS SHOULD NEVER HAPPEN");
			}
		}
		return ve;
	}
	/**
	 * Changes $ and # to velocity escapes.  This is helps filter out velocity code injections.
	 * @param s 
	 * @return
	 */
	public static String cleanVelocity(String s) {
		if (s==null) {
			return null;
		}
		s=s.replace("$", "${esc.dollar}");
		s=s.replace("#", "${esc.hash}");
		return s;
	}

	public static String getDotResourceLoaderClassName() {
		if(dotResourceLoaderClassName == null){
			init();
			if(dotResourceLoaderClassName == null){
				Logger.fatal(VelocityUtil.class,"Velocity Engine unable to initialize : THIS SHOULD NEVER HAPPEN");
				throw new DotRuntimeException("Velocity Engine unable to initialize : THIS SHOULD NEVER HAPPEN");
			}
		}
		return dotResourceLoaderClassName;
	}
	
	public String parseVelocity(String velocityCode, Context ctx){
		VelocityEngine ve = VelocityUtil.getEngine();
		StringWriter stringWriter = new StringWriter();
		try {
		   ve.evaluate(ctx, stringWriter, "VelocityUtil:parseVelocity", velocityCode);
		}catch (Exception e) {
		Logger.error(this,e.getMessage(),e);
		}
		return stringWriter.toString(); 
		
	}

	public static String convertToVelocityVariable(String variable) {
		return convertToVelocityVariable(variable, false);
	}
	
	public static String convertToVelocityVariable(String variable, boolean firstLetterUppercase){
		
		Boolean upperCase = firstLetterUppercase;
		String velocityvar = "";
		String re = "[^a-zA-Z0-9]+";
		
		for(int i=0;i < variable.length() ; i++){
			Character c = variable.charAt(i);
			if(upperCase){
				c=Character.toUpperCase(c);
			}
			else{
				c=Character.toLowerCase(c);
			}
			if(c == ' '){
				upperCase = true;
			}
			else{
				upperCase = false;
				velocityvar+=c;
			}
		}
		velocityvar = velocityvar.replaceAll(re, "");
		return velocityvar; 
		
	}
	
	
	public static Boolean isNotAllowedVelocityVariableName(String variable){
		

		String [] notallwdvelvars={"inode","type", "modDate", "owner", "ownerCanRead", "ownerCanWrite", "ownerCanPublish",
				"modUser", "working", "live", "deleted", "locked","structureInode", "languageId", "permissions",
				"identifier", "conHost", "conFolder", "Host", "folder"}; 
		Boolean found=false;
		for(String notallowed : notallwdvelvars){
			 if(variable.equalsIgnoreCase(notallowed)){
				 found=true;
			 }
			
		}
		return found;
	}


	
	
}
