package com.dotmarketing.factories;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.hibernate.HibernateException;

import com.dotmarketing.beans.Inode;
import com.dotmarketing.beans.Tree;
import com.dotmarketing.db.DotHibernate;
import com.dotmarketing.util.Logger;

/**
 * 
 * @author maria
 */
public class TreeFactory {

	public static Tree getTree(String x) {
		try {
			return (Tree) new DotHibernate(Tree.class).load(Long.parseLong(x));
		} catch (Exception e) {
			return (Tree) new DotHibernate(Tree.class).load(x);
		}
	}

	public static Tree getTree(Inode parent, Inode child) {
		String relationType = "child";
		return getTree(parent, child, relationType);
	}

	public static Tree getTree(String parent, String child) {
		String relationType = "child";
		return getTree(parent, child, relationType);
	}

	public static Tree getTree(String parent, String child, String relationType) {
		try {
			String query = "from tree in class com.dotmarketing.beans.Tree where parent = ? and child = ? ";
			if(relationType != null) query += " and relation_type = ?";
			DotHibernate dh = new DotHibernate(Tree.class);
			dh.setQuery(query);
			dh.setParam(parent);
			dh.setParam(child);
			if(relationType != null) dh.setParam(relationType);

			return (Tree) dh.load();
		} catch (Exception e) {
			Logger.warn(TreeFactory.class, "getTree failed:" + e, e);
		}

		return new Tree();
	}

	public static Tree getTree(Inode parent, Inode child, String relationType) {
		return getTree(parent.getInode(), child.getInode(), relationType);
	}

	public static Tree getTreeByChildAndRelationType(Inode child, String relationType) {
		return getTreeByChildAndRelationType(child.getInode(), relationType);
	}
	
	public static Tree getTreeByChildAndRelationType(String child, String relationType) {
		try {
			DotHibernate dh = new DotHibernate(Tree.class);
			dh.setQuery("from tree in class com.dotmarketing.beans.Tree where child = ? and relation_type = ?");
			dh.setParam(child);
			dh.setParam(relationType);

			return (Tree) dh.load();
		} catch (Exception e) {
			Logger.warn(TreeFactory.class, "getTree failed:" + e, e);
		}

		return new Tree();
	}

	public static Tree getTreeByParentAndRelationType(Inode parent, String relationType) {
		try {
			DotHibernate dh = new DotHibernate(Tree.class);
			dh.setQuery("from tree in class com.dotmarketing.beans.Tree where parent = ? and relation_type = ?");
			dh.setParam(parent.getInode());
			dh.setParam(relationType);

			return (Tree) dh.load();
		} catch (Exception e) {
			Logger.warn(TreeFactory.class, "getTree failed:" + e, e);
		}

		return new Tree();
	}

	@SuppressWarnings("unchecked")
	public static List<Tree> getTreesByParentAndRelationType(Inode parent, String relationType) {
		try {
			DotHibernate dh = new DotHibernate(Tree.class);
			dh
					.setQuery("from tree in class com.dotmarketing.beans.Tree where parent = ? and relation_type = ? order by tree_order asc");
			dh.setParam(parent.getInode());
			dh.setParam(relationType);

			return dh.list();
		} catch (Exception e) {
			Logger.warn(TreeFactory.class, "getTree failed:" + e, e);
		}

		return new ArrayList<Tree>();
	}

	@SuppressWarnings("unchecked")
	public static List<Tree> getTreesByChildAndRelationType(Inode child, String relationType) {
		try {
			DotHibernate dh = new DotHibernate(Tree.class);
			dh
					.setQuery("from tree in class com.dotmarketing.beans.Tree where child = ? and relation_type = ? order by tree_order asc");
			dh.setParam(child.getInode());
			dh.setParam(relationType);

			return dh.list();
		} catch (Exception e) {
			Logger.warn(TreeFactory.class, "getTree failed:" + e, e);
		}

		return new ArrayList<Tree>();
	}

	@SuppressWarnings("unchecked")
	public static List<Tree> getTreesByRelationType(String relationType) {
		try {
			DotHibernate dh = new DotHibernate(Tree.class);
			dh.setQuery("from tree in class com.dotmarketing.beans.Tree where relation_type = ?");
			dh.setParam(relationType);

			return dh.list();
		} catch (Exception e) {
			Logger.warn(TreeFactory.class, "getTree failed:" + e, e);
		}

		return new ArrayList<Tree>();
	}

	@SuppressWarnings("unchecked")
	public static List<Tree> getTreesByParent(Inode inode) {
		return getTreesByParent(inode.getInode());
	}
	
	@SuppressWarnings("unchecked")
	public static List<Tree> getTreesByParent(String inode) {
		try {
			DotHibernate dh = new DotHibernate(Tree.class);
			dh.setQuery("from tree in class com.dotmarketing.beans.Tree where parent = ?");
			dh.setParam(inode);

			return dh.list();
		} catch (Exception e) {
			Logger.warn(TreeFactory.class, "getTree failed:" + e, e);
		}

		return new ArrayList<Tree>();
	}

	public static List<Tree> getTreesByChild(Inode inode) {
		return getTreesByChild(inode.getInode());
	}
	
	@SuppressWarnings("unchecked")
	public static List<Tree> getTreesByChild(String inode) {
		try {
			DotHibernate dh = new DotHibernate(Tree.class);
			dh.setQuery("from tree in class com.dotmarketing.beans.Tree where child = ?");
			dh.setParam(inode);

			return dh.list();
		} catch (Exception e) {
			Logger.warn(TreeFactory.class, "getTree failed:" + e, e);
		}

		return new ArrayList<Tree>();
	}

	public static void swapTrees(Inode i1, Inode i2) throws HibernateException {

		List<Tree> newTrees = new ArrayList<Tree>();

		// Removing actual trees and creating the new ones
		Iterator<Tree> it = getTreesByParent(i1).iterator();
		while (it.hasNext()) {
			Tree tree = (Tree) it.next();
			newTrees.add(new Tree(i2.getInode(), tree.getChild(), tree.getRelationType(), tree.getTreeOrder()));
			deleteTree(tree);
		}

		it = getTreesByChild(i1).iterator();
		while (it.hasNext()) {
			Tree tree = (Tree) it.next();
			newTrees.add(new Tree(tree.getParent(), i2.getInode(), tree.getRelationType(), tree.getTreeOrder()));
			deleteTree(tree);
		}

		it = getTreesByParent(i2).iterator();
		while (it.hasNext()) {
			Tree tree = (Tree) it.next();
			newTrees.add(new Tree(i1.getInode(), tree.getChild(), tree.getRelationType(), tree.getTreeOrder()));
			deleteTree(tree);
		}

		it = getTreesByChild(i2).iterator();
		while (it.hasNext()) {
			Tree tree = (Tree) it.next();
			newTrees.add(new Tree(tree.getParent(), i1.getInode(), tree.getRelationType(), tree.getTreeOrder()));
			deleteTree(tree);
		}

		// Saving new trees
		it = newTrees.iterator();
		while (it.hasNext()) {
			Tree tree = (Tree) it.next();
			saveTree(tree);
		}
		DotHibernate.flush();
		DotHibernate.getSession().refresh(i1);
		DotHibernate.getSession().refresh(i2);

	}

	public static void deleteTree(Tree tree) {
		DotHibernate.delete(tree);
	}

	public static void deleteTreesByParent(Inode parent) {
		DotHibernate.delete("from tree in class com.dotmarketing.beans.Tree where tree.parent = '" + parent.getInode()+"'");
	}

	public static void deleteTreesByParentAndRelationType(Inode parent, String relationType) {
		DotHibernate.delete("from tree in class com.dotmarketing.beans.Tree where tree.parent = '" + parent.getInode() + 
				"' and tree.relationType = '" + relationType + "'");
	}


	public static void deleteTreesByChildAndRelationType(Inode child, String relationType) {
		DotHibernate.delete("from tree in class com.dotmarketing.beans.Tree where tree.child = '" + child.getInode() + 
				"' and tree.relationType = '" + relationType + "'");
	}
	
	public static void deleteTreesByChild(Inode child) {
		DotHibernate.delete("from tree in class com.dotmarketing.beans.Tree where tree.child = '" + child.getInode()+"'");
	}

	public static void deleteTreesByRelationType(String relationType) {
		DotHibernate
				.delete("from tree in class com.dotmarketing.beans.Tree where tree.relationType = '" + relationType + "'");
	}

	public static void saveTree(Tree tree) {
		DotHibernate.saveOrUpdate(tree);
	}

	
}
