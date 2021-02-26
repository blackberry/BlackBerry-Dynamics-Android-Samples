/* Copyright (c) 2021 BlackBerry Limited.
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

package com.example.gghangura.adminconsole;

/**
 * Created by gghangura on 2017-03-23.
 */

public class RestUser {
    private String userName;
    private String displayName;
    private String firstName;
    private String lastName;
    private String email;
    private String directoryId;

    public String UserName() {
        return userName;
    }
    public String DisplayName() {
        return displayName;
    }
    public String FirstName() {
        return firstName;
    }
    public String LastName() {
        return lastName;
    }
    public String Email() {
        return email;
    }
    public String DirectoryId() {
        return directoryId;
    }

    RestUser(Object user, Object dis, Object first,Object last, Object ema, Object directory) {
        this.userName = (String) user;
        this.displayName = (String) dis;
        this.firstName = (String) first;
        this.lastName = (String) last;
        this.email = (String) ema;
        this.directoryId = (String) directory;
    }

}
