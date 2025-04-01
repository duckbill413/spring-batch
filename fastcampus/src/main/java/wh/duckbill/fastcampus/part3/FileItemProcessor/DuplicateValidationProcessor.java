package wh.duckbill.fastcampus.part3.FileItemProcessor;


import org.springframework.batch.item.ItemProcessor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * author        : duckbill413
 * date          : 2023-02-01
 * description   :
 **/

public class DuplicateValidationProcessor<T> implements ItemProcessor<T, T> {
    private final Map<String, Object> keyPool = new ConcurrentHashMap<>(); // Multi-thread 환경 지원
    private final Function<T, String> keyExtractor;
    private final boolean allowDuplicate;

    public DuplicateValidationProcessor(Function<T, String> keyExtractor, boolean allowDuplicate) {
        this.keyExtractor = keyExtractor;
        this.allowDuplicate = allowDuplicate;
    }

    @Override
    public T process(T item) throws Exception {
        if (allowDuplicate)
            return item;

        String key = keyExtractor.apply(item);
        if (keyPool.containsKey(key))
            return null;

        keyPool.put(key, key);
        return item ;
    }
}
