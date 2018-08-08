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
import com.mycompany.mailsender.domain.Sender;
import com.mycompany.mailsender.service.exceptions.IllegalOrphanException;
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
public class SenderJpaController implements Serializable {

    public SenderJpaController() {
        this.emf = JPAUtil.emfactory;
    }
    private EntityManagerFactory emf = null;

    public EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    public void create(Sender sender) {
        if (sender.getEmailCollection() == null) {
            sender.setEmailCollection(new ArrayList<Email>());
        }
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Collection<Email> attachedEmailCollection = new ArrayList<Email>();
            for (Email emailCollectionEmailToAttach : sender.getEmailCollection()) {
                emailCollectionEmailToAttach = em.getReference(emailCollectionEmailToAttach.getClass(), emailCollectionEmailToAttach.getId());
                attachedEmailCollection.add(emailCollectionEmailToAttach);
            }
            sender.setEmailCollection(attachedEmailCollection);
            em.persist(sender);
            for (Email emailCollectionEmail : sender.getEmailCollection()) {
                Sender oldSenderIdOfEmailCollectionEmail = emailCollectionEmail.getSenderId();
                emailCollectionEmail.setSenderId(sender);
                emailCollectionEmail = em.merge(emailCollectionEmail);
                if (oldSenderIdOfEmailCollectionEmail != null) {
                    oldSenderIdOfEmailCollectionEmail.getEmailCollection().remove(emailCollectionEmail);
                    oldSenderIdOfEmailCollectionEmail = em.merge(oldSenderIdOfEmailCollectionEmail);
                }
            }
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Sender sender) throws IllegalOrphanException, NonexistentEntityException, Exception {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Sender persistentSender = em.find(Sender.class, sender.getId());
            Collection<Email> emailCollectionOld = persistentSender.getEmailCollection();
            Collection<Email> emailCollectionNew = sender.getEmailCollection();
            List<String> illegalOrphanMessages = null;
            for (Email emailCollectionOldEmail : emailCollectionOld) {
                if (!emailCollectionNew.contains(emailCollectionOldEmail)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Email " + emailCollectionOldEmail + " since its senderId field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Email> attachedEmailCollectionNew = new ArrayList<Email>();
            for (Email emailCollectionNewEmailToAttach : emailCollectionNew) {
                emailCollectionNewEmailToAttach = em.getReference(emailCollectionNewEmailToAttach.getClass(), emailCollectionNewEmailToAttach.getId());
                attachedEmailCollectionNew.add(emailCollectionNewEmailToAttach);
            }
            emailCollectionNew = attachedEmailCollectionNew;
            sender.setEmailCollection(emailCollectionNew);
            sender = em.merge(sender);
            for (Email emailCollectionNewEmail : emailCollectionNew) {
                if (!emailCollectionOld.contains(emailCollectionNewEmail)) {
                    Sender oldSenderIdOfEmailCollectionNewEmail = emailCollectionNewEmail.getSenderId();
                    emailCollectionNewEmail.setSenderId(sender);
                    emailCollectionNewEmail = em.merge(emailCollectionNewEmail);
                    if (oldSenderIdOfEmailCollectionNewEmail != null && !oldSenderIdOfEmailCollectionNewEmail.equals(sender)) {
                        oldSenderIdOfEmailCollectionNewEmail.getEmailCollection().remove(emailCollectionNewEmail);
                        oldSenderIdOfEmailCollectionNewEmail = em.merge(oldSenderIdOfEmailCollectionNewEmail);
                    }
                }
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = sender.getId();
                if (findSender(id) == null) {
                    throw new NonexistentEntityException("The sender with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException {
        EntityManager em = null;
        try {
            em = getEntityManager();
            em.getTransaction().begin();
            Sender sender;
            try {
                sender = em.getReference(Sender.class, id);
                sender.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The sender with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Email> emailCollectionOrphanCheck = sender.getEmailCollection();
            for (Email emailCollectionOrphanCheckEmail : emailCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Sender (" + sender + ") cannot be destroyed since the Email " + emailCollectionOrphanCheckEmail + " in its emailCollection field has a non-nullable senderId field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            em.remove(sender);
            em.getTransaction().commit();
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Sender> findSenderEntities() {
        return findSenderEntities(true, -1, -1);
    }

    public List<Sender> findSenderEntities(int maxResults, int firstResult) {
        return findSenderEntities(false, maxResults, firstResult);
    }

    private List<Sender> findSenderEntities(boolean all, int maxResults, int firstResult) {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Sender.class));
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

    public Sender findSender(Integer id) {
        EntityManager em = getEntityManager();
        try {
            return em.find(Sender.class, id);
        } finally {
            em.close();
        }
    }

    public int getSenderCount() {
        EntityManager em = getEntityManager();
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Sender> rt = cq.from(Sender.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
