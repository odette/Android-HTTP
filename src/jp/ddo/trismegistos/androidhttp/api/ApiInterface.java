
package jp.ddo.trismegistos.androidhttp.api;

import jp.ddo.trismegistos.androidhttp.exception.ApiAccessException;
import jp.ddo.trismegistos.androidhttp.exception.ApiParseException;

/**
 * API接続のためのinterface。
 * 
 * @author y_sugasawa
 * @since 2013/01/27
 */
public interface ApiInterface<T> {

    /**
     * POST通信を行う。
     * 
     * @return APIの結果
     * @throws ApiAccessException API通信時の例外発生時
     * @throws ApiParseException 通信結果のparse処理失敗時
     */
    public T post() throws ApiAccessException, ApiParseException;

    /**
     * GET通信を行う。
     * 
     * @return APIの結果
     * @throws ApiAccessException API通信時の例外発生時
     * @throws ApiParseException 通信結果のparse処理失敗時
     */
    public T get() throws ApiAccessException, ApiParseException;
}
