# FC Integrated With Dingtalk Callback Demo

This demo introduces a simple solution to integrate Dingtalk with Function Compute, using Dingtalk http callback to invoke function, 
and then function can read the [Dingtalk Event](https://developers.dingtalk.com/document/app/configure-event-subcription) you subscript.
When the events triggered, such as department change、new member join, Dingtalk will send the notification to the function HTTP URL, 
and then you can process the event accordingly.

## Step1: Prepare the environment
1. Install [Funcraft](https://help.aliyun.com/document_detail/140283.html?spm=a2c4g.11186623.6.820.6a034e21y2jlx1) on the local machine. For more information, see installation instructions.
  - Run `fun --version` to check whether the installation is successful.
  - You need to configure funcraft with your own aliyun access key id and access key secret. Follow the steps in Configure Funcraft .Run fun config to configure Funcraft. Then configure Account ID, Access Key ID, Access Key Secret, and Default region name as prompted.

```
$ fun config
Aliyun Account ID 1234xxx
Aliyun Access Key ID xxxx
Aliyun Access Key Secret xxxx
Default region name cn-xxxx
The timeout in seconds for each SDK client invoking 300
The maximum number of retries for each SDK client 5
Allow to anonynously report usage statistics to improve the tool over time? (Y/n)

```

2. Enter [Dingtalk open platform](https://open-dev.dingtalk.com/#/index)
  - Enter Application-Developing -> Corporation-Inner-Develop -> MiniApp, and create a MiniApp, then record the `AppKey`.
  - Enter the MiniApp you just created, and go to the Event-Subscription, fill or get the `aes_key`、`token` 


## Step2: Deploy function
1. Edit `template.yml` file to fill the environment variables `your_app_key、your_aes_key、your_token` 
2. For testing purpose, you can create a logsore for the function, all function logs will be collected to the logstore:
  - Create a [Aliyun Logstore](https://www.alibabacloud.com/help/doc-detail/48874.htm) on the region according to `fun config` config。
  - Fill `your_project_name`和`your_logstore_name` on `template.yml` file。

3. Run following command on the demo root path：

 ```
fun build  # build the demo
fun deploy -y # deploy function
```

## Step3: Apply a custom domain for function

Since the HTTP callback endpoint must be a public network access URL at Dingtalk, so you need to apply a [custom domain](https://help.aliyun.com/document_detail/90763.html?spm=a2c4g.11186623.6.663.29333360fRtzdh)
, and bind the function to the domain path.

Eg: this demo's custom domain `http://30612374-1221968287646227.test.functioncompute.com/fc-dingtalk-demo`

![](https://congxiao.oss-cn-beijing.aliyuncs.com/20210223134710.jpg?versionId=CAEQCRiBgICykNy1vhciIDJkZWZmNjcwZTc2NzQ0YTU5YTc2ZmNiMWYzNzE2MTNh)

## Step4: Event registration at Dingtalk developer platform
Enter [Dingtalk open platform](https://open-dev.dingtalk.com/#/index)

1. Enter the MiniApp you just created, and go to the Event-Subscription, fill the `Request URL` box using the function custom domain.
2. Click `Save` button and Dingtalk will send a `check_url` request to your function, save success means integration is OK. 
3. Open the switch button of the event you want to subscribe.

More information please see [Event subscription](https://developers.dingtalk.com/document/app/configure-event-subcription)

![](https://congxiao.oss-cn-beijing.aliyuncs.com/20210222223931.jpg?versionId=CAEQCRiBgIDe1.eovhciIGYzMDdhMzEwYzM0MDQ3MThhYWY5NmFjM2I4OGVhYjRm)

## Step5: Testing
When a new member join to your corporation, function will receive a `hrm_user_record_change` event, you can see the function log on the logstore:

![](https://congxiao.oss-cn-beijing.aliyuncs.com/func-log.png?versionId=CAEQCRiBgICd0MOyvhciIGU2N2M4MzkzNjdiYjQ3Zjc5NTQzNzI3MmVkZGM3MGE0)
