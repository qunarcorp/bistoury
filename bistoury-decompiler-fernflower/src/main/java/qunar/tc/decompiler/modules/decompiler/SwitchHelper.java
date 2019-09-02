// Copyright 2000-2017 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package qunar.tc.decompiler.modules.decompiler;

import qunar.tc.decompiler.code.CodeConstants;
import qunar.tc.decompiler.main.ClassesProcessor;
import qunar.tc.decompiler.main.DecompilerContext;
import qunar.tc.decompiler.main.extern.IFernflowerLogger;
import qunar.tc.decompiler.main.rels.MethodWrapper;
import qunar.tc.decompiler.modules.decompiler.exps.*;
import qunar.tc.decompiler.modules.decompiler.sforms.DirectGraph;
import qunar.tc.decompiler.modules.decompiler.stats.SwitchStatement;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SwitchHelper {
    public static void simplify(SwitchStatement switchStatement) {
        SwitchExprent switchExprent = (SwitchExprent) switchStatement.getHeadexprent();
        Exprent value = switchExprent.getValue();
        if (isEnumArray(value)) {
            List<List<Exprent>> caseValues = switchStatement.getCaseValues();
            final Map<Exprent, Exprent> mapping = new HashMap<>(caseValues.size());
            ArrayExprent array = (ArrayExprent) value;
            final FieldExprent arrayField = (FieldExprent) array.getArray();
            ClassesProcessor.ClassNode classNode =
                    DecompilerContext.getClassProcessor().getMapRootClasses().get(arrayField.getClassname());
            if (classNode != null) {
                MethodWrapper wrapper = classNode.getWrapper().getMethodWrapper(CodeConstants.CLINIT_NAME, "()V");
                if (wrapper != null && wrapper.root != null) {
                    wrapper.getOrBuildGraph().iterateExprents(new DirectGraph.ExprentIterator() {
                        @Override
                        public int processExprent(Exprent exprent) {
                            if (exprent instanceof AssignmentExprent) {
                                AssignmentExprent assignment = (AssignmentExprent) exprent;
                                Exprent left = assignment.getLeft();
                                if (left.type == Exprent.EXPRENT_ARRAY && ((ArrayExprent) left).getArray().equals(arrayField)) {
                                    mapping.put(assignment.getRight(), ((InvocationExprent) ((ArrayExprent) left).getIndex()).getInstance());
                                }
                            }
                            return 0;
                        }
                    });
                }
            }

            List<List<Exprent>> realCaseValues = new ArrayList<>(caseValues.size());
            for (List<Exprent> caseValue : caseValues) {
                List<Exprent> values = new ArrayList<>(caseValue.size());
                realCaseValues.add(values);
                for (Exprent exprent : caseValue) {
                    if (exprent == null) {
                        values.add(null);
                    } else {
                        Exprent realConst = mapping.get(exprent);
                        if (realConst == null) {
                            DecompilerContext.getLogger()
                                    .writeMessage("Unable to simplify switch on enum: " + exprent + " not found, available: " + mapping,
                                            IFernflowerLogger.Severity.ERROR);
                            return;
                        }
                        values.add(realConst.copy());
                    }
                }
            }
            caseValues.clear();
            caseValues.addAll(realCaseValues);
            switchExprent.replaceExprent(value, ((InvocationExprent) array.getIndex()).getInstance().copy());
        }
    }

    private static boolean isEnumArray(Exprent exprent) {
        if (exprent instanceof ArrayExprent) {
            Exprent field = ((ArrayExprent) exprent).getArray();
            Exprent index = ((ArrayExprent) exprent).getIndex();
            return field instanceof FieldExprent &&
                    (((FieldExprent) field).getName().startsWith("$SwitchMap") ||
                            (index instanceof InvocationExprent && ((InvocationExprent) index).getName().equals("ordinal")));
        }
        return false;
    }
}
