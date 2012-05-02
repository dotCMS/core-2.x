/*
CSS Utilities
by Andrew Gregory
http://www.scss.com.au/family/andrew/

I have placed this code in the public domain. Feel free to use it however you
wish.

v1.3  6-Oct-2004 Added el.className checks
v1.2  5-Aug-2004 Simplified code by using regular expressions.
v1.1 12-Apr-2004 Fixed bug in elementRemoveClass() which removed partially matching classnames.
v1.0 29-Mar-2004 Initial version. Allows non-destructive setting and removal of CSS class names.
*/
// Test if an element has the given CSS class
function elementHasClass(el,cl){return (el.className&&el.className.search(new RegExp('\\b'+cl+'\\b'))>-1);}
// Ensure an element has the given CSS class
function elementAddClass(el,cl){var c=el.className;if(!c)c='';if(!elementHasClass(el,cl))c+=((c.length>0)?' ':'')+cl;el.className=c;}
// Ensure an element no longer has the given CSS class 
function elementRemoveClass(el,cl){if(el.className)el.className=el.className.replace(new RegExp('\\s*\\b'+cl+'\\b\\s*'),' ').replace(/^\s*/,'').replace(/\s*$/,'');}
