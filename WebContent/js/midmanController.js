var reqUrl;
var frmData;
$(document).ready(function() {
	$("form").submit(function(event) {
		var $form = $(this);
		reqUrl = $form.prop('action');
		console.log($form.serialize());
		frmData = JSON.stringify($form.serializeArray());
		var proxyUrl = window.location.href;
		event.preventDefault();

		var posting = $.post(proxyUrl, {
			requestedUrl : reqUrl,
			formData : frmData
		});

		posting.done(function(data) {

			$("#result").empty().append($(data));
		});

	});
});

$(document).ready(function() {
	$("a").click(function(event) {
		var link = $(this).prop('href');
		var proxyUrl = window.location.href;
		event.preventDefault();

		var posting = $.get(proxyUrl, {
			requestedUrl : link
		});

		posting.done(function(data) {

			$("#result").empty().append($(data));
		});
	});
});

var methodType;
var urlType;
XMLHttpRequest.prototype.realSend = XMLHttpRequest.prototype.send;
var newSend = function(vData) {
	
	var postParams=vData;
	if (vData.indexOf("formData") < 0) {
	postParams="requestedUrl="+reqUrl+"&urlType="+urlType+"&formData="+QueryStringToJSON(vData)+"";
	}
	console.log("vdata:====>>>>>>> \n"+postParams);
	this.realSend(postParams);
	
	
};
XMLHttpRequest.prototype.send = newSend;

XMLHttpRequest.prototype.realOpen = XMLHttpRequest.prototype.open;
var overriddenOpen = function(method, uri, async) {

	if (uri.indexOf('http') == 0) {
		urlType = 'absolute';
	} else {
		urlType = 'relative';
	}

	if (method == 'GET') {

		uri = window.location.href + "urlType=" + urlType + "&requestedUrl="
				+ uri;
	} else if (method == 'POST') {
		methodType = method;
		reqUrl = uri;
		uri = window.location.href;

	}

	this.realOpen(method, uri, true);
};
XMLHttpRequest.prototype.open = overriddenOpen;

function QueryStringToJSON(vData) {
	var pairs = vData.split('&');

	var postParamString=[];
	var i=0;
	pairs.forEach(function(pair) {
		pair = pair.split('=');
		var result = {};
		result['name'] = decodeURIComponent(pair[0] || '');
		result['value'] = decodeURIComponent(pair[1] || '');
		postParamString[i++]=result;
	});

	return JSON.stringify(postParamString);
}
