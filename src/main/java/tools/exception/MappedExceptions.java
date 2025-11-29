package tools.exception;

public class MappedExceptions {

	public static class PagamentoNaoEncontradoException extends RuntimeException {

		public PagamentoNaoEncontradoException(String message) {
			super(message);
		}

	}

	public static class PagamentoJaCanceladoException extends RuntimeException {

		public PagamentoJaCanceladoException(String message) {
			super(message);
		}
	}

	public static class IdempotencyConflictException extends RuntimeException {
		private final String responsePayload;

		public IdempotencyConflictException(String message, String responsePayload) {
			super(message);
			this.responsePayload = responsePayload;
		}

		public String getResponsePayload() {
			return responsePayload;
		}
	}
}
