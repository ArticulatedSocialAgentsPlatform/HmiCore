
attribute vec4 mcPosition;
attribute vec3 mcNormal;


void main(void)
{
  
    gl_Position = gl_ModelViewProjectionMatrix * mcPosition;
}
