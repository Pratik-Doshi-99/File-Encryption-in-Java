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
import java.util.Scanner;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
/**
 *
 * @author Pratik Doshi
 * Contains methods that are used for decryption of files
 */
public class FileDecryption {
    
    private File SOURCE;
    private File TARGET;
    private EncryptionSupport DEC;
    
    //Constructor to instantiate the object with a source file (one to be 
    //decrypted) and a password
    FileDecryption(String sourceFile,String password)throws FileNotFoundException
    {
        this.SOURCE = new File(sourceFile);
        if(!SOURCE.exists())throw new FileNotFoundException("File not found");
        this.DEC = new AESEncryptionSupport(password);
    }
    
    //Method to change the source file
    public void changeSourceFile(String sourceFile) throws FileNotFoundException{
  
        this.SOURCE = new File(sourceFile);
        if(!SOURCE.exists())throw new FileNotFoundException("File not found");
    }
    
    //Method to change the password
    public void changePassword(String password){
        this.DEC.changePassword(password);
    }
    
    /*Method that opens the source file(directory), looks for the file that
      contains the name of the original file, decrypts it with the password
      and creates the Target file (one where the decrypted data is stored). This 
      is the point where the code checks whether the password is right. If the
      password is wrong, the BadPaddingException is thrown*/
    private void setTarget() throws BadPaddingException, TamperedFileException{
        
        try{
            FileInputStream fis = new FileInputStream(new File(
                    SOURCE.getAbsoluteFile() + "\\_name"));
            
            byte [] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            buffer = DEC.decrypt(buffer);
            TARGET = new File(SOURCE.getParent()  + "\\"
                    + this.getString(buffer));
            if(TARGET.exists()){
                TARGET = new File(SOURCE.getParent()  + "\\1"
                        + this.getString(buffer));
                int i = 1;
                while(TARGET.exists()){
                    i++;
                    TARGET = new File(SOURCE.getParent()  + "\\"
                            + Integer.toString(i) + this.getString(buffer));
                }
                
            }
            
        } catch (FileNotFoundException ex) {
            throw new TamperedFileException("File has been tampered with");
        } catch (IOException ex) {
            
        }
    }
    
    /*The method called to decrypt the file. Throws a WrongPassword Exception when
    it detects a BadPaddingException (when password is wrong) and throws a 
    TamperedFileException when the setTarget() method does not find the file 
    that contains the name of the original file, indicating a tampering
    with the main directory*/
    public void decrypt(boolean deleteEncrypted) throws WrongPasswordException, 
            TamperedFileException{
        FileOutputStream fos = null;
        FileInputStream fis = null;
        try{
            this.setTarget();
            fos = new FileOutputStream(TARGET);
            String basic_path = SOURCE.getAbsolutePath();
            File file = new File(basic_path + "\\_1");
            int i = 1;
            while(file.exists()) {
                fis = new FileInputStream(file);
                byte buffer[] = new byte[fis.available()];
                fis.read(buffer);
                buffer = DEC.decrypt(buffer);
                fos.write(buffer);
                fis.close();
                i++;
                file = new File(basic_path + "\\_" + Integer.toString(i));
            }
            fos.close();
            
            if(deleteEncrypted)SOURCE.delete();
            
        }catch (BadPaddingException ex){
            throw new WrongPasswordException("Wrong password");
            
        } catch (IOException ex) {

        }
    }
    
    //Converts a byte array into a String of text characters
    private static String getString(byte b[]){
        String str = "";
        for (byte c : b) {
            str += (char)c;
        }
        return str;
    }
    
    
    public static void main(String[] args) {
        try{
            FileDecryption enc = new FileDecryption("C:\\Users\\Pratik Doshi\\"
                    + "Videos\\Her","pratikdoshi99");
            enc.decrypt(true);
            
        } catch (FileNotFoundException ex) {
            //code if encrypted folder location is invalid
        } catch (WrongPasswordException ex){
            //code if entered password is wrong
        } catch (TamperedFileException ex) {
            //code if the encrypted file has been tampered with or the referenced
            //file is not one that was encrypted earlier
        }
        
    }    
}

class WrongPasswordException extends Exception{
    private String MESSAGE;
    WrongPasswordException(String message){
        this.MESSAGE = message;
    }
    
    @Override
    public String getMessage(){
        return MESSAGE;
    }
}

class TamperedFileException extends Exception{
    private String MESSAGE;
    TamperedFileException(String message){
        MESSAGE = message;
    } 
    
    @Override
    public String getMessage(){
        return MESSAGE;
    }
}
