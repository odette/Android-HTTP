
package jp.ddo.trismegistos.androidhttp.item;

import jp.ddo.trismegistos.androidhttp.exception.ApiAccessException;
import jp.ddo.trismegistos.androidhttp.exception.ApiParseException;

/**
 * 実行結果を格納するためのObject。
 * 
 * @author y_sugasawa
 * @since 2013/03/22
 */
public class ResultItem<T> {

    /** 結果。 */
    public T result;

    /** API通信例外。 */
    public ApiAccessException accessEx;

    /** parse例外。 */
    public ApiParseException parseEx;
}
