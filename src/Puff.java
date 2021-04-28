/* 
 * Puff.java
 *
 * A program that decompresses a file that was compressed using 
 * Huffman encoding.
 *
 * <Pavan Kancherlapalli>, <kpavan05@gmail.com>
 * <10 Dec 2011>
 */ 

import java.io.*;

public class Puff {

    /* Put any methods that you add here. */
    private int[] frequencies;
    private Code[] codearr;
    private HeapToBinTree tree;
    public static final int ASCII_LEN = 257;

    public Puff(){   
      frequencies = new int[ASCII_LEN];
      tree = null;
      codearr = null;
      
      for(int i=0; i<ASCII_LEN; i++){
        frequencies[i] =0;
      }   
    }
    
    public void getFrequencies(ObjectInputStream in){
       try{  
         for(int i=0; i<ASCII_LEN; i++){
             frequencies[i] = in.readInt();
         }
       }catch(Exception e){
         System.out.println("Exception: " + e);
       }
      }
    
    public boolean createTree(){ 
      boolean bret = false;
      try{
        int frequency = 0;
        Character c = null;
        char ch;
        tree = new HeapToBinTree(ASCII_LEN);
        
        for(int i=0;i<ASCII_LEN;i++){
          frequency = frequencies[i];
          if(frequency >0){
            ch = (char)i;
            c = new Character(ch);
            tree.insertHeapItem(c,frequency);
          }
        }
        bret =  tree.convertHeapToTree();
        tree.levelOrderPrint();
      }catch(Exception e){
        return false;
      }
      return bret;
    }
    
    
    public void unCompress(BitReader in,FileWriter out){
      try{
        tree.initIterator();
        int ich = in.getBit();
        Character ch = null;  
        while(ich != -1){
          ch = (Character)tree.parseWithIterator(ich);
          if(ch != null ){
            char c = ch.charValue();
            // if it is EOF then we have no more bits to read. extra bits are just
            //buffered bits we can safely ignore.
            if(c == 256)
              break;
            out.write(ch);
          }
          ich = in.getBit();
        }
        tree.reInitIterator();
      }catch(Exception e){
        return;
      }
    }
    /** 
     * main method for decompression.  Takes command line arguments. 
     * To use, type: java Puff input-file-name output-file-name 
     * at the command-line prompt. 
     */ 
    public static void main(String[] args) throws IOException {
        ObjectInputStream in = null;      // reads in the compressed file
        FileWriter out = null;            // writes out the decompressed file

        // Check for the file names on the command line.
        if (args.length != 2) {
            System.out.println("Usage: java Puff <in fname> <out fname>");
            System.exit(1);
        }

        // Open the input file.
        try {
            in = new ObjectInputStream(new FileInputStream(args[0]));
        } catch (FileNotFoundException e) {
            System.out.println("Can't open file " + args[0]);
            System.exit(1);
        }

        // Open the output file.
        try {
            out = new FileWriter(args[1]);
        } catch (FileNotFoundException e) {
            System.out.println("Can't open file " + args[1]);
            System.exit(1);
        }
    
        Puff pf = new Puff();
        pf.getFrequencies(in);
        // Create a BitReader that is able to read the compressed file.
        BitReader reader = new BitReader(in);


        /****** Add your code here. ******/
        pf.createTree();
        pf.unCompress(reader,out);
        
        /* Leave these lines at the end of the method. */
        in.close();
        out.close();
    }
}
