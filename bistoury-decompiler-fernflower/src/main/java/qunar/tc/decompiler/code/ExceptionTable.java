// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package qunar.tc.decompiler.code;

import java.util.Collections;
import java.util.List;

public class ExceptionTable {
    public static final ExceptionTable EMPTY = new ExceptionTable(Collections.<ExceptionHandler>emptyList());

    private final List<ExceptionHandler> handlers;

    public ExceptionTable(List<ExceptionHandler> handlers) {
        this.handlers = handlers;
    }

    public List<ExceptionHandler> getHandlers() {
        return handlers;
    }
}