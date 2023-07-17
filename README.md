### TrackRecord 

开源免费生活轨迹记录软件





### 修改

android部分：

TrackRecord\android\app\src\main\java\cn\tyl\gps_demo\util\HttpUtil.java

第30行：修改你的后端地址

```java
private static final String URL = "http://你的后端地址"; 
```

第52行：修改你的accessToken

```java
.header("token", "你的后端token")
```

TrackRecord\android\app\src\main\AndroidManifest.xml

第48行：android:value部分，改为你的高德地图sdk的key

```xml
 <meta-data android:name="com.amap.api.v2.apikey" android:value="xxxxx">
```





server部分：

TrackRecord\server\app.js

第42行：定义你的accessToken，这个token会用于 android 和web端的访问

```js
let accessToken = "你的token";
```

TrackRecord\server\model\db.js

如果你的mongodb需要密码，则打开 8-16行注释，并修改第9行的账号密码

```js
/*
mongoose.connect('mongodb://账号:密码@127.0.0.1:27017/gps?authSource=admin',function (err) {

    if (err)
        console.log(err)
    else
        console.log("数据库连接成功")
});
*/
```

如果你的mongodb不需要密码，并且端口也是默认的，那么无需修改



web部分：

TrackRecord\web\index.html

第18行： 修改腾旭地图的apikey为你自己申请的key

```html
<script src="https://map.qq.com/api/gljs?v=1.exp&key=你的腾讯地图apikey"></script>
```

第68、69行： 修改你后端的accessToken 和 后端url

```js
   base_url: 'http://你的后端地址',
   accessToken:"你的后端访问token"
```

