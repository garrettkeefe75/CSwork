/* This node class is used to build linked lists for the
 * string_set class.
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
 *Node which contains a string and a vector of pointers to other nodes.
 */

#include "node.h"

namespace cs3505
{

  //Node Constructor
  node::node(const std::string & data, const int maxSize)
  {
    this->data = data;
    //initializes a vector of node* with maxSize elements, and then copys
    //NULL to each of those elements
    std::vector<node*> p (maxSize, NULL);
    this->pntrs = p;
  }

  node::~node()
  {
    
  }

}
