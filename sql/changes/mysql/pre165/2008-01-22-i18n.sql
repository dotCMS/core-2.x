/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET NAMES utf8 */;

/* --> TABLE `abcontact` */

ALTER TABLE `abcontact` 
	DROP primary key;
ALTER TABLE `abcontact` 
	CHANGE comments comments BLOB;
ALTER TABLE `abcontact` 
	CHANGE comments comments longtext CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE contactid contactid BLOB;
ALTER TABLE `abcontact` 
	CHANGE contactid contactid varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE businesspager businesspager BLOB;
ALTER TABLE `abcontact` 
	CHANGE businesspager businesspager varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE instantmessenger instantmessenger BLOB;
ALTER TABLE `abcontact` 
	CHANGE instantmessenger instantmessenger varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE businessphone businessphone BLOB;
ALTER TABLE `abcontact` 
	CHANGE businessphone businessphone varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE homeemailaddress homeemailaddress BLOB;
ALTER TABLE `abcontact` 
	CHANGE homeemailaddress homeemailaddress varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE businessstate businessstate BLOB;
ALTER TABLE `abcontact` 
	CHANGE businessstate businessstate varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE jobclass jobclass BLOB;
ALTER TABLE `abcontact` 
	CHANGE jobclass jobclass varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE timezoneid timezoneid BLOB;
ALTER TABLE `abcontact` 
	CHANGE timezoneid timezoneid varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE homestreet homestreet BLOB;
ALTER TABLE `abcontact` 
	CHANGE homestreet homestreet varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE homecell homecell BLOB;
ALTER TABLE `abcontact` 
	CHANGE homecell homecell varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE homefax homefax BLOB;
ALTER TABLE `abcontact` 
	CHANGE homefax homefax varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE userid userid BLOB;
ALTER TABLE `abcontact` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE businessemailaddress businessemailaddress BLOB;
ALTER TABLE `abcontact` 
	CHANGE businessemailaddress businessemailaddress varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE emailaddress emailaddress BLOB;
ALTER TABLE `abcontact` 
	CHANGE emailaddress emailaddress varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE businesscell businesscell BLOB;
ALTER TABLE `abcontact` 
	CHANGE businesscell businesscell varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE homephone homephone BLOB;
ALTER TABLE `abcontact` 
	CHANGE homephone homephone varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE nickname nickname BLOB;
ALTER TABLE `abcontact` 
	CHANGE nickname nickname varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE businesszip businesszip BLOB;
ALTER TABLE `abcontact` 
	CHANGE businesszip businesszip varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE employeenumber employeenumber BLOB;
ALTER TABLE `abcontact` 
	CHANGE employeenumber employeenumber varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE businessstreet businessstreet BLOB;
ALTER TABLE `abcontact` 
	CHANGE businessstreet businessstreet varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE businesscountry businesscountry BLOB;
ALTER TABLE `abcontact` 
	CHANGE businesscountry businesscountry varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE homecountry homecountry BLOB;
ALTER TABLE `abcontact` 
	CHANGE homecountry homecountry varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE jobtitle jobtitle BLOB;
ALTER TABLE `abcontact` 
	CHANGE jobtitle jobtitle varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE businesscity businesscity BLOB;
ALTER TABLE `abcontact` 
	CHANGE businesscity businesscity varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE hoursofoperation hoursofoperation BLOB;
ALTER TABLE `abcontact` 
	CHANGE hoursofoperation hoursofoperation longtext CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE lastname lastname BLOB;
ALTER TABLE `abcontact` 
	CHANGE lastname lastname varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE hometollfree hometollfree BLOB;
ALTER TABLE `abcontact` 
	CHANGE hometollfree hometollfree varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE businesscompany businesscompany BLOB;
ALTER TABLE `abcontact` 
	CHANGE businesscompany businesscompany varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE businesstollfree businesstollfree BLOB;
ALTER TABLE `abcontact` 
	CHANGE businesstollfree businesstollfree varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE homecity homecity BLOB;
ALTER TABLE `abcontact` 
	CHANGE homecity homecity varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE businessfax businessfax BLOB;
ALTER TABLE `abcontact` 
	CHANGE businessfax businessfax varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE middlename middlename BLOB;
ALTER TABLE `abcontact` 
	CHANGE middlename middlename varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE homezip homezip BLOB;
ALTER TABLE `abcontact` 
	CHANGE homezip homezip varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE firstname firstname BLOB;
ALTER TABLE `abcontact` 
	CHANGE firstname firstname varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE homestate homestate BLOB;
ALTER TABLE `abcontact` 
	CHANGE homestate homestate varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE website website BLOB;
ALTER TABLE `abcontact` 
	CHANGE website website varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	CHANGE homepager homepager BLOB;
ALTER TABLE `abcontact` 
	CHANGE homepager homepager varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontact` 
	ADD primary key(contactid);
ALTER TABLE `abcontact` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `abcontacts_ablists` */

ALTER TABLE `abcontacts_ablists` 
	CHANGE contactid contactid BLOB;
ALTER TABLE `abcontacts_ablists` 
	CHANGE contactid contactid varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontacts_ablists` 
	CHANGE listid listid BLOB;
ALTER TABLE `abcontacts_ablists` 
	CHANGE listid listid varchar(100) CHARACTER SET utf8;
ALTER TABLE `abcontacts_ablists` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `ablist` */

ALTER TABLE `ablist` 
	DROP primary key;
ALTER TABLE `ablist` 
	CHANGE userid userid BLOB;
ALTER TABLE `ablist` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `ablist` 
	CHANGE listid listid BLOB;
ALTER TABLE `ablist` 
	CHANGE listid listid varchar(100) CHARACTER SET utf8;
ALTER TABLE `ablist` 
	CHANGE name name BLOB;
ALTER TABLE `ablist` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `ablist` 
	ADD primary key(listid);
ALTER TABLE `ablist` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `address` */

ALTER TABLE `address` 
	DROP primary key;
ALTER TABLE `address` 
	CHANGE phone phone BLOB;
ALTER TABLE `address` 
	CHANGE phone phone varchar(100) CHARACTER SET utf8;
ALTER TABLE `address` 
	CHANGE classname classname BLOB;
ALTER TABLE `address` 
	CHANGE classname classname varchar(100) CHARACTER SET utf8;
ALTER TABLE `address` 
	CHANGE cell cell BLOB;
ALTER TABLE `address` 
	CHANGE cell cell varchar(100) CHARACTER SET utf8;
ALTER TABLE `address` 
	CHANGE classpk classpk BLOB;
ALTER TABLE `address` 
	CHANGE classpk classpk varchar(100) CHARACTER SET utf8;
ALTER TABLE `address` 
	CHANGE fax fax BLOB;
ALTER TABLE `address` 
	CHANGE fax fax varchar(100) CHARACTER SET utf8;
ALTER TABLE `address` 
	CHANGE addressid addressid BLOB;
ALTER TABLE `address` 
	CHANGE addressid addressid varchar(100) CHARACTER SET utf8;
ALTER TABLE `address` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `address` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `address` 
	CHANGE street2 street2 BLOB;
ALTER TABLE `address` 
	CHANGE street2 street2 varchar(100) CHARACTER SET utf8;
ALTER TABLE `address` 
	CHANGE userid userid BLOB;
ALTER TABLE `address` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `address` 
	CHANGE country country BLOB;
ALTER TABLE `address` 
	CHANGE country country varchar(100) CHARACTER SET utf8;
ALTER TABLE `address` 
	CHANGE street1 street1 BLOB;
ALTER TABLE `address` 
	CHANGE street1 street1 varchar(100) CHARACTER SET utf8;
ALTER TABLE `address` 
	CHANGE description description BLOB;
ALTER TABLE `address` 
	CHANGE description description varchar(100) CHARACTER SET utf8;
ALTER TABLE `address` 
	CHANGE username username BLOB;
ALTER TABLE `address` 
	CHANGE username username varchar(100) CHARACTER SET utf8;
ALTER TABLE `address` 
	CHANGE state state BLOB;
ALTER TABLE `address` 
	CHANGE state state varchar(100) CHARACTER SET utf8;
ALTER TABLE `address` 
	CHANGE zip zip BLOB;
ALTER TABLE `address` 
	CHANGE zip zip varchar(100) CHARACTER SET utf8;
ALTER TABLE `address` 
	CHANGE city city BLOB;
ALTER TABLE `address` 
	CHANGE city city varchar(100) CHARACTER SET utf8;
ALTER TABLE `address` 
	ADD primary key (addressid);
ALTER TABLE `address` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `adminconfig` */

ALTER TABLE `adminconfig` 
	DROP primary key;
ALTER TABLE `adminconfig` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `adminconfig` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `adminconfig` 
	CHANGE type_ type_ BLOB;
ALTER TABLE `adminconfig` 
	CHANGE type_ type_ varchar(100) CHARACTER SET utf8;
ALTER TABLE `adminconfig` 
	CHANGE configid configid BLOB;
ALTER TABLE `adminconfig` 
	CHANGE configid configid varchar(100) CHARACTER SET utf8;
ALTER TABLE `adminconfig` 
	CHANGE config config BLOB;
ALTER TABLE `adminconfig` 
	CHANGE config config longtext CHARACTER SET utf8;
ALTER TABLE `adminconfig` 
	CHANGE name name BLOB;
ALTER TABLE `adminconfig` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `adminconfig` 
	ADD primary key(configid);
ALTER TABLE `adminconfig` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `banner` */

ALTER TABLE `banner` 
	CHANGE active active BLOB;
ALTER TABLE `banner` 
	CHANGE active active varchar(1) CHARACTER SET utf8;
ALTER TABLE `banner` 
	CHANGE title title BLOB;
ALTER TABLE `banner` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `banner` 
	CHANGE link link BLOB;
ALTER TABLE `banner` 
	CHANGE link link varchar(255) CHARACTER SET utf8;
ALTER TABLE `banner` 
	CHANGE caption caption BLOB;
ALTER TABLE `banner` 
	CHANGE caption caption longtext CHARACTER SET utf8;
ALTER TABLE `banner` 
	CHANGE placement placement BLOB;
ALTER TABLE `banner` 
	CHANGE placement placement varchar(255) CHARACTER SET utf8;
ALTER TABLE `banner` 
	CHANGE new_window new_window BLOB;
ALTER TABLE `banner` 
	CHANGE new_window new_window varchar(1) CHARACTER SET utf8;
ALTER TABLE `banner` 
	CHANGE path path BLOB;
ALTER TABLE `banner` 
	CHANGE path path text CHARACTER SET utf8;
ALTER TABLE `banner` 
	CHANGE body body BLOB;
ALTER TABLE `banner` 
	CHANGE body body varchar(255) CHARACTER SET utf8;
ALTER TABLE `banner` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `bjentries_bjtopics` */

ALTER TABLE `bjentries_bjtopics` 
	CHANGE topicid topicid BLOB;
ALTER TABLE `bjentries_bjtopics` 
	CHANGE topicid topicid varchar(100) CHARACTER SET utf8;
ALTER TABLE `bjentries_bjtopics` 
	CHANGE entryid entryid BLOB;
ALTER TABLE `bjentries_bjtopics` 
	CHANGE entryid entryid varchar(100) CHARACTER SET utf8;
ALTER TABLE `bjentries_bjtopics` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `bjentries_bjverses` */

ALTER TABLE `bjentries_bjverses` 
	CHANGE verseid verseid BLOB;
ALTER TABLE `bjentries_bjverses` 
	CHANGE verseid verseid varchar(100) CHARACTER SET utf8;
ALTER TABLE `bjentries_bjverses` 
	CHANGE entryid entryid BLOB;
ALTER TABLE `bjentries_bjverses` 
	CHANGE entryid entryid varchar(100) CHARACTER SET utf8;
ALTER TABLE `bjentries_bjverses` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `bjentry` */

ALTER TABLE `bjentry` 
	DROP primary key;
ALTER TABLE `bjentry` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `bjentry` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `bjentry` 
	CHANGE userid userid BLOB;
ALTER TABLE `bjentry` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `bjentry` 
	CHANGE versesinput versesinput BLOB;
ALTER TABLE `bjentry` 
	CHANGE versesinput versesinput longtext CHARACTER SET utf8;
ALTER TABLE `bjentry` 
	CHANGE content content BLOB;
ALTER TABLE `bjentry` 
	CHANGE content content longtext CHARACTER SET utf8;
ALTER TABLE `bjentry` 
	CHANGE entryid entryid BLOB;
ALTER TABLE `bjentry` 
	CHANGE entryid entryid varchar(100) CHARACTER SET utf8;
ALTER TABLE `bjentry` 
	CHANGE name name BLOB;
ALTER TABLE `bjentry` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `bjentry` 
	ADD primary key(entryid);
ALTER TABLE `bjentry` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `bjtopic` */

ALTER TABLE `bjtopic` 
	DROP primary key;
ALTER TABLE `bjtopic` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `bjtopic` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `bjtopic` 
	CHANGE userid userid BLOB;
ALTER TABLE `bjtopic` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `bjtopic` 
	CHANGE topicid topicid BLOB;
ALTER TABLE `bjtopic` 
	CHANGE topicid topicid varchar(100) CHARACTER SET utf8;
ALTER TABLE `bjtopic` 
	CHANGE description description BLOB;
ALTER TABLE `bjtopic` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `bjtopic` 
	CHANGE name name BLOB;
ALTER TABLE `bjtopic` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `bjtopic` 
	ADD primary key(topicid);
ALTER TABLE `bjtopic` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `bjverse` */

ALTER TABLE `bjverse` 
	DROP primary key;
ALTER TABLE `bjverse` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `bjverse` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `bjverse` 
	CHANGE verseid verseid BLOB;
ALTER TABLE `bjverse` 
	CHANGE verseid verseid varchar(100) CHARACTER SET utf8;
ALTER TABLE `bjverse` 
	CHANGE userid userid BLOB;
ALTER TABLE `bjverse` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `bjverse` 
	CHANGE name name BLOB;
ALTER TABLE `bjverse` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `bjverse` 
	ADD primary key(verseid);
ALTER TABLE `bjverse` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `blogscategory` */

ALTER TABLE `blogscategory` 
	DROP primary key;
ALTER TABLE `blogscategory` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `blogscategory` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogscategory` 
	CHANGE userid userid BLOB;
ALTER TABLE `blogscategory` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogscategory` 
	CHANGE name name BLOB;
ALTER TABLE `blogscategory` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogscategory` 
	CHANGE categoryid categoryid BLOB;
ALTER TABLE `blogscategory` 
	CHANGE categoryid categoryid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogscategory` 
	ADD primary key(categoryid);
ALTER TABLE `blogscategory` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `blogscomments` */

ALTER TABLE `blogscomments` 
	DROP primary key;
ALTER TABLE `blogscomments` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `blogscomments` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogscomments` 
	CHANGE userid userid BLOB;
ALTER TABLE `blogscomments` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogscomments` 
	CHANGE content content BLOB;
ALTER TABLE `blogscomments` 
	CHANGE content content longtext CHARACTER SET utf8;
ALTER TABLE `blogscomments` 
	CHANGE entryid entryid BLOB;
ALTER TABLE `blogscomments` 
	CHANGE entryid entryid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogscomments` 
	CHANGE username username BLOB;
ALTER TABLE `blogscomments` 
	CHANGE username username varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogscomments` 
	CHANGE commentsid commentsid BLOB;
ALTER TABLE `blogscomments` 
	CHANGE commentsid commentsid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogscomments` 
	ADD primary key(commentsid);
ALTER TABLE `blogscomments` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `blogsentry` */

ALTER TABLE `blogsentry` 
	DROP primary key;
ALTER TABLE `blogsentry` 
	CHANGE title title BLOB;
ALTER TABLE `blogsentry` 
	CHANGE title title varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogsentry` 
	CHANGE entryid entryid BLOB;
ALTER TABLE `blogsentry` 
	CHANGE entryid entryid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogsentry` 
	CHANGE content content BLOB;
ALTER TABLE `blogsentry` 
	CHANGE content content longtext CHARACTER SET utf8;
ALTER TABLE `blogsentry` 
	CHANGE categoryid categoryid BLOB;
ALTER TABLE `blogsentry` 
	CHANGE categoryid categoryid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogsentry` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `blogsentry` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogsentry` 
	CHANGE userid userid BLOB;
ALTER TABLE `blogsentry` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogsentry` 
	ADD primary key(entryid);
ALTER TABLE `blogsentry` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `blogslink` */

ALTER TABLE `blogslink` 
	DROP primary key;
ALTER TABLE `blogslink` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `blogslink` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogslink` 
	CHANGE userid userid BLOB;
ALTER TABLE `blogslink` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogslink` 
	CHANGE linkid linkid BLOB;
ALTER TABLE `blogslink` 
	CHANGE linkid linkid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogslink` 
	CHANGE url url BLOB;
ALTER TABLE `blogslink` 
	CHANGE url url varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogslink` 
	CHANGE name name BLOB;
ALTER TABLE `blogslink` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogslink` 
	ADD primary key(linkid);
ALTER TABLE `blogslink` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `blogsprops` */

ALTER TABLE `blogsprops` 
	DROP primary key;
ALTER TABLE `blogsprops` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `blogsprops` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogsprops` 
	CHANGE userid userid BLOB;
ALTER TABLE `blogsprops` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogsprops` 
	CHANGE entryid entryid BLOB;
ALTER TABLE `blogsprops` 
	CHANGE entryid entryid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogsprops` 
	CHANGE username username BLOB;
ALTER TABLE `blogsprops` 
	CHANGE username username varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogsprops` 
	CHANGE propsid propsid BLOB;
ALTER TABLE `blogsprops` 
	CHANGE propsid propsid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogsprops` 
	ADD primary key(propsid);
ALTER TABLE `blogsprops` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `blogsreferer` */

ALTER TABLE `blogsreferer` 
	DROP primary key;
ALTER TABLE `blogsreferer` 
	CHANGE type_ type_ BLOB;
ALTER TABLE `blogsreferer` 
	CHANGE type_ type_ varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogsreferer` 
	CHANGE entryid entryid BLOB;
ALTER TABLE `blogsreferer` 
	CHANGE entryid entryid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogsreferer` 
	CHANGE url url BLOB;
ALTER TABLE `blogsreferer` 
	CHANGE url url varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogsreferer` 
	ADD primary key(entryid,url,type_);
ALTER TABLE `blogsreferer` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `blogsuser` */

ALTER TABLE `blogsuser` 
	DROP primary key;
ALTER TABLE `blogsuser` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `blogsuser` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogsuser` 
	CHANGE userid userid BLOB;
ALTER TABLE `blogsuser` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogsuser` 
	CHANGE entryid entryid BLOB;
ALTER TABLE `blogsuser` 
	CHANGE entryid entryid varchar(100) CHARACTER SET utf8;
ALTER TABLE `blogsuser` 
	ADD primary key(userid);
ALTER TABLE `blogsuser` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `bookmarksentry` */

ALTER TABLE `bookmarksentry` 
	DROP primary key;
ALTER TABLE `bookmarksentry` 
	CHANGE comments comments BLOB;
ALTER TABLE `bookmarksentry` 
	CHANGE comments comments longtext CHARACTER SET utf8;
ALTER TABLE `bookmarksentry` 
	CHANGE userid userid BLOB;
ALTER TABLE `bookmarksentry` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `bookmarksentry` 
	CHANGE folderid folderid BLOB;
ALTER TABLE `bookmarksentry` 
	CHANGE folderid folderid varchar(100) CHARACTER SET utf8;
ALTER TABLE `bookmarksentry` 
	CHANGE entryid entryid BLOB;
ALTER TABLE `bookmarksentry` 
	CHANGE entryid entryid varchar(100) CHARACTER SET utf8;
ALTER TABLE `bookmarksentry` 
	CHANGE url url BLOB;
ALTER TABLE `bookmarksentry` 
	CHANGE url url varchar(100) CHARACTER SET utf8;
ALTER TABLE `bookmarksentry` 
	CHANGE name name BLOB;
ALTER TABLE `bookmarksentry` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `bookmarksentry` 
	ADD primary key(entryid);
ALTER TABLE `bookmarksentry` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `bookmarksfolder` */

ALTER TABLE `bookmarksfolder` 
	DROP primary key;
ALTER TABLE `bookmarksfolder` 
	CHANGE userid userid BLOB;
ALTER TABLE `bookmarksfolder` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `bookmarksfolder` 
	CHANGE folderid folderid BLOB;
ALTER TABLE `bookmarksfolder` 
	CHANGE folderid folderid varchar(100) CHARACTER SET utf8;
ALTER TABLE `bookmarksfolder` 
	CHANGE parentfolderid parentfolderid BLOB;
ALTER TABLE `bookmarksfolder` 
	CHANGE parentfolderid parentfolderid varchar(100) CHARACTER SET utf8;
ALTER TABLE `bookmarksfolder` 
	CHANGE name name BLOB;
ALTER TABLE `bookmarksfolder` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `bookmarksfolder` 
	ADD primary key(folderid);
ALTER TABLE `bookmarksfolder` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `calevent` */

ALTER TABLE `calevent` 
	DROP primary key;
ALTER TABLE `calevent` 
	CHANGE phone phone BLOB;
ALTER TABLE `calevent` 
	CHANGE phone phone varchar(100) CHARACTER SET utf8;
ALTER TABLE `calevent` 
	CHANGE remindby remindby BLOB;
ALTER TABLE `calevent` 
	CHANGE remindby remindby varchar(100) CHARACTER SET utf8;
ALTER TABLE `calevent` 
	CHANGE title title BLOB;
ALTER TABLE `calevent` 
	CHANGE title title varchar(100) CHARACTER SET utf8;
ALTER TABLE `calevent` 
	CHANGE recurrence recurrence BLOB;
ALTER TABLE `calevent` 
	CHANGE recurrence recurrence longtext CHARACTER SET utf8;
ALTER TABLE `calevent` 
	CHANGE userid userid BLOB;
ALTER TABLE `calevent` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `calevent` 
	CHANGE username username BLOB;
ALTER TABLE `calevent` 
	CHANGE username username varchar(100) CHARACTER SET utf8;
ALTER TABLE `calevent` 
	CHANGE zip zip BLOB;
ALTER TABLE `calevent` 
	CHANGE zip zip varchar(100) CHARACTER SET utf8;
ALTER TABLE `calevent` 
	CHANGE location location BLOB;
ALTER TABLE `calevent` 
	CHANGE location location varchar(100) CHARACTER SET utf8;
ALTER TABLE `calevent` 
	CHANGE city city BLOB;
ALTER TABLE `calevent` 
	CHANGE city city varchar(100) CHARACTER SET utf8;
ALTER TABLE `calevent` 
	CHANGE eventid eventid BLOB;
ALTER TABLE `calevent` 
	CHANGE eventid eventid varchar(100) CHARACTER SET utf8;
ALTER TABLE `calevent` 
	CHANGE groupid groupid BLOB;
ALTER TABLE `calevent` 
	CHANGE groupid groupid varchar(100) CHARACTER SET utf8;
ALTER TABLE `calevent` 
	CHANGE type_ type_ BLOB;
ALTER TABLE `calevent` 
	CHANGE type_ type_ varchar(100) CHARACTER SET utf8;
ALTER TABLE `calevent` 
	CHANGE street street BLOB;
ALTER TABLE `calevent` 
	CHANGE street street varchar(100) CHARACTER SET utf8;
ALTER TABLE `calevent` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `calevent` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `calevent` 
	CHANGE description description BLOB;
ALTER TABLE `calevent` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `calevent` 
	CHANGE state state BLOB;
ALTER TABLE `calevent` 
	CHANGE state state varchar(100) CHARACTER SET utf8;
ALTER TABLE `calevent` 
	ADD primary key(eventid);
ALTER TABLE `calevent` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `caltask` */

ALTER TABLE `caltask` 
	DROP primary key;
ALTER TABLE `caltask` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `caltask` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `caltask` 
	CHANGE userid userid BLOB;
ALTER TABLE `caltask` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `caltask` 
	CHANGE title title BLOB;
ALTER TABLE `caltask` 
	CHANGE title title varchar(100) CHARACTER SET utf8;
ALTER TABLE `caltask` 
	CHANGE description description BLOB;
ALTER TABLE `caltask` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `caltask` 
	CHANGE taskid taskid BLOB;
ALTER TABLE `caltask` 
	CHANGE taskid taskid varchar(100) CHARACTER SET utf8;
ALTER TABLE `caltask` 
	ADD primary key(taskid);
ALTER TABLE `caltask` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `campaign` */

ALTER TABLE `campaign` 
	DROP INDEX idx_campaign_1;
ALTER TABLE `campaign` 
	CHANGE active active BLOB;
ALTER TABLE `campaign` 
	CHANGE active active varchar(1) CHARACTER SET utf8;
ALTER TABLE `campaign` 
	CHANGE wassent wassent BLOB;
ALTER TABLE `campaign` 
	CHANGE wassent wassent varchar(1) CHARACTER SET utf8;
ALTER TABLE `campaign` 
	CHANGE title title BLOB;
ALTER TABLE `campaign` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `campaign` 
	CHANGE message message BLOB;
ALTER TABLE `campaign` 
	CHANGE message message longtext CHARACTER SET utf8;
ALTER TABLE `campaign` 
	CHANGE sends_per_hour sends_per_hour BLOB;
ALTER TABLE `campaign` 
	CHANGE sends_per_hour sends_per_hour varchar(15) CHARACTER SET utf8;
ALTER TABLE `campaign` 
	CHANGE sendto sendto BLOB;
ALTER TABLE `campaign` 
	CHANGE sendto sendto varchar(15) CHARACTER SET utf8;
ALTER TABLE `campaign` 
	CHANGE from_name from_name BLOB;
ALTER TABLE `campaign` 
	CHANGE from_name from_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `campaign` 
	CHANGE isrecurrent isrecurrent BLOB;
ALTER TABLE `campaign` 
	CHANGE isrecurrent isrecurrent varchar(1) CHARACTER SET utf8;
ALTER TABLE `campaign` 
	CHANGE user_id user_id BLOB;
ALTER TABLE `campaign` 
	CHANGE user_id user_id varchar(255) CHARACTER SET utf8;
ALTER TABLE `campaign` 
	CHANGE sendemail sendemail BLOB;
ALTER TABLE `campaign` 
	CHANGE sendemail sendemail varchar(1) CHARACTER SET utf8;
ALTER TABLE `campaign` 
	CHANGE locked locked BLOB;
ALTER TABLE `campaign` 
	CHANGE locked locked varchar(1) CHARACTER SET utf8;
ALTER TABLE `campaign` 
	CHANGE subject subject BLOB;
ALTER TABLE `campaign` 
	CHANGE subject subject varchar(255) CHARACTER SET utf8;
ALTER TABLE `campaign` 
	CHANGE from_email from_email BLOB;
ALTER TABLE `campaign` 
	CHANGE from_email from_email varchar(255) CHARACTER SET utf8;
ALTER TABLE `campaign` 
	ADD INDEX idx_campaign_1(user_id);
ALTER TABLE `campaign` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `category` */

ALTER TABLE `category` 
	DROP INDEX idx_category_2,
	DROP INDEX idx_category_1;
ALTER TABLE `category` 
	CHANGE active active BLOB;
ALTER TABLE `category` 
	CHANGE active active varchar(1) CHARACTER SET utf8;
ALTER TABLE `category` 
	CHANGE keywords keywords BLOB;
ALTER TABLE `category` 
	CHANGE keywords keywords longtext CHARACTER SET utf8;
ALTER TABLE `category` 
	CHANGE category_name category_name BLOB;
ALTER TABLE `category` 
	CHANGE category_name category_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `category` 
	CHANGE category_key category_key BLOB;
ALTER TABLE `category` 
	CHANGE category_key category_key varchar(255) CHARACTER SET utf8;
ALTER TABLE `category` 
	ADD INDEX idx_category_2(category_key),
	ADD INDEX idx_category_1(category_name);
ALTER TABLE `category` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `click` */

ALTER TABLE `click` 
	DROP INDEX idx_click_1;
ALTER TABLE `click` 
	CHANGE link link BLOB;
ALTER TABLE `click` 
	CHANGE link link varchar(255) CHARACTER SET utf8;
ALTER TABLE `click` 
	ADD INDEX idx_click_1(link);
ALTER TABLE `click` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `clickstream` */

ALTER TABLE `clickstream` 
	DROP INDEX idx_user_clickstream_2,
	DROP INDEX idx_user_clickstream_1;
ALTER TABLE `clickstream` 
	CHANGE remote_address remote_address BLOB;
ALTER TABLE `clickstream` 
	CHANGE remote_address remote_address varchar(255) CHARACTER SET utf8;
ALTER TABLE `clickstream` 
	CHANGE user_agent user_agent BLOB;
ALTER TABLE `clickstream` 
	CHANGE user_agent user_agent varchar(255) CHARACTER SET utf8;
ALTER TABLE `clickstream` 
	CHANGE user_id user_id BLOB;
ALTER TABLE `clickstream` 
	CHANGE user_id user_id varchar(255) CHARACTER SET utf8;
ALTER TABLE `clickstream` 
	CHANGE cookie_id cookie_id BLOB;
ALTER TABLE `clickstream` 
	CHANGE cookie_id cookie_id varchar(255) CHARACTER SET utf8;
ALTER TABLE `clickstream` 
	CHANGE referer referer BLOB;
ALTER TABLE `clickstream` 
	CHANGE referer referer varchar(255) CHARACTER SET utf8;
ALTER TABLE `clickstream` 
	CHANGE bot bot BLOB;
ALTER TABLE `clickstream` 
	CHANGE bot bot varchar(1) CHARACTER SET utf8;
ALTER TABLE `clickstream` 
	ADD INDEX idx_user_clickstream_2(user_id),
	ADD INDEX idx_user_clickstream_1(cookie_id);
ALTER TABLE `clickstream` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `clickstream_request` */

ALTER TABLE `clickstream_request` 
	DROP INDEX idx_user_clickstream_request_2;
ALTER TABLE `clickstream_request` 
	CHANGE protocol protocol BLOB;
ALTER TABLE `clickstream_request` 
	CHANGE protocol protocol varchar(255) CHARACTER SET utf8;
ALTER TABLE `clickstream_request` 
	CHANGE request_uri request_uri BLOB;
ALTER TABLE `clickstream_request` 
	CHANGE request_uri request_uri varchar(255) CHARACTER SET utf8;
ALTER TABLE `clickstream_request` 
	CHANGE query_string query_string BLOB;
ALTER TABLE `clickstream_request` 
	CHANGE query_string query_string longtext CHARACTER SET utf8;
ALTER TABLE `clickstream_request` 
	CHANGE server_name server_name BLOB;
ALTER TABLE `clickstream_request` 
	CHANGE server_name server_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `clickstream_request` 
	ADD INDEX idx_user_clickstream_request_2(request_uri);
ALTER TABLE `clickstream_request` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `communication` */

ALTER TABLE `communication` 
	CHANGE title title BLOB;
ALTER TABLE `communication` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `communication` 
	CHANGE text_message text_message BLOB;
ALTER TABLE `communication` 
	CHANGE text_message text_message longtext CHARACTER SET utf8;
ALTER TABLE `communication` 
	CHANGE modified_by modified_by BLOB;
ALTER TABLE `communication` 
	CHANGE modified_by modified_by varchar(255) CHARACTER SET utf8;
ALTER TABLE `communication` 
	CHANGE communication_type communication_type BLOB;
ALTER TABLE `communication` 
	CHANGE communication_type communication_type varchar(255) CHARACTER SET utf8;
ALTER TABLE `communication` 
	CHANGE from_email from_email BLOB;
ALTER TABLE `communication` 
	CHANGE from_email from_email varchar(255) CHARACTER SET utf8;
ALTER TABLE `communication` 
	CHANGE from_name from_name BLOB;
ALTER TABLE `communication` 
	CHANGE from_name from_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `communication` 
	CHANGE ext_comm_id ext_comm_id BLOB;
ALTER TABLE `communication` 
	CHANGE ext_comm_id ext_comm_id varchar(255) CHARACTER SET utf8;
ALTER TABLE `communication` 
	CHANGE email_subject email_subject BLOB;
ALTER TABLE `communication` 
	CHANGE email_subject email_subject varchar(255) CHARACTER SET utf8;
ALTER TABLE `communication` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `company` */

ALTER TABLE `company` 
	DROP primary key;
ALTER TABLE `company` 
	CHANGE phone phone BLOB;
ALTER TABLE `company` 
	CHANGE phone phone varchar(100) CHARACTER SET utf8;
ALTER TABLE `company` 
	CHANGE portalurl portalurl BLOB;
ALTER TABLE `company` 
	CHANGE portalurl portalurl varchar(100) CHARACTER SET utf8;
ALTER TABLE `company` 
	CHANGE type_ type_ BLOB;
ALTER TABLE `company` 
	CHANGE type_ type_ varchar(100) CHARACTER SET utf8;
ALTER TABLE `company` 
	CHANGE street street BLOB;
ALTER TABLE `company` 
	CHANGE street street varchar(100) CHARACTER SET utf8;
ALTER TABLE `company` 
	CHANGE fax fax BLOB;
ALTER TABLE `company` 
	CHANGE fax fax varchar(100) CHARACTER SET utf8;
ALTER TABLE `company` 
	CHANGE size_ size_ BLOB;
ALTER TABLE `company` 
	CHANGE size_ size_ varchar(100) CHARACTER SET utf8;
ALTER TABLE `company` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `company` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `company` 
	CHANGE key_ key_ BLOB;
ALTER TABLE `company` 
	CHANGE key_ key_ longtext CHARACTER SET utf8;
ALTER TABLE `company` 
	CHANGE homeurl homeurl BLOB;
ALTER TABLE `company` 
	CHANGE homeurl homeurl varchar(100) CHARACTER SET utf8;
ALTER TABLE `company` 
	CHANGE mx mx BLOB;
ALTER TABLE `company` 
	CHANGE mx mx varchar(100) CHARACTER SET utf8;
ALTER TABLE `company` 
	CHANGE authtype authtype BLOB;
ALTER TABLE `company` 
	CHANGE authtype authtype varchar(100) CHARACTER SET utf8;
ALTER TABLE `company` 
	CHANGE emailaddress emailaddress BLOB;
ALTER TABLE `company` 
	CHANGE emailaddress emailaddress varchar(100) CHARACTER SET utf8;
ALTER TABLE `company` 
	CHANGE shortname shortname BLOB;
ALTER TABLE `company` 
	CHANGE shortname shortname varchar(100) CHARACTER SET utf8;
ALTER TABLE `company` 
	CHANGE state state BLOB;
ALTER TABLE `company` 
	CHANGE state state varchar(100) CHARACTER SET utf8;
ALTER TABLE `company` 
	CHANGE zip zip BLOB;
ALTER TABLE `company` 
	CHANGE zip zip varchar(100) CHARACTER SET utf8;
ALTER TABLE `company` 
	CHANGE name name BLOB;
ALTER TABLE `company` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `company` 
	CHANGE city city BLOB;
ALTER TABLE `company` 
	CHANGE city city varchar(100) CHARACTER SET utf8;
ALTER TABLE `company` 
	ADD primary key(companyid);
ALTER TABLE `company` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `containers` */

ALTER TABLE `containers` 
	CHANGE friendly_name friendly_name BLOB;
ALTER TABLE `containers` 
	CHANGE friendly_name friendly_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `containers` 
	CHANGE deleted deleted BLOB;
ALTER TABLE `containers` 
	CHANGE deleted deleted varchar(1) CHARACTER SET utf8;
ALTER TABLE `containers` 
	CHANGE show_on_menu show_on_menu BLOB;
ALTER TABLE `containers` 
	CHANGE show_on_menu show_on_menu varchar(1) CHARACTER SET utf8;
ALTER TABLE `containers` 
	CHANGE post_loop post_loop BLOB;
ALTER TABLE `containers` 
	CHANGE post_loop post_loop longtext CHARACTER SET utf8;
ALTER TABLE `containers` 
	CHANGE mod_user mod_user BLOB;
ALTER TABLE `containers` 
	CHANGE mod_user mod_user varchar(100) CHARACTER SET utf8;
ALTER TABLE `containers` 
	CHANGE sort_contentlets_by sort_contentlets_by BLOB;
ALTER TABLE `containers` 
	CHANGE sort_contentlets_by sort_contentlets_by varchar(255) CHARACTER SET utf8;
ALTER TABLE `containers` 
	CHANGE title title BLOB;
ALTER TABLE `containers` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `containers` 
	CHANGE notes notes BLOB;
ALTER TABLE `containers` 
	CHANGE notes notes varchar(255) CHARACTER SET utf8;
ALTER TABLE `containers` 
	CHANGE live live BLOB;
ALTER TABLE `containers` 
	CHANGE live live varchar(1) CHARACTER SET utf8;
ALTER TABLE `containers` 
	CHANGE staticify staticify BLOB;
ALTER TABLE `containers` 
	CHANGE staticify staticify varchar(1) CHARACTER SET utf8;
ALTER TABLE `containers` 
	CHANGE code code BLOB;
ALTER TABLE `containers` 
	CHANGE code code longtext CHARACTER SET utf8;
ALTER TABLE `containers` 
	CHANGE lucene_query lucene_query BLOB;
ALTER TABLE `containers` 
	CHANGE lucene_query lucene_query longtext CHARACTER SET utf8;
ALTER TABLE `containers` 
	CHANGE locked locked BLOB;
ALTER TABLE `containers` 
	CHANGE locked locked varchar(1) CHARACTER SET utf8;
ALTER TABLE `containers` 
	CHANGE pre_loop pre_loop BLOB;
ALTER TABLE `containers` 
	CHANGE pre_loop pre_loop longtext CHARACTER SET utf8;
ALTER TABLE `containers` 
	CHANGE use_div use_div BLOB;
ALTER TABLE `containers` 
	CHANGE use_div use_div varchar(1) CHARACTER SET utf8;
ALTER TABLE `containers` 
	CHANGE working working BLOB;
ALTER TABLE `containers` 
	CHANGE working working varchar(1) CHARACTER SET utf8;
ALTER TABLE `containers` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `content_rating` */

ALTER TABLE `content_rating` 
	CHANGE user_id user_id BLOB;
ALTER TABLE `content_rating` 
	CHANGE user_id user_id varchar(255) CHARACTER SET utf8;
ALTER TABLE `content_rating` 
	CHANGE session_id session_id BLOB;
ALTER TABLE `content_rating` 
	CHANGE session_id session_id varchar(255) CHARACTER SET utf8;
ALTER TABLE `content_rating` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `contentlet` */

ALTER TABLE `contentlet` 
	CHANGE text4 text4 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text4 text4 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE show_on_menu show_on_menu BLOB;
ALTER TABLE `contentlet` 
	CHANGE show_on_menu show_on_menu varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool18 bool18 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool18 bool18 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool17 bool17 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool17 bool17 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area5 text_area5 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area5 text_area5 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool20 bool20 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool20 bool20 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text24 text24 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text24 text24 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool21 bool21 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool21 bool21 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text25 text25 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text25 text25 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area6 text_area6 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area6 text_area6 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text12 text12 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text12 text12 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool19 bool19 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool19 bool19 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool24 bool24 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool24 bool24 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool16 bool16 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool16 bool16 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area13 text_area13 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area13 text_area13 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text13 text13 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text13 text13 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text14 text14 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text14 text14 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool25 bool25 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool25 bool25 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool15 bool15 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool15 bool15 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text16 text16 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text16 text16 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area21 text_area21 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area21 text_area21 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text11 text11 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text11 text11 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text15 text15 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text15 text15 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area22 text_area22 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area22 text_area22 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area2 text_area2 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area2 text_area2 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area23 text_area23 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area23 text_area23 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area12 text_area12 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area12 text_area12 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool8 bool8 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool8 bool8 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool5 bool5 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool5 bool5 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE locked locked BLOB;
ALTER TABLE `contentlet` 
	CHANGE locked locked varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE disabled_wysiwyg disabled_wysiwyg BLOB;
ALTER TABLE `contentlet` 
	CHANGE disabled_wysiwyg disabled_wysiwyg varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area17 text_area17 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area17 text_area17 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area10 text_area10 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area10 text_area10 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE friendly_name friendly_name BLOB;
ALTER TABLE `contentlet` 
	CHANGE friendly_name friendly_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text19 text19 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text19 text19 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area11 text_area11 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area11 text_area11 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area20 text_area20 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area20 text_area20 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area9 text_area9 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area9 text_area9 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool4 bool4 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool4 bool4 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area19 text_area19 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area19 text_area19 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool6 bool6 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool6 bool6 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool7 bool7 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool7 bool7 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool12 bool12 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool12 bool12 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area25 text_area25 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area25 text_area25 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text1 text1 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text1 text1 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area14 text_area14 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area14 text_area14 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text7 text7 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text7 text7 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text17 text17 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text17 text17 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool11 bool11 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool11 bool11 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text6 text6 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text6 text6 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area16 text_area16 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area16 text_area16 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE working working BLOB;
ALTER TABLE `contentlet` 
	CHANGE working working varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE deleted deleted BLOB;
ALTER TABLE `contentlet` 
	CHANGE deleted deleted varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area1 text_area1 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area1 text_area1 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area15 text_area15 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area15 text_area15 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool13 bool13 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool13 bool13 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text18 text18 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text18 text18 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE live live BLOB;
ALTER TABLE `contentlet` 
	CHANGE live live varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE review_interval review_interval BLOB;
ALTER TABLE `contentlet` 
	CHANGE review_interval review_interval varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text20 text20 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text20 text20 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text21 text21 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text21 text21 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool9 bool9 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool9 bool9 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool14 bool14 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool14 bool14 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text9 text9 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text9 text9 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE title title BLOB;
ALTER TABLE `contentlet` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area24 text_area24 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area24 text_area24 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area3 text_area3 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area3 text_area3 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text10 text10 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text10 text10 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area4 text_area4 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area4 text_area4 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool10 bool10 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool10 bool10 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE mod_user mod_user BLOB;
ALTER TABLE `contentlet` 
	CHANGE mod_user mod_user varchar(100) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool23 bool23 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool23 bool23 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text23 text23 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text23 text23 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool3 bool3 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool3 bool3 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text5 text5 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text5 text5 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area18 text_area18 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area18 text_area18 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text8 text8 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text8 text8 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool2 bool2 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool2 bool2 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area8 text_area8 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area8 text_area8 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool1 bool1 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool1 bool1 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text2 text2 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text2 text2 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text_area7 text_area7 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text_area7 text_area7 longtext CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE bool22 bool22 BLOB;
ALTER TABLE `contentlet` 
	CHANGE bool22 bool22 varchar(1) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text3 text3 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text3 text3 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	CHANGE text22 text22 BLOB;
ALTER TABLE `contentlet` 
	CHANGE text22 text22 varchar(255) CHARACTER SET utf8;
ALTER TABLE `contentlet` 
	DEFAULT CHARACTER SET utf8;



/* --> TABLE `counter` */

ALTER TABLE `counter` 
	DROP primary key;
ALTER TABLE `counter` 
	CHANGE name name BLOB;
ALTER TABLE `counter` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `counter` 
	ADD primary key(name);
ALTER TABLE `counter` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `cyrususer` */

ALTER TABLE `cyrususer` 
	DROP primary key;
ALTER TABLE `cyrususer` 
	CHANGE userid userid BLOB;
ALTER TABLE `cyrususer` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `cyrususer` 
	CHANGE password_ password_ BLOB;
ALTER TABLE `cyrususer` 
	CHANGE password_ password_ varchar(100) CHARACTER SET utf8;
ALTER TABLE `cyrususer` 
	ADD primary key(userid);
ALTER TABLE `cyrususer` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `cyrusvirtual` */

ALTER TABLE `cyrusvirtual` 
	DROP primary key;
ALTER TABLE `cyrusvirtual` 
	CHANGE userid userid BLOB;
ALTER TABLE `cyrusvirtual` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `cyrusvirtual` 
	CHANGE emailaddress emailaddress BLOB;
ALTER TABLE `cyrusvirtual` 
	CHANGE emailaddress emailaddress varchar(100) CHARACTER SET utf8;
ALTER TABLE `cyrusvirtual` 
	ADD primary key(emailaddress);
ALTER TABLE `cyrusvirtual` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `dlfileprofile` */

ALTER TABLE `dlfileprofile` 
	DROP primary key;
ALTER TABLE `dlfileprofile` 
	CHANGE readroles readroles BLOB;
ALTER TABLE `dlfileprofile` 
	CHANGE readroles readroles varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlfileprofile` 
	CHANGE filename filename BLOB;
ALTER TABLE `dlfileprofile` 
	CHANGE filename filename varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlfileprofile` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `dlfileprofile` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlfileprofile` 
	CHANGE userid userid BLOB;
ALTER TABLE `dlfileprofile` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlfileprofile` 
	CHANGE writeroles writeroles BLOB;
ALTER TABLE `dlfileprofile` 
	CHANGE writeroles writeroles varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlfileprofile` 
	CHANGE repositoryid repositoryid BLOB;
ALTER TABLE `dlfileprofile` 
	CHANGE repositoryid repositoryid varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlfileprofile` 
	CHANGE description description BLOB;
ALTER TABLE `dlfileprofile` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `dlfileprofile` 
	CHANGE username username BLOB;
ALTER TABLE `dlfileprofile` 
	CHANGE username username varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlfileprofile` 
	CHANGE versionuserid versionuserid BLOB;
ALTER TABLE `dlfileprofile` 
	CHANGE versionuserid versionuserid varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlfileprofile` 
	CHANGE versionusername versionusername BLOB;
ALTER TABLE `dlfileprofile` 
	CHANGE versionusername versionusername varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlfileprofile` 
	ADD primary key(companyid,repositoryid,filename);
ALTER TABLE `dlfileprofile` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `dlfilerank` */

ALTER TABLE `dlfilerank` 
	DROP primary key;
ALTER TABLE `dlfilerank` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `dlfilerank` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlfilerank` 
	CHANGE userid userid BLOB;
ALTER TABLE `dlfilerank` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlfilerank` 
	CHANGE repositoryid repositoryid BLOB;
ALTER TABLE `dlfilerank` 
	CHANGE repositoryid repositoryid varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlfilerank` 
	CHANGE filename filename BLOB;
ALTER TABLE `dlfilerank` 
	CHANGE filename filename varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlfilerank` 
	ADD primary key(companyid,userid,repositoryid,filename);
ALTER TABLE `dlfilerank` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `dlfileversion` */

ALTER TABLE `dlfileversion` 
	DROP primary key;
ALTER TABLE `dlfileversion` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `dlfileversion` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlfileversion` 
	CHANGE userid userid BLOB;
ALTER TABLE `dlfileversion` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlfileversion` 
	CHANGE repositoryid repositoryid BLOB;
ALTER TABLE `dlfileversion` 
	CHANGE repositoryid repositoryid varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlfileversion` 
	CHANGE username username BLOB;
ALTER TABLE `dlfileversion` 
	CHANGE username username varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlfileversion` 
	CHANGE filename filename BLOB;
ALTER TABLE `dlfileversion` 
	CHANGE filename filename varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlfileversion` 
	ADD primary key(companyid,repositoryid,filename,version);
ALTER TABLE `dlfileversion` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `dlrepository` */

ALTER TABLE `dlrepository` 
	DROP primary key;
ALTER TABLE `dlrepository` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `dlrepository` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlrepository` 
	CHANGE groupid groupid BLOB;
ALTER TABLE `dlrepository` 
	CHANGE groupid groupid varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlrepository` 
	CHANGE userid userid BLOB;
ALTER TABLE `dlrepository` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlrepository` 
	CHANGE readroles readroles BLOB;
ALTER TABLE `dlrepository` 
	CHANGE readroles readroles varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlrepository` 
	CHANGE writeroles writeroles BLOB;
ALTER TABLE `dlrepository` 
	CHANGE writeroles writeroles varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlrepository` 
	CHANGE description description BLOB;
ALTER TABLE `dlrepository` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `dlrepository` 
	CHANGE repositoryid repositoryid BLOB;
ALTER TABLE `dlrepository` 
	CHANGE repositoryid repositoryid varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlrepository` 
	CHANGE username username BLOB;
ALTER TABLE `dlrepository` 
	CHANGE username username varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlrepository` 
	CHANGE name name BLOB;
ALTER TABLE `dlrepository` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `dlrepository` 
	ADD primary key(repositoryid);
ALTER TABLE `dlrepository` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `ecom_discount_code` */

ALTER TABLE `ecom_discount_code` 
	DROP INDEX uk_discount_code_id;
ALTER TABLE `ecom_discount_code` 
	CHANGE code_description code_description BLOB;
ALTER TABLE `ecom_discount_code` 
	CHANGE code_description code_description varchar(100) CHARACTER SET utf8;
ALTER TABLE `ecom_discount_code` 
	CHANGE no_bulk_disc no_bulk_disc BLOB;
ALTER TABLE `ecom_discount_code` 
	CHANGE no_bulk_disc no_bulk_disc varchar(1) CHARACTER SET utf8;
ALTER TABLE `ecom_discount_code` 
	CHANGE code_id code_id BLOB;
ALTER TABLE `ecom_discount_code` 
	CHANGE code_id code_id varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_discount_code` 
	CHANGE free_shipping free_shipping BLOB;
ALTER TABLE `ecom_discount_code` 
	CHANGE free_shipping free_shipping varchar(1) CHARACTER SET utf8;
ALTER TABLE `ecom_discount_code` 
	ADD INDEX uk_discount_code_id(code_id);
ALTER TABLE `ecom_discount_code` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `ecom_order` */

ALTER TABLE `ecom_order` 
	CHANGE card_type card_type BLOB;
ALTER TABLE `ecom_order` 
	CHANGE card_type card_type varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE billing_contact_email billing_contact_email BLOB;
ALTER TABLE `ecom_order` 
	CHANGE billing_contact_email billing_contact_email varchar(100) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE modified_fh modified_fh BLOB;
ALTER TABLE `ecom_order` 
	CHANGE modified_fh modified_fh varchar(1) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE invoice_number invoice_number BLOB;
ALTER TABLE `ecom_order` 
	CHANGE invoice_number invoice_number varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE shipping_zip shipping_zip BLOB;
ALTER TABLE `ecom_order` 
	CHANGE shipping_zip shipping_zip varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE check_bank_name check_bank_name BLOB;
ALTER TABLE `ecom_order` 
	CHANGE check_bank_name check_bank_name varchar(100) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE shipping_fax shipping_fax BLOB;
ALTER TABLE `ecom_order` 
	CHANGE shipping_fax shipping_fax varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE shipping_country shipping_country BLOB;
ALTER TABLE `ecom_order` 
	CHANGE shipping_country shipping_country varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE billing_fax billing_fax BLOB;
ALTER TABLE `ecom_order` 
	CHANGE billing_fax billing_fax varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE billing_address2 billing_address2 BLOB;
ALTER TABLE `ecom_order` 
	CHANGE billing_address2 billing_address2 varchar(255) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE shipping_label shipping_label BLOB;
ALTER TABLE `ecom_order` 
	CHANGE shipping_label shipping_label varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE backend_user backend_user BLOB;
ALTER TABLE `ecom_order` 
	CHANGE backend_user backend_user varchar(100) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE payment_type payment_type BLOB;
ALTER TABLE `ecom_order` 
	CHANGE payment_type payment_type varchar(10) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE billing_last_name billing_last_name BLOB;
ALTER TABLE `ecom_order` 
	CHANGE billing_last_name billing_last_name varchar(100) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE billing_state billing_state BLOB;
ALTER TABLE `ecom_order` 
	CHANGE billing_state billing_state varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE shipping_address1 shipping_address1 BLOB;
ALTER TABLE `ecom_order` 
	CHANGE shipping_address1 shipping_address1 varchar(255) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE billing_contact_phone billing_contact_phone BLOB;
ALTER TABLE `ecom_order` 
	CHANGE billing_contact_phone billing_contact_phone varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE tracking_number tracking_number BLOB;
ALTER TABLE `ecom_order` 
	CHANGE tracking_number tracking_number varchar(255) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE name_on_card name_on_card BLOB;
ALTER TABLE `ecom_order` 
	CHANGE name_on_card name_on_card varchar(100) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE modified_qb modified_qb BLOB;
ALTER TABLE `ecom_order` 
	CHANGE modified_qb modified_qb varchar(1) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE billing_city billing_city BLOB;
ALTER TABLE `ecom_order` 
	CHANGE billing_city billing_city varchar(100) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE shipping_phone shipping_phone BLOB;
ALTER TABLE `ecom_order` 
	CHANGE shipping_phone shipping_phone varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE discount_codes discount_codes BLOB;
ALTER TABLE `ecom_order` 
	CHANGE discount_codes discount_codes varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE billing_country billing_country BLOB;
ALTER TABLE `ecom_order` 
	CHANGE billing_country billing_country varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE shipping_city shipping_city BLOB;
ALTER TABLE `ecom_order` 
	CHANGE shipping_city shipping_city varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE po_number po_number BLOB;
ALTER TABLE `ecom_order` 
	CHANGE po_number po_number varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE card_number card_number BLOB;
ALTER TABLE `ecom_order` 
	CHANGE card_number card_number varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE billing_zip billing_zip BLOB;
ALTER TABLE `ecom_order` 
	CHANGE billing_zip billing_zip varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE check_number check_number BLOB;
ALTER TABLE `ecom_order` 
	CHANGE check_number check_number varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE shipping_state shipping_state BLOB;
ALTER TABLE `ecom_order` 
	CHANGE shipping_state shipping_state varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE shipping_address2 shipping_address2 BLOB;
ALTER TABLE `ecom_order` 
	CHANGE shipping_address2 shipping_address2 varchar(255) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE card_verification_value card_verification_value BLOB;
ALTER TABLE `ecom_order` 
	CHANGE card_verification_value card_verification_value varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE billing_address1 billing_address1 BLOB;
ALTER TABLE `ecom_order` 
	CHANGE billing_address1 billing_address1 varchar(255) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE billing_contact_name billing_contact_name BLOB;
ALTER TABLE `ecom_order` 
	CHANGE billing_contact_name billing_contact_name varchar(100) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE billing_first_name billing_first_name BLOB;
ALTER TABLE `ecom_order` 
	CHANGE billing_first_name billing_first_name varchar(100) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE tax_exempt_number tax_exempt_number BLOB;
ALTER TABLE `ecom_order` 
	CHANGE tax_exempt_number tax_exempt_number varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	CHANGE billing_phone billing_phone BLOB;
ALTER TABLE `ecom_order` 
	CHANGE billing_phone billing_phone varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_order` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `ecom_order_item` */

ALTER TABLE `ecom_order_item` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `ecom_product` */

ALTER TABLE `ecom_product` 
	CHANGE comments comments BLOB;
ALTER TABLE `ecom_product` 
	CHANGE comments comments varchar(255) CHARACTER SET utf8;
ALTER TABLE `ecom_product` 
	CHANGE req_shipping req_shipping BLOB;
ALTER TABLE `ecom_product` 
	CHANGE req_shipping req_shipping varchar(1) CHARACTER SET utf8;
ALTER TABLE `ecom_product` 
	CHANGE showonweb showonweb BLOB;
ALTER TABLE `ecom_product` 
	CHANGE showonweb showonweb varchar(1) CHARACTER SET utf8;
ALTER TABLE `ecom_product` 
	CHANGE long_description long_description BLOB;
ALTER TABLE `ecom_product` 
	CHANGE long_description long_description varchar(255) CHARACTER SET utf8;
ALTER TABLE `ecom_product` 
	CHANGE featured featured BLOB;
ALTER TABLE `ecom_product` 
	CHANGE featured featured varchar(1) CHARACTER SET utf8;
ALTER TABLE `ecom_product` 
	CHANGE title title BLOB;
ALTER TABLE `ecom_product` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `ecom_product` 
	CHANGE short_description short_description BLOB;
ALTER TABLE `ecom_product` 
	CHANGE short_description short_description varchar(255) CHARACTER SET utf8;
ALTER TABLE `ecom_product` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `ecom_product_format` */

ALTER TABLE `ecom_product_format` 
	CHANGE format_name format_name BLOB;
ALTER TABLE `ecom_product_format` 
	CHANGE format_name format_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `ecom_product_format` 
	CHANGE format format BLOB;
ALTER TABLE `ecom_product_format` 
	CHANGE format format varchar(100) CHARACTER SET utf8;
ALTER TABLE `ecom_product_format` 
	CHANGE item_num item_num BLOB;
ALTER TABLE `ecom_product_format` 
	CHANGE item_num item_num varchar(50) CHARACTER SET utf8;
ALTER TABLE `ecom_product_format` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `ecom_product_price` */

ALTER TABLE `ecom_product_price` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `entity` */

ALTER TABLE `entity` 
	DROP INDEX idx_entity_1;
ALTER TABLE `entity` 
	CHANGE entity_name entity_name BLOB;
ALTER TABLE `entity` 
	CHANGE entity_name entity_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `entity` 
	ADD INDEX idx_entity_1(entity_name);
ALTER TABLE `entity` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `event` */

ALTER TABLE `event` 
	CHANGE received_adm_approval received_adm_approval BLOB;
ALTER TABLE `event` 
	CHANGE received_adm_approval received_adm_approval varchar(1) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE phone phone BLOB;
ALTER TABLE `event` 
	CHANGE phone phone varchar(64) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE title title BLOB;
ALTER TABLE `event` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE comments_equipment comments_equipment BLOB;
ALTER TABLE `event` 
	CHANGE comments_equipment comments_equipment longtext CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE address1 address1 BLOB;
ALTER TABLE `event` 
	CHANGE address1 address1 varchar(255) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE fax fax BLOB;
ALTER TABLE `event` 
	CHANGE fax fax varchar(64) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE contact_fax contact_fax BLOB;
ALTER TABLE `event` 
	CHANGE contact_fax contact_fax varchar(255) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE web_address web_address BLOB;
ALTER TABLE `event` 
	CHANGE web_address web_address varchar(255) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE address2 address2 BLOB;
ALTER TABLE `event` 
	CHANGE address2 address2 varchar(255) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE url url BLOB;
ALTER TABLE `event` 
	CHANGE url url varchar(255) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE zip zip BLOB;
ALTER TABLE `event` 
	CHANGE zip zip varchar(32) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE location location BLOB;
ALTER TABLE `event` 
	CHANGE location location varchar(255) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE email_response email_response BLOB;
ALTER TABLE `event` 
	CHANGE email_response email_response longtext CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE city city BLOB;
ALTER TABLE `event` 
	CHANGE city city varchar(255) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE show_public show_public BLOB;
ALTER TABLE `event` 
	CHANGE show_public show_public varchar(1) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE contact_email contact_email BLOB;
ALTER TABLE `event` 
	CHANGE contact_email contact_email varchar(255) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE contact_name contact_name BLOB;
ALTER TABLE `event` 
	CHANGE contact_name contact_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE registration registration BLOB;
ALTER TABLE `event` 
	CHANGE registration registration varchar(1) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE directions directions BLOB;
ALTER TABLE `event` 
	CHANGE directions directions longtext CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE include_file include_file BLOB;
ALTER TABLE `event` 
	CHANGE include_file include_file varchar(255) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE contact_company contact_company BLOB;
ALTER TABLE `event` 
	CHANGE contact_company contact_company varchar(255) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE contact_phone contact_phone BLOB;
ALTER TABLE `event` 
	CHANGE contact_phone contact_phone varchar(255) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE user_id user_id BLOB;
ALTER TABLE `event` 
	CHANGE user_id user_id varchar(255) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE country country BLOB;
ALTER TABLE `event` 
	CHANGE country country varchar(64) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE time_tbd time_tbd BLOB;
ALTER TABLE `event` 
	CHANGE time_tbd time_tbd varchar(1) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE featured featured BLOB;
ALTER TABLE `event` 
	CHANGE featured featured varchar(1) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE description description BLOB;
ALTER TABLE `event` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE state state BLOB;
ALTER TABLE `event` 
	CHANGE state state varchar(255) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE address3 address3 BLOB;
ALTER TABLE `event` 
	CHANGE address3 address3 varchar(255) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE email email BLOB;
ALTER TABLE `event` 
	CHANGE email email varchar(64) CHARACTER SET utf8;
ALTER TABLE `event` 
	CHANGE subtitle subtitle BLOB;
ALTER TABLE `event` 
	CHANGE subtitle subtitle varchar(255) CHARACTER SET utf8;
ALTER TABLE `event` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `event_registration` */

ALTER TABLE `event_registration` 
	CHANGE comments comments BLOB;
ALTER TABLE `event_registration` 
	CHANGE comments comments longtext CHARACTER SET utf8;
ALTER TABLE `event_registration` 
	CHANGE full_name full_name BLOB;
ALTER TABLE `event_registration` 
	CHANGE full_name full_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `event_registration` 
	CHANGE email email BLOB;
ALTER TABLE `event_registration` 
	CHANGE email email varchar(255) CHARACTER SET utf8;
ALTER TABLE `event_registration` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `facility` */

ALTER TABLE `facility` 
	CHANGE active active BLOB;
ALTER TABLE `facility` 
	CHANGE active active varchar(1) CHARACTER SET utf8;
ALTER TABLE `facility` 
	CHANGE facility_description facility_description BLOB;
ALTER TABLE `facility` 
	CHANGE facility_description facility_description varchar(255) CHARACTER SET utf8;
ALTER TABLE `facility` 
	CHANGE facility_name facility_name BLOB;
ALTER TABLE `facility` 
	CHANGE facility_name facility_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `facility` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `field` */

ALTER TABLE `field` 
	CHANGE listed listed BLOB;
ALTER TABLE `field` 
	CHANGE listed listed varchar(1) CHARACTER SET utf8;
ALTER TABLE `field` 
	CHANGE field_name field_name BLOB;
ALTER TABLE `field` 
	CHANGE field_name field_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `field` 
	CHANGE required required BLOB;
ALTER TABLE `field` 
	CHANGE required required varchar(1) CHARACTER SET utf8;
ALTER TABLE `field` 
	CHANGE default_value default_value BLOB;
ALTER TABLE `field` 
	CHANGE default_value default_value varchar(255) CHARACTER SET utf8;
ALTER TABLE `field` 
	CHANGE hint hint BLOB;
ALTER TABLE `field` 
	CHANGE hint hint varchar(255) CHARACTER SET utf8;
ALTER TABLE `field` 
	CHANGE field_values field_values BLOB;
ALTER TABLE `field` 
	CHANGE field_values field_values longtext CHARACTER SET utf8;
ALTER TABLE `field` 
	CHANGE indexed indexed BLOB;
ALTER TABLE `field` 
	CHANGE indexed indexed varchar(1) CHARACTER SET utf8;
ALTER TABLE `field` 
	CHANGE velocity_var_name velocity_var_name BLOB;
ALTER TABLE `field` 
	CHANGE velocity_var_name velocity_var_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `field` 
	CHANGE field_type field_type BLOB;
ALTER TABLE `field` 
	CHANGE field_type field_type varchar(255) CHARACTER SET utf8;
ALTER TABLE `field` 
	CHANGE regex_check regex_check BLOB;
ALTER TABLE `field` 
	CHANGE regex_check regex_check varchar(255) CHARACTER SET utf8;
ALTER TABLE `field` 
	CHANGE field_contentlet field_contentlet BLOB;
ALTER TABLE `field` 
	CHANGE field_contentlet field_contentlet varchar(255) CHARACTER SET utf8;
ALTER TABLE `field` 
	CHANGE field_relation_type field_relation_type BLOB;
ALTER TABLE `field` 
	CHANGE field_relation_type field_relation_type varchar(255) CHARACTER SET utf8;
ALTER TABLE `field` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `file_asset` */

ALTER TABLE `file_asset` 
	DROP INDEX idx_file_1;
ALTER TABLE `file_asset` 
	CHANGE friendly_name friendly_name BLOB;
ALTER TABLE `file_asset` 
	CHANGE friendly_name friendly_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `file_asset` 
	CHANGE deleted deleted BLOB;
ALTER TABLE `file_asset` 
	CHANGE deleted deleted varchar(1) CHARACTER SET utf8;
ALTER TABLE `file_asset` 
	CHANGE show_on_menu show_on_menu BLOB;
ALTER TABLE `file_asset` 
	CHANGE show_on_menu show_on_menu varchar(1) CHARACTER SET utf8;
ALTER TABLE `file_asset` 
	CHANGE mod_user mod_user BLOB;
ALTER TABLE `file_asset` 
	CHANGE mod_user mod_user varchar(255) CHARACTER SET utf8;
ALTER TABLE `file_asset` 
	CHANGE title title BLOB;
ALTER TABLE `file_asset` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `file_asset` 
	CHANGE mime_type mime_type BLOB;
ALTER TABLE `file_asset` 
	CHANGE mime_type mime_type varchar(255) CHARACTER SET utf8;
ALTER TABLE `file_asset` 
	CHANGE live live BLOB;
ALTER TABLE `file_asset` 
	CHANGE live live varchar(1) CHARACTER SET utf8;
ALTER TABLE `file_asset` 
	CHANGE locked locked BLOB;
ALTER TABLE `file_asset` 
	CHANGE locked locked varchar(1) CHARACTER SET utf8;
ALTER TABLE `file_asset` 
	CHANGE file_name file_name BLOB;
ALTER TABLE `file_asset` 
	CHANGE file_name file_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `file_asset` 
	CHANGE author author BLOB;
ALTER TABLE `file_asset` 
	CHANGE author author varchar(255) CHARACTER SET utf8;
ALTER TABLE `file_asset` 
	CHANGE working working BLOB;
ALTER TABLE `file_asset` 
	CHANGE working working varchar(1) CHARACTER SET utf8;
ALTER TABLE `file_asset` 
	ADD INDEX idx_file_1(mod_user);
ALTER TABLE `file_asset` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `folder` */

ALTER TABLE `folder` 
	DROP INDEX idx_folder_1;
ALTER TABLE `folder` 
	CHANGE files_masks files_masks BLOB;
ALTER TABLE `folder` 
	CHANGE files_masks files_masks varchar(255) CHARACTER SET utf8;
ALTER TABLE `folder` 
	CHANGE show_on_menu show_on_menu BLOB;
ALTER TABLE `folder` 
	CHANGE show_on_menu show_on_menu varchar(1) CHARACTER SET utf8;
ALTER TABLE `folder` 
	CHANGE title title BLOB;
ALTER TABLE `folder` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `folder` 
	CHANGE path path BLOB;
ALTER TABLE `folder` 
	CHANGE path path varchar(255) CHARACTER SET utf8;
ALTER TABLE `folder` 
	CHANGE name name BLOB;
ALTER TABLE `folder` 
	CHANGE name name varchar(255) CHARACTER SET utf8;
ALTER TABLE `folder` 
	ADD INDEX idx_folder_1(name);
ALTER TABLE `folder` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `group_` */

ALTER TABLE `group_` 
	DROP primary key;
ALTER TABLE `group_` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `group_` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `group_` 
	CHANGE layoutids layoutids BLOB;
ALTER TABLE `group_` 
	CHANGE layoutids layoutids varchar(100) CHARACTER SET utf8;
ALTER TABLE `group_` 
	CHANGE groupid groupid BLOB;
ALTER TABLE `group_` 
	CHANGE groupid groupid varchar(100) CHARACTER SET utf8;
ALTER TABLE `group_` 
	CHANGE name name BLOB;
ALTER TABLE `group_` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `group_` 
	CHANGE parentgroupid parentgroupid BLOB;
ALTER TABLE `group_` 
	CHANGE parentgroupid parentgroupid varchar(100) CHARACTER SET utf8;
ALTER TABLE `group_` 
	ADD primary key(groupid);
ALTER TABLE `group_` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `groups_roles` */

ALTER TABLE `groups_roles` 
	CHANGE groupid groupid BLOB;
ALTER TABLE `groups_roles` 
	CHANGE groupid groupid varchar(100) CHARACTER SET utf8;
ALTER TABLE `groups_roles` 
	CHANGE roleid roleid BLOB;
ALTER TABLE `groups_roles` 
	CHANGE roleid roleid varchar(100) CHARACTER SET utf8;
ALTER TABLE `groups_roles` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `host` */

ALTER TABLE `host` 
	DROP INDEX idx_host_1;
ALTER TABLE `host` 
	CHANGE is_default is_default BLOB;
ALTER TABLE `host` 
	CHANGE is_default is_default varchar(1) CHARACTER SET utf8;
ALTER TABLE `host` 
	CHANGE hostname hostname BLOB;
ALTER TABLE `host` 
	CHANGE hostname hostname varchar(255) CHARACTER SET utf8;
ALTER TABLE `host` 
	CHANGE aliases aliases BLOB;
ALTER TABLE `host` 
	CHANGE aliases aliases longtext CHARACTER SET utf8;
ALTER TABLE `host` 
	ADD INDEX idx_host_1(hostname);
ALTER TABLE `host` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `htmlpage` */

ALTER TABLE `htmlpage` 
	CHANGE deleted deleted BLOB;
ALTER TABLE `htmlpage` 
	CHANGE deleted deleted varchar(1) CHARACTER SET utf8;
ALTER TABLE `htmlpage` 
	CHANGE friendly_name friendly_name BLOB;
ALTER TABLE `htmlpage` 
	CHANGE friendly_name friendly_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `htmlpage` 
	CHANGE show_on_menu show_on_menu BLOB;
ALTER TABLE `htmlpage` 
	CHANGE show_on_menu show_on_menu varchar(1) CHARACTER SET utf8;
ALTER TABLE `htmlpage` 
	CHANGE page_url page_url BLOB;
ALTER TABLE `htmlpage` 
	CHANGE page_url page_url varchar(255) CHARACTER SET utf8;
ALTER TABLE `htmlpage` 
	CHANGE mod_user mod_user BLOB;
ALTER TABLE `htmlpage` 
	CHANGE mod_user mod_user varchar(100) CHARACTER SET utf8;
ALTER TABLE `htmlpage` 
	CHANGE title title BLOB;
ALTER TABLE `htmlpage` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `htmlpage` 
	CHANGE metadata metadata BLOB;
ALTER TABLE `htmlpage` 
	CHANGE metadata metadata longtext CHARACTER SET utf8;
ALTER TABLE `htmlpage` 
	CHANGE https_required https_required BLOB;
ALTER TABLE `htmlpage` 
	CHANGE https_required https_required varchar(1) CHARACTER SET utf8;
ALTER TABLE `htmlpage` 
	CHANGE live live BLOB;
ALTER TABLE `htmlpage` 
	CHANGE live live varchar(1) CHARACTER SET utf8;
ALTER TABLE `htmlpage` 
	CHANGE locked locked BLOB;
ALTER TABLE `htmlpage` 
	CHANGE locked locked varchar(1) CHARACTER SET utf8;
ALTER TABLE `htmlpage` 
	CHANGE redirect redirect BLOB;
ALTER TABLE `htmlpage` 
	CHANGE redirect redirect varchar(255) CHARACTER SET utf8;
ALTER TABLE `htmlpage` 
	CHANGE working working BLOB;
ALTER TABLE `htmlpage` 
	CHANGE working working varchar(1) CHARACTER SET utf8;
ALTER TABLE `htmlpage` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `identifier` */

ALTER TABLE `identifier` 
	DROP INDEX uri;
ALTER TABLE `identifier` 
	CHANGE uri uri BLOB;
ALTER TABLE `identifier` 
	CHANGE uri uri varchar(255) CHARACTER SET utf8;
ALTER TABLE `identifier`
	ADD UNIQUE INDEX uri(uri,host_inode);
ALTER TABLE `identifier` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `igfolder` */

ALTER TABLE `igfolder` 
	DROP primary key;
ALTER TABLE `igfolder` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `igfolder` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `igfolder` 
	CHANGE groupid groupid BLOB;
ALTER TABLE `igfolder` 
	CHANGE groupid groupid varchar(100) CHARACTER SET utf8;
ALTER TABLE `igfolder` 
	CHANGE userid userid BLOB;
ALTER TABLE `igfolder` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `igfolder` 
	CHANGE folderid folderid BLOB;
ALTER TABLE `igfolder` 
	CHANGE folderid folderid varchar(100) CHARACTER SET utf8;
ALTER TABLE `igfolder` 
	CHANGE parentfolderid parentfolderid BLOB;
ALTER TABLE `igfolder` 
	CHANGE parentfolderid parentfolderid varchar(100) CHARACTER SET utf8;
ALTER TABLE `igfolder` 
	CHANGE name name BLOB;
ALTER TABLE `igfolder` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `igfolder` 
	ADD primary key(folderid);
ALTER TABLE `igfolder` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `igimage` */

ALTER TABLE `igimage` 
	DROP primary key;
ALTER TABLE `igimage` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `igimage` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `igimage` 
	CHANGE userid userid BLOB;
ALTER TABLE `igimage` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `igimage` 
	CHANGE folderid folderid BLOB;
ALTER TABLE `igimage` 
	CHANGE folderid folderid varchar(100) CHARACTER SET utf8;
ALTER TABLE `igimage` 
	CHANGE description description BLOB;
ALTER TABLE `igimage` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `igimage` 
	CHANGE imageid imageid BLOB;
ALTER TABLE `igimage` 
	CHANGE imageid imageid varchar(100) CHARACTER SET utf8;
ALTER TABLE `igimage` 
	ADD primary key(imageid,companyid);
ALTER TABLE `igimage` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `image` */

ALTER TABLE `image` 
	DROP primary key;
ALTER TABLE `image` 
	CHANGE text_ text_ BLOB;
ALTER TABLE `image` 
	CHANGE text_ text_ longtext CHARACTER SET utf8;
ALTER TABLE `image` 
	CHANGE imageid imageid BLOB;
ALTER TABLE `image` 
	CHANGE imageid imageid varchar(200) CHARACTER SET utf8;
ALTER TABLE `image` 
	ADD primary key(imageid);
ALTER TABLE `image` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `indexation` */

ALTER TABLE `indexation` 
	DROP INDEX idx_indexation_1;
ALTER TABLE `indexation` 
	CHANGE server_id server_id BLOB;
ALTER TABLE `indexation` 
	CHANGE server_id server_id varchar(255) CHARACTER SET utf8;
ALTER TABLE `indexation` 
	ADD INDEX idx_indexation_1(server_id);
ALTER TABLE `indexation` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `inode` */

ALTER TABLE `inode` 
	DROP INDEX idx_index_1;
ALTER TABLE `inode` 
	CHANGE type type BLOB;
ALTER TABLE `inode` 
	CHANGE type type varchar(64) CHARACTER SET utf8;
ALTER TABLE `inode` 
	ADD INDEX idx_index_1(type);
ALTER TABLE `inode` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `journalarticle` */

ALTER TABLE `journalarticle` 
	DROP primary key;
ALTER TABLE `journalarticle` 
	CHANGE approvedbyuserid approvedbyuserid BLOB;
ALTER TABLE `journalarticle` 
	CHANGE approvedbyuserid approvedbyuserid varchar(100) CHARACTER SET utf8;
ALTER TABLE `journalarticle` 
	CHANGE groupid groupid BLOB;
ALTER TABLE `journalarticle` 
	CHANGE groupid groupid varchar(100) CHARACTER SET utf8;
ALTER TABLE `journalarticle` 
	CHANGE type_ type_ BLOB;
ALTER TABLE `journalarticle` 
	CHANGE type_ type_ varchar(100) CHARACTER SET utf8;
ALTER TABLE `journalarticle` 
	CHANGE title title BLOB;
ALTER TABLE `journalarticle` 
	CHANGE title title varchar(100) CHARACTER SET utf8;
ALTER TABLE `journalarticle` 
	CHANGE content content BLOB;
ALTER TABLE `journalarticle` 
	CHANGE content content longtext CHARACTER SET utf8;
ALTER TABLE `journalarticle` 
	CHANGE portletid portletid BLOB;
ALTER TABLE `journalarticle` 
	CHANGE portletid portletid varchar(100) CHARACTER SET utf8;
ALTER TABLE `journalarticle` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `journalarticle` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `journalarticle` 
	CHANGE structureid structureid BLOB;
ALTER TABLE `journalarticle` 
	CHANGE structureid structureid varchar(100) CHARACTER SET utf8;
ALTER TABLE `journalarticle` 
	CHANGE templateid templateid BLOB;
ALTER TABLE `journalarticle` 
	CHANGE templateid templateid varchar(100) CHARACTER SET utf8;
ALTER TABLE `journalarticle` 
	CHANGE userid userid BLOB;
ALTER TABLE `journalarticle` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `journalarticle` 
	CHANGE articleid articleid BLOB;
ALTER TABLE `journalarticle` 
	CHANGE articleid articleid varchar(100) CHARACTER SET utf8;
ALTER TABLE `journalarticle` 
	CHANGE approvedbyusername approvedbyusername BLOB;
ALTER TABLE `journalarticle` 
	CHANGE approvedbyusername approvedbyusername varchar(100) CHARACTER SET utf8;
ALTER TABLE `journalarticle` 
	CHANGE username username BLOB;
ALTER TABLE `journalarticle` 
	CHANGE username username varchar(100) CHARACTER SET utf8;
ALTER TABLE `journalarticle` 
	ADD primary key(articleid,version);
ALTER TABLE `journalarticle` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `journalstructure` */

ALTER TABLE `journalstructure` 
	DROP primary key;
ALTER TABLE `journalstructure` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `journalstructure` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `journalstructure` 
	CHANGE structureid structureid BLOB;
ALTER TABLE `journalstructure` 
	CHANGE structureid structureid varchar(100) CHARACTER SET utf8;
ALTER TABLE `journalstructure` 
	CHANGE groupid groupid BLOB;
ALTER TABLE `journalstructure` 
	CHANGE groupid groupid varchar(100) CHARACTER SET utf8;
ALTER TABLE `journalstructure` 
	CHANGE userid userid BLOB;
ALTER TABLE `journalstructure` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `journalstructure` 
	CHANGE xsd xsd BLOB;
ALTER TABLE `journalstructure` 
	CHANGE xsd xsd longtext CHARACTER SET utf8;
ALTER TABLE `journalstructure` 
	CHANGE description description BLOB;
ALTER TABLE `journalstructure` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `journalstructure` 
	CHANGE username username BLOB;
ALTER TABLE `journalstructure` 
	CHANGE username username varchar(100) CHARACTER SET utf8;
ALTER TABLE `journalstructure` 
	CHANGE name name BLOB;
ALTER TABLE `journalstructure` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `journalstructure` 
	CHANGE portletid portletid BLOB;
ALTER TABLE `journalstructure` 
	CHANGE portletid portletid varchar(100) CHARACTER SET utf8;
ALTER TABLE `journalstructure` 
	ADD primary key(structureid);
ALTER TABLE `journalstructure` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `journaltemplate` */

ALTER TABLE `journaltemplate` 
	DROP primary key;
ALTER TABLE `journaltemplate` 
	CHANGE groupid groupid BLOB;
ALTER TABLE `journaltemplate` 
	CHANGE groupid groupid varchar(100) CHARACTER SET utf8;
ALTER TABLE `journaltemplate` 
	CHANGE portletid portletid BLOB;
ALTER TABLE `journaltemplate` 
	CHANGE portletid portletid varchar(100) CHARACTER SET utf8;
ALTER TABLE `journaltemplate` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `journaltemplate` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `journaltemplate` 
	CHANGE structureid structureid BLOB;
ALTER TABLE `journaltemplate` 
	CHANGE structureid structureid varchar(100) CHARACTER SET utf8;
ALTER TABLE `journaltemplate` 
	CHANGE templateid templateid BLOB;
ALTER TABLE `journaltemplate` 
	CHANGE templateid templateid varchar(100) CHARACTER SET utf8;
ALTER TABLE `journaltemplate` 
	CHANGE userid userid BLOB;
ALTER TABLE `journaltemplate` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `journaltemplate` 
	CHANGE smallimageurl smallimageurl BLOB;
ALTER TABLE `journaltemplate` 
	CHANGE smallimageurl smallimageurl varchar(100) CHARACTER SET utf8;
ALTER TABLE `journaltemplate` 
	CHANGE description description BLOB;
ALTER TABLE `journaltemplate` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `journaltemplate` 
	CHANGE username username BLOB;
ALTER TABLE `journaltemplate` 
	CHANGE username username varchar(100) CHARACTER SET utf8;
ALTER TABLE `journaltemplate` 
	CHANGE xsl xsl BLOB;
ALTER TABLE `journaltemplate` 
	CHANGE xsl xsl longtext CHARACTER SET utf8;
ALTER TABLE `journaltemplate` 
	CHANGE name name BLOB;
ALTER TABLE `journaltemplate` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `journaltemplate` 
	ADD primary key(templateid);
ALTER TABLE `journaltemplate` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `language` */

ALTER TABLE `language` 
	CHANGE country country BLOB;
ALTER TABLE `language` 
	CHANGE country country varchar(255) CHARACTER SET utf8;
ALTER TABLE `language` 
	CHANGE language_code language_code BLOB;
ALTER TABLE `language` 
	CHANGE language_code language_code varchar(5) CHARACTER SET utf8;
ALTER TABLE `language` 
	CHANGE language language BLOB;
ALTER TABLE `language` 
	CHANGE language language varchar(255) CHARACTER SET utf8;
ALTER TABLE `language` 
	CHANGE country_code country_code BLOB;
ALTER TABLE `language` 
	CHANGE country_code country_code varchar(255) CHARACTER SET utf8;
ALTER TABLE `language` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `layer` */

ALTER TABLE `layer` 
	DROP primary key;
ALTER TABLE `layer` 
	CHANGE hrefhover hrefhover BLOB;
ALTER TABLE `layer` 
	CHANGE hrefhover hrefhover varchar(100) CHARACTER SET utf8;
ALTER TABLE `layer` 
	CHANGE layerid layerid BLOB;
ALTER TABLE `layer` 
	CHANGE layerid layerid varchar(100) CHARACTER SET utf8;
ALTER TABLE `layer` 
	CHANGE href href BLOB;
ALTER TABLE `layer` 
	CHANGE href href varchar(100) CHARACTER SET utf8;
ALTER TABLE `layer` 
	CHANGE background background BLOB;
ALTER TABLE `layer` 
	CHANGE background background varchar(100) CHARACTER SET utf8;
ALTER TABLE `layer` 
	CHANGE negalert negalert BLOB;
ALTER TABLE `layer` 
	CHANGE negalert negalert varchar(100) CHARACTER SET utf8;
ALTER TABLE `layer` 
	CHANGE foreground foreground BLOB;
ALTER TABLE `layer` 
	CHANGE foreground foreground varchar(100) CHARACTER SET utf8;
ALTER TABLE `layer` 
	CHANGE skinid skinid BLOB;
ALTER TABLE `layer` 
	CHANGE skinid skinid varchar(100) CHARACTER SET utf8;
ALTER TABLE `layer` 
	CHANGE posalert posalert BLOB;
ALTER TABLE `layer` 
	CHANGE posalert posalert varchar(100) CHARACTER SET utf8;
ALTER TABLE `layer` 
	ADD primary key(layerid,skinid);
ALTER TABLE `layer` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `layout` */

ALTER TABLE `layout` 
	DROP primary key;
ALTER TABLE `layout` 
	CHANGE narrow2 narrow2 BLOB;
ALTER TABLE `layout` 
	CHANGE narrow2 narrow2 longtext CHARACTER SET utf8;
ALTER TABLE `layout` 
	CHANGE modeedit modeedit BLOB;
ALTER TABLE `layout` 
	CHANGE modeedit modeedit longtext CHARACTER SET utf8;
ALTER TABLE `layout` 
	CHANGE userid userid BLOB;
ALTER TABLE `layout` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `layout` 
	CHANGE wide wide BLOB;
ALTER TABLE `layout` 
	CHANGE wide wide longtext CHARACTER SET utf8;
ALTER TABLE `layout` 
	CHANGE statemin statemin BLOB;
ALTER TABLE `layout` 
	CHANGE statemin statemin longtext CHARACTER SET utf8;
ALTER TABLE `layout` 
	CHANGE modehelp modehelp BLOB;
ALTER TABLE `layout` 
	CHANGE modehelp modehelp longtext CHARACTER SET utf8;
ALTER TABLE `layout` 
	CHANGE narrow1 narrow1 BLOB;
ALTER TABLE `layout` 
	CHANGE narrow1 narrow1 longtext CHARACTER SET utf8;
ALTER TABLE `layout` 
	CHANGE layoutid layoutid BLOB;
ALTER TABLE `layout` 
	CHANGE layoutid layoutid varchar(100) CHARACTER SET utf8;
ALTER TABLE `layout` 
	CHANGE columnorder columnorder BLOB;
ALTER TABLE `layout` 
	CHANGE columnorder columnorder varchar(100) CHARACTER SET utf8;
ALTER TABLE `layout` 
	CHANGE name name BLOB;
ALTER TABLE `layout` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `layout` 
	CHANGE statemax statemax BLOB;
ALTER TABLE `layout` 
	CHANGE statemax statemax longtext CHARACTER SET utf8;
ALTER TABLE `layout` 
	ADD primary key(layoutid,userid);
ALTER TABLE `layout` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `links` */

ALTER TABLE `links` 
	CHANGE deleted deleted BLOB;
ALTER TABLE `links` 
	CHANGE deleted deleted varchar(1) CHARACTER SET utf8;
ALTER TABLE `links` 
	CHANGE friendly_name friendly_name BLOB;
ALTER TABLE `links` 
	CHANGE friendly_name friendly_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `links` 
	CHANGE show_on_menu show_on_menu BLOB;
ALTER TABLE `links` 
	CHANGE show_on_menu show_on_menu varchar(1) CHARACTER SET utf8;
ALTER TABLE `links` 
	CHANGE mod_user mod_user BLOB;
ALTER TABLE `links` 
	CHANGE mod_user mod_user varchar(100) CHARACTER SET utf8;
ALTER TABLE `links` 
	CHANGE title title BLOB;
ALTER TABLE `links` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `links` 
	CHANGE protocal protocal BLOB;
ALTER TABLE `links` 
	CHANGE protocal protocal varchar(100) CHARACTER SET utf8;
ALTER TABLE `links` 
	CHANGE link_type link_type BLOB;
ALTER TABLE `links` 
	CHANGE link_type link_type varchar(255) CHARACTER SET utf8;
ALTER TABLE `links` 
	CHANGE target target BLOB;
ALTER TABLE `links` 
	CHANGE target target varchar(100) CHARACTER SET utf8;
ALTER TABLE `links` 
	CHANGE live live BLOB;
ALTER TABLE `links` 
	CHANGE live live varchar(1) CHARACTER SET utf8;
ALTER TABLE `links` 
	CHANGE locked locked BLOB;
ALTER TABLE `links` 
	CHANGE locked locked varchar(1) CHARACTER SET utf8;
ALTER TABLE `links` 
	CHANGE link_code link_code BLOB;
ALTER TABLE `links` 
	CHANGE link_code link_code varchar(255) CHARACTER SET utf8;
ALTER TABLE `links` 
	CHANGE url url BLOB;
ALTER TABLE `links` 
	CHANGE url url varchar(255) CHARACTER SET utf8;
ALTER TABLE `links` 
	CHANGE working working BLOB;
ALTER TABLE `links` 
	CHANGE working working varchar(1) CHARACTER SET utf8;
ALTER TABLE `links` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `mailing_list` */

ALTER TABLE `mailing_list` 
	DROP INDEX idx_mailinglist_1;
ALTER TABLE `mailing_list` 
	CHANGE user_id user_id BLOB;
ALTER TABLE `mailing_list` 
	CHANGE user_id user_id varchar(255) CHARACTER SET utf8;
ALTER TABLE `mailing_list` 
	CHANGE title title BLOB;
ALTER TABLE `mailing_list` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `mailing_list` 
	CHANGE public_list public_list BLOB;
ALTER TABLE `mailing_list` 
	CHANGE public_list public_list varchar(1) CHARACTER SET utf8;
ALTER TABLE `mailing_list` 
	ADD INDEX idx_mailinglist_1(user_id);
ALTER TABLE `mailing_list` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `mailreceipt` */

ALTER TABLE `mailreceipt` 
	DROP primary key;
ALTER TABLE `mailreceipt` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `mailreceipt` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `mailreceipt` 
	CHANGE userid userid BLOB;
ALTER TABLE `mailreceipt` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `mailreceipt` 
	CHANGE recipientaddress recipientaddress BLOB;
ALTER TABLE `mailreceipt` 
	CHANGE recipientaddress recipientaddress varchar(100) CHARACTER SET utf8;
ALTER TABLE `mailreceipt` 
	CHANGE recipientname recipientname BLOB;
ALTER TABLE `mailreceipt` 
	CHANGE recipientname recipientname varchar(100) CHARACTER SET utf8;
ALTER TABLE `mailreceipt` 
	CHANGE receiptid receiptid BLOB;
ALTER TABLE `mailreceipt` 
	CHANGE receiptid receiptid varchar(100) CHARACTER SET utf8;
ALTER TABLE `mailreceipt` 
	CHANGE subject subject BLOB;
ALTER TABLE `mailreceipt` 
	CHANGE subject subject varchar(100) CHARACTER SET utf8;
ALTER TABLE `mailreceipt` 
	ADD primary key(receiptid);
ALTER TABLE `mailreceipt` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `mbmessage` */

ALTER TABLE `mbmessage` 
	DROP primary key;
ALTER TABLE `mbmessage` 
	CHANGE topicid topicid BLOB;
ALTER TABLE `mbmessage` 
	CHANGE topicid topicid varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbmessage` 
	CHANGE threadid threadid BLOB;
ALTER TABLE `mbmessage` 
	CHANGE threadid threadid varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbmessage` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `mbmessage` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbmessage` 
	CHANGE userid userid BLOB;
ALTER TABLE `mbmessage` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbmessage` 
	CHANGE messageid messageid BLOB;
ALTER TABLE `mbmessage` 
	CHANGE messageid messageid varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbmessage` 
	CHANGE username username BLOB;
ALTER TABLE `mbmessage` 
	CHANGE username username varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbmessage` 
	CHANGE parentmessageid parentmessageid BLOB;
ALTER TABLE `mbmessage` 
	CHANGE parentmessageid parentmessageid varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbmessage` 
	CHANGE subject subject BLOB;
ALTER TABLE `mbmessage` 
	CHANGE subject subject varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbmessage` 
	CHANGE body body BLOB;
ALTER TABLE `mbmessage` 
	CHANGE body body longtext CHARACTER SET utf8;
ALTER TABLE `mbmessage` 
	ADD primary key(messageid,topicid);
ALTER TABLE `mbmessage` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `mbmessageflag` */

ALTER TABLE `mbmessageflag` 
	DROP primary key;
ALTER TABLE `mbmessageflag` 
	CHANGE userid userid BLOB;
ALTER TABLE `mbmessageflag` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbmessageflag` 
	CHANGE topicid topicid BLOB;
ALTER TABLE `mbmessageflag` 
	CHANGE topicid topicid varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbmessageflag` 
	CHANGE messageid messageid BLOB;
ALTER TABLE `mbmessageflag` 
	CHANGE messageid messageid varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbmessageflag` 
	CHANGE flag flag BLOB;
ALTER TABLE `mbmessageflag` 
	CHANGE flag flag varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbmessageflag` 
	ADD primary key(topicid,messageid,userid);
ALTER TABLE `mbmessageflag` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `mbthread` */

ALTER TABLE `mbthread` 
	DROP primary key;
ALTER TABLE `mbthread` 
	CHANGE topicid topicid BLOB;
ALTER TABLE `mbthread` 
	CHANGE topicid topicid varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbthread` 
	CHANGE rootmessageid rootmessageid BLOB;
ALTER TABLE `mbthread` 
	CHANGE rootmessageid rootmessageid varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbthread` 
	CHANGE threadid threadid BLOB;
ALTER TABLE `mbthread` 
	CHANGE threadid threadid varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbthread` 
	ADD primary key(threadid);
ALTER TABLE `mbthread` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `mbtopic` */

ALTER TABLE `mbtopic` 
	DROP primary key;
ALTER TABLE `mbtopic` 
	CHANGE groupid groupid BLOB;
ALTER TABLE `mbtopic` 
	CHANGE groupid groupid varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbtopic` 
	CHANGE topicid topicid BLOB;
ALTER TABLE `mbtopic` 
	CHANGE topicid topicid varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbtopic` 
	CHANGE readroles readroles BLOB;
ALTER TABLE `mbtopic` 
	CHANGE readroles readroles varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbtopic` 
	CHANGE portletid portletid BLOB;
ALTER TABLE `mbtopic` 
	CHANGE portletid portletid varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbtopic` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `mbtopic` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbtopic` 
	CHANGE userid userid BLOB;
ALTER TABLE `mbtopic` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbtopic` 
	CHANGE writeroles writeroles BLOB;
ALTER TABLE `mbtopic` 
	CHANGE writeroles writeroles varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbtopic` 
	CHANGE description description BLOB;
ALTER TABLE `mbtopic` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `mbtopic` 
	CHANGE username username BLOB;
ALTER TABLE `mbtopic` 
	CHANGE username username varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbtopic` 
	CHANGE name name BLOB;
ALTER TABLE `mbtopic` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `mbtopic` 
	ADD primary key(topicid);
ALTER TABLE `mbtopic` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `multi_tree` */

ALTER TABLE `multi_tree` 
	DROP INDEX idx_multitree_1;
ALTER TABLE `multi_tree` 
	CHANGE relation_type relation_type BLOB;
ALTER TABLE `multi_tree` 
	CHANGE relation_type relation_type varchar(64) CHARACTER SET utf8;
ALTER TABLE `multi_tree` 
	ADD INDEX idx_multitree_1(relation_type);
ALTER TABLE `multi_tree` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `networkaddress` */

ALTER TABLE `networkaddress` 
	DROP primary key;
ALTER TABLE `networkaddress` 
	CHANGE comments comments BLOB;
ALTER TABLE `networkaddress` 
	CHANGE comments comments longtext CHARACTER SET utf8;
ALTER TABLE `networkaddress` 
	CHANGE content content BLOB;
ALTER TABLE `networkaddress` 
	CHANGE content content longtext CHARACTER SET utf8;
ALTER TABLE `networkaddress` 
	CHANGE notifyby notifyby BLOB;
ALTER TABLE `networkaddress` 
	CHANGE notifyby notifyby varchar(100) CHARACTER SET utf8;
ALTER TABLE `networkaddress` 
	CHANGE addressid addressid BLOB;
ALTER TABLE `networkaddress` 
	CHANGE addressid addressid varchar(100) CHARACTER SET utf8;
ALTER TABLE `networkaddress` 
	CHANGE userid userid BLOB;
ALTER TABLE `networkaddress` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `networkaddress` 
	CHANGE url url BLOB;
ALTER TABLE `networkaddress` 
	CHANGE url url varchar(100) CHARACTER SET utf8;
ALTER TABLE `networkaddress` 
	CHANGE name name BLOB;
ALTER TABLE `networkaddress` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `networkaddress` 
	ADD primary key (addressid);
ALTER TABLE `networkaddress` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `note` */

ALTER TABLE `note` 
	DROP primary key;
ALTER TABLE `note` 
	CHANGE classname classname BLOB;
ALTER TABLE `note` 
	CHANGE classname classname varchar(100) CHARACTER SET utf8;
ALTER TABLE `note` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `note` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `note` 
	CHANGE userid userid BLOB;
ALTER TABLE `note` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `note` 
	CHANGE noteid noteid BLOB;
ALTER TABLE `note` 
	CHANGE noteid noteid varchar(100) CHARACTER SET utf8;
ALTER TABLE `note` 
	CHANGE content content BLOB;
ALTER TABLE `note` 
	CHANGE content content longtext CHARACTER SET utf8;
ALTER TABLE `note` 
	CHANGE username username BLOB;
ALTER TABLE `note` 
	CHANGE username username varchar(100) CHARACTER SET utf8;
ALTER TABLE `note` 
	CHANGE classpk classpk BLOB;
ALTER TABLE `note` 
	CHANGE classpk classpk varchar(100) CHARACTER SET utf8;
ALTER TABLE `note` 
	ADD primary key(noteid);
ALTER TABLE `note` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `organization` */

ALTER TABLE `organization` 
	CHANGE partner_key partner_key BLOB;
ALTER TABLE `organization` 
	CHANGE partner_key partner_key varchar(255) CHARACTER SET utf8;
ALTER TABLE `organization` 
	CHANGE phone phone BLOB;
ALTER TABLE `organization` 
	CHANGE phone phone varchar(100) CHARACTER SET utf8;
ALTER TABLE `organization` 
	CHANGE title title BLOB;
ALTER TABLE `organization` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `organization` 
	CHANGE ceo_name ceo_name BLOB;
ALTER TABLE `organization` 
	CHANGE ceo_name ceo_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `organization` 
	CHANGE partner_url partner_url BLOB;
ALTER TABLE `organization` 
	CHANGE partner_url partner_url varchar(255) CHARACTER SET utf8;
ALTER TABLE `organization` 
	CHANGE fax fax BLOB;
ALTER TABLE `organization` 
	CHANGE fax fax varchar(100) CHARACTER SET utf8;
ALTER TABLE `organization` 
	CHANGE street2 street2 BLOB;
ALTER TABLE `organization` 
	CHANGE street2 street2 varchar(255) CHARACTER SET utf8;
ALTER TABLE `organization` 
	CHANGE country country BLOB;
ALTER TABLE `organization` 
	CHANGE country country varchar(255) CHARACTER SET utf8;
ALTER TABLE `organization` 
	CHANGE street1 street1 BLOB;
ALTER TABLE `organization` 
	CHANGE street1 street1 varchar(255) CHARACTER SET utf8;
ALTER TABLE `organization` 
	CHANGE state state BLOB;
ALTER TABLE `organization` 
	CHANGE state state varchar(255) CHARACTER SET utf8;
ALTER TABLE `organization` 
	CHANGE zip zip BLOB;
ALTER TABLE `organization` 
	CHANGE zip zip varchar(100) CHARACTER SET utf8;
ALTER TABLE `organization` 
	CHANGE is_system is_system BLOB;
ALTER TABLE `organization` 
	CHANGE is_system is_system varchar(1) CHARACTER SET utf8;
ALTER TABLE `organization` 
	CHANGE city city BLOB;
ALTER TABLE `organization` 
	CHANGE city city varchar(255) CHARACTER SET utf8;
ALTER TABLE `organization` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `passwordtracker` */

ALTER TABLE `passwordtracker` 
	DROP primary key;
ALTER TABLE `passwordtracker` 
	CHANGE userid userid BLOB;
ALTER TABLE `passwordtracker` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `passwordtracker` 
	CHANGE passwordtrackerid passwordtrackerid BLOB;
ALTER TABLE `passwordtracker` 
	CHANGE passwordtrackerid passwordtrackerid varchar(100) CHARACTER SET utf8;
ALTER TABLE `passwordtracker` 
	CHANGE password_ password_ BLOB;
ALTER TABLE `passwordtracker` 
	CHANGE password_ password_ varchar(100) CHARACTER SET utf8;
ALTER TABLE `passwordtracker` 
	ADD primary key(passwordtrackerid);
ALTER TABLE `passwordtracker` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `permission` */

ALTER TABLE `permission` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `pollschoice` */

ALTER TABLE `pollschoice` 
	DROP primary key;
ALTER TABLE `pollschoice` 
	CHANGE description description BLOB;
ALTER TABLE `pollschoice` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `pollschoice` 
	CHANGE questionid questionid BLOB;
ALTER TABLE `pollschoice` 
	CHANGE questionid questionid varchar(100) CHARACTER SET utf8;
ALTER TABLE `pollschoice` 
	CHANGE choiceid choiceid BLOB;
ALTER TABLE `pollschoice` 
	CHANGE choiceid choiceid varchar(100) CHARACTER SET utf8;
ALTER TABLE `pollschoice` 
	ADD primary key(choiceid,questionid);
ALTER TABLE `pollschoice` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `pollsdisplay` */

ALTER TABLE `pollsdisplay` 
	DROP primary key;
ALTER TABLE `pollsdisplay` 
	CHANGE userid userid BLOB;
ALTER TABLE `pollsdisplay` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `pollsdisplay` 
	CHANGE layoutid layoutid BLOB;
ALTER TABLE `pollsdisplay` 
	CHANGE layoutid layoutid varchar(100) CHARACTER SET utf8;
ALTER TABLE `pollsdisplay` 
	CHANGE questionid questionid BLOB;
ALTER TABLE `pollsdisplay` 
	CHANGE questionid questionid varchar(100) CHARACTER SET utf8;
ALTER TABLE `pollsdisplay` 
	CHANGE portletid portletid BLOB;
ALTER TABLE `pollsdisplay` 
	CHANGE portletid portletid varchar(100) CHARACTER SET utf8;
ALTER TABLE `pollsdisplay` 
	ADD primary key(layoutid,userid,portletid);
ALTER TABLE `pollsdisplay` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `pollsquestion` */

ALTER TABLE `pollsquestion` 
	DROP primary key;
ALTER TABLE `pollsquestion` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `pollsquestion` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `pollsquestion` 
	CHANGE groupid groupid BLOB;
ALTER TABLE `pollsquestion` 
	CHANGE groupid groupid varchar(100) CHARACTER SET utf8;
ALTER TABLE `pollsquestion` 
	CHANGE userid userid BLOB;
ALTER TABLE `pollsquestion` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `pollsquestion` 
	CHANGE title title BLOB;
ALTER TABLE `pollsquestion` 
	CHANGE title title varchar(100) CHARACTER SET utf8;
ALTER TABLE `pollsquestion` 
	CHANGE description description BLOB;
ALTER TABLE `pollsquestion` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `pollsquestion` 
	CHANGE username username BLOB;
ALTER TABLE `pollsquestion` 
	CHANGE username username varchar(100) CHARACTER SET utf8;
ALTER TABLE `pollsquestion` 
	CHANGE questionid questionid BLOB;
ALTER TABLE `pollsquestion` 
	CHANGE questionid questionid varchar(100) CHARACTER SET utf8;
ALTER TABLE `pollsquestion` 
	CHANGE portletid portletid BLOB;
ALTER TABLE `pollsquestion` 
	CHANGE portletid portletid varchar(100) CHARACTER SET utf8;
ALTER TABLE `pollsquestion` 
	ADD primary key(questionid);
ALTER TABLE `pollsquestion` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `pollsvote` */

ALTER TABLE `pollsvote` 
	DROP primary key;
ALTER TABLE `pollsvote` 
	CHANGE userid userid BLOB;
ALTER TABLE `pollsvote` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `pollsvote` 
	CHANGE questionid questionid BLOB;
ALTER TABLE `pollsvote` 
	CHANGE questionid questionid varchar(100) CHARACTER SET utf8;
ALTER TABLE `pollsvote` 
	CHANGE choiceid choiceid BLOB;
ALTER TABLE `pollsvote` 
	CHANGE choiceid choiceid varchar(100) CHARACTER SET utf8;
ALTER TABLE `pollsvote` 
	ADD primary key(questionid,userid);
ALTER TABLE `pollsvote` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `portlet` */

ALTER TABLE `portlet` 
	DROP primary key;
ALTER TABLE `portlet` 
	CHANGE defaultpreferences defaultpreferences BLOB;
ALTER TABLE `portlet` 
	CHANGE defaultpreferences defaultpreferences longtext CHARACTER SET utf8;
ALTER TABLE `portlet` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `portlet` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `portlet` 
	CHANGE groupid groupid BLOB;
ALTER TABLE `portlet` 
	CHANGE groupid groupid varchar(100) CHARACTER SET utf8;
ALTER TABLE `portlet` 
	CHANGE roles roles BLOB;
ALTER TABLE `portlet` 
	CHANGE roles roles longtext CHARACTER SET utf8;
ALTER TABLE `portlet` 
	CHANGE portletid portletid BLOB;
ALTER TABLE `portlet` 
	CHANGE portletid portletid varchar(100) CHARACTER SET utf8;
ALTER TABLE `portlet` 
	ADD primary key(portletid,groupid,companyid);
ALTER TABLE `portlet` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `portletpreferences` */

ALTER TABLE `portletpreferences` 
	DROP primary key;
ALTER TABLE `portletpreferences` 
	CHANGE userid userid BLOB;
ALTER TABLE `portletpreferences` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `portletpreferences` 
	CHANGE preferences preferences BLOB;
ALTER TABLE `portletpreferences` 
	CHANGE preferences preferences longtext CHARACTER SET utf8;
ALTER TABLE `portletpreferences` 
	CHANGE layoutid layoutid BLOB;
ALTER TABLE `portletpreferences` 
	CHANGE layoutid layoutid varchar(100) CHARACTER SET utf8;
ALTER TABLE `portletpreferences` 
	CHANGE portletid portletid BLOB;
ALTER TABLE `portletpreferences` 
	CHANGE portletid portletid varchar(100) CHARACTER SET utf8;
ALTER TABLE `portletpreferences` 
	ADD primary key(portletid,userid,layoutid);
ALTER TABLE `portletpreferences` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `projfirm` */

ALTER TABLE `projfirm` 
	DROP primary key;
ALTER TABLE `projfirm` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `projfirm` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `projfirm` 
	CHANGE userid userid BLOB;
ALTER TABLE `projfirm` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `projfirm` 
	CHANGE description description BLOB;
ALTER TABLE `projfirm` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `projfirm` 
	CHANGE username username BLOB;
ALTER TABLE `projfirm` 
	CHANGE username username varchar(100) CHARACTER SET utf8;
ALTER TABLE `projfirm` 
	CHANGE url url BLOB;
ALTER TABLE `projfirm` 
	CHANGE url url varchar(100) CHARACTER SET utf8;
ALTER TABLE `projfirm` 
	CHANGE name name BLOB;
ALTER TABLE `projfirm` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `projfirm` 
	CHANGE firmid firmid BLOB;
ALTER TABLE `projfirm` 
	CHANGE firmid firmid varchar(100) CHARACTER SET utf8;
ALTER TABLE `projfirm` 
	ADD primary key(firmid);
ALTER TABLE `projfirm` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `projproject` */

ALTER TABLE `projproject` 
	DROP primary key;
ALTER TABLE `projproject` 
	CHANGE code code BLOB;
ALTER TABLE `projproject` 
	CHANGE code code varchar(100) CHARACTER SET utf8;
ALTER TABLE `projproject` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `projproject` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `projproject` 
	CHANGE userid userid BLOB;
ALTER TABLE `projproject` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `projproject` 
	CHANGE projectid projectid BLOB;
ALTER TABLE `projproject` 
	CHANGE projectid projectid varchar(100) CHARACTER SET utf8;
ALTER TABLE `projproject` 
	CHANGE description description BLOB;
ALTER TABLE `projproject` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `projproject` 
	CHANGE username username BLOB;
ALTER TABLE `projproject` 
	CHANGE username username varchar(100) CHARACTER SET utf8;
ALTER TABLE `projproject` 
	CHANGE name name BLOB;
ALTER TABLE `projproject` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `projproject` 
	CHANGE firmid firmid BLOB;
ALTER TABLE `projproject` 
	CHANGE firmid firmid varchar(100) CHARACTER SET utf8;
ALTER TABLE `projproject` 
	ADD primary key(projectid);
ALTER TABLE `projproject` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `projtask` */

ALTER TABLE `projtask` 
	DROP primary key;
ALTER TABLE `projtask` 
	CHANGE comments comments BLOB;
ALTER TABLE `projtask` 
	CHANGE comments comments longtext CHARACTER SET utf8;
ALTER TABLE `projtask` 
	CHANGE taskid taskid BLOB;
ALTER TABLE `projtask` 
	CHANGE taskid taskid varchar(100) CHARACTER SET utf8;
ALTER TABLE `projtask` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `projtask` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `projtask` 
	CHANGE userid userid BLOB;
ALTER TABLE `projtask` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `projtask` 
	CHANGE projectid projectid BLOB;
ALTER TABLE `projtask` 
	CHANGE projectid projectid varchar(100) CHARACTER SET utf8;
ALTER TABLE `projtask` 
	CHANGE description description BLOB;
ALTER TABLE `projtask` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `projtask` 
	CHANGE username username BLOB;
ALTER TABLE `projtask` 
	CHANGE username username varchar(100) CHARACTER SET utf8;
ALTER TABLE `projtask` 
	CHANGE name name BLOB;
ALTER TABLE `projtask` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `projtask` 
	ADD primary key(taskid);
ALTER TABLE `projtask` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `projtime` */

ALTER TABLE `projtime` 
	DROP primary key;
ALTER TABLE `projtime` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `projtime` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `projtime` 
	CHANGE timeid timeid BLOB;
ALTER TABLE `projtime` 
	CHANGE timeid timeid varchar(100) CHARACTER SET utf8;
ALTER TABLE `projtime` 
	CHANGE userid userid BLOB;
ALTER TABLE `projtime` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `projtime` 
	CHANGE projectid projectid BLOB;
ALTER TABLE `projtime` 
	CHANGE projectid projectid varchar(100) CHARACTER SET utf8;
ALTER TABLE `projtime` 
	CHANGE description description BLOB;
ALTER TABLE `projtime` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `projtime` 
	CHANGE username username BLOB;
ALTER TABLE `projtime` 
	CHANGE username username varchar(100) CHARACTER SET utf8;
ALTER TABLE `projtime` 
	CHANGE taskid taskid BLOB;
ALTER TABLE `projtime` 
	CHANGE taskid taskid varchar(100) CHARACTER SET utf8;
ALTER TABLE `projtime` 
	ADD primary key(timeid);
ALTER TABLE `projtime` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `recipient` */

ALTER TABLE `recipient` 
	DROP INDEX idx_recipiets_1,
	DROP INDEX idx_communication_user_id;
ALTER TABLE `recipient` 
	CHANGE lastname lastname BLOB;
ALTER TABLE `recipient` 
	CHANGE lastname lastname varchar(255) CHARACTER SET utf8;
ALTER TABLE `recipient` 
	CHANGE user_id user_id BLOB;
ALTER TABLE `recipient` 
	CHANGE user_id user_id varchar(100) CHARACTER SET utf8;
ALTER TABLE `recipient` 
	CHANGE email email BLOB;
ALTER TABLE `recipient` 
	CHANGE email email varchar(255) CHARACTER SET utf8;
ALTER TABLE `recipient` 
	CHANGE last_message last_message BLOB;
ALTER TABLE `recipient` 
	CHANGE last_message last_message varchar(255) CHARACTER SET utf8;
ALTER TABLE `recipient` 
	CHANGE name name BLOB;
ALTER TABLE `recipient` 
	CHANGE name name varchar(255) CHARACTER SET utf8;
ALTER TABLE `recipient` 
	ADD INDEX idx_recipiets_1(email),
	ADD INDEX idx_communication_user_id(user_id);
ALTER TABLE `recipient` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `recurance` */

ALTER TABLE `recurance` 
	CHANGE recurrance_days_of_week recurrance_days_of_week BLOB;
ALTER TABLE `recurance` 
	CHANGE recurrance_days_of_week recurrance_days_of_week varchar(255) CHARACTER SET utf8;
ALTER TABLE `recurance` 
	CHANGE recurrance_occurs recurrance_occurs BLOB;
ALTER TABLE `recurance` 
	CHANGE recurrance_occurs recurrance_occurs varchar(255) CHARACTER SET utf8;
ALTER TABLE `recurance` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `relationship` */

ALTER TABLE `relationship` 
	CHANGE parent_relation_name parent_relation_name BLOB;
ALTER TABLE `relationship` 
	CHANGE parent_relation_name parent_relation_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `relationship` 
	CHANGE child_relation_name child_relation_name BLOB;
ALTER TABLE `relationship` 
	CHANGE child_relation_name child_relation_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `relationship` 
	CHANGE relation_type_value relation_type_value BLOB;
ALTER TABLE `relationship` 
	CHANGE relation_type_value relation_type_value varchar(50) CHARACTER SET utf8;
ALTER TABLE `relationship` 
	CHANGE child_required child_required BLOB;
ALTER TABLE `relationship` 
	CHANGE child_required child_required varchar(1) CHARACTER SET utf8;
ALTER TABLE `relationship` 
	CHANGE parent_required parent_required BLOB;
ALTER TABLE `relationship` 
	CHANGE parent_required parent_required varchar(1) CHARACTER SET utf8;
ALTER TABLE `relationship` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `release_` */

ALTER TABLE `release_` 
	DROP primary key;
ALTER TABLE `release_` 
	CHANGE releaseid releaseid BLOB;
ALTER TABLE `release_` 
	CHANGE releaseid releaseid varchar(100) CHARACTER SET utf8;
ALTER TABLE `release_` 
	ADD primary key(releaseid);
ALTER TABLE `release_` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `report_asset` */

ALTER TABLE `report_asset` 
	CHANGE report_name report_name BLOB;
ALTER TABLE `report_asset` 
	CHANGE report_name report_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `report_asset` 
	CHANGE ds ds BLOB;
ALTER TABLE `report_asset` 
	CHANGE ds ds varchar(100) CHARACTER SET utf8;
ALTER TABLE `report_asset` 
	CHANGE requires_input requires_input BLOB;
ALTER TABLE `report_asset` 
	CHANGE requires_input requires_input varchar(1) CHARACTER SET utf8;
ALTER TABLE `report_asset` 
	CHANGE report_description report_description BLOB;
ALTER TABLE `report_asset` 
	CHANGE report_description report_description text CHARACTER SET utf8;
ALTER TABLE `report_asset` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `report_parameter` */

ALTER TABLE `report_parameter` 
	DROP INDEX report_inode;
ALTER TABLE `report_parameter` 
	CHANGE parameter_description parameter_description BLOB;
ALTER TABLE `report_parameter` 
	CHANGE parameter_description parameter_description text CHARACTER SET utf8;
ALTER TABLE `report_parameter` 
	CHANGE default_value default_value BLOB;
ALTER TABLE `report_parameter` 
	CHANGE default_value default_value text CHARACTER SET utf8;
ALTER TABLE `report_parameter` 
	CHANGE class_type class_type BLOB;
ALTER TABLE `report_parameter` 
	CHANGE class_type class_type varchar(250) CHARACTER SET utf8;
ALTER TABLE `report_parameter` 
	CHANGE parameter_name parameter_name BLOB;
ALTER TABLE `report_parameter` 
	CHANGE parameter_name parameter_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `report_parameter` 
	ADD UNIQUE INDEX report_inode(report_inode,parameter_name);
ALTER TABLE `report_parameter` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `role_` */

ALTER TABLE `role_` 
	DROP primary key;
ALTER TABLE `role_` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `role_` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `role_` 
	CHANGE roleid roleid BLOB;
ALTER TABLE `role_` 
	CHANGE roleid roleid varchar(100) CHARACTER SET utf8;
ALTER TABLE `role_` 
	CHANGE name name BLOB;
ALTER TABLE `role_` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `role_` 
	ADD primary key(roleid);
ALTER TABLE `role_` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `shoppingcart` */

ALTER TABLE `shoppingcart` 
	DROP primary key;
ALTER TABLE `shoppingcart` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `shoppingcart` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingcart` 
	CHANGE couponids couponids BLOB;
ALTER TABLE `shoppingcart` 
	CHANGE couponids couponids longtext CHARACTER SET utf8;
ALTER TABLE `shoppingcart` 
	CHANGE userid userid BLOB;
ALTER TABLE `shoppingcart` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingcart` 
	CHANGE cartid cartid BLOB;
ALTER TABLE `shoppingcart` 
	CHANGE cartid cartid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingcart` 
	CHANGE itemids itemids BLOB;
ALTER TABLE `shoppingcart` 
	CHANGE itemids itemids longtext CHARACTER SET utf8;
ALTER TABLE `shoppingcart` 
	ADD primary key(cartid);
ALTER TABLE `shoppingcart` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `shoppingcategory` */

ALTER TABLE `shoppingcategory` 
	DROP primary key;
ALTER TABLE `shoppingcategory` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `shoppingcategory` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingcategory` 
	CHANGE parentcategoryid parentcategoryid BLOB;
ALTER TABLE `shoppingcategory` 
	CHANGE parentcategoryid parentcategoryid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingcategory` 
	CHANGE name name BLOB;
ALTER TABLE `shoppingcategory` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingcategory` 
	CHANGE categoryid categoryid BLOB;
ALTER TABLE `shoppingcategory` 
	CHANGE categoryid categoryid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingcategory` 
	ADD primary key(categoryid);
ALTER TABLE `shoppingcategory` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `shoppingcoupon` */

ALTER TABLE `shoppingcoupon` 
	DROP primary key;
ALTER TABLE `shoppingcoupon` 
	CHANGE limitskus limitskus BLOB;
ALTER TABLE `shoppingcoupon` 
	CHANGE limitskus limitskus longtext CHARACTER SET utf8;
ALTER TABLE `shoppingcoupon` 
	CHANGE limitcategories limitcategories BLOB;
ALTER TABLE `shoppingcoupon` 
	CHANGE limitcategories limitcategories longtext CHARACTER SET utf8;
ALTER TABLE `shoppingcoupon` 
	CHANGE discounttype discounttype BLOB;
ALTER TABLE `shoppingcoupon` 
	CHANGE discounttype discounttype varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingcoupon` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `shoppingcoupon` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingcoupon` 
	CHANGE couponid couponid BLOB;
ALTER TABLE `shoppingcoupon` 
	CHANGE couponid couponid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingcoupon` 
	CHANGE description description BLOB;
ALTER TABLE `shoppingcoupon` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `shoppingcoupon` 
	CHANGE name name BLOB;
ALTER TABLE `shoppingcoupon` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingcoupon` 
	ADD primary key(couponid);
ALTER TABLE `shoppingcoupon` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `shoppingitem` */

ALTER TABLE `shoppingitem` 
	DROP primary key;
ALTER TABLE `shoppingitem` 
	CHANGE fieldsquantities fieldsquantities BLOB;
ALTER TABLE `shoppingitem` 
	CHANGE fieldsquantities fieldsquantities longtext CHARACTER SET utf8;
ALTER TABLE `shoppingitem` 
	CHANGE properties properties BLOB;
ALTER TABLE `shoppingitem` 
	CHANGE properties properties longtext CHARACTER SET utf8;
ALTER TABLE `shoppingitem` 
	CHANGE categoryid categoryid BLOB;
ALTER TABLE `shoppingitem` 
	CHANGE categoryid categoryid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingitem` 
	CHANGE supplieruserid supplieruserid BLOB;
ALTER TABLE `shoppingitem` 
	CHANGE supplieruserid supplieruserid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingitem` 
	CHANGE smallimageurl smallimageurl BLOB;
ALTER TABLE `shoppingitem` 
	CHANGE smallimageurl smallimageurl varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingitem` 
	CHANGE name name BLOB;
ALTER TABLE `shoppingitem` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingitem` 
	CHANGE largeimageurl largeimageurl BLOB;
ALTER TABLE `shoppingitem` 
	CHANGE largeimageurl largeimageurl varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingitem` 
	CHANGE itemid itemid BLOB;
ALTER TABLE `shoppingitem` 
	CHANGE itemid itemid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingitem` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `shoppingitem` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingitem` 
	CHANGE mediumimageurl mediumimageurl BLOB;
ALTER TABLE `shoppingitem` 
	CHANGE mediumimageurl mediumimageurl varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingitem` 
	CHANGE description description BLOB;
ALTER TABLE `shoppingitem` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `shoppingitem` 
	CHANGE sku sku BLOB;
ALTER TABLE `shoppingitem` 
	CHANGE sku sku varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingitem` 
	ADD primary key(itemid);
ALTER TABLE `shoppingitem` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `shoppingitemfield` */

ALTER TABLE `shoppingitemfield` 
	DROP primary key;
ALTER TABLE `shoppingitemfield` 
	CHANGE description description BLOB;
ALTER TABLE `shoppingitemfield` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `shoppingitemfield` 
	CHANGE itemfieldid itemfieldid BLOB;
ALTER TABLE `shoppingitemfield` 
	CHANGE itemfieldid itemfieldid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingitemfield` 
	CHANGE name name BLOB;
ALTER TABLE `shoppingitemfield` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingitemfield` 
	CHANGE itemid itemid BLOB;
ALTER TABLE `shoppingitemfield` 
	CHANGE itemid itemid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingitemfield` 
	CHANGE values_ values_ BLOB;
ALTER TABLE `shoppingitemfield` 
	CHANGE values_ values_ longtext CHARACTER SET utf8;
ALTER TABLE `shoppingitemfield` 
	ADD primary key(itemfieldid);
ALTER TABLE `shoppingitemfield` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `shoppingitemprice` */

ALTER TABLE `shoppingitemprice` 
	DROP primary key;
ALTER TABLE `shoppingitemprice` 
	CHANGE itempriceid itempriceid BLOB;
ALTER TABLE `shoppingitemprice` 
	CHANGE itempriceid itempriceid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingitemprice` 
	CHANGE itemid itemid BLOB;
ALTER TABLE `shoppingitemprice` 
	CHANGE itemid itemid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingitemprice` 
	ADD primary key(itempriceid);
ALTER TABLE `shoppingitemprice` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `shoppingorder` */

ALTER TABLE `shoppingorder` 
	DROP primary key;
ALTER TABLE `shoppingorder` 
	CHANGE comments comments BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE comments comments longtext CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE shippingemailaddress shippingemailaddress BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE shippingemailaddress shippingemailaddress varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE pppaymentstatus pppaymentstatus BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE pppaymentstatus pppaymentstatus varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE cctype cctype BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE cctype cctype varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE ccname ccname BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE ccname ccname varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE shippingstate shippingstate BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE shippingstate shippingstate varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE pptxnid pptxnid BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE pptxnid pptxnid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE shippingcity shippingcity BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE shippingcity shippingcity varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE shippingcompany shippingcompany BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE shippingcompany shippingcompany varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE couponids couponids BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE couponids couponids longtext CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE userid userid BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE billingphone billingphone BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE billingphone billingphone varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE shippingphone shippingphone BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE shippingphone shippingphone varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE ccnumber ccnumber BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE ccnumber ccnumber varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE altshipping altshipping BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE altshipping altshipping varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE shippinglastname shippinglastname BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE shippinglastname shippinglastname varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE shippingcountry shippingcountry BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE shippingcountry shippingcountry varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE billingcountry billingcountry BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE billingcountry billingcountry varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE shippingzip shippingzip BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE shippingzip shippingzip varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE ccvernumber ccvernumber BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE ccvernumber ccvernumber varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE billingzip billingzip BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE billingzip billingzip varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE billingstreet billingstreet BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE billingstreet billingstreet varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE shippingfirstname shippingfirstname BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE shippingfirstname shippingfirstname varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE shippingstreet shippingstreet BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE shippingstreet shippingstreet varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE billingfirstname billingfirstname BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE billingfirstname billingfirstname varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE orderid orderid BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE orderid orderid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE billingstate billingstate BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE billingstate billingstate varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE billinglastname billinglastname BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE billinglastname billinglastname varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE billingcompany billingcompany BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE billingcompany billingcompany varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE billingcity billingcity BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE billingcity billingcity varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE billingemailaddress billingemailaddress BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE billingemailaddress billingemailaddress varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE ppreceiveremail ppreceiveremail BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE ppreceiveremail ppreceiveremail varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	CHANGE pppayeremail pppayeremail BLOB;
ALTER TABLE `shoppingorder` 
	CHANGE pppayeremail pppayeremail varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorder` 
	ADD primary key(orderid);
ALTER TABLE `shoppingorder` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `shoppingorderitem` */

ALTER TABLE `shoppingorderitem` 
	DROP primary key;
ALTER TABLE `shoppingorderitem` 
	CHANGE supplieruserid supplieruserid BLOB;
ALTER TABLE `shoppingorderitem` 
	CHANGE supplieruserid supplieruserid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorderitem` 
	CHANGE description description BLOB;
ALTER TABLE `shoppingorderitem` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `shoppingorderitem` 
	CHANGE orderid orderid BLOB;
ALTER TABLE `shoppingorderitem` 
	CHANGE orderid orderid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorderitem` 
	CHANGE properties properties BLOB;
ALTER TABLE `shoppingorderitem` 
	CHANGE properties properties longtext CHARACTER SET utf8;
ALTER TABLE `shoppingorderitem` 
	CHANGE name name BLOB;
ALTER TABLE `shoppingorderitem` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorderitem` 
	CHANGE sku sku BLOB;
ALTER TABLE `shoppingorderitem` 
	CHANGE sku sku varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorderitem` 
	CHANGE itemid itemid BLOB;
ALTER TABLE `shoppingorderitem` 
	CHANGE itemid itemid varchar(100) CHARACTER SET utf8;
ALTER TABLE `shoppingorderitem` 
	ADD primary key(orderid,itemid);
ALTER TABLE `shoppingorderitem` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `skin` */

ALTER TABLE `skin` 
	DROP primary key;
ALTER TABLE `skin` 
	CHANGE gammaskinid gammaskinid BLOB;
ALTER TABLE `skin` 
	CHANGE gammaskinid gammaskinid varchar(100) CHARACTER SET utf8;
ALTER TABLE `skin` 
	CHANGE betalayerid betalayerid BLOB;
ALTER TABLE `skin` 
	CHANGE betalayerid betalayerid varchar(100) CHARACTER SET utf8;
ALTER TABLE `skin` 
	CHANGE alphalayerid alphalayerid BLOB;
ALTER TABLE `skin` 
	CHANGE alphalayerid alphalayerid varchar(100) CHARACTER SET utf8;
ALTER TABLE `skin` 
	CHANGE alphaskinid alphaskinid BLOB;
ALTER TABLE `skin` 
	CHANGE alphaskinid alphaskinid varchar(100) CHARACTER SET utf8;
ALTER TABLE `skin` 
	CHANGE gammalayerid gammalayerid BLOB;
ALTER TABLE `skin` 
	CHANGE gammalayerid gammalayerid varchar(100) CHARACTER SET utf8;
ALTER TABLE `skin` 
	CHANGE bgskinid bgskinid BLOB;
ALTER TABLE `skin` 
	CHANGE bgskinid bgskinid varchar(100) CHARACTER SET utf8;
ALTER TABLE `skin` 
	CHANGE bglayerid bglayerid BLOB;
ALTER TABLE `skin` 
	CHANGE bglayerid bglayerid varchar(100) CHARACTER SET utf8;
ALTER TABLE `skin` 
	CHANGE betaskinid betaskinid BLOB;
ALTER TABLE `skin` 
	CHANGE betaskinid betaskinid varchar(100) CHARACTER SET utf8;
ALTER TABLE `skin` 
	CHANGE name name BLOB;
ALTER TABLE `skin` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `skin` 
	CHANGE skinid skinid BLOB;
ALTER TABLE `skin` 
	CHANGE skinid skinid varchar(100) CHARACTER SET utf8;
ALTER TABLE `skin` 
	CHANGE imageid imageid BLOB;
ALTER TABLE `skin` 
	CHANGE imageid imageid varchar(100) CHARACTER SET utf8;
ALTER TABLE `skin` 
	ADD primary key(skinid);
ALTER TABLE `skin` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `structure` */

ALTER TABLE `structure` 
	CHANGE default_structure default_structure BLOB;
ALTER TABLE `structure` 
	CHANGE default_structure default_structure varchar(1) CHARACTER SET utf8;
ALTER TABLE `structure` 
	CHANGE description description BLOB;
ALTER TABLE `structure` 
	CHANGE description description varchar(255) CHARACTER SET utf8;
ALTER TABLE `structure` 
	CHANGE review_interval review_interval BLOB;
ALTER TABLE `structure` 
	CHANGE review_interval review_interval varchar(255) CHARACTER SET utf8;
ALTER TABLE `structure` 
	CHANGE name name BLOB;
ALTER TABLE `structure` 
	CHANGE name name varchar(255) CHARACTER SET utf8;
ALTER TABLE `structure` 
	CHANGE reviewer_role reviewer_role BLOB;
ALTER TABLE `structure` 
	CHANGE reviewer_role reviewer_role varchar(255) CHARACTER SET utf8;
ALTER TABLE `structure` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `tag` */

ALTER TABLE `tag` 
	DROP primary key;
ALTER TABLE `tag` 
	CHANGE user_id user_id BLOB;
ALTER TABLE `tag` 
	CHANGE user_id user_id varchar(255) CHARACTER SET utf8;
ALTER TABLE `tag` 
	CHANGE tagname tagname BLOB;
ALTER TABLE `tag` 
	CHANGE tagname tagname varchar(255) CHARACTER SET utf8;
ALTER TABLE `tag` 
	ADD primary key(tagname);
ALTER TABLE `tag` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `tag_inode` */

ALTER TABLE `tag_inode` 
	DROP primary key;
ALTER TABLE `tag_inode` 
	CHANGE inode inode BLOB;
ALTER TABLE `tag_inode` 
	CHANGE inode inode varchar(255) CHARACTER SET utf8;
ALTER TABLE `tag_inode` 
	CHANGE tagname tagname BLOB;
ALTER TABLE `tag_inode` 
	CHANGE tagname tagname varchar(255) CHARACTER SET utf8;
ALTER TABLE `tag_inode` 
	ADD primary key(inode,tagname);
ALTER TABLE `tag_inode` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `template` */

ALTER TABLE `template` 
	CHANGE deleted deleted BLOB;
ALTER TABLE `template` 
	CHANGE deleted deleted varchar(1) CHARACTER SET utf8;
ALTER TABLE `template` 
	CHANGE friendly_name friendly_name BLOB;
ALTER TABLE `template` 
	CHANGE friendly_name friendly_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `template` 
	CHANGE show_on_menu show_on_menu BLOB;
ALTER TABLE `template` 
	CHANGE show_on_menu show_on_menu varchar(1) CHARACTER SET utf8;
ALTER TABLE `template` 
	CHANGE footer footer BLOB;
ALTER TABLE `template` 
	CHANGE footer footer longtext CHARACTER SET utf8;
ALTER TABLE `template` 
	CHANGE mod_user mod_user BLOB;
ALTER TABLE `template` 
	CHANGE mod_user mod_user varchar(100) CHARACTER SET utf8;
ALTER TABLE `template` 
	CHANGE title title BLOB;
ALTER TABLE `template` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `template` 
	CHANGE header header BLOB;
ALTER TABLE `template` 
	CHANGE header header longtext CHARACTER SET utf8;
ALTER TABLE `template` 
	CHANGE live live BLOB;
ALTER TABLE `template` 
	CHANGE live live varchar(1) CHARACTER SET utf8;
ALTER TABLE `template` 
	CHANGE locked locked BLOB;
ALTER TABLE `template` 
	CHANGE locked locked varchar(1) CHARACTER SET utf8;
ALTER TABLE `template` 
	CHANGE working working BLOB;
ALTER TABLE `template` 
	CHANGE working working varchar(1) CHARACTER SET utf8;
ALTER TABLE `template` 
	CHANGE body body BLOB;
ALTER TABLE `template` 
	CHANGE body body longtext CHARACTER SET utf8;
ALTER TABLE `template` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `tree` */

ALTER TABLE `tree` 
	DROP primary key;
ALTER TABLE `tree` 
	CHANGE relation_type relation_type BLOB;
ALTER TABLE `tree` 
	CHANGE relation_type relation_type varchar(64) CHARACTER SET utf8;
ALTER TABLE `tree` 
	ADD PRIMARY KEY (child,parent,relation_type);
ALTER TABLE `tree` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `user_` */

ALTER TABLE `user_` 
	DROP primary key;
ALTER TABLE `user_` 
	CHANGE comments comments BLOB;
ALTER TABLE `user_` 
	CHANGE comments comments longtext CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE lastloginip lastloginip BLOB;
ALTER TABLE `user_` 
	CHANGE lastloginip lastloginip varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE resolution resolution BLOB;
ALTER TABLE `user_` 
	CHANGE resolution resolution varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE loginip loginip BLOB;
ALTER TABLE `user_` 
	CHANGE loginip loginip varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE timezoneid timezoneid BLOB;
ALTER TABLE `user_` 
	CHANGE timezoneid timezoneid varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE icqid icqid BLOB;
ALTER TABLE `user_` 
	CHANGE icqid icqid varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE userid userid BLOB;
ALTER TABLE `user_` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE msnid msnid BLOB;
ALTER TABLE `user_` 
	CHANGE msnid msnid varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE emailaddress emailaddress BLOB;
ALTER TABLE `user_` 
	CHANGE emailaddress emailaddress varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE favoritebibleverse favoritebibleverse BLOB;
ALTER TABLE `user_` 
	CHANGE favoritebibleverse favoritebibleverse varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE password_ password_ BLOB;
ALTER TABLE `user_` 
	CHANGE password_ password_ varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE refreshrate refreshrate BLOB;
ALTER TABLE `user_` 
	CHANGE refreshrate refreshrate varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE nickname nickname BLOB;
ALTER TABLE `user_` 
	CHANGE nickname nickname varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE favoriteactivity favoriteactivity BLOB;
ALTER TABLE `user_` 
	CHANGE favoriteactivity favoriteactivity varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE greeting greeting BLOB;
ALTER TABLE `user_` 
	CHANGE greeting greeting varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE smsid smsid BLOB;
ALTER TABLE `user_` 
	CHANGE smsid smsid varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE aimid aimid BLOB;
ALTER TABLE `user_` 
	CHANGE aimid aimid varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE favoritefood favoritefood BLOB;
ALTER TABLE `user_` 
	CHANGE favoritefood favoritefood varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE ymid ymid BLOB;
ALTER TABLE `user_` 
	CHANGE ymid ymid varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE lastname lastname BLOB;
ALTER TABLE `user_` 
	CHANGE lastname lastname varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `user_` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE layoutids layoutids BLOB;
ALTER TABLE `user_` 
	CHANGE layoutids layoutids varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE favoritemovie favoritemovie BLOB;
ALTER TABLE `user_` 
	CHANGE favoritemovie favoritemovie varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE languageid languageid BLOB;
ALTER TABLE `user_` 
	CHANGE languageid languageid varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE middlename middlename BLOB;
ALTER TABLE `user_` 
	CHANGE middlename middlename varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE firstname firstname BLOB;
ALTER TABLE `user_` 
	CHANGE firstname firstname varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE skinid skinid BLOB;
ALTER TABLE `user_` 
	CHANGE skinid skinid varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	CHANGE favoritemusic favoritemusic BLOB;
ALTER TABLE `user_` 
	CHANGE favoritemusic favoritemusic varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_` 
	ADD primary key(userid);
ALTER TABLE `user_` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `user_comments` */

ALTER TABLE `user_comments` 
	DROP INDEX idx_user_comments_1;
ALTER TABLE `user_comments` 
	CHANGE user_id user_id BLOB;
ALTER TABLE `user_comments` 
	CHANGE user_id user_id varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_comments` 
	CHANGE type type BLOB;
ALTER TABLE `user_comments` 
	CHANGE type type varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_comments` 
	CHANGE comment_user_id comment_user_id BLOB;
ALTER TABLE `user_comments` 
	CHANGE comment_user_id comment_user_id varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_comments` 
	CHANGE subject subject BLOB;
ALTER TABLE `user_comments` 
	CHANGE subject subject varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_comments` 
	CHANGE ucomment ucomment BLOB;
ALTER TABLE `user_comments` 
	CHANGE ucomment ucomment longtext CHARACTER SET utf8;
ALTER TABLE `user_comments` 
	CHANGE method method BLOB;
ALTER TABLE `user_comments` 
	CHANGE method method varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_comments` 
	ADD INDEX idx_user_comments_1(user_id);
ALTER TABLE `user_comments` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `user_filter` */

ALTER TABLE `user_filter` 
	CHANGE phone phone BLOB;
ALTER TABLE `user_filter` 
	CHANGE phone phone varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE active_ active_ BLOB;
ALTER TABLE `user_filter` 
	CHANGE active_ active_ varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var12 var12 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var12 var12 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var7 var7 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var7 var7 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE fax fax BLOB;
ALTER TABLE `user_filter` 
	CHANGE fax fax varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var11 var11 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var11 var11 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var14 var14 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var14 var14 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE emailaddress emailaddress BLOB;
ALTER TABLE `user_filter` 
	CHANGE emailaddress emailaddress varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE zip zip BLOB;
ALTER TABLE `user_filter` 
	CHANGE zip zip varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE createdsince createdsince BLOB;
ALTER TABLE `user_filter` 
	CHANGE createdsince createdsince varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var20 var20 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var20 var20 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE birthdaytypesearch birthdaytypesearch BLOB;
ALTER TABLE `user_filter` 
	CHANGE birthdaytypesearch birthdaytypesearch varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var5 var5 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var5 var5 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var17 var17 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var17 var17 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var23 var23 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var23 var23 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var18 var18 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var18 var18 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var22 var22 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var22 var22 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE firstname firstname BLOB;
ALTER TABLE `user_filter` 
	CHANGE firstname firstname varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE state state BLOB;
ALTER TABLE `user_filter` 
	CHANGE state state varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var15 var15 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var15 var15 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var4 var4 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var4 var4 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var1 var1 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var1 var1 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE title title BLOB;
ALTER TABLE `user_filter` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var9 var9 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var9 var9 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE tagname tagname BLOB;
ALTER TABLE `user_filter` 
	CHANGE tagname tagname varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var2 var2 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var2 var2 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE categories categories BLOB;
ALTER TABLE `user_filter` 
	CHANGE categories categories varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE createdtypesearch createdtypesearch BLOB;
ALTER TABLE `user_filter` 
	CHANGE createdtypesearch createdtypesearch varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var24 var24 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var24 var24 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var13 var13 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var13 var13 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var3 var3 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var3 var3 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var25 var25 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var25 var25 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var19 var19 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var19 var19 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE lastvisittypesearch lastvisittypesearch BLOB;
ALTER TABLE `user_filter` 
	CHANGE lastvisittypesearch lastvisittypesearch varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE city city BLOB;
ALTER TABLE `user_filter` 
	CHANGE city city varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE lastlogintypesearch lastlogintypesearch BLOB;
ALTER TABLE `user_filter` 
	CHANGE lastlogintypesearch lastlogintypesearch varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var6 var6 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var6 var6 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE cell cell BLOB;
ALTER TABLE `user_filter` 
	CHANGE cell cell varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var16 var16 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var16 var16 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var8 var8 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var8 var8 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE lastname lastname BLOB;
ALTER TABLE `user_filter` 
	CHANGE lastname lastname varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE country country BLOB;
ALTER TABLE `user_filter` 
	CHANGE country country varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE lastloginsince lastloginsince BLOB;
ALTER TABLE `user_filter` 
	CHANGE lastloginsince lastloginsince varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE middlename middlename BLOB;
ALTER TABLE `user_filter` 
	CHANGE middlename middlename varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var21 var21 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var21 var21 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE lastvisitsince lastvisitsince BLOB;
ALTER TABLE `user_filter` 
	CHANGE lastvisitsince lastvisitsince varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	CHANGE var10 var10 BLOB;
ALTER TABLE `user_filter` 
	CHANGE var10 var10 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_filter` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `user_preferences` */

ALTER TABLE `user_preferences` 
	DROP INDEX idx_preference_1;
ALTER TABLE `user_preferences` 
	CHANGE user_id user_id BLOB;
ALTER TABLE `user_preferences` 
	CHANGE user_id user_id varchar(100) CHARACTER SET utf8;
ALTER TABLE `user_preferences` 
	CHANGE preference preference BLOB;
ALTER TABLE `user_preferences` 
	CHANGE preference preference varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_preferences` 
	CHANGE pref_value pref_value BLOB;
ALTER TABLE `user_preferences` 
	CHANGE pref_value pref_value longtext CHARACTER SET utf8;
ALTER TABLE `user_preferences` 
	ADD INDEX idx_preference_1(preference);
ALTER TABLE `user_preferences` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `user_proxy` */

ALTER TABLE `user_proxy` 
	DROP INDEX user_id;
ALTER TABLE `user_proxy` 
	CHANGE var1 var1 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var1 var1 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE title title BLOB;
ALTER TABLE `user_proxy` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var9 var9 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var9 var9 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE how_heard how_heard BLOB;
ALTER TABLE `user_proxy` 
	CHANGE how_heard how_heard varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var2 var2 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var2 var2 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var12 var12 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var12 var12 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var7 var7 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var7 var7 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var24 var24 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var24 var24 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var13 var13 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var13 var13 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE company company BLOB;
ALTER TABLE `user_proxy` 
	CHANGE company company varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var3 var3 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var3 var3 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var25 var25 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var25 var25 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE suffix suffix BLOB;
ALTER TABLE `user_proxy` 
	CHANGE suffix suffix varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var11 var11 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var11 var11 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var14 var14 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var14 var14 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE prefix prefix BLOB;
ALTER TABLE `user_proxy` 
	CHANGE prefix prefix varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var19 var19 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var19 var19 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE cqanswer cqanswer BLOB;
ALTER TABLE `user_proxy` 
	CHANGE cqanswer cqanswer varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE school school BLOB;
ALTER TABLE `user_proxy` 
	CHANGE school school varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE organization organization BLOB;
ALTER TABLE `user_proxy` 
	CHANGE organization organization varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE cquestionid cquestionid BLOB;
ALTER TABLE `user_proxy` 
	CHANGE cquestionid cquestionid varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var6 var6 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var6 var6 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE long_lived_cookie long_lived_cookie BLOB;
ALTER TABLE `user_proxy` 
	CHANGE long_lived_cookie long_lived_cookie varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var20 var20 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var20 var20 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var5 var5 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var5 var5 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var16 var16 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var16 var16 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var17 var17 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var17 var17 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var23 var23 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var23 var23 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var8 var8 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var8 var8 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE last_message last_message BLOB;
ALTER TABLE `user_proxy` 
	CHANGE last_message last_message varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE user_id user_id BLOB;
ALTER TABLE `user_proxy` 
	CHANGE user_id user_id varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var18 var18 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var18 var18 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var22 var22 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var22 var22 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var15 var15 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var15 var15 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var21 var21 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var21 var21 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE website website BLOB;
ALTER TABLE `user_proxy` 
	CHANGE website website varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var10 var10 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var10 var10 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	CHANGE var4 var4 BLOB;
ALTER TABLE `user_proxy` 
	CHANGE var4 var4 varchar(255) CHARACTER SET utf8;
ALTER TABLE `user_proxy` 
	ADD UNIQUE INDEX user_id(user_id);
ALTER TABLE `user_proxy` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `users_groups` */

ALTER TABLE `users_groups` 
	CHANGE groupid groupid BLOB;
ALTER TABLE `users_groups` 
	CHANGE groupid groupid varchar(100) CHARACTER SET utf8;
ALTER TABLE `users_groups` 
	CHANGE userid userid BLOB;
ALTER TABLE `users_groups` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `users_groups` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `users_projprojects` */

ALTER TABLE `users_projprojects` 
	CHANGE userid userid BLOB;
ALTER TABLE `users_projprojects` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `users_projprojects` 
	CHANGE projectid projectid BLOB;
ALTER TABLE `users_projprojects` 
	CHANGE projectid projectid varchar(100) CHARACTER SET utf8;
ALTER TABLE `users_projprojects` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `users_projtasks` */

ALTER TABLE `users_projtasks` 
	CHANGE userid userid BLOB;
ALTER TABLE `users_projtasks` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `users_projtasks` 
	CHANGE taskid taskid BLOB;
ALTER TABLE `users_projtasks` 
	CHANGE taskid taskid varchar(100) CHARACTER SET utf8;
ALTER TABLE `users_projtasks` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `users_roles` */

ALTER TABLE `users_roles` 
	CHANGE userid userid BLOB;
ALTER TABLE `users_roles` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `users_roles` 
	CHANGE roleid roleid BLOB;
ALTER TABLE `users_roles` 
	CHANGE roleid roleid varchar(100) CHARACTER SET utf8;
ALTER TABLE `users_roles` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `usertracker` */

ALTER TABLE `usertracker` 
	DROP primary key;
ALTER TABLE `usertracker` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `usertracker` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `usertracker` 
	CHANGE userid userid BLOB;
ALTER TABLE `usertracker` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `usertracker` 
	CHANGE useragent useragent BLOB;
ALTER TABLE `usertracker` 
	CHANGE useragent useragent varchar(100) CHARACTER SET utf8;
ALTER TABLE `usertracker` 
	CHANGE remotehost remotehost BLOB;
ALTER TABLE `usertracker` 
	CHANGE remotehost remotehost varchar(100) CHARACTER SET utf8;
ALTER TABLE `usertracker` 
	CHANGE remoteaddr remoteaddr BLOB;
ALTER TABLE `usertracker` 
	CHANGE remoteaddr remoteaddr varchar(100) CHARACTER SET utf8;
ALTER TABLE `usertracker` 
	CHANGE usertrackerid usertrackerid BLOB;
ALTER TABLE `usertracker` 
	CHANGE usertrackerid usertrackerid varchar(100) CHARACTER SET utf8;
ALTER TABLE `usertracker` 
	ADD primary key(usertrackerid);
ALTER TABLE `usertracker` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `usertrackerpath` */

ALTER TABLE `usertrackerpath` 
	DROP primary key;
ALTER TABLE `usertrackerpath` 
	CHANGE usertrackerpathid usertrackerpathid BLOB;
ALTER TABLE `usertrackerpath` 
	CHANGE usertrackerpathid usertrackerpathid varchar(100) CHARACTER SET utf8;
ALTER TABLE `usertrackerpath` 
	CHANGE path path BLOB;
ALTER TABLE `usertrackerpath` 
	CHANGE path path longtext CHARACTER SET utf8;
ALTER TABLE `usertrackerpath` 
	CHANGE usertrackerid usertrackerid BLOB;
ALTER TABLE `usertrackerpath` 
	CHANGE usertrackerid usertrackerid varchar(100) CHARACTER SET utf8;
ALTER TABLE `usertrackerpath` 
	ADD primary key(usertrackerpathid);
ALTER TABLE `usertrackerpath` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `virtual_link` */

ALTER TABLE `virtual_link` 
	DROP INDEX idx_virtual_link_1;
ALTER TABLE `virtual_link` 
	CHANGE active active BLOB;
ALTER TABLE `virtual_link` 
	CHANGE active active varchar(1) CHARACTER SET utf8;
ALTER TABLE `virtual_link` 
	CHANGE title title BLOB;
ALTER TABLE `virtual_link` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `virtual_link` 
	CHANGE uri uri BLOB;
ALTER TABLE `virtual_link` 
	CHANGE uri uri varchar(255) CHARACTER SET utf8;
ALTER TABLE `virtual_link` 
	CHANGE url url BLOB;
ALTER TABLE `virtual_link` 
	CHANGE url url varchar(255) CHARACTER SET utf8;
ALTER TABLE `virtual_link` 
	ADD INDEX idx_virtual_link_1(url);
ALTER TABLE `virtual_link` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `web_event` */

ALTER TABLE `web_event` 
	DROP INDEX ix_web_event;
ALTER TABLE `web_event` 
	CHANGE comments comments BLOB;
ALTER TABLE `web_event` 
	CHANGE comments comments varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event` 
	CHANGE summary summary BLOB;
ALTER TABLE `web_event` 
	CHANGE summary summary text CHARACTER SET utf8;
ALTER TABLE `web_event` 
	CHANGE partners_only partners_only BLOB;
ALTER TABLE `web_event` 
	CHANGE partners_only partners_only varchar(1) CHARACTER SET utf8;
ALTER TABLE `web_event` 
	CHANGE title title BLOB;
ALTER TABLE `web_event` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event` 
	CHANGE terms_conditions terms_conditions BLOB;
ALTER TABLE `web_event` 
	CHANGE terms_conditions terms_conditions varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event` 
	CHANGE show_on_web show_on_web BLOB;
ALTER TABLE `web_event` 
	CHANGE show_on_web show_on_web varchar(1) CHARACTER SET utf8;
ALTER TABLE `web_event` 
	CHANGE description description BLOB;
ALTER TABLE `web_event` 
	CHANGE description description varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event` 
	CHANGE is_institute is_institute BLOB;
ALTER TABLE `web_event` 
	CHANGE is_institute is_institute varchar(1) CHARACTER SET utf8;
ALTER TABLE `web_event` 
	CHANGE subtitle subtitle BLOB;
ALTER TABLE `web_event` 
	CHANGE subtitle subtitle varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event` 
	ADD INDEX ix_web_event(title);
ALTER TABLE `web_event` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `web_event_attendee` */

ALTER TABLE `web_event_attendee` 
	DROP INDEX ix_web_event_attendee_2,
	DROP INDEX ix_web_event_attendee_1;
ALTER TABLE `web_event_attendee` 
	CHANGE badge_name badge_name BLOB;
ALTER TABLE `web_event_attendee` 
	CHANGE badge_name badge_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event_attendee` 
	CHANGE title title BLOB;
ALTER TABLE `web_event_attendee` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event_attendee` 
	CHANGE email email BLOB;
ALTER TABLE `web_event_attendee` 
	CHANGE email email varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event_attendee` 
	CHANGE last_name last_name BLOB;
ALTER TABLE `web_event_attendee` 
	CHANGE last_name last_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event_attendee` 
	CHANGE first_name first_name BLOB;
ALTER TABLE `web_event_attendee` 
	CHANGE first_name first_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event_attendee` 
	ADD INDEX ix_web_event_attendee_2(last_name),
	ADD INDEX ix_web_event_attendee_1(first_name);
ALTER TABLE `web_event_attendee` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `web_event_location` */

ALTER TABLE `web_event_location` 
	DROP INDEX ix_web_event_location_1,
	DROP INDEX ix_web_event_location;
ALTER TABLE `web_event_location` 
	CHANGE web_reg_active web_reg_active BLOB;
ALTER TABLE `web_event_location` 
	CHANGE web_reg_active web_reg_active varchar(1) CHARACTER SET utf8;
ALTER TABLE `web_event_location` 
	CHANGE almost_at_capacity almost_at_capacity BLOB;
ALTER TABLE `web_event_location` 
	CHANGE almost_at_capacity almost_at_capacity varchar(1) CHARACTER SET utf8;
ALTER TABLE `web_event_location` 
	CHANGE default_contract_partner_price default_contract_partner_price BLOB;
ALTER TABLE `web_event_location` 
	CHANGE default_contract_partner_price default_contract_partner_price varchar(1) CHARACTER SET utf8;
ALTER TABLE `web_event_location` 
	CHANGE show_on_web show_on_web BLOB;
ALTER TABLE `web_event_location` 
	CHANGE show_on_web show_on_web varchar(1) CHARACTER SET utf8;
ALTER TABLE `web_event_location` 
	CHANGE full_capacity full_capacity BLOB;
ALTER TABLE `web_event_location` 
	CHANGE full_capacity full_capacity varchar(1) CHARACTER SET utf8;
ALTER TABLE `web_event_location` 
	CHANGE hotel_name hotel_name BLOB;
ALTER TABLE `web_event_location` 
	CHANGE hotel_name hotel_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event_location` 
	CHANGE short_description short_description BLOB;
ALTER TABLE `web_event_location` 
	CHANGE short_description short_description varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event_location` 
	CHANGE text_email text_email BLOB;
ALTER TABLE `web_event_location` 
	CHANGE text_email text_email text CHARACTER SET utf8;
ALTER TABLE `web_event_location` 
	CHANGE state state BLOB;
ALTER TABLE `web_event_location` 
	CHANGE state state varchar(50) CHARACTER SET utf8;
ALTER TABLE `web_event_location` 
	CHANGE city city BLOB;
ALTER TABLE `web_event_location` 
	CHANGE city city varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event_location` 
	ADD INDEX ix_web_event_location_1(state),
	ADD INDEX ix_web_event_location(city);
ALTER TABLE `web_event_location` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `web_event_registration` */

ALTER TABLE `web_event_registration` 
	CHANGE card_type card_type BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE card_type card_type varchar(50) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE badge_printed badge_printed BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE badge_printed badge_printed varchar(1) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE billing_address_1 billing_address_1 BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE billing_address_1 billing_address_1 varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE billing_contact_email billing_contact_email BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE billing_contact_email billing_contact_email varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE modified_qb modified_qb BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE modified_qb modified_qb varchar(1) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE billing_city billing_city BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE billing_city billing_city varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE invoice_number invoice_number BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE invoice_number invoice_number varchar(50) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE post_email_sent post_email_sent BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE post_email_sent post_email_sent varchar(1) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE billing_country billing_country BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE billing_country billing_country varchar(50) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE card_name card_name BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE card_name card_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE po_number po_number BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE po_number po_number varchar(50) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE how_did_you_hear how_did_you_hear BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE how_did_you_hear how_did_you_hear varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE card_number card_number BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE card_number card_number varchar(50) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE check_bank_name check_bank_name BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE check_bank_name check_bank_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE billing_zip billing_zip BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE billing_zip billing_zip varchar(50) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE check_number check_number BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE check_number check_number varchar(50) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE card_exp_year card_exp_year BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE card_exp_year card_exp_year varchar(50) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE ceo_name ceo_name BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE ceo_name ceo_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE card_verification_value card_verification_value BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE card_verification_value card_verification_value varchar(10) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE billing_contact_name billing_contact_name BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE billing_contact_name billing_contact_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE billing_address_2 billing_address_2 BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE billing_address_2 billing_address_2 varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE billing_state billing_state BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE billing_state billing_state varchar(50) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE reminder_email_sent reminder_email_sent BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE reminder_email_sent reminder_email_sent varchar(1) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE billing_contact_phone billing_contact_phone BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE billing_contact_phone billing_contact_phone varchar(50) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	CHANGE card_exp_month card_exp_month BLOB;
ALTER TABLE `web_event_registration` 
	CHANGE card_exp_month card_exp_month varchar(50) CHARACTER SET utf8;
ALTER TABLE `web_event_registration` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `web_form` */

ALTER TABLE `web_form` 
	DROP INDEX idx_user_webform_1;
ALTER TABLE `web_form` 
	CHANGE phone phone BLOB;
ALTER TABLE `web_form` 
	CHANGE phone phone varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_form` 
	CHANGE title title BLOB;
ALTER TABLE `web_form` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_form` 
	CHANGE form_type form_type BLOB;
ALTER TABLE `web_form` 
	CHANGE form_type form_type varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_form` 
	CHANGE address address BLOB;
ALTER TABLE `web_form` 
	CHANGE address address varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_form` 
	CHANGE middle_name middle_name BLOB;
ALTER TABLE `web_form` 
	CHANGE middle_name middle_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_form` 
	CHANGE address1 address1 BLOB;
ALTER TABLE `web_form` 
	CHANGE address1 address1 varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_form` 
	CHANGE last_name last_name BLOB;
ALTER TABLE `web_form` 
	CHANGE last_name last_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_form` 
	CHANGE middle_initial middle_initial BLOB;
ALTER TABLE `web_form` 
	CHANGE middle_initial middle_initial varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_form` 
	CHANGE address2 address2 BLOB;
ALTER TABLE `web_form` 
	CHANGE address2 address2 varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_form` 
	CHANGE country country BLOB;
ALTER TABLE `web_form` 
	CHANGE country country varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_form` 
	CHANGE prefix prefix BLOB;
ALTER TABLE `web_form` 
	CHANGE prefix prefix varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_form` 
	CHANGE full_name full_name BLOB;
ALTER TABLE `web_form` 
	CHANGE full_name full_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_form` 
	CHANGE email email BLOB;
ALTER TABLE `web_form` 
	CHANGE email email varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_form` 
	CHANGE state state BLOB;
ALTER TABLE `web_form` 
	CHANGE state state varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_form` 
	CHANGE zip zip BLOB;
ALTER TABLE `web_form` 
	CHANGE zip zip varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_form` 
	CHANGE custom_fields custom_fields BLOB;
ALTER TABLE `web_form` 
	CHANGE custom_fields custom_fields text CHARACTER SET utf8;
ALTER TABLE `web_form` 
	CHANGE city city BLOB;
ALTER TABLE `web_form` 
	CHANGE city city varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_form` 
	CHANGE first_name first_name BLOB;
ALTER TABLE `web_form` 
	CHANGE first_name first_name varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_form` 
	CHANGE organization organization BLOB;
ALTER TABLE `web_form` 
	CHANGE organization organization varchar(255) CHARACTER SET utf8;
ALTER TABLE `web_form` 
	ADD INDEX idx_user_webform_1(form_type);
ALTER TABLE `web_form` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `wikidisplay` */

ALTER TABLE `wikidisplay` 
	DROP primary key;
ALTER TABLE `wikidisplay` 
	CHANGE userid userid BLOB;
ALTER TABLE `wikidisplay` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `wikidisplay` 
	CHANGE nodeid nodeid BLOB;
ALTER TABLE `wikidisplay` 
	CHANGE nodeid nodeid varchar(100) CHARACTER SET utf8;
ALTER TABLE `wikidisplay` 
	CHANGE layoutid layoutid BLOB;
ALTER TABLE `wikidisplay` 
	CHANGE layoutid layoutid varchar(100) CHARACTER SET utf8;
ALTER TABLE `wikidisplay` 
	CHANGE portletid portletid BLOB;
ALTER TABLE `wikidisplay` 
	CHANGE portletid portletid varchar(100) CHARACTER SET utf8;
ALTER TABLE `wikidisplay` 
	ADD primary key(layoutid,userid,portletid);
ALTER TABLE `wikidisplay` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `wikinode` */

ALTER TABLE `wikinode` 
	DROP primary key;
ALTER TABLE `wikinode` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `wikinode` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `wikinode` 
	CHANGE userid userid BLOB;
ALTER TABLE `wikinode` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `wikinode` 
	CHANGE readroles readroles BLOB;
ALTER TABLE `wikinode` 
	CHANGE readroles readroles varchar(100) CHARACTER SET utf8;
ALTER TABLE `wikinode` 
	CHANGE writeroles writeroles BLOB;
ALTER TABLE `wikinode` 
	CHANGE writeroles writeroles varchar(100) CHARACTER SET utf8;
ALTER TABLE `wikinode` 
	CHANGE nodeid nodeid BLOB;
ALTER TABLE `wikinode` 
	CHANGE nodeid nodeid varchar(100) CHARACTER SET utf8;
ALTER TABLE `wikinode` 
	CHANGE description description BLOB;
ALTER TABLE `wikinode` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `wikinode` 
	CHANGE username username BLOB;
ALTER TABLE `wikinode` 
	CHANGE username username varchar(100) CHARACTER SET utf8;
ALTER TABLE `wikinode` 
	CHANGE name name BLOB;
ALTER TABLE `wikinode` 
	CHANGE name name varchar(100) CHARACTER SET utf8;
ALTER TABLE `wikinode` 
	ADD primary key(nodeid);
ALTER TABLE `wikinode` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `wikipage` */

ALTER TABLE `wikipage` 
	DROP primary key;
ALTER TABLE `wikipage` 
	CHANGE companyid companyid BLOB;
ALTER TABLE `wikipage` 
	CHANGE companyid companyid varchar(100) CHARACTER SET utf8;
ALTER TABLE `wikipage` 
	CHANGE userid userid BLOB;
ALTER TABLE `wikipage` 
	CHANGE userid userid varchar(100) CHARACTER SET utf8;
ALTER TABLE `wikipage` 
	CHANGE title title BLOB;
ALTER TABLE `wikipage` 
	CHANGE title title varchar(100) CHARACTER SET utf8;
ALTER TABLE `wikipage` 
	CHANGE nodeid nodeid BLOB;
ALTER TABLE `wikipage` 
	CHANGE nodeid nodeid varchar(100) CHARACTER SET utf8;
ALTER TABLE `wikipage` 
	CHANGE content content BLOB;
ALTER TABLE `wikipage` 
	CHANGE content content longtext CHARACTER SET utf8;
ALTER TABLE `wikipage` 
	CHANGE username username BLOB;
ALTER TABLE `wikipage` 
	CHANGE username username varchar(100) CHARACTER SET utf8;
ALTER TABLE `wikipage` 
	CHANGE format format BLOB;
ALTER TABLE `wikipage` 
	CHANGE format format varchar(100) CHARACTER SET utf8;
ALTER TABLE `wikipage` 
	ADD PRIMARY KEY (nodeid,title,version);
ALTER TABLE `wikipage` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `workflow_comment` */

ALTER TABLE `workflow_comment` 
	CHANGE posted_by posted_by BLOB;
ALTER TABLE `workflow_comment` 
	CHANGE posted_by posted_by varchar(255) CHARACTER SET utf8;
ALTER TABLE `workflow_comment` 
	CHANGE wf_comment wf_comment BLOB;
ALTER TABLE `workflow_comment` 
	CHANGE wf_comment wf_comment longtext CHARACTER SET utf8;
ALTER TABLE `workflow_comment` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `workflow_history` */

ALTER TABLE `workflow_history` 
	CHANGE change_desc change_desc BLOB;
ALTER TABLE `workflow_history` 
	CHANGE change_desc change_desc longtext CHARACTER SET utf8;
ALTER TABLE `workflow_history` 
	CHANGE made_by made_by BLOB;
ALTER TABLE `workflow_history` 
	CHANGE made_by made_by varchar(255) CHARACTER SET utf8;
ALTER TABLE `workflow_history` 
	DEFAULT CHARACTER SET utf8;

/* --> TABLE `workflow_task` */

ALTER TABLE `workflow_task` 
	DROP INDEX idx_workflow_2,
	DROP INDEX idx_workflow_1,
	DROP INDEX idx_workflow_3;
ALTER TABLE `workflow_task` 
	CHANGE title title BLOB;
ALTER TABLE `workflow_task` 
	CHANGE title title varchar(255) CHARACTER SET utf8;
ALTER TABLE `workflow_task` 
	CHANGE belongs_to belongs_to BLOB;
ALTER TABLE `workflow_task` 
	CHANGE belongs_to belongs_to varchar(255) CHARACTER SET utf8;
ALTER TABLE `workflow_task` 
	CHANGE description description BLOB;
ALTER TABLE `workflow_task` 
	CHANGE description description longtext CHARACTER SET utf8;
ALTER TABLE `workflow_task` 
	CHANGE status status BLOB;
ALTER TABLE `workflow_task` 
	CHANGE status status varchar(255) CHARACTER SET utf8;
ALTER TABLE `workflow_task` 
	CHANGE assigned_to assigned_to BLOB;
ALTER TABLE `workflow_task` 
	CHANGE assigned_to assigned_to varchar(255) CHARACTER SET utf8;
ALTER TABLE `workflow_task` 
	CHANGE created_by created_by BLOB;
ALTER TABLE `workflow_task` 
	CHANGE created_by created_by varchar(255) CHARACTER SET utf8;
ALTER TABLE `workflow_task` 
	ADD INDEX idx_workflow_2(belongs_to),
	ADD INDEX idx_workflow_1(assigned_to),
	ADD INDEX idx_workflow_3(status);
ALTER TABLE `workflow_task` 
	DEFAULT CHARACTER SET utf8;

ALTER DATABASE dotcms
	DEFAULT CHARACTER SET utf8;

/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
