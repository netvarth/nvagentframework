package com.nv.agent.repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.LockModeType;
import javax.persistence.Query;

import com.nv.platform.base.dao.PersistenceException;


/**
 * This class is wrapper around entityManager through which all jpa operations
 * are being fulfilled. Instead of directly accessing em, this interface defines 
 * methods through all the operations can be done.
 * @author Joshi Manjila
 */
public interface BaseDao {

	/**
	 * This method will persist a newly created entity
	 * @param <T> refers typed object of type;
	 * @param obj object
	 * @throws PersistenceException persistenceException
	 */
	<T> void  save(final T obj)throws PersistenceException;
	
	/**
	 * This method will merge the changes to a already existing entity.
	 * @param <T> refers typed object of type;
	 * @param obj object
	 * @throws PersistenceException persistenceException
	 */
	<T> void  update(final T obj) throws PersistenceException;
	
	/**
	 * This method will remove the object from persistant store.
	 * @param <T> refers typed object of type;
	 * @param obj  object
	 * @throws PersistenceException persistenceException
	 */
	<T> void  delete(T obj)throws PersistenceException;
	
	/**
	 * This method will find the entity by id and return
	 * @param <T> refers typed object of type;
	 * @param clazz input class name
	 * @param id input id to be fetched
	 * @return T object of T 
	 * @throws PersistenceException persistenceException
	 */
	<T>T getById(Class<T> clazz,int id) throws PersistenceException;

	/**
	 * This method will find the entity by id (of string type) and return
	 * @param <T> refers typed object of type;
	 * @param clazz input class name
	 * @param id input id to be fetched
	 * @return T object of T 
	 * @throws PersistenceException persistenceException
	 */
	<T>T getById(Class<T> clazz,String id) throws PersistenceException;


	
     /**
      * This will return object by UId when Uid is of integer type
      * @param clazz typed className
      * @param <T> refers typed object of type;
      * @param uid unique id if any
      * @return T typed object
      * @throws PersistenceException if any db errors are there
      */
	<T> T getByUid(Class<T> clazz,int uid) throws PersistenceException;

  
	/**
	 * For executing a query and return a single object this method is used.
	 * @param clazz input class
	 * @param <T> refers typed object of type;
	 * @param query represents the Query
	 * @return T type object
	 * @throws PersistenceException uf abt db exception happends
	 */
	<T> T executeUniqueQuery(Class<T> clazz,Query query) throws PersistenceException;
	
	/**
	 * This method is rarely used to fetch all the entities in a class
	 * @param <T> refers typed object of type;
	 * @param clazz typed class
	 * @return list of T objects
	 * @throws PersistenceException persistenceExcetpion
	 */
	<T> List<T> loadAll(Class<T> clazz)throws PersistenceException;

    
	/**
     * This method will execute query and return a list of objecct of type T
     * @param <T> refers typed object of type;
     * @param clazz typed class
     * @param query hql query
     * @return list of objects of T types;
     * @throws PersistenceException when db operation fails
     */
	<T> List<T> executeQuery(Class<T> clazz,Query query) throws PersistenceException;
	
	/**
	 * This method is used to delete with id
	 * @param <T> refers typed object ;
	 * @param clazz typed class
	 * @param id db id
	 * @throws PersistenceException persistenceException
	 */
	<T> void deleteWithId(Class<T> clazz, int id) throws PersistenceException;
	
     /**
      
      * This method is used to build a query object from string argument.   
      * @param queryString string query 
      * @return Query object
      */
	 Query buildQuery(String queryString);
	 
	  <T> T getByObjectId(Class<T> a, Object id) throws PersistenceException;
	  
	  <T> void deleteWithObjectId(Class<T> clazz,Object id) throws PersistenceException;
	  
	  <T> void saveAndUnlock(T obj) throws PersistenceException ;
	
	  <T> T lockAndget(Class<T> clazz, int id) throws PersistenceException ;	
}
