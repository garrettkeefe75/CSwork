//Author: Garrett Keefe
//Date: September 14, 2020
//I did not work with anybody on this assignment. Sources are listed in my .h files.
//
//This files tests the functionality of the pntvec.cpp class and returns whether or not the file has errors.
//

#include <iostream>

#include <sstream>

#include "pntvec.h"

#include <cmath>

//Declarations of our helper functions
int testDefaultConstructor();

int testParameterizedConstructor();

int testCopyConstructor(pntvec p);

int testPlusOperator();

int testMinusOperator();

int testNegationOperator();

int testMultiplicationOperator();

int testDistanceTo();

int testOutputOperator();

int testInputOperator();

int main()
{
  //Each of these represent one test
  if(testDefaultConstructor() == -1)
    {
      std::cout << "Error in ConstructorE" << std::endl;
      return -1;
    }

  if(testParameterizedConstructor() == -1)
    {
      std::cout << "Error in ConstructorP." << std::endl;
      return -1;
    }

  pntvec p = pntvec(98.75, 1.555, 10000000.96);
  if(testCopyConstructor(p) == -1)
    {
      std::cout << "Error in ConstructorC." << std::endl;
    }

  if(testPlusOperator() == -1)
    {
      std::cout << "Error in Operator+." << std::endl;
      return -1;
    }

  if(testMinusOperator() == -1)
    {
      std::cout << "Error in Operator-." << std::endl;
      return -1;
    }

  if(testNegationOperator() == -1)
    {
      std::cout << "Error in Negation Operator-." << std::endl;
      return -1;
    }

  if(testMultiplicationOperator() == -1)
    {
      std::cout << "Error in Operator*." << std::endl;
      return -1;
    }

  if(testDistanceTo() == -1)
    {
      std::cout << "Error in distance_to." << std::endl;
      return -1;
    }

  if(testOutputOperator() == -1)
    {
      std::cout << "Error in Operator<<." << std::endl;
      return -1;
    }

  if(testInputOperator() == -1)
    {
      std::cout << "Error in Operator>>." << std::endl;
      return -1;
    }

  std::cout << "No error." << std::endl;
  return 0;
}

//These are Helper functions which act as makeshift unit tests.
int testDefaultConstructor()
{
  pntvec p;
  if(p.get_x() == 0 && p.get_y() == 0 && p.get_z() == 0)
    return 0;
  return -1;
}

int testParameterizedConstructor()
{
  pntvec p = pntvec(1.4, 2.5, 3.6);
  if(p.get_x() == 1.4 && p.get_y() == 2.5 && p.get_z() == 3.6)
    return 0;
  return -1;
}

int testCopyConstructor(pntvec p)
{
  if(p.get_x() == 98.75 && p.get_y() == 1.555 && p.get_z() == 10000000.96)
    return 0;
  return -1;
}

int testPlusOperator()
{
  pntvec p = pntvec(9.7, 8.2, -10.1);

  pntvec q = pntvec(-9.7, -8.2, 10.1);

  pntvec result1 = p + q;
  pntvec result2 = q + p;

  if((result1.get_x() == 0 && result1.get_y() == 0 && result1.get_z() == 0) && result1 == result2)
    return 0;
  return -1;
}

int testMinusOperator()
{
  pntvec p = pntvec(1, 2, 1);

  pntvec q = pntvec(9.3, -4.1, 22);

  pntvec result1 = p - q;
  pntvec result2 = q - p;

  if((result.get_x() == -8.3 && result.get_y() == 6.1 && result.get_z() == -21.0) && result1 != result2)
    return 0;
  return -1;
}

int testNegationOperator()
{
  pntvec p = pntvec(33.3, 0.0, -19.111);
  pntvec testPoint = -p;
  if(testPoint.get_x() == -33.3 && testPoint.get_y() == 0 && testPoint.get_z() == 19.111)
    return 0;
  return -1;
}

int testMultiplicationOperator()
{
  pntvec p = pntvec(7, 3, 1);
  pntvec testPoint = p * 5;

  if(testPoint.get_x() == 35 && testPoint.get_y() == 15 && testPoint.get_z() == 5)
    return 0;
  return -1;
}

int testDistanceTo()
{
  pntvec p = pntvec();
  pntvec q = pntvec(1, 1, 1);

  if(p.distance_to(q) == sqrt(3))
    return 0;
  return -1;
}

int testOutputOperator()
{
  pntvec p = pntvec(1, 2, 3);
  //declares a string and streames the point to it for testing the out Operator
  std::ostringstream s1;
  s1 << p;
  if(s1.str() == "(1, 2, 3)")
    return 0;
  return -1;
}

int testInputOperator()
{
  pntvec p;
  //declares a string and sets it to a string for testing the in Operator
  std::istringstream s1;
  s1.str("1 2 3");
  s1 >> p;
  if(p.get_x() == 1 && p.get_y() == 2 && p.get_z() == 3)
    return 0;
  return -1;
}
