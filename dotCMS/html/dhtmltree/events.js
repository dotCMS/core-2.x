// Cross-browser event handling
// by Scott Andrew LePera
// http://www.scottandrew.com/weblog/articles/cbs-events

// Modified 2004-08-10 by Andrew Grgeory to work around Konqueror bug
// Modified 2004-06-04 by Andrew Gregory to support legacy (NS3,4) browsers
// http://www.scss.com.au/family/andrew/

// eg. addEvent(imgObj, 'mousedown', processEvent, false);
function addEvent(obj, evType, fn, useCapture) {
  // work around Konqueror bug #57913 which prevents
  // window.addEventListener('load',...) from working
  var ua = navigator.userAgent;
  var konq = ua.indexOf('KHTML') != -1 && ua.indexOf('Safari') == -1 && obj == window && evType == 'load';
  // don't use addEventListener for Konq, have Konq fall back to the old
  // obj.onload method
  if (obj.addEventListener && !konq) {
    obj.addEventListener(evType, fn, useCapture);
    return true;
  } else if (obj.attachEvent) {
    return obj.attachEvent('on' + evType, fn);
  } else {
    if (!obj.cb_events) {
      obj.cb_events = new Object();
      obj.cb_ftemp = null;
    }
    var events = obj.cb_events[evType];
    if (!events) {
      events = new Array();
      obj.cb_events[evType] = events;
    }
    var i = 0;
    while ((i < events.length) && (events[i] != fn)) {
      i++;
    }
    if (i == events.length) {
      events[i] = fn;
      obj['on' + evType] = new Function("var ret=false,e=this.cb_events['"+evType+"'];if(e){for(var i=0;i<e.length;i++){this.cb_ftemp=e[i];ret=this.cb_ftemp()||ret;}return ret;}");
    }
    return true;
  }
}

// eg. removeEvent(imgObj, 'mousedown', processEvent, false);
function removeEvent(obj, evType, fn, useCapture) {
  // work around Konqueror bug #57913 which prevents
  // window.addEventListener('load',...) from working
  var ua = navigator.userAgent;
  var konq = ua.indexOf('KHTML') != -1 && ua.indexOf('Safari') == -1 && obj == window && evType == 'load';
  // don't use addEventListener for Konq, have Konq fall back to the old
  // obj.onload method
  if (obj.removeEventListener && !konq) {
    obj.removeEventListener(evType, fn, useCapture);
    return true;
  } else if (obj.detachEvent) {
    return obj.detachEvent('on' + evType, fn);
  } else {
    var ret = false;
    if (obj.cb_events) {
      var events = obj.cb_events[evType];
      if (events) {
        // remove any matching functions from the events array, shuffling items
        // down to fill in the space before truncating the array
        var dest = 0;
        for (var src = 0; src < events.length; src++) {
          if (dest != src) {
            events[dest] = events[src];
          }
          if (events[dest] == fn) {
            ret = true;
          } else {
            dest++;
          }
        }
        events.length = dest;
      }
    }
    return ret;
  }
}
