// blinnTextured1.vs
//

attribute vec4 mcPosition;
attribute vec3 mcNormal;
attribute vec4 texCoord1;

uniform vec4 diffuseColor;
uniform vec4 specularColor;
uniform float shininess;
uniform vec4 ambientColor;

uniform float repeatS; // repeat scale factor for texture coords
uniform float repeatT;
uniform float offsetS; // offset for texture coords
uniform float offsetT;



varying vec3 ecNormal;
varying vec3 ecPosition;
varying vec4 specularCol;
varying float specExp;
varying vec4 ambientCol;
varying vec4 texCoord;


void main(void)
{

    ecPosition = vec3(gl_ModelViewMatrix * mcPosition);
    ecNormal = gl_NormalMatrix * mcNormal;

    specularCol = specularColor;
    specExp = shininess;
    ambientCol = ambientColor;

    texCoord.s = offsetS + repeatS * texCoord1.s;
    texCoord.t = offsetT + repeatT * texCoord1.t;
    texCoord.p = texCoord1.p;
    texCoord.q = texCoord1.q;
 
    gl_Position = gl_ModelViewProjectionMatrix * mcPosition;
     
}
