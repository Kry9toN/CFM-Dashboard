# 控制方法

## **指令控制** <a href="#cmd" id="cmd"></a>

### **原版** <a href="#undefined" id="undefined"></a>

`/data/adb/modules/Clash_For_Magisk/scripts/clash.service -k && /data/adb/modules/Clash_For_Magisk/scripts/clash.tproxy -k` #停止

`/data/adb/modules/Clash_For_Magisk/scripts/clash.service -s && /data/adb/modules/Clash_For_Magisk/scripts/clash.tproxy -s` #启动

### 魔改版 <a href="#undefined" id="undefined"></a>

`/data/clash/scripts/clash.service -k && /data/clash/scripts/clash.tproxy -k` #停止

`/data/clash/scripts/clash.service -s && /data/clash/scripts/clash.tproxy -s` #启动

### 三版本通用 <a href="#undefined" id="undefined"></a>

* **原版在Android12**下如不能控制请参阅[常见问题](broken-reference)
* 本方法存在局限性：如**用此方法停止后重启手机**只能用上方指令启动

`touch /data/adb/modules/Clash_For_Magisk/disable` #停止

`rm -f /data/adb/modules/Clash_For_Magisk/disable` #启动

## **Dashboard App**

此App目前存在两个版本：[由_LikeJson_开发的原版](https://github.com/LikeJson/DashBoard)及[由_adlyq_ fork的版本](https://github.com/Adlyq/DashBoard-1)

原版也可在[TG频道](https://t.me/db4cm)下载

* 原版DB仅可控制原版模块（魔改版请自行测试）
* fork版本为魔改2专供，于V4.8版本适配了魔改版1及原版，但是体验暂时不如与魔改2搭配使用

原版已于2021年八月停更，但该有的功能已经齐全

fork版还在维护，加入了一些小功能，如：日志显示、测速、手动更新GeoX、热加载配置等，**但也考虑到功能重叠删除了些功能订阅下载、cpfm控制等。**

## 网页面板 <a href="#web_db" id="web_db"></a>

### 远程cal面板 <a href="#undefined" id="undefined"></a>

浏览器打开 [http://clash.razord.top](http://clash.razord.top) 或者 [http://yacd.haishan.me](http://yacd.haishan.me)。

### 本地面板 <a href="#local_db" id="local_db"></a>

如果你担心在线版本的控制面板存在隐私问题, Clash For Magisk模块本身已配置本地控制面板, 浏览器打开 [http://127.0.0.1:9090/ui](http://127.0.0.1:9090/ui) 即可使用。

**如果你修改过`template`的`external-controller`请使用你设置的地址**

### **更新本地面板** <a href="#update_local_db" id="update_local_db"></a>

三大面板自行选择：[MetaCubeX/Dashboard](https://github.com/MetaCubeX/Dashboard)、[yacd](https://github.com/haishanh/yacd)、[razord](https://github.com/Dreamacro/clash-dashboard)

* 下载面板文件解压到Clash配置目录某目录, 更改template文件里external-ui: 后的文件夹名称为该目录名重启Clash服务即可。见[config.yaml教材](broken-reference)。
* **或**替换原来的面板文件既可

## RESTful API

高级操作，具体请看[官方文档](https://clash.gitbook.io/doc/restful-api)
