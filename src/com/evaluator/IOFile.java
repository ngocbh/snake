/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.evaluator;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author Admin
 */
public class IOFile {
    public String line;
    
    public void Write(int numOR , float score, double time, float step, String urlFile) {
//                             điểm , thời gian, số bước đi
        try {
            File f = new File(urlFile);    
            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(numOR + "\n" + score + "\n" + time + "\n" + step);
            
            bw.close();
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }
    
    public String ReadNumOR(String urlFile) {
        try {
            File f = new File(urlFile);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            line = br.readLine();
            fr.close();
            br.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return line;
    }
    
    public String ReadScore(String urlFile) {
        try {
            File f = new File(urlFile);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            line = br.readLine();
            line = br.readLine();
            fr.close();
            br.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return line;
    }
    
    public String ReadTime(String urlFile) {
        try {
            File f = new File(urlFile);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            line = br.readLine();
            line = br.readLine();
            line = br.readLine();
            fr.close();
            br.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return line;
    }
    
    public String ReadStep(String urlFile) {
        try {
            File f = new File(urlFile);
            FileReader fr = new FileReader(f);
            BufferedReader br = new BufferedReader(fr);
            line = br.readLine();
            line = br.readLine();
            line = br.readLine();
            line = br.readLine();
            fr.close();
            br.close();
        } catch (FileNotFoundException ex) {
            System.out.println(ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
        return line;
    }
}
