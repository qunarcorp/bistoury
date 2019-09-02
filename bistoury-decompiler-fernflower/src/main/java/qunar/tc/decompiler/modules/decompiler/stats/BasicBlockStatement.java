/*
 * Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package qunar.tc.decompiler.modules.decompiler.stats;

import qunar.tc.decompiler.code.CodeConstants;
import qunar.tc.decompiler.code.Instruction;
import qunar.tc.decompiler.code.SimpleInstructionSequence;
import qunar.tc.decompiler.code.cfg.BasicBlock;
import qunar.tc.decompiler.main.DecompilerContext;
import qunar.tc.decompiler.main.collectors.BytecodeMappingTracer;
import qunar.tc.decompiler.main.collectors.CounterContainer;
import qunar.tc.decompiler.modules.decompiler.ExprProcessor;
import qunar.tc.decompiler.util.TextBuffer;

public class BasicBlockStatement extends Statement {

    // *****************************************************************************
    // private fields
    // *****************************************************************************

    private final BasicBlock block;

    // *****************************************************************************
    // constructors
    // *****************************************************************************

    public BasicBlockStatement(BasicBlock block) {

        type = Statement.TYPE_BASICBLOCK;

        this.block = block;

        id = block.id;
        CounterContainer coun = DecompilerContext.getCounterContainer();
        if (id >= coun.getCounter(CounterContainer.STATEMENT_COUNTER)) {
            coun.setCounter(CounterContainer.STATEMENT_COUNTER, id + 1);
        }

        Instruction instr = block.getLastInstruction();
        if (instr != null) {
            if (instr.group == CodeConstants.GROUP_JUMP && instr.opcode != CodeConstants.opc_goto) {
                lastBasicType = LASTBASICTYPE_IF;
            } else if (instr.group == CodeConstants.GROUP_SWITCH) {
                lastBasicType = LASTBASICTYPE_SWITCH;
            }
        }

        // monitorenter and monitorexits
        buildMonitorFlags();
    }

    // *****************************************************************************
    // public methods
    // *****************************************************************************

    @Override
    public TextBuffer toJava(int indent, BytecodeMappingTracer tracer) {
        TextBuffer tb = ExprProcessor.listToJava(varDefinitions, indent, tracer);
        tb.append(ExprProcessor.listToJava(exprents, indent, tracer));
        return tb;
    }

    @Override
    public Statement getSimpleCopy() {

        BasicBlock newblock = new BasicBlock(
                DecompilerContext.getCounterContainer().getCounterAndIncrement(CounterContainer.STATEMENT_COUNTER));

        SimpleInstructionSequence seq = new SimpleInstructionSequence();
        for (int i = 0; i < block.getSeq().length(); i++) {
            seq.addInstruction(block.getSeq().getInstr(i).clone(), -1);
        }

        newblock.setSeq(seq);

        return new BasicBlockStatement(newblock);
    }


    // *****************************************************************************
    // getter and setter methods
    // *****************************************************************************

    public BasicBlock getBlock() {
        return block;
    }
}
