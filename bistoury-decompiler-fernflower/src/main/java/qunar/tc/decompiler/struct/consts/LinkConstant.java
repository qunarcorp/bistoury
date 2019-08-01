// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package qunar.tc.decompiler.struct.consts;

import qunar.tc.decompiler.code.CodeConstants;

public class LinkConstant extends PooledConstant {
    public int index1, index2;
    public String classname;
    public String elementname;
    public String descriptor;

    public LinkConstant(int type, String classname, String elementname, String descriptor) {
        super(type);
        this.classname = classname;
        this.elementname = elementname;
        this.descriptor = descriptor;

        initConstant();
    }

    public LinkConstant(int type, int index1, int index2) {
        super(type);
        this.index1 = index1;
        this.index2 = index2;
    }

    private void initConstant() {
        if (type == CodeConstants.CONSTANT_Methodref ||
                type == CodeConstants.CONSTANT_InterfaceMethodref ||
                type == CodeConstants.CONSTANT_InvokeDynamic ||
                type == CodeConstants.CONSTANT_MethodHandle) {
            int parenth = descriptor.indexOf(')');
            if (descriptor.length() < 2 || parenth < 0 || descriptor.charAt(0) != '(') {
                throw new IllegalArgumentException("Invalid descriptor: " + descriptor);
            }
        }
    }

    @Override
    public void resolveConstant(ConstantPool pool) {
        if (type == CodeConstants.CONSTANT_NameAndType) {
            elementname = pool.getPrimitiveConstant(index1).getString();
            descriptor = pool.getPrimitiveConstant(index2).getString();
        } else if (type == CodeConstants.CONSTANT_MethodHandle) {
            LinkConstant ref_info = pool.getLinkConstant(index2);

            classname = ref_info.classname;
            elementname = ref_info.elementname;
            descriptor = ref_info.descriptor;
        } else {
            if (type != CodeConstants.CONSTANT_InvokeDynamic) {
                classname = pool.getPrimitiveConstant(index1).getString();
            }

            LinkConstant nametype = pool.getLinkConstant(index2);
            elementname = nametype.elementname;
            descriptor = nametype.descriptor;
        }

        initConstant();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof LinkConstant)) return false;

        LinkConstant cn = (LinkConstant) o;
        return this.type == cn.type &&
                this.elementname.equals(cn.elementname) &&
                this.descriptor.equals(cn.descriptor) &&
                (this.type != CodeConstants.CONSTANT_NameAndType || this.classname.equals(cn.classname));
    }
}