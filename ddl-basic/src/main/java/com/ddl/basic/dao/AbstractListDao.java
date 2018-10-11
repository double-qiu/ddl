package com.ddl.basic.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

/**
 * ClassName: AbstractClzssDao  
 * 抽象dao操作
 * @author DOUBLE
 * @version @param <T>
 */
@Mapper
public interface AbstractListDao<T> {

    /**
     * 批量添加维度数据
     *
     * @param recordList
     * @return
     */
    int insertList(List<T> recordList);
}
