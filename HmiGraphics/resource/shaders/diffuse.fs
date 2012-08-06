// diffuse.fs
//
// Define the variables, set by the vertex shader:
varying vec3 N, L;

void main(void)
{
    vec3 NN = normalize(N);
    vec3 NL = normalize(L);

    // calculate diffuse lighting
    float intensity = max(0.0, dot(NN, NL));
    vec3 diffuse = gl_Color.rgb * intensity;

    gl_FragColor.rgb = diffuse;
    gl_FragColor.a = gl_Color.a;
}
