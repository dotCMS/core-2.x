dojo.provide("dotcms.dojo.data.UsersRolesReadStore");

dojo.require("dojo.data.api.Read");
dojo.require("dojo.data.api.Request");

dojo.declare("dotcms.dojo.data.UsersRolesReadStore", null, {
	
	includeRoles: false,
	assetInode: null,
	permission: 1,
	includeBlankResult: false,
	hideSystemRoles: false,


	constructor: function (options) {
		this.hideSystemRoles = options.hideSystemRoles;
		this.assetInode = options.assetInode;
		this.includeBlankResult = options.includeBlankResult;
		this.includeRoles = options.includeRoles;
		this.permission = options.permission;
		window.top._dotUsersRolesStore = this;
	},

	getValue: function (item, attribute, defaultValue) {
		return item[attribute]?item[attribute]:defaultValue;
	},
	
	getValues: function (item, attribute) {
		return dojo.isArray(item[attribute])?item[attribute]:[item[attribute]]; 
	},
	
	getAttributes: function (item) {
		var attributes = new Array();
		for(att in item) {
			attributes.push(att);
		}
		return attributes;			
	},
	
	hasAttribute: function (item, attribute) {
		return item[attribute] != null;
	},
	
	containsValue: function (item, attribute, value) {
		var values = this.getValues(item, attribute);
		return dojo.indexOf(values, value) >= 0;
	},
	
	
	isItemLoaded: function (item) {
		return this.isItem(item)?true:false;
	},
	
	loadItem: function (keywordArgs) {
		
		var scope = keywordArgs.scope;
		keywordArgs.onItem.call(scope?scope:dojo.global(), keywordArgs.item);
		
	},
	
	fetch: function (keywordArgs) {
		
		var fetchCallback = dojo.hitch(this, this.fetchCallback, keywordArgs);
		
		if(dojo.isString(keywordArgs.query)) {
			keywordArgs.query = { name: keywordArgs.query };
		}
		
		if(this.assetInode) {
			keywordArgs.queryOptions.assetInode = this.assetInode;
		}
		
		if(this.permission) {
			keywordArgs.queryOptions.permission = this.permission;
		}
		
		if(this.hideSystemRoles) {
			keywordArgs.queryOptions.hideSystemRoles = this.hideSystemRoles;
		}
		
		if(this.includeRoles) {
			keywordArgs.queryOptions.includeRoles = this.includeRoles;
		}
		
		if((keywordArgs.query.name == '' || 
				keywordArgs.query.name=='undefined' || 
				keywordArgs.query.name.indexOf('*')===-1) 
				&& (keywordArgs.count == 'undefined' || keywordArgs.count ==null ) 
				&& (keywordArgs.start == 'undefined' || keywordArgs.start ==null) ){
			this.currentRequest.abort = function () { };
			return this.currentRequest;
			
		}else{
		    UserAjax.fetchUsersAndRoles(keywordArgs.query, keywordArgs.queryOptions, keywordArgs.start, keywordArgs.count, fetchCallback);
		    this.currentRequest = keywordArgs;
		    this.currentRequest.abort = function () { };
		    return this.currentRequest;
		}
	},
	
	fetchCallback: function (keywordArgs, results) {
		
		var scope = keywordArgs.scope;
		
		if(this.includeBlankResult) {
			var tempArr = new Array();
			tempArr.push({ name:" ", id:"", emailaddress:"", type:"_blank" });
			results.data = tempArr.concat(results.data);
		}
		
		if(keywordArgs.onBegin) {
			keywordArgs.onBegin.call(scope?scope:dojo.global, results.totalResults, this.currentRequest);
		}
		
		if(keywordArgs.onItem) {
			dojo.forEach(results.list, function (result) {
				keywordArgs.onItem.call(scope?scope:dojo.global, result, this.currentRequest);
			}, this);
		}

		if(keywordArgs.onComplete) {
			keywordArgs.onComplete.call(scope?scope:dojo.global, results.list, this.currentRequest);
		}
	},
	
	getFeatures: function () {
		return { 'dojo.data.api.Read': true };
	},
	
	close: function (request) {
		this.currentRequest = null;
	},
	
	getLabel: function (item) {
		if(item.type == '_blank') return ""; 
 		return item["name"] + " (" + item["type"] + ")";
	},
	
	getLabelAttributes: function (item) {
		return ["name", "type"] 
	},
	
	getIdentity: function (item) {
		if(item.type == '_blank') return ""; 
 		return item["type"] + "-" + item["id"] 
	}
	
});
