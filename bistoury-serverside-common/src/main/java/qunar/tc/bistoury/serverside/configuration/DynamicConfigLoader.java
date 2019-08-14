
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

package qunar.tc.bistoury.serverside.configuration;

import java.util.ServiceLoader;

/**
 * @author keli.wang
 * @since 2018-11-23
 */
public final class DynamicConfigLoader {
    // TODO(keli.wang): can we set this using config?
    private static final DynamicConfigFactory FACTORY;

    static {
        ServiceLoader<DynamicConfigFactory> factories = ServiceLoader.load(DynamicConfigFactory.class);
        DynamicConfigFactory instance = null;
        for (DynamicConfigFactory factory : factories) {
            instance = factory;
            break;
        }

        FACTORY = instance;
    }

    private DynamicConfigLoader() {
    }

    public static <T> DynamicConfig<T> load(final String name) {
        return load(name, true);
    }

    @SuppressWarnings("unchecked")
    public static <T> DynamicConfig<T> load(final String name, final boolean failOnNotExist) {
        return FACTORY.create(name, failOnNotExist);
    }
}
