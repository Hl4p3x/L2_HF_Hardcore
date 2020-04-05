package handlers.communityboard.custom;

import com.l2jserver.common.util.StringUtil;

public class ProcessResult {

    private final boolean success;
    private final String result;

    private ProcessResult(Boolean success, String result) {
        this.success = success;
        this.result = result;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }

    public String getResult() {
        return result;
    }

    public static ProcessResult failure(String result) {
        return new ProcessResult(false, result);
    }

    public static ProcessResult success() {
        return new ProcessResult(true, StringUtil.EMPTY);
    }

    public static ProcessResult success(String text) {
        return new ProcessResult(true, text);
    }

    @Override
    public String toString() {
        return "ProcessResult{" +
                "success=" + success +
                ", result='" + result + '\'' +
                '}';
    }

}
