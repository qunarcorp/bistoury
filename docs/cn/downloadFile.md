## 文件下载
在使用文件下载前，需要管理员配置`download_dir_limit.properties`文件，以确定哪些文件是可以下载的

除了配置全局的文件下载限制外，可针对单个appcode配置各自的下载目录

```properties
#文件下载文件夹限制，只能在指定目录下下载，可以对每个appcode进行单独限制

#文件下载文件夹限制, 全局默认值, 多个文件夹使用英文逗号（,）分隔，dump文件夹。/tmp/bistoury-class-dump位于com/taobao/arthas/core/advisor/Enhancer.java:209中修改
default.download.dump.dir=/tmp/bistoury/qjtools/qjdump,/tmp/bistoury-class-dump
#文件下载文件夹限制，全局默认值, 多个文件夹使用英文逗号（,）分隔，其他文件夹
default.download.other.dir=/tmp/bistoury/other

#针对appcode进行限制
#文件下载文件夹限制, 针对appcode限制, 多个文件夹使用英文逗号（,）分隔，dump文件夹
#appcode.download.dump.dir=/tmp/bistoury/qjtools/qjdump
#文件下载文件夹限制，针对appcode限制, 多个文件夹使用英文逗号（,）分隔，其他文件夹
#appcode.download.other.dir=/Users/leix.xie/Downloads/
```

提示：

- 配置文件中的所配置的路径均为临时目录，如果需要上线，请务必根据实际情况修改目录
- 需要dump 插桩后的class文件的话，请移步修改com/taobao/arthas/core/advisor/Enhancer.java:209中的dump路径
