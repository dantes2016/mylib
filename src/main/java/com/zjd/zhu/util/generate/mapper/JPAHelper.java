package com.zjd.zhu.util.generate.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
public class JPAHelper {

    private static final Logger logger = LoggerFactory.getLogger(JPAHelper.class);

    private static final String PRIMARY_KEY = "id";

    private static final String VERSION_KEY = "lockVersion";

    public static <T> String getPrimaryKey(Class<T> type) {
        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(Id.class)) {
                if (field.getType() != Long.class) {
                    logger.error("Only Long type primary key supported.");
                    throw new RuntimeException("can't get primary key.");
                }
                return field.getName();
            }
        }
        if (type.getSuperclass() != null) {
            return getPrimaryKey(type.getSuperclass());
        }
        return PRIMARY_KEY;
    }

    public static <T> String getTableName(Class<T> type) {
        if (!type.isAnnotationPresent(Table.class)) {
            throw new RuntimeException("can't get table name.");
        }
        return type.getAnnotation(Table.class).name();
    }

    public static <T> String getSequenceName(Class<T> type) {
        if (!type.isAnnotationPresent(SequenceGenerator.class)) {
        	return "hibernate_sequence";
            //throw new RuntimeException("can't get sequence name.");
        }
        return type.getAnnotation(SequenceGenerator.class).name();
    }

    public static <T> String getVersionName(Class<T> type) {
        for (Field field : type.getDeclaredFields()) {
            if (field.isAnnotationPresent(Version.class)) {
                if (field.getType() != int.class) {
                    logger.error("Only Long type primary key supported.");
                    throw new RuntimeException("can't get primary key.");
                }
                return field.getName();
            }
        }
        if (type.getSuperclass() != null) {
            return getVersionName(type.getSuperclass());
        }
        return VERSION_KEY;
    }


    
    public static List<Field> getAllFields(Class<?> type){
        List<Field> fieldList = new ArrayList<Field>();       
        while (type.getSuperclass() != null) {
            for (Field field : type.getDeclaredFields()) {
                if("serialVersionUID".equalsIgnoreCase(field.getName())){
                    continue;
                }
                if(field.isAnnotationPresent(Transient.class)){
                    continue;
                }
                
                fieldList.add(field);
            }
            type = type.getSuperclass();
        }        
       
        return fieldList;
    }

    public static <T> boolean hasField(Class<T> type, String fieldName) {
        for (Field field : type.getDeclaredFields()) {
            if (field.getName().equalsIgnoreCase(fieldName)) {
                return true;
            }
        }
        if (type.getSuperclass() != null) {
            return hasField(type.getSuperclass(), fieldName);
        }
        return false;
    }

    

}
