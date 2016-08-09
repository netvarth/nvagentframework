package com.nv.agent.repository.cache;


import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.transaction.annotation.Transactional;
import com.nv.platform.base.exception.ErrorStatusType;
import com.nv.platform.cache.CacheDao;
import com.nv.platform.cache.CacheException;

public class CacheDaoImpl implements CacheDao {

	@PersistenceContext(unitName="cache")
	private EntityManager em;
	
	String paramString="param";
	
    
	@Transactional(value="cache",readOnly=false)
	public <T> void  update(final T obj) throws CacheException {
		try{em.merge(obj);
		}catch (ClassCastException e) {

			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"exception in update",e);

			throw pe;
		}catch (IllegalArgumentException e) {

			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"exception in update",e);

			throw pe;
		}catch(RuntimeException e){

			CacheException pe = new CacheException(ErrorStatusType.SERVICEUNAVAILABLE,"exception in update" ,e);

			throw pe;
		}		

	}	    
	/**
	 * 
	 * @param obj  object
	 * @throws CacheException cacheException
	 */
	@Transactional(value="cache",readOnly=false)
	public <T> void  delete(T obj) throws CacheException{
		try{
			em.merge(obj);
			em.remove(obj);
		}catch (ClassCastException e) {
			CacheException pe=new CacheException(ErrorStatusType.INTERNALSERVERERROR,"exception in delete",e);

			throw pe;
		}
		catch(RuntimeException e){

			CacheException pe = new CacheException(ErrorStatusType.SERVICEUNAVAILABLE,"exception in delete",e);

			throw pe;
		}		

	}

	
	
	/**
	 * 
	 * @param obj object
	 * @throws CacheException {{@link CacheException}
	 */
	
	@Transactional(value="cache",readOnly=false)
	public <T> void save(T obj) throws CacheException {
		try{
			em.persist(obj);
		}catch (ClassCastException e) {
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"exception in save",e);

			throw pe;
		}
		catch(RuntimeException e){

			CacheException pe = new CacheException(ErrorStatusType.SERVICEUNAVAILABLE,"exception in save",e);

			throw pe;
		}		
	}
	/**
	 * 
	 * @param a iput class name
	 * @param id input id to be fetched
	 * @return T object of T
	 * @throws CacheException {{@link CacheException}
	 */
	@Transactional(value="cache",readOnly=false)
	public <T> T getById(Class<T> a, int id) throws CacheException {			
		try{
			T obj=em.find(a, id);
			return obj;
		}catch(NoResultException e){
			//e.printStackTrace();
			return null;
		}catch (ClassCastException e) {
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"execption in getById",e);
			
			throw pe;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"execption in getById",e);
			throw pe;
		}catch(RuntimeException e){
			e.printStackTrace();
			CacheException pe = new CacheException(ErrorStatusType.SERVICEUNAVAILABLE,"execption in getById",e);
			throw pe;
		}

	}	
	
	
   
	
	@Transactional(value="cache",readOnly=false)
	public <T> T getByObjectId(Class<T> a, Object id) throws CacheException {			
		try{
			T obj=em.find(a, id);
			return obj;
		}catch(NoResultException e){
			//e.printStackTrace();
			return null;
		}catch (ClassCastException e) {
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"execption in getById",e);
			
			throw pe;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"execption in getById",e);
			throw pe;
		}catch(RuntimeException e){
			e.printStackTrace();
			CacheException pe = new CacheException(ErrorStatusType.SERVICEUNAVAILABLE,"execption in getById",e);
			throw pe;
		}

	}		
	
	/**
	 * 
	 * @param className iput class name
	 * @param uid input uid to be fetched
	 * @return T  object of T
	 * @throws CacheException {{@link CacheException}
	 */
	@Transactional(value="cache",readOnly=false)
	public <T> T getByUid(Class<T> className, int uid) throws CacheException {
		try{
			Query query=em.createQuery("from "+className.getName()+" as obj where obj.uid=:uid");
			query.setParameter("uid", uid);
			return (T) query.getSingleResult();
		}catch(NoResultException e){
			CacheException pe= new CacheException(ErrorStatusType.NOTFOUND,"execption in getByUid",e);
			throw pe;
		}catch (ClassCastException e) {
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"execption in getByUid",e);

			throw pe;
		}catch (IllegalArgumentException e) {

			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"execption in getByUid",e);

			throw pe;
		}catch(RuntimeException e){

			CacheException pe = new CacheException(ErrorStatusType.SERVICEUNAVAILABLE,"execption in getById",e);

			throw pe;
		}
	}
  
   /**
    * 
    * @param className name
    * @param uid uid
    * @param <T> this describes the type param
    * @return T type
    * @throws CacheException {@link CacheException}
    */
	@Transactional(value="cache",readOnly=false)
	public <T> T getByUid(Class<T> className, String uid) throws CacheException {
		try{
			Query query=em.createQuery("from "+className.getName()+" as obj where obj.uid=:uid");
			query.setParameter("uid", uid);
			T result = (T) query.getSingleResult();
		if (result==null){
			CacheException pe= new CacheException(ErrorStatusType.NOTFOUND,"execption in getByUid");
		    throw pe;
		}
		return result;    
		}catch (ClassCastException e) {
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"execption in getByUid",e);

			throw pe;
		}catch (IllegalArgumentException e) {

			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"execption in getByUid",e);

			throw pe;
		}catch(RuntimeException e){

			CacheException pe = new CacheException(ErrorStatusType.SERVICEUNAVAILABLE,"execption in getById",e);

			throw pe;
		}
	    
	}



	
	/**
	 * 
	 * @param className iput class name
	 * @param query HQL query 
	 * @return objects of type T
	 * @throws CacheException {{@link CacheException}
	 */
	@Transactional(value="cache",readOnly=false)
	public <T> List<T> executeQuery(Class<T> className,Query query) throws CacheException{
		List<T>  response=null;
		try{
			response= query.getResultList();
		}
		catch (NoResultException e) {
			return null;
		}
		catch (ClassCastException e) {
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeQuery",e);

			throw pe;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeQuery",e);

			throw pe;
		}catch (RuntimeException e) {
			CacheException pe = new CacheException(ErrorStatusType.SERVICEUNAVAILABLE,"execption in getById",e);

			throw pe;
		}
		return response;
	}
	/**
	 * 
	 * @param className iput class name
	 * @param query HQL query 
	 * @return single object of type T
	 * @throws CacheException {{@link CacheException}
	 */
	@SuppressWarnings("unchecked")
	@Transactional(value="cache",readOnly=false)
	public <T> T executeUniqueQuery(Class<T> className,Query query) throws CacheException
	{
		T response=null;
		try{
			response= (T) query.getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
		catch (ClassCastException e) {
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeUnique Query",e);

			throw pe;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeUnique Query",e);

			throw pe;
		}catch (RuntimeException e) {
			e.printStackTrace();
			CacheException pe = new CacheException(ErrorStatusType.SERVICEUNAVAILABLE,"execeptionUniquery",e);

			throw pe;
		}
		return response;
	}


	/**
	 * 
	 * @param className iput class name
	 * @return all objects of T
	 * @throws CacheException {{@link CacheException}
	 */

	@Transactional(value="cache",readOnly=false)
	public <T> List<T> loadAll(Class<T> className)throws CacheException{
		List<T>  response=null;
		try{
			Query query =em.createQuery("select p from "+className.getSimpleName()+" p");
			response=query.getResultList();
		}
			catch (ClassCastException e) {
				CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in loadAll method",e);

				throw pe;
			}catch (IllegalArgumentException e) {
				e.printStackTrace();
				CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in loadAllMethod",e);

				throw pe;
			}catch (RuntimeException e) {
				CacheException pe = new CacheException(ErrorStatusType.SERVICEUNAVAILABLE,"Exception in loadAllMethod",e);

				throw pe;
			}
		return response;
	}


	/**
	 * 
	 * @param a iput class name
	 * @param str input string
	 * @return object of T 
	 * @throws CacheException {{@link CacheException}
	 */
	
	@Transactional(value="cache",readOnly=false)
	public <T> T getById(Class<T> a, String str) throws CacheException {
		try{
			T obj=em.find(a, str);
			return obj;
		}catch(NoResultException e){
			CacheException pe= new CacheException(ErrorStatusType.NOTFOUND,"execption in getById",e);
			throw pe;
		}catch (ClassCastException e) {
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"execption in getById",e);

			throw pe;
		}catch (IllegalArgumentException e) {

			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"execption in getById",e);

			throw pe;
		}catch(RuntimeException e){

			CacheException pe = new CacheException(ErrorStatusType.SERVICEUNAVAILABLE,"execption in getById",e);

			throw pe;
		}

	
	}
	/* (non-Javadoc)
	 * @see com.nv.platform.base.dao.BaseDao#deleteWithId(int)
	 */
	
	@Transactional(value="cache")
	public <T> void deleteWithId(Class<T> clazz,int id) throws CacheException {
		try{
		T obj=em.find(clazz,id);
		em.remove(obj);}
		catch (ClassCastException e) {
			CacheException pe=new CacheException(ErrorStatusType.INTERNALSERVERERROR,"exception in delete",e);

			throw pe;
		}
		catch(RuntimeException e){

			CacheException pe = new CacheException(ErrorStatusType.SERVICEUNAVAILABLE,"exception in delete",e);

			throw pe;
		}	
		
	}

	
	
	@Transactional(value="cache")
	public <T> void deleteWithObjectId(Class<T> clazz,Object id) throws CacheException {
		try{
		T obj=em.find(clazz,id);
		em.remove(obj);}
		catch (ClassCastException e) {
			CacheException pe=new CacheException(ErrorStatusType.INTERNALSERVERERROR,"exception in delete",e);

			throw pe;
		}
		catch(RuntimeException e){

			CacheException pe = new CacheException(ErrorStatusType.SERVICEUNAVAILABLE,"exception in delete",e);

			throw pe;
		}	
		
	}

	
	
	@Transactional(value="cache")
	public Query buildQuery(String queryString) {
		Query query = em.createQuery(queryString);
		return query;
	}
	
	
	
	@Transactional(value="cache",readOnly=false)
	@Override
	public <T> void updateAndUnlock(T obj) throws CacheException {
		update(obj);
		em.lock(obj, LockModeType.NONE);
		
	}
	
	@Transactional(value="cache",readOnly=true)
	@Override
	public <T> T lockAndget(Class<T> clazz, int id) throws CacheException {
		 Map<String,Object> map = new HashMap<String, Object>();
		 map.put("javax.persistence.lock.timeout", 0);
		return  em.find(clazz, id, LockModeType.PESSIMISTIC_READ,map);
	}
	
	
	/* (non-Javadoc)
	 * @see com.nv.ynw.repository.BaseDao#executeUniqueQuery(java.lang.Class, java.lang.String)
	 */
	@Override
	@Transactional(value="ynw",readOnly=true)
	public <T> T lockAndExecuteUniqueQuery(Class<T> clazz, String queryString,Object ...params) throws CacheException {
		Query query = this.buildQuery(queryString);
		query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
		int count=1;
				
	    for(Object param:params){
	    	query.setParameter(paramString+count++,param);
	    }
		T response=null;
		try{
			response= (T) query.getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
		catch (ClassCastException e) {
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeUnique Query",e);

			throw pe;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeUnique Query",e);

			throw pe;
		}catch (RuntimeException e) {
			e.printStackTrace();
			CacheException pe = new CacheException(ErrorStatusType.SERVICEUNAVAILABLE,"execeptionUniquery",e);

			throw pe;
		}
		return response;

	}

	
	/* (non-Javadoc)
	 * @see com.nv.ynw.repository.BaseDao#executeUniqueQuery(java.lang.Class, java.lang.String)
	 */
	@Override
	@Transactional(value="ynw",readOnly=true)
	public <T> T executeUniqueQuery(Class<T> clazz, String queryString,Object ...params) throws CacheException {
		Query query = this.buildQuery(queryString);
		int count=1;
				
	    for(Object param:params){
	    	query.setParameter(paramString+count++,param);
	    }
		T response=null;
		try{
			response= (T) query.getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
		catch (ClassCastException e) {
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeUnique Query",e);

			throw pe;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeUnique Query",e);

			throw pe;
		}catch (RuntimeException e) {
			e.printStackTrace();
			CacheException pe = new CacheException(ErrorStatusType.SERVICEUNAVAILABLE,"execeptionUniquery",e);

			throw pe;
		}
		return response;

	}
	/* (non-Javadoc)
	 * @see com.nv.ynw.repository.BaseDao#executeQuery(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T> List<T> executeQuery(Class<T> clazz, String queryString,Object ...params) throws CacheException {
		Query query = this.buildQuery(queryString);
		int count=1;

		for(Object param:params){
	    	query.setParameter(paramString+count++,param);
	    }
		
		List<T>  response=null;
		try{
			response= query.getResultList();
		}
		catch (NoResultException e) {
			return null;
		}
		catch (ClassCastException e) {
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeQuery",e);

			throw pe;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeQuery",e);

			throw pe;
		}catch (RuntimeException e) {
			CacheException pe = new CacheException(ErrorStatusType.SERVICEUNAVAILABLE,"execption in getById",e);

			throw pe;
		}
		return response;

	}
	
	
	@Override
	public <T> List<T> executeQuery(Class<T> clazz, String queryString, int[] limits, Object ...params) throws CacheException {
		Query query = this.buildQuery(queryString);
		int count=1;

		for(Object param:params){
	    	query.setParameter(paramString+count++,param);
	    }
		query.setFirstResult(limits[0]);
		query.setMaxResults(limits[1]);
		List<T>  response=null;
		try{
			response= query.getResultList();
		}
		catch (NoResultException e) {
			return null;
		}
		catch (ClassCastException e) {
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeQuery",e);

			throw pe;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeQuery",e);

			throw pe;
		}catch (RuntimeException e) {
			CacheException pe = new CacheException(ErrorStatusType.SERVICEUNAVAILABLE,"execption in getById",e);

			throw pe;
		}
		return response;

	}
	
	
	@Override
	public <T> List<T> executeQuery(Class<T> clazz, String queryString, int[] limits) throws CacheException {
		Query query = this.buildQuery(queryString);
	
		query.setFirstResult(limits[0]);
		query.setMaxResults(limits[1]);
		List<T>  response=null;
		try{
			response= query.getResultList();
		}
		catch (NoResultException e) {
			return null;
		}
		catch (ClassCastException e) {
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeQuery",e);

			throw pe;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeQuery",e);

			throw pe;
		}catch (RuntimeException e) {
			CacheException pe = new CacheException(ErrorStatusType.SERVICEUNAVAILABLE,"execption in getById",e);

			throw pe;
		}
		return response;

	}
	

	/* (non-Javadoc)
	 * @see com.nv.ynw.repository.BaseDao#executeQuery(java.lang.Class, java.lang.String)
	 */
	@Override
	public <T> List<T> lockAndExecuteQuery(Class<T> clazz, String queryString,Object ...params) throws CacheException {
		Query query = this.buildQuery(queryString);
		query.setLockMode(LockModeType.PESSIMISTIC_WRITE);
		int count=1;

		for(Object param:params){
	    	query.setParameter(paramString+count++,param);
	    }
		
		List<T>  response=null;
		try{
			response= query.getResultList();
		}
		catch (NoResultException e) {
			return null;
		}
		catch (ClassCastException e) {
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeQuery",e);

			throw pe;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			CacheException pe= new CacheException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeQuery",e);

			throw pe;
		}catch (RuntimeException e) {
			CacheException pe = new CacheException(ErrorStatusType.SERVICEUNAVAILABLE,"execption in getById",e);

			throw pe;
		}
		return response;

	}
	/* (non-Javadoc)
	 * @see com.nv.ynw.cache.CacheDao#executeUpdate(java.lang.String)
	 */
	@Override
	public void executeUpdate(String String) throws CacheException {
		Query query =em.createNativeQuery(String);
		query.executeUpdate();
	}
	
	

}
