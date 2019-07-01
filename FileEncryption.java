/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cipher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Date;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
/**
 *
 * @author Pratik Doshi
 * Contains methods used for encryption of files
 */
public class FileEncryption {
   
    
    private final int BUFFER_SIZE = 200000000; /*Measured in bytes. Magnitude
                                                 Arbitrarily decided on the basis
                                                 of observations made during runtime
                                                 on a 8th Generation Intel
                                                 processor with 8GB RAM.
                                                */ 
    private File SOURCE;
    private File TARGET;
    private EncryptionSupport ENC;
    
    /*Constructor to initialize object with a source file (one that has to be
    encrypted) and a password to use as key during such encryption*/
    FileEncryption(String sourceFile,String password)throws FileNotFoundException
    {
        this.SOURCE = new File(sourceFile);
        if(!SOURCE.exists())throw new FileNotFoundException("File not found");     
        ENC = new AESEncryptionSupport(password);
    }
    
    /*Used to assign the correct File object (one that represents the directory
    created to store encrypted data*/
    private void setTarget(String name){
        
        this.TARGET = new File(SOURCE.getParent()  + "\\" + 
                this.removeExtension(name,""));
        if(TARGET.exists()){
            TARGET =  new File(SOURCE.getParent()  + "\\" + 
                this.removeExtension(name,"1"));
            int i = 1;
            while(TARGET.exists()){
                i++;
                TARGET =  new File(SOURCE.getParent()  + "\\" + 
                this.removeExtension(name,Integer.toString(i)));
            }
        }
        
    }
    
    /*Overcomes the problem of file extensions. The method getName() in class
    File returns the name of the file along with an extension. This method is
    called because the encrypted data is stored in multiple files inside a 
    directory. If not called, there is a risk of naming a director with a file 
    extension. The suffix is required in cases when a directory with the same 
    name is already present in the target location*/ 
    private String removeExtension(String name,String suffix){
        int i;
        int len = name.length();
        for(i = len-1;i>=0;i--){
            if(name.charAt(i)=='.')break;
        }
        if(i<0) return name + suffix;
        return name.substring(0, i) + suffix + "_" + name.substring(i+1,len);
    }
    
    /*  Called to set up the necessary directories where encrypted data will
        be stored*/
    private void createFileSystem(String name){
        this.setTarget(name);
        this.TARGET.mkdir();
        this.createFileWithData(ENC.encrypt(
                SOURCE.getName().getBytes()), "_name");
    }
    
    /* Method that simplifies the process of creating files which will store
      encrypted data inside the main directory*/
    private void createFileWithData(byte [] b, String name){
        try {
            FileOutputStream fos = new FileOutputStream(new File(
                    this.TARGET.getAbsolutePath() + "\\" + name));
            fos.write(b);
            fos.close();
        } catch (FileNotFoundException ex) {
            
        } catch (IOException ex) {
            
        }
    }
    
    // Called when the source file that this object refers to has to be changed
    public void changeSourceFile(String sourceFile) throws FileNotFoundException{
  
        this.SOURCE = new File(sourceFile);
        if(!SOURCE.exists())throw new FileNotFoundException("File not found");
    }
    
    // Called when the password that this object refers to has to be changed
    public void changePassword(String password){
        ENC.changePassword(password);
    }
    
    /* Called to encrypt data in the file. The parameter name is used to define
     the name of the mian directory within which files containing encrypted data
     will be stored. If left blank, the directory will be named after the
     original source file.*/ 
    
    public void encrypt(String name, boolean deleteOriginal){
        if(name.equals("") || name==null)
            name = this.SOURCE.getName();
        
        try {
            this.createFileSystem(name);
            FileInputStream fis = new FileInputStream(this.SOURCE);
            
            int i = 1;
            int remaining = fis.available();
            while(remaining>0){
                byte [] buffer = new byte[Math.min(remaining, BUFFER_SIZE)];
                remaining -= fis.read(buffer);
                this.createFileWithData(ENC.encrypt(buffer),
                        "_" + Integer.toString(i));
                i++;
            }
            fis.close();
            if(deleteOriginal)SOURCE.delete();
            fis = null;
        } catch (FileNotFoundException ex) {
            /*Virtually never thrown because the validity of the file
             is already checked by the constructor*/  
        } catch (IOException ex) {
            //Instances of IOException already covered
        } 
        
    }

    //
    public static void main(String[] args) {
        try{
            FileEncryption enc = new FileEncryption("C:\\File1.txt","password");
            enc.encrypt("Encrypted File",false); 
            
        } catch (FileNotFoundException ex) {
            //code if source file doesnt exist
        }
    }
    
    
}
