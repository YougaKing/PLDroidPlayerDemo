package cdn.youga.instrument;

/**
 * author: YougaKingWu@gmail.com
 * created on: 2018/05/21 13:57
 * description:
 */
public interface Action<T> {

    void  call(Wrapper<T> wrapper);
}
