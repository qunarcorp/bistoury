use bistoury;
insert into bistoury_app(code, name, group_code, status, creator) values ('bistoury_demo_app','demo_app','tcdev',1,'admin');
insert into bistoury_user_app (app_code, user_code) values ('bistoury_demo_app','admin');
insert into bistoury_server (server_id, ip, port, host, log_dir, room, app_code, auto_jstack_enable, auto_jmap_histo_enable) values ('bade8ba7d59b4ca0b91a044739a670aa','172.19.0.5',8686,'172.19.0.5','/home/q/www/logs','cn0','bistoury_demo_app',1,1);
insert into bistoury_server (server_id, ip, port, host, log_dir, room, app_code, auto_jstack_enable, auto_jmap_histo_enable) values ('bade8ba7d59b4ca0b91a044739a670ac','172.19.0.6',8687,'172.19.0.5','/home/q/www/logs','cn0','bistoury_demo_app',1,1);
