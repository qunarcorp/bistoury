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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.commands.arthas.ArthasEntity;
import qunar.tc.bistoury.commands.arthas.TelnetConstants;
import qunar.tc.bistoury.common.BistouryConstants;

import java.io.IOException;

/**
 * @author zhenyu.nie created on 2018 2018/10/15 19:07
 */
public abstract class AbstractTelnetStore implements TelnetStore {

    private static final Logger logger = LoggerFactory.getLogger(AbstractTelnetStore.class);

    private static final int MAX_ILLEGAL_VERSION_COUNT = 2;

    private enum CheckVersion {
        check, notCheck
    }

    private ArthasEntity arthasEntity;

    protected AbstractTelnetStore() {

    }

    @Override
    public Telnet getTelnet(int pid) throws Exception {
        int illegalVersionCount = 0;
        while (illegalVersionCount < MAX_ILLEGAL_VERSION_COUNT) {
            try {
                TelnetClient client = doGetTelnet(pid);
                return createTelnet(client, CheckVersion.check);
            } catch (IllegalVersionException e) {
                sleepSec(3);
                illegalVersionCount++;
            }
        }
        logger.error("illegal version can not resolved");
        throw new IllegalVersionException();
    }

    private void sleepSec(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Telnet createTelnet(TelnetClient client, CheckVersion checkVersion) throws IOException {
        Telnet telnet = doCreateTelnet(client);
        String version = telnet.getVersion();
        if (checkVersion == CheckVersion.check && versionIllegal(version)) {
            return doWithIllegalVersion(telnet, version);
        } else {
            return telnet;
        }
    }

    private Telnet doWithIllegalVersion(Telnet telnet, String version) {
        logger.warn("bistoury version illegal, current [{}], get [{}]", BistouryConstants.CURRENT_VERSION, version);
        try {
            telnet.write(BistouryConstants.SHUTDOWN_COMMAND);
        } catch (Exception e) {
            // ignore
        } finally {
            telnet.close();
        }
        throw new IllegalVersionException();
    }

    private boolean versionIllegal(String version) {
        return !BistouryConstants.CURRENT_VERSION.equals(version);
    }

    protected abstract Telnet doCreateTelnet(TelnetClient client) throws IOException;

    private synchronized TelnetClient doGetTelnet(int pid) {
        TelnetClient client = tryGetClient();
        if (client != null) {
            return client;
        }

        try {
            try {
                return createClient(pid);
            } catch (Exception e) {
                return forceCreateClient(pid);
            }
        } catch (Exception e) {
            resetClient();
            throw new IllegalStateException("can not init bistoury, " + e.getMessage(), e);
        }
    }

    private TelnetClient tryGetClient() {
        try {
            return createClient();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Telnet tryGetTelnet() throws Exception {
        TelnetClient client = tryGetClient();
        if (client != null) {
            return createTelnet(client, CheckVersion.notCheck);
        }
        return null;
    }

    private void resetClient() {
        this.arthasEntity = null;
    }

    private TelnetClient createClient(int pid) throws IOException {
        if (arthasEntity == null || arthasEntity.getPid() != pid) {
            return forceCreateClient(pid);
        } else {
            return createClient();
        }
    }

    private TelnetClient forceCreateClient(int pid) throws IOException {
        ArthasEntity arthasEntity = new ArthasEntity(pid);
        arthasEntity.start();
        TelnetClient client = createClient();
        this.arthasEntity = arthasEntity;
        return client;
    }

    private TelnetClient createClient() throws IOException {
        TelnetClient client = new TelnetClient();
        client.setConnectTimeout(TelnetConstants.TELNET_CONNECT_TIMEOUT);
        client.connect(TelnetConstants.TELNET_CONNECTION_IP, TelnetConstants.TELNET_CONNECTION_PORT);
        return client;
    }
}
