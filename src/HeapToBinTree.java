/* 
 * HeapToBinTree.java
 *
 * data structure for Huffman encoding.
 *
 * <Pavan Kancherlapalli>, <kpavan05@gmail.com>
 * <10 Dec 2011>
 */ 


/*
 * This data stucture intially stores the items and frequencies in a  heap
 * similar to priority queue.While combining heap items heap is gradually converted
 * to Binary Tree.
*/
class HeapToBinTree{

  private class HeapItem{
       Object ch;
       int frequency;
       HeapItem left;
       HeapItem right;
       HeapItem parent;
       private HeapItem(Object o, int freq){
           ch = o;
           frequency = freq;
           left = null;
           right = null;
           parent = null;
       }
       private int compareTo(HeapItem other){
         
         if(other.frequency > frequency)
          return -1;
         else if(other.frequency < frequency)
          return 1;
         else 
         {
           Character ch1 = (Character)ch;
           Character ch2 = (Character)other.ch;
           if(ch1 != null && ch2 != null)
           {
             return ch1.compareTo(ch2);
           }
           else
             return 0;
         }
       }
  }
 
  /* 
   * Heap class is taken from examples folder of  cscie119 course
   */ 
  private class Heap{
        private HeapItem[]  contents;
        private int numItems;
        private Heap(int maxSize){  
          contents = new HeapItem[maxSize];
          numItems =0;
        }
        private int length(){
          return numItems;
        }
        private void siftUp(int i){
          HeapItem toSift = contents[i];

          // Find where the sifted element belongs.
          int child = i;
          while (child > 0) {
            int parent = (child - 1)/2;

            // Check if we're done.
            if (toSift.compareTo(contents[parent]) >= 0)
              break;

            // If not, move parent down and move up one level in the tree.
            contents[child] = contents[parent];
            child = parent;
          }
          contents[child] = toSift;
        }
        
        private void siftDown(int i){
          HeapItem toSift = contents[i];

          // Find where the sifted element belongs.
          int parent = i;
          int child = 2 * parent + 1;
          while (child < numItems) {
            // If the right child is bigger, compare with it.
            if (child < numItems - 1  &&  contents[child].compareTo(contents[child + 1]) > 0)
            child = child + 1;

          // Check if we're done.
          if (toSift.compareTo(contents[child]) <= 0)
            break;

            // If not, move child up and move down one level in the tree.
          contents[parent] = contents[child];
          parent = child;
          child = 2 * parent + 1;
          }
          contents[parent] = toSift;
        }
        
        private void insert(HeapItem item){
           if(numItems == contents.length)
              throw new ArrayIndexOutOfBoundsException();
           contents[numItems] = item;
           siftUp(numItems);
           numItems++;
        }
        
        private HeapItem remove(){
           HeapItem removed =  contents[0];
           contents[0] = contents[numItems-1];
           numItems--;
           siftDown(0);
           
           return removed;
        }
  }
  
  private Heap heap;
  private TreeIterator itr;
  public HeapToBinTree(int size){
     heap =new Heap(size);
     itr = null;
  }
  public void printHeap(){
    System.out.println();
    for(int i=0;i<heap.length();i++)
      System.out.print(heap.contents[i].ch+ ",");
  }
  public boolean convertHeapToTree(){
    // if there are less than 2 items, explicitly add items to heap to have atleast two
    // so that a tree can be formed.
    if(heap.length() ==0){
      HeapItem item1 = new HeapItem(null,0);
      heap.insert(item1);
      HeapItem item2 = new HeapItem(null,0);
      heap.insert(item2);
    }
    else if(heap.length() ==1){
      HeapItem item = new HeapItem(null,0);
      heap.insert(item);
    }
       
     while(combineHeapItemsToTreeNode()){
       printHeap();
     }
     System.out.println();
     System.out.println();
     if(heap.length() ==1) 
       return true;
     return false;
  }
  
  public HeapItem insertHeapItem(Object o, int freq){
      HeapItem item = new HeapItem(o,freq);
      heap.insert(item);
      return item;
  }
  
  /*
   * combining heap items one by one to form a tree
   */ 
  public boolean combineHeapItemsToTreeNode(){
      if(heap.length() <2)
        return false;
      
      HeapItem first = heap.remove();
      HeapItem second = heap.remove();

      if(second == null)return false;
          
      int freq = first.frequency + second.frequency;
      HeapItem item = insertHeapItem(null, freq);
      item.left = first;
      item.right = second;
      first.parent = item;
      second.parent = item;
      return true; 
  }
  
  public TreeIterator initIterator(){
    itr = new TreeIterator();
    return itr;
  }
  public void reInitIterator(){
    itr = null;
  }

  //need to init iterator before using this method
  public Object parseWithIterator(int n){
    if((n !=0 && n != 1) || itr == null) return null;
    HeapItem item = (HeapItem)itr.next(n);
    return item.ch;
  }
  
  /* 
   * iterator is used to move left, right, up along the binary tree.
   * iterator is declared public so that will be helpful while 
   * encoding and decoding of huffman tree.
   */ 
  public class TreeIterator{
      private HeapItem item;
      
      public TreeIterator(){
        item = heap.contents[0];
      }
      
      public Object left(){
        if(item != null)
        item = item.left;
        return item;
      }
      public Object right(){
        if(item != null)
        item = item.right;
        return item;
      }
      public Object up(){
        if(item != null)
        item= item.parent;
        return item;
      }
      public Object getItem(){
        Object ch = null;
        if(item !=null)
          ch = item.ch;
        return ch;
      }
      public boolean isLeaf(){
        if(item != null && item.right == null && item.left== null)
          return true;
        return false;
      }
      public Object next(int ich){
        if(item == null)
          return null;
        // if the item has no children means we reach the node which has the required object
        // and the iterator has to be pointed to root node for next parse.
        if(item.right == null && item.left== null){
             item = heap.contents[0];
        }
        if(ich ==1)
          item = item.right;
        else
          item = item.left;   
        return item;
      }     
  }
  
  /* This class is taken and modified from Linked tree.java. 
   * used to level order printing of huffman tree.
   */
  private class NodePlusDepth {
        private HeapItem node;
        private int depth;
        
        private NodePlusDepth(HeapItem node, int depth) {
            this.node = node;
            this.depth = depth;
        }
    }
    public void levelOrderPrint() {
        LLQueue<NodePlusDepth> q = new LLQueue<NodePlusDepth>();
        
        // Insert the root into the queue if the root is not null.
        if (heap.contents[0] != null)
            q.insert(new NodePlusDepth(heap.contents[0], 0));
        
        // We continue until the queue is empty.  At each step,
        // we remove an element from the queue, print its value,
        // and insert its children (if any) into the queue.
        // We also keep track of the current level, and add a newline
        // whenever we advance to a new level.
        int level = 0;
        while (!q.isEmpty()) {
            NodePlusDepth item = q.remove();
            
            if (item.depth > level) {
                System.out.println();
                level++;
            }
         
             System.out.print(item.node.ch +" , ");
            
            if (item.node.left != null)
                q.insert(new NodePlusDepth(item.node.left, item.depth + 1));
            if (item.node.right != null)
                q.insert(new NodePlusDepth(item.node.right, item.depth + 1));
        }
    }
}