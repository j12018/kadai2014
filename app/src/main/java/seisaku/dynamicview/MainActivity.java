package seisaku.dynamicview;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {


    MyAsyncHttpClient myAsyncHttpClient;
    private static final String TAG = "DynamicView";
    private static final int INIT_CHILD_COUNT = 3;
    private static final String KEY_INPUT_DATA = "input.data";
    private static final String KEY_FIELD_COUNT = "fld.count";
    private static final String KEY_SELECT_POS = "select.pos";
    private static final String TYPE_PHONE = "参加者";
    private static final String TYPE_MAIL = "使用キャラクター（ドラスレ）";
    private static final String TYPE_ADD = "色（ブロックス、NightClan）";
    private static final String[] ITEM_TYPES = {TYPE_PHONE, TYPE_MAIL, TYPE_ADD};
    private android.os.Bundle savedInstanceState;
    private static final String url = "http://j12025.sangi01.net/upload/post.php";

    public static InputStream is = null;
    public static JSONArray json_array = null;
    public static DefaultHttpClient httpClient;

    Button b1;

    class EditItem {
        String type;
        LinearLayout layout;
        List<View> fields;

        EditItem() {
            fields = new ArrayList<View>();
        }
    }

    // すべての項目と項目追加ボタンの親ビュー
    private LinearLayout mContainerView;
    // 追加項目選択ダイアログ
    private AlertDialog mItemSelectDialog;

    // 追加項目のマップ
    private Map<String, EditItem> fItems = new HashMap<String, EditItem>();


    public static void main(String getSERVER,String getURL,String getDB,String getID){
        // スキーム登録：スキーム(httpやhttpsなんか)を登録します。ポート80番で接続
        SchemeRegistry schReg = new SchemeRegistry();
        schReg.register(new Scheme(HttpHost.DEFAULT_SCHEME_NAME, PlainSocketFactory.getSocketFactory(),80));

        // HTTPパラメータ設定：プロトコルやエンコードを指定します。
        HttpParams httpParams;
        httpParams= new BasicHttpParams();
        HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);

        // HTTPクライアント生成：httpを利用するためのクライアント生成（ブラウザみたいな感じです）
        httpClient = new DefaultHttpClient(new ThreadSafeClientConnManager(httpParams, schReg),httpParams);

        // HTTP Request送信
        HttpResponse response = null;
        try{
            // URIを設定：先ほど作成したphpファイルにアクセスするための情報を設定。
            Uri.Builder uriBuilder = new Uri.Builder();
            uriBuilder.path("http://j12025.sangi01.net/cake2/post.php"); //先ほど作成したphpのURLを設定
            uriBuilder.appendQueryParameter("j12025.sangi01.net",getSERVER); // サーバー名を設定
            uriBuilder.appendQueryParameter("j12025",getID); // UserIDを設定
            uriBuilder.appendQueryParameter("j12025",getDB); // Database名を設定
            //↓このresponse変数がphpで出力したJSONデータを受け取る。
            response = httpClient.execute(new HttpHost(getSERVER),new HttpGet(uriBuilder.build().toString()));
        }catch(Exception e){
            Log.e("Errrer", "接続エラー");
            return;
        }

        // レスポンスを取得してStringに変換
        StringBuilder json = new StringBuilder();
        try{
            HttpEntity entity = response.getEntity();
            InputStream input = entity.getContent();
            InputStreamReader reader = new InputStreamReader(input);
            BufferedReader bufReader = new BufferedReader(reader);
            String line;
            while((line = bufReader.readLine()) != null){
                json.append(line);
            }
        }catch(IOException e){
            Log.e("Errrer","バッファ読み込み失敗");
            return;
        }

        // JSON解析：json_arrayにデータを格納。これでMySQLデータ読み込み完了です。
        try{
            JSONObject json_data = new JSONObject(json.toString());
            json_array = json_data.getJSONArray("response");
        }catch(JSONException e){
            Log.e("Errrer","JSONデータが不正");
            return;
        }
        return;
    }





    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContainerView = (LinearLayout) findViewById(R.id.root_view);

        b1 = (Button)findViewById(R.id.button1);

        myAsyncHttpClient = new MyAsyncHttpClient(getApplicationContext());

        b1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String moji1 = "123";
                String moji2 = "465";
                String moji3 = "789";
                myAsyncHttpClient.newRequestParams();

                //aaやbbはweb側と合わせる
                myAsyncHttpClient.setParams("aa",moji1);	//送るファイルを設定
                myAsyncHttpClient.setParams("bb",moji2);	//送るファイルを設定
                myAsyncHttpClient.setParams("cc",moji3);	//送るファイルを設定
                myAsyncHttpClient.access();
            }
        });

    }

    /**
     * アクティビティが再構築された後、保存状態で画面を更新する。
     */
    @Override
    protected void onRestoreInstanceState(Bundle inState) {
        super.onRestoreInstanceState(inState);
        for (String itemType : ITEM_TYPES) {
            int fieldCnt = inState.getInt(itemType + KEY_FIELD_COUNT);
            if (fieldCnt > 0) {
                inflateEditItem(itemType);
                for (int i = 0; i < fieldCnt; i++) {
                    int pos = inState.getInt(itemType + KEY_SELECT_POS + i);
                    String data = inState.getString(itemType + KEY_INPUT_DATA
                            + i);
                    inflateEditRow(itemType, data, pos);
                }
            }
        }
    }

    /**
     * 「新規項目追加」ボタンの onClickハンドラ.
     */
    public void onAddNewItemClicked(View v) {
        final int checkedItem = -1;
        mItemSelectDialog = new AlertDialog.Builder(MainActivity.this)
                .setTitle("追加する項目を選択してください")
                .setSingleChoiceItems(ITEM_TYPES, checkedItem,
                        new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog,
                                                int which) {
                                String type = ITEM_TYPES[which];
                                mItemSelectDialog.dismiss();
                                // 　選択されたタイプの項目を親コンテナに挿入
                                inflateEditItem(type);
                                // 挿入項目に最初の１行追加
                                inflateEditRow(type, "", 0);
                            }
                        }).create();
        mItemSelectDialog.show();
    }

    /**
     * 「新規追加」ボタンの onClick ハンドラ.
     */
    public void onAddNewClicked(View v) {
        // 親ビューのテキストビューから項目タイプを取得
        View rowContainer = (View) v.getParent();
        TextView textv = (TextView) rowContainer.findViewById(R.id.textv_item);
        String itemType = textv.getText().toString();

        // 新規項目を取得して
        inflateEditRow(itemType, "", 0);
    }

    // 各行の "X" ボタンの onClick ハンドラ
    public void onDeleteClicked(View v) {
        // ボタンの親 : rowView を取得
        View rowView = (View) v.getParent();
        // その親 : 項目レイアウトを取得
        LinearLayout rowContainer = (LinearLayout) rowView.getParent();
        String type = ((TextView) rowContainer.findViewById(R.id.textv_item))
                .getText().toString();

        if (rowContainer.getChildCount() == INIT_CHILD_COUNT) {
            // 行が１つの場合トップコンテナから項目を削除する
            mContainerView.removeView(rowContainer);
            fItems.remove(type);
        } else {
            // 項目から行を削除する
            rowContainer.removeView(rowView);
            fItems.get(type).fields.remove(rowView);
        }
    }

    // 項目を取得するためのヘルパー
    private void inflateEditItem(String type) {
        EditItem editItem = new EditItem();

        // レイアウトXMLから項目ビューを取得
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View itemView = inflater.inflate(R.layout.row_container, null);
        editItem.layout = (LinearLayout) itemView;
        editItem.type = type;
        fItems.put(type, editItem);

        // 追加項目のラベルに、項目タイプを設定
        final TextView textv = (TextView) itemView
                .findViewById(R.id.textv_item);
        textv.setText(type);

        // すべての行の最後で「新規項目追加」ボタンの前に入れる
        mContainerView.addView(itemView, mContainerView.getChildCount() - 1);
    }

    // 行を取得するためのヘルパー
    private void inflateEditRow(String itemType, String data, int select) {

        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflateRowView(itemType);

        final Spinner spinner = findSpinner(itemType, rowView);
        LinearLayout itemLayout = fItems.get(itemType).layout;
        fItems.get(itemType).fields.add(rowView);

        if (data != null && !data.equals("")) {

        }
        if (select > 0) {
            spinner.setSelection(select);
        }

        // すべての行の最後で「新規追加」ボタンの前に入れる
        itemLayout.addView(rowView, itemLayout.getChildCount() - 1);
    }

    private View inflateRowView(String itemType) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = null;
        if (itemType.equals(TYPE_PHONE)) {
            rowView = inflater.inflate(R.layout.row, null);
        } else if (itemType.equals(TYPE_MAIL)) {
            rowView = inflater.inflate(R.layout.row2, null);
        } else {
            rowView = inflater.inflate(R.layout.row3, null);
        }
        return rowView;
    }


    private Spinner findSpinner(String itemType, View rowView) {
        Spinner spinner = null;
        if (itemType.equals(TYPE_PHONE)) {
            spinner = (Spinner) rowView.findViewById(R.id.spinner_phone);
        } else if (itemType.equals(TYPE_MAIL)) {
            spinner = (Spinner) rowView.findViewById(R.id.spinner_mail);
            spinner = (Spinner) rowView.findViewById(R.id.spinner_mail2);
        } else {
            spinner = (Spinner) rowView.findViewById(R.id.spinner_add);
            spinner = (Spinner) rowView.findViewById(R.id.spinner_add2);
        }
        return spinner;
    }


}


