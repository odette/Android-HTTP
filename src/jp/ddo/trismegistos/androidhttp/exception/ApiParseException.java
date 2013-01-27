package jp.ddo.trismegistos.androidhttp.exception;

/**
 * APIの結果をパースした際の例外クラス。
 * 
 * @author y_sugasawa
 * @since 2013/01/27
 */
public class ApiParseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ApiParseException() {
		super();
	}

	/**
	 * @param detailMessage
	 * @param throwable
	 */
	public ApiParseException(final String detailMessage, final Throwable throwable) {
		super(detailMessage, throwable);
	}

	/**
	 * @param detailMessage
	 */
	public ApiParseException(final String detailMessage) {
		super(detailMessage);
	}

	/**
	 * @param throwable
	 */
	public ApiParseException(final Throwable throwable) {
		super(throwable);
	}

}
