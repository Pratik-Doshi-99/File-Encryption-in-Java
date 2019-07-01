/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cipher;

import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 *
 * @author Pratik Doshi
 * Interface implemented by classes encapsulating cryptographic algorithms
 */
public interface EncryptionSupport {
    public byte[] encrypt(byte[] b);
    public byte[] decrypt(byte[] b)throws BadPaddingException;
    public String getPassword();
    public void changePassword(String password);
}

/**
 *
 * @author Pratik Doshi
 * Implementation of the EncryptionSupport interface. Encapsulates the
 * AES (256 bit) algorithm
 * Made with reference to example provided by
 * Lokesh Gupta at https://howtodoinjava.com/security/java-aes-encryption-example/
 * 
 */
class AESEncryptionSupport implements EncryptionSupport{
    private String password;
    
    AESEncryptionSupport(String password){
        this.password = password;
    }
    
    @Override
    public String getPassword(){
        return password;
    }
    
    @Override
    public void changePassword(String password){
        this.password = password;
    }
    
    @Override
    public byte[] encrypt(byte b[]){
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.ENCRYPT_MODE, this.setKey());
            
            b = c.doFinal(b);
            
        } catch (NoSuchAlgorithmException ex) {
            
        } catch (NoSuchPaddingException ex) {
            
        } catch (InvalidKeyException ex) {
            
        } catch (IllegalBlockSizeException ex) {
            
        } catch (BadPaddingException ex) {
            
        }
        return b;
    }
    
    @Override
    public byte[] decrypt(byte[] b)throws BadPaddingException{
        try {
            Cipher c = Cipher.getInstance("AES");
            c.init(Cipher.DECRYPT_MODE, this.setKey());
            b = c.doFinal(b);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | 
                IllegalBlockSizeException | InvalidKeyException ex) {
           
        } catch (BadPaddingException ex) {
            throw new BadPaddingException("Wrong password");
        }
        return b;
        
    }
    
    private SecretKeySpec setKey(){
        byte [] k = password.getBytes();
        SecretKeySpec key = null;
        try {
            k= MessageDigest.getInstance("SHA-1").digest(k);
            k = Arrays.copyOf(k,16);
            key = new SecretKeySpec(k,"AES");
        } catch (NoSuchAlgorithmException ex) {
            
        }
        return key;
            
    }
}

