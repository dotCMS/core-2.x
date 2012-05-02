package com.dotmarketing.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.Query;
import net.sf.hibernate.Session;

import com.dotmarketing.exception.DotRuntimeException;
import com.dotmarketing.util.Logger;

/**
 * 
 * @author will & david (2005)
 */
public class DotHibernate {

	private Class thisClass;

	private Query query;

	private int maxResults;

	private int firstResult;

	private int t;

	private static final boolean useCache = true;

	public DotHibernate(Class c) {
		setClass(c);
	}

	public DotHibernate() {
	}

	public void setClass(Class c) {
		thisClass = c;
	}

	public static String getTableName(Class c) {

		return HibernateUtil.getTableName(c);
	}

	public int getCount() {
		getSession();
		int i = 0;

		try {
			if (maxResults > 0) {
				query.setMaxResults(maxResults);
			}
			if (firstResult > 0) {
				query.setFirstResult(firstResult);
			}

			i = ((Integer) query.list().iterator().next()).intValue();
		} catch (Exception e) {
			Logger.error(this, "---------- DotHibernate: error on list ---------------", e);
			handleSessionException();
			// throw new DotRuntimeException(e.toString());
		}

		return i;
	}

	public void setFirstResult(int firstResult) {
		this.firstResult = firstResult;
	}

	public void setMaxResults(int g) {
		this.maxResults = g;
	}

	public void setParam(long g) {
		query.setLong(t, g);
		t++;
	}
	
	public void setParam(Long g) {
		query.setLong(t, g);
		t++;
	}

	public void setParam(String g) {
		query.setString(t, g);
		t++;
	}

	public void setParam(int g) {
		query.setInteger(t, g);
		t++;
	}
	
	public void setParam(Integer g) {
		query.setInteger(t, g);
		t++;
	}

	public void setParam(java.util.Date g) {
		query.setTimestamp(t, g);
		t++;
	}

	public void setParam(boolean g) {
		query.setBoolean(t, g);
		t++;
	}
	
	public void setParam(Boolean g) {
		query.setBoolean(t, g);
		t++;
	}

	public void setParam(double g) {
		query.setDouble(t, g);
		t++;
	}
	
	public void setParam(Double g) {
		query.setDouble(t, g);
		t++;
	}

	public void setParam(float g) {
		query.setFloat(t, g);
		t++;
	}
	
	public void setParam(Float g) {
		query.setFloat(t, g);
		t++;
	}

	public void setParam(Object g) {
		query.setEntity(t, g);
		t++;
	}

	public void setQuery(String x) {
		Session session = getSession();

		try {
			query = session.createQuery(x);
			query.setCacheable(useCache);
		} catch (Exception e) {
			Logger.error(this, "---------- DotHibernate: error setting query ---------------", e);
			handleSessionException();
			// throw new DotRuntimeException(e.toString());
		}
	}

	public void setSQLQuery(String x) {
		Session session = getSession();

		try {
			query = session.createSQLQuery(x, getTableName(thisClass), thisClass);
			query.setCacheable(useCache);
		} catch (Exception e) {
			Logger.error(this, "---------- DotHibernate: error setting query ---------------", e);
			handleSessionException();
			// throw new DotRuntimeException(e.toString());
		}
	}

	
	public void setSQLQueryWithMultipleTables(String SQL, Class[] joinClasses, String[] arg) {
		Session session = getSession();
		try {
			query = session.createSQLQuery(SQL,arg,joinClasses);
			query.setCacheable(false);
			Logger.info(this, "Query: " + query.getQueryString());
		} catch (Exception e) {
			Logger.warn(this, "---------- DotHibernate: error setting query ---------------");
			Logger.error(this,e.getMessage(),e);
		}
	}
	
	/*
	 * hibernate delete object
	 */
	public static boolean delete(Object obj) {
		Session session = getSession();

		try {
			session.delete(obj);
			session.flush();
			
			return true;
		} catch (Exception e) {
			Logger.error(DotHibernate.class, "---------- DotHibernate: error on delete ---------------", e);
			handleSessionException();
			
			return false;
			// throw new DotRuntimeException(e.toString());
		}
	}

	/*
	 * hibernate delete object
	 */
	public static boolean delete(String sql) {
		Session session = getSession();

		try {
			session.delete(sql);
			
			return true;
		} catch (Exception e) {
			Logger.error(DotHibernate.class, "---------- DotHibernate: error on delete ---------------", e);
			handleSessionException();
			
			return false;
			// throw new DotRuntimeException(e.toString());
		}
	}

	public static java.util.List find(String x) {
		Session session = getSession();

		try {
			return (ArrayList) session.find(x);
		} catch (Exception e) {
			Logger.error(DotHibernate.class, "---------- DotHibernate: error on find ---------------", e);
			handleSessionException();
			return new java.util.ArrayList();
		}
	}

    public static Object load(Class c, Serializable key) {
        Session session = getSession();

        try {
            return (Object) session.load(c, key);
        } catch (Exception e) {
            Logger.warn(DotHibernate.class, "---------- DotHibernate: error on load ---------------", e);
            handleSessionException();
            try {
                return c.newInstance();
            } catch (Exception e1) {
                Logger.error(DotHibernate.class, "---------- DotHibernate: error on load ---------------", e1);
                return null;
            }
        }
    }

	/*
	 * hibernate RecipientList object
	 */
	public List list() {
		getSession();
		try {
			if (maxResults > 0) {
				query.setMaxResults(maxResults);
			}
			if (firstResult > 0) {
				query.setFirstResult(firstResult);
			}

			long before = System.currentTimeMillis();
			java.util.List l = query.list();
			long after = System.currentTimeMillis();
			if(((after - before) / 1000) > 20) {
				String[] paramsA = query.getNamedParameters();
				String params = "";
				for(String s : paramsA)
					params = s + ", ";
				Logger.warn(this, "Too slow query sql: " + query.getQueryString() + " " + params);
			}
			return l;
			
		} catch (Exception e) {
			Logger.warn(this, "---------- DotHibernate: error on list ---------------", e);
			/*Ozzy i comment this because see DOTCMS-206. it have nonsence to make a rollback 
			 * when we are doing a search and the object is not found. this make some other operation
			 * to rollback when this is not required
			 **/
			//handleSessionException();
			// throw new DotRuntimeException(e.toString());
			return new java.util.ArrayList();
		}
	}

	public Object load(long id) {
		Session session = getSession();

		if (id == 0) {
			try {
				return thisClass.newInstance();
			} catch (Exception e) {
				throw new DotRuntimeException(e.toString());
			}
		}

		try {
			return session.load(thisClass, new Long(id));
		} catch (Exception e) {
			Logger.debug(this, "---------- DotHibernate: error on load ---------------", e);
			/*Ozzy i comment this because see DOTCMS-206. it have nonsence to make a rollback 
			 * when we are doing a search and the object is not found. this make some other operation
			 * to rollback when this is not required
			 **/
			//handleSessionException();

			// if no object is found in db, return an new Object
			try {
				return thisClass.newInstance();
			} catch (Exception ex) {
				throw new DotRuntimeException(e.toString());
			}
		}
		// return new Object();
	}

	public Object load(String id) {
		Session session = getSession();

		if (id == null) {
			try {
				return thisClass.newInstance();
			} catch (Exception e) {
				throw new DotRuntimeException(e.toString());
			}
		}

		try {
			return session.load(thisClass, id);
		} catch (Exception e) {
			Logger.debug(this, "---------- DotHibernate: error on load ---------------", e);
			
			/*Ozzy i comment this because see DOTCMS-206. it have nonsence to make a rollback 
			 * when we are doing a search and the object is not found. this make some other operation
			 * to rollback when this is not required
			 **/
			//handleSessionException();

			// if no object is found in db, return an new Object
			try {
				return thisClass.newInstance();
			} catch (Exception ex) {
				throw new DotRuntimeException(e.toString());
			}
		}

		// return new Object();
	}

	public Object load() {
		getSession();
		ArrayList l = new java.util.ArrayList();
		Object obj = new Object();

		try {
			if (maxResults > 0) {
				query.setMaxResults(maxResults);
			}

			l = (java.util.ArrayList) query.list();
			obj = l.get(0);
			query = null;
		} catch (java.lang.IndexOutOfBoundsException iob) {
			// if no object is found in db, return an new Object
			try {
				obj = thisClass.newInstance();
			} catch (Exception ex) {
				Logger.error(this, query.getQueryString(), ex);
				throw new DotRuntimeException(ex.toString());
			}
		} catch (Exception e) {
			Logger.warn(this, "---------- DotHibernate: can't load- no results from query---------------", e);
			/*Ozzy i comment this because see DOTCMS-206. it have nonsence to make a rollback 
			 * when we are doing a search and the object is not found. this make some other operation
			 * to rollback when this is not required
			 **/
			//handleSessionException();

			try {
				obj = thisClass.newInstance();
			} catch (Exception ee) {
				Logger.error(this, "---------- DotHibernate: can't load- thisClass.newInstance()---------------", e);
				throw new DotRuntimeException(e.toString());
			}
		}

		return obj;
	}

	public String getQuery() {

		StringBuffer sb = new StringBuffer(this.query.getQueryString() + "\n");
		try {
			for (int i = 0; i < this.query.getNamedParameters().length; i++) {
				sb.append("param " + i + " = " + query.getNamedParameters()[i]);
			}
		} catch (Exception e) {
		}

		return sb.toString();

	}

	public static void save(Object obj) {
		Session session = getSession();

		try {
			session.save(obj);
			session.flush();
			// session.save(obj);
		} catch (Exception e) {
			Logger.error(DotHibernate.class, "---------- DotHibernate: error on save ---------------", e);
			handleSessionException();
			// throw new DotRuntimeException(e.toString());
		}
	}

	public static void saveOrUpdate(Object obj) {
		Session session = getSession();
		try {
			session.saveOrUpdate(obj);
			session.flush();
			// session.save(obj);
		} catch (Exception e) {
			Logger.error(DotHibernate.class, "---------- DotHibernate: error on update ---------------", e);
			handleSessionException();
			// throw new DotRuntimeException(e.toString());
		}
	}

	public static void update(Object obj) {
		Session session = getSession();

		try {
			session.update(obj);

			// session.save(obj);
		} catch (Exception e) {
			Logger.error(DotHibernate.class, "---------- DotHibernate: error on update ---------------", e);
			handleSessionException();
			// throw new DotRuntimeException(e.toString());
		}
	}


	/*
	 * Attempts to find a session associated with the Thread. If there isn't a
	 * session, it will create one.
	 */
	public static Session getSession() {

		try {
			return HibernateUtil.getSession();
		} catch (Exception e) {
			Logger.error(DotHibernate.class, e.getMessage(), e);
		}
		return null;
		
	}

	public static void closeSession() {
		try {
			HibernateUtil.closeSession();
		} catch (Exception e) {
			Logger.error(DotHibernate.class, e.getMessage(), e);
		}
	}

	public static void startTransaction() {

		/*
		 * Transactions are now used by default
		 * 
		 */
		try {
			getSession().connection().setAutoCommit(false);
			Logger.debug(DotHibernate.class, "Starting Transaction!");
		} catch (Exception e) {
			Logger.error(DotHibernate.class, "---------- DotHibernate: error on startTransaction ---------------", e);
			handleSessionException();
			// throw new DotRuntimeException(e.toString());
		}
	}

	public static boolean commitTransaction() {
		closeSession();
		return true;
	}

	public static void rollbackTransaction() {

		handleSessionException();

	}

	public static void flush() {
		Session session = getSession();

		try {
			session.flush();
		} catch (Exception e) {
			Logger.error(DotHibernate.class, "---------- DotHibernate: error on flush ---------------", e);
			handleSessionException();
			// throw new DotRuntimeException(e.toString());
		}
	}

	public static String getDialect() {
		try {
			return HibernateUtil.getDialect();
		} catch (Exception e) {
			Logger.error(DotHibernate.class, e.getMessage(), e);
			return null;
		}
	}
	
	private static void handleSessionException() {

		try {
			HibernateUtil.sessionCleanupAndRollback();
		} catch (Exception e) {
			Logger.error(DotHibernate.class, e.toString(), e);
		}
		
	}
	
	public static void saveWithPrimaryKey(Object obj, Serializable id) {
		Session session = getSession();

		try {
			session.save(obj, id);
			session.flush();
			// session.save(obj);
		} catch (Exception e) {
			Logger.error(DotHibernate.class, "---------- DotHibernate: error on save ---------------", e);
			handleSessionException();
			// throw new DotRuntimeException(e.toString());
		}
	}
	
    public void setDate(java.util.Date g) {
        query.setDate(t, g);
        t++;
    }
    
    public static void evict(Object obj){
        Session session = getSession();
        try {
            session.evict(obj);
        } catch (HibernateException e) {
            Logger.error(DotHibernate.class, e.toString(), e);
        }
    }
	
	

}