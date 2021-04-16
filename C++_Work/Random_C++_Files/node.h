/* This node class is used to build drop lists for the
 * string_set class.
 *
 * Original Author: Peter Jensen
 * September 18, 2020
 */

/*
 *Editor/Co-Author: Garrett Keefe
 *Date: September, 28, 2020
 *
 *I certify that I did not use and designs and/or code from outside resources,
 *all information was either retrieved from the hashset example or my own brain.
 *
 *This file handles all necessary declarations for the node.cpp file.
 */

// Guard against double inclusion

#ifndef NODE_H
#define NODE_H

// You will use strings, and you may use vectors.

#include <string>
#include <vector>

namespace cs3505
{
  // We're in a namespace - declarations will be within this CS3505 
  // namespace.  (There are no definitions here, see node.cpp.)

  /* Node class for holding elements. */

  class node
  {
    friend class string_set;   // This allows functions in string_set to access
			       //   private data (and constructor) within this class.
 
    private:

    //Class Variables
    std::string data;
    std::vector<node*> pntrs;
    //Constructor and Destructor
    node(const std::string & data, const int maxSize);
    ~node();

  };
}
		
#endif 
	
