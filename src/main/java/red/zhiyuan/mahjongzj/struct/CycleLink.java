package red.zhiyuan.mahjongzj.struct;

import com.google.common.collect.Lists;

import java.util.List;

public class CycleLink<T> {

    private Node<T> header;  //链表头结点
    private Node<T> tail;    //链表尾结点
    private int size;     //保存已经有的结点

    public CycleLink() {
    }

    /**
     * 在尾部添加
     *
     * @param element
     * @return
     */
    public boolean add(T element) {
        linkLast(element);
        return true;
    }

    /**
     * 获取指定索引处的元素
     *
     * @param index
     * @return
     */
    public T getElement(int index) {
        return (T) getNodeByIndex(index).data;
    }

    /**
     * 获取指定位置的结点
     * @param index
     * @return
     */
    public Node<T> getNodeByIndex(int index){
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("获取位置超过了链表长度范围");
        Node currentNode = header;
        for (int i = 0; i < index; i++) {
            currentNode = currentNode.next;
        }
        return currentNode;
    }

    /**
     * 获取指定位置前驱的结点
     * @param index
     * @return
     */
    public Node getNodeByIndexBefore(int index){
        Node preNode = header;
        for (int i = 0; i < index - 1; i++) {
            preNode = preNode.next;      //获得前驱结点
        }
        return preNode;
    }

    /**
     * 获取指定元素的前驱
     *
     * @param currentElem
     * @return
     */
    public T priorElement(T currentElem) {
        int index = getIndex(currentElem);
        if (index == -1)
            return null;
        else {
            if (index == 0) {
                return null;
            } else {
                return (T) getNodeByIndex(index - 1).data;
            }
        }
    }

    /**
     * 获取指定元素的后驱
     *
     * @param currentElem
     * @return
     */
    public T nextElement(T currentElem) {
        int index = getIndex(currentElem);
        if (index == -1)
            return null;
        else {
            if (index == size - 1) {
                return header.data;
            } else {
                return (T) getNodeByIndex(index + 1).data;
            }
        }
    }

    public int getIndex(T element) {
        Node current = header;
        for (int i = 0; i < size && current != null; i++, current = current.next) {
            if (current.data.equals(element))
                return i;
        }
        return -1;
    }

    /**
     * 在头部插入
     *
     * @param element
     * @return
     */
    public boolean addFirst(T element) {
        linkFirst(element);
        return true;
    }

    /**
     * 在指定位置插入元素
     *
     * @param index
     * @param element
     * @return
     */
    public boolean insert(int index, T element) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("插入位置超出链表范围");
        if (index == 0)
            linkFirst(element);
        else {
            Node preNode = getNodeByIndexBefore(index);
            Node newNode = new Node(element, null);
            newNode.next = preNode.next;
            preNode.next = newNode;
            size++;
        }
        return true;
    }

    /**
     * 删除元素
     *
     * @param index
     * @return
     */
    public T delete(int index) {
        if (index < 0 || index > size)
            throw new IndexOutOfBoundsException("删除位置超出链表范围");

        Node currentNode = header;
        if (index == 0) {
            header = header.next;
            currentNode.next = null;
            tail.next = header;
        } else {
            Node currentNodeBefore = null;
            for (int i = 0; i < index; i++) {
                currentNodeBefore = currentNode;//前置结点
                currentNode = currentNode.next;  //要删除的当前结点
            }
            //删除的是尾结点
            if(index == size - 1){
                tail = currentNodeBefore;  //尾结点变为删除结点的前置结点
                tail.next = header;
            }else {
                currentNodeBefore.next = currentNode.next;
            }
            currentNode.next = null;
        }
        size--;
        return (T) currentNode.data;
    }

    //删除最后一个元素
    public T remove(){
        return delete(size-1);
    }

    /**
     * 尾部插入
     *
     * @param e
     */
    private void linkLast(T e) {
        final Node<T> l = tail;
        final Node<T> newNode = new Node<>(e, null);
        if (l == null) {
            header = newNode;
            tail = header;
        } else {
            tail.next = newNode;   //尾结点指向新结点
            newNode.next = header; //新结点指向头结点
            tail = newNode;        //新结点作为尾结点
        }
        size++;
    }

    private void linkFirst(T e) {
        final Node<T> l = header;
        Node<T> newNode = new Node<>(e, null);
        if (l == null) {
            header = newNode;
            tail = header;
        } else {
            newNode.next = header; //新结点指向header
            header = newNode;      //新结点称为头结点
            tail.next = header;   //尾结点指向头结点
        }
        size++;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    //清空线性表
    public void clear(){
        //将头结点和尾结点设为空
        header = null;
        tail = null;
        size = 0;
    }

    public List<Node> toList() {
        List<Node> nodes = Lists.newArrayList();
        Node current = header;
        for (int i = 0; i < size && current != null; i++, current = current.next) {
            nodes.add(current);
        }
        return nodes;
    }

    @Override
    public String toString() {
        if (isEmpty())
            return "[]";
        else {
            StringBuilder sb = new StringBuilder("[");
            sb.append(header.data + "->").toString();
            if (header.next != null) {
                for (Node current = header.next; current != header; current = current.next)
                    sb.append(current.data + "->").toString();
            }
            int len = sb.length();
            return sb.delete(len - 2, len).append("]").toString();
        }
    }
}