package my.cache;

import java.util.*;

public final class LFU<T> {

    private class Node {

        /**
         * Непосредственно элемент, хранящийся в кеше.
         */
        private T element;

        /**
         * Частота использования хранимого элемента. Нужен для получения соотв. записи в dataMap.
         */
        private int freq;

        public Node(T element) {
            this.element = element;
        }

        public T getElement() {
            return element;
        }

        public void setElement(T element) {
            this.element = element;
        }

        public int getFreq() {
            return freq;
        }

        public void setFreq(int freq) {
            this.freq = freq;
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

    /**
     * Размер кеша.
     */
    private int size;

    /**
     * Содержит все эелементы кэша вместе. Обеспечивает время доступа О(1).
     */
    private List<Node> cachedData = new ArrayList<>(size);

    /**
     * Ключом является частота использования, значением - список элементов с соответствующей частотой использования.
     */
    private Map<Integer, List<Node>> dataMap = new HashMap<>();

    /**
     * Значение наименьшей частоты использования элемента. Нужно для удаления наименее используемого элемента.
     */
    private int lowestFreq;

    public LFU(int size) {
        this.size = size;
    }

    public T get(T element) {
        Node nodeToFind = new Node(element);
        if (!cachedData.contains(nodeToFind)) {
            return null;
        }
        Node node = cachedData.get(cachedData.indexOf(nodeToFind));
        int freqOld = node.getFreq();
        int freqNew = freqOld + 1;
        // Получаем список элементов с одинаковой частотой использования и удаляем из него запрошенный элемент
        List<Node> nodes = dataMap.get(freqOld);
        nodes.remove(node);
        if (nodes.size() == 0) {
            // Если нет других элементов с данной частотой использования, то удаляем соответствующую запись из мапы.
            dataMap.remove(freqOld);
            // Если перенесенный вверх элемент мапы был наименее используемым, то повышаем его используемость
            if (node.getFreq() == lowestFreq) {
                lowestFreq = freqNew;
            }
        }
        // Повышаем использование получаемого элемента
        node.setFreq(freqNew);
        if (dataMap.get(freqNew) == null) {
            List<Node> nodes2 = new ArrayList<>();
            nodes2.add(node);
            dataMap.put(freqNew, nodes2);
        } else {
            dataMap.get(freqNew).add(node);
        }
        return node.getElement();
    }

    public void put(T element) {
        Node node = new Node(element);
        if (cachedData.size() == size) {
            // Удаляем наименее часто используемый элемент из кеша
            List<Node> nodes = dataMap.get(lowestFreq);
            Node nodeToRemove = nodes.get(nodes.size() - 1);
            nodes.remove(nodeToRemove);
            cachedData.remove(nodeToRemove);
            if (nodes.size() == 0) {
                // Если удаленный элемент был единственным с данной частотой использования, то удаляем соотв. запись в мапе
                dataMap.remove(lowestFreq);
            }
        }
        if (!cachedData.contains(node)) {
//            Добавляем новый элемент
            node.setFreq(1);
            if (dataMap.get(1) == null) {
                List<Node> nodes = new ArrayList<>();
                nodes.add(node);
                dataMap.put(1, nodes);
                lowestFreq = 1;
            } else {
                dataMap.get(1).add(node);
            }
            cachedData.add(node);
        }
    }

    public void clear() {
        cachedData.clear();
        dataMap.clear();
        lowestFreq = 0;
    }

    public boolean contains(T element) {
        return cachedData.contains(new Node(element));
    }
}
