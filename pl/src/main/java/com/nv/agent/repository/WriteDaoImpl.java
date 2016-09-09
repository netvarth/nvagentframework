package com.nv.agent.repository;



import javax.persistence.EntityManager;
import javax.persistence.IdClass;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.EntityType;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.nv.platform.base.BasePlatformConstants;
import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.base.dao.WriteDao;
import com.nv.platform.base.entity.BaseEntity;
import com.nv.platform.base.entity.sequence.IdGenerator;
import com.nv.platform.base.exception.ErrorStatusType;
import com.nv.platform.base.service.RequestScopeManager;
/**
 * 
 *
 *
 * @author Joshi Manjila
 */
public  class WriteDaoImpl implements WriteDao{

	private static final String paramString="param";

	@PersistenceContext(unitName="ynw")
	private EntityManager em;

	/**
	 * 
	 * @param a iput class name
	 * @param id input id to be fetched
	 * @return T object of T
	 * @throws PersistenceException persistenceException
	 */
	@Transactional(readOnly=true,propagation=Propagation.REQUIRED)
	public <T> T getById(Class<T> a, Object id) throws PersistenceException {			
		try{
			T obj=em.find(a, id);
			return obj;
		}catch(NoResultException e){
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

	@Transactional(readOnly=false,propagation=Propagation.REQUIRED)
	public <T> void  update(final T obj) throws PersistenceException {

		if (obj==null) throw new PersistenceException(ErrorStatusType.UNPROCESSABLENTITY, "updating persisting object is null");
		
		try{
			em.merge(obj);
		}catch (ClassCastException e) {

			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"exception in update",e);

			throw pe;
		}catch (IllegalArgumentException e) {

			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"exception in update",e);

			throw pe;
		}catch(RuntimeException e){

			PersistenceException pe = new PersistenceException(ErrorStatusType.SERVICEUNAVAILABLE,"exception in update" ,e);

			throw pe;
		}		

	}	    

	/**
	 * 
	 * @param obj object
	 * @throws PersistenceException persistenceException
	 */

	@Transactional(readOnly=false,propagation=Propagation.REQUIRED)
	public <T> void save(T obj) throws PersistenceException {


		try{
			if (obj==null) throw new PersistenceException(ErrorStatusType.UNPROCESSABLENTITY, "updating persisting object is null");
			em.persist(obj);
		}catch (ClassCastException e) {
			PersistenceException pe= new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"exception in save",e);

			throw pe;
		}
		catch(RuntimeException e){

			PersistenceException pe = new PersistenceException(ErrorStatusType.SERVICEUNAVAILABLE,"exception in save",e);

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
	@Transactional(readOnly=true,propagation=Propagation.REQUIRED)
	public <T> T getByUid(Class<T> className, Object uid) throws PersistenceException {
		try{
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<T> cq = cb.createQuery(className);
			Root<T> root = cq.from(className) ;
			EntityType<T> Root_ = root.getModel();
			Predicate condition = cb.equal(root.get(Root_.getSingularAttribute("uid", String.class)), uid);
			cq.where(condition); 
			cq.select(root);
			TypedQuery<T> query =em.createQuery(cq);
			T result=  query.getSingleResult();
			return result;	
		}catch(NoResultException e){
			return null;
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








	/* (non-Javadoc)
	 * @see com.nv.platform.base.dao.BaseDao#deleteWithId(int)
	 */
	@Override
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED)
	public <T> void delete(Class<T> clazz,Object id) throws PersistenceException {


		if (id==null) throw new PersistenceException(ErrorStatusType.UNPROCESSABLENTITY,"deleting object id refers null");	
		try{
			T obj=em.find(clazz,id);
			em.remove(obj);}
		catch (ClassCastException e) {
			PersistenceException pe=new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"exception in delete",e);
			throw pe;
		}
		catch(RuntimeException e){

			PersistenceException pe = new PersistenceException(ErrorStatusType.SERVICEUNAVAILABLE,"exception in delete",e);

			throw pe;
		}	

	}
	/* (non-Javadoc)
	 * @see com.nv.platform.base.dao.BaseDao#executeUpdate(String,Object...)
	 */

	@Override
	@Transactional(readOnly=false,propagation=Propagation.REQUIRED)
	public int executeUpdate(String queryStr, Object... params) throws PersistenceException {
		Query query = em.createNativeQuery(queryStr);
		int count=1;
		for(Object param:params){
	    	query.setParameter(paramString+count++,param);
	    }
		try{
			query.executeUpdate();
		}catch(RuntimeException e){
			PersistenceException pe = new PersistenceException(ErrorStatusType.SERVICEUNAVAILABLE,"exception in execute native query",e);
			throw pe;
		}
		return count;	
	}
}

