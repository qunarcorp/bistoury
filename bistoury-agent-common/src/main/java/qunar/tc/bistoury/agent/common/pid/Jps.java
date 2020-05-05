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

package qunar.tc.bistoury.agent.common.pid;
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import qunar.tc.bistoury.agent.common.pid.bean.Arguments;
import qunar.tc.bistoury.agent.common.pid.bean.Res;
import sun.jvmstat.monitor.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Jps {
    private static Arguments arguments;

    public static void main(String[] var0) {
        Res<List<String>> res = new Res<>();
        executeJps(new String[]{"-l"}, res);
        if (res.getCode() == 0) {
            System.out.println(res.getData());
        } else {
            System.out.println(res.getMessage());
        }
    }

    public static void executeJps(String[] param, Res<List<String>> res) {
        List<String> result = new ArrayList<>();
        res.setData(result);
        res.setCode(0);
        try {
            arguments = new Arguments(param);
        } catch (IllegalArgumentException var20) {
            res.setCode(-1);
            res.setMessage(var20.getMessage());
            return;
        }

        if (arguments.isHelp()) {
            res.setCode(-1);
            res.setMessage("命令格式错误");
            return;
        }

        try {
            HostIdentifier var1 = arguments.hostId();
            MonitoredHost var25 = MonitoredHost.getMonitoredHost(var1);
            Set var3 = var25.activeVms();
            Iterator var4 = var3.iterator();

            while (true) {
                while (var4.hasNext()) {
                    Integer var5 = (Integer) var4.next();
                    StringBuilder var6 = new StringBuilder();
                    int var8 = var5;
                    var6.append(var8);
                    if (arguments.isQuiet()) {
                        System.out.println(var6);
                    } else {
                        MonitoredVm var9;
                        String var10 = "//" + var8 + "?mode=r";

                        try {
                            VmIdentifier var12 = new VmIdentifier(var10);
                            var9 = var25.getMonitoredVm(var12, 0);
                            var6.append(" " + MonitoredVmUtil.mainClass(var9, arguments.showLongPaths()));
                            String var13;
                            if (arguments.showMainArgs()) {
                                var13 = MonitoredVmUtil.mainArgs(var9);
                                if (var13 != null && var13.length() > 0) {
                                    var6.append(" " + var13);
                                }
                            }

                            if (arguments.showVmArgs()) {
                                var13 = MonitoredVmUtil.jvmArgs(var9);
                                if (var13 != null && var13.length() > 0) {
                                    var6.append(" " + var13);
                                }
                            }

                            if (arguments.showVmFlags()) {
                                var13 = MonitoredVmUtil.jvmFlags(var9);
                                if (var13 != null && var13.length() > 0) {
                                    var6.append(" " + var13);
                                }
                            }

                            var25.detach(var9);
                            result.add(var6.toString());
                        } catch (Exception var22) {
                            //ignore
                        }
                    }
                }
                return;
            }
        } catch (MonitorException var24) {
            res.setCode(-1);
            if (var24.getMessage() != null) {
                res.setMessage(var24.getMessage());
            } else {
                Throwable var2 = var24.getCause();
                if (var2 != null && var2.getMessage() != null) {
                    res.setMessage(var2.getMessage());
                } else {
                    res.setMessage(var24.getStackTrace().toString());
                }
            }
        }
    }
}
