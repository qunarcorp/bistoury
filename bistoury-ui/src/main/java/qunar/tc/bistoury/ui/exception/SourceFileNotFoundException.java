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

package qunar.tc.bistoury.ui.exception;

/**
 * @author leix.xie
 * @date 2019/4/26 11:47
 * @describe
 */
public class SourceFileNotFoundException extends RuntimeException {

    public SourceFileNotFoundException() {
    }

    public SourceFileNotFoundException(String message) {
        super(message);
    }

    public SourceFileNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public SourceFileNotFoundException(Throwable cause) {
        super(cause);
    }

    public SourceFileNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
