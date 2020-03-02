package red.zhiyuan.mahjongzj.bean;

public class JsonReturn<T> {
    private int code;
    private String message;
    private T data;
    private String type;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T>JsonReturn<T> success(String type, T data) {
        JsonReturn<T> result = new JsonReturn<T>();
        result.setCode(200);
        result.setMessage("success");
        result.setData(data);
        result.type = type;
        return result;
    }

    public static <T>JsonReturn<T> error(String type, String message) {
        JsonReturn<T> result = new JsonReturn<T>();
        result.setCode(500);
        result.setMessage(message);
        result.type = type;
        return result;
    }
}