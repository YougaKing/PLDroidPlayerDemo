package cdn.youga.instrument;

/**
 * author: YougaKingWu@gmail.com
 * created on: 2018/05/21 13:53
 * description:
 */
public class Wrapper<T> {
    public int code;
    public String message;
    public T t;

    public Wrapper(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public Wrapper(int code, T t) {
        this.code = code;
        this.t = t;
    }
}
