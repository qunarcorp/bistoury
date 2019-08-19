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

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import org.apache.commons.net.telnet.TelnetClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.agent.common.AgentConstants;
import qunar.tc.bistoury.agent.common.util.AgentUtils;
import qunar.tc.bistoury.agent.common.util.NetWorkUtils;
import qunar.tc.bistoury.clientside.common.meta.MetaStore;
import qunar.tc.bistoury.clientside.common.meta.MetaStores;
import qunar.tc.bistoury.commands.arthas.ArthasEntity;
import qunar.tc.bistoury.commands.arthas.ArthasTelnetPortHelper;
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
    public Telnet getTelnet(String nullableAppCode, int pid) throws Exception {
        int illegalVersionCount = 0;
        while (illegalVersionCount < MAX_ILLEGAL_VERSION_COUNT) {
            try {
                TelnetClient client = doGetTelnet(nullableAppCode, pid);
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

    private synchronized TelnetClient doGetTelnet(String nullableAppCode, int pid) {
        TelnetClient client = tryGetClient(nullableAppCode);
        if (client != null) {
            return client;
        }

        try {
            try {
                return createClient(nullableAppCode, pid);
            } catch (Exception e) {
                return forceCreateClient(nullableAppCode, pid);
            }
        } catch (Exception e) {
            resetClient();
            throw new IllegalStateException("can not init bistoury, " + e.getMessage(), e);
        }
    }

    private TelnetClient tryGetClient(String nullableAppCode) {
        try {
            return createClient(nullableAppCode);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Telnet tryGetTelnet(String nullableAppCode) throws Exception {
        TelnetClient client = tryGetClient(nullableAppCode);
        if (client != null) {
            return createTelnet(client, CheckVersion.notCheck);
        }
        return null;
    }

    private void resetClient() {
        this.arthasEntity = null;
    }

    private TelnetClient createClient(String nullableAppCode, int pid) throws IOException {
        if (arthasEntity == null || arthasEntity.getPid() != pid) {
            return forceCreateClient(nullableAppCode, pid);
        } else {
            return createClient(nullableAppCode);
        }
    }

    private TelnetClient forceCreateClient(String nullableAppCode, int pid) throws IOException {
        ArthasEntity arthasEntity = new ArthasEntity(nullableAppCode, pid);
        arthasEntity.start();
        TelnetClient client = createClient(nullableAppCode);
        this.arthasEntity = arthasEntity;
        return client;
    }

    private TelnetClient createClient(String nullableAppCode) throws IOException {
        TelnetClient client = new TelnetClient();
        client.setConnectTimeout(TelnetConstants.TELNET_CONNECT_TIMEOUT);
        client.connect(TelnetConstants.TELNET_CONNECTION_IP, ArthasTelnetPortHelper.getTelnetPort(nullableAppCode));
        return client;
    }
}
