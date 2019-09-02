// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package qunar.tc.decompiler.modules.decompiler;

import qunar.tc.decompiler.modules.decompiler.exps.Exprent;
import qunar.tc.decompiler.util.ListStack;

public class ExprentStack extends ListStack<Exprent> {

    public ExprentStack() {
    }

    public ExprentStack(ListStack<Exprent> list) {
        super(list);
        pointer = list.getPointer();
    }

    @Override
    public Exprent pop() {

        return this.remove(--pointer);
    }

    @Override
    public ExprentStack clone() {
        return new ExprentStack(this);
    }
}
