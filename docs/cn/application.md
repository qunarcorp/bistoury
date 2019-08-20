# 应用中心
bistoury实现了一个简单的应用中心功能，如果公司内部有自己的应用中心，可以实现`bistoury-application-api`模块中的接口接入自己的应用中心。
## 功能说明

应用中心主要职责如下：
- 人/应用/服务器之间的关系
- 应用/服务器的信息
## 使用说明
- 点击【应用中心】进入应用中心页面，这里会展示当前登录用户的所有应用
![app](../image/app_panel.png)
- 点击【新增】可以新增一个应用，新增应用时会默认将当前登录用户添加到应用负责人中.
![app_add](../image/app_add.png)
- 点击【管理】可以对应用信息进行管理，可以修改应用信息
![app_info](../image/app_info.png)
- 点击【服务器管理】tab也可以对当前应用的服务器信息进行管理，点击列表中的开关可以对线程级cpu监控及堆对象概览的开关进行管理
![app_server](../image/app_server.png)
- 点击新增可以新增服务器，管理可以堆服务器信息进行管理
![app_server_manger](../image/app_server_manger.png)