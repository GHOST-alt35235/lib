
package cn.edu.wynu.common;

import java.util.HashMap;

public class AjaxResult extends HashMap<String, Object> {
    public static final String CODE_TAG = "code";
    public static final String MSG_TAG = "msg";
    public static final String DATA_TAG = "data";

    public AjaxResult() {
    }

    public AjaxResult(int code, String msg, Object data) {
        super.put(CODE_TAG, code);
        super.put(MSG_TAG, msg);
        if (data != null) {
            super.put(DATA_TAG, data);
        }
    }

    public AjaxResult(int code, String msg) {
        this(code, msg, null);
    }

    public static AjaxResult success() {
        return new AjaxResult(HttpStatus.SUCCESS, "操作成功");
    }

    public static AjaxResult success(String msg) {
        return new AjaxResult(HttpStatus.SUCCESS, msg);
    }

    public static AjaxResult success(String msg, Object data) {
        return new AjaxResult(HttpStatus.SUCCESS, msg, data);
    }

    public static AjaxResult error() {
        return new AjaxResult(HttpStatus.ERROR, "操作失败");
    }

    public static AjaxResult error(String msg) {
        return new AjaxResult(HttpStatus.ERROR, msg);
    }

    public static AjaxResult error(String msg, Object data) {
        return new AjaxResult(HttpStatus.ERROR, msg, data);
    }

    @Override
    public AjaxResult put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
