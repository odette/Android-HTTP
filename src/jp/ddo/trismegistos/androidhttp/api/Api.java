
package jp.ddo.trismegistos.androidhttp.api;

import jp.ddo.trismegistos.androidhttp.exception.ApiParseException;
import jp.ddo.trismegistos.androidhttp.parser.Parser;

/**
 * API接続を行う。
 * 
 * @author y_sugasawa
 * @param <T>
 * @since 2013/01/27
 */
public class Api<T> extends AbstractApi<T> {

    /** parserクラス。 */
    private Parser<T> parser;

    /**
     * デフォルトコンストラクタ。
     * 
     * @param url
     * @param parser
     */
    public Api(final String url, final Parser<T> parser) {
        super(url);
        this.parser = parser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected T parse(final String data) throws ApiParseException {
        if (parser == null) {
            throw new ApiParseException("parser is null.");
        }
        return parser.parse(data);
    }

}
