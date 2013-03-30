
package jp.ddo.trismegistos.androidhttp.exception;

/**
 * API通信時の例外クラス。
 * 
 * @author y_sugasawa
 * @since 2013/02/24
 */
public class ApiAccessException extends IllegalAccessException {

    private static final long serialVersionUID = 1L;

    /** 通信エラーかどうか。 */
    public boolean isDisConnect;
    /** タイムアウトエラーかどうか。 */
    public boolean isTimeout;
    /** ステータスコード。 */
    public int statusCode;

    /**
     * コンストラクタ。
     */
    public ApiAccessException() {
        super();
    }

    /**
     * コンストラクタ。
     * 
     * @param statusCode ステータスコード
     */
    public ApiAccessException(final int statusCode) {
        this.statusCode = statusCode;
    }

    /**
     * コンストラクタ。
     * 
     * @param detailMessage
     */
    public ApiAccessException(final String detailMessage) {
        super(detailMessage);
    }

}
