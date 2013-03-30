
package jp.ddo.trismegistos.androidhttp.api;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jp.ddo.trismegistos.androidhttp.exception.ApiAccessException;
import jp.ddo.trismegistos.androidhttp.exception.ApiParseException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.util.Log;

/**
 * API接続を行う抽象クラス。
 * 
 * @author y_sugasawa
 * @since 2013/01/27
 */
abstract public class AbstractApi<T> implements ApiInterface<T> {

    /** タグ。 */
    private static final String TAG = AbstractApi.class.getSimpleName();

    private static final String UTF_8 = "UTF-8";
    private static final Charset UTF_8_CHAR_SET = Charset.forName(UTF_8);
    private static final int FILE_PARAM_LENGTH = 2;

    /** デフォルトのコネクションタイムアウト時間。 */
    private static final int DEFAULT_CONNECTION_TIMEOUT = 7 * 1000;
    /** デフォルトのソケットタイムアウト時間。 */
    private static final int DEFAULT_SOCKET_TIMEOUT = 7 * 1000;

    /** APIのURL */
    private String url;

    /** パラメータ */
    private Map<String, String> params;

    /** パラメータ(キーが複数のモノ) */
    private Map<String, Collection<String>> arrayParams;

    /** アップロードするファイルパラメーター */
    private String[] fileParam;

    /**
     * デフォルトコンストラクタ。
     * 
     * @param url
     */
    public AbstractApi(final String url) {
        this.url = url;
        params = new HashMap<String, String>();
        arrayParams = new HashMap<String, Collection<String>>();
        fileParam = new String[FILE_PARAM_LENGTH];
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T get() throws ApiAccessException, ApiParseException {
        final HttpGet httpGet = new HttpGet(createUrl());
        setTimeoutSetting(httpGet.getParams());
        final HttpClient httpClient = new DefaultHttpClient();
        httpGet.setHeader("Connection", "Keep-Alive");
        return request(httpClient, httpGet);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T post() throws ApiAccessException, ApiParseException {
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpPost httpPost = new HttpPost(url);
        setTimeoutSetting(httpPost.getParams());
        final MultipartEntity multipartEntity = new MultipartEntity(
                HttpMultipartMode.BROWSER_COMPATIBLE);

        try {
            if (fileParam[0] != null && fileParam[1] != null) {
                final File file = new File(fileParam[1]);
                final FileBody fileBody = new FileBody(file, "image/jpeg");
                multipartEntity.addPart(fileParam[0], fileBody);
            }
            for (final Map.Entry<String, String> entry : params.entrySet()) {
                multipartEntity.addPart(entry.getKey(), new StringBody(entry.getValue(),
                        UTF_8_CHAR_SET));
            }
            for (final Map.Entry<String, Collection<String>> entry : arrayParams.entrySet()) {
                for (final String val : entry.getValue()) {
                    multipartEntity.addPart(entry.getKey() + getArrayParameterSuffix(),
                            new StringBody(val,
                                    UTF_8_CHAR_SET));
                }
            }
        } catch (final UnsupportedEncodingException e) {
            Log.e(TAG, "" + e.getMessage());
            throw new ApiAccessException(e.getMessage());
        }
        httpPost.setEntity(multipartEntity);
        return request(httpClient, httpPost);
    }

    /**
     * リクエストを実行する。
     * 
     * @param httpClient
     * @param httpRequestBase
     * @return
     * @throws ApiAccessException
     * @throws ApiParseException
     */
    private T request(final HttpClient httpClient, final HttpRequestBase httpRequestBase)
            throws ApiAccessException, ApiParseException {
        try {
            final HttpResponse response = httpClient.execute(httpRequestBase);
            final int status = response.getStatusLine().getStatusCode();
            if (HttpStatus.SC_OK != status) {
                Log.e(TAG, "HTTP-STATUS-CODE is " + status);
                throw new ApiAccessException(status);
            }
            final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            response.getEntity().writeTo(outputStream);
            return parse(outputStream.toString());
        } catch (final ConnectTimeoutException e) {
            Log.e(TAG, "" + e.getMessage());
            final ApiAccessException ex = new ApiAccessException();
            ex.isTimeout = true;
            throw ex;
        } catch (final SocketTimeoutException e) {
            Log.e(TAG, "" + e.getMessage());
            final ApiAccessException ex = new ApiAccessException();
            ex.isTimeout = true;
            throw ex;
        } catch (final ClientProtocolException e) {
            Log.e(TAG, "" + e.getMessage());
            final ApiAccessException ex = new ApiAccessException();
            ex.isDisConnect = true;
            throw ex;
        } catch (final ApiParseException e) {
            Log.e(TAG, "" + e.getMessage());
            throw e;
        } catch (final IOException e) {
            Log.e(TAG, "" + e.getMessage());
            final ApiAccessException ex = new ApiAccessException();
            ex.isDisConnect = true;
            throw ex;
        }
    }

    /**
     * HTTPレスポンスの変換を行う。
     * 
     * @param data
     * @return
     * @throws ApiParseException
     */
    abstract protected T parse(final String data) throws ApiParseException;

    /**
     * リクエストURLを作成する。
     * 
     * @return
     */
    private String createUrl() {
        final StringBuilder sb = new StringBuilder(url);
        sb.append("?");
        for (final Map.Entry<String, String> entry : params.entrySet()) {
            sb.append(entry.getKey());
            sb.append("=");
            sb.append(encode(entry.getValue()));
            sb.append("&");
        }
        for (final Map.Entry<String, Collection<String>> entry : arrayParams.entrySet()) {
            for (final String val : entry.getValue()) {
                sb.append(entry.getKey());
                sb.append(getArrayParameterSuffix());
                sb.append("=");
                sb.append(encode(val));
                sb.append("&");
            }
        }
        final String url = sb.toString();
        return url.substring(0, url.length() - 1);
    }

    /**
     * UTF8でエンコードする。
     * 
     * @param val
     * @return
     */
    private String encode(final String val) {
        try {
            return URLEncoder.encode(val, UTF_8);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /**
     * タイムアウト時間の設定を行う。
     * 
     * @param params HttpParams
     */
    private void setTimeoutSetting(final HttpParams params) {
        HttpConnectionParams.setConnectionTimeout(params, getConnectionTimeout());
        HttpConnectionParams.setSoTimeout(params, getSocketTimeout());
    }

    /**
     * コネクションタイムアウトの時間を取得する。
     * 
     * @return コネクションタイムアウト時間(単位ms)
     */
    protected int getConnectionTimeout() {
        return DEFAULT_CONNECTION_TIMEOUT;
    }

    /**
     * ソケットタイムアウトの時間を取得する。
     * 
     * @return ソケットタイムアウト時間(単位ms)
     */
    protected int getSocketTimeout() {
        return DEFAULT_SOCKET_TIMEOUT;
    }

    /**
     * 複数パラメーターのKEYのsuffixを取得する。
     * 
     * @return suffix
     */
    protected String getArrayParameterSuffix() {
        return "";
    }

    /**
     * パラメータを設定する。
     * 
     * @param key
     * @param val
     */
    public void setParams(final String key, final String val) {
        params.put(key, val);
    }

    /**
     * 複数系のパラメータを設定する。
     * 
     * @param key
     * @param vals
     */
    public void setArrayParams(final String key, final Collection<String> vals) {
        arrayParams.put(key, vals);
    }

    /**
     * ファイルパラメーターを設定する。
     * 
     * @param key
     * @param filePath
     */
    public void setFileParam(final String key, final String filePath) {
        fileParam[0] = key;
        fileParam[1] = filePath;
    }

    /**
     * 各パラメータを初期化する。
     */
    public void allClear() {
        params.clear();
        arrayParams.clear();
        fileParam = new String[FILE_PARAM_LENGTH];
    }
}
