/* 
 * Huff.java
 *
 * A program that compresses a file using Huffman encoding.
 *
 * <Pavan Kancherlapalli>, <kpavan05@gmail.com>
 * <10 Dec 2011>
 */ 

import java.io.*;


public class Huff {

    /* Put any methods that you add here. */
    private int[] frequencies;
    private Code[] codearr;
    private HeapToBinTree tree;
    private HeapToBinTree.TreeIterator itr;
    public static final int ASCII_LEN = 257;
    public int nBytes =0;
    public Huff(){   
      frequencies = new int[ASCII_LEN];
      tree = null;
      codearr = null;
      
      for(int i=0; i<ASCII_LEN; i++){
        frequencies[i] =0;
      }   
    }
    
    public void getFrequencies(String file){
       try{  
         FileReader in = new FileReader(file);
         int ich = in.read();
         while(ich != -1){
           frequencies[ich] = frequencies[ich] +1;
           ich = in.read();
         }
         frequencies[256] = frequencies[256] +1;
         in.close();
       }catch(Exception e){
         System.out.println("Exception: " + e);
       }
      }
    
    public boolean createTreeAndHeader(ObjectOutputStream out){
      boolean bret = false;
      try{
        int frequency = 0;
        Character c = null;
        char ch;
        tree = new HeapToBinTree(ASCII_LEN);
      
        for(int i=0;i<ASCII_LEN;i++){
          frequency = frequencies[i];
          out.writeInt(frequency);
          nBytes +=4;
          if(frequency >0){
             ch = (char)i;
             c = new Character(ch);
             tree.insertHeapItem(c,frequency);
             bret = true;
          }
        }
        bret =  tree.convertHeapToTree();
        tree.levelOrderPrint();
      }catch(Exception e){
        return false;
      }
      return bret;
    }
     
    public void buildEncoding(){
      codearr = new Code[ASCII_LEN];
      
      Code encode = new Code();      
      itr = tree.initIterator();
      buildCodeRecursive(encode,0,codearr);
    }
    private void buildCodeRecursive(Code encode,int nway,Code[] arr){
      if(itr == null)
        return;
      
      if(nway == -1){
        encode.addBit(0);
        itr.left();
      }
      if(nway == 1){
        encode.addBit(1);
        itr.right();
      }
      if(itr.isLeaf()){
        if(itr.getItem()== null)
          return;
        int ich =((Character)itr.getItem()).charValue();
        Code code = new Code(encode);
        arr[ich]= code;
        return;
      }
      buildCodeRecursive(encode,-1,arr);
      encode.removeBit();
      itr.up();
      buildCodeRecursive(encode,1,arr);
      encode.removeBit();
      itr.up();
    }
    
    public void compress(FileReader in, BitWriter out){
      try{
       int ich = in.read();
       int nbits =0;
       boolean bComplete = true;
        while(ich != -1){  
          Code encode = codearr[ich];
          out.writeCode(encode);
          nbits += encode.length();
          ich = in.read();
        }
        if(nbits %8 >0)
          bComplete= false;
        
        //write EOF so that it will help while decompression where to stopping
        out.writeCode(codearr[256]);
        
        //flush the bits that haven't reached byte boundary
        if(!bComplete)
          out.flushBits();
      }catch(Exception e){
        return;
      }
    }
    /** 
     * main method for compression.  Takes command line arguments. 
     * To use, type: java Huff input-file-name output-file-name 
     * at the command-line prompt. 
     */ 
    public static void main(String[] args) throws IOException {
        FileReader in = null;               // reads in the original file
        ObjectOutputStream out = null;      // writes out the compressed file

        // Check for the file names on the command line.
        if (args.length != 2) {
            System.out.println("Usage: java Huff <in fname> <out fname>");
            System.exit(1);
        }

        // Open the input file.
        try {
            in = new FileReader(args[0]);
        } catch (FileNotFoundException e) {
            System.out.println("Can't open file " + args[0]);
            System.exit(1);
        }

        
        // Open the output file.
        try {
            out = new ObjectOutputStream(new FileOutputStream(args[1]));
        } catch (FileNotFoundException e) {
            System.out.println("Can't open file " + args[1]);
            System.exit(1);
        }
        
        Huff hf = new Huff();
        hf.getFrequencies(args[0]);
        hf.createTreeAndHeader(out);
        
        // Create a BitWriter that is able to write to the compressed file.
        BitWriter writer = new BitWriter(out);

        /****** Add your code below. ******/
        /* 
         * Note: after you read the input file once, you will need
         * to reopen it in order to read through the file
         * a second time.
         */
        hf.buildEncoding();
        hf.compress(in,writer);
        hf.nBytes = hf.nBytes + writer.getNumBytesWritten();
        
        System.out.println("Number of bytes written " + hf.nBytes);
        /* Leave these lines at the end of the method. */
        in.close();
        out.close();
    }
}
