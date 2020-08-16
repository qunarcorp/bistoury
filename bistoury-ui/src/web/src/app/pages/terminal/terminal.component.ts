import {Component, ElementRef, HostListener, Input, OnInit} from '@angular/core';
import {Terminal} from 'xterm';
import {FitAddon} from 'xterm-addon-fit';
import 'xterm/css/xterm.css';
import {ApplicationServer} from '../model/applicationServer';
import {WebSocketService} from '../../services/webSocketService';
import {ApplicationService} from '../../services/applicationService';
import {Response} from '../model/response';
import {NzMessageService} from 'ng-zorro-antd';
import {RemoteCommand} from '../model/remoteCommand';
import {CookieService} from 'ngx-cookie-service';
import * as JsEncryptModule from 'jsencrypt';
import CryptoJS from 'crypto-js';

@Component({
    selector: 'app-terminal',
    templateUrl: './terminal.component.html',
    styleUrls: ['./terminal.component.css']
})
export class TerminalComponent implements OnInit {
    @Input() appCode: string;

    @Input()
    servers: Array<ApplicationServer> = [];


    @Input()
    set _selectServers(servers: Array<ApplicationServer>) {
        this.selectServers = servers;
        try {
            this.prompt();
        } catch (e) {
            //ignore
        }
    }

    @HostListener('window:onresize', ['$event'])
    onResize(event) {
        console.log(event);
        this.fitAddon.fit();
    }

    commandLine: string = '';
    command: string = '';
    selectServers: Array<ApplicationServer> = [];
    element: ElementRef;
    term: Terminal;
    fitAddon: FitAddon = new FitAddon();
    chineseReg = new RegExp('^[\u4E00-\u9FFF]+$');

    errorMapping = ApplicationService.errorMapping;

    wsCache: Map<string, WebSocket> = new Map<string, WebSocket>();
    wsOpen = new Set<string>();

    isTab = false;
    tabHostLength = this.servers.length;
    allContent = {};
    tabContent: string = '';
    lastHost: string = '';
    lastOutput = '';
    receiving = false;
    isCanceled = false;

    historyCommands = [];
    historyIndex = 0;

    ignoreInputCodes = new Set([3, 9, 127]);

    waitCommand = new Set([
        'ls',
        'jvm',
        'thread',
        'sysprop',
        'sysenv',
        'dump',
        'classloader',
        'options',
        'logger',
        'getstatic',
        'vmoption',
        'mbean',
        'ognl',
        'jad',
        'sc',
        'sm'
    ]);

    linuxCommandType = new Set([1, 4, 6]);
    qToolsCommandSet = new Set(['qjmap', 'qjtop', 'qjmxcli', 'qjdump']);
    arthasCommands = new Set(['dashboard',
        'thread',
        'jvm',
        'sysprop',
        'getstatic',
        'sc',
        'sm',
        'dump',
        'jad',
        'classloader',
        'redefine',
        'monitor',
        'watch',
        'trace',
        'stack',
        'tt',
        'options',
        'reset',
        'shutdown',
        'history',
        'sysenv',
        'ognl',
        'mc',
        'mbean',
        'heapdump',
        'vmoption',
        'logger',
        'stop',
        'profilerstart',
        'profilerstop',
        'profilersearch']);

    constructor(element: ElementRef,
                private webSocketService: WebSocketService,
                private applicationService: ApplicationService,
                private message: NzMessageService,
                private cookies: CookieService) {
        this.element = element;
    }


    ngOnInit(): void {
        const rows = Number(((document.documentElement.clientHeight - 175) / 17).toFixed(0));
        this.term = new Terminal({
            rendererType: 'canvas', /* 渲染类型 'dom' | 'canvas'*/
            rows: rows - 1, // 行数
            cols: 150,
            convertEol: true, // 启用时，光标将设置为下一行的开头
            scrollback: 2000, // 终端中的回滚量
            disableStdin: false, // 是否应禁用输入。
            cursorStyle: 'block', // 光标样式
            cursorBlink: true, // 光标闪烁
            theme: {
                foreground: '#cccddd', // 字体
                background: '#0a2933', // 背景色
                cursor: 'help', // 设置光标
                black: '\\033[30m',
                red: '\\033[31m',
                green: '\\033[32m',
                yellow: '\\033[33m',
                blue: '\\033[34m',
                magenta: '\\033[35m',
                cyan: '\\033[36m',
                white: '\\033[37m',
                brightBlack: '\\033[1;30m',
                brightRed: '\\033[1;31m',
                brightGreen: '\\033[1;32m',
                brightYellow: '\\033[1;33m',
                brightBlue: '\\033[1;34m',
                brightMagenta: '\\033[1;35m',
                brightCyan: '\\033[1;36m',
                brightWhite: '\\033[1;37m'

            }
        });
        this.element.nativeElement.width = this.element.nativeElement.clientWidth
        this.term.open(this.element.nativeElement);
        this.term.loadAddon(this.fitAddon);
        this.fitAddon.fit();
        this.term.focus();
        this.write('欢迎使用Bistoury');
        this.prompt();
        this.term.onData(data => {
            if (this.ignoreInputCodes.has(data.charCodeAt(0))) {
                //ignore
            } else if (data.charCodeAt(0) === 13) {
                this.executeCommand();
            } else {
                this.write(data);
                this.commandLine += data;
            }

        });
        this.term.attachCustomKeyEventHandler(e => {
            if (e.type != 'keydown') {
                return;
            }
            console.log(e);
            if ((e.ctrlKey || e.metaKey) && e.keyCode === 67) {
                this.cancelCommand();
            } else if (e.ctrlKey && e.keyCode === 76) {
                this.clear();
            } else if (e.keyCode === 9) {
                this.tabCommand();
            } else if (e.keyCode === 38) {
                this.preHistoryCommand();
                return false;
            } else if (e.keyCode === 40) {
                this.nextHistoryCommand();
                return false;
            } else if (e.keyCode === 8) {
                // Do not delete the prompt
                if (this.commandLine.length > 0) {
                    var lastIndex = this.commandLine.length - 1;
                    if (this.chineseReg.test(this.commandLine.charAt(lastIndex))) {
                        this.write('\b \b\b \b');
                    } else {
                        this.write('\b \b');
                    }
                    this.commandLine = this.commandLine.substr(0, lastIndex);
                }
            }
            return true;
        });
    }

    executeCommand() {
        this.parseCommand();
        if (this.qToolsCommandSet.has(this.command)) {
            this.send(6, this.commandLine);
        } else if (this.arthasCommands.has(this.command)) {
            this.send(7, this.commandLine);
        } else if (this.command === 'jstack' || this.command === 'jstat') {
            this.send(4, this.commandLine);
        } else {
            this.send(1, this.commandLine);
        }
    }

    tabRecover(multi: boolean) {
        const lastIndex = this.commandLine.lastIndexOf(' ');
        const lastStr = this.commandLine.substring(lastIndex + 1);
        const command = this.commandLine.substring(0, lastIndex + 1);
        const lines = this.tabContent.match(/[^\n]+/g);
        const match = this.matchItem(lines, lastStr);
        if (!multi || this.tabHostLength <= 0) {
            this.prompt();
            this.commandLine = command + match;
            this.write(this.commandLine)
        }
    }

    tabCommand() {
        this.isTab = true;
        this.send(3, this.commandLine);
    }

    cancelCommand() {
        if (this.receiving) {
            this.commandLine = '';
            this.send(2, '');
        } else {
            this.prompt();
        }
        this.isCanceled = true;
    }

    send(type: number, input: string) {
        if (type != 2 && input.length == 0) {
            this.prompt();
            return;
        }
        if (input.indexOf('｜') >= 0) {
            this.writeln('\\033[31m[WARING]:\\033[0m Chinese character \[｜\] in command');
        }
        if (this.servers == null || this.servers.length == 0) {
            this.writeln('The application center didn\'t find the machine belonging to the application');
            this.prompt();
            return;
        }
        if (this.selectServers != null && this.selectServers.length > 1) {
            this.tabHostLength = this.selectServers.length;
            this.selectServers.forEach((server: ApplicationServer) => {
                this.sendWs(type, input, server, true);
            })
        } else if (this.selectServers == null || this.selectServers.length == 0) {
            this.tabHostLength = this.servers.length;
            this.servers.forEach((server: ApplicationServer) => {
                this.sendWs(type, input, server, true);
            })
        } else {
            this.tabHostLength = 1;
            this.sendWs(type, input, this.selectServers[0], false);
        }
    }

    sendWs(type: number, input: string, server: ApplicationServer, multi: boolean) {
        const promise = this.getWs(type, input, server, multi);
        this.wsOpen.add(server.ip);
        promise.then((ws) => {
            this.newLine();
            this.allContent[server.ip] = '';
            const data = this.encryptData(JSON.stringify(this.buildCommand(type, input, server)));
            this.pushHistoryCommand();
            ws.send(JSON.stringify(data));
        }).catch(reason => {
            console.log('ws 连接失败' + reason);
            this.wsOpen.delete(server.ip);
            if (this.wsOpen.size == 0) {
                this.prompt();
            }
        });
    }

    public getWs(type: number, input: string, server: ApplicationServer, multi: boolean) {
        return new Promise<WebSocket>(((resolve, reject) => {
            if (this.wsCache.has(server.ip)) {
                resolve(this.wsCache.get(server.ip));
            } else {
                this.applicationService.getWebSocketUrl(server.ip).then((res: Response<string>) => {
                    if (res.status == 0 || res.status == 100) {
                        try {
                            const ws = this.webSocketService.getWS(res.data);
                            ws.binaryType = 'arraybuffer';
                            ws.onopen = () => {
                                this.wsCache.set(server.ip, ws);
                                resolve(ws);
                            };
                            ws.onmessage = (e) => {
                                if (typeof (e.data) == 'string') {
                                    this.recv(JSON.parse(e.data), server, multi);
                                } else {
                                    this.recv(e.data, server, multi);
                                }
                            };
                            ws.onerror = (e) => {
                                console.log('ws error');
                                console.log(e);
                                ws.close();
                            };
                            ws.onclose = (e) => {
                                console.log(e);
                                this.warn(server.host + '> 已与proxy断开连接');
                                this.wsOpen.delete(server.ip);
                                this.wsCache.delete(server.ip);
                                if (this.wsOpen.size == 0) {
                                    this.prompt();
                                }
                            };
                        } catch (ex) {
                            this.newLine();
                            console.log(ex);
                            this.warn(server.host + '> 连接proxy失败');
                            this.tabHostLength--;
                            this.wsOpen.delete(server.ip);
                        }
                    } else {
                        this.newLine();
                        this.tabHostLength--;
                        this.wsOpen.delete(server.ip);
                        if (multi) {
                            this.warn(server.host + '>not find proxy for agent');
                        } else {
                            this.warn('not find proxy for agent');
                        }
                        reject();
                    }
                }).catch(reason => {
                    this.tabHostLength--;
                    this.wsCache.delete(server.ip);
                    console.log(reason);
                    if (multi) {
                        this.warn(server.host + '>not find proxy for agent');
                    } else {
                        this.warn('not find proxy for agent');
                    }
                });
            }
        }));

    }

    recv(data, server: ApplicationServer, multi: boolean) {
        this.receiving = true;
        const dataView = new DataView(data);
        const id = this.webSocketService.getInt64(dataView, 0, false);
        const type = dataView.getInt32(8);
        dataView.getInt32(12);
        const dataLength = dataView.getInt32(16);
        //8+4+4+4
        const content = this.utf8ArrayToStr(new Uint8Array(data.slice(20, 20 + dataLength)));
        const line = '------------------';
        let fullContent = '';
        const wait = this.needWait();
        const serverIp = server.ip;
        this.allContent[serverIp] = this.allContent[serverIp] || '';
        const host = server.host;

        if (type != 2 && type != 3) {
            if (type == 1 && !wait) {
                if (multi) {
                    if (host != this.lastHost) {
                        if (this.lastOutput && !this.lastOutput.endsWith('\n')) {
                            this.newLine();
                        }
                        this.writeln(line + host + line);
                        this.lastHost = host;
                    }
                    this.write(content);
                } else {
                    this.write(content);
                }
            } else {
                this.allContent[serverIp] += content;
            }
        }
        if (type == -1 || type == 2) {
            if (type == 2 && this.isTab) {
                this.tabContent += this.allContent[serverIp];
            }
            if (type == 2 && !wait) {
                return;
            }
            let result = '';
            if (multi) {
                result = line + host + line + '\n';
            }
            fullContent = this.allContent[serverIp];
            if (type == 2) {
                fullContent = this.process(this.allContent[serverIp]);
            }
            result += fullContent;
            const len = result.length;
            if (len > 0 && result.charAt(len - 1) != '\n') {
                result += '\n';
            }
            if (type == -1 && id == -1) {
                const errorMsg = JSON.parse(result);
                console.log(errorMsg);
                const error = this.errorMapping[errorMsg.code];
                if (errorMsg.message) {
                    this.writeln(line + host + line);
                    this.write(errorMsg.message);
                } else if (error) {
                    this.writeln(line + host + line);
                    this.write(error);
                } else {
                    this.writeln(line + host + line);
                    this.write('未定义的错误' + (result ? ': ' : '') + result);
                }
                this.wsOpen.delete(serverIp)
                if (this.wsOpen.size == 0) {
                    this.prompt();
                }
            } else {
                this.write(result);
            }
        } else if (type == 3) {
            if (content != '' && !this.isCanceled) {
                this.writeln(line + '统计' + line + '\n' + content);
            }
            if (this.isTab) {
                this.tabHostLength--;
                this.tabRecover(multi);
            } else {
                this.wsOpen.delete(serverIp);
                if (this.wsOpen.size == 0) {
                    this.prompt();
                }
            }
        }
    }

    needWait() {
        return this.isTab || this.waitCommand.has(this.command);
    }

    buildCommand(type: number, input: string, server: ApplicationServer): RemoteCommand {
        if (this.linuxCommandType.has(type)) {
            input = JSON.stringify({command: input});
        }
        let remoteCommand = new RemoteCommand();
        remoteCommand.type = type;
        remoteCommand.app = this.appCode;
        remoteCommand.command = input;
        remoteCommand.hosts = ['' + server.host + ''];
        remoteCommand.user = this.getUserName();
        remoteCommand.token = this.getToken();
        return remoteCommand;
    }

    parseCommand() {
        const args = this.commandLine.trim().split(/\s+/);
        this.command = args[0];
    }

    prompt() {
        this.isTab = false;
        this.isCanceled = false;
        this.receiving = false;
        this.commandLine = '';
        this.command = '';
        this.tabContent = '';

        this.newLine();
        const applicationServers = this.selectServers;
        let line: string;
        if (applicationServers.length == 0) {
            line = this.appCode + '@ALL$ ';
        } else if (applicationServers.length > 2) {
            line = this.appCode + '@many servers$ ';
        } else {
            line = this.appCode + '@' + applicationServers.map(server => server.host).join(',') + '$ ';
        }
        this.write('\x1b[33m' + line + '\x1B[0m');
    }


    warn(info): void {
        this.writeln(info);
    }

    error(error): void {
        this.newLine();
        this.write(error);
        this.prompt();
    }

    write(line): void {
        this.term.focus();
        this.term.write(line);
    }

    writeln(line): void {
        this.term.focus();
        this.term.writeln(line);
    }

    newLine(): void {
        this.writeln('');
    }

    clear(): void {
        this.term.clear();
    }

    cleanInput() {
        while (this.commandLine && this.commandLine.length > 0) {
            let lastIndex = this.commandLine.length - 1;
            if (this.chineseReg.test(this.commandLine.charAt(lastIndex))) {
                this.write('\b \b\b \b');
            } else {
                this.write('\b \b');
            }
            this.commandLine = this.commandLine.substr(0, lastIndex);
        }
    }

    setInput(newInput: string) {
        this.cleanInput();
        this.write(newInput);
        this.commandLine = newInput;
    }

    pushHistoryCommand() {
        if (!this.commandLine || this.commandLine.trim().length == 0) {
            return;
        }
        if (this.historyCommands.length > 1000) {
            this.historyCommands.shift();
        }
        this.historyCommands.push(this.commandLine);
        this.historyIndex = this.historyCommands.length - 1;
    }

    preHistoryCommand(): string {
        if (this.historyIndex >= 0) {
            this.cleanInput();
            this.commandLine = this.historyCommands[this.historyIndex--];
            this.write(this.commandLine);
            return this.commandLine;
        }
        return '';
    }

    nextHistoryCommand(): string {
        if (this.historyIndex == -1) {
            this.historyIndex = 0;
        }
        if (this.historyIndex == this.historyCommands.length - 1) {
            this.cleanInput();
            this.commandLine = '';
            return this.commandLine;
        } else if (this.historyIndex < this.historyCommands.length - 1) {
            this.cleanInput();
            this.commandLine = this.historyCommands[++this.historyIndex];
            this.write(this.commandLine);
            return this.commandLine;
        }
        return '';
    }

    matchItem(lines: Array<string>, str) {

        if (lines == null || lines.length == 0) {
            return str;
        }

        const newLines = lines.filter((line: string) => {
            return line.indexOf("No such file or directory") < 0
        })

        if (newLines.length == 0) {
            return str;
        }

        const first = newLines[0];
        const len = str.length;
        let match = str;
        for (let i = len; i < first.length; i++) {
            const c = first[i];
            for (let j = 1; j < newLines.length; j++) {
                let line = newLines[j];
                if (line == '') continue;
                if (i >= line.length || line[i] != c) {
                    return match;
                }
            }
            match += c;
        }
        return match;
    }

    getUserName(): string {
        return this.cookies.get('tc_bistoury_ui_login_id');
    }

    getToken(): string {
        return this.cookies.get('oc-admin_token').replace(/\s/g, '+');
    }

    process(content) {
        if ((this.isTab || (this.command == 'ls' && !this.ls_l()))) {
            const lines = content.split('\n');
            content = this.adapt(lines, lines.length, 164);
        }
        return content;
    };

    adapt(list, size, width) {
        let i, j, line_num, formatted = '';

        line_num = this.get_line_num(list, size, width);
        for (i = 0; i < line_num; i++) {
            for (j = i; j < size; j += line_num) {
                formatted += this.format(list[j], this.get_col_width(list, size, line_num, Math.floor(j / line_num)) + 2);
            }
            formatted += '\n';
        }
        return formatted;
    };

    ls_l() {
        if (this.command != 'ls') {
            return false;
        }
        const paramsLine = this.commandLine.substring(2);
        const params = paramsLine.trim().split(/\s/g);
        return params.length > 0 && params[0].startsWith('-') && params[0].indexOf('l') > 0;

    };

    format(content, width) {
        let str = '';
        for (let i = 0; i < width - content.length; i++) {
            str += ' ';
        }
        return content + str;
    };

    get_line_num(list, size, width) {
        let line_num = 1;

        while (line_num < size && this.get_line_width(list, size, line_num) > width) {
            line_num++;
        }
        // console.log("合适的行数: " + line_num);
        return line_num;
    }

    get_line_width(list, size, line_num) {
        let i, line_width = 0;

        for (i = 0; i < size; i += line_num) {
            line_width += this.get_col_width(list, size, line_num, Math.floor(i / line_num)) + 2;
        }
        return line_width;
    }

    get_col_width(list, size, line_num, col_index) {
        let i, start, len, max_len = 0;

        start = col_index * line_num;
        for (i = start; i < size; i++) {
            if (i != start && i % line_num == 0) {
                break;
            }
            len = list[i].length;
            if (len > max_len) {
                max_len = len;
            }
        }
        return max_len;
    }

    encryptData(content) {
        const k1 = this.makeId();
        const dataEnc = this.encryptByDES(content, k1);

        const publicKey = '-----BEGIN PUBLIC KEY-----\n' +
            'MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCzYgJiPl4ltUdOvTIx8yu5iw0+\n' +
            'k7jANyrVzXOJy+478EhBaf8MoHaHvbH06PfaLjmFJXsRZKv9Qq5SieQcLlnG60Uu\n' +
            'utpen1Nf490au+nPCP++nK3L5ZBqaSCAq4GUAniARR1wWl9TYW0walBCpD2N2Swy\n' +
            'MLu9z+Lnhd7auqYSzwIDAQAB\n' +
            '-----END PUBLIC KEY-----';
        const crypt = new JsEncryptModule.JSEncrypt();
        crypt.setPublicKey(publicKey);
        const k1Enc = crypt.encrypt(k1);

        return {
            '0': k1Enc,
            '1': dataEnc
        };
    }

    encryptByDES(message, key) {
        var keyHex = CryptoJS.enc.Utf8.parse(key);
        var encrypted = CryptoJS.DES.encrypt(message, keyHex, {
            mode: CryptoJS.mode.ECB,
            padding: CryptoJS.pad.Pkcs7
        });
        return encrypted.toString();
    }

    makeId() {
        var text = '';
        var possible = '0123456789abcdef';

        for (var i = 0; i < 8; i++) {
            text += possible.charAt(Math.floor(Math.random() * possible.length));
        }

        return text;
    }

    utf8ArrayToStr(array: Uint8Array): string {
        var out, i, len, c;
        var char2, char3;

        out = '';
        len = array.length;
        i = 0;
        while (i < len) {
            c = array[i++];
            switch (c >> 4) {
                case 0:
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 6:
                case 7:
                    out += String.fromCharCode(c);
                    break;
                case 12:
                case 13:
                    char2 = array[i++];
                    out += String.fromCharCode(((c & 0x1F) << 6) | (char2 & 0x3F));
                    break;
                case 14:
                    char2 = array[i++];
                    char3 = array[i++];
                    out += String.fromCharCode(((c & 0x0F) << 12) |
                        ((char2 & 0x3F) << 6) |
                        ((char3 & 0x3F) << 0));
                    break;
            }
        }

        return out;
    }
}
