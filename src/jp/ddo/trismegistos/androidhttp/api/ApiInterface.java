
package jp.ddo.trismegistos.androidhttp.api;

/**
 * API接続のためのinterface。
 * 
 * @author y_sugasawa
 * @since 2013/01/27
 */
public interface ApiInterface<T> {

    public T post();

    public T get() throws Exception;
}
