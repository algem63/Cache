package my.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LRU<T> {

    private class Node {

        /**
         * Предыдущий элемент в цепочке, более ранний по времени добавления.
         */
        private Node prev;

        /**
         * Последующий элемент в цепочке, более поздний по времени добавления.
         */
        private Node next;

        /**
         * Непосредственно элемент, хранящийся в кеше.
         */
        private T element;

        Node(T element) {
            this.element = element;
        }

        Node getPrev() {
            return prev;
        }

        void setPrev(Node prev) {
            this.prev = prev;
        }

        Node getNext() {
            return next;
        }

        void setNext(Node next) {
            this.next = next;
        }

        T getElement() {
            return element;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return element.equals(node.element);
        }

        @Override
        public int hashCode() {
            return Objects.hash(element);
        }
    }

    private Node lruElement, mruElement;

    private int size;
    private List<Node> cachedData = new ArrayList<>(size);

    public LRU(int size) {
        this.size = size;
    }

    public T get(T element) {
        Node node = new Node(element);
        if (!cachedData.contains(node)) {
            return null;
        }
        Node nodeFromCache = cachedData.get(cachedData.indexOf(node));
        // Если запрошенный элемент является последним в цепочке, т.е. последним по времени, то не трогаем его,
        // т.к. он не нуждается в обновлении.
        if (!nodeFromCache.equals(mruElement)) {
            if (nodeFromCache.equals(lruElement)) {
                // Обновляем последний используемый элемент
                Node next = lruElement.getNext();
                next.setPrev(null);
                lruElement.setPrev(this.mruElement);
                lruElement.setNext(null);
                mruElement.setNext(lruElement);
                mruElement = lruElement;
                lruElement = next;
            } else {
                // Обновляем элемент из середины кеша
                Node prev = nodeFromCache.getPrev();
                Node next = nodeFromCache.getNext();
                prev.setNext(next);
                next.setPrev(prev);
                nodeFromCache.setPrev(this.mruElement);
                nodeFromCache.setNext(null);
                mruElement.setNext(nodeFromCache);
                this.mruElement = nodeFromCache;
            }
        }
        return nodeFromCache.getElement();
    }

    public void put(T element) {
        Node node = new Node(element);
        if (cachedData.size() == size) {
            // Удаляем наименее используемый элемент
            cachedData.remove(lruElement);
            Node next = lruElement.getNext();
            next.setPrev(null);
            lruElement = next;
        }
        if (!cachedData.contains(node)) {
            if (cachedData.size() == 0) {
                mruElement = node;
            } else {
                node.setPrev(mruElement);
                mruElement.setNext(node);
                if (lruElement == null) {
                    lruElement = mruElement;
                }
                mruElement = node;
            }
            cachedData.add(node);
        }
    }

    public void clear() {
        cachedData.clear();
        lruElement = null;
        mruElement = null;
    }

    public boolean contains(T element) {
        return cachedData.contains(new Node(element));
    }
}