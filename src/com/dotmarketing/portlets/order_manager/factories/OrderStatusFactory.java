package com.dotmarketing.portlets.order_manager.factories;

import com.dotmarketing.util.Config;

public class OrderStatusFactory {
	
	public static String getNameById(int id)
	{
		String returnValue = "";
		String[] parametersName = Config.getStringArrayProperty("ECOM_ORDER_STATUSES");
		for(int i = 0;i < parametersName.length;i++)
		{
			if(Config.getIntProperty(parametersName[i]) == id)
			{
				returnValue = Config.getStringProperty(parametersName[i] + "_FN");
				break;
			}
		}
		return returnValue;
	}
	
	public static int getIdByName(String name)
	{
		int returnValue = 0;
		String[] parametersName = Config.getStringArrayProperty("ECOM_ORDER_STATUSES");
		for(int i = 0;i < parametersName.length;i++)
		{
			if(Config.getStringProperty(parametersName[i]+ "_FN").equals(name))
			{
				returnValue = Config.getIntProperty(parametersName[i]);
				break;
			}
		}
		return returnValue;
	}
}
