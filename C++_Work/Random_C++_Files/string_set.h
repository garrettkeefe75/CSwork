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
 *
 *I certify that I did not cheat.
 *
 *This file handles all declarations necessary for the string_set.cpp file.
 */

#ifndef STRING_SET_H
#define STRING_SET_H

#include "node.h"  

namespace cs3505
{
  class string_set
    {
      // Default visibility is private. 
      int max_width;  // The maximum width of of a drop list in each node

      node *head;     // The head of the list will sit in a sentinal node
                      // (without any data in it).  This sentinal node
                      // will have a maximum width next list.  The head
                      // sentinal node should be in heap memory.

      int size;       // The number of elements in the set

    public:
      string_set(int max_next_width = 10);   // Constructor.  Notice the default parameter value.
      string_set(const string_set & other);  // Copy constructor
      ~string_set();                         // Destructor

      void add      (const std::string & target);        // Not const - modifies the object
      void remove   (const std::string & target);        // Not const - modifies the object
      bool contains (const std::string & target) const;  // Const - does not change the object
      int  get_size () const;                            // Const - does not change object

      string_set & operator= (const string_set & rhs);   // Not const - modifies this object

      std::vector<std::string> get_elements();           // Returns all the elements in this string_set,
                                                         // in ascending order.  
    private:
      // Static - algorith remains the same between objects.
      static int coinFlip ();
      void clean();
  };

}

#endif
