package net.ouranos.connector.digiline.common.exception;

/**
 * エラーレスポンスクラス
 * @param message 例外メッセージ
 */
public record ErrorResponse(String code, String message, String detail) {}
