$(document).ready(function () {
    var headers = [{
        name: "首页",
        href: "/",
        active: function () {
            if (window.location.pathname == "/" || window.location.pathname == "/qconsole.html") {
                return true;
            }
            return false;
        }
    }, {
        name: "主机信息",
        href: "/machine.html",
        active: function () {
            if (window.location.pathname == "/machine.html") {
                return true;
            }
            return false;
        }
    }, {
        name: "在线Debug",
        href: "/debug.html",
        active: function () {
            if (window.location.pathname == "/debug.html") {
                return true;
            }
            return false;
        }
    }, {
        name: "动态监控",
        href: "/monitor.html",
        active: function () {
            if (window.location.pathname == "/monitor.html") {
                return true;
            }
            return false;
        }
    }, {
        name: "文件下载",
        href: "/download.html",
        active: function () {
            if (window.location.pathname == "/download.html") {
                return true;
            }
            return false;
        }
    }, {
        name: "应用中心",
        href: "/application.html",
        active: function () {
            if (window.location.pathname == "/application.html") {
                return true;
            }
            return false;
        }
    }]

    function initHeader() {
        var href = window.location.href;
        var containerFluid = $("<div></div>").addClass("container-fluid");
        var navbarHeader = $("<div></div>").addClass("navbar-header").append($("<a></a>").attr("href", "/").append($("<img>").attr("src", "/image/tcdev.png")));

        var navbarLeft = $("<ul></ul>").addClass("nav navbar-nav navbar-left");
        headers.forEach(function (header) {
            var li = $("<li></li>").append($("<a></a>").attr("href", header.href).append(header.name));
            if (typeof header.active == "function" && header.active.call()) {
                li.addClass("active");
            }
            navbarLeft.append(li);
        })
        if (href.indexOf("help.html") >= 0) {
            var help = $("<li></li>").addClass("active").append($("<a></a>").attr("href", "/help.html").append("帮助文档"));
            navbarLeft.append(help)
        }

        var dropdownLi = getCurrentEnv();
        navbarLeft.append(dropdownLi);

        var navbarRight = $("<ul></ul>").addClass("nav navbar-nav navbar-right");
        var back = $("<li></li>").append($("<a></a>").addClass("navbar-brand back").attr("href", "/").append("返回首页"));
        var logout = $("<li></li>").append($("<a></a>").addClass("navbar-brand back").attr("href", "/logout.do").append("退出"));
        if (href.indexOf("help.html") < 0) {
            var help = $("<li></li>").append($("<a></a>").addClass("navbar-brand").attr("href", "/help.html").append("帮助文档"));
            navbarRight.append(help)
        }
        navbarRight.append(back);
        navbarRight.append(logout);

        var navbarCollapse = $("<div></div>").addClass("collapse navbar-collapse").attr("id", "bs-example-navbar-collapse-1");
        navbarCollapse.append(navbarLeft).append(navbarRight);

        containerFluid.append(navbarHeader);
        containerFluid.append(navbarCollapse);
        var nav = $("<nav></nav>").addClass("navbar navbar-default");
        nav.append(containerFluid);
        $("#header").append(nav);
    }

    function getCurrentEnv() {
        var urls = {
            "dev": "/api/url/redirect.do?name=bistoury.ui.dev",
            "beta": "/api/url/redirect.do?name=bistoury.ui.beta",
            "prod": "/api/url/redirect.do?name=bistoury.ui.prod"
        }
        var li = $("<li></li>").addClass("dropdown");
        var currentUrl = $("<a></a>").addClass("dropdown-toggle").attr("data-toggle", "dropdown").attr("role", "button").attr("aria-haspopup", "true").attr("aria-expanded", false);
        var name = "";

        var urlUl = $("<ul></ul>").addClass("dropdown-menu");
        var dev = $("<li></li>").append($("<a></a>").attr("href", urls.dev).append("开发环境"));
        var beta = $("<li></li>").append($("<a></a>").attr("href", urls.beta).append("测试环境"));
        var prod = $("<li></li>").append($("<a></a>").attr("href", urls.prod).append("生产环境"));
        if (window.location.host.indexOf("beta") >= 0) {
            window.appcenter = window.location.host + "/application.html";
            name = "测试环境）";
            urlUl.append(prod)
            urlUl.append(dev)
        } else if (window.location.host.indexOf("prod") >= 0) {
            window.appcenter = window.location.host + "/application.html";
            name = "线上环境";
            urlUl.append(beta)
            urlUl.append(dev)
        } else {
            window.appcenter = window.location.host + "/application.html";
            name = "开发环境";
            urlUl.append(prod)
            urlUl.append(beta)
        }
        currentUrl.append($("<span></span>").append(name)).append($("<span></span>").addClass("caret"));
        li.append(currentUrl).append(urlUl);
        return li;
    }

    window.appcenter = "";

    initHeader();
})