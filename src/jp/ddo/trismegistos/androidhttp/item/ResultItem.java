
package jp.ddo.trismegistos.androidhttp.item;

import jp.ddo.trismegistos.androidhttp.exception.ApiAccessException;

/**
 * 実行結果を格納するためのObject。
 * 
 * @author y_sugasawa
 * @since 2013/03/22
 */
public class ResultItem<T> {

    /** 結果。 */
    public T result;

    /** 例外。 */
    public ApiAccessException ex;
}
