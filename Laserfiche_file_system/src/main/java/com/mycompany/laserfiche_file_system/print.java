package com.mycompany.laserfiche_file_system;


import com.laserfiche.api.client.model.AccessKey;
import com.laserfiche.repository.api.RepositoryApiClient;
import com.laserfiche.repository.api.RepositoryApiClientImpl;
import com.laserfiche.repository.api.clients.impl.model.Entry;
import com.laserfiche.repository.api.clients.impl.model.EntryType;
import com.laserfiche.repository.api.clients.impl.model.Folder;
import com.laserfiche.repository.api.clients.impl.model.ODataValueContextOfIListOfEntry;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.processing.FilerException;

import java.io.*;




public class print {

    //get access to the API
    public static String servicePrincipalKey = "5w-5Sbp5T2eyBsduFo-g";
    public static String accessKeyBase64 = "ewoJImN1c3RvbWVySWQiOiAiMTQwMTM1OTIzOCIsCgkiY2xpZW50SWQiOiAiMDcwYzllYTYtMzQwZS00ODdmLTlmNzItM2YyNjQ0NWNkZWZmIiwKCSJkb21haW4iOiAibGFzZXJmaWNoZS5jYSIsCgkiandrIjogewoJCSJrdHkiOiAiRUMiLAoJCSJjcnYiOiAiUC0yNTYiLAoJCSJ1c2UiOiAic2lnIiwKCQkia2lkIjogInlpUkFKeGZ0eVpZVXk1TFBFYUhmTF9MRS03RWZjSW5nQ3NCVGtxa09yb28iLAoJCSJ4IjogImJ2ZmpDQU9acUdYeVhLdXNELUpEdFkzRVhwNms5WWtTOFZWYzRicER2OFEiLAoJCSJ5IjogInRmYXBLSDc4Qm45LUp5aVZQeDRrQWVDZFlqSjN6RWxHLVZGeU9lS0dNUUEiLAoJCSJkIjogIlNXei1kTUI1bTktWWtkNFJiLVFyMllYbE9BVlpYV0loV3hxVi1QTkFBWEkiLAoJCSJpYXQiOiAxNjc3Mjk3NzE1Cgl9Cn0=";
    public static String repositoryId = "r-0001d410ba56";
            
    public static AccessKey accessKey = AccessKey.createFromBase64EncodedAccessKey(accessKeyBase64);

    public static RepositoryApiClient client = RepositoryApiClientImpl.createFromAccessKey(
            servicePrincipalKey, accessKey);


    
    public static List <Entry> printlocalentry(List <Entry> x){ //receives a local directory and prints requirements 

        //check if the entry is a file or a folder 

        for(Entry thisentry:x){ //increment through the list of entries ot get each individual entry's information 
            File check=new File(thisentry.getFullPath()); //create a file with the path of the entry 

            if(check.isFile() || check.isDirectory()){ //if it is a file or directory 
                System.out.println("Name: "+thisentry.getName()+"....absolute path: "+thisentry.getFullPath());

                //if it's a folder, print the size of 0
                if(check.isDirectory()){
                    System.out.print("...length: 0 bytes");
                }
                else{ //if it's a folder, use the length function to get teh 
                    System.out.println("...length: "+check.length()+" bytes");
                }
                System.out.println("\n"); //space out each entry's info
            }
        }

        return x;//return teh list of entries 
    }
    

    public static List <Entry> printremoteentry(List <Entry> x){ //receives a file from the API, prints the relevant information

        for(Entry thisentry: x){
            System.out.println("Entry ID: "+thisentry.getId()+"....Name: "+thisentry.getName()+"....absolute path: "+thisentry.getFullPath());

            if(!(thisentry.getEntryType().toString()).equals("Folder")){ //checks if the entry is not a folder (directory), it's gonna read the length
                System.out.println("...length: "+remotelength(thisentry.getId()));
            }

            else{ //if the entry is a folder (directory), it returns a length of 0
            System.out.println("...length :"+0);
            }
        }

        return x; //return the list of entries passed in
    }
    
    
    public static long remotelength(int id){ //receives the id to find specific file and get its length  
        
        
        // Download an ENTRY 
          int entryIdToDownload = id;

        //create an empty file with the same pathname that the downloaded file. This variable is made so you can get length 
        File temp=new File("DownloadFile.txt"); //ENTER PATHNAME OF FILE BEING READ HERE  

          final String FILE_NAME = "DownloadFile.txt"; //ENTER PATHNAME HERE TOO
          Consumer<InputStream> consumer = inputStream -> {
              File exportedFile = new File(FILE_NAME);

              try (FileOutputStream outputStream = new FileOutputStream(exportedFile)) {
                  byte[] buffer = new byte[1024];
                  while (true) {
                      int length = inputStream.read(buffer);
                      if (length == -1) {
                          break;
                      }
                      outputStream.write(buffer, 0, length);
                  }
              } catch (Exception e) {
                  e.printStackTrace();
              } finally {
                  try {
                      inputStream.close();
                  } catch (IOException e) {
                      e.printStackTrace();
                  }
              }
          };


     
        
        //gets an entry object and its information
        client.getEntriesClient()
                .exportDocument(repositoryId, entryIdToDownload, null, consumer)
                .join();
        client.close(); //close the file


        long LengthOfFile= temp.length();
        return LengthOfFile;

    }
    
}
