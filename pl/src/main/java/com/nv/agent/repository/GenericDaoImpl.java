/**
 * 
 */
package com.nv.agent.repository;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.transaction.annotation.Transactional;

import com.nv.platform.base.dao.GenericDao;
import com.nv.platform.base.dao.PersistenceException;





/**
 * @author Shaby
 * 
 * Oct 23, 2007 11:04:11 AM
 */
public class GenericDaoImpl<T> implements GenericDao<T>{
	
	
	private static final Log log = LogFactory.getLog (GenericDaoImpl.class);
	/**
	 * Injecting Entity Manager as a Private variable
	
	 */
	
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
	
	protected BaseDao baseDao ;
	

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
	 * @see com.nv.platform.base.dao.GenericDao#delete(java.lang.Object)
	 */
	@Override

	public void delete(T obj) throws PersistenceException {
		baseDao.delete(obj);
		
	}





	/* (non-Javadoc)
	 * @see com.nv.platform.base.dao.GenericDao#getById(java.lang.Class, int)
	 */
	@Override

	public T getById( int id) throws PersistenceException {
		
		return baseDao.getById(type, id);
	}





	/* (non-Javadoc)
	 * @see com.nv.platform.base.dao.GenericDao#getByUid(java.lang.Class, int)
	 */
	@Override
	
	public T getByUid( int uid) throws PersistenceException {
		
		return baseDao.getByUid(type, uid);
	}
 	
	
	/* (non-Javadoc)
	 * @see com.nv.platform.base.dao.GenericDao#getById(java.lang.Class, java.lang.String)
	 */
	@Override
	
	public T getById(String str) throws PersistenceException {
		return baseDao.getById(type, str);
	}

	@Override
	@Transactional(value="ynw",readOnly=false)
	public void deleteWithId(int id) throws PersistenceException {
		Object obj = baseDao.getById(type,id);
		baseDao.delete(obj);
	}





	public void setBaseDao(BaseDao baseDao) {
		this.baseDao = baseDao;
	}

}
