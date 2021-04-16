//Author: Garrett Keefe
//Date: September 14, 2020
//I did not work with anybody else on this assignment. I used geeksforgeeks.org and the official c++ documentation as 
//sources for my code.
//
//This is the Header File for the pntvec class
//
#include <iostream>

#include <sstream>

class pntvec
{
 private:
  //Class variables
  double x;
  double y;
  double z;

 public:
  
  //Class constructors.
  pntvec();
  pntvec(double x, double y, double z);
  pntvec(const pntvec & copy);
  //Class methods.
  double get_x() const;
  double get_y() const;
  double get_z() const;
  double distance_to(pntvec point) const;
  //Overload math operators.
  const pntvec operator+ (const pntvec & rhs) const;
  const pntvec operator- (const pntvec & rhs) const;
  const pntvec operator* (const double rhs) const;
  const pntvec operator- () const;
  pntvec & operator= (const pntvec & rhs);

  //Overloads for Operator for printing and reading.
  friend std::ostream & operator<< (std::ostream & out, const pntvec & point);
  friend std::istream & operator>> (std::istream & in, pntvec & point);  
};
