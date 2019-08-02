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

package qunar.tc.bistoury.commands.heapHisto;

import com.google.common.base.Strings;
import com.sun.tools.attach.VirtualMachine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.bistoury.common.FileUtil;
import sun.tools.attach.HotSpotVirtualMachine;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author: leix.xie
 * @date: 2019/4/1 10:20
 * @describe：
 */
public class HeapHistoBeanHandle {
    private static final Logger logger = LoggerFactory.getLogger(HeapHistoBeanHandle.class);
    private static final Pattern PATTERN = Pattern.compile("\\s+(\\d+):{1}\\s+(\\d+)\\s+(\\d+)\\s+(.+)");

    private static final int COUNT_INDEX = 2;
    private static final int BYTES_INDEX = 3;
    private static final int CLASSNAME_INDEX = 4;

    private String param;

    private int pid;

    public HeapHistoBeanHandle(final String param, final int pid) {
        this.param = param;
        this.pid = pid;
    }

    public List<HistogramBean> heapHisto() {
        VirtualMachine vm = null;
        try {
            vm = VirtualMachine.attach(String.valueOf(pid));
            return processHeapHisto(vm);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (vm != null) {
                try {
                    vm.detach();
                } catch (IOException e) {
                    logger.error("disconnect vm error ", e);
                }
            }
        }
    }

    private List<HistogramBean> processHeapHisto(VirtualMachine vm) throws Exception {
        if (isHotSpotVm()) {
            return this.getHistogramBeans(vm);
        } else {
            throw new RuntimeException("support HotSpot Virtual Machine only");
        }
    }

    private List<HistogramBean> getHistogramBeans(VirtualMachine vm) throws Exception {
        try (InputStream inputStream = ((HotSpotVirtualMachine) vm).heapHisto(this.param)) {
            final String heapHisto = FileUtil.read(inputStream);
            return parse(heapHisto);
        } catch (IOException e) {
            logger.info("get Heap Histo error", e);
            throw e;
        }
    }

    private List<HistogramBean> parse(final String histogram) {
        final Matcher matcher = PATTERN.matcher("");
        final String[] lines = histogram.split("\\n");
        final List<HistogramBean> entries = new ArrayList<>(lines.length);

        for (final String line : lines) {
            HistogramBean histogramBean = parseHistogramBean(matcher, line);
            if (histogramBean != null) {
                entries.add(histogramBean);
            }
        }

        return entries;
    }

    private HistogramBean parseHistogramBean(Matcher matcher, String line) {
        matcher.reset(line);
        if (matcher.matches()) {
            return new HistogramBean(matcher.group(COUNT_INDEX), matcher.group(BYTES_INDEX), matcher.group(CLASSNAME_INDEX));
        }
        return null;
    }

    private boolean isHotSpotVm() {
        String vmName = System.getProperty("java.vm.name");
        return !Strings.isNullOrEmpty(vmName) && vmName.toLowerCase().contains("hotspot");
    }
}
