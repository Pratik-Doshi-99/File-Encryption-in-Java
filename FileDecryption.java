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

/*Earlier Algorithm

class FileDecryption2{
    private File SOURCE;
    private File TARGET;
    private String CODE;
    
    
    private FileOutputStream FOS;
    private Scanner FIS;
    
    //
    FileDecryption2(String sourceFile,String password)throws FileNotFoundException
    {
        this.SOURCE = new File(sourceFile);
        if(!SOURCE.exists())throw new FileNotFoundException("File not found");
        this.TARGET = new File(SOURCE.getParent() + "\\" +  
                this.retrieveExtension(SOURCE.getName()));
        this.CODE = password;
    }
    
    //
    public void changeSourceFile(String sourceFile) throws FileNotFoundException{
  
        this.SOURCE = new File(sourceFile);
        if(!SOURCE.exists())throw new FileNotFoundException("File not found");
        this.TARGET = new File(SOURCE.getParent()  + "\\" + 
                this.retrieveExtension(SOURCE.getName()));
    }
    
    //
    public void changePassword(String password){
        this.CODE = password; 
    }
    
    //
    public void decrypt(boolean deleteEncrypted){
        try{
            FIS = new Scanner(this.SOURCE);
            //FIS.useDelimiter(FileEncryption.DELIMITER);
            FOS = new FileOutputStream(this.TARGET);
            
            Cipher c = Cipher.getInstance("AES");
            byte [] k = this.CODE.getBytes("UTF-8");
            k= MessageDigest.getInstance("SHA-1").digest(k);
            k = Arrays.copyOf(k,16);
            SecretKeySpec key = new SecretKeySpec(k,"AES");
            c.init(Cipher.DECRYPT_MODE, key);
            System.out.println(FIS.hasNext() + "\n");
            boolean check;
            while((check = FIS.hasNext())){
                byte[] data = c.doFinal(FIS.next().getBytes());
                System.out.println(data[0] + " " + data[1]);
                System.out.println(check);
                FOS.write(data);
                
            }
            
            FOS.close();
            FIS.close();
            if(deleteEncrypted)SOURCE.delete();
            
        }catch(IOException e){
            System.out.println(e.getMessage());
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
        } catch (NoSuchPaddingException ex) {
            System.out.println(ex.getMessage());
        } catch (InvalidKeyException ex) {
            System.out.println(ex.getMessage());
            System.out.println("Wrong password");
        } catch (IllegalBlockSizeException ex) {
            System.out.println(ex.getMessage());
        } catch (BadPaddingException ex) {
            System.out.println(ex.getMessage());
        } finally {
            try {
                FOS.close();
                FIS.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
                System.out.println("IOException");
            } catch (NullPointerException ex) {
                System.out.println(ex.getMessage());
            }
            
        }
    }
    
    //
    private String retrieveExtension(String name){
        int i;
        int len = name.length();
        for(i = len-1;i>=0;i--){
            if(name.charAt(i)=='_')break;
        }
        if(i<0) return name;
        return name.substring(0, i) + "." + name.substring(i+1,len);
    }
    
    public static void main(String[] args) {
        /*try{
            FileDecryption enc = new FileDecryption("C:\\Users\\Pratik Doshi\\"
                    + "Desktop\\New Text Document_txt","password");
            enc.decrypt(false);
            /*enc.changePassword("pratik");
            enc.changeSourceFile("C:\\Users\\Pratik Doshi\\"
                    + "Videos\\Too.Big.To.Fail.2011.720p.BluRay-@iMediaShare"
                    + " - Copy_mp4");
            enc.decrypt(true);
            
        } catch (FileNotFoundException ex) {
            System.out.println(ex.getMessage());
        }
        
    }
}*/



