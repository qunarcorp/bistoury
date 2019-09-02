// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package qunar.tc.decompiler.code;

import qunar.tc.decompiler.util.VBStyleCollection;

public class SimpleInstructionSequence extends InstructionSequence {

    public SimpleInstructionSequence() {
    }

    public SimpleInstructionSequence(VBStyleCollection<Instruction, Integer> collinstr) {
        super(collinstr);
    }

    @Override
    public SimpleInstructionSequence clone() {
        SimpleInstructionSequence newseq = new SimpleInstructionSequence(collinstr.clone());
        newseq.setPointer(this.getPointer());

        return newseq;
    }
}
