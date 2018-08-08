/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mailsender.service;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import com.mycompany.mailsender.domain.Email;
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
public class RecipientJpaController implements Serializable {

    public RecipientJpaController() {
       this.emf = JPAUtil.emfactory;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Recipient recipient) {
        if (recipient.getEmailCollection() == null) {
            recipient.setEmailCollection(new ArrayList<Email>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Email> attachedEmailCollection = new ArrayList<Email>();
            for (Email emailCollectionEmailToAttach : recipient.getEmailCollection()) {
                emailCollectionEmailToAttach = em.getReference(emailCollectionEmailToAttach.getClass(), emailCollectionEmailToAttach.getId());
                attachedEmailCollection.add(emailCollectionEmailToAttach);
            }
            recipient.setEmailCollection(attachedEmailCollection);
            em.persist(recipient);
            for (Email emailCollectionEmail : recipient.getEmailCollection()) {
                emailCollectionEmail.getRecipientCollection().add(recipient);
                emailCollectionEmail = em.merge(emailCollectionEmail);
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Recipient recipient) throws NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Recipient persistentRecipient = em.find(Recipient.class, recipient.getId());
            Collection<Email> emailCollectionOld = persistentRecipient.getEmailCollection();
            Collection<Email> emailCollectionNew = recipient.getEmailCollection();
            Collection<Email> attachedEmailCollectionNew = new ArrayList<Email>();
            for (Email emailCollectionNewEmailToAttach : emailCollectionNew) {
                emailCollectionNewEmailToAttach = em.getReference(emailCollectionNewEmailToAttach.getClass(), emailCollectionNewEmailToAttach.getId());
                attachedEmailCollectionNew.add(emailCollectionNewEmailToAttach);
            }
            emailCollectionNew = attachedEmailCollectionNew;
            recipient.setEmailCollection(emailCollectionNew);
            recipient = em.merge(recipient);
            for (Email emailCollectionOldEmail : emailCollectionOld) {
                if (!emailCollectionNew.contains(emailCollectionOldEmail)) {
                    emailCollectionOldEmail.getRecipientCollection().remove(recipient);
                    emailCollectionOldEmail = em.merge(emailCollectionOldEmail);
                }
            }
            for (Email emailCollectionNewEmail : emailCollectionNew) {
                if (!emailCollectionOld.contains(emailCollectionNewEmail)) {
                    emailCollectionNewEmail.getRecipientCollection().add(recipient);
                    emailCollectionNewEmail = em.merge(emailCollectionNewEmail);
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = recipient.getId();
                if (findRecipient(id) == null) {
                    throw new NonexistentEntityException("The recipient with id " + id + " no longer exists.");
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
            Recipient recipient;
            try {
                recipient = em.getReference(Recipient.class, id);
                recipient.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The recipient with id " + id + " no longer exists.", enfe);
            }
            Collection<Email> emailCollection = recipient.getEmailCollection();
            for (Email emailCollectionEmail : emailCollection) {
                emailCollectionEmail.getRecipientCollection().remove(recipient);
                emailCollectionEmail = em.merge(emailCollectionEmail);
            }
            em.remove(recipient);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Recipient> findRecipientEntities() {
        return findRecipientEntities(true, -1, -1);
    }

    public List<Recipient> findRecipientEntities(int maxResults, int firstResult) {
        return findRecipientEntities(false, maxResults, firstResult);
    }

    private List<Recipient> findRecipientEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Recipient.class));
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

    public Recipient findRecipient(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Recipient.class, id);
        } finally {
            em.close();
        }
    }

    public int getRecipientCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Recipient> rt = cq.from(Recipient.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
