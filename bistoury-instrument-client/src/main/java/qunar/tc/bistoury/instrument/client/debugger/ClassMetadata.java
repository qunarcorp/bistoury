/*
 * Copyright (C) 2019 Qunar, Inc.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package qunar.tc.bistoury.instrument.client.debugger;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.objectweb.asm.Opcodes;

import java.util.List;

/**
 * @author keli.wang
 */
final class ClassMetadata {
    private final List<ClassField> fields;
    private final List<ClassField> staticFields;
    private final Multimap<String, LocalVariable> variables;

    public ClassMetadata() {
        fields = Lists.newArrayList();
        staticFields = Lists.newArrayList();
        variables = ArrayListMultimap.create();
    }

    public void addField(final ClassField field) {
        if (isStaticField(field)) {
            staticFields.add(field);
        } else {
            fields.add(field);
        }
    }

    public void addVariable(final String methodId, final LocalVariable variable) {
        variables.put(methodId, variable);
    }

    public List<ClassField> getFields() {
        return fields;
    }

    public List<ClassField> getStaticFields() {
        return staticFields;
    }

    public Multimap<String, LocalVariable> getVariables() {
        return variables;
    }

    private boolean isStaticField(final ClassField field) {
        return (field.getAccess() & Opcodes.ACC_STATIC) != 0;
    }

    @Override
    public String toString() {
        return "ClassMetadata{" +
                "fields=" + fields +
                ", staticFields=" + staticFields +
                ", variables=" + variables +
                '}';
    }
}
