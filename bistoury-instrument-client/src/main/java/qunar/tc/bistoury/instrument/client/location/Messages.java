/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package qunar.tc.bistoury.instrument.client.location;

final class Messages {
    public static final String UNDEFINED_BREAKPOINT_LOCATION =
            "Snapshot location not defined";

    public static final String BREAKPOINT_ONLY_SUPPORTS_JAVA_FILES =
            "Only files with .java extension are supported";

    public static final String SOURCE_FILE_NOT_FOUND_IN_EXECUTABLE =
            "File was not found in the executable";

    public static final String INVALID_LINE_NUMBER =
            "Invalid line number %s";

    public static final String NO_CODE_FOUND_AT_LINE =
            "No code found at line %s";
}

