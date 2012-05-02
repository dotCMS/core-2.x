-- Replaces all the web assets inodes entries in the multi_tree 
-- by his identifiers
update multi_tree set parent1 = identifier1.inode, parent2 = identifier2.inode, child = identifier3.inode
from identifier as identifier1, identifier as identifier2, identifier as identifier3, tree as tree1, tree as tree2, tree as tree3
where multi_tree.parent1 = tree1.child and tree1.parent = identifier1.inode  and 
multi_tree.parent2 = tree2.child and tree2.parent = identifier2.inode and
multi_tree.child = tree3.child and tree3.parent = identifier3.inode