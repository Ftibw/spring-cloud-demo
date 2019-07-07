package com.ftibw.demo.auth.config;

import org.bson.BsonUndefined;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.*;
import org.springframework.data.mongodb.core.mapping.MongoMappingContext;

import java.util.ArrayList;
import java.util.List;

/**
 * @author PengZhen 2018-01-30
 * <p>
 * mongo数据库相关配置
 */
@Configuration
public class MongoConfig {

    @Bean
    public MappingMongoConverter mappingMongoConverter(MongoDbFactory factory, MongoMappingContext context,
                                                       BeanFactory beanFactory) {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(factory);
        MappingMongoConverter mappingConverter = new MappingMongoConverter(dbRefResolver, context);
        try {
            mappingConverter.setCustomConversions(beanFactory.getBean(MongoCustomConversions.class));
        } catch (NoSuchBeanDefinitionException ignore) {
        }

        // Don't save _class to mongo
        mappingConverter.setTypeMapper(new DefaultMongoTypeMapper(null));

        return mappingConverter;
    }

    @Bean
    public MongoCustomConversions customConversions() throws Exception {
        List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
        converterList.add(new BsonUndefinedToEmptyStringConverter());
        return new MongoCustomConversions(converterList);
    }

    public static class BsonUndefinedToEmptyStringConverter implements Converter<BsonUndefined, String> {

        /* (non-Javadoc)
         * @see org.springframework.core.convert.converter.Converter#convert(java.lang.Object)
         */
        @Override
        public String convert(BsonUndefined source) {
            return "";
        }
    }
}