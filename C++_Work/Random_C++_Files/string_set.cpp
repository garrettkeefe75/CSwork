/* A 'string set' is defined as a set of strings stored
 * in sorted order in a drop list. 
 *
 * For lists that do not exceed 2 ^ (max_width + 1)
 * elements, the add, remove, and contains functions are 
 * O(lg size) on average.  The operator= and get_elements 
 * functions are O(size).   
 * 
 * Original Author: Peter Jensen
 * September 18, 2020
 */

/*
 *Editor/Co-Author: Garrett Keefe
 *Date: September 28, 2020
 *
 *I certify that I did not cheat.
 *
 *A drop list stores its elements by having randomly determined layers for each node.
 *It traverses the list by following the highest level until there are either no nodes 
 *left or we have found a node that exceeds the placement of the node to remove/find/add.
 *It then moves down a level and continues the process until it has found all of previous
 *nodes to Update or the node it was looking for.
 */

#include "string_set.h"
#include <vector>
#include <stdlib.h>
#include <time.h>

namespace cs3505
{
  //String_set constructor. Builds the head node with a width of max_width.
  string_set::string_set(int max_width)
  {
    node *h = new node("", max_width);
    this->head = h;
    this->max_width = max_width;
    this->size = 0;
    //set the seed for the random number generator used later in the class
    srand(time(NULL));
  }

  
  /** Copy constructor:  Initialize this set
    * to contain exactly the same elements as
    * another set.
    */
  string_set::string_set (const string_set & other)
  {
    *this = other;
  }


  /** Destructor:  Release any memory allocated
    * for this object.
    */
  string_set::~string_set()
  {
    this->clean();
    // All other class variables are released via their destructors    
  }

  //Returns the size variable of the class
  int string_set::get_size() const
  {
    return this->size;
  }

  /*This function adds in a new node and updates all of the relevant pointers to follow 
   *specifications for a drop list.
   *
   *Param targer: the string to be added to the drop list.
   */
  void string_set::add(const std::string & target)
  {
    //checks that the string does not already exist.
    if(this->contains(target))
      return;

    //intializes local variables for use in traversing the drop list
    node *currentNode = this->head;
    int currentLevel = this->max_width - 1;
    int nodeSize = 1;
    std::vector<node*> priorNodes (this->max_width);

    //This for loop uses a simulated coin flip to determine the size of the node to be added
    for(int i = 1; i <= this->max_width - 1; i++)
      {
	if(this->coinFlip() == 0)
	  break;
	else
	  nodeSize++;
      }

    //creates the node to be added with the target string and the size determined previously.
    node *newNode = new node(target, nodeSize);

    while(currentLevel != -1)
      {
	//Seemingly unnecessary boolean reverse operation is because I was having problems with pntr == NULL
	if(!(currentNode->pntrs[currentLevel] != NULL))
	  {
	    priorNodes[currentLevel] = currentNode;
	    currentLevel--;
	  }
	else
	  {
	    //if the data in next pntr is lexigraphically greater than target
	    if(target.compare(currentNode->pntrs[currentLevel]->data) > 0)
	      {
		//move to next node on current level
		currentNode = currentNode->pntrs[currentLevel];
	      }
	    else
	      {
		//save the node to be updated for this level
		priorNodes[currentLevel] = currentNode;
		currentLevel--;
	      }
	  }
      }

    //updates all of the pointers necessary and increment size.
    for(int i = 0; i < nodeSize; i++)
      {
	node *temp = priorNodes[i]->pntrs[i];
	priorNodes[i]->pntrs[i] = newNode;
	newNode->pntrs[i] = temp;
      }
    this->size++;
  }

  /*This function removes a node with a string equal to the target parameter
   *by finding all the nodes that precede it and setting them to point over to 
   *next node in the drop list. 
   *
   */
  void string_set::remove(const std::string & target)
  {
    //checks if this isn't contained in the drop list
    if(!this->contains(target))
      return;

    //initializes local variables for drop list traversal.
    node *currentNode = this->head;
    int currentLevel = this->max_width - 1;
    std::vector<node*> priorNodes (this->max_width);
    //sets preliminary data until the correct node is found.
    int nodeSize = 0;
    node *nodeToRemove;

    while(currentLevel != -1)
      {
	//weird bool expression explained in add
	if(!(currentNode->pntrs[currentLevel] != NULL))
	  {
	    priorNodes[currentLevel] = currentNode;
	    currentLevel--;
	  }
	else
	  {
	    //similar traversal as add
	    if(target.compare(currentNode->pntrs[currentLevel]->data) > 0)
	      {
		currentNode = currentNode->pntrs[currentLevel];
	      }
	    //if the target string equals the data of the next pointer
	    else if(target.compare(currentNode->pntrs[currentLevel]->data) == 0)
	      {
		//sets the local variables to the correct information for the nodeToRemove
		nodeSize = currentNode->pntrs[currentLevel]->pntrs.size();
		nodeToRemove = currentNode->pntrs[currentLevel];
		//saves node to be update and decrements
		priorNodes[currentLevel] = currentNode;
		currentLevel--;
	      }
	    else
	      {
		//saves node to be updated and decrements
		//Note: techinically this step is unnecessary as none of these saved nodes
		//will need to be updated as the nodeToRemove is not on their level.
		//Just done to resemble add and contains.
		priorNodes[currentLevel] = currentNode;
		currentLevel--;
	      }
	  }
      }

    //points the previous node over the node we wish to remove
    for(int i = 0; i < nodeSize; i++)
      {
	priorNodes[i]->pntrs[i] = nodeToRemove->pntrs[i];
	//clears the pointers so that the node destructor does not destroy in use nodes.
	nodeToRemove->pntrs[i] = NULL;
      }
    //removes heap allocation for this node.
    delete nodeToRemove;
    this->size--;
  }

  /*This function traverses through the drop list and returns true if the target parameter is
   *found in any of the nodes. Returns false otherwise.
   *
   */
  bool string_set::contains(const std::string & target) const
  {
    //initializes local variables for traversal.
    node *currentNode = this->head;
    int currentLevel = this->max_width - 1;

    while(currentLevel != -1)
      {
	//weird bool expression explained in add
	if(!(currentNode->pntrs[currentLevel] != NULL))
	  {
	    currentLevel--; 
	  }
	else
	  {
	    //similar travesal as add/remove
	    if(target.compare(currentNode->pntrs[currentLevel]->data) > 0)
	      {
		currentNode = currentNode->pntrs[currentLevel];
	      }
	    else if(target.compare(currentNode->pntrs[currentLevel]->data) == 0)
	      {
		//string has been found, return true;
		return true;
	      }
	    else
	      {
		currentLevel--;
	      }
	  }
      }
    return false;
  }

  /*This function cleans this object and then reinitializes it with the
   *right hand sides (rhs) parameters.
   *
   *Note: this isn't actually a direct copy as the new nodes (probably) have
   *different widths than the original object
   *Param rhs: the right hand side of the equation.
   */
  string_set & string_set::operator= (const string_set & rhs)
  {
    //cleans the pre-existing nodes in this.
    this->clean();
    //essentially the constructor
    node *hTemp = new node("", rhs.max_width);
    this->head = hTemp;
    this->max_width = rhs.max_width;
    this->size = 0;

    /*traverses through the list and adds all elements to this
     */
    node *currentNode = rhs.head;
    while(currentNode->pntrs[0] != NULL)
      {
	currentNode = currentNode->pntrs[0];
	this->add(currentNode->data);
      }
  }

  /*This function returns a list containing all of the string currently
   *in the drop list.
   *
   */
  std::vector<std::string> string_set::get_elements()
  {
    std::vector<std::string> listToReturn;
    node *currentNode = this->head;
    //traverses through level 0 because all nodes exist on that level
    while(currentNode->pntrs[0] != NULL)
      {
	currentNode = currentNode->pntrs[0];
	listToReturn.push_back(currentNode->data);
      }
    return listToReturn;
  }
  /*This function loops through all of the nodes in this and deletes them. 
   *
   */
  void string_set::clean()
  {
    node *currentNode = this->head;
    while(currentNode->pntrs[0] != NULL)
      {
	node *temp = currentNode;
	currentNode = currentNode->pntrs[0];
	delete temp;
      }
    delete currentNode;
  }

  /*This function returns a random number between 0 and 1, simulating
   *a coin flip.
   */
  int string_set::coinFlip()
  {
    int r = rand() % 2;
    return r;
  }
}
