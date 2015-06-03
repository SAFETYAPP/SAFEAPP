/*
 * Copyright (c) 2013 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.viewnine.nuttysnap.youtube;

import com.google.android.gms.common.Scopes;
import com.google.api.services.youtube.YouTubeScopes;

public class Auth {
    // Register an API key here: https://code.google.com/apis/console

//    public static final String KEY = "AIzaSyDlXH7Oj0a8U6BH_ObmPpwQ7QwYwSC89sw";

//    public static final String KEY = "AIzaSyDRsvalD7vPnBF32cn40Mwa_6Z752kvwvk"; //Worked

    public static final String KEY = "AIzaSyBZhDeUy8GL_7CQvjEhA98V3ZJx-CcWZEA";


    public static final String[] SCOPES = {Scopes.PROFILE, YouTubeScopes.YOUTUBE};
}
