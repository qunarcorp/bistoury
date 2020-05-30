# bistoury存储方案
bistory的动态监控数据、线程级cpu监控数据、jmap数据均需要在应用机器上保存三天，需要选择一款数据库来保存这些数据。

bistoury之前的版本采用的是rocksdb进行存储，定时手动调用compact，但是rocksdb进行数据压缩时会使得磁盘IO及cpu飙高，rocksdb自身的压缩线程优先级太低，长时间使用会导致磁盘占用增高。

基于以上问题，bistoury将存储方案从rocksdb换为sqlite，SQLite是一个轻量级、跨平台的关系型数据库。既然号称关系型数据库，删除数据不会直接释放磁盘，下次写入时会复用已经删除的磁盘空间，保证磁盘占用不会无限增长

## 存储方案切换
agent 启动时配置系统参数-Dbistoury.store.db=sqlite可将存储方案从rocksdb切为sqlite
**注意：** 切换存储方案后，会将原来rocksDb存储的数据全部删除，导致最近三天的数据丢失

