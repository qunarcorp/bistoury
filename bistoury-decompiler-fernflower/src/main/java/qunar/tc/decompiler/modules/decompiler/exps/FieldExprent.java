/*
 * Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
 */
package qunar.tc.decompiler.modules.decompiler.exps;

import qunar.tc.decompiler.code.CodeConstants;
import qunar.tc.decompiler.main.ClassesProcessor.ClassNode;
import qunar.tc.decompiler.main.DecompilerContext;
import qunar.tc.decompiler.main.collectors.BytecodeMappingTracer;
import qunar.tc.decompiler.main.rels.MethodWrapper;
import qunar.tc.decompiler.modules.decompiler.ExprProcessor;
import qunar.tc.decompiler.modules.decompiler.vars.VarVersionPair;
import qunar.tc.decompiler.struct.attr.StructLocalVariableTableAttribute;
import qunar.tc.decompiler.struct.consts.LinkConstant;
import qunar.tc.decompiler.struct.gen.FieldDescriptor;
import qunar.tc.decompiler.struct.gen.VarType;
import qunar.tc.decompiler.struct.match.MatchEngine;
import qunar.tc.decompiler.struct.match.MatchNode;
import qunar.tc.decompiler.util.InterpreterUtil;
import qunar.tc.decompiler.util.TextBuffer;
import qunar.tc.decompiler.util.TextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FieldExprent extends Exprent {
    private final String name;
    private final String classname;
    private final boolean isStatic;
    private Exprent instance;
    private final FieldDescriptor descriptor;

    public FieldExprent(LinkConstant cn, Exprent instance, Set<Integer> bytecodeOffsets) {
        this(cn.elementname, cn.classname, instance == null, instance, FieldDescriptor.parseDescriptor(cn.descriptor), bytecodeOffsets);
    }

    public FieldExprent(String name, String classname, boolean isStatic, Exprent instance, FieldDescriptor descriptor, Set<Integer> bytecodeOffsets) {
        super(EXPRENT_FIELD);
        this.name = name;
        this.classname = classname;
        this.isStatic = isStatic;
        this.instance = instance;
        this.descriptor = descriptor;

        addBytecodeOffsets(bytecodeOffsets);
    }

    @Override
    public VarType getExprType() {
        return descriptor.type;
    }

    @Override
    public int getExprentUse() {
        return 0; // multiple references to a field considered dangerous in a multithreaded environment, thus no Exprent.MULTIPLE_USES set here
    }

    @Override
    public List<Exprent> getAllExprents() {
        List<Exprent> lst = new ArrayList<>();
        if (instance != null) {
            lst.add(instance);
        }
        return lst;
    }

    @Override
    public Exprent copy() {
        return new FieldExprent(name, classname, isStatic, instance == null ? null : instance.copy(), descriptor, bytecode);
    }

    private boolean isAmbiguous() {
        MethodWrapper method = (MethodWrapper) DecompilerContext.getProperty(DecompilerContext.CURRENT_METHOD_WRAPPER);
        if (method != null) {
            StructLocalVariableTableAttribute attr = method.methodStruct.getLocalVariableAttr();
            if (attr != null) {
                return attr.containsName(name);
            }
        }

        return false;
    }

    @Override
    public TextBuffer toJava(int indent, BytecodeMappingTracer tracer) {
        TextBuffer buf = new TextBuffer();

        if (isStatic) {
            ClassNode node = (ClassNode) DecompilerContext.getProperty(DecompilerContext.CURRENT_CLASS_NODE);
            if (node == null || !classname.equals(node.classStruct.qualifiedName) || isAmbiguous()) {
                buf.append(DecompilerContext.getImportCollector().getShortNameInClassContext(ExprProcessor.buildJavaClassName(classname)));
                buf.append(".");
            }
        } else {
            String super_qualifier = null;

            if (instance != null && instance.type == Exprent.EXPRENT_VAR) {
                VarExprent instVar = (VarExprent) instance;
                VarVersionPair pair = new VarVersionPair(instVar);

                MethodWrapper currentMethod = (MethodWrapper) DecompilerContext.getProperty(DecompilerContext.CURRENT_METHOD_WRAPPER);

                if (currentMethod != null) { // FIXME: remove
                    String this_classname = currentMethod.varproc.getThisVars().get(pair);

                    if (this_classname != null) {
                        if (!classname.equals(this_classname)) { // TODO: direct comparison to the super class?
                            super_qualifier = this_classname;
                        }
                    }
                }
            }

            if (super_qualifier != null) {
                TextUtil.writeQualifiedSuper(buf, super_qualifier);
            } else {
                TextBuffer buff = new TextBuffer();
                boolean casted = ExprProcessor.getCastedExprent(instance, new VarType(CodeConstants.TYPE_OBJECT, 0, classname), buff, indent, true, tracer);
                String res = buff.toString();

                if (casted || instance.getPrecedence() > getPrecedence()) {
                    res = "(" + res + ")";
                }

                buf.append(res);
            }

            if (buf.toString().equals(
                    VarExprent.VAR_NAMELESS_ENCLOSURE)) { // FIXME: workaround for field access of an anonymous enclosing class. Find a better way.
                buf.setLength(0);
            } else {
                buf.append(".");
            }
        }

        buf.append(name);

        tracer.addMapping(bytecode);

        return buf;
    }

    @Override
    public void replaceExprent(Exprent oldExpr, Exprent newExpr) {
        if (oldExpr == instance) {
            instance = newExpr;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof FieldExprent)) return false;

        FieldExprent ft = (FieldExprent) o;
        return InterpreterUtil.equalObjects(name, ft.getName()) &&
                InterpreterUtil.equalObjects(classname, ft.getClassname()) &&
                isStatic == ft.isStatic() &&
                InterpreterUtil.equalObjects(instance, ft.getInstance()) &&
                InterpreterUtil.equalObjects(descriptor, ft.getDescriptor());
    }

    public String getClassname() {
        return classname;
    }

    public FieldDescriptor getDescriptor() {
        return descriptor;
    }

    public Exprent getInstance() {
        return instance;
    }

    public boolean isStatic() {
        return isStatic;
    }

    public String getName() {
        return name;
    }

    // *****************************************************************************
    // IMatchable implementation
    // *****************************************************************************

    @Override
    public boolean match(MatchNode matchNode, MatchEngine engine) {
        if (!super.match(matchNode, engine)) {
            return false;
        }

        MatchNode.RuleValue rule = matchNode.getRules().get(MatchProperties.EXPRENT_FIELD_NAME);
        if (rule != null) {
            if (rule.isVariable()) {
                return engine.checkAndSetVariableValue((String) rule.value, this.name);
            } else {
                return rule.value.equals(this.name);
            }
        }

        return true;
    }
}