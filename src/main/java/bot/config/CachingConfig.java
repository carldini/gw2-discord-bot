package bot.config;

import java.lang.invoke.MethodHandles;

import org.ehcache.event.CacheEvent;
import org.ehcache.event.CacheEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CachingConfig {

  public class CacheLogger implements CacheEventListener<Object, Object> {
    private final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
  
    @Override
    public void onEvent(CacheEvent<?, ?> cacheEvent) {
      LOGGER.info("Key: {} | EventType: {} | Old value: {} | New value: {}",
               cacheEvent.getKey(), cacheEvent.getType(), cacheEvent.getOldValue(), 
               cacheEvent.getNewValue());
    }
  }
}