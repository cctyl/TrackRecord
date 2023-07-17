package cn.tyl.gps_demo.util;

import android.util.Log;

import com.elvishew.xlog.XLog;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Set;

import cn.tyl.gps_demo.entity.GPSData;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HttpUtil {


    private static final String URL = "http://你的后端地址";

    private static Gson gson = new Gson();
    private static MediaType mediaType = MediaType.parse("application/json; charset=utf-8");//"类型,字节码"
    //1.创建OkHttpClient对象
    private static  OkHttpClient okHttpClient = new OkHttpClient();

    /**
     * 创建一个json请求
     * @param api 前面需要带上 /，示例 /xxx/xxx
     */
    private static void createRequest(String api,Object data,String methodName,Callback callback){
        //数据转换json字符串
        String value = gson.toJson(data);
        XLog.d("准备发送请求，参数为：" + value);

        //2.通过RequestBody.create 创建requestBody对象
        RequestBody requestBody = RequestBody.create(mediaType, value);

        //3.创建Request对象，设置URL地址，将RequestBody作为post方法的参数传入
        Request request = new Request.Builder()
                .url(URL + api)
                .header("token", "你的后端token")
                .method(methodName,requestBody)
                .build();
        //4.创建一个call对象,参数就是Request请求对象
        Call call = okHttpClient.newCall(request);
        //5.请求加入调度,重写回调方法
        call.enqueue(callback);
    }


    /**
     * 上传一条gps数据
     * @param data
     */
    public static void uploadOne(Object data) {

        createRequest("/gps/add",data,"POST",new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                XLog.d("onFailure: " + e.toString());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                XLog.d("响应为: " + response.body().string());
            }
        });
    }

    /**
     * 上传多条gps数据
     * @param data
     */
    public static void uploadMany(Set<GPSData> data) {

        XLog.d("uploadMany:"+data);
        createRequest("/gps/addMany",data,"POST",new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                XLog.d("onFailure: " + e.toString());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                XLog.d("响应为: " + response.body().string());
            }
        });
    }

}
