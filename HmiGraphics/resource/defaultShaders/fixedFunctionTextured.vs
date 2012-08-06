// textured.vs fixed function version
//
varying float LightIntensity;
const float specularContribution = 0.1;
const float diffuseContribution = 1.0 - specularContribution;

void main(void)
{
    vec3 ecPosition = vec3(gl_ModelViewMatrix * gl_Vertex);
    vec3 tnorm = normalize(gl_NormalMatrix * gl_Normal);
     
    vec3 lightVec = normalize(vec3(gl_LightSource[0].position) - ecPosition);
    vec3 reflectVec = reflect(-lightVec, tnorm);
    vec3 viewVec = normalize(-ecPosition);
    
    float spec = clamp(dot(reflectVec, viewVec), 0.0, 1.0);
    spec = pow(spec, 16.0);
    
    LightIntensity = diffuseContribution * max(dot(lightVec, tnorm), 0.0)
                   + specularContribution * spec;
                   
    gl_TexCoord[0] = gl_MultiTexCoord0;
    
    // vertex MVP transform
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
     
}
