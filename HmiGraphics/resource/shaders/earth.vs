// Earth vertex shader computes lighting coefficients.
//

// varying variables passed to fragment shader
varying float Diffuse;
varying vec3  Specular;
varying vec2  TexCoord;

void main(void)
{
    // calculate vertex position in eye coordinates
    vec3 ecPosition = vec3(gl_ModelViewMatrix * gl_Vertex);
    
    // compute the transformed normal
    vec3 tnorm      = normalize(gl_NormalMatrix * gl_Normal);


    // compute normalized vectors toward the sun, and towards the viewer
    vec3 lightVec = normalize(gl_LightSource[0].position - ecPosition);
    vec3 viewVec = normalize(-ecPosition);

    
    // compute the reflection vector
    vec3 reflectVec = reflect(-lightVec, tnorm);
    

    // Calculate specular light intensity, scale down and
    // apply a slightly yellowish tint. 
    float spec = clamp(dot(reflectVec, viewVec), 0.0, 1.0);
    float specIntensity = pow(spec, 8.0);
    specIntensity       = 0.3 * specIntensity; 
    Specular            = specIntensity * vec3 (1.0, 0.941, 0.898);
    
    // Calculate a diffuse light intensity
    Diffuse             = max(dot(lightVec, tnorm), 0.0);

    // Pass texture coordinates fragment shader
    TexCoord    = gl_MultiTexCoord0.st;
    //TexCoord.x  = TexCoord.x;
    
    // output final vertex information
    gl_Position     = gl_ModelViewProjectionMatrix * gl_Vertex;
}
