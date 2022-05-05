# ccod-config-starter

#### 软件架构
软件架构说明

1.CustomMadeEnvironmentPostProcessor为切入点，加载自定义的远程数据源到spring environment当中

2.CustomRefreshBeanPostProcessor在容器启动扫描bean带@value注解的解析字段元数据并加入待刷新缓存bean列表


3.DoRefreshJob为自动刷新实现类，两秒执行一次，会调用资源加载器的customSourceProvide.refresh()方法获取需要变更的key列表，注：为了支持spring el表达式的解析功能，需要在refresh方法中把变更的字段信息重新写入environment当中

4.本项目主要针对小型项目开发，为了不引入三方依赖，支持多远程数据源聚合，可自由扩展redis，mysql远程数据源

5.刷新部分逻辑借鉴apollo，感兴趣可见https://gitee.com/apolloconfig/apollo?_from=gitee_search

#### 安装教程

基于springboot项目
install 本项目后，在自己项目中引入maven依赖
为了避免依赖冲突，建议在最上层model引入依赖
```
<dependency>
    <groupId>com.ccod.refresh</groupId>
    <artifactId>ccod-refresh-starter</artifactId>
    <version>1.0.0</version>
<dependency>
```

#### 使用说明

项目基于springboot
1. 配置中新增配置 ccod.custom.enable=true 则代表自动刷新功能的开启，否则将不生效
2. 项目远程数据来源接口为com.ccod.refresh.provide.CustomSourceProvide，默认实现为com.ccod.refresh.provide.impl.DefaultRedisSourceProvide，可通过配置【ccod.custom.refresh.provide】指定全类名来替换，可配置多个，逗号隔开

| 配置名称 |  默认值 |                                    说明                                     |
|:-----|-----:|:-------------------------------------------------------------------------:|
| ccod.custom.enable  | true |                             功能全局开关，为true代表开启                              |
| ccod.custom.refresh.provide  |  com.ccod.refresh.provide.impl.DefaultRedisSourceProvide | 远程数据源加载类，可修改此配置执行任意加载资源逻辑，可参考com.ccod.refresh.provide.CustomSourceProvide |
| ccod.custom.ann.parse  |  com.ccod.refresh.parse.impl.SpringValueParse |            spring注解解析器，默认实现为@value注解解析器，可配置多个逗号隔开，可对其他资源注解进行扩展            |

#### 参与贡献

1.  Fork 本仓库
2.  新建 Feat_xxx 分支
3.  提交代码
4.  新建 Pull Request
