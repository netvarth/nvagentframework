/**
 * SendMsgBaseDaoImpl.java
 * @author Nithesh
 *
 * Version 1.0 Jan 7, 2016
 *
 * Copyright (c) 2016 Netvarth Technologies, Inc.
 * All rights reserved.
 *
 */
package com.nv.agent.repository.email;

import java.util.ArrayList;
import java.util.List;
import com.nv.platform.sendmsg.PendingMessageEntity;
import com.nv.platform.sendmsg.PendingMsgPojo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.transaction.annotation.Transactional;

import com.nv.platform.base.dao.PersistenceException;
import com.nv.platform.sendmsg.common.SendMsgBaseDAO;
import com.nv.agent.repository.GenericDaoImpl;

/**
 *
 *
 * @author Nithesh
 */
public class SendMsgBaseDaoImpl extends GenericDaoImpl<SendMsgBaseDAO> implements SendMsgBaseDAO{
	@PersistenceContext(unitName="ynw" )
	private EntityManager em;
	
	public static final String GET_NEW_MSG = "from PendingMessageEntity as msg where msg.status=:param3 and msg.communicationType=:param1 and msg.applicationSpecifier=:param2 ORDER BY lastAttemptOn ASC";
	public static final String GET_INQUEUE_FROM_TABLE = "from PendingMessageEntity as msg where msg.status=:param3 and msg.communicationType=:param1 and msg.applicationSpecifier=:param2 ORDER BY lastAttemptOn ASC";
	public static final String GET_COUNT_NEW_FROM_TABLE = "from PendingMessageEntity as msg where msg.status=:param3 and msg.communicationType=:param1  and msg.applicationSpecifier=:param2";
	

	/* (non-Javadoc)
	 * @see com.nv.platform.sendmsg.common.SendMsgBaseDAO#getInqueueMsg(java.lang.String, java.lang.String)
	 */
	@Override
	public List<Integer> getInqueueMsg(String type, String context) throws PersistenceException {
		Query query = em
				.createQuery(GET_INQUEUE_FROM_TABLE);
		query.setParameter("param1", type);
		query.setParameter("param2", "ynw");
		query.setParameter("param3", "INQUEUE");
		List<PendingMessageEntity> pendingMessages = baseDao.executeQuery(
				PendingMessageEntity.class, query);
		List<Integer> inqueMsgIds = new ArrayList<Integer>();
		for (PendingMessageEntity PendingMessageEntity : pendingMessages) {
			inqueMsgIds.add(PendingMessageEntity.getId());

		}
		return inqueMsgIds;
	}

	/* (non-Javadoc)
	 * @see com.nv.platform.sendmsg.common.SendMsgBaseDAO#getNewMsg(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<Integer> getNewMsg(String type, String count, String context) throws PersistenceException {
		Query query = em
				.createQuery(GET_NEW_MSG);
		query.setParameter("param1", type);
		query.setParameter("param2", "ynw");
		query.setParameter("param3", "NEW");
		query.setFirstResult(0);
		query.setMaxResults(Integer.parseInt(count));
		List<PendingMessageEntity> pendingMessages = baseDao.executeQuery(
				PendingMessageEntity.class, query);
		List<Integer> inqueMsgIds = new ArrayList<Integer>();
		for (PendingMessageEntity PendingMessageEntity : pendingMessages) {
			inqueMsgIds.add(PendingMessageEntity.getId());
		}
		return inqueMsgIds;
	}

	/* (non-Javadoc)
	 * @see com.nv.platform.sendmsg.common.SendMsgBaseDAO#getNewMsgCount(java.lang.String, java.lang.String)
	 */
	@Override
	public Long getNewMsgCount(String type, String context) throws PersistenceException {
		Query query = em
				.createQuery(GET_COUNT_NEW_FROM_TABLE);
		query.setParameter("param1", type);
		query.setParameter("param2", "ynw");
		query.setParameter("param3", "NEW");
		List<PendingMessageEntity> pendingMessages = baseDao.executeQuery(
				PendingMessageEntity.class, query);
		if (pendingMessages == null) {
			return (long) 0;
		}
		return (long) pendingMessages.size();
	}

	@Transactional(value ="ynw",readOnly=false)
	@Override
	public void saveMsg(PendingMsgPojo obj) throws PersistenceException {

		PendingMessageEntity ptbl = pojoToPendingMsg(obj);
		baseDao.save(ptbl);
		obj.setId(ptbl.getId());
	}

	@Transactional(value ="ynw",readOnly=false)
	@Override
	public void deleteMsg(PendingMsgPojo obj) throws PersistenceException {
		PendingMessageEntity pTbl = (PendingMessageEntity) baseDao.getById(
				PendingMessageEntity.class, obj.getId());
		baseDao.delete(pTbl);
	}

	@Transactional(value ="ynw",readOnly=false)
	@Override
	public void updateMsg(PendingMsgPojo obj) throws PersistenceException {
		baseDao.update(pojoToPendingMsg(obj));
	}

	/* (non-Javadoc)
	 * @see com.nv.platform.sendmsg.common.SendMsgBaseDAO#setCompanyType(java.lang.String, java.lang.String)
	 */
	@Override
	public void setCompanyType(String companyType, String jndiName) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see com.nv.platform.sendmsg.common.SendMsgBaseDAO#getPendingMsgTblById(int)
	 */
	@Override
	public PendingMsgPojo getPendingMsgTblById(int id) throws PersistenceException {
		PendingMessageEntity pTbl = (PendingMessageEntity) baseDao.getById(
				PendingMessageEntity.class, id);
		return pendingMsgToPojo(pTbl);
	}
	private PendingMsgPojo pendingMsgToPojo(PendingMessageEntity pTbl) {
		PendingMsgPojo pPojoTbl = new PendingMsgPojo();
		pPojoTbl.setApplicationSpecifier(pTbl.getApplicationSpecifier());
		pPojoTbl.setAttemptsMade(pTbl.getAttemptsMade());
		pPojoTbl.setClusterId(pTbl.getClusterId());
		pPojoTbl.setCommunicationType(pTbl.getCommunicationType());
		pPojoTbl.setContent(pTbl.getContent());
		pPojoTbl.setErrorCode(pTbl.getErrorCode());
		pPojoTbl.setId(pTbl.getId());
		pPojoTbl.setLastAttemptOn(pTbl.getLastAttemptOn());
		pPojoTbl.setStatus(pTbl.getStatus());

		return pPojoTbl;
	}

	private PendingMessageEntity pojoToPendingMsg(PendingMsgPojo pPojoTbl) {
		PendingMessageEntity pTbl = new PendingMessageEntity();
		pTbl.setApplicationSpecifier(pPojoTbl.getApplicationSpecifier());
		pTbl.setAttemptsMade(pPojoTbl.getAttemptsMade());
		pTbl.setClusterId(pPojoTbl.getClusterId());
		pTbl.setCommunicationType(pPojoTbl.getCommunicationType());
		pTbl.setContent(pPojoTbl.getContent());
		pTbl.setErrorCode(pPojoTbl.getErrorCode());
		pTbl.setLastAttemptOn(pPojoTbl.getLastAttemptOn());
		pTbl.setStatus(pPojoTbl.getStatus());
		return pTbl;
	}

	

	/**
	 * @return the em
	 */
	public EntityManager getEm() {
		return em;
	}

	/**
	 * @param em the em to set
	 */
	public void setEm(EntityManager em) {
		this.em = em;
	}

}
