/*
 * Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package qunar.tc.decompiler.modules.decompiler.stats;

import qunar.tc.decompiler.main.collectors.BytecodeMappingTracer;
import qunar.tc.decompiler.modules.decompiler.DecHelper;
import qunar.tc.decompiler.modules.decompiler.ExprProcessor;
import qunar.tc.decompiler.modules.decompiler.StatEdge;
import qunar.tc.decompiler.util.TextBuffer;

import java.util.Arrays;
import java.util.List;


public class SequenceStatement extends Statement {


    // *****************************************************************************
    // constructors
    // *****************************************************************************

    private SequenceStatement() {
        type = TYPE_SEQUENCE;
    }

    public SequenceStatement(List<? extends Statement> lst) {

        this();

        lastBasicType = lst.get(lst.size() - 1).getLastBasicType();

        for (Statement st : lst) {
            stats.addWithKey(st, st.id);
        }

        first = stats.get(0);
    }

    private SequenceStatement(Statement head, Statement tail) {

        this(Arrays.asList(head, tail));

        List<StatEdge> lstSuccs = tail.getSuccessorEdges(STATEDGE_DIRECT_ALL);
        if (!lstSuccs.isEmpty()) {
            StatEdge edge = lstSuccs.get(0);

            if (edge.getType() == StatEdge.TYPE_REGULAR && edge.getDestination() != head) {
                post = edge.getDestination();
            }
        }
    }


    // *****************************************************************************
    // public methods
    // *****************************************************************************

    public static Statement isHead2Block(Statement head) {

        if (head.getLastBasicType() != LASTBASICTYPE_GENERAL) {
            return null;
        }

        // at most one outgoing edge
        StatEdge edge = null;
        List<StatEdge> lstSuccs = head.getSuccessorEdges(STATEDGE_DIRECT_ALL);
        if (!lstSuccs.isEmpty()) {
            edge = lstSuccs.get(0);
        }

        if (edge != null && edge.getType() == StatEdge.TYPE_REGULAR) {
            Statement stat = edge.getDestination();

            if (stat != head && stat.getPredecessorEdges(StatEdge.TYPE_REGULAR).size() == 1
                    && !stat.isMonitorEnter()) {

                if (stat.getLastBasicType() == LASTBASICTYPE_GENERAL) {
                    if (DecHelper.checkStatementExceptions(Arrays.asList(head, stat))) {
                        return new SequenceStatement(head, stat);
                    }
                }
            }
        }

        return null;
    }

    @Override
    public TextBuffer toJava(int indent, BytecodeMappingTracer tracer) {
        TextBuffer buf = new TextBuffer();
        boolean islabeled = isLabeled();

        buf.append(ExprProcessor.listToJava(varDefinitions, indent, tracer));

        if (islabeled) {
            buf.appendIndent(indent++).append("label").append(this.id.toString()).append(": {").appendLineSeparator();
            tracer.incrementCurrentSourceLine();
        }

        boolean notempty = false;

        for (int i = 0; i < stats.size(); i++) {

            Statement st = stats.get(i);

            if (i > 0 && notempty) {
                buf.appendLineSeparator();
                tracer.incrementCurrentSourceLine();
            }

            TextBuffer str = ExprProcessor.jmpWrapper(st, indent, false, tracer);
            buf.append(str);

            notempty = !str.containsOnlyWhitespaces();
        }

        if (islabeled) {
            buf.appendIndent(indent - 1).append("}").appendLineSeparator();
            tracer.incrementCurrentSourceLine();
        }

        return buf;
    }

    @Override
    public Statement getSimpleCopy() {
        return new SequenceStatement();
    }
}
