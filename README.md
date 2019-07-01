# File-Encryption-in-Java
Contains java classes that can be used to encrypt and decrypt files.
The classes can be used to encrypt individual files only. The methods 
create a folder in the same directory as the referenced file. This folder
further contains files that store encrypted data from the referenced files.
The decryption process works when the encrypted folder is referenced.
The code also allows the user to:
  1) Assign a different name to the encrypted directory, the original name
     being restored during decryption
  2) Encrypt the same file with multiple passwords, with multiple directories
     being created and their naming scheme auto-adjusted.

Reference from article by Mr Lokesh Gupta: https://howtodoinjava.com/security/java-aes-encryption-example/
