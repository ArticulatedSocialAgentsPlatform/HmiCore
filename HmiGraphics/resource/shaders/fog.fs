// fog.fs
//
// per-pixel fog


varying vec3 N, L;
uniform float density;

void main(void)
{

    const float specularExp = 128.0;
    vec3 NN = normalize(N);
    vec3 NL = normalize(L);
    vec3 NH = normalize(NL + vec3(0.0, 0.0, 1.0));

    // calculate diffuse lighting
    float intensity = max(0.0, dot(NN, NL));
    vec4 diffuse = gl_Color * intensity;

    // calculate specular lighting
    vec4 specular = vec4(0.0);
    if (intensity > 0.0)
    {
        intensity = max(0.0, dot(NN, NH));
        specular = vec4(pow(intensity, specularExp));
    }

    // sum the diffuse and specular components
    vec4 objectColor = diffuse + specular; 
    objectColor.a = gl_Color.a;
  
    const vec4 fogColor = vec4(0.5, 0.8, 0.5, 1.0);

    // calculate 2nd order exponential fog factor
    // based on fragment's Z distance
    const float e = 2.71828;
    float fogFactor = (density * gl_FragCoord.z);
    fogFactor *= fogFactor;
    fogFactor = clamp(pow(e, -fogFactor), 0.0, 1.0);
    // Blend fog color with incoming color
    gl_FragColor = mix(fogColor, objectColor, fogFactor);
}
