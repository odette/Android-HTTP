package jp.ddo.trismegistos.androidhttp.api;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jp.ddo.trismegistos.androidhttp.exception.ApiParseException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 * API接続を行う抽象クラス。
 * 
 * @author y_sugasawa
 * @since 2013/01/27
 */
abstract public class AbstractApi<T> implements ApiInterface<T> {

	private static final String TAG = AbstractApi.class.getSimpleName();
	private static final String UTF_8 = "UTF-8";
	private static final Charset UTF_8_CHAR_SET = Charset.forName(UTF_8);
	private static final int FILE_PARAM_LENGTH = 2;

	/** APIのURL */
	private String url;

	/** パラメータ */
	private Map<String, String> params;

	/** パラメータ(キーが複数のモノ) */
	private Map<String, Collection<String>> multipleParams;

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
		multipleParams = new HashMap<String, Collection<String>>();
		fileParam = new String[FILE_PARAM_LENGTH];
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T get() {
		HttpGet method = null;
		method = new HttpGet(createUrl());
		final DefaultHttpClient client = new DefaultHttpClient();
		method.setHeader("Connection", "Keep-Alive");
		try {
			final HttpResponse response = client.execute(method);
			final int status = response.getStatusLine().getStatusCode();
			if (HttpStatus.SC_OK != status) {
				// TODO ネットワークエラーのExceptionを返す？
			}
			return parse(EntityUtils.toString(response.getEntity()));
		} catch (final ClientProtocolException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.getMessage());
		} catch (final ApiParseException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.getMessage());
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			Log.e(TAG, e.getMessage());
		}
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public T post() {
		final HttpClient httpClient = new DefaultHttpClient();
		final HttpPost httpPost = new HttpPost(url);
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
			for (final Map.Entry<String, Collection<String>> entry : multipleParams.entrySet()) {
				for (final String val : entry.getValue()) {
					multipartEntity.addPart(entry.getKey() + "[]", new StringBody(val,
							UTF_8_CHAR_SET));
				}
			}
			httpPost.setEntity(multipartEntity);
			final HttpResponse response = httpClient.execute(httpPost);
			final int status = response.getStatusLine().getStatusCode();
			if (HttpStatus.SC_OK != status) {
				// TODO ネットワークエラーのExceptionを返す？
			}
			return parse(EntityUtils.toString(response.getEntity()));
		} catch (final ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final ApiParseException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return null;
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
		for (final Map.Entry<String, Collection<String>> entry : multipleParams.entrySet()) {
			for (final String val : entry.getValue()) {
				sb.append(entry.getKey());
				sb.append("[]");
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
	public void setMultipleParams(final String key, final Collection<String> vals) {
		multipleParams.put(key, vals);
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
		multipleParams.clear();
		fileParam = new String[FILE_PARAM_LENGTH];
	}
}
