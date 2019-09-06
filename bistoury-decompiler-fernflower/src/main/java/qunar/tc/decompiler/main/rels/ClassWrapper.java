// Copyright 2000-2018 JetBrains s.r.o. Use of this source code is governed by the Apache 2.0 license that can be found in the LICENSE file.
package qunar.tc.decompiler.main.rels;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import qunar.tc.decompiler.code.CodeConstants;
import qunar.tc.decompiler.jdk827.List827;
import qunar.tc.decompiler.main.DecompilerContext;
import qunar.tc.decompiler.main.collectors.CounterContainer;
import qunar.tc.decompiler.main.collectors.VarNamesCollector;
import qunar.tc.decompiler.main.extern.IFernflowerLogger;
import qunar.tc.decompiler.main.extern.IFernflowerPreferences;
import qunar.tc.decompiler.modules.decompiler.exps.Exprent;
import qunar.tc.decompiler.modules.decompiler.exps.VarExprent;
import qunar.tc.decompiler.modules.decompiler.sforms.DirectGraph;
import qunar.tc.decompiler.modules.decompiler.stats.RootStatement;
import qunar.tc.decompiler.modules.decompiler.vars.VarProcessor;
import qunar.tc.decompiler.modules.decompiler.vars.VarVersionPair;
import qunar.tc.decompiler.struct.StructClass;
import qunar.tc.decompiler.struct.StructField;
import qunar.tc.decompiler.struct.StructMethod;
import qunar.tc.decompiler.struct.attr.StructLocalVariableTableAttribute;
import qunar.tc.decompiler.struct.gen.MethodDescriptor;
import qunar.tc.decompiler.util.InterpreterUtil;
import qunar.tc.decompiler.util.VBStyleCollection;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ClassWrapper {
    private final StructClass classStruct;
    private final Set<String> hiddenMembers = new HashSet<>();
    private final VBStyleCollection<Exprent, String> staticFieldInitializers = new VBStyleCollection<>();
    private final VBStyleCollection<Exprent, String> dynamicFieldInitializers = new VBStyleCollection<>();
    private final VBStyleCollection<MethodWrapper, String> methods = new VBStyleCollection<>();

    public ClassWrapper(StructClass classStruct) {
        this.classStruct = classStruct;
    }

    public void init() {
        DecompilerContext.setProperty(DecompilerContext.CURRENT_CLASS, classStruct);
        DecompilerContext.setProperty(DecompilerContext.CURRENT_CLASS_WRAPPER, this);
        DecompilerContext.getLogger().startClass(classStruct.qualifiedName);

        int maxSec = Integer.parseInt(DecompilerContext.getProperty(IFernflowerPreferences.MAX_PROCESSING_METHOD).toString());
        boolean testMode = DecompilerContext.getOption(IFernflowerPreferences.UNIT_TEST_MODE);

        for (final StructMethod mt : classStruct.getMethods()) {
            DecompilerContext.getLogger().startMethod(mt.getName() + " " + mt.getDescriptor());

            MethodDescriptor md = MethodDescriptor.parseDescriptor(mt.getDescriptor());
            final VarProcessor varProc = new VarProcessor(mt, md);
            DecompilerContext.startMethod(varProc);

            VarNamesCollector vc = varProc.getVarNamesCollector();
            CounterContainer counter = DecompilerContext.getCounterContainer();

            RootStatement root = null;

            boolean isError = false;

            try {
                if (mt.containsCode()) {
                    if (maxSec == 0 || testMode) {
                        root = MethodProcessorRunnable.codeToJava(mt, md, varProc);
                    } else {
                        MethodProcessorRunnable mtProc = new MethodProcessorRunnable(mt, md, varProc, DecompilerContext.getCurrentContext());

                        Thread mtThread = new Thread(mtProc, "Java decompiler");
                        long stopAt = System.currentTimeMillis() + maxSec * 1000L;

                        mtThread.start();

                        while (!mtProc.isFinished()) {
                            try {
                                synchronized (mtProc.lock) {
                                    mtProc.lock.wait(200);
                                }
                            } catch (InterruptedException e) {
                                killThread(mtThread);
                                throw e;
                            }

                            if (System.currentTimeMillis() >= stopAt) {
                                String message = "Processing time limit exceeded for method " + mt.getName() + ", execution interrupted.";
                                DecompilerContext.getLogger().writeMessage(message, IFernflowerLogger.Severity.ERROR);
                                killThread(mtThread);
                                isError = true;
                                break;
                            }
                        }

                        if (!isError) {
                            root = mtProc.getResult();
                        }
                    }
                } else {
                    boolean thisVar = !mt.hasModifier(CodeConstants.ACC_STATIC);

                    int paramCount = 0;
                    if (thisVar) {
                        varProc.getThisVars().put(new VarVersionPair(0, 0), classStruct.qualifiedName);
                        paramCount = 1;
                    }
                    paramCount += md.params.length;

                    int varIndex = 0;
                    for (int i = 0; i < paramCount; i++) {
                        varProc.setVarName(new VarVersionPair(varIndex, 0), vc.getFreeName(varIndex));

                        if (thisVar) {
                            if (i == 0) {
                                varIndex++;
                            } else {
                                varIndex += md.params[i - 1].stackSize;
                            }
                        } else {
                            varIndex += md.params[i].stackSize;
                        }
                    }
                }
            } catch (Throwable t) {
                String message = "Method " + mt.getName() + " " + mt.getDescriptor() + " couldn't be decompiled.";
                DecompilerContext.getLogger().writeMessage(message, IFernflowerLogger.Severity.WARN, t);
                isError = true;
            }

            MethodWrapper methodWrapper = new MethodWrapper(root, varProc, mt, counter);
            methodWrapper.decompiledWithErrors = isError;

            methods.addWithKey(methodWrapper, InterpreterUtil.makeUniqueKey(mt.getName(), mt.getDescriptor()));

            if (!isError) {
                // rename vars so that no one has the same name as a field
                final VarNamesCollector namesCollector = new VarNamesCollector();
                //classStruct.getFields().forEach(f -> namesCollector.addName(f.getName()));
                List827.forEach(classStruct.getFields(), new Function<StructField, Void>() {
                    @Override
                    public Void apply(StructField input) {
                        namesCollector.addName(input.getName());
                        return null;
                    }
                });

                varProc.refreshVarNames(namesCollector);

                // if debug information present and should be used
                if (DecompilerContext.getOption(IFernflowerPreferences.USE_DEBUG_VAR_NAMES)) {
                    StructLocalVariableTableAttribute attr = mt.getLocalVariableAttr();
                    if (attr != null) {
                        // only param names here
                        varProc.setDebugVarNames(attr.getMapParamNames());

                        // the rest is here
                        methodWrapper.getOrBuildGraph().iterateExprents(new DirectGraph.ExprentIterator() {
                            @Override
                            public int processExprent(Exprent exprent) {
                                List<Exprent> lst = exprent.getAllExprents(true);
                                lst.add(exprent);
                                /*lst.stream()
                                        .filter(e -> e.type == Exprent.EXPRENT_VAR)
                                        .forEach(e -> {
                                            VarExprent varExprent = (VarExprent) e;
                                            String name = varExprent.getDebugName(mt);
                                            if (name != null) {
                                                varProc.setVarName(varExprent.getVarVersionPair(), name);
                                            }
                                        });*/
                                ArrayList<Exprent> list = Lists.newArrayList(lst);
                                List827.filter(list, new Predicate<Exprent>() {
                                    @Override
                                    public boolean apply(Exprent e) {
                                        return e.type == Exprent.EXPRENT_VAR;
                                    }
                                });
                                List827.forEach(list, new Function<Exprent, Void>() {
                                    @Override
                                    public Void apply(Exprent e) {
                                        VarExprent varExprent = (VarExprent) e;
                                        String name = varExprent.getDebugName(mt);
                                        if (name != null) {
                                            varProc.setVarName(varExprent.getVarVersionPair(), name);
                                        }
                                        return null;
                                    }
                                });
                                return 0;
                            }
                        });
                    }
                }
            }

            DecompilerContext.getLogger().endMethod();
        }

        DecompilerContext.getLogger().endClass();
    }

    @SuppressWarnings("deprecation")
    private static void killThread(Thread thread) {
        thread.stop();
    }

    public MethodWrapper getMethodWrapper(String name, String descriptor) {
        return methods.getWithKey(InterpreterUtil.makeUniqueKey(name, descriptor));
    }

    public StructClass getClassStruct() {
        return classStruct;
    }

    public VBStyleCollection<MethodWrapper, String> getMethods() {
        return methods;
    }

    public Set<String> getHiddenMembers() {
        return hiddenMembers;
    }

    public VBStyleCollection<Exprent, String> getStaticFieldInitializers() {
        return staticFieldInitializers;
    }

    public VBStyleCollection<Exprent, String> getDynamicFieldInitializers() {
        return dynamicFieldInitializers;
    }

    @Override
    public String toString() {
        return classStruct.qualifiedName;
    }
}