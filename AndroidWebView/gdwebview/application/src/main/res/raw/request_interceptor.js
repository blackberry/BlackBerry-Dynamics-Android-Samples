/*
 * Copyright (c) 2020 BlackBerry Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
const INTERCEPT_REQUEST_MARKER = 'gdinterceptrequest';

function randowmStr() {
	return Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
}

var serializeForm = function (form) {

	var serialized = [];

	for (var i = 0; i < form.elements.length; i++) {

		var field = form.elements[i];

		if (!field.name || field.disabled || field.type === 'file' || field.type === 'reset' || field.type === 'button') continue;

		if (field.type === 'select-multiple') {
			for (var n = 0; n < field.options.length; n++) {
				if (!field.options[n].selected) continue;
				serialized.push(encodeURIComponent(field.name) + "=" + encodeURIComponent(field.options[n].value));
			}
		}

		else if ((field.type !== 'checkbox' && field.type !== 'radio') || field.checked) {
			serialized.push(encodeURIComponent(field.name) + "=" + encodeURIComponent(field.value));
		}
	}

	return serialized.join('&');
};

function serializeMultipart(formData, boundaryValue) {
	let elements = [];
	formData.forEach((value, name) => {
		elements.push('--' + boundaryValue + '\n' + 'Content-Disposition: form-data; name="' + name + '"\n\n' + value + '\n');
	});
	return elements.join('') + '--' + boundaryValue;
}

function generateRandom() {
	return Math.floor((1 + Math.random()) * 0x10000)
		.toString(16)
		.substring(1);
}

document.addEventListener('submit', function (e) {
	let form = e.srcElement;
	if (form.action && form.method != "get") {
		let body = serializeForm(form);
		let requestId = generateRandom();
		form.action = form.action + INTERCEPT_REQUEST_MARKER + requestId;
		RequestInterceptor.addRequestBody(requestId, body,form.action+'','{"this": "document.submit event"}');
	} else {
        RequestInterceptor.addRequestBody(requestId, "",(form && form.action) || "", '{"this": "document.submit event NO FORM !!!"}');
	}
}, false);

(function (XHR) {

    if(!XHR.prototype.xRequest){
        console.log('xRequest define property');
        Object.defineProperty(XHR.prototype, 'xRequest', {
                value: {
                    url: "",
                    requestId: null
                }
        });
    } else {
        console.log('xRequest defined');
    }

	let open = XHR.prototype.open;
	let send = XHR.prototype.send;
	let setHeader = XHR.prototype.setRequestHeader;

	XHR.prototype.setRequestHeader = function (name, value) {
	    console.log('XHR.prototype.setRequestHeader ' + name + ' ' + value);
		setHeader.call(this, name, value);
		this.headers = this.headers || [];
		this.headers.push({
			'name': name,
			'value': value
		});

        console.log('GD XHR setHeaders addRequestBody url = ' + this.xRequest.url);

		RequestInterceptor.addRequestBody(this.xRequest.requestId, "",this.xRequest.url || "",'{"this": "XHR.prototype.setRequestHeader"}');
	};

	XHR.prototype.open = function (method, url, async, user, pass) {

        async = true;
		console.log('XHR.prototype.open ' + method + ' ' + url + ' ' + async + ' ' + user + ' ' + pass);
        this.xRequest.requestId = generateRandom();
        url += INTERCEPT_REQUEST_MARKER + this.xRequest.requestId;

        this.xRequest.url = url;

        console.log('GD XHR open addRequestBody url = ' + this.xRequest.url);

		RequestInterceptor.addRequestBody(this.xRequest.requestId, "",this.xRequest.url+'','{"this": "XHR.prototype.open"}');

		open.call(this, method, url, async, user, pass);
	};

	XHR.prototype.send = function (data) {
		console.log('XHR.prototype.send reqID : ' + this.xRequest.requestId);
		if (this.xRequest.requestId != null) {
			let body = data;
			if (data instanceof FormData) {
				let boundary = "--WebKitBoundary" + randowmStr();
				body = serializeMultipart(data, boundary);
				this.setRequestHeader('Content-Type', 'multipart/form-data; boundary=' + boundary);
			}

			console.log('GD XHR send addRequestBody url = ' + this.xRequest.url);

			RequestInterceptor.addRequestBody(this.xRequest.requestId, body || "", this.xRequest.url || "", '{"this": "XHR.prototype.send"}');
		} else {
		    RequestInterceptor.addRequestBody("baad", "", this.xRequest.url || "", '{"this": "XHR.prototype.send"}');
		}
		send.call(this, data);
	}


})(XMLHttpRequest);

(function () {
	const originalFetch = window.fetch;

	window.fetch = function () {

	    console.log('GD fetch, url = ' + arguments[0]);

	    let requestId = generateRandom();
	    let url = arguments[0];
		let options = arguments[1];

		url += INTERCEPT_REQUEST_MARKER + requestId;
		arguments[0] = url;

		console.log('GD fetch, requestId = ' + requestId);

		if (options != undefined) {

            console.log('GD fetch, method = ' + options.method);
            console.log('GD fetch, mode = ' + options.mode);

            let fetchMode = ' "mode" : "' + options.mode + '"';

			if (options.method && options.method != 'GET' && options.method != 'HEAD' && options.method != 'OPTIONS') {

				let body = options.body || null;

				if (body instanceof FormData) {

                    let boundary = '----WebKitBoundary' + randowmStr();

				    var serializedFetchBody = [];

				    for (var pair of body.entries()) {
                        serializedFetchBody.push('--' + boundary + '\n' + 'Content-Disposition: form-data; name="' + encodeURIComponent(pair[0]) + '"\n\n' + encodeURIComponent(pair[1]) + '\n');
                    }

                    serializedFetchBody.push('--' + boundary + '--\n');

                    console.log('GD fetch, addBody 1 requestId = ' + requestId + ', ' + serializedFetchBody.join(''));

					RequestInterceptor.addRequestBody(requestId, serializedFetchBody.join(''),url+"",'{"this":"window.fetch","bodyType":"FormData"},' + fetchMode + '}');
				} else {
				    console.log('GD fetch, addBody 2 requestId = ' + requestId);

				    RequestInterceptor.addRequestBody(requestId, body+"",url+"",'{"this":"window.fetch","bodyType": "string",' + fetchMode + '}' );
				}
			} else {
			    console.log('GD fetch addBody, 3 requestId = ' + requestId);

			    RequestInterceptor.addRequestBody(requestId, "",url+"",'{"this":"window.fetch", "method": "'+(!options?'':options.method)+',' + fetchMode + '}');
			}
		}

		return originalFetch.apply(this, arguments);
	}
})();

