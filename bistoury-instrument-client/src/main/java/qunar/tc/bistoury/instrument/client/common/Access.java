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

package qunar.tc.bistoury.instrument.client.common;

/**
 * 修饰符计算，比如去除PUBLIC；添加PRIVATE；判断是否是ABSTRACT等等。
 *
 * @author Daniel Li
 * @since 30 March 2015
 */
public class Access {

    private int access;

    public static Access of(int access) {
        return new Access(access);
    }

    private Access(int access) {
        this.access = access;
    }

    public Access remove(int remove) {
        access &= ~remove;
        return this;
    }

    public Access add(int add) {
        access |= add;
        return this;
    }

    public boolean contain(int partAccess) {
        return (access & partAccess) != 0;
    }

    public int get() {
        return access;
    }
}
