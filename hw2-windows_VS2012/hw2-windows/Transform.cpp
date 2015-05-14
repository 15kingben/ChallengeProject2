// Transform.cpp: implementation of the Transform class.

// Note: when you construct a matrix using mat4() or mat3(), it will be COLUMN-MAJOR
// Keep this in mind in readfile.cpp and display.cpp
// See FAQ for more details or if you're having problems.
#include <stdio.h>
#include <iostream>
#include "Transform.h"

// Helper rotation function.  Please implement this.  
mat3 Transform::rotate(const float degrees, const vec3& axis) 
{
    // YOUR CODE FOR HW1 HERE
	mat3 mat(1,0,0,0,1,0,0,0,1);
	mat *= cos(degrees*pi/180);
	float x = axis.x;
	float y = axis.y;
	float z = axis.z;
	mat3 matx(x*x,x*y,x*z,x*y,y*y,y*z,x*z,y*z,z*z);
	vec3 axis1 = axis;
	//mat3 mat1 = glm::transpose(axis1)*axis;
	mat = mat + (1-cos(degrees*pi/180))*matx + sin(degrees/180*pi)*mat3(0,axis.z,axis.y*-1,axis.z*-1,0,axis.x,axis.y,axis.x*-1,0);
	

  // You will change this return call
  return mat;
}

void Transform::left(float degrees, vec3& eye, vec3& up) {
  // YOUR CODE FOR HW1 HERE
	//mat3 rotx(1.0f,0.0f,0.0f,0.0f,cos(degrees/180*pi), sin(degrees/180*pi),0.0f, -sin(degrees/180*pi),cos(degrees/180*pi));
	
	//mat3 rotz(cos(degrees/180*pi), sin(degrees/180*pi),0.0f, -sin(degrees/180*pi),cos(degrees/180*pi),0.0,0.0,0.0,1.0);
	mat3 rot = rotate(degrees, glm::normalize(up));
	
	eye = rot*eye;
	up = rot*up;
	//printf("Coordinates: %.2f, %.2f, %.2f; distance: %.2f\n", up.x, up.y, up.z, sqrt(pow(eye.x, 2) + pow(eye.y, 2) + pow(eye.z, 2)));
	//assert( glm::dot(eye, up) == 0);
}

// Transforms the camera up around the "crystal ball" interface
void Transform::up(float degrees, vec3& eye, vec3& up) {
  // YOUR CODE FOR HW1 HERE 
	vec3 axis1 = glm::cross(eye,up);
	axis1 = glm::normalize(axis1);
	mat3 rot = rotate(degrees,axis1);
	eye = rot*eye;
	up = rot*up;
	//up = glm::cross(eye,axis);
	//up = glm::cross(eye,axis1);
	//printf("Coordinates: %.2f, %.2f, %.2f; distance: %.2f\n", up.x, up.y, up.z, sqrt(pow(eye.x, 2) + pow(eye.y, 2) + pow(eye.z, 2)));
}

mat4 Transform::lookAt(const vec3 &eye, const vec3 &center, const vec3 &up) 
{
    // Your implementation of the glm::lookAt matrix

  // YOUR CODE FOR HW1 HERE
	vec3 w = glm::normalize(eye-center);//z vector
	vec3 u = glm::normalize(glm::cross(up,w));
	vec3 v = glm::normalize(up);
  // You will change this return 
	mat4 transform(u.x,v.x,w.x,0,  u.y,v.y,w.y,0,  u.z,v.z,w.z,0,  -glm::dot(u,eye-center),-glm::dot(v,eye-center),-glm::dot(w,eye-center),1);
	std::cout <<"eyepos "  << eye.x + eye.y + eye.z  << " " << "upvector ";
		return transform;

}

mat4 Transform::perspective(float fovy, float aspect, float zNear, float zFar)
{
	//theta == fovy / 2
	//d = cot(theta)
	//perspective transformation == -1/z or -1/d--It is negative because opengl looks down negative z axis
	float A = -((zFar + zNear)/(zFar-zNear));
	float B = -(2*zFar*zNear)/(zFar-zNear);
	float theta = fovy / 2 / 180 * pi;
	float d = 1.0/glm::tan(theta);
    mat4 ret;
	ret = mat4(d/aspect,0,0,0,  0,d,0,0,  0,0,A,-1,  0,0,B,0);
	
    // YOUR CODE FOR HW2 HERE
    // New, to implement the perspective transform as well.  
	

	return ret;

}

mat4 Transform::scale(const float &sx, const float &sy, const float &sz) 
{
    mat4 ret;
    // YOUR CODE FOR HW2 HERE
    // Implement scaling 

	ret = mat4(sx,0,0,0,  0,sy,0,0,  0,0,sz,0,  0,0,0,1);
    return ret;
}

mat4 Transform::translate(const float &tx, const float &ty, const float &tz) 
{
    mat4 ret;
    // YOUR CODE FOR HW2 HERE
    // Implement translation 
	ret = mat4(1,0,0,0,  0,1,0,0, 0,0,1,0,  tx,ty,tz,1);
	//
	//ret[3] = vec4(tx,ty,tz,1); 

	return ret;

	return mat4(1.0, 0.0, 0.0, tx,
                0.0, 1.0, 0.0, ty,
                0.0, 0.0, 1.0, tz,
                0.0, 0.0, 0.0, 1.0);
}

// To normalize the up direction and construct a coordinate frame.  
// As discussed in the lecture.  May be relevant to create a properly 
// orthogonal and normalized up. 
// This function is provided as a helper, in case you want to use it. 
// Using this function (in readfile.cpp or 
//.cpp) is optional.  

vec3 Transform::upvector(const vec3 &up, const vec3 & zvec) 
{
    vec3 x = glm::cross(up,zvec); 
    vec3 y = glm::cross(zvec,x); 
    vec3 ret = glm::normalize(y); 
    return ret; 
}


Transform::Transform()
{

}

Transform::~Transform()
{

}
