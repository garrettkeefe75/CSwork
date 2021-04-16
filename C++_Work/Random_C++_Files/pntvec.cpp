//Author: Garrett Keefe 
//Date: September 14, 2020
//I did not work with anyone on this project. I listed sources in my .h files.
//
//This file defines the pntvec class and implements functionality to represent a 3d point is space represented as a
//vector.

#include <iostream>

#include "pntvec.h"

#include <cmath>

//Empty Constructor
pntvec::pntvec()
{
  this->x = 0;
  this->y = 0;
  this->z = 0;
}

//Parameter Constructor
pntvec::pntvec(double x, double y, double z)
{
  this->x = x;
  this->y = y;
  this->z = z;
}

//Copy Constructor
pntvec::pntvec(const pntvec & copy)
{
  this->x = copy.x;
  this->y = copy.y;
  this->z = copy.z;
}

//Getter Functions
double pntvec::get_x() const
{
  return this->x;
}

double pntvec::get_y() const
{
  return this->y;
}

double pntvec::get_z() const
{
  return this->z;
}

//Calculates the distance between the point that its called upon and the point that it is passed. 
double pntvec::distance_to(pntvec point) const
{
  double distance;
  //Calculates the deltas very quickly by utilizing our inbuilt Operator- to quickly perform all of the math required.
  pntvec deltas = *this - point;
  distance = (deltas.get_x() * deltas.get_x()) + (deltas.get_y() * deltas.get_y()) + (deltas.get_z() * deltas.get_z());
  return sqrt(distance);
}

//Math Overloads
const pntvec pntvec::operator+ (const pntvec & rhs) const
{
  double newX = this->x + rhs.x;
  double newY = this->y + rhs.y;
  double newZ = this->z + rhs.z;
  return pntvec(newX, newY, newZ);
}

const pntvec pntvec::operator- (const pntvec & rhs) const
{
  double newX = this->x - rhs.x;
  double newY = this->y - rhs.y;
  double newZ = this->z - rhs.z;
  return pntvec(newX, newY, newZ);
}

const pntvec pntvec::operator- () const
{
  double X = -(this->x);
  double Y = -(this->y);
  double Z = -(this->z);
  return pntvec(X, Y, Z);
}

const pntvec pntvec::operator* (const double rhs) const
{
  double X = this->x * rhs;
  double Y = this->y * rhs;
  double Z = this->z * rhs;
  return pntvec(X, Y, Z);
}

pntvec & pntvec::operator= (const pntvec & rhs)
{
  //avoid self assignment
  if(&rhs == this)
    return *this;

  this->x = rhs.x;
  this->y = rhs.y;
  this->z = rhs.z;
  return *this;
}

//Streaming Operators
std::ostream & operator<< (std::ostream & out, const pntvec & point)
{
  out << "(" << point.x << ", " << point.y << ", " << point.z << ")";
  return out; 
}

std::istream & operator>> (std::istream & in, pntvec & point)
{
  in >> point.x >> point.y >> point.z;
  return in;
}
