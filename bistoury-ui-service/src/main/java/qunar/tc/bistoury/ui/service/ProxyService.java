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

package qunar.tc.bistoury.ui.service;

import qunar.tc.bistoury.ui.util.ProxyInfo;

import java.util.List;
import java.util.Optional;

public interface ProxyService {

    List<String> getAllProxyUrls();

    List<String> getWebSocketUrl(final String agentIp);

    Optional<ProxyInfo> getNewProxyInfo(final String agentIp);
}
