package com.github.mwedgwood;

import com.google.common.base.CaseFormat;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.reflections.ReflectionUtils.*;

public class MetaDataCache {

    Map<Class<?>, MetaData> cache;

    private static class SingletonHolder {
        private static final MetaDataCache INSTANCE = new MetaDataCache();
    }

    private MetaDataCache() {
        cache = new ConcurrentHashMap<>();
    }

    public static MetaDataCache getInstance() {
        return SingletonHolder.INSTANCE;
    }

    public MetaData getMetaDataForClass(Class<?> klass) {
        if (cache.get(klass) == null) {
            cache.put(klass, new MetaData(klass));
        }
        return cache.get(klass);
    }


    public static class MetaData {
        private Class<?> klass;
        private Map<String, Method> columnToSetter;

        MetaData(Class<?> klass) {
            this.klass = klass;
            this.columnToSetter = initColumnToSetterMap();
        }

        public Method setterForColumn(String columnName) {
            return columnToSetter.get(columnName);
        }

        Map<String, Method> initColumnToSetterMap() {
            HashMap<String, Method> result = new HashMap<>();
            for (Method setter : setters()) {
                result.put(CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, setter.getName().replaceFirst("set", "")), setter);
            }
            return result;
        }

        Set<Method> setters() {
            return getAllMethods(klass, withModifier(Modifier.PUBLIC), withPrefix("set"), withParametersCount(1));
        }
    }

}
