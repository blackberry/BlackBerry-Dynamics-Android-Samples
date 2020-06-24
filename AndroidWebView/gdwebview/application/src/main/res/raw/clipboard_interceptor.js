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
document.addEventListener('paste',function(e) {
    if (e.clipboardData && e.clipboardData.getData) {
        event.preventDefault();
        let pastedText = AndroidClipboardListener.onTextPaste();
        if (pastedText) {
            const selection = document.getSelection();
            if (!selection.rangeCount) return false;
            selection.deleteFromDocument();
            let start = e.srcElement.selectionStart;
            let end = e.srcElement.selectionEnd;
            e.srcElement.setRangeText(pastedText, start, end);
        }
    }
}, false);

document.addEventListener('copy', function (e) {
    if (e.clipboardData && e.clipboardData.getData) {
        let copiedText = document.getSelection().toString();
        if (!AndroidClipboardListener.onTextCopy(copiedText)) {
            e.preventDefault();
        }
    }
}, false);

document.addEventListener('cut', e => {
    if (e.clipboardData && e.clipboardData.getData) {
        let cutText = document.getSelection().toString();
        if (!AndroidClipboardListener.onTextCut(cutText)) {
            e.stopPropagation();
            e.preventDefault();
            let start = e.srcElement.selectionStart;
            let end = e.srcElement.selectionEnd;
            e.srcElement.setRangeText('', start, end);
        }
    }
}, false);
