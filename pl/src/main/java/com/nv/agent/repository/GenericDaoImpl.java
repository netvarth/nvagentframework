/**
 * 
 */
package com.nv.agent.repository;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.nv.platform.base.dao.GenericDao;
import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.base.dao.ReadDao;
import com.nv.platform.base.dao.WriteDao;

public class GenericDaoImpl<T> implements GenericDao<T>{
	
	protected WriteDao baseDao ;
	protected ReadDao readDao;
	private static final Log log = LogFactory.getLog (GenericDaoImpl.class);
	private Class<T> type;
	
	/**
	 * Constructor of GenericDaoImpl
	 */
	
	@SuppressWarnings("unchecked")
	public GenericDaoImpl(){
		
		Type t = getClass().getGenericSuperclass();
        ParameterizedType pt = (ParameterizedType) t;
        type = (Class<T>) pt.getActualTypeArguments()[0];
	}
	
	/* (non-Javadoc)
	 * @see com.nv.platform.base.dao.GenericDao#save(java.lang.Object)
	 */
	@Override
	
	public void save(T obj) throws PersistenceException {
		baseDao.save(obj);
		
	}

	/* (non-Javadoc)
	 * @see com.nv.platform.base.dao.GenericDao#update(java.lang.Object)
	 */
	@Override
	
	public void update(T obj) throws PersistenceException {
		baseDao.update(obj);
		
	}

	/* (non-Javadoc)
	 * @see com.nv.platform.base.dao.GenericDao#getById(java.lang.Class, int)
	 */
	@Override

	public T getById(Object id) throws PersistenceException {
		
		return baseDao.getById(type, id);
	}

	@Override
	public void delete(Object id) throws PersistenceException {
		this.baseDao.delete(type, id);
	}

	/* (non-Javadoc)
	 * @see com.nv.platform.base.dao.GenericDao#executeUniqueQuery(java.lang.Class, java.lang.String, java.lang.Object[])
	 */
	@Override
	public T executeUniqueQuery(Class<T> clazz, String query, Object... param) throws PersistenceException {
		
		return readDao.executeUniqueQuery(clazz, query, param);
	}

	/* (non-Javadoc)
	 * @see com.nv.platform.base.dao.GenericDao#executeQuery(java.lang.Class, java.lang.String, java.lang.Object[])
	 */
	@Override
	public List<T> executeQuery(Class<T> clazz, String query, Object... param) throws PersistenceException {
		
		return readDao.executeQuery(clazz, query, param);
	}

	/* (non-Javadoc)
	 * @see com.nv.platform.base.dao.GenericDao#loadAll(java.lang.Class)
	 */
	@Override
	public  List<T> loadAll(Class<T> className) throws PersistenceException {
		 
		return readDao.loadAll(className);
	}

	/**
	 * @param readDao the readDao to set
	 */
	public void setReadDao(ReadDao readDao) {
		this.readDao = readDao;
	}

	@Override
	public int executeUpdate(String query, Object... param) throws PersistenceException {
		return baseDao.executeUpdate(query,param);
	}
	
	public void setBaseDao(WriteDao baseDao) {
		this.baseDao = baseDao;
	}
}
