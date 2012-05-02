How to build the dotCMS sql files

It is now possible to build the dotcms sql files by using an ant task. There are two different tasks that build different dbs. The first ant task buildsql builds the dbs (oracle, postgresql) that use a sequence for a primary key. The second ant task buildmXsql builds the dbs that use an autonumber/identity (mssql and mysql). The ant task trys to do a find and replace on the Inode.hbm.xml to make sure the id generators are correct for each build.

The class responsable for this task is:
	com.dotmarketing.db.DotSQLGeneratorTask

How to build the sql files

   1. Make sure that your Inode.hbm.xml is in good shape. This means that the Inode.hbm.xml file needs to have both sequence and native generators listed where needed and commented out. Also, make sure any indexes you need created are referenced in the file as well in the column elements.
   
   2. Install ant 1.6+
   
   3. make sure that the $ANT_HOME/bin is in your path. Try:
   
      	set PATH=%PATH%;c:\path_to_ant
   
   4. cd into the $DOTCMS_HOME
   
   5. type
      
      	ant buildsql
      
      The ant script will read the Inode.hbm.xml and generate the files for postgreSQL and Oracle databases (dbs that use sequences for autonumbering). then type
     
      	ant buildmXsql

      which will build the MySQL.sql files and the "Sybase" sql files, which is for MSSQL.
      
   6. All generated SQL files are placed into $DOTCMS_HOME/sql/cms
