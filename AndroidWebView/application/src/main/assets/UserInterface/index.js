/** Copyright (c) 2018 BlackBerry Ltd.
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

// Placeholders to make the JavaScript appear valid. The bridge values comes
// from the Java layer.
var bridge;
var bridgeSettings;

// Fake bridge objects for running in a browser.
class Bridge {
    getTitle() {
        return "Bridge Test";
    }
    getStr() {
        return "Bridge test class";
    }   
    getButtons() {
        return "{}";
    }
}

class BridgeSettings {
    constructor() {
        this._settings = {'links': [], 'intercept': true, 'injectHTML': false,
            'nslookup': true, 'apacheRedirect':true, 'retrieve':false,
            'debugEnabled': true, 'allowCache':true,
            'stripContentSecurityPolicy':true, 'appKinetics':false
        };
    }
    get() {
        return JSON.stringify(this._settings);
    }
    merge(toMergeJSON) {
        this._settings = JSON.parse(toMergeJSON);
        return this.get();
    }
}

class Utility {
    static element(tag, text) {
        const return_ = document.createElement(tag);
        if (text !== null) {
            const elementText = document.createTextNode(text);
            return_.appendChild(elementText);
        }
        return return_;
    }
    
    static link_element(address) {
        const linkElement = Utility.element('a', address);
        linkElement.setAttribute('href', address);
        return linkElement;
    }
}

class Control {
    constructor(controlName, element) {
        this._controlName = controlName;
        this._label = controlName + ": ";
        this._element = element;
        this.update();
        element.onclick = (event) => {
            event.stopPropagation();
            this._value = !this._value;
            this._element.textContent = 'Updating';
            const merge = {};
            merge[this._controlName] = this._value;
            bridgeSettings.merge(JSON.stringify(merge));
            this.update();
        };
    }
    
    update() {
        this._value = JSON.parse(bridgeSettings.get())[this._controlName];
        const controlString = this._label + this._value;
        this._element.textContent = controlString;
    }
}

class Main {
    constructor() {
        this._controls = [];
    }

    initialise(titleID, controlsID, smallprintID) {
        const u = Utility;
        alert("Test for alert notification to WebChromeClient.");

        const controlsElement = document.getElementById(controlsID);
        if (controlsElement === null) {
            document.body.appendChild(u.element(
                'p', 'getElementById(' + controlsID + ') null.'));
            return;
        }

        const smallprintElement = document.getElementById(smallprintID);
        if (smallprintElement === null) {
            document.body.appendChild(u.element(
                'p', 'getElementById(' + smallprintID + ') null.'));
            return;
        }

        const titleElement = document.getElementById(titleID);
        if (titleElement === null) {
            document.body.appendChild(u.element(
                'p', 'getElementById(' + titleID + ') null.'));
            return;
        }        

        if (bridge === undefined) {
            // This means that the `bridge` hasn't been inserted by the
            // application, which happens when this code is run in a browser for
            // development. Set up the fake bridge.
            bridge = new Bridge();
            bridgeSettings = new BridgeSettings();
        }
        
        titleElement.textContent = bridge.getTitle();
        const controlsJSON = bridgeSettings.get();
        const buttonsJSON = bridge.getButtons();

        let tag = 'p';
        if (controlsElement.tagName.toLowerCase() === 'ul') {
            tag = 'li';
        }

        smallprintElement.appendChild(u.element('p',
            'main(' + titleID + ',' + controlsID + ',' + smallprintID + ') "' +
            tag + '" <' + controlsElement.tagName + '> "' +
            bridge.getStr() + '" controlsJSON"' + controlsJSON +
            '" buttonsJSON"' + buttonsJSON + '"'
        ));

        const controls = JSON.parse(controlsJSON);
        const buttons = JSON.parse(buttonsJSON);

        smallprintElement.appendChild(u.element('p',
            'controls' + JSON.stringify(controls) +
            ' buttons' + JSON.stringify(buttons) + '.'
        ));

        let parent = null;
        for(const link of controls.links) {
            if (parent === null) {
                parent = u.element(tag, null);
                controlsElement.appendChild(parent);
            }
            if (link.startsWith("http")) {
                parent.appendChild(u.link_element(link));
                parent = null;
            }
            else {
                parent.appendChild(u.element('span', link + " "));
            }
        }
        
        for(const controlName of Object.keys(controls).sort()) {
            if (controlName !== "links") {
                const controlElement = u.element(tag, null);
                this._controls.push(new Control(controlName, controlElement));
                controlsElement.appendChild(controlElement);
            }
        }
        
        function callBridge(functionName, resultElement) { return function() {
            resultElement.textContent = " " + (bridge[functionName])();
        };}
            
        for(const buttonName of Object.keys(buttons).sort()) {
            const holder = u.element(tag, null);
            controlsElement.appendChild(holder);
            const button = u.element('button', buttons[buttonName]);
            const result = u.element('span', "");
            button.onclick = callBridge(buttonName, result);
            holder.appendChild(button);
            holder.appendChild(result);
        }
    
        smallprintElement.appendChild(u.element('p', "End of main()."));
    }
}

var main = new Main();
