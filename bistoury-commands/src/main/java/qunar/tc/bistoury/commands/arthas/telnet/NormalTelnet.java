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

package qunar.tc.bistoury.commands.arthas.telnet;

import org.apache.commons.net.telnet.TelnetClient;

import java.io.IOException;

/**
 * @author zhenyu.nie created on 2018 2018/11/28 19:02
 */
public class NormalTelnet extends AbstractTelnet {

    public NormalTelnet(TelnetClient client) throws IOException {
        super(client);
    }

    @Override
    protected ResultProcessor getProcessor(Writer writer) {
        return new SkipFirstLineProcessor(new PromptProcessor(writer));
    }
}
