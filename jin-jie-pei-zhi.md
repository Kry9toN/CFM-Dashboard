# 进阶配置

如果有高度自定义需求，请学习yaml语法（[教程1](http://c.biancheng.net/spring\_boot/yaml.html)、[教程2](https://www.yiibai.com/yaml/)、[教程3](https://www.ruanyifeng.com/blog/2016/07/yaml.html)）、并阅读[config.yaml](broken-reference)教材。

## 工作模式 <a href="#work_mod" id="work_mod"></a>

模块默认接管所有应用流量。

你如果并不想某些应用的流量通过Clash服务, 则可以打开模块默认配置目录, 将`clash.config`文件里的`mode`值改为`blacklist`(黑名单且为默认值)并在`packages.list`文件里写入相应应用的包名, **一行一个**。

如果想仅部分应用的流量通过Clash服务处理, 则更改`mode`值为`whitelist`(白名单)且`packages.list`文件里一行一个写入包名. 在此之外, 你还可以**仅启动**Clash内核, 更改`mode`值为`core`即可。

### 注意 <a href="#undefined" id="undefined"></a>

`fake-ip`模式并不支持黑白名单，不走代理的应用会没网。

不知道`fake-ip`是啥请看[config.yaml教材](broken-reference)。

## enhanced-mod

有两个选项`fake-ip`和`redir-host`，具体参见[config.yaml教材](broken-reference)。

`fake-ip`[原理](https://blog.skk.moe/post/what-happend-to-dns-in-proxy/)

若更改`enhanced-mode`为`fake-ip`(可配置[fake-ip-filter](https://github.com/Dreamacro/clash/wiki/configuration#user-content-all-configuration-options:\~:text=%23%20Hostnames%20in%20this%20list%20will,%23%20%20%20%2D%20localhost.ptlogin2.qq.com)以期解决WiFi验证问题), 请将`clash.config`文件`reserved_ip`字符串中的`198.18.0.0/15`**删除**!!!!!

同理更改`enhanced-mode`为`redir-host`就把`198.18.0.0/15`**`加回`**

## 自动更新订阅 <a href="#auto_subcript" id="auto_subcript"></a>

### 单机场和多机场（原版使用这种方法需要自己手改配置文件） <a href="#undefined" id="undefined"></a>

直接在`/data/clash/config.yaml`中填入自己的订阅地址即可

`如果需要增减机场请参考`[config.yaml教材](broken-reference)。

### 如仅需使用机场的规则，不需要自定义的请使用下方订阅方法

### 单机场（如需定制规则，魔改版不推荐这种方法） <a href="#undefined" id="undefined"></a>

请打开模块默认配置目录下的`clash.config`文件, 将`auto_subscription`（魔改2为`auto_updateSubcript`）的值改为`true`并在`subscription_url`后填写你的clash订阅地址. 模块默认每天凌晨两点更新订阅, 你可以通过查看Clash配置目录下的`run`文件夹里的`run.logs`日志文件查看订阅是否更新成功(或查看相关文件时间戳), 如需更改自动订阅的时间, 可更改`update_interval`的值, 请自行学习[Crontab](https://en.wikipedia.org/wiki/Cron)相关内容。

## 你也可以手动更新

**原版**更新指令为`/data/adb/modules/Clash_For_Magisk/scripts/clash.tool -s`

**魔改版1**指令为`/data/clash/scripts/clash.tool -s`

**魔改版2**指令为`/data/clash/scripts/clash.tool -`u

## 配合类Adguard应用使用 <a href="#adg" id="adg"></a>

更改模块工作模式为core, 类Adguard应用设置代理(socks5或http均可)

## 配置本地subconverter <a href="#local_subconverter" id="local_subconverter"></a>

可以借助termux搭建或者封装成Magisk模块, 配置[本地生成](https://github.com/tindy2013/subconverter/blob/master/README-cn.md#%E6%9C%AC%E5%9C%B0%E7%94%9F%E6%88%90)模式, 修改clash.tool里关于订阅更新的函数

Termux搭建示例, 不使用root权限可能无法解压和启动:

```bash
su -c 'wget https://github.com/tindy2013/subconverter/releases/latest/download/subconverter_aarch64.tar.gz && tar -zxvf subconverter_aarch64.tar.gz'
#启动示例
su -c './subconverter/subconverter'
#如果需要常驻后台, 请了解&或者nohup相关命令.
#如果需要开机自启, 请了解Magisk模块开发, 开发者指南:https://topjohnwu.github.io/Magisk/guides.html
```

clash.tool脚本subscription函数改动示例:

```bash
subscription() {
    if [ "${auto_subscription}" = "true" ] ; then
        ${subconverter_path}/subconverter -g > /dev/null 2>&1
        if [ $? -eq 0 ] ; then
            cp -f ${subconverter_path}/config.yaml ${Clash_config_file}
            ${scripts_dir}/clash.service -k && ${scripts_dir}/clash.tproxy -k
            sleep 5
            ${scripts_dir}/clash.service -s && ${scripts_dir}/clash.tproxy -s
            if [ "$?" = "0" ] ; then
                echo "info: 订阅更新成功,CFM已成功重启." >> ${CFM_logs_file}
            else
                echo "err: 订阅更新成功,CFM重启失败." >> ${CFM_logs_file}
            fi
        else
            echo "wro: 订阅更新失败,配置文件未发生变化." >> ${CFM_logs_file}
        fi
    else
        exit 0
    fi
}
##subconverter_path变量为subconverter文件夹路径, 更新订阅后可以考虑利用Clash RESTful API重载配置而不是重启clash服务
```
