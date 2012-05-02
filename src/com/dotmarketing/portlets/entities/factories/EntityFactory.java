package com.dotmarketing.portlets.entities.factories;

import java.util.List;

import com.dotmarketing.db.DbConnectionFactory;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.factories.InodeFactory;
import com.dotmarketing.portlets.categories.model.Category;
import com.dotmarketing.portlets.entities.model.Entity;

/**
 *
 * @author  will
 */
public class EntityFactory {
	
	public static Entity getEntity(String entityName) {
        if (entityName == null) return new Entity ();        
		DotHibernate dh = new DotHibernate(Entity.class);
		dh.setQuery(
			"from entity in class com.dotmarketing.portlets.entities.model.Entity where entity_name like ? ");
		dh.setParam(entityName);
		return (Entity) dh.load();
				
	}
	
	@SuppressWarnings("unchecked")
	public static java.util.List<Category> getEntityCategories(Entity entity) {
		return InodeFactory.getParentsOfClass(entity,Category.class, "sort_order");
	}
	
	@SuppressWarnings("unchecked")
	public static java.util.List<Category> getActiveEntityCategories(Entity entity) 
	{
		String condition = "active = " + DbConnectionFactory.getDBTrue();
		String sortOrder = "sort_order asc, category_name asc";
		List<Category> categories = InodeFactory.getParentsOfClassByConditionSorted(entity,Category.class, condition,sortOrder);
		return categories;
	}
}
