
package com.nv.agent.repository;


import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nv.platform.base.BasePlatformConstants;
import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.base.dao.ReadDao;
import com.nv.platform.base.entity.BaseEntity;
import com.nv.platform.base.exception.ErrorStatusType;
import com.nv.platform.base.service.RequestScopeManager;
import com.nv.platform.log.api.NVLogger;
import com.nv.platform.log.impl.NVLoggerAPIFactory;



public class ReadDaoImpl implements ReadDao{
	NVLogger logger = NVLoggerAPIFactory.getLogger(ReadDaoImpl.class);
	private static final Integer DEFAULTLIMIT = 100;
	@PersistenceContext(unitName="ynw")
	private EntityManager em;
	private String paramString="param";
	
	/**
	 * 
	 * @param a iput class name
	 * @param id input id to be fetched
	 * @return T object of T
	 * @throws PersistenceException persistenceException
	 */
	@Transactional(value="write",readOnly=true,propagation=Propagation.REQUIRED)
	public <T> T getById(Class<T> a, int id) throws PersistenceException {			
		try{
			T obj=em.find(a, id);
			return obj;
		}catch(NoResultException e){
			//e.printStackTrace();
			return null;
		}catch (ClassCastException e) {
			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"execption in getById",e);
			
			throw pe;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"execption in getById",e);
			throw pe;
		}catch(RuntimeException e){
			e.printStackTrace();
			PersistenceException pe = new PersistenceException(ErrorStatusType.SERVICEUNAVAILABLE,"execption in getById",e);
			throw pe;
		}

	}	
	
	@Transactional(value="write",readOnly=true,propagation=Propagation.REQUIRED)
	public <T> T getByObjectId(Class<T> a, Object id) throws PersistenceException {			
		try{
			T obj=em.find(a, id);
			return obj;
		}catch(NoResultException e){
			//e.printStackTrace();
			return null;
		}catch (ClassCastException e) {
			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"execption in getById",e);
			
			throw pe;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"execption in getById",e);
			throw pe;
		}catch(RuntimeException e){
			e.printStackTrace();
			PersistenceException pe = new PersistenceException(ErrorStatusType.SERVICEUNAVAILABLE,"execption in getById",e);
			throw pe;
		}

	}		
		/**
	 * 
	 * @param className iput class name
	 * @param uid input uid to be fetched
	 * @return T  object of T
	 * @throws PersistenceException persistenceException
	 */
	@Transactional(value="write",readOnly=true,propagation=Propagation.REQUIRED)
	public <T> T getByUid(Class<T> className, int uid) throws PersistenceException {
		try{
			Query query=em.createQuery("from "+className.getName()+" as obj where obj.uid=:uid");
			query.setParameter("uid", uid);
			return (T) query.getSingleResult();
		}catch(NoResultException e){
			PersistenceException pe= new PersistenceException(ErrorStatusType.NOTFOUND,"execption in getByUid",e);
			throw pe;
		}catch (ClassCastException e) {
			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"execption in getByUid",e);

			throw pe;
		}catch (IllegalArgumentException e) {

			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"execption in getByUid",e);

			throw pe;
		}catch(RuntimeException e){

			PersistenceException pe = new PersistenceException(ErrorStatusType.SERVICEUNAVAILABLE,"execption in getById",e);

			throw pe;
		}
	}


	@Transactional(value="write",readOnly=true,propagation=Propagation.REQUIRED)
	public <T> T getByUid(Class<T> className, String uid) throws PersistenceException {
		try{
			Query query=em.createQuery("from "+className.getName()+" as obj where obj.uid=:uid");
			query.setParameter("uid", uid);
			T result = (T) query.getSingleResult();
		if (result==null){
			PersistenceException pe= new PersistenceException(ErrorStatusType.NOTFOUND,"execption in getByUid");
		    throw pe;
		}
		return result;    
		}catch (ClassCastException e) {
			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"execption in getByUid",e);

			throw pe;
		}catch (IllegalArgumentException e) {

			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"execption in getByUid",e);

			throw pe;
		}catch(RuntimeException e){

			PersistenceException pe = new PersistenceException(ErrorStatusType.SERVICEUNAVAILABLE,"execption in getById",e);

			throw pe;
		}
	    
	}



	/**
	 * 
	 * @param a iput class name
	 * @param str input string
	 * @return object of T 
	 * @throws PersistenceException persistenceException
	 */
	
	@Transactional(value="write",readOnly=true,propagation=Propagation.REQUIRED)
	public <T> T getById(Class<T> a, String str) throws PersistenceException {
		try{
			T obj=em.find(a, str);
			return obj;
		}catch(NoResultException e){
			PersistenceException pe= new PersistenceException(ErrorStatusType.NOTFOUND,"execption in getById",e);
			throw pe;
		}catch (ClassCastException e) {
			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"execption in getById",e);

			throw pe;
		}catch (IllegalArgumentException e) {

			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"execption in getById",e);

			throw pe;
		}catch(RuntimeException e){

			PersistenceException pe = new PersistenceException(ErrorStatusType.SERVICEUNAVAILABLE,"execption in getById",e);

			throw pe;
		}

	
	}

	
	/**
	 * 
	 * @param className iput class name
	 * @return all objects of T
	 * @throws PersistenceException persistenceException
	 */

	@Transactional(value="write",readOnly=true,propagation=Propagation.REQUIRED)
	public <T> List<T> loadAll(Class<T> className)throws PersistenceException{
		List<T>  response=null;
		try{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(className);
			
			
			
			TypedQuery<T> query =em.createQuery(cq);
			
			query.setMaxResults(DEFAULTLIMIT);
			response=query.getResultList();	}
			catch (ClassCastException e) {
				PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"Exception in loadAll method",e);

				throw pe;
			}catch (IllegalArgumentException e) {
				e.printStackTrace();
				PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"Exception in loadAllMethod",e);

				throw pe;
			}catch (RuntimeException e) {
				PersistenceException pe = new PersistenceException(ErrorStatusType.SERVICEUNAVAILABLE,"Exception in loadAllMethod",e);

				throw pe;
			}
		return response;
	}

	

	

	@Override
	@Transactional(value="write",readOnly=true,propagation=Propagation.REQUIRED)
	public <T> List<T> executeQuery(Class<T> clazz, String queryString,  int[] limits, Object ...params) throws PersistenceException {
		Query query = em.createQuery(queryString);
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
			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeQuery",e);

			throw pe;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeQuery",e);

			throw pe;
		}catch (RuntimeException e) {
			PersistenceException pe = new PersistenceException(ErrorStatusType.SERVICEUNAVAILABLE,"execption in getById",e);

			throw pe;
		}
		return response;

	}

	
	@Override
	@Transactional(value="write",readOnly=true,propagation=Propagation.REQUIRED)
	public <T> List<T> executeQuery(Class<T> clazz, String queryString,  int[] limits) throws PersistenceException {
		Query query = em.createQuery(queryString);
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
			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeQuery",e);

			throw pe;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeQuery",e);

			throw pe;
		}catch (RuntimeException e) {
			PersistenceException pe = new PersistenceException(ErrorStatusType.SERVICEUNAVAILABLE,"execption in getById",e);

			throw pe;
		}
		return response;

	}
		
	
	
	/* (non-Javadoc)
	 * @see com.nv.ynw.repository.BaseDao#executeQuery(java.lang.Class, java.lang.String)
	 */
	@Override
	@Transactional(value="write",readOnly=true,propagation=Propagation.REQUIRED)
	public <T> List<T> executeQuery(Class<T> clazz, String queryString,Object ...params) throws PersistenceException {
		TypedQuery<T> query = em.createQuery(queryString,clazz);
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
			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeQuery",e);

			throw pe;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeQuery",e);

			throw pe;
		}catch (RuntimeException e) {
			PersistenceException pe = new PersistenceException(ErrorStatusType.SERVICEUNAVAILABLE,"execption in getById",e);

			throw pe;
		}
		return response;

	}
	
	

	@Override
	@Transactional(value="write",readOnly=true,propagation=Propagation.REQUIRED)
	public <T> T executeUniqueQuery(Class<T> clazz, String queryString,Object ...params) throws PersistenceException {
		TypedQuery<T> query = em.createQuery(queryString,clazz);
		int count=1;
		for(Object param:params){
	    	query.setParameter(paramString+count++,param);
	    }
		T response=null;
		try{
			response=  query.getSingleResult();
		}
		catch (NoResultException e) {
			return null;
		}
		catch (ClassCastException e) {
			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeUnique Query",e);

			throw pe;
		}catch (IllegalArgumentException e) {
			e.printStackTrace();
			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"Exception in executeUnique Query",e);

			throw pe;
		}catch (RuntimeException e) {
			e.printStackTrace();
			PersistenceException pe = new PersistenceException(ErrorStatusType.SERVICEUNAVAILABLE,"execeptionUniquery",e);

			throw pe;
		}
		return response;

	}

	/* (non-Javadoc)
	 * @see com.nv.platform.base.dao.readDao#lockAndget(java.lang.Class, int)
	 */
	@Override
	public <T> T lockAndget(Class<T> clazz, int id) throws PersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.nv.platform.base.dao.ReadDao#updateAndUnlock(java.lang.Object)
	 */
	@Override
	public <T> void updateAndUnlock(T obj) throws PersistenceException {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.nv.platform.base.dao.ReadDao#lockAndExecuteUniqueQuery(java.lang.Class, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> T lockAndExecuteUniqueQuery(Class<T> clazz, String queryString, Object... params)
			throws PersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.nv.platform.base.dao.ReadDao#lockAndExecuteQuery(java.lang.Class, java.lang.String, java.lang.Object[])
	 */
	@Override
	public <T> List<T> lockAndExecuteQuery(Class<T> clazz, String queryString, Object... params)
			throws PersistenceException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see com.nv.platform.base.dao.ReadDao#executeUpdate(java.lang.String)
	 */
	@Override
	public void executeUpdate(String String) throws PersistenceException {
		// TODO Auto-generated method stub
		
	}
}
