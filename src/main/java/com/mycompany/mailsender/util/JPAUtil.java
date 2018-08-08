/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mailsender.util;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author aman
 */
public class JPAUtil {

    
    public static EntityManagerFactory emfactory = Persistence.createEntityManagerFactory("com.mycompany_mailsender_jar_1.0-SNAPSHOTPU2");

    public static void close() {
        emfactory.close();
    }
}
