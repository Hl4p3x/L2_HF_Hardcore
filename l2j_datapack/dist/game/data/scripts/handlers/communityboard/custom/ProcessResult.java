package handlers.communityboard.custom;

import com.l2jserver.util.StringUtil;

public class ProcessResult {

    private final boolean success;
    private final String comment;

    private ProcessResult() {
        success = true;
        comment = StringUtil.EMPTY;
    }

    private ProcessResult(String comment) {
        this.success = false;
        this.comment = comment;
    }

    public boolean isSuccess() {
        return success;
    }

    public boolean isFailure() {
        return !success;
    }

    public String getComment() {
        return comment;
    }

    public static ProcessResult failure(String comment) {
        return new ProcessResult(comment);
    }

    public static ProcessResult success() {
        return new ProcessResult();
    }

    @Override
    public String toString() {
        return "ProcessResult{" +
                "success=" + success +
                ", comment='" + comment + '\'' +
                '}';
    }

}
