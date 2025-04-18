package wh.duckbill.fastcampus.part3.FileItemReader;


import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

import java.util.List;

/**
 * author        : duckbill413
 * date          : 2023-01-24
 * description   :
 **/

public class CustomItemReader<T> implements ItemReader<T> {
    private final List<T> items;

    public CustomItemReader(List<T> items){
        this.items = items;
    }

    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        if (!items.isEmpty())
            return items.remove(0);
        return null; // null을 리턴하면 junk 종료를 의미
    }
}
