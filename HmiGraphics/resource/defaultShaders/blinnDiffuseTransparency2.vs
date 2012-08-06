// textured.vs
//

attribute vec4 mcPosition;
attribute vec3 mcNormal;
attribute vec4 texCoord1;
attribute vec4 texCoord2;
varying vec4 texCoord[2];
varying float LightIntensity;
const float specularContribution = 0.1;
const float diffuseContribution = 1.0 - specularContribution;

void main(void)
{
    vec3 ecPosition = vec3(gl_ModelViewMatrix * mcPosition);
    vec3 ecNormal = normalize(gl_NormalMatrix * mcNormal);
     
    vec3 lightVec = normalize(vec3(gl_LightSource[0].position) - ecPosition);
    vec3 reflectVec = reflect(-lightVec, ecNormal);
    vec3 viewVec = normalize(-ecPosition);
    
    float spec = clamp(dot(reflectVec, viewVec), 0.0, 1.0);
    spec = pow(spec, 16.0);
    
    LightIntensity = diffuseContribution * max(dot(lightVec, ecNormal), 0.0)
                   + specularContribution * spec;
                   
    texCoord[0] = texCoord1;
    texCoord[1] = texCoord2;
  
    // vertex MVP transform
    gl_Position = gl_ModelViewProjectionMatrix * mcPosition;
     
}
