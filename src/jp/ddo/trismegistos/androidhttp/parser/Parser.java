
package jp.ddo.trismegistos.androidhttp.parser;

import jp.ddo.trismegistos.androidhttp.exception.ApiParseException;

/**
 * APIの結果のパース処理のinterface。
 * 
 * @author y_sugasawa
 * @since 2013/01/27
 */
public interface Parser<T> {

    /**
     * parse処理。
     * 
     * @param data parse対象の文字列。
     * @return
     * @throws ApiParseException parse失敗時
     */
    public T parse(String data) throws ApiParseException;

}
