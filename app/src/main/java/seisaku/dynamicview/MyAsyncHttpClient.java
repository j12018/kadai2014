package seisaku.dynamicview;

import android.content.Context;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;

import org.apache.http.Header;

public class MyAsyncHttpClient extends AsyncHttpClient{

    RequestParams params;     // 送信パラメータ
    MyAsyncHttpResponseHandler myAsyncHttpResponseHandler = new MyAsyncHttpResponseHandler();
    final String MY_URL = "http://j12025.sangi01.net/cakephp/app/webroot/post.php";     //URL

    public MyAsyncHttpClient(Context context) {
        myAsyncHttpResponseHandler.setContext(context);
    }

    @Override
    public RequestHandle post(String url, RequestParams params, ResponseHandlerInterface responseHandler) {
        return super.post(url, params, responseHandler);
    }

    public void setParams(String id,String str) {
        params.put(id, str);//追加
    }

    public void newRequestParams() { // パラメータの初期化
        params = new RequestParams();
    }

    public RequestHandle access() {
        return post(MY_URL, params, myAsyncHttpResponseHandler);
    }
}

class MyAsyncHttpResponseHandler extends AsyncHttpResponseHandler {
    Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    public void onSuccess(int state, Header[] header, byte[] response) {//通信成功

        Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFinish() {

        Toast.makeText(context, "Finish", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {// 通信失敗

        Toast.makeText(context, "Failure", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onStart() {
    }
}