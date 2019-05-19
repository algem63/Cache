package my.cache;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LTUCache<T> {

    private class Node<T> {
        private long lastUseTime;
        private Node<T> prev;
        private Node<T> next;
        private T element;

        public Node(T element) {
            this.element = element;
        }

        public long getLastUseTime() {
            return lastUseTime;
        }

        public void setLastUseTime(long lastUseTime) {
            this.lastUseTime = lastUseTime;
        }

        public Node<T> getPrev() {
            return prev;
        }

        public void setPrev(Node<T> prev) {
            this.prev = prev;
        }

        public Node<T> getNext() {
            return next;
        }

        public void setNext(Node<T> next) {
            this.next = next;
        }

        public T getElement() {
            return element;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node<?> node = (Node<?>) o;
            return element.equals(node.element);
        }

        @Override
        public int hashCode() {
            return Objects.hash(element);
        }
    }

    private Node<T> ltuElement, mtuElement;
    private List<Node<T>> cachedData;
    int cacheSize;

    public LTUCache(int size) {
        this.cacheSize = size;
        cachedData = new ArrayList<>(size);
    }

    public void putToCache(T element) {
        Node<T> node = new Node<>(element);
        node.setLastUseTime(System.currentTimeMillis());
        if (cachedData.size() == cacheSize) {
            // Удаляем наименее используемый элемент
            cachedData.remove(ltuElement);
            Node<T> next = ltuElement.getNext();
            next.setPrev(null);
            ltuElement = next;
        }
        if (cachedData.size() == 0) {
            mtuElement = node;
        } else {
            node.setPrev(mtuElement);
            mtuElement.setNext(node);
            if (ltuElement == null) {
                ltuElement = mtuElement;
            }
            mtuElement = node;
        }
        cachedData.add(node);
    }

    public T getFromCache(T element) {
        Node<T> node = new Node<>(element);
        if (!cachedData.contains(node)) {
            return null;
        }
        Node<T> nodeFromCache = cachedData.get(cachedData.indexOf(node));
        if (nodeFromCache.equals(mtuElement)) {
            // Обновляем первый используемый элемент
            this.mtuElement.setLastUseTime(System.currentTimeMillis());
        } else if (nodeFromCache.equals(ltuElement)) {
            // Обновляем последний используемый элемент
            Node<T> next = ltuElement.getNext();
            next.setPrev(null);
            ltuElement.setPrev(this.mtuElement);
            ltuElement.setNext(null);
            ltuElement.setLastUseTime(System.currentTimeMillis());
            mtuElement.setNext(ltuElement);
            mtuElement = ltuElement;
            ltuElement = next;
        } else {
            // Обновляем элемент из середины кеша
            Node<T> prev = nodeFromCache.getPrev();
            Node<T> next = nodeFromCache.getNext();
            prev.setNext(next);
            next.setPrev(prev);
            nodeFromCache.setPrev(this.mtuElement);
            nodeFromCache.setNext(null);
            nodeFromCache.setLastUseTime(System.currentTimeMillis());
            mtuElement.setNext(nodeFromCache);
            this.mtuElement = nodeFromCache;
        }
        return nodeFromCache.getElement();
    }

    public void cleanCache() {
        cachedData.clear();
        ltuElement = null;
        mtuElement = null;
    }
}
