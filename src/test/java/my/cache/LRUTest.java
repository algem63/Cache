package my.cache;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class LRUTest {

    private static LRU<String> cache;

    @BeforeAll
    static void init() {
        cache = new LRU<>(3);
    }

    @Test
    @Order(1)
    void insertItemsTest() {
        // Добавляем в кеш три элемента, последним будет эелемент "С".
        cache.put("A");
        cache.put("B");
        cache.put("C");
        assertTrue(cache.contains("A"));
        assertTrue(cache.contains("B"));
        assertTrue(cache.contains("C"));
    }

    @Test
    @Order(2)
    void removeLRUItemTest() {
        // При добавлении элемента "D" будет вытеснен самый старый элемент - "А".
        cache.put("D");
        assertTrue(cache.contains("B"));
        assertTrue(cache.contains("C"));
        assertTrue(cache.contains("D"));
        assertFalse(cache.contains("A"));
    }

    @Test
    @Order(3)
    void getItemTest() {
        // Берем самый редко используемый элемент, это "B". Он становится наиболее часто используемым.
        cache.get("B");
        // Теперь самым неиспользуемым становится элемент "С", именно он будет вытеснен при добавлении
        // нового элемента.
        cache.put("E");
        assertTrue(cache.contains("D"));
        assertTrue(cache.contains("B"));
        assertTrue(cache.contains("E"));
        assertFalse(cache.contains("C"));
    }

    @Test
    @Order(4)
    void removeLRUItemAgainTest() {
        // Сейчас кеш содержит элементы D, B и Е, именно в такой последовательности использования.
        // Если запросить элементы D и В, то последним окажется элемент Е. Именно он и будет удален
        // при добавлении нового элемента.
        cache.get("D");
        cache.get("B");
        cache.put("F");
        assertTrue(cache.contains("D"));
        assertTrue(cache.contains("B"));
        assertTrue(cache.contains("F"));
        assertFalse(cache.contains("E"));

    }

    @Test
    @Order(5)
    void cacheClearTest() {
        cache.clear();
        assertFalse(cache.contains("A"));
        assertFalse(cache.contains("B"));
        assertFalse(cache.contains("C"));
        assertFalse(cache.contains("D"));
        assertFalse(cache.contains("E"));
        assertFalse(cache.contains("F"));
    }
}
