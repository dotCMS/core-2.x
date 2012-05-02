package com.dotmarketing.viewtools;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.velocity.tools.view.context.ViewContext;
import org.apache.velocity.tools.view.tools.ViewTool;

import com.dotmarketing.beans.Identifier;
import com.dotmarketing.business.APILocator;
import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.exception.DotDataException;
import com.dotmarketing.exception.DotSecurityException;
import com.dotmarketing.factories.IdentifierFactory;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.categories.business.CategoryAPI;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.entities.factories.EntityFactory;
import com.dotmarketing.portlets.entities.model.Entity;
import com.dotmarketing.portlets.events.factories.EventFactory;
import com.dotmarketing.portlets.events.model.Event;
import com.dotmarketing.portlets.events.model.Recurance;
import com.dotmarketing.portlets.facilities.model.Facility;
import com.dotmarketing.portlets.files.model.File;
import com.dotmarketing.util.Calendar;
import com.dotmarketing.util.InodeUtils;
import com.dotmarketing.util.Logger;
import com.dotmarketing.util.UtilHTML;
import com.dotmarketing.util.UtilMethods;
import com.liferay.portal.model.User;

public class EventsWebAPI implements ViewTool {

	private CategoryAPI categoryAPI = APILocator.getCategoryAPI();

	public CategoryAPI getCategoryAPI() {
		return categoryAPI;
	}

	public void setCategoryAPI(CategoryAPI categoryAPI) {
		this.categoryAPI = categoryAPI;
	}

    private HttpServletRequest request;
    
    public void init(Object obj) {
        ViewContext context = (ViewContext) obj;
        this.request = context.getRequest();
    }

    //Single event methods
    @Deprecated
    public Event getEvent(long eventInode) {
       return getEvent(String.valueOf(eventInode));
    }

    public Event getEvent(String eventInode) {

        Event ret = (Event) InodeFactory.getInode(eventInode, Event.class);
        return ret;
    }

    public List getEventAttachments(Event event) {

        java.util.List _files = InodeFactory.getChildrenClass(event, Identifier.class);
        ArrayList ret = new ArrayList();
        Iterator it = _files.iterator();
        while (it.hasNext()) {
            Identifier identifier = (Identifier) it.next();
            File file = (File) IdentifierFactory.getWorkingChildOfClass(identifier, File.class);
            ret.add(file);
        }
        return ret;
    }

    public Recurance getEventRecurance(Event ev) {
        if (ev != null)
            return (Recurance) InodeFactory.getChildOfClass(ev, Recurance.class);
        else
            return null;
    }

    public String recuranceToString(Recurance r) {
        return com.dotmarketing.util.UtilHTML.recuranceToString(r);
    }

    public File getEventImage(String eventInode) {

        Event event = (Event) InodeFactory.getInode(eventInode, Event.class);
        return getEventImage(event);

    }

    public File getEventImage(Event event) {
        File file = null;

        if (InodeUtils.isSet(event.getInode())) {
            List identifiers = InodeFactory.getChildrenClass(event, Identifier.class);
            Iterator it = identifiers.iterator();
            while (it.hasNext()) {
                Identifier identifier = (Identifier) it.next();
                file = (File) IdentifierFactory.getWorkingChildOfClass(identifier, File.class);
                if (UtilMethods.isImage(file.getFileName()))
                    return file;
            }
        }

        file = new File();

        return file;
    }

    public HashMap getEventCategories(Event ev) {

        HashMap ret = new HashMap();

        Entity entity = EntityFactory.getEntity("Event");
        List categories = EntityFactory.getEntityCategories(entity);
        Iterator catsIter = categories.iterator();

        while (catsIter.hasNext()) {

            Category cat = (Category) catsIter.next();
            List cats = InodeFactory.getChildrenClass(cat, Category.class);
            List eventCats = InodeFactory.getParentsOfClass(ev, Category.class);
            ArrayList list = new ArrayList();
            Iterator it = eventCats.iterator();
            while (it.hasNext()) {
                Category catTemp = (Category) it.next();
                if (cats.contains(catTemp)) {
                    list.add(catTemp);
                }
            }
            ret.put(cat.getCategoryName(), list);
        }
        return ret;
    }


    //Events pulling methods
    public List getEventsList(Date from, Date to, String keywords, String[] categories, int maxEvents) {

        java.util.Calendar fromCal = new GregorianCalendar();
        fromCal.setTime(from);
        java.util.Calendar toCal = new GregorianCalendar();
        toCal.setTime(to);
        fromCal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        fromCal.set(java.util.Calendar.MINUTE, 0);
        fromCal.set(java.util.Calendar.SECOND, 0);
        toCal.set(java.util.Calendar.HOUR_OF_DAY, 23);
        toCal.set(java.util.Calendar.MINUTE, 59);
        toCal.set(java.util.Calendar.SECOND, 59);

        return EventFactory.getPublicEvents(fromCal.getTime(), toCal.getTime(), keywords, categories, maxEvents);

    }

//  Events pulling methods
    public List getEventsListWithOrCategories(Date from, Date to, String keywords, String[] categories, int maxEvents) {

        java.util.Calendar fromCal = new GregorianCalendar();
        fromCal.setTime(from);
        java.util.Calendar toCal = new GregorianCalendar();
        toCal.setTime(to);
        fromCal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        fromCal.set(java.util.Calendar.MINUTE, 0);
        fromCal.set(java.util.Calendar.SECOND, 0);
        toCal.set(java.util.Calendar.HOUR_OF_DAY, 23);
        toCal.set(java.util.Calendar.MINUTE, 59);
        toCal.set(java.util.Calendar.SECOND, 59);

        return EventFactory.getPublicEventsWithOrCategories(fromCal.getTime(), toCal.getTime(), keywords, categories, maxEvents);

    }

    //Utility Methods
    public Date getTodayDate() {
        return new Date ();
    }

    public String getTodayDateString() {
        return UtilMethods.dateToHTMLDate(new Date ());
    }

    public java.util.Calendar getCalendar() {
        return new GregorianCalendar();
    }

    /**
     * Add x number of days in the calendar argument
     * @param cal calendar where is gonna be added the dates
     * @param days number of days to add
     * @return s calendar object
     */
    public java.util.Calendar addCalendarDate(java.util.Calendar cal, int days) {
        cal.add(GregorianCalendar.DATE, days);
        return cal;
    }

    public List getEventsCategories() {
        ArrayList retList = new ArrayList();

        Entity entity = EntityFactory.getEntity("Event");
        List categories = EntityFactory.getEntityCategories(entity);
        Iterator catsIter = categories.iterator();

        while (catsIter.hasNext()) {
            Category cat = (Category) catsIter.next();
            List cats = InodeFactory.getChildrenClass(cat, Category.class);
            retList.add(cats);
        }
        return retList;

    }

    public String getEventsCategoriesList(String inode) throws DotDataException, DotSecurityException {
		User user = null;
        try {
            if (request.getSession() != null)
                user = (com.liferay.portal.model.User) request.getSession().getAttribute(com.dotmarketing.util.WebKeys.CMS_USER);
        } catch (Exception nsue) {
            Logger.warn(this, "Exception trying to getUser: " + nsue.getMessage(), nsue);
        }
        String retList = "";
        Entity entity = EntityFactory.getEntity("Event");
        List categories = EntityFactory.getEntityCategories(entity);
        Iterator catsIter = categories.iterator();

        while (catsIter.hasNext()) {
            Category cat = (Category) catsIter.next();
            retList = retList + UtilHTML.getSelectCategories(cat, 0, inode + ",", user, true);

        }
        return retList;
    }

    public String getEventsCategoriesListByEntityName(String entityName, String inode) throws DotDataException, DotSecurityException {
		User user = null;
        try {
            if (request.getSession() != null)
                user = (com.liferay.portal.model.User) request.getSession().getAttribute(com.dotmarketing.util.WebKeys.CMS_USER);
        } catch (Exception nsue) {
            Logger.warn(this, "Exception trying to getUser: " + nsue.getMessage(), nsue);
        }
    	
        StringBuffer retList = new StringBuffer(512);
        retList.ensureCapacity(128);

        retList.append("<div style=\"float:left; margin:0px 17px 0px 0px;\">");

        Entity e = EntityFactory.getEntity(entityName);
        List<Category> categories = EntityFactory.getEntityCategories(e);
        List<Category> permissionedCategories = new ArrayList<Category>();
        for (Category category : categories) {
			if(categoryAPI.canUseCategory(category, user, true)){
				permissionedCategories.add(category);
			}
		}
        
        List<HashMap<Category, List<Category>>> childCategories = new ArrayList<HashMap<Category, List<Category>>>(10);
        HashMap<Category, List<Category>> parent;
        List<Category> child;
        List<Category> permissionedChild = new ArrayList<Category>();
        int totalItems = 0;
        for (Category tempCat: permissionedCategories) {
            ++totalItems;
            parent = new HashMap<Category, List<Category>>();
            child = categoryAPI.getChildren(tempCat, user, true);
            for (Category childCat : child) {
            	if(categoryAPI.canUseCategory(childCat, user, true)){
            		permissionedChild.add(childCat);
            	}
			}
            totalItems += permissionedChild.size();
            parent.put(tempCat, permissionedChild);
            childCategories.add(parent);
        }

        int div = 1;

        if (totalItems % 2 != 0)
            div = ((int) ((double) totalItems / (double) 2)) + 1;
        else
            div = (totalItems / 2);

        int i = 0;

        Iterator<HashMap<Category, List<Category>>> parentCatsIter = childCategories.iterator();
        Iterator catsIter = null;
        Category cat;
        Set<Category> tempSet;
        String catTitle = null;
        for (; parentCatsIter.hasNext() && (i < div); ) {
            parent = parentCatsIter.next();
            tempSet = parent.keySet();
            for (Category tempCat: tempSet) {
                ++i;
                catsIter = parent.get(tempCat).iterator();

                if (i == div) {
                    catTitle = tempCat.getCategoryName();
                    break;
                } else {
                    retList.append("<b>" + tempCat.getCategoryName() + "</b><br/>");
                }

                while (catsIter.hasNext() && (i < div)) {
                    ++i;
                    cat = (Category) catsIter.next();
                    if ((inode != null) && (-1 < inode.indexOf("" + cat.getInode())))
                        retList.append("<input type=\"checkbox\" id=\"categoryId\" name=\"categoryId\" value=\"" + cat.getInode() + "\" checked />&nbsp;" + cat.getCategoryName() + " <br/>");
                    else
                        retList.append("<input type=\"checkbox\" id=\"categoryId\" name=\"categoryId\" value=\"" + cat.getInode() + "\" />&nbsp;" + cat.getCategoryName() + " <br/>");
                }
            }
        }

        retList.append("</div>");
        retList.append("<div style=\"float:left; \">");

        if (catTitle != null)
            retList.append("<b>" + catTitle + "</b><br/>");

        if (catsIter != null) {
            while (catsIter.hasNext()) {
                ++i;
                cat = (Category) catsIter.next();
                if ((inode != null) && (-1 < inode.indexOf("" + cat.getInode())))
                    retList.append("<input type=\"checkbox\" id=\"categoryId\" name=\"categoryId\" value=\"" + cat.getInode() + "\" checked />&nbsp;" + cat.getCategoryName() + " <br/>");
                else
                    retList.append("<input type=\"checkbox\" id=\"categoryId\" name=\"categoryId\" value=\"" + cat.getInode() + "\" />&nbsp;" + cat.getCategoryName() + " <br/>");
            }
        }

        for (; parentCatsIter.hasNext(); ) {
            parent = parentCatsIter.next();
            tempSet = parent.keySet();
            for (Category tempCat: tempSet) {
                retList.append("<b>" + tempCat.getCategoryName() + "</b><br/>");

                catsIter = parent.get(tempCat).iterator();

                while (catsIter.hasNext()) {
                    cat = (Category) catsIter.next();
                    if ((inode != null) && (-1 < inode.indexOf("" + cat.getInode())))
                        retList.append("<input type=\"checkbox\" id=\"categoryId\" name=\"categoryId\" value=\"" + cat.getInode() + "\" checked />&nbsp;" + cat.getCategoryName() + " <br/>");
                    else
                        retList.append("<input type=\"checkbox\" id=\"categoryId\" name=\"categoryId\" value=\"" + cat.getInode() + "\" />&nbsp;" + cat.getCategoryName() + " <br/>");

                }
            }
        }

        retList.append("</div>");
        retList.append("<div style=\"clear:both;\"></div>");

        return retList.toString();
    }

  //Methods for macros
    public List getEvents(Date startDate, int daysDuration, String cats) {


        List parents = new ArrayList();
        if(cats != null){
            StringTokenizer st = new StringTokenizer(cats, ",");
            while (st.hasMoreTokens()) {
                try {
                    Category cat = new Category();
                    cat.setInode(st.nextToken());
                    parents.add(cat);
                } catch (Exception e) {
                }
            }
        }
        
        
        java.util.Calendar startCal = new GregorianCalendar();
        startCal.setTime(startDate);
        startCal.set(java.util.Calendar.HOUR_OF_DAY, 0);
        startCal.set(java.util.Calendar.MINUTE, 0);
        startCal.set(java.util.Calendar.SECOND, 0);
        String startDateStr = UtilMethods.dateToJDBC(startCal.getTime());
        
        
        java.util.Calendar cal = new GregorianCalendar();
        cal.set(java.util.Calendar.HOUR_OF_DAY, 23);
        cal.set(java.util.Calendar.MINUTE, 59);
        cal.set(java.util.Calendar.SECOND, 59);
        cal.add(java.util.Calendar.DAY_OF_YEAR, daysDuration);
        
        String endDateStr = UtilMethods.dateToJDBC(cal.getTime());
        
        return InodeFactory.getChildrenClassByConditionAndOrderBy(parents, Event.class, "start_date >= '" + startDateStr + "' and start_date <= '" + endDateStr +"' and approval_status = 1 and show_public = " + DbConnectionFactory.getDBTrue(), "start_date");
    }


    public Map getCalendarMap (String year, String month) {
        GregorianCalendar cal = new GregorianCalendar ();

        if (year == null || year.equals("")) year = Integer.toString(cal.get(GregorianCalendar.YEAR));
        if (month == null || month.equals("")) month = Integer.toString(cal.get(GregorianCalendar.MONTH));
        return Calendar.getMap(year, month);
    }

    public String getMenuMonth(int month){
        String monthName="";
        switch (month) {
            case 0: monthName="JAN";
                    break;
            case 1: monthName="FEB";
                    break;
            case 2: monthName="MAR";
                    break;
            case 3: monthName="APR";
                    break;
            case 4: monthName="MAY";
                    break;
            case 5: monthName="JUN";
                    break;
            case 6: monthName="JUL";
                    break;
            case 7: monthName="AUG";
                    break;
            case 8: monthName="SEP";
                    break;
            case 9: monthName="OCT";
                    break;
            case 10: monthName="NOV";
                    break;
            case 11: monthName="DEC";
                    break;

        }

        return monthName;
    }

    public String getMonthsMenu (String fromYear, String month, String toYear) {
    String calendarMenu="";
    int displayMonth = 8;
    String tabClass="";

    try {

    	Logger.debug(this.getClass(), "getMonthsMenu - parseInt month parameter: " + month);
        int intMonth = Integer.parseInt(month);

        for (int i=0;i<12;i++) {
        
        if(displayMonth > 11){
            displayMonth = displayMonth - 12;
        }
        
        if(displayMonth == intMonth){
            tabClass = "selected";
        }else if(displayMonth < 8){
            tabClass = "fut";
        }else{
            tabClass="past";
        }
        
        calendarMenu= calendarMenu + "\n<li class=\"" + tabClass + "\"><a href=\"javascript:doChangeMonth(" + fromYear + ", " + displayMonth + ", " + 
            toYear + ")\">"+getMenuMonth(displayMonth)+"</a></li>";
        displayMonth = displayMonth +1;
        }

        Logger.debug(this.getClass(), "getMonthsMenu - returning calendarMenu");
        return calendarMenu;
    } catch (java.lang.NumberFormatException nfe) {
    	Logger.error(this.getClass(), "getMonthsMenu - error parseInt month parameter: " + nfe.toString());
        return getMonthsMenu(fromYear, "0", toYear);
    } catch (Exception e) {
    	Logger.error(this.getClass(), "getMonthsMenu - error: " + e.toString());
        return "";
    }
    }

    public List getEventsInCalendar(String yearStr, String monthStr) {

        String catId = request.getParameter("categoryId");
        return getEventsInCalendar(yearStr, monthStr, catId);
    }

    public List getEventsInCalendar(String yearStr, String monthStr, String catId) {

        GregorianCalendar cal = new GregorianCalendar ();
        if (yearStr == null || yearStr.equals("")) yearStr = Integer.toString(cal.get(GregorianCalendar.YEAR));
        if (monthStr == null || monthStr.equals("")) monthStr = Integer.toString(cal.get(GregorianCalendar.MONTH));

        int year = Integer.parseInt(yearStr);
        int month = Integer.parseInt(monthStr);

        ArrayList list = new ArrayList ();
        if ((catId != null) && (catId.indexOf(',')) < 0) {
            Category category = (Category) InodeFactory.getInode(catId, Category.class);
            if (InodeUtils.isSet(category.getInode())) {
                list.add(category);
            }
        }

        cal.set(GregorianCalendar.HOUR_OF_DAY, 0);
        cal.set(GregorianCalendar.MINUTE, 0);
        cal.set(GregorianCalendar.SECOND, 0);
        cal.set(GregorianCalendar.DAY_OF_MONTH, 1);
        try {
            cal.set(GregorianCalendar.MONTH, month);
        } catch (Exception e) { }
        try {
            cal.set(GregorianCalendar.YEAR, year);
        } catch (Exception e) { }

        java.util.Calendar from = new GregorianCalendar ();
        java.util.Calendar to = new GregorianCalendar ();

        from.setTime(cal.getTime());
        from.set(GregorianCalendar.DAY_OF_MONTH, 1);

        to.setTime(cal.getTime());
        to.add(GregorianCalendar.MONTH, 1);
        to.set(GregorianCalendar.DAY_OF_MONTH, 1);

        List events = new ArrayList();
        if ((list.size() == 1) || (request.getParameter("categoryId") == null) || (request.getParameter("categoryId").trim().equals("0"))) {
            events = EventFactory.getEventsByDateRangeYParents(from.getTime(), to.getTime(), list);

        } else {
            String[] temp = request.getParameter("categoryId").split(",");

//          events = EventFactory.getPublicEventsByDateRangeYParent(from.getTime(), to.getTime(), temp, null, "", 0);
            events = EventFactory.getPublicEventsWithOrCategories(from.getTime(), to.getTime(), "", temp, 0);
        }

        // initialize ArrayList and fill with spaces
        ArrayList monthData = new ArrayList();

        for (int row = 0; row < 6; row++) {
            ArrayList weekVect = new ArrayList();
            monthData.add(weekVect);

            for (int col = 0; col < 7; col++) {
                weekVect.add(new ArrayList());
            }
        }

        // populate actual data
        cal = new GregorianCalendar(year, month, 1);

        int totalDays = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
        int prevcol = 0;
        int row = 0;

        for (int i = 1; i <= totalDays; i++) {
            cal.set(GregorianCalendar.DATE, i);

            int dayOfWeek = cal.get(GregorianCalendar.DAY_OF_WEEK);

            //int weekOfMonth = cal.get(GregorianCalendar.WEEK_OF_MONTH);
            int col = dayOfWeek - GregorianCalendar.SUNDAY;

            if (prevcol == 6) {
                row++;
            }

            ArrayList weekVect = ( ArrayList ) monthData.get(row);
            ArrayList weekEvents = (ArrayList) weekVect.get(col);
            //Add Events
            Iterator evIterator = events.iterator();
            while (evIterator.hasNext()) {
                Event ev = (Event)evIterator.next ();
                java.util.Calendar evStartDate = new GregorianCalendar();
                evStartDate.setTime(ev.getStartDate());
                evStartDate.set(java.util.Calendar.HOUR_OF_DAY, 0);
                evStartDate.set(java.util.Calendar.MINUTE, 0);
                evStartDate.set(java.util.Calendar.SECOND, 0);
                java.util.Calendar evEndDate = new GregorianCalendar();
                evEndDate.setTime(ev.getEndDate());
                evEndDate.set(java.util.Calendar.HOUR_OF_DAY, 23);
                evEndDate.set(java.util.Calendar.MINUTE, 59);
                evEndDate.set(java.util.Calendar.SECOND, 59);
                if (cal.getTime().compareTo(evStartDate.getTime()) >= 0 && cal.getTime().compareTo(evEndDate.getTime()) <= 0)
                    weekEvents.add(ev);
            }

            prevcol = col;
        }

        //remove last row if only 5 rows are needed
        if (row == 4) {
            monthData.remove(5);
        }

        return monthData;
    }

    public Facility getEventFacility(Event ev) {
        Facility facility = null;

        facility = (Facility) InodeFactory.getParentOfClass(ev, Facility.class);

        return facility;
    }

    public String calendarEventAux(String[] calendarCategories)
    {
        String returnValue = "?";
        if(calendarCategories != null && calendarCategories.length > 0)
        {
            for(int i = 0;i < calendarCategories.length;i++)
            {
                returnValue += "eventTypes=" + calendarCategories[i] + "&";
            }
        }
        else
        {
            returnValue = "";
        }
        return returnValue;
    }

    public String getCurrentYear () {
    GregorianCalendar cal = new GregorianCalendar ();
    
    return Integer.toString(cal.get(GregorianCalendar.YEAR)); 
    }
    
    public String getCurrentMonth () {
    GregorianCalendar cal = new GregorianCalendar ();
    
    return Integer.toString(cal.get(GregorianCalendar.MONTH)); 
    }
    
    public String getCurrentDay () {
    GregorianCalendar cal = new GregorianCalendar ();
    
    return Integer.toString(cal.get(GregorianCalendar.DAY_OF_MONTH)); 
    }
    
    public List getEventsByDateRange(Date from, Date to) {
    	List ret = new ArrayList();
    	try{
    		ret = EventFactory.getEventsByDateRange(from, to);
    	}catch (Exception e) {
			Logger.error(this,"Unable to get events by date range : " + e.getMessage() ,e);
		}
    	return ret;
    }
    
    public List<Event> getLatestEvents(int maxEvents){
    	
    	GregorianCalendar cal = new GregorianCalendar ();
		String yearStr  = Integer.toString(cal.get(GregorianCalendar.YEAR));
        String monthStr = Integer.toString(cal.get(GregorianCalendar.MONTH));

        int year = Integer.parseInt(yearStr);
        int month = Integer.parseInt(monthStr);
        try {
            cal.set(GregorianCalendar.MONTH, month);
        } catch (Exception e) { }
        try {
            cal.set(GregorianCalendar.YEAR, year);
        } catch (Exception e) { }

		java.util.Calendar from = new GregorianCalendar ();
        java.util.Calendar to = new GregorianCalendar ();

        from.setTime(cal.getTime());
        from.set(GregorianCalendar.DAY_OF_MONTH, 1);
        to.setTime(cal.getTime());
        to.add(GregorianCalendar.MONTH, 1);
        to.set(GregorianCalendar.DAY_OF_MONTH, 1);
        
        
    	return EventFactory.getLatestEvents(maxEvents,from.getTime(),to.getTime());
    	
    }
    
}