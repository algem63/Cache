package my.cache;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class LFUTest {

    private static LFU<String> cache;

    @BeforeAll
    static void init() {
        cache = new LFU<>(3);
    }

    @Test
    @Order(1)
    void insertItemsTest() {
        // Добавляем в кеш три элемента, частота использования каждого равна единице.
        cache.put("A");
        cache.put("B");
        cache.put("C");
        assertTrue(cache.contains("A"));
        assertTrue(cache.contains("B"));
        assertTrue(cache.contains("C"));
    }

    @Test
    @Order(2)
    void removeLastItem() {
        // Т.к. сейчас все элементы кеша имеют одинаковую частоту использования, то при добавлении элемента "D" будет
        // вытеснен последний - элемент "С".
        cache.put("D");
        assertTrue(cache.contains("A"));
        assertTrue(cache.contains("B"));
        assertTrue(cache.contains("D"));
        assertFalse(cache.contains("C"));
    }

    @Test
    @Order(3)
    void removeLFUItem() {
        // Сейчас в кеше находятся элементы "A", "B", "D" с частотой использования, равной одному. Повышаем частоту
        // использования элементов "B" и "D" до двух:
        cache.get("B");
        cache.get("D");
        // Добавляемый элемент "C" вытеснит элемент "A", добавленный первым
        cache.put("C");
        assertTrue(cache.contains("C"));
        assertTrue(cache.contains("B"));
        assertTrue(cache.contains("D"));
        assertFalse(cache.contains("A"));
    }

    @Test
    @Order(4)
    void removeLFUItemAgainTest() {
        // В кеше элементы D и В с частотой использования два и элемент С с частотой использования
        // один. Поднимем частоту использования В и С, чтобы при добавлении следующего элемента вытеснить D
        cache.get("B");
        cache.get("C");
        cache.get("C");
        cache.put("E");
        assertTrue(cache.contains("B"));
        assertTrue(cache.contains("C"));
        assertTrue(cache.contains("E"));
    }

    @Test
    @Order(5)
    void cleanCache() {
        cache.clear();
        assertFalse(cache.contains("A"));
        assertFalse(cache.contains("B"));
        assertFalse(cache.contains("C"));
        assertFalse(cache.contains("D"));
        assertFalse(cache.contains("Е"));
    }
}