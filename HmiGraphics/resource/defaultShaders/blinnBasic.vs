// Basic Blinn-Phong shader

attribute vec4 mcPosition;
attribute vec3 mcNormal;

uniform vec4 diffuseColor;
uniform vec4 specularColor;
uniform float shininess;
uniform vec4 ambientColor;
//uniform vec4 lightPosition;

varying vec3 ecNormal;
varying vec3 ecPosition;
varying vec4 diffuseCol;
varying vec4 specularCol;
varying float specExp;
varying vec4 ambientCol;


void main(void)
{
    ecPosition = vec3(gl_ModelViewMatrix * mcPosition);
    ecNormal = gl_NormalMatrix * mcNormal;

    diffuseCol = diffuseColor;
    specularCol = specularColor;
    specExp = shininess;
    ambientCol = ambientColor;
 
    gl_Position = gl_ModelViewProjectionMatrix * mcPosition;
}
