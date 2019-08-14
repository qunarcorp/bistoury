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

package qunar.tc.bistoury.application.api.pojo;

/**
 * @author leix.xie
 * @date 2019/7/4 15:40
 * @describe
 */
public class PermissionDenyException extends RuntimeException {
    public PermissionDenyException() {
        super();
    }

    public PermissionDenyException(String message) {
        super(message);
    }

    public PermissionDenyException(String message, Throwable cause) {
        super(message, cause);
    }

    public PermissionDenyException(Throwable cause) {
        super(cause);
    }

    protected PermissionDenyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
