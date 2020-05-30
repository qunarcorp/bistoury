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

package qunar.tc.bistoury.agent.common;

import java.util.Map;

/**
 * @author sen.chai
 * @date 15-6-15
 */
public interface ResponseHandler {

    boolean isWritable();

    boolean isActive();

    void handle(String line);

    void handle(int code, String line);

    void handle(int code, byte[] data);

    void handle(byte[] dataBytes);

    void handleError(int errorCode);

    void handleError(String error);

    void handleError(Throwable throwable);

    void handleEOF();

    void handleEOF(int exitCode);

    void handle(int code, byte[] data, Map<String, String> responseHeader);
}

    