/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mailsender.domain;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

/**
 *
 * @author aman
 */
@Entity
@Table(name = "sender", catalog = "mail", schema = "")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Sender.findAll", query = "SELECT s FROM Sender s")
    , @NamedQuery(name = "Sender.findById", query = "SELECT s FROM Sender s WHERE s.id = :id")
    , @NamedQuery(name = "Sender.findByEmailAddress", query = "SELECT s FROM Sender s WHERE s.emailAddress = :emailAddress")
    , @NamedQuery(name = "Sender.findByDescription", query = "SELECT s FROM Sender s WHERE s.description = :description")})
public class Sender implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Column(name = "email_address")
    private String emailAddress;
    @Column(name = "description")
    private String description;
    @OneToMany(fetch = FetchType.EAGER ,cascade = CascadeType.ALL, mappedBy = "senderId")
    private Collection<Email> emailCollection;

    public Sender() {
    }

    public Sender(Integer id) {
        this.id = id;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @XmlTransient
    public Collection<Email> getEmailCollection() {
        return emailCollection;
    }

    public void setEmailCollection(Collection<Email> emailCollection) {
        this.emailCollection = emailCollection;
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
        if (!(object instanceof Sender)) {
            return false;
        }
        Sender other = (Sender) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.mycompany.mailsender.domain.Sender[ id=" + id + " ]";
    }
    
}
