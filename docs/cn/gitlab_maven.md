在使用在线debug和动态监控时，我们提供了一下三种方式查看代码源码

- 反编译字节码
- 项目代码从git获取源码
- jar包里的类从maven获取源码

如果要使用gitlab或maven获取源码需要做以下配置。

## git配置
### 要求

目前只支持gitlab api v3、github，gitlab api v4会陆续支持。所以要使用git查询源码功能，需要公司部署有自己的gitlab仓库，且提供gitlab v3版api，或者源码在github仓库里。

### 配置

- 配置发布信息位置

    在文件`bistoury-proxy-bin/conf/releaseInfo_config.properties`中配置发布信息配置文件的位置，默认值`/tmp/bistoury/releaseInfo.properties`
- 配置发布信息

    - 在上面配置的路径下配置发布信息。默认提供`properties`文件解析，如果需要自定义解析方法，可以实现`qunar.tc.bistoury.ui.util.ReleaseInfoParse`接口，并在`qunar.tc.bistoury.ui.service.impl.ReleaseInfoServiceImpl`中修改实现类
    - 配置文件中格式如下
        ```properties
        #gitlab项目名，项目名组成：owner/repo
        project=tc/bistoury
        #项目所属module，没有module时值为英文句号[.]
        module=bistoury-ui
        #应用运行的版本hash/分支/tag
        output=master
        ```
- 配置git
    
    在文件`bistoury-ui-bin/conf/config.properties`中，
    - 配置仓库类型：`git.repository`，仓库为github则配置值为github，仓库为gitlab并且使用api v3则配置值为gitlabv3
    - 配置仓库地址：`git.endpoint`，用户从git仓库拿代码，gitlab填写首页地址，github填写api地址（https://api.github.com）
- 配置private token获取链接

    使用api从git获取文件需要一个授权码，需要将获取授权码页面的链接配置到`bistoury-ui-bin/conf/url_redirect.properties`文件中的`gitlab.private.token.url`。
    - gitlab获取位置一般在gitlab>Profile Settings>Account
    - github获取位置一般在github>settings>Developer settings>Personal access tokens，链接一般为：[https://github.com/settings/tokens](https://github.com/settings/tokens)
- 配置private token
    在线debug和动态监控页面按钮上方都有一个配置private token的链接，点击配置private token。

- 配置目录格式
    不同的项目类型有不同的目录格式，如maven项目的源码在`module/src/main/java`下，不同项目的目录格式可能不同，所以需要在`bistoury-ui-bin/conf/config.properties`文件中配置`file.path.format`，默认配置为maven项目的目录格式
## maven配置

maven配置之后可以从maven私服下载源码，所以需要在`bistoury-ui-bin/conf/config.properties`文件中配置`maven.nexus.url`为jar source的下载链接

**注意：**我们在拿到文件之后会对文件进行hash值进行校验保证文件完整性，在下载后会从响应头部获取文件的hash值。
- 获取的key为`ETag`；
- 值的格式：`{SHA1{d32c943ba20a1351181e11adb71f7e3e28bdfe3c}}`，其中SHA1是hash算法，`d32c943ba20a1351181e11adb71f7e3e28bdfe3c`是hash值。
- hash算法支持MD5、SHA1和SHA256。

