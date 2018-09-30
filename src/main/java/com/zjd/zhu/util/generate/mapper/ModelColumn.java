package com.zjd.zhu.util.generate.mapper;

import java.io.Serializable;

public class ModelColumn implements Serializable {

    private static final long serialVersionUID = 930237585911485783L;

    /**
     * 数据库中对应的字段名
     */
    private String name;

    private Object value;
    
    /**
     * JAVA类中的属性名
     */
    private String field;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

}
