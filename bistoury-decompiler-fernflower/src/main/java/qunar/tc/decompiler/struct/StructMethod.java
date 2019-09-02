// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package qunar.tc.decompiler.struct;

import qunar.tc.decompiler.code.*;
import qunar.tc.decompiler.struct.attr.StructGeneralAttribute;
import qunar.tc.decompiler.struct.attr.StructLocalVariableTableAttribute;
import qunar.tc.decompiler.struct.consts.ConstantPool;
import qunar.tc.decompiler.util.DataInputFullStream;
import qunar.tc.decompiler.util.VBStyleCollection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/*
  method_info {
    u2 access_flags;
    u2 name_index;
    u2 descriptor_index;
    u2 attributes_count;
    attribute_info attributes[attributes_count];
  }
*/
public class StructMethod extends StructMember {
    private static final int[] opr_iconst = {-1, 0, 1, 2, 3, 4, 5};
    private static final int[] opr_loadstore = {0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3, 0, 1, 2, 3};
    private static final int[] opcs_load = {CodeConstants.opc_iload, CodeConstants.opc_lload, CodeConstants.opc_fload, CodeConstants.opc_dload, CodeConstants.opc_aload};
    private static final int[] opcs_store = {CodeConstants.opc_istore, CodeConstants.opc_lstore, CodeConstants.opc_fstore, CodeConstants.opc_dstore, CodeConstants.opc_astore};

    private final StructClass classStruct;
    private final String name;
    private final String descriptor;

    private boolean containsCode = false;
    private int localVariables = 0;
    private int codeLength = 0;
    private int codeFullLength = 0;
    private InstructionSequence seq;
    private boolean expanded = false;
    private Map<String, StructGeneralAttribute> codeAttributes;

    public StructMethod(DataInputFullStream in, StructClass clStruct) throws IOException {
        classStruct = clStruct;

        accessFlags = in.readUnsignedShort();
        int nameIndex = in.readUnsignedShort();
        int descriptorIndex = in.readUnsignedShort();

        ConstantPool pool = clStruct.getPool();
        String[] values = pool.getClassElement(ConstantPool.METHOD, clStruct.qualifiedName, nameIndex, descriptorIndex);
        name = values[0];
        descriptor = values[1];

        attributes = readAttributes(in, pool);
        if (codeAttributes != null) {
            attributes.putAll(codeAttributes);
            codeAttributes = null;
        }
    }

    @Override
    protected StructGeneralAttribute readAttribute(DataInputFullStream in, ConstantPool pool, String name) throws IOException {
        if (StructGeneralAttribute.ATTRIBUTE_CODE.getName().equals(name)) {
            if (!classStruct.isOwn()) {
                // skip code in foreign classes
                in.discard(8);
                in.discard(in.readInt());
                in.discard(8 * in.readUnsignedShort());
            } else {
                containsCode = true;
                in.discard(6);
                localVariables = in.readUnsignedShort();
                codeLength = in.readInt();
                in.discard(codeLength);
                int excLength = in.readUnsignedShort();
                in.discard(excLength * 8);
                codeFullLength = codeLength + excLength * 8 + 2;
            }

            codeAttributes = readAttributes(in, pool);

            return null;
        }

        return super.readAttribute(in, pool, name);
    }

    public void expandData() throws IOException {
        if (containsCode && !expanded) {
            byte[] code = classStruct.getLoader().loadBytecode(this, codeFullLength);
            seq = parseBytecode(new DataInputFullStream(code), codeLength, classStruct.getPool());
            expanded = true;
        }
    }

    public void releaseResources() {
        if (containsCode && expanded) {
            seq = null;
            expanded = false;
        }
    }

    @SuppressWarnings("AssignmentToForLoopParameter")
    private InstructionSequence parseBytecode(DataInputFullStream in, int length, ConstantPool pool) throws IOException {
        VBStyleCollection<Instruction, Integer> instructions = new VBStyleCollection<>();

        int bytecode_version = classStruct.getBytecodeVersion();

        for (int i = 0; i < length; ) {

            int offset = i;

            int opcode = in.readUnsignedByte();
            int group = CodeConstants.GROUP_GENERAL;

            boolean wide = (opcode == CodeConstants.opc_wide);

            if (wide) {
                i++;
                opcode = in.readUnsignedByte();
            }

            List<Integer> operands = new ArrayList<>();

            if (opcode >= CodeConstants.opc_iconst_m1 && opcode <= CodeConstants.opc_iconst_5) {
                operands.add(opr_iconst[opcode - CodeConstants.opc_iconst_m1]);
                opcode = CodeConstants.opc_bipush;
            } else if (opcode >= CodeConstants.opc_iload_0 && opcode <= CodeConstants.opc_aload_3) {
                operands.add(opr_loadstore[opcode - CodeConstants.opc_iload_0]);
                opcode = opcs_load[(opcode - CodeConstants.opc_iload_0) / 4];
            } else if (opcode >= CodeConstants.opc_istore_0 && opcode <= CodeConstants.opc_astore_3) {
                operands.add(opr_loadstore[opcode - CodeConstants.opc_istore_0]);
                opcode = opcs_store[(opcode - CodeConstants.opc_istore_0) / 4];
            } else {
                switch (opcode) {
                    case CodeConstants.opc_bipush:
                        operands.add((int) in.readByte());
                        i++;
                        break;
                    case CodeConstants.opc_ldc:
                    case CodeConstants.opc_newarray:
                        operands.add(in.readUnsignedByte());
                        i++;
                        break;
                    case CodeConstants.opc_sipush:
                    case CodeConstants.opc_ifeq:
                    case CodeConstants.opc_ifne:
                    case CodeConstants.opc_iflt:
                    case CodeConstants.opc_ifge:
                    case CodeConstants.opc_ifgt:
                    case CodeConstants.opc_ifle:
                    case CodeConstants.opc_if_icmpeq:
                    case CodeConstants.opc_if_icmpne:
                    case CodeConstants.opc_if_icmplt:
                    case CodeConstants.opc_if_icmpge:
                    case CodeConstants.opc_if_icmpgt:
                    case CodeConstants.opc_if_icmple:
                    case CodeConstants.opc_if_acmpeq:
                    case CodeConstants.opc_if_acmpne:
                    case CodeConstants.opc_goto:
                    case CodeConstants.opc_jsr:
                    case CodeConstants.opc_ifnull:
                    case CodeConstants.opc_ifnonnull:
                        if (opcode != CodeConstants.opc_sipush) {
                            group = CodeConstants.GROUP_JUMP;
                        }
                        operands.add((int) in.readShort());
                        i += 2;
                        break;
                    case CodeConstants.opc_ldc_w:
                    case CodeConstants.opc_ldc2_w:
                    case CodeConstants.opc_getstatic:
                    case CodeConstants.opc_putstatic:
                    case CodeConstants.opc_getfield:
                    case CodeConstants.opc_putfield:
                    case CodeConstants.opc_invokevirtual:
                    case CodeConstants.opc_invokespecial:
                    case CodeConstants.opc_invokestatic:
                    case CodeConstants.opc_new:
                    case CodeConstants.opc_anewarray:
                    case CodeConstants.opc_checkcast:
                    case CodeConstants.opc_instanceof:
                        operands.add(in.readUnsignedShort());
                        i += 2;
                        if (opcode >= CodeConstants.opc_getstatic && opcode <= CodeConstants.opc_putfield) {
                            group = CodeConstants.GROUP_FIELDACCESS;
                        } else if (opcode >= CodeConstants.opc_invokevirtual && opcode <= CodeConstants.opc_invokestatic) {
                            group = CodeConstants.GROUP_INVOCATION;
                        }
                        break;
                    case CodeConstants.opc_invokedynamic:
                        if (classStruct.isVersionGE_1_7()) { // instruction unused in Java 6 and before
                            operands.add(in.readUnsignedShort());
                            in.discard(2);
                            group = CodeConstants.GROUP_INVOCATION;
                            i += 4;
                        }
                        break;
                    case CodeConstants.opc_iload:
                    case CodeConstants.opc_lload:
                    case CodeConstants.opc_fload:
                    case CodeConstants.opc_dload:
                    case CodeConstants.opc_aload:
                    case CodeConstants.opc_istore:
                    case CodeConstants.opc_lstore:
                    case CodeConstants.opc_fstore:
                    case CodeConstants.opc_dstore:
                    case CodeConstants.opc_astore:
                    case CodeConstants.opc_ret:
                        if (wide) {
                            operands.add(in.readUnsignedShort());
                            i += 2;
                        } else {
                            operands.add(in.readUnsignedByte());
                            i++;
                        }
                        if (opcode == CodeConstants.opc_ret) {
                            group = CodeConstants.GROUP_RETURN;
                        }
                        break;
                    case CodeConstants.opc_iinc:
                        if (wide) {
                            operands.add(in.readUnsignedShort());
                            operands.add((int) in.readShort());
                            i += 4;
                        } else {
                            operands.add(in.readUnsignedByte());
                            operands.add((int) in.readByte());
                            i += 2;
                        }
                        break;
                    case CodeConstants.opc_goto_w:
                    case CodeConstants.opc_jsr_w:
                        opcode = opcode == CodeConstants.opc_jsr_w ? CodeConstants.opc_jsr : CodeConstants.opc_goto;
                        operands.add(in.readInt());
                        group = CodeConstants.GROUP_JUMP;
                        i += 4;
                        break;
                    case CodeConstants.opc_invokeinterface:
                        operands.add(in.readUnsignedShort());
                        operands.add(in.readUnsignedByte());
                        in.discard(1);
                        group = CodeConstants.GROUP_INVOCATION;
                        i += 4;
                        break;
                    case CodeConstants.opc_multianewarray:
                        operands.add(in.readUnsignedShort());
                        operands.add(in.readUnsignedByte());
                        i += 3;
                        break;
                    case CodeConstants.opc_tableswitch:
                        in.discard((4 - (i + 1) % 4) % 4);
                        i += ((4 - (i + 1) % 4) % 4); // padding
                        operands.add(in.readInt());
                        i += 4;
                        int low = in.readInt();
                        operands.add(low);
                        i += 4;
                        int high = in.readInt();
                        operands.add(high);
                        i += 4;

                        for (int j = 0; j < high - low + 1; j++) {
                            operands.add(in.readInt());
                            i += 4;
                        }
                        group = CodeConstants.GROUP_SWITCH;

                        break;
                    case CodeConstants.opc_lookupswitch:
                        in.discard((4 - (i + 1) % 4) % 4);
                        i += ((4 - (i + 1) % 4) % 4); // padding
                        operands.add(in.readInt());
                        i += 4;
                        int npairs = in.readInt();
                        operands.add(npairs);
                        i += 4;

                        for (int j = 0; j < npairs; j++) {
                            operands.add(in.readInt());
                            i += 4;
                            operands.add(in.readInt());
                            i += 4;
                        }
                        group = CodeConstants.GROUP_SWITCH;
                        break;
                    case CodeConstants.opc_ireturn:
                    case CodeConstants.opc_lreturn:
                    case CodeConstants.opc_freturn:
                    case CodeConstants.opc_dreturn:
                    case CodeConstants.opc_areturn:
                    case CodeConstants.opc_return:
                    case CodeConstants.opc_athrow:
                        group = CodeConstants.GROUP_RETURN;
                }
            }

            int[] ops = null;
            if (!operands.isEmpty()) {
                ops = new int[operands.size()];
                for (int j = 0; j < operands.size(); j++) {
                    ops[j] = operands.get(j);
                }
            }

            Instruction instr = Instruction.create(opcode, wide, group, bytecode_version, ops);

            instructions.addWithKey(instr, offset);

            i++;
        }

        // initialize exception table
        List<ExceptionHandler> lstHandlers = new ArrayList<>();

        int exception_count = in.readUnsignedShort();
        for (int i = 0; i < exception_count; i++) {
            ExceptionHandler handler = new ExceptionHandler();
            handler.from = in.readUnsignedShort();
            handler.to = in.readUnsignedShort();
            handler.handler = in.readUnsignedShort();

            int excclass = in.readUnsignedShort();
            if (excclass != 0) {
                handler.exceptionClass = pool.getPrimitiveConstant(excclass).getString();
            }

            lstHandlers.add(handler);
        }

        InstructionSequence seq = new FullInstructionSequence(instructions, new ExceptionTable(lstHandlers));

        // initialize instructions
        int i = seq.length() - 1;
        seq.setPointer(i);

        while (i >= 0) {
            Instruction instr = seq.getInstr(i--);
            if (instr.group != CodeConstants.GROUP_GENERAL) {
                instr.initInstruction(seq);
            }
            seq.addToPointer(-1);
        }

        return seq;
    }

    public StructClass getClassStruct() {
        return classStruct;
    }

    public String getName() {
        return name;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public boolean containsCode() {
        return containsCode;
    }

    public int getLocalVariables() {
        return localVariables;
    }

    public InstructionSequence getInstructionSequence() {
        return seq;
    }

    public StructLocalVariableTableAttribute getLocalVariableAttr() {
        return getAttribute(StructGeneralAttribute.ATTRIBUTE_LOCAL_VARIABLE_TABLE);
    }

    @Override
    public String toString() {
        return name;
    }
}