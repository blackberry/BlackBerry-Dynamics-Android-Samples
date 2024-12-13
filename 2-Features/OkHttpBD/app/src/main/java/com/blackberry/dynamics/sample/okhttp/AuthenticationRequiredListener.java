/* Copyright (c) 2022 BlackBerry Limited
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

package com.blackberry.dynamics.sample.okhttp;

import com.blackberry.dynamics.sample.okhttp.DialogsUtil.CredentialsCache.AuthType;


/**
 * Used by ReactiveBasicAuthInterceptor to let the UI layer know that a credentials dialog popup
 * need to be shown to the user. The interceptor requests for this when there are no credentials
 * found with it.
 */
public interface AuthenticationRequiredListener {

    void showCredsDialogPopup(AuthType AuthType);
}
