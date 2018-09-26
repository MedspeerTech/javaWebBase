//package medspeer.tech.config;
//
//
//
////    import net.sf.ehcache.config.CacheConfiguration;
//    import org.springframework.cache.CacheManager;
//    import org.springframework.cache.annotation.CachingConfigurer;
//    import org.springframework.cache.annotation.EnableCaching;
//    import org.springframework.cache.ehcache.EhCacheCacheManager;
//    import org.springframework.cache.ehcache.EhCacheManagerFactoryBean;
//    import org.springframework.cache.interceptor.*;
//    import org.springframework.context.annotation.Bean;
//    import org.springframework.context.annotation.ComponentScan;
//    import org.springframework.context.annotation.Configuration;
//    import org.springframework.core.io.ClassPathResource;
//
//    @EnableCaching
//    @Configuration
////    @ComponentScan(basePackages = "medspeer.tech")
//    public class CacheConfig
////            implements CachingConfigurer
//    {
//
////        @Bean(destroyMethod="shutdown")
////        public net.sf.ehcache.CacheManager ehCacheManager() {
////            CacheConfiguration cacheConfiguration = new CacheConfiguration();
////            cacheConfiguration.setName("testCache");
////            cacheConfiguration.setMemoryStoreEvictionPolicy("LRU");
////            cacheConfiguration.setMaxEntriesLocalHeap(1000);
////
////            net.sf.ehcache.config.Configuration config = new net.sf.ehcache.config.Configuration();
////            config.addCache(cacheConfiguration);
////
////            return net.sf.ehcache.CacheManager.newInstance(config);
////        }
////
////        @Bean
////        @Override
////        public CacheManager cacheManager() {
////            return new EhCacheCacheManager(ehCacheManager());
////        }
////
////        @Bean
////        @Override
////        public CacheResolver cacheResolver() {
////            return new SimpleCacheResolver();
////        }
////
////        @Bean
////        @Override
////        public KeyGenerator keyGenerator() {
////            return new SimpleKeyGenerator();
////        }
////
////        @Bean
////        @Override
////        public CacheErrorHandler errorHandler() {
////            return new SimpleCacheErrorHandler();
////        }
//
//        @Bean
//        public EhCacheManagerFactoryBean ehCacheCacheManager() {
//            EhCacheManagerFactoryBean factory = new EhCacheManagerFactoryBean();
//            factory.setConfigLocation(new ClassPathResource("ehcache.xml"));
//            factory.setShared(true);
//            return factory;
//        }
//
//    }
//
