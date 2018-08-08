/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mailsender.domain;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author aman
 */
@Entity
@Table(name = "Email", catalog = "mail", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Email.findAll", query = "SELECT e FROM Email e")
    , @NamedQuery(name = "Email.findById", query = "SELECT e FROM Email e WHERE e.id = :id")
    , @NamedQuery(name = "Email.findByMessage", query = "SELECT e FROM Email e WHERE e.message = :message")
    , @NamedQuery(name = "Email.findByDescription", query = "SELECT e FROM Email e WHERE e.description = :description")
    , @NamedQuery(name = "Email.findByDuration", query = "SELECT e FROM Email e WHERE e.duration = :duration")
    , @NamedQuery(name = "Email.findByNoAtatime", query = "SELECT e FROM Email e WHERE e.noAtatime = :noAtatime")})
public class Email implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "subject")
    private String subject;
    @Column(name = "message")
    private String message;
    @Column(name = "description")
    private String description;
    @Column(name = "duration")
    private Integer duration;
    @Column(name = "noAtatime")
    private Integer noAtatime;
    @JoinTable(name = "Email_Receipient", joinColumns = {
        @JoinColumn(name = "Email_id", referencedColumnName = "id")}, inverseJoinColumns = {
        @JoinColumn(name = "Recipient_id", referencedColumnName = "id")})
    @ManyToMany(fetch = FetchType.EAGER)
    private Collection<Recipient> recipientCollection;
    @JoinColumn(name = "sender_id", referencedColumnName = "id")
    @ManyToOne(optional = false,fetch = FetchType.EAGER)
    private Sender senderId;

    public Email() {
    }

    public Email(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getNoAtatime() {
        return noAtatime;
    }

    public void setNoAtatime(Integer noAtatime) {
        this.noAtatime = noAtatime;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    
    @XmlTransient
    public Collection<Recipient> getRecipientCollection() {
        return recipientCollection;
    }

    public void setRecipientCollection(Collection<Recipient> recipientCollection) {
        this.recipientCollection = recipientCollection;
    }

    public Sender getSenderId() {
        return senderId;
    }

    public void setSenderId(Sender senderId) {
        this.senderId = senderId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Email)) {
            return false;
        }
        Email other = (Email) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.mailsender.domain.Email[ id=" + id + " ]";
    }
    
}
