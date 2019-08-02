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

package qunar.tc.bistoury.proxy.util;

import com.google.common.util.concurrent.FutureCallback;

/**
 * @author zhenyu.nie created on 2019 2019/5/15 13:53
 */
public interface FutureSuccessCallBack<V> extends FutureCallback<V> {

    @Override
    default void onFailure(Throwable t) {
        // do nothing
    }
}
