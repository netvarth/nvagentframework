package com.nv.agent.repository;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.base.exception.ErrorStatusType;

public  class BaseDaoImpl implements BaseDao{
	@PersistenceContext(unitName="ynw")
	private EntityManager em;
	
	
    
	@Transactional(value="ynw",readOnly=false)
	public <T> void  update(final T obj) throws PersistenceException {
		try{em.merge(obj);
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
	 * @param obj  object
	 * @throws PersistenceException persistenceException
	 */
	@Transactional(value="ynw",readOnly=false)
	public <T> void  delete(T obj) throws PersistenceException{
		try{
			em.merge(obj);
			em.remove(obj);
		}catch (ClassCastException e) {
			PersistenceException pe=new PersistenceException(ErrorStatusType.INTERNALSERVERERROR,"exception in delete",e);

			throw pe;
		}
		catch(RuntimeException e){

			PersistenceException pe = new PersistenceException(ErrorStatusType.SERVICEUNAVAILABLE,"exception in delete",e);

			throw pe;
		}		

	}

	
	
	/**
	 * 
	 * @param obj object
	 * @throws PersistenceException persistenceException
	 */
	
	@Transactional(value="ynw",readOnly=false)
	public <T> void save(T obj) throws PersistenceException {
		try{
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
	 * @param a iput class name
	 * @param id input id to be fetched
	 * @return T object of T
	 * @throws PersistenceException persistenceException
	 */
	@Transactional(value="ynw",readOnly=false)
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
	
	@Transactional(value="ynw",readOnly=false)
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
	@Transactional(value="ynw",readOnly=false)
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


	@Transactional(value="ynw",readOnly=false)
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
	 * @param sequence database sequence number
	 * @return sequence next database sequence number
	 * @throws PersistenceException persistenceException
	 */
	public BigInteger getNextSequenceVal(String sequence) throws PersistenceException {
		try{
			Query query = em.createNativeQuery("select nextval('"+sequence+"')");			
			return (BigInteger) query.getSingleResult();
		}catch (RuntimeException e) {
			PersistenceException pe= new PersistenceException(ErrorStatusType.NOTFOUND,"execption during generating next seqence value",e);

			throw pe;
		}
	}
	/**
	 * 
	 * @param className iput class name
	 * @param query HQL query 
	 * @return objects of type T
	 * @throws PersistenceException persistenceException
	 */
	@Transactional(value="ynw",readOnly=false)
	public <T> List<T> executeQuery(Class<T> className,Query query) throws PersistenceException{
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
	/**
	 * 
	 * @param className iput class name
	 * @param query HQL query 
	 * @return single object of type T
	 * @throws PersistenceException persistenceException
	 */
	@SuppressWarnings("unchecked")
	@Transactional(value="ynw",readOnly=false)
	public <T> T executeUniqueQuery(Class<T> className,Query query) throws PersistenceException
	{
		T response=null;
		try{
			response= (T) query.getSingleResult();
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


	/**
	 * 
	 * @param className iput class name
	 * @return all objects of T
	 * @throws PersistenceException persistenceException
	 */

	@Transactional(value="ynw",readOnly=true)
	public <T> List<T> loadAll(Class<T> className)throws PersistenceException{
		List<T>  response=null;
		try{
			Query query =em.createQuery("select p from "+className.getSimpleName()+" p");
			response=query.getResultList();
		}
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


	/**
	 * 
	 * @param a iput class name
	 * @param str input string
	 * @return object of T 
	 * @throws PersistenceException persistenceException
	 */
	
	@Transactional(value="ynw",readOnly=true)
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
	/* (non-Javadoc)
	 * @see com.nv.platform.base.dao.BaseDao#deleteWithId(int)
	 */
	@Override
	@Transactional(value="ynw",readOnly=false)
	public <T> void deleteWithId(Class<T> clazz,int id) throws PersistenceException {
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
	@Override
	@Transactional(value="ynw",readOnly=false)
	public <T> void deleteWithObjectId(Class<T> clazz,Object id) throws PersistenceException {
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

	/**
	 * 
	 */
	@Override
	
	public Query buildQuery(String queryString) {
		Query query = em.createQuery(queryString);
		return query;
	}
	@Override
	public <T> void saveAndUnlock(T obj) throws PersistenceException {
		// TODO Auto-generated method stub
		
	}
	@Override
	public <T> T lockAndget(Class<T> clazz, int id) throws PersistenceException {
		// TODO Auto-generated method stub
		return null;
	}
	


}

