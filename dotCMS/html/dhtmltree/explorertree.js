/*

Explorer Tree 1.6
=================
by Andrew Gregory
http://www.scss.com.au/family/andrew/webdesign/explorertree/

This work is licensed under the Creative Commons Attribution License. To view a
copy of this license, visit http://creativecommons.org/licenses/by/1.0/ or send
a letter to Creative Commons, 559 Nathan Abbott Way, Stanford, California 94305,
USA.

IMPORTANT NOTE:
Variables and functions with names starting with an underscore (_) are
'internal' and not to be used.

*/

var explorerTreeAutoCollapse = {'default':false};
var explorerTreeBulletWidth = {'default':20};

//addEvent(window, 'load', explorerTreeRefreshAll, false);

// Refresh all explorer trees
function explorerTreeRefreshAll() {
  // We don't actually need createElement, but we do
  // need good DOM support, so this is a good check.
  //if (!document.createElement) return;
  //alert("Enter in 1");
  var ul, uls = document.getElementsByTagName('ul');
  var ulExplorer;
  for (var uli = 0; uli < uls.length; uli++) {
    ul = uls[uli];
    if (ul.nodeName.toLowerCase() == 'ul' && elementHasClass(ul, 'explorertree')) {
      _explorerTreeInitUL(ul,0);
      ulExplorer = ul;
    }
  }
  /*if (ulExplorer) { 
    var li = _explorerTreeSearchFirstLI(ulExplorer);
	  openLI(li);
  }*/
}

/*DOTMARKETING*/
function openLI(li) {
//alert("Enter in 2");
	  if (li) {
	    if (!window.IE7) {
	        _explorerTreeOpen(li);
	    }
	    if (scroll) {
	      // get height of window we're in
	      var h;
	      if (window.innerHeight) {
	        // Netscape, Mozilla, Opera
	        h = window.innerHeight;
	      } else if (document.documentElement && document.documentElement.clientHeight) {
	        // IE6 in 'standards' mode
	        h = document.documentElement.clientHeight;
	        
	      } else if (document.body && document.body.clientHeight) {
	        // other IEs
	        h = document.body.clientHeight;
	      } else {
	        h = 0;
	      }
	      // scroll so the list item is centered on the window
	      window.scroll(0, li.offsetTop - h / 2);
	    }
   	}
}


function _explorerTreeSearchFirstLI(ul) {
//alert("Enter in 3");
  if (!ul.childNodes || ul.childNodes.length == 0) return null;
  // Iterate LIs
  for (var itemi = 0; itemi < ul.childNodes.length; itemi++) {
    var item = ul.childNodes[itemi];
    if (item.nodeName.toLowerCase() == 'li') {
        return item;
    }
  }
  return null;
}

/*DOTMARKETING*/

// Refresh the specified explorer tree
function explorerTreeRefresh(id) {
//alert("Enter in 4");
  _explorerTreeInitUL(document.getElementById(id),0);
}

// Get the root element (<ul>) of the tree the given element is part of.
function _explorerTreeGetRoot(element) {
//alert("Enter in 5");
  for (var e = element; e != null; e = e.parentNode) {
    if (e.nodeName.toLowerCase() == 'ul' && elementHasClass(e, 'explorertree')) {
      break;
    }
  }
  return e;
}

// Get the ID of the tree the given element is part of. Returns the ID or
// 'default' if there is no ID.
function _explorerTreeGetId(element) {
//alert("Enter in 6");
  var e = _explorerTreeGetRoot(element);
  var id = e ? e.getAttribute('id') : '';
  return (!id || id == '') ? 'default' : id;
}

// Initialise the given list
function _explorerTreeInitUL(ul, level) {
//alert("Enter in 7");
  if (window.IE7) {
  //alert("Enter in 71");
  	return;
  }
  if (navigator.userAgent.indexOf('Gecko') != -1) {
  //alert("Enter in 72");
    addEvent(ul, 'mousedown', _explorerTreeStopGeckoSelect, false);
  }
  if (!ul.childNodes || ul.childNodes.length == 0) return;
  // Iterate LIs
  //alert("Enter in 73");
  for (var itemi = 0; itemi < ul.childNodes.length; itemi++) {
    var item = ul.childNodes[itemi];
    if (item.nodeName.toLowerCase() == 'li') {
      addEvent(item, 'click', _explorerTreeOnClick, false);
      
      // Iterate things in this LI
      var hassubul = false;
      for (var subitemi = 0; subitemi < item.childNodes.length; subitemi++) {
        var subitem = item.childNodes[subitemi];
        if (subitem.nodeName.toLowerCase() == 'a') {
          addEvent(subitem, 'click', _explorerTreeOnClick, false);
        }
        if (subitem.nodeName.toLowerCase() == 'ul') {
          hassubul = true; 
          _explorerTreeInitUL(subitem, level+1);
        }
      }
      if (hassubul) {
        // item is expandable, but don't change it if it's already been set to
        // something else
        if (!elementHasClass(item, 'explorertree-open') &&
            !elementHasClass(item, 'explorertree-bullet')) {
				elementAddClass(item, 'explorertree-closed');
        }
      } else {
        // item has no sub-lists, make sure it's non-expandable
        elementRemoveClass(item, 'explorertree-open');
        elementRemoveClass(item, 'explorertree-closed');
        elementAddClass(item, 'explorertree-bullet');
      }
    }
  }
}

// Gecko selects text when bullets are clicked on - stop it!
function _explorerTreeStopGeckoSelect(evt) {
//alert("Enter in 8");
  if (!evt) var evt = window.event;
  if (evt.preventDefault) {
    evt.preventDefault();
  }
  return true;
}

// Handle clicking on LI and A elements in the tree.
function _explorerTreeOnClick(evt) {
//alert("Enter in 9");
  if (!evt) var evt = window.event;
  var element = (evt.target) ? evt.target : evt.srcElement;
  if (this != element) {
    return true;
  }
  if (element.nodeName.toLowerCase() == 'li') {
    // toggle open/closed state, if possible
		if (elementHasClass(element, 'explorertree-open')) {
	      elementRemoveClass(element, 'explorertree-open');
	      elementAddClass(element, 'explorertree-closed');
	    } else if (elementHasClass(element, 'explorertree-closed')) {
	      elementRemoveClass(element, 'explorertree-closed');
	      elementAddClass(element, 'explorertree-open');
	    } else {
	      return true;
	    }
    if (explorerTreeAutoCollapse[_explorerTreeGetId(element)]) {
      _explorerTreeCollapseAllButElement(element);
    }
  } else if (element.nodeName.toLowerCase() == 'a') {
    // let hyperlinks work as expected
    // TO DO: target support untested!!!
    var href = element.getAttribute('href');
    if (href) {
      var target = element.getAttribute('target');
      if (!target) {
        target = '_self';
      }
      switch (target) {
        case '_blank':
          window.open(href);
          break;
        case '_self':
          window.location.href = href;
          break;
        case '_parent':
          window.parent.location.href = href;
          break;
        case '_top':
          window.top.location.href = href;
          break;
        default:
          window.open(href, target);
          break;
      }
    }
  } else {
    return true;
  }
  // we handled the event - stop it from propagating any further
  evt.cancelBubble = true;
  if (evt.stopPropagation) {
    evt.stopPropagation();
  }
  return false;
}

// Open the specified tree branch
function _explorerTreeOpen(li) {
//alert("Enter in 10");
  if (!elementHasClass(li, 'explorertree-bullet')) {
    elementRemoveClass(li, 'explorertree-closed');
    elementAddClass(li, 'explorertree-open');
  }
}

// Close the specified tree branch
function _explorerTreeClose(li) {
//alert("Enter in 11");
  if (!elementHasClass(li, 'explorertree-bullet')) {
    elementRemoveClass(li, 'explorertree-open');
    elementAddClass(li, 'explorertree-closed');
  }
}

// Collapse the specified tree
function explorerTreeCollapse(id) {
//alert("Enter in 12");
  _explorerTreeSetState(document.getElementById(id), true, null);
}

// Fully expand the specified tree
function explorerTreeExpand(id) {
//alert("Enter in 13");
  if (!explorerTreeAutoCollapse[id]) {
    _explorerTreeSetState(document.getElementById(id), false, null);
  }
}

// Collapse all the branches of tree except for those leading to the specified
// element. 
function _explorerTreeCollapseAllButElement(e) {
//alert("Enter in 14");
  var excluded = new Array();
  var tree = null;
  for (var element = e; element != null; element = element.parentNode) {
    if (element.nodeName.toLowerCase() == 'li') {
      excluded[excluded.length] = element;
    }
    if (element.nodeName.toLowerCase() == 'ul' && elementHasClass(element, 'explorertree')) {
      tree = element;
    }
  }
  if (tree) {
    _explorerTreeSetState(tree, true, excluded)
  }
}

// Set the open/closed state of all the LIs under the tree.
// The excludedElements parameter is used to implement the auto-collapse feature
// that automatically collapses tree branches other than the one actively being
// opened by the user.
function _explorerTreeSetState(ul, collapse, excludedElements) {
//alert("Enter in 15");
  if (window.IE7) return;
  if (!ul.childNodes || ul.childNodes.length == 0) return;
  // Iterate LIs
  for (var itemi = 0; itemi < ul.childNodes.length; itemi++) {
    var item = ul.childNodes[itemi];
    if (item.nodeName.toLowerCase() == 'li') {
      var excluded = false;
      if (excludedElements) {
        for (var exi = 0; exi < excludedElements.length; exi++) {
          if (item == excludedElements[exi]) {
            excluded = true;
            break;
          }
        }
      }
      if (!excluded) {
        if (collapse) {
          _explorerTreeClose(item);
        } else {
          _explorerTreeOpen(item);
        }
      }
      for (var subitemi = 0; subitemi < item.childNodes.length; subitemi++) {
        var subitem = item.childNodes[subitemi];
        if (subitem.nodeName.toLowerCase() == 'ul') {
          _explorerTreeSetState(subitem, collapse, excludedElements);
        }
      }
    }
  }
}

// Open the tree out so the list item with the link with the specified HREF is
// visible. Optionally scrolls so the item is visible. Optionally opens the
// found branch. Returns the LI that contains the specified HREF, or null if
// unsuccessful.
function explorerTreeOpenTo(id, href, scroll, expand) {
//alert("Enter in 16");
  var li = _explorerTreeSearch(document.getElementById(id), _explorerTreeNormalizeHref(href));
  if (li) {
    if (!window.IE7) {
      if (explorerTreeAutoCollapse[id]) {
        _explorerTreeCollapseAllButElement(li);
      }
      if (expand) {
        _explorerTreeOpen(li);
      }
    }
    if (scroll) {
      // get height of window we're in
      var h;
      if (window.innerHeight) {
        // Netscape, Mozilla, Opera
        h = window.innerHeight;
      } else if (document.documentElement && document.documentElement.clientHeight) {
        // IE6 in 'standards' mode
        h = document.documentElement.clientHeight;
        
      } else if (document.body && document.body.clientHeight) {
        // other IEs
        h = document.body.clientHeight;
      } else {
        h = 0;
      }
      // scroll so the list item is centered on the window
      window.scroll(0, li.offsetTop - h / 2);
    }
  }
  return li;
}

// Search the list (and sub-lists) for the given href. Returns the LI object if
// found, otherwise returns null.
function _explorerTreeSearch(ul, href) {
//alert("Enter in 17");
  if (!ul.childNodes || ul.childNodes.length == 0) return null;
  // Iterate LIs
  for (var itemi = 0; itemi < ul.childNodes.length; itemi++) {
    var item = ul.childNodes[itemi];
    if (item.nodeName.toLowerCase() == 'li') {
      for (var subitemi=0; subitemi < item.childNodes.length; subitemi++) {
        var subitem = item.childNodes[subitemi];
        if (subitem.nodeName.toLowerCase() == 'a') {
          if (_explorerTreeNormalizeHref(subitem.getAttribute('href')) == href) {
            return item;
          }
        }
        if (subitem.nodeName.toLowerCase() == 'ul') {
          var found = _explorerTreeSearch(subitem, href);
          if (found) {
            _explorerTreeOpen(item);
            return found;
          }
        }
      }
    }
  }
  return null;
}

// When Opera performs HTMLElement.getAttribute('href'), it *doesn't* actually
// return the raw HREF like it's supposed to. It 'normalizes' it, adding in any
// missing protocol, host name/port, and converts relative HREFs (eg
// '../../index.html') into absolute HREFs (eg '/index.html'). It does exactly
// the same thing in CSS generated content for the attr(href) function. If all
// browsers did that it would make URL comparisons trivial. Unfortunately, other
// browsers don't, and they're probably doing the right thing too by returning
// the href as it appears in the HTML.
// What this function does is normalize HREFs so we can do a meaningful
// comparison in *all* browsers.
function _explorerTreeNormalizeHref(href) {
//alert("Enter in 18");
  var i, h = href, l = window.location;
  
  // immediately return explicit protocols
  if (href.substring(0, 7) == 'telnet:') return href;
  if (href.substring(0, 7) == 'mailto:') return href;
  if (href.substring(0, 7) == 'gopher:') return href;
  if (href.substring(0, 5) == 'http:'  ) return href;
  if (href.substring(0, 5) == 'news:'  ) return href;
  if (href.substring(0, 5) == 'rtsp:'  ) return href;
  
  // handle absolute references
  if (h.charAt(0) == '/') {
    return l.protocol + '//' + l.host + h;
  }
  
  // strip off the filename (if any) of the location to leave the folder we're in
  l = l.toString();
  i = l.lastIndexOf('/');
  if (i != -1) {
    l = l.substring(0, i + 1);
  }
  
  // handle any relative directory references, i.e. '../'
  while (h.substring(0, 3) == '../') {
    h = h.substring(3);
    i = l.lastIndexOf('/', l.length - 2);
    if (i != -1) {
      l = l.substring(0, i + 1);
    }
  }
  
  return l + h;
}
