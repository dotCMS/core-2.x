package com.dotmarketing.util;

import java.util.ArrayList;
import java.util.List;

import com.dotmarketing.startup.runalways.Task00001LoadSchema;
import com.dotmarketing.startup.runalways.Task00003CreateSystemRoles;
import com.dotmarketing.startup.runalways.Task00004LoadStarter;
import com.dotmarketing.startup.runalways.Task00005LoadFixassets;
import com.dotmarketing.startup.runalways.Task00006CreateSystemLayout;
import com.dotmarketing.startup.runalways.Task00007RemoveSitesearchQuartzJob;
import com.dotmarketing.startup.runonce.Task00760AddContentletStructureInodeIndex;
import com.dotmarketing.startup.runonce.Task00765AddUserForeignKeys;
import com.dotmarketing.startup.runonce.Task00766AddFieldVariableTable;
import com.dotmarketing.startup.runonce.Task00767FieldVariableValueTypeChange;
import com.dotmarketing.startup.runonce.Task00768CreateTagStorageFieldOnHostStructure;
import com.dotmarketing.startup.runonce.Task00769UpdateTagDataModel;

public class TaskLocatorUtil {

	public static List<Class<?>> getFixTaskClasses() {
		List<Class<?>> ret = new ArrayList<Class<?>>();
			
		return ret;
	}
	
	public static List<Class<?>> getStartupRunOnceTaskClasses() {
		List<Class<?>> ret = new ArrayList<Class<?>>();
		ret.add(Task00760AddContentletStructureInodeIndex.class);	
		ret.add(Task00765AddUserForeignKeys.class);	
		ret.add(Task00766AddFieldVariableTable.class);
		ret.add(Task00767FieldVariableValueTypeChange.class);
		ret.add(Task00768CreateTagStorageFieldOnHostStructure.class);
		ret.add(Task00769UpdateTagDataModel.class);
		return ret;
	}
	
	public static List<Class<?>> getStartupRunAlwaysTaskClasses() {
		List<Class<?>> ret = new ArrayList<Class<?>>();
		ret.add(Task00001LoadSchema.class);
		ret.add(Task00003CreateSystemRoles.class);
		ret.add(Task00004LoadStarter.class);
		ret.add(Task00005LoadFixassets.class);
		ret.add(Task00006CreateSystemLayout.class);
		ret.add(Task00007RemoveSitesearchQuartzJob.class);
		return ret;
	}
}
