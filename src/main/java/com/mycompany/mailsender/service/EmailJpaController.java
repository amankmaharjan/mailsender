/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mailsender.service;

import com.mycompany.mailsender.domain.Email;
import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.mycompany.mailsender.domain.Sender;
import com.mycompany.mailsender.domain.Recipient;
import com.mycompany.mailsender.service.exceptions.NonexistentEntityException;
import com.mycompany.mailsender.util.JPAUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

/**
 *
 * @author aman
 */
public class EmailJpaController implements Serializable {

    public EmailJpaController( ) {
        this.emf = JPAUtil.emfactory;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Email email) {
        if (email.getRecipientCollection() == null) {
            email.setRecipientCollection(new ArrayList<Recipient>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Sender senderId = email.getSenderId();
            if (senderId != null) {
                senderId = em.getReference(senderId.getClass(), senderId.getId());
                email.setSenderId(senderId);
            }
            Collection<Recipient> attachedRecipientCollection = new ArrayList<Recipient>();
            for (Recipient recipientCollectionRecipientToAttach : email.getRecipientCollection()) {
                recipientCollectionRecipientToAttach = em.getReference(recipientCollectionRecipientToAttach.getClass(), recipientCollectionRecipientToAttach.getId());
                attachedRecipientCollection.add(recipientCollectionRecipientToAttach);
            }
            email.setRecipientCollection(attachedRecipientCollection);
            em.persist(email);
            if (senderId != null) {
                senderId.getEmailCollection().add(email);
                senderId = em.merge(senderId);
            }
            for (Recipient recipientCollectionRecipient : email.getRecipientCollection()) {
                recipientCollectionRecipient.getEmailCollection().add(email);
                recipientCollectionRecipient = em.merge(recipientCollectionRecipient);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Email email) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Email persistentEmail = em.find(Email.class, email.getId());
            Sender senderIdOld = persistentEmail.getSenderId();
            Sender senderIdNew = email.getSenderId();
            Collection<Recipient> recipientCollectionOld = persistentEmail.getRecipientCollection();
            Collection<Recipient> recipientCollectionNew = email.getRecipientCollection();
            if (senderIdNew != null) {
                senderIdNew = em.getReference(senderIdNew.getClass(), senderIdNew.getId());
                email.setSenderId(senderIdNew);
            }
            Collection<Recipient> attachedRecipientCollectionNew = new ArrayList<Recipient>();
            for (Recipient recipientCollectionNewRecipientToAttach : recipientCollectionNew) {
                recipientCollectionNewRecipientToAttach = em.getReference(recipientCollectionNewRecipientToAttach.getClass(), recipientCollectionNewRecipientToAttach.getId());
                attachedRecipientCollectionNew.add(recipientCollectionNewRecipientToAttach);
            }
            recipientCollectionNew = attachedRecipientCollectionNew;
            email.setRecipientCollection(recipientCollectionNew);
            email = em.merge(email);
            if (senderIdOld != null && !senderIdOld.equals(senderIdNew)) {
                senderIdOld.getEmailCollection().remove(email);
                senderIdOld = em.merge(senderIdOld);
            }
            if (senderIdNew != null && !senderIdNew.equals(senderIdOld)) {
                senderIdNew.getEmailCollection().add(email);
                senderIdNew = em.merge(senderIdNew);
            }
            for (Recipient recipientCollectionOldRecipient : recipientCollectionOld) {
                if (!recipientCollectionNew.contains(recipientCollectionOldRecipient)) {
                    recipientCollectionOldRecipient.getEmailCollection().remove(email);
                    recipientCollectionOldRecipient = em.merge(recipientCollectionOldRecipient);
                }
            }
            for (Recipient recipientCollectionNewRecipient : recipientCollectionNew) {
                if (!recipientCollectionOld.contains(recipientCollectionNewRecipient)) {
                    recipientCollectionNewRecipient.getEmailCollection().add(email);
                    recipientCollectionNewRecipient = em.merge(recipientCollectionNewRecipient);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = email.getId();
                if (findEmail(id) == null) {
                    throw new NonexistentEntityException("The email with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Email email;
            try {
                email = em.getReference(Email.class, id);
                email.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The email with id " + id + " no longer exists.", enfe);
            }
            Sender senderId = email.getSenderId();
            if (senderId != null) {
                senderId.getEmailCollection().remove(email);
                senderId = em.merge(senderId);
            }
            Collection<Recipient> recipientCollection = email.getRecipientCollection();
            for (Recipient recipientCollectionRecipient : recipientCollection) {
                recipientCollectionRecipient.getEmailCollection().remove(email);
                recipientCollectionRecipient = em.merge(recipientCollectionRecipient);
            }
            em.remove(email);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Email> findEmailEntities() {
        return findEmailEntities(true, -1, -1);
    }

    public List<Email> findEmailEntities(int maxResults, int firstResult) {
        return findEmailEntities(false, maxResults, firstResult);
    }

    private List<Email> findEmailEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Email.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Email findEmail(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Email.class, id);
        } finally {
            em.close();
        }
    }

    public int getEmailCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Email> rt = cq.from(Email.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
