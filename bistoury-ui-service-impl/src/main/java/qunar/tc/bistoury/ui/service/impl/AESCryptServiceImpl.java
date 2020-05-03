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

package qunar.tc.bistoury.ui.service.impl;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import qunar.tc.bistoury.common.AESCryptUtils;
import qunar.tc.bistoury.ui.service.AESCryptService;

import java.util.Base64;

/**
 * @author keli.wang
 */
@Service
public class AESCryptServiceImpl implements AESCryptService {
    private static final Logger LOG = LoggerFactory.getLogger(AESCryptServiceImpl.class);

    @Override
    public String encrypt(String data) {
        try {
            if (Strings.isNullOrEmpty(data)) {
                return Strings.nullToEmpty(data);
            }
            return new String(Base64.getEncoder().encode(AESCryptUtils.encrypt(data.getBytes(Charsets.UTF_8))), Charsets.UTF_8);
        } catch (Exception e) {
            LOG.error("无法加密字符串。data={}", data, e);
            throw new RuntimeException("加密字符串失败", e);
        }
    }

    @Override
    public String decrypt(String data) {
        try {
            return new String(AESCryptUtils.decrypt(Base64.getDecoder().decode(data.getBytes(Charsets.UTF_8))), Charsets.UTF_8);
        } catch (Exception e) {
            LOG.error("解密字符串失败。data={}", data, e);
            throw new RuntimeException("解密字符串失败", e);
        }
    }
}
