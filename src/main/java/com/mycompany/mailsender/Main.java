/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.mailsender;

import com.mycompany.mailsender.view.MainFrame;



/**
 *
 * @author aman
 */
public class Main {

    public static void main(String[] args) throws ClassNotFoundException {

        Class.forName("com.mycompany.mailsender.util.JPAUtil");
        new MainFrame().setVisible(true);

    }
}
