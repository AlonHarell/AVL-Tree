import java.util.LinkedList;
import java.util.List;


/**
 *
 * AVLTree
 *
 * An implementation of a AVL Tree with
 * distinct integer keys and info
 *
 */
// Alon Harell, Username alonharell, ID 318509403
// Yaron Gitelman, Username yaron1, ID 211673496
public class AVLTree {
	
	public IAVLNode Ext = new AVLNode(); //Will represent external leaves for the tree.
	
	public int size;
	public IAVLNode root;
	public IAVLNode maxNode;
	public IAVLNode minNode;
	
	public AVLTree()
	{
		Ext.setHeight(-1); //Initialization settings for external nodes, all in O(1)
		((AVLNode)Ext).setSize(0);
	}
	

  /**
   * public boolean empty()
   *
   * returns true if and only if the tree is empty
   *
   */
	public boolean empty() {
		return (this.root == null); //Tree is empty iff it has no root. Accessing field in O(1)
	}

 /**
   * public String search(int k)
   *
   * returns the info of an item with key k if it exists in the tree
   * otherwise, returns null
   */
  public String search(int k)
  { 
	  if(this.empty())
	  {
		  return null;
	  }
	  
	  //Returns the value of the found node. If found an external node, will return null.
	  IAVLNode node = searchFromNode(k,this.getRoot()); //O(log n), see SearchFromNode
	  if (node.isRealNode()) //Found a real node
	  {
		  return node.getValue(); //O(1)
	  }
	  else //Did not find the key
	  {
		  return null;
	  }
	  
	 //Total: O(log n)
  }
  
  
  public IAVLNode searchFromNode(int key, IAVLNode node)
  {
	  //Recursive algorithm:
	  if ((!node.isRealNode()) || (node.getKey() == key)) 
	  {
		  //If key found returns it. If reached External leaf, key not in the tree, will return Ext
		  return node;
	  }
	  
	  //For each node, utilizing BST's definition:
	  if (node.getKey() < key) 
	  {
		  return this.searchFromNode(key, node.getRight()); //If searched key is greater, then should be in the right subtree
	  }
	  else 
	  {
		  return this.searchFromNode(key, node.getLeft()); //If smaller, should be in the left subtree
	  }
	  
	  //Each iterations goes to a node's child. At most, iterations as AVL tree's height, which is O(logn)
	  
  }
  

  /**
   * public int insert(int k, String i)
   *
   * inserts an item with key k and info i to the AVL tree.
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
   * promotion/rotation - counted as one rebalnce operation, double-rotation is counted as 2.
   * returns -1 if an item with key k already exists in the tree.
   */
   public int insert(int k, String i) {
	   
	   //Creating the new node to be insterted. All initializations in O(1)
	   IAVLNode toInsert = new AVLNode();
	   ((AVLNode)toInsert).setKey(k);
	   ((AVLNode)toInsert).setValue(i);
	   toInsert.setHeight(0); //Always inserted as a leaf, even if it's also a root
	   
	   int rebalanceCount = insertNode(toInsert); //Actual insertion, worst case O(log n)
	   
	   return rebalanceCount;
   } //Total: O(log n)
   
   public int insertNode(IAVLNode toInsert)
   {
	   int k = toInsert.getKey(); //O(1)
	   int rebalanceCount = 0;
	   if (this.empty()) //O(1),  if tree is empty, will be inserted as only node
	   {
		   this.root = toInsert; 
	   }
	   else //If tree isn't empty
	   {
		   IAVLNode pos = findInsertPosition(this.getRoot(), k); //Finding insertion position, equivalent to search so O(log n)
		   if (pos.getKey() == k) // key already in tree
		   {
			   return -1;
		   }
		   
		   //The actual insertion
		   toInsert.setParent(pos);
		   if (pos.getKey() > toInsert.getKey()) //Insert as right or left child, according to key
		   {
			   pos.setLeft(toInsert);
		   }
		   else
		   {
			   pos.setRight(toInsert);
		   }
		   
		   IAVLNode x = toInsert;
		   ((AVLNode)x).updateSize(); //O(1), calculates according to fields
		   while(x != null) //Go up in the tree, rebalance and adjust heights and sizes
		   {
			   IAVLNode z = x.getParent();
			   if (z != null)
			   {
				   if (z.getHeight() - x.getHeight() == 0) //Case A, need to rebalance: Rank difference between x and parent is 0
				   {
					   if (z.getLeft() == x) //Check wether x is left or right child, and rebalance accordingly (symmetrical)
					   {
						   IAVLNode y = z.getRight(); //z's other child
						   switch(Math.abs(z.getHeight() - y.getHeight()))//Checks rank differences between z and right child y
						   {	
						   		case 1: //Case 1 from lecture
						   		{
						   			((AVLNode)z).promote(); //All height(rank) changes done in O(1)
						   			rebalanceCount+=1;
						   			break;
						   		}
						   		
						   		case 2: //Case 2 or 3 from lecture
						   		{
						   			if (Math.abs(x.getHeight() - x.getRight().getHeight()) == 2) //Single rotation required
						   			{
						   				this.rotateRight(z); //All rotations done in O(1)
						   				((AVLNode)z).demote();
						   				rebalanceCount+=2;
						   			}
						   			else //Double rotation required
						   			{
						   				this.rotateLeft(x);
						   				this.rotateRight(z);
						   				((AVLNode)x).demote();
						   				((AVLNode)z).demote();
						   				IAVLNode b = x.getParent();
						   				((AVLNode)b).promote();
						   				rebalanceCount+=5;
						   				
						   			}
						   			break;
						   		}
						   } 
					   }
					   else //x is a right child
					   {
						   //Same as the above if block, but mirrored
						   IAVLNode y = z.getLeft();
						   
						   switch(Math.abs(z.getHeight() - y.getHeight()))//Checks rank differences between z and left child y
						   {	
						   		case 1: //Case 1
						   		{
						   			((AVLNode)z).promote();
						   			rebalanceCount+=1;
						   			break;
						   		}
						   		
						   		case 2: //Case 2 or 3
						   		{
						   			if (Math.abs(x.getHeight() - x.getLeft().getHeight()) == 2) //Single rotation required
						   			{
						   				this.rotateLeft(z);
						   				((AVLNode)z).demote();
						   				rebalanceCount+=2;
						   			}
						   			else //Double rotation required
						   			{
						   				this.rotateRight(x);
						   				this.rotateLeft(z);
						   				((AVLNode)x).demote();
						   				((AVLNode)z).demote();
						   				IAVLNode b = x.getParent();
						   				((AVLNode)b).promote();
						   				rebalanceCount+=5;
						   			}
						   			break;
						   		}
						   }

						   
					   }

				   }
				   else
				   {
					   //Case B, No need to rebalance.
					  
				   }
				   
			   }
			   else //z is null
			   {
				   //Reached root, no need for rebalance
				   
			   }
			   
			   ((AVLNode)x).updateSize();  //Update x's subtree size, since change (because of insertion and perhaps rebalance)
			   if (x.getParent() == null) //Check if x should be assigned as the Tree's root
			   {
				   this.root = x; //Accessing field in constant time
			   }
			   
			   x = z;
			   //Go up in the tree, redo process. Even if z is currently not x's parent due to rotation, no further rebalance will be needed
			   // since rotations are terminal operations. Won't happen again for the same reason.
		   }

	   }
	   //Whole loop above does at most O(log n) iterations (goes from x up to the root, maybe 1 more iteration because of rotation)
	   //Each iteration operates in constant time, so loop's complexity is O(log n)

	   this.size = this.size + 1; //A node was inserted
	   this.UpdateMinMax(); //Update min, max in tree in case the new node changes them
	   return rebalanceCount;
   } //Total complexity: O(log n)
   
   
   public void UpdateMinMax() //This method will update minNode and maxNode fields
   {
	   if (this.empty()) //If tree is empty
	   {
		   this.maxNode = null;
		   this.minNode = null;
	   }
	   else
	   {
		   //Finds the node with max key
		   IAVLNode nodeMax = this.getRoot(); //O(1)
		   while (nodeMax.getRight().isRealNode()) //Goes on the rightmost path, worst case as long as tree's height so O(log n)
		   {
			   nodeMax = nodeMax.getRight();
		   }
		   this.maxNode = nodeMax;
		   
		   //Find the node with min key
		   IAVLNode nodeMin = this.getRoot();
		   while (nodeMin.getLeft().isRealNode()) //Goes on the leftmost path, worst case as long as tree's height so O(log n)
		   {
			   nodeMin = nodeMin.getLeft();
		   }
		   this.minNode = nodeMin;
		   
	   }
	   //Total complexity: O(log n)
   }
   
   
   
   public IAVLNode findInsertPosition(IAVLNode node,int key)
   {
	   IAVLNode toReturn = this.getRoot();
	   while (node.isRealNode())
	   {
		   //Searches for the to-be-inserted node's supposed parent, by going down the tree.
		   toReturn = node;
		   if (node.getKey() == key)
		   {
			   return node;
		   }
		   if (node.getKey() > key)
		   {
			   node = node.getLeft();
		   }
		   else
		   {
			   node = node.getRight();
		   }
		   //Will exit the loop when finds the correct leaf or unary node, or the node itself if it's alredy present
	   }
	   
	   return toReturn;
	   
	 //Each iterations goes to a node's child. At most, iterations as AVL tree's height, which is O(logn)
   }
   
   

  /**
   * public int delete(int k)
   *
   * deletes an item with key k from the binary tree, if it is there;
   * the tree must remain valid (keep its invariants).
   * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
   * demotion/rotation - counted as one rebalnce operation, double-rotation is counted as 2.
   * returns -1 if an item with key k was not found in the tree.
   */
   public int delete(int k)
   {
	   if(this.empty()){ //Tree is empty, nothing to delete
		   return -1;
	   }
	   
	   IAVLNode toDelete = this.searchFromNode(k,this.root); //O(log n), check if key is in tree
	   if(toDelete.isRealNode() == false)
	   {
		   return -1;
	   }
	   
	   int rebalanceCount = 0;
	   
	   if (toDelete.getRight().isRealNode() && toDelete.getLeft().isRealNode()) //check if toDelete is a binary node
	   {
		   //This subroutine will switch between toDelete and it's successor's key and info fields, practically switching the nodes themselves
		   IAVLNode successor = ((AVLNode)toDelete).getSuccessor(); //O(log n) for finding successor
		   
		   //The following operate in O(1)
		   int key = toDelete.getKey();
		   String val = toDelete.getValue();
		   ((AVLNode)toDelete).setKey(successor.getKey());
		   ((AVLNode)toDelete).setValue(successor.getValue());
		   ((AVLNode)successor).setKey(key);
		   ((AVLNode)successor).setValue(val);
		   
		   toDelete = successor; //Now we will delete the Object which used to represent the successor; is placed as a leaf or unary node
		   
	   }
	   
	   IAVLNode z = toDelete.getParent();
	   IAVLNode y = toDelete; //For convenience
	   if ((y.getLeft().isRealNode()) || (y.getRight().isRealNode())) //Check if Unary node. Not binary because if was, alredy switched with successor which is at most unary
	   {
		   IAVLNode ychild = y.getLeft(); //ychild will be the only real child node of y
		   if (!ychild.isRealNode()) {
			   ychild = y.getRight();
		   }
		   
		   if (z == null) //y was the root
		   {
			   //The deletion: y's child will be the new root
			   ychild.setParent(null);
			   this.root = ychild;
			   ((AVLNode)ychild).updateSize(); //O(1)
			   this.size = ((AVLNode)ychild).getSize();
			   return 0; //Done here, no need to rebalance
		   }
		   else
		   {
			   if (z.getLeft() == y) //y was a left node 
			   {
				   z.setLeft(ychild);
			   }
			   else //y was a right node
			   {
				   z.setRight(ychild);
			   }
		   }

	   }
	   else //y is a leaf
	   {
		   if (z == null) //y was the root
		   {
			   this.root = null;
			   this.size = 0;
			   return 0; //Done here, no need to rebalance
		   }
		   else
		   {
			   if (z.getLeft() == y) //y was a left node 
			   {
				   z.setLeft(Ext); //Deleting y, replacing with external node
			   }
			   else
			   {
				   z.setRight(Ext); //Deleting y, replacing with external node
			   }
		   }
	   }
	   
	   
	   //Rebalance process, starting from z. Will also update sizes
	   while (z != null) //From z, up to the tree's root
	   {
		   
		   if ((z.getHeight() - z.getLeft().getHeight() == 2) && ((z.getHeight() - z.getRight().getHeight() == 2))) //Case 1, z is a 2-2 node
		   {
			   ((AVLNode)z).demote();
			   rebalanceCount+=1;
		   }
		   else
		   {
			   if ((z.getHeight() - z.getLeft().getHeight() == 3)) //Cases 2,3,4 as z is a 3-1 node
			   {
				   //IAVLNode x = z.getLeft();
				   y = z.getRight();
				   
				   if ((y.getHeight() - y.getLeft().getHeight() == 1) && (y.getHeight() - y.getRight().getHeight() == 1)) //Case 2, y is a 1-1 node
				   {
					   this.rotateLeft(z);
					   ((AVLNode)z).demote();
					   ((AVLNode)y).promote();
					   rebalanceCount+=3;
				   }
				   else
				   {
					   if ((y.getHeight() - y.getLeft().getHeight() == 2) && (y.getHeight() - y.getRight().getHeight() == 1)) //Case 3 y is a 2-1 node
					   {
						   this.rotateLeft(z);
						   ((AVLNode)z).demote();
						   ((AVLNode)z).demote();
						   rebalanceCount+=3;
					   }
					   
					   else
					   {
						   if ((y.getHeight() - y.getLeft().getHeight() == 1) && (y.getHeight() - y.getRight().getHeight() == 2)) //Case 4, y is a 1-2 node
						   {
							   this.rotateRight(y);
							   this.rotateLeft(z);
							   IAVLNode a = z.getParent();
							   ((AVLNode)a).promote();
							   ((AVLNode)z).demote();
							   ((AVLNode)z).demote();
							   ((AVLNode)y).demote();
							   rebalanceCount+=6;
						   }
					   }
					   
				   }
				   //Note: all rotations and height(rank) changes run in O(1)
				   
				   
			   }
			   else//Same up to symmetry
			   {
				   
				   if ((z.getHeight() - z.getRight().getHeight() == 3)) //Cases 2,3,4 as z is a 1-3 node
				   {
					   y = z.getLeft();
					   
					   if ((y.getHeight() - y.getLeft().getHeight() == 1) && (y.getHeight() - y.getRight().getHeight() == 1)) //Case 2, y is a 1-1 node
					   {
						   this.rotateRight(z);
						   ((AVLNode)z).demote();
						   ((AVLNode)y).promote();
						   rebalanceCount+=3;
					   }
					   else
					   {
						   if ((y.getHeight() - y.getLeft().getHeight() == 1) && (y.getHeight() - y.getRight().getHeight() == 2)) //Case 3 y is a 1-2 node
						   {
							   this.rotateRight(z);
							   ((AVLNode)z).demote();
							   ((AVLNode)z).demote();
							   rebalanceCount+=3;
						   }
						   else
						   {
							   if ((y.getHeight() - y.getLeft().getHeight() == 2) && (y.getHeight() - y.getRight().getHeight() == 1)) //Case 4, y is a 2-1 node
							   {
								   this.rotateLeft(y);
								   this.rotateRight(z);
								   IAVLNode a = z.getParent();
								   ((AVLNode)a).promote();
								   ((AVLNode)z).demote();
								   ((AVLNode)z).demote();
								   ((AVLNode)y).demote();
								   rebalanceCount+=6;
							   }
						   }  
						   
					   }
				   }
				   
				   
				   
				   
				   
			   }
			   
			   
		   }

		   
		   ((AVLNode)z).updateSize(); //Since a node was delete, update node's size field accordingly in O(1)
		   
		   if (z.getParent() == null) //If z is the new root
		   {
			   this.root = z;
		   }
		   
		   z = z.getParent(); //Go up in tree
			   
	   }
	   
	   //Loop iterates from the deleted node up to tree's root. At most, number of iterations as tree's height. AVL so O(log n)
	   //Each iterations runs in O(1). So whole loop's complexity is O(1)

	   this.UpdateMinMax(); //O(1); in case maxNode / minNode should change due to deletion
	   this.size = this.size - 1;
	   return rebalanceCount;
	   
	   
   } //Total complexity: O(log n)

   /**
    * public String min()
    *
    * Returns the info of the item with the smallest key in the tree,
    * or null if the tree is empty
    */
   public String min()
   {
	   if (this.minNode != null) //Accessing field in O(1). minNode maintained as node with smallest key
       {
           return this.minNode.getValue(); //getValue runs in O(1)
       }
       else //Tree is empty if no minNode
       {
           return null;
       }
   } //Total: O(1)

   /**
    * public String max()
    *
    * Returns the info of the item with the largest key in the tree,
    * or null if the tree is empty
    */
   public String max()
   {
	   if (this.maxNode != null) //Accessing field in O(1). maxNode maintained as node with largest key
       {
           return this.maxNode.getValue(); //getValue runs in O(1)
       }
       else //Tree is empty if no maxNode
       {
           return null;
       }
   } //Total: O(1)

  /**
   * public int[] keysToArray()
   *
   * Returns a sorted array which contains all keys in the tree,
   * or an empty array if the tree is empty.
   */
  public int[] keysToArray()
  {
	  int[] arr = new int[this.size()]; //Creates the array to be returned
	  if (this.empty() == false)
	  {
		  List<IAVLNode> lst = new LinkedList<IAVLNode>(); //Creates a list to be used in the scan
		  inOrderScan(this.getRoot(),lst); //O(n), in-order scan of the tree
		  int i=0;
		  for(IAVLNode node : lst) //Again, O(n). Move from list to array
		  {
			  arr[i]=node.getKey(); //Converts the list of node to an array of keys, in order
			  i++;
		  }
	  }
	  return arr;
	  //Worst case: O(n)
  }

  /**
   * public String[] infoToArray()
   *
   * Returns an array which contains all info in the tree,
   * sorted by their respective keys,
   * or an empty array if the tree is empty.
   */
  public String[] infoToArray()
  {
	  String[] arr = new String[this.size()]; //Creates the array to be returned
	  if (this.empty() == false)
	  {
		  List<IAVLNode> lst = new LinkedList<IAVLNode>(); //Creates a list to be used in the scan
		  inOrderScan(this.getRoot(),lst); //O(n), in-order scan of the tree
		  int i=0;
		  for(IAVLNode node : lst) //Again, O(n). Move from list to array
		  {
			  arr[i]=node.getValue(); //Converts the list of node to an array of values, in order
			  i++;
		  }
	  }
	  return arr;
	  //Worst case: O(n)
  }
  
  public void inOrderScan(IAVLNode node, List<IAVLNode> lst)
  {
	  //Recursively scans the tree
	  if (node.isRealNode())
	  {
		  inOrderScan(node.getLeft(),lst);
		  lst.add(node); //Added at the end of the current list. This function is called with a Linked List so insertion at end is O(1)
		  inOrderScan(node.getRight(),lst);
		  //In-Order so scan is done left, middle, right
	  }
	  //Iterates once for each node (and the external leaves) so number of iterations <= 2n thus Total complexity O(n)
  }
  
  

   /**
    * public int size()
    *
    * Returns the number of nodes in the tree.
    *
    * precondition: none
    * postcondition: none
    */
   public int size()
   {
	   return this.size; //Field access is O(1), thus whole function runs in O(1)
   }
   
     /**
    * public int getRoot()
    *
    * Returns the root AVL node, or null if the tree is empty
    *
    * precondition: none
    * postcondition: none
    */
   public IAVLNode getRoot()
   {
	   return this.root; //Field access is O(1), thus whole function runs in O(1)
   }
     /**
    * public string split(int x)
    *
    * splits the tree into 2 trees according to the key x. 
    * Returns an array [t1, t2] with two AVL trees. keys(t1) < x < keys(t2).
	  * precondition: search(x) != null (i.e. you can also assume that the tree is not empty)
    * postcondition: none
    */   
   
   
   public AVLTree[] split(int x)
   {
	   IAVLNode xNode = this.searchFromNode(x, this.getRoot());//get a pointer to the node of x in the original tree. O(log(n)), because of serachFromNode
	   AVLTree T1 = new AVLTree(); //create a tree such that all his nodes' keys<x. O(1), because of AVLTree()
	   AVLTree T2 = new AVLTree();//create a tree such that all his nodes' keys>x. O(1), because of AVLTree()
	   
	   if (xNode.getLeft().isRealNode()) //check if left sub tree is not virtual, if so we disconnect it from the original tree and assign T1's root as the left child (because this sub tree's keys are smaller than x's). condition takes O(1) because of getLeft,isRealNode
	   {
		   IAVLNode NPT1Node = xNode.getLeft();//O(1), because of getLeft()
		   NPT1Node.setParent(null);//O(1), because of setParent() 
		   T1.size = ((AVLNode)NPT1Node).getSize();//update it's size. O(1) because of getSize
		   T1.root=NPT1Node;//O(1), because we just assign the root
	   }
	   
	   if (xNode.getRight().isRealNode()) //check if right sub tree is not virtual, if so we disconnect it from the original tree and assign T2's root as the right child (because this sub tree's keys are bigger than x's). condition takes O(1) because of getRight,isRealNode
	   {
		   IAVLNode NPT2Node = xNode.getRight();//O(1), because of getRight()
		   NPT2Node.setParent(null);//O(1), because of setParent()
		   T2.size = ((AVLNode)NPT2Node).getSize();//update it's size. O(1) because of getSize
		   T2.root=NPT2Node;//O(1), because we just assign the root
	   }
	   
	   while (xNode.getParent()!=null) //as long as we don't reach the root. O(log(n)) iterations, condition takes O(1) because of getParent
	   {
		   if (xNode==xNode.getParent().getRight())  //if xNode is a right child, the parent node and it's left subtree have smaller keys than xNode's key (and respectively, smaller than x). condition takes O(1) because of getParent,getRight
		   {
			   //so, we disconnect the left sub tree from the parent (if it is not virtual), create a pointer to a node with the same parameters of the parent node (so that it won't be connected to the original tree)
			   //and join them with T1 by using the join method
			   IAVLNode joinNode = new AVLNode(xNode.getParent().getKey(),xNode.getParent().getValue(),null,Ext,Ext);//O(1), because of AVLNode's constructor,getKey,getValue,getParent
			   AVLTree joinT = new AVLTree();//empty tree for the left subtree of xNode's parent. O(1), because of AVLtree()
			   if (xNode.getParent().getLeft().isRealNode())//check if left sub tree is not virtual, if so we disconnect it from the original tree and assign T1's root as the left child (because this sub tree's keys are smaller than x's). condition takes O(1) because of getleft,isRealNode
			   {
				   IAVLNode NPNode = xNode.getParent().getLeft();//O(1), because of getLeft()
				   NPNode.setParent(null);//O(1), because of setParent()
				   joinT.size = ((AVLNode)NPNode).getSize();//update it's size. O(1) because of getSize
				   joinT.root=NPNode;//O(1), because we just assign the root

			   }
			   T1.join(joinNode, joinT);//O(|T1.height-joinT.height|+1), because of join
		   }
		   
		   if (xNode==xNode.getParent().getLeft()) //we do the same as above, but for T2 which contains keys bigger than x
		   {
			   IAVLNode joinNode = new AVLNode(xNode.getParent().getKey(),xNode.getParent().getValue(),null,Ext,Ext);//O(1), because of AVLNode's constructor,getKey,getValue,getParent
			   AVLTree joinT = new AVLTree();//empty tree for the right subtree of xNode's parent. O(1), because of AVLtree()
			   if (xNode.getParent().getRight().isRealNode()) //check if right sub tree is not virtual, if so we disconnect it from the original tree and assign T2's root as the right child (because this sub tree's keys are bigger than x's). condition takes O(1) because of getRight,isRealNode
			   {
				   IAVLNode NPNode = xNode.getParent().getRight();//O(1), because of getRight()
				   NPNode.setParent(null);//O(1), because of setParent()
				   joinT.size = ((AVLNode)NPNode).getSize();//update it's size. O(1) because of getSize
				   joinT.root=NPNode;//O(1), because we just assign the root
			   }
			   T2.join(joinNode, joinT);//O(|T1.height-joinT.height|+1), because of join
		   }
		   
		   xNode=xNode.getParent();//continue going up the tree, so we will join every node to it's correct output tree - T1 or T2.
	   }//whole loop takes O(log(n)), because of the analysis we saw in class of split's algorithm that uses the efficient method of join, which works in O(|tree.rank - t.rank| + 1)
	   
	   T1.UpdateMinMax();//update min and max values of T1. O(log(n)) because of UpdateMinMax
	   T2.UpdateMinMax();//update min and max values of T2. O(log(n)) because of UpdateMinMax
	   return new AVLTree[]{T1,T2};//return the array of the split trees as needed, O(1) because it returns existing objects
   }//overall, we get that the complexity is O(log(n))*4+O(1)=O(log(n)), as required
   
   /**
    * public join(IAVLNode x, AVLTree t)
    * 
    * joins t and x with the tree. 	
    * Returns the complexity of the operation (|tree.rank - t.rank| + 1).
	  * precondition: keys(x,t) < keys() or keys(x,t) > keys(). t/tree might be empty (rank = -1).
    * postcondition: none
    */   
   public int join(IAVLNode x, AVLTree t)
   {
	   int complex = 1; //Tracks complexity of the operation
	   if ((this.empty()) && (t.empty())) //If both are empty, tree will be comprised solely of x
	   {
		   this.root = x;
		   x.setLeft(Ext); //Only node, so external leaves as left and right.
		   x.setRight(Ext);
		   ((AVLNode)x).updateHeight(); //O(1)
		   ((AVLNode)x).updateSize(); //O(1)
		   this.size = 1;
		   this.maxNode = x;
		   this.minNode = x;
		   return 1;
	   }
	   
	   //One or more of the trees isn't empty
	   AVLTree Ttall;
	   AVLTree Tshort;
	   
	   if ((this.empty()) || (t.empty())) //Only one of them is empty (If both, previous If statement wouldve caught it)
	   {
		   if ((this.empty())) //this is empty
		   {
			   t.insertNode(x); //Insert x in to the non-empty tree is equivalent to joining with an empty tree
			   this.root = t.root; //Update this's fields so it's practically t, the non-empty result of the joining
			   this.size = t.size;
			   this.maxNode = t.maxNode;
			   this.minNode = t.minNode;
			   complex = t.getRoot().getHeight() - 0 + 1; 
		   }
		   else //t is empty
		   {
			   this.insertNode(x); //Insert x in to the non-empty tree is equivalent to joining with an empty tree
			   complex = this.getRoot().getHeight() - 0 + 1;
		   }
		   
		   return complex;
	   }
	   else //Both aren't empty
	   {
		   complex = Math.abs(this.getRoot().getHeight() - t.getRoot().getHeight()) + 1; //Calculation on O(1)
	   }
	   
	   //Now both tress are not empty.
	   
	   if (this.getRoot().getHeight() >= t.getRoot().getHeight()) //All functions in O(1), ascertain which tree is shorter
	   {
		   Ttall = this;
		   Tshort = t;
	   }
	   else
	   {
		   Ttall = t;
		   Tshort = this;
	   }
	   
	   IAVLNode B;
	   IAVLNode C;
	   
	   
	   if (Ttall.getRoot().getKey() > x.getKey()) //From precondition: keys in Tall tree > x.key > keys in Short tree
	   {
		   B = TraverseLeft(Ttall, Tshort.getRoot().getHeight(),true); //O(|Ttall.rank - Tshort.rank|+1) To be x's right child
		   if (B.isRealNode() == false) //If B is an external leaf, x should be a leaf
		   {
			   C = TraverseLeft(Ttall, Tshort.getRoot().getHeight(),false);
			   //In this implementation, to save memory, external leaves have no actual parent node. So to find it's "parent", need to find the prior node on the leftmost path
		   }
		   else
		   {
			   C = B.getParent();
		   }
		   
		   //Setting x as the joining node between trees, at the correct heights
		   x.setLeft(Tshort.getRoot());
		   x.setRight(B);
		   if (C != null) //B is definitely a left child, since we got it from TraverseLeft
		   {
			   C.setLeft(x);
		   }
		   
		   //In accordance with keys' values:
		   this.maxNode = Ttall.maxNode;
		   this.minNode = Tshort.minNode;
		   
	   }
	   else //From precondition: keys in Short tree > x.key > keys in Tall tree
	   {
		   B = TraverseRight(Ttall, Tshort.getRoot().getHeight(),true); //O(|Ttall.rank - Tshort.rank|+1) To be x's left child
		   if (B.isRealNode() == false) //If B is an external leaf, x should be a leaf
		   {
			   C = TraverseRight(Ttall, Tshort.getRoot().getHeight(),false);
			 //In this implementation, to save memory, external leaves have no actual parent node. So to find it's "parent", need to find the prior node on the leftmost path
		   }
		   else
		   {
			   C = B.getParent();
		   }
		   
		   //Setting x as the joining node between trees, at the correct heights
		   x.setRight(Tshort.getRoot());
		   x.setLeft(B);
		   if (C != null) //b is definitely a right child, since we got it from TraverseLeft
		   {
			   C.setRight(x);
		   }
		   
		   //In accordance with keys' values:
		   this.maxNode = Tshort.maxNode;
		   this.minNode = Ttall.minNode;
		   
	   }

	   
	   //It's rebalance o'clock (Rebalance and size-update loop)
	   ((AVLNode)x).updateHeight(); //O(1)
	   while(x != null) //Go up in the tree, rebalance and adjust heights
	   {

		   IAVLNode z = x.getParent();
		   if (z != null) //Rebalance proccess, until x is the root (or should be root)
		   {
			   //Note: all getHeigt, getLeft, getRight, getParent operations run in O(1)
			   if (z.getHeight() - x.getHeight() == 0) //Case A, need to rebalance!
			   {
				   if (z.getLeft() == x)
				   {
					   IAVLNode y = z.getRight();
					   switch(Math.abs(z.getHeight() - y.getHeight()))//Checks rank differences between z and right child y
					   {	
					   		case 1: //Case 1, z is a 0-1 node
					   		{
					   			((AVLNode)z).promote();
					   			break;
					   		}
					   		
					   		case 2: //Case 2 or 3 for insertion (z is 0-2), or unique join case
					   		{
					   			if ((x.getHeight() - x.getRight().getHeight() == 1) && (x.getHeight() - x.getLeft().getHeight() == 1)) //Unique join case, x is a 1-1 node
					   			{
					   				this.rotateRight(z);
					   				((AVLNode)x).promote();
					   			}
					   			else
					   			{
					   				if (Math.abs(x.getHeight() - x.getRight().getHeight()) == 2) //Single rotation required, x is a 1-2 node
						   			{
						   				this.rotateRight(z);
						   				((AVLNode)z).demote();
						   				
						   			}
						   			else //Double rotation required, x can only be a 2-1 node
						   			{
						   				this.rotateLeft(x);
						   				this.rotateRight(z);
						   				((AVLNode)x).demote();
						   				((AVLNode)z).demote();
						   				IAVLNode b = x.getParent();
						   				((AVLNode)b).promote();
						   				
						   				
						   			}
					   			}
					   			break;
					   		}
					   } 
				   }
				   else //x is a right child
				   {
					   //Same as above, but mirrored (Symmetic cases)
					   IAVLNode y = z.getLeft();
					   
					   switch(Math.abs(z.getHeight() - y.getHeight()))//Checks rank differences between z and left child y
					   {	
					   		case 1: //Case 1, z is a 1-0 node
					   		{
					   			((AVLNode)z).promote();
					   			break;
					   		}
					   		
					   		case 2: //Case 2 or 3 for insertion (z is 2-0), or unique join case
					   		{
					   			if ((x.getHeight() - x.getRight().getHeight() == 1) && (x.getHeight() - x.getLeft().getHeight() == 1)) //Unique join case, x is a 1-1 node
					   			{
					   				this.rotateLeft(z);
					   				((AVLNode)x).promote();
					   			}
					   			else
					   			{
					   				if (Math.abs(x.getHeight() - x.getLeft().getHeight()) == 2) //Single rotation required, x is a 2-1 node
						   			{
						   				this.rotateLeft(z);
						   				((AVLNode)z).demote();
						   				
						   			}
						   			else //Double rotation required, x is a 1-2 node
						   			{
						   				this.rotateRight(x);
						   				this.rotateLeft(z);
						   				((AVLNode)x).demote();
						   				((AVLNode)z).demote();
						   				IAVLNode b = x.getParent();
						   				((AVLNode)b).promote();
						   				
						   			}
					   			}
					   			
					   			break;
					   		}
					   }

					   
				   }

			   } //All rebalance operations (promotions, demotions, rotations) are O(1)
			   else
			   {
				   //Case B, No need to rebalance.
				   ((AVLNode)x).updateHeight();
			   }
			   
		   }
		   else //z is null
		   {
			   
			   ((AVLNode)x).updateHeight(); //Height fixes might be reuqired, anyway done in O(1)
		   }
		   ((AVLNode)x).updateSize(); //Update sizes due to joining, O(1) 
		   
		   if (x.getParent() == null) //if x should be root
		   {
			   this.root = x;
		   }

		   x = z; //Go up in the tree, redo process 
	   }
	   //The loop iterates from the joining point to root, so from shorter tree's height to taller tree's height
	   //Each iteration runs in constant time O(1), so loop runs in O(|Ttall.rank - Tshort.rank|+1)
	   
	   this.size = ((AVLNode)this.root).getSize(); //O(1) update tree's size
	   return complex;
   } //Total complexity: O(|Ttall.rank - Tshort.rank|+1) thus O(|this.rank - t.rank| +1)
   
   
   public IAVLNode TraverseLeft(AVLTree tree, int k, boolean allowExt) //To be used only in Join method
   {
	   //Will traverse the leftmost path in the tree, and return the first node with height<=k.
	   //if allowExt, returned node can be an external leaf. Else, it will be the first node with height <= k OR lowest real node 
	   
	   IAVLNode node = tree.getRoot();
	   
	   while (node.getHeight() > k) //Loop traverses leftmost path to node of height <= k so O(tree.rank - k +1)
	   {
		   if ((allowExt) || (node.getLeft().isRealNode())) //If there is a left child, including external
		   {
			   node = node.getLeft();
		   }
		   else //If there is no left child, or there is no left child discluding external
		   {
			   break;
		   }
	   }
	   
	   return node;
   } //Worst case: k=0, so O(tree.height +1) so O(log n)
   
   public IAVLNode TraverseRight(AVLTree tree, int k, boolean allowExt) //to be used only in Join method
   {
	   //Will traverse the leftmost path in the tree, and return the first node with height<=k.
	   //if allowExt, returned node can be an external leaf. Else, it will be the first node with height <= k OR lowest real node
	   
	   IAVLNode node = tree.getRoot();
	   
	   while ((node.getHeight() > k) && (node.isRealNode())) //Loop traverses rightmost path to node of height <= k so O(tree.rank - k +1)
	   {
		   if ((allowExt) || (node.getRight().isRealNode())) //If there is a right child, including external
		   {
			   node = node.getRight();
		   } 
		   else //If there is no right child, or there is no right child discluding external
		   {
			   break;
		   }
	   }
	   
	   return node;
   } //Worst case: k=0, so O(tree.height +1) so O(log n)
   
   
   public void rotateRight(IAVLNode x) //Right rotation on edge x - x.left
   {
	   IAVLNode y = x.getLeft();
	   IAVLNode B = y.getRight();
	   IAVLNode P = x.getParent(); //All get functions are O(1)
	   if(P != null) //In case parent isn't null, need to update pointers
	   {
		   if (P.getLeft() == x) //x is a left child
		   {
			   P.setLeft(y);
		   }
		   else
		   {
			   P.setRight(y);
		   }
	   }
	   else
	   {
		   y.setParent(null);
	   }
	   
	   //Actual rotation:
	   y.setRight(x); 
	   x.setLeft(B);
	   
	   ((AVLNode)x).updateSize();
	   ((AVLNode)y).updateSize(); //Size updates for nodes since some subtrees are swapped, in O(1)
	   
   }
   
   public void rotateLeft(IAVLNode y) { //left rotate on edge y - y.right
	   IAVLNode x = y.getRight();
	   IAVLNode B = x.getLeft();
	   IAVLNode P = y.getParent(); //All get functions are O(1)
	   if(P != null) //In case parent isn't null, need to update pointers
	   {
		   if (P.getLeft() == y) //y is a left child
		   {
			   P.setLeft(x);
		   }
		   else
		   {
			   P.setRight(x);
		   }
	   }
	   else
	   {
		   x.setParent(null);
	   }
	   
	   //Actual rotation:
	   x.setLeft(y);
	   y.setRight(B);
	   
	   ((AVLNode)x).updateSize();
	   ((AVLNode)y).updateSize(); //Size updates for nodes since some subtrees are swapped, in O(1)
	   
	   
   }
   
   
   
   
   
   

	/**
	   * public interface IAVLNode
	   * ! Do not delete or modify this - otherwise all tests will fail !
	   */
	public interface IAVLNode{	
		public int getKey(); //returns node's key (for virtual node return -1)
		public String getValue(); //returns node's value [info] (for virtual node return null)
		public void setLeft(IAVLNode node); //sets left child
		public IAVLNode getLeft(); //returns left child (if there is no left child return null)
		public void setRight(IAVLNode node); //sets right child
		public IAVLNode getRight(); //returns right child (if there is no right child return null)
		public void setParent(IAVLNode node); //sets parent
		public IAVLNode getParent(); //returns the parent (if there is no parent return null)
		public boolean isRealNode(); // Returns True if this is a non-virtual AVL node
    	public void setHeight(int height); // sets the height of the node
    	public int getHeight(); // Returns the height of the node (-1 for virtual nodes)
	}

	
   /**
   * public class AVLNode
   *
   * If you wish to implement classes other than AVLTree
   * (for example AVLNode), do it in this file, not in 
   * another file.
   * This class can and must be modified.
   * (It must implement IAVLNode)
   */
	public class AVLNode implements IAVLNode{
		//initializing parameters for a virtual node. O(1), because it just assigns a constant number of variables 
		public int key = -1;
		public String value;
		public IAVLNode parent;
		public IAVLNode left;
		public IAVLNode right;
		public int size=1;
		public int height;

		public AVLNode() { //empty constructor, used for virtual nodes and initializes parameters accordingly. O(1), because it just assigns a constant number of variables
			this.left = Ext;
			this.right = Ext;
			
		}
		public AVLNode(int key, String value, IAVLNode parent, IAVLNode left, IAVLNode right) { //assigns key,value,nodes,height and size according to the input parameters and formulas seen in class. O(1), because it just assigns a constant number of variables and because of updateSize,updateHeight
			this.key=key;
			this.value=value;
			this.parent=(AVLNode) parent;
			this.left=(AVLNode) left;
			this.right=(AVLNode) right;
			this.updateSize();
			this.updateHeight();
			
		}
		public int getKey()
		{
			return this.key; //return the key of this AVLNode Object. O(1), because it just returns a pointer
		}
		
		public String getValue()
		{
			return this.value; //return the value of this AVLNode Object. O(1), because it just returns a pointer
		}
		
		public void setLeft(IAVLNode node) //sets a new left child and updates size and height accordingly. O(1), because of updateSize,setParent and it just assigns one field
		{
			node.setParent(this);
			this.left=node;
			this.updateSize();
		}
		
		public IAVLNode getLeft() //returns left child, real or virtual. O(1), because it just returns a pointer
		{
			return this.left;
		}
		
		public void setRight(IAVLNode node) //sets a new right child and updates size and height accordingly. O(1), because of updateSize,setParent and it just assigns one field
		{
			node.setParent(this);
			this.right=node;
			this.updateSize();
		}
		
		public IAVLNode getRight() //returns right child, real or virtual. O(1), because it just returns a pointer
		{
			return this.right;
		}
		
		public void setParent(IAVLNode node) //sets a new parent. O(1), because it just assigns a field
		{
			this.parent=(AVLNode) node;
		}
		
		public IAVLNode getParent() //returns parent, real or virtual. O(1), because it just returns a pointer
		{
			return this.parent;
		}
		
		public boolean isRealNode() //returns True if this is a non-virtual node.
		{
			return (this.getHeight() != -1); //identifies a virtual node (external leaf) as one with -1 height. condition and return take O(1)
		}
		
		public void setHeight(int height) //sets a new height. O(1), because it just assigns a field
		{
			this.height=height;
		}
		
		public void updateHeight() { //updates height according to the formula seen in class. O(1), because of arithmetic operations and it just assigns a field
			this.height=1+Math.max(left.getHeight(), right.getHeight());
		}
		
		public int getHeight() //returns the height of the node. O(1), because it just returns a pointer
		{
			return this.height;
		}
		
		public int getSize() { //returns the size of a node. O(1), because it just returns a pointer
			return this.size;
		}
		
		public void setSize(int size) { //sets a new size. O(1), because it just assigns a field
			this.size=size;
		}
		
		public void updateSize() //updates size according to the formula seen in class. O(1), because of arithmetic operations and it just assigns a field
		{
			this.size = 1+((AVLNode) left).getSize()+((AVLNode) right).getSize();
		}
		
		public void setKey(int key) //sets a new key to the node. O(1), because it just assigns a field
		{
			this.key=key;
		}
		
		public void setValue(String value) //sets a new value to the node. O(1), because it just assigns a field
		{
			this.value=value;
		}
		
		public void promote() //increases the "rank" of a node by 1. O(1), because it just assigns a field
		{
			this.height+=1;
		}
		
		public void demote() //decreases the "rank" of a node by 1. O(1), because it just assigns a field
		{
			this.height-=1;
		}
		
		public IAVLNode getSuccessor() //returns the successor according to the algorithm seen in class
		{
			if((this.getRight()!=null) && (this.getRight().isRealNode())) {//if there exits a right child, we'll go down to him and then continue going to the next left child as long as it exists
				//by that, we get the minimal key of keys that are bigger than the original node, i.e that successor. condition takes O(1), because of getRight,isRealNode
				IAVLNode x=this.getRight();//O(1), because of getRight
				while((x.getLeft()!=null) && (x.getLeft().isRealNode())) {//condition takes O(1), because of getLeft,isRealNode
					x=x.getLeft();//O(1), because of getLeft
				}
				return x;//O(1), returns an existing object
			}//if there is no actual right child, the successor is the first node on the way to the root that is bigger than our original node.
			//i.e the first parent such that our original node is in it's left subtree.
			if(this.getParent()!=null){//condition takes O(1) because of getParent
				IAVLNode x=this;//O(1), assigns a node with an existing object
				while(x.getParent()!=null && x==x.getParent().getRight()) {//condition takes O(1), because of getParent,getRight
					x=x.getParent();//O(1), because of getParent
				}
				if(x.getParent()!=null && x==x.getParent().getLeft()) {//condition takes O(1), because of getParent,getLeft
					x=x.getParent();//O(1), because of getParent
					return x;//O(1), returns an existing object
				}
			}
			return null; // or else, there is'nt a successor at all. O(1), returns null
		}//overall, complexity is O(log(n)), because in each condition we use operations of O(1) complexity and we climb up to the root at most - O(log(n)) iterations
		
	}
}

  

