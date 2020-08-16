import {Component, OnInit} from '@angular/core';
import {Header} from "../model/header";

@Component({
    selector: 'app-header',
    templateUrl: './header.component.html',
    styleUrls: ['./header.component.css']
})
export class HeaderComponent implements OnInit {
    headers: Set<Header>;

    constructor() {
    }

    ngOnInit(): void {
        this.headers = new Set<Header>([{
            name: '首页',
            href: "/index.html",
            selected: window.location.pathname == "/" || window.location.pathname == "/index.html",
            child: []
        }, {
            name: '主机信息',
            href: "/machine.html",
            selected: window.location.pathname == "/machine.html",
            child: []
        }, {
            name: '在线Debug',
            href: "/debug.html",
            selected: window.location.pathname == "/debug.html",
            child: []
        }, {
            name: '动态监控',
            href: "/monitor.html",
            selected: window.location.pathname == "/monitor.html",
            child: []
        }, {
            name: '文件下载',
            href: "/download.html",
            selected: window.location.pathname == "/download.html",
            child: []
        }, {
            name: '应用中心',
            href: "/application.html",
            selected: window.location.pathname == "/application.html",
            child: []
        }]);
        this.headers.add(this.buildEnvHeader());
        console.log(this.headers)
    }

    buildEnvHeader(): Header {
        const headers = {
            "dev": new Header('开发环境', 'http://bistoury.dev.qunar.com', false, []),
            "beta": new Header('测试环境', 'http://bistoury.beta.qunar.com', false, []),
            "prod": new Header('线上环境', 'http://bistoury.corp.qunar.com', false, [])
        }

        let header;
        if (window.location.host.indexOf("beta") >= 0) {
            header = new Header("当前环境（beta）", '', false, [headers.prod, headers.dev])
        } else if (window.location.host.indexOf("corp") >= 0) {
            header = new Header("当前环境（prod）", '', false, [headers.beta, headers.dev])
        } else {
            header = new Header("当前环境（dev）", '', false, [headers.prod, headers.beta])
        }
        return header;
    }

    jump(href: string, selected: boolean) {
        if (!selected && href && href.length > 0) {
            window.location.href = href;
        }
    }
}
