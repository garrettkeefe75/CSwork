#include <iostream>

#include "pntvec.h"

#include <cmath>

#include <vector>

#include <fstream>

#include <string>

#include <sstream>

#include <limits>

///Author: Garrett Keefe
///Date: August 28, 2020
///Class: CS 3505
///I did not cooperate with anyone during this assignment and all resources found on the internet was from the official C++ reference documents
///or was received from stackoverflow.com
///
///File: This program takes a list of points and utilizes some math to find which of the candidate points provided best represents the overall 
///mean of the position of the point_cloud.

double generateScore(pntvec candidate, std::vector<pntvec> pointCloud);

std::vector<pntvec> generatePointCloud(std::string textFile);

///This function prints out the required output and runs all of the helper functions which contain the logic for the problem as well as the file transfer.
int main()
{
  ///sets the bestScore to the highest finite number possible to facilitate the checking of the lowest score later on.
  double bestScore = std::numeric_limits<double>::max();
  pntvec bestCandidate;
  std::vector<pntvec> pointCloud = generatePointCloud("point_cloud.txt");
  std::vector<pntvec> candidates = generatePointCloud("candidates.txt");
  ///Calculates all of the scores for each of the candidates and then checks whether the score was better than the previous best.
  for(int i = 0; i < candidates.size(); i++)
    {
      double newScore = generateScore(candidates[i], pointCloud);
      if(newScore < bestScore)
	{
	  bestScore = newScore;
	  bestCandidate = candidates[i];
	}
    }

  std::cout << bestScore << "\n" << bestCandidate.x << " " << bestCandidate.y << " " << bestCandidate.z << std::endl;

  return 0;
}

///This function takes a string that represents a filename to be accessed. The program opens and reads the file and then saves the information
///to a vector containing the provided pntvec.h class and returns that vector for use in main.
std::vector<pntvec> generatePointCloud(std::string textFile)
{
  std::vector<pntvec> pointCloud;
  std::ifstream fileToRead(textFile.c_str());
  while(true)
    {
      double x;
      double y;
      double z;
      fileToRead>>x;
      fileToRead>>y;
      fileToRead>>z;
      ///checks if any of the previous operations fails and then throws out any possible 'extras' to avoid bad data.
      if(fileToRead.fail())
	break;
      pntvec p;
      p.x = x;
      p.y = y;
      p.z = z;
      pointCloud.push_back(p);
    }
  fileToRead.close();
  return pointCloud;
}

///This function contains the logic behind the problem. It takes a single candidate and calculates its score by calculating the distance in 3D
///between it and every other point in the pointCloud and then it squares the distances and adds them together.
double generateScore(pntvec candidate, std::vector<pntvec> pointCloud)
{
  double score = 0;
  for(int i = 0; i < pointCloud.size(); i++)
    {
      double deltaX = candidate.x - pointCloud[i].x;
      double deltaY = candidate.y - pointCloud[i].y;
      double deltaZ = candidate.z - pointCloud[i].z;
      double distance = sqrt ((deltaX*deltaX) + (deltaY*deltaY) + (deltaZ*deltaZ));
      score = score + (distance*distance);
    }
  return score;
}
