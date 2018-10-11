package com.ddl.basic.manager.batch;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;
import java.util.concurrent.RecursiveTask;

import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ddl.basic.dao.AbstractListDao;

public abstract class AbstractManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractManager.class);

    private static final int BATCH_SIZE = 500;
    private static final int FORK_SIZE = 400;

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    private ForkJoinPool forkjoinPool = new ForkJoinPool();


    /**
     * fork/join 提交方式
     * record 字段较多，单条sql长度超过2500个字符，字段超过60个 使用该方法
     *
     * @param tClass     对象
     * @param recordList 记录集合
     * @return
     */
    @SuppressWarnings("rawtypes")
	protected int addDataParamFork(Class tClass, List recordList) {
        if (recordList == null || recordList.isEmpty()) {
            return 0;
        }
        int result = 1;
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false)) {
            Future<Integer> future = forkjoinPool.submit(new BatchInsert(sqlSession, 0, recordList.size(), tClass, recordList));
            result = future.get();
            sqlSession.commit();
        } catch (Exception e) {
            LOGGER.error("插入异常", e);
            throw new RuntimeException("sql 执行异常", e);
        }
        return result;
    }

    /**
     * 单线程，分批提交方式
     *
     * @param tClass
     * @param recordList
     * @return
     */
    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    protected int addDataParam(Class tClass, List recordList) throws Exception {
        if (recordList == null || recordList.isEmpty()) {
            return 0;
        }
        int result = 0;
        try (SqlSession sqlSession = sqlSessionFactory.openSession(ExecutorType.BATCH, false)) {

           /* int batchLastIndex = BATCH_SIZE;
            AbstractListDao abstractListDao = (AbstractListDao) sqlSession.getMapper(tClass);
            for (int index = 0; index < recordList.size(); ) {
                if (batchLastIndex >= recordList.size()) {
                    batchLastIndex = recordList.size();
                    abstractListDao.insertList(recordList.subList(index, batchLastIndex));
                    result = result + batchLastIndex;
                    sqlSession.commit();
                    sqlSession.clearCache();
                    break;
                } else {
                    abstractListDao.insertList(recordList.subList(index, batchLastIndex));
                    result = result + batchLastIndex;
                    sqlSession.commit();
                    sqlSession.clearCache();
                    index = batchLastIndex;
                    batchLastIndex = index + BATCH_SIZE;
                }
            }
            sqlSession.commit();*/
        	int total = recordList.size();
        	int div = Math.floorDiv(total, BATCH_SIZE);
    		int mod = Math.floorMod(total, BATCH_SIZE);
            int loopCount = div == 0 ? 1 : mod == 0 ? div : div + 1;
            AbstractListDao abstractListDao = (AbstractListDao) sqlSession.getMapper(tClass);
            for (int i = 0; i < loopCount; i++) {
            	int fromIndex = i * BATCH_SIZE, toIndex = (i + 1) * BATCH_SIZE; 
            	if (i == loopCount - 1) {
            		toIndex = total;
            	}
            	abstractListDao.insertList(new ArrayList(recordList.subList(fromIndex, toIndex)));
            	sqlSession.commit();
            	sqlSession.clearCache();
            	result += toIndex - fromIndex;
            }
        } catch (Exception e) {
        	e.printStackTrace();
            throw new Exception("sql 执行异常", e);
        }
        return result;
    }
    
    @SuppressWarnings("serial")
	class BatchInsert extends RecursiveTask<Integer> {
        private SqlSession sqlSession;
        private int start;
        private int end;
        @SuppressWarnings("rawtypes")
		private Class daoClass;
        @SuppressWarnings("rawtypes")
		private List recordList;

        @SuppressWarnings("rawtypes")
		public BatchInsert(SqlSession sqlSession, int start, int end,  Class daoClass, List recordList) {
            this.sqlSession = sqlSession;
            this.start = start;
            this.end = end;
            this.daoClass = daoClass;
            this.recordList = recordList;
        }

        @Override
        protected Integer compute() {
            Integer size = 0;
            if (end - start <= FORK_SIZE) {
                try {
                    size = size + doInsert();
                } catch (Exception e) {
                    throw new RuntimeException("sql 执行异常", e);
                }
            } else {
                int middle = (end + start) / 2;
                BatchInsert t1 = new BatchInsert(sqlSession, start, middle + 1, daoClass, recordList);
                BatchInsert t2 = new BatchInsert(sqlSession, middle + 1, end, daoClass, recordList);
                t1.fork();
                t2.fork();
                Integer t1Result = t1.join();
                Integer t2Result = t2.join();
                size = size + t1Result + t2Result;
            }
            return size;
        }

        private int doInsert() throws Exception {
            return addDataParam(daoClass, recordList.subList(start, end));
        }
    }
}
