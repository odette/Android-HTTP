
package jp.ddo.trismegistos.androidhttp.api;

/**
 * API接続を行い、レスポンスを文字列として返すクラス。
 * 
 * @author y_sugasawa
 * @since 2013/01/27
 */
public class DefaultApi extends AbstractApi<String> {

    /**
     * デフォルトコンストラクタ。
     * 
     * @param url
     */
    public DefaultApi(final String url) {
        super(url);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String parse(final String data) {
        return data;
    }

}
