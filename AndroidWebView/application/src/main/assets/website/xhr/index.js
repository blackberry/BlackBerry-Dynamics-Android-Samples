
function element(tag, text) {
    const element = document.createElement(tag);
    if (text !== null) {
        const elementText = document.createTextNode(text);
        element.appendChild(elementText);
    }
    return element;
}

function xhrLoaded(changedRequest, resultParent) {
    return function () {
        //alert('Loaded: "' + changedRequest.readyState + '"');
        const response = JSON.parse(changedRequest.responseText);
        resultParent.appendChild(element('pre', JSON.stringify(response)));
    };
}

function xhrError(changedRequest, resultParent) {
    return function () {
        //alert('Error: "' + changedRequest.readyState + '"');
        //const response = JSON.parse(changedRequest.responseText);
        resultParent.appendChild(element('p', "Error."));
    };
}

function getter_listener(numRequests, timeout) { return function (event) {
    event.stopPropagation();
    const requests = Array.from(new Array(numRequests),
                                () => new XMLHttpRequest());
    const parents = Array.from(new Array(numRequests));
    for(const [index, request] of requests.entries()) {
        parents[index] = element('div', String(index + 1));
        document.body.appendChild(parents[index]);
        request.open('GET', 'json/target_' + index + '_.json', true);
        request.setRequestHeader('Content-Type', 'application/json');
        request.onload = xhrLoaded(request, parents[index]);
        request.onerror = xhrError(request, parents[index]);
    }
    function execute(index) {
        parents[index].appendChild(element('span', " sending"));
        requests[index].send();
    }
    for(const index of requests.keys()) {
        setTimeout(execute, (0.0 + index) * timeout * 1000.0, index);
    }
    document.body.appendChild(element('p', "getter_listener finished."));
};}

function click_listener(event) {
    const numRequests = 15;
    const timeout = 0.5;
    const p = element('p', 'Click or tap for XHR. Number:' + numRequests +
                      ' Timeout:' + String((1000.0 * timeout).toFixed(0)));
    p.addEventListener('click', getter_listener(numRequests, timeout));
    document.body.appendChild(p);
    event.stopPropagation();
}

function main(clickID) {
    const clickElement = document.getElementById(clickID);
    if (clickElement === null) {
        document.body.appendChild(element(
            'p', 'getElementById(' + clickID + ') null.'));
        return;
    }
    clickElement.addEventListener('click', click_listener);
}
