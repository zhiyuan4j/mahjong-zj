package red.zhiyuan.mahjongzj.struct;

/**
 * @author zhiyuan.wang
 * @date 2020/2/1
 */
public class Node<T> {
    public T data;
    public Node<T> next;
    public Node(T data, Node<T> next){
        this.data=data;
        this.next=next;
    }
}
