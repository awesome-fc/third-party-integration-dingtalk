# 函数计算钉钉HTTP回调Demo

本项目使用函数计算实现钉钉订阅事件的回调HTTP接口，将订阅的钉钉事件通知发送到函数计算中以进行下一步处理。

钉钉会向应用推送订阅的事件，例如部门变更、签到通知、打卡通知等。通过订阅这些事件，开发者可以更好地与钉钉集成。你只需告诉钉钉当某个事件发生时，钉钉需要推送消息到哪个URL，钉钉会以HTTP POST请求的方式将事件内容以JSON格式推送给定义的回调接口。

## 准备步骤
1. 安装[Fun工具](https://help.aliyun.com/document_detail/140283.html?spm=a2c4g.11186623.6.820.6a034e21y2jlx1)
- 执行 `fun --version` 检查安装是否成功。
- 执行 `fun config` 配置 Fun。然后按照提示依次配置 Account ID、Access Key ID、Access Key Secret、Default region name。
2. 进入[钉钉开发者平台](https://open-dev.dingtalk.com/#/index)
 - 进入应用开发-企业内部开发-小程序-创建应用：然后记录下应用的`AppKey`
 - 进入企业内部开发-小程序-事件订阅，获取或者填写`aes_key`、`token`

## 部署函数
1. 编辑`template.yml`文件，将其中的环境变量的值填入上述拿到的值：`your_app_key、your_aes_key、your_token`
2. 为方便测试，可以为函数配置上日志Logstore，函数中打印出的日志都会被搜集到该Logstore中
- 在准备步骤 `fun config` 配置的对应region中创建相应的Logstore。
- 填入`template.yml`文件`your_project_name`和`your_logstore_name`。
3. 直接在本项目根目录下运行：
 ```
fun build  # 编译项目
fun deploy -y # 部署函数
```

## 申请自定义域名
因为钉钉的回调HTTP接口必须要求为公网可访问的接口，则需要为函数申请一个自定义域名，并绑定到本函数
具体可以参考文档[绑定自定义域名](https://help.aliyun.com/document_detail/90763.html?spm=a2c4g.11186623.6.663.29333360fRtzdh) 进行操作。

例如本demo申请的一个自定义域名为：`http://30612374-1221968287646227.test.functioncompute.com/fc-dingtalk-demo`

![](https://congxiao.oss-cn-beijing.aliyuncs.com/20210222224022.jpg?versionId=CAEQCRiBgIDVh.uovhciIDljNDZkZWQxN2ZmZjRjNWQ4NmIzN2JjMTYyOTBiOWFi)

## 在钉钉开发者平台注册事件订阅

1. 进入企业内部开发-小程序-事件订阅，将上一步中配置的自定义域名填入"请求网址"中
2. 点击"保存"后页面会自定向该请求网址发送一条校验信息，如果没有报错则表示订阅成功。
3. 页面下方事件订阅，可打开相关需要订阅的事件即可。

更多信息参考钉钉文档[配置事件订阅](https://developers.dingtalk.com/document/app/configure-event-subcription)

![](https://congxiao.oss-cn-beijing.aliyuncs.com/20210222223931.jpg?versionId=CAEQCRiBgIDe1.eovhciIGYzMDdhMzEwYzM0MDQ3MThhYWY5NmFjM2I4OGVhYjRm)

## 测试
订阅了在钉钉的应用群中，当新成员加入时候，可以进入配置的函数日志Logstore查看打印出的日志触发了`hrm_user_record_change`事件：

![](https://congxiao.oss-cn-beijing.aliyuncs.com/func-log.png?versionId=CAEQCRiBgICd0MOyvhciIGU2N2M4MzkzNjdiYjQ3Zjc5NTQzNzI3MmVkZGM3MGE0)
