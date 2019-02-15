package handlers.communityboard.custom;

import com.l2jserver.util.StringUtil;

public class ProcessResultCarrier<T> {

    private T result;
    private String message;
    private boolean success;

    public ProcessResultCarrier(T result, String message, boolean success) {
        this.result = result;
        this.message = message;
        this.success = success;
    }

    public T getResult() {
        return result;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccess() {
        return success;
    }

    public static <T> ProcessResultCarrier<T> failure(String result) {
        return new ProcessResultCarrier<>(null, result, false);
    }

    public static <T> ProcessResultCarrier<T> success(T item) {
        return new ProcessResultCarrier<>(item, StringUtil.EMPTY, true);
    }

    public static <T> ProcessResultCarrier<T> success(T item, String text) {
        return new ProcessResultCarrier<>(item, text, true);
    }

}
