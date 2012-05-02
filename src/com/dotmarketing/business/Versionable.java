package com.dotmarketing.business;

import java.util.Date;

public interface Versionable {

	public String getVersionId();
	public void setVersionId(String versionId);
	public String getVersionType();
	public String getInode();
	public boolean isArchived();
	public boolean isWorking();
	public boolean isLive();
	public String getTitle();
	public String getModUser();
	public Date getModDate();
	public boolean isLocked();
}
