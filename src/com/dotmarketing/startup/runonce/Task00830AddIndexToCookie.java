package com.dotmarketing.startup.runonce;

import java.util.List;

import com.dotmarketing.startup.AbstractJDBCStartupTask;

public class Task00830AddIndexToCookie extends AbstractJDBCStartupTask {

	public boolean forceRun() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getPostgresScript() {
		return "CREATE INDEX clickstream_cookieid_idx ON clickstream USING btree (cookie_id);";
	}

	@Override
	public String getMySQLScript() {
		return "CREATE INDEX clickstream_cookieid_idx ON clickstream (cookie_id);";
	}

	@Override
	public String getOracleScript() {
		return "CREATE INDEX clickstream_cookieid_idx ON clickstream (cookie_id);";
	}

	@Override
	public String getMSSQLScript() {
		return "CREATE INDEX clickstream_cookieid_idx ON clickstream (cookie_id);";
	}

	@Override
	protected List<String> getTablesToDropConstraints() {
		// TODO Auto-generated method stub
		return null;
	}

}
