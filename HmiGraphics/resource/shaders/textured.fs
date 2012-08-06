// textured.fs
//

varying float LightIntensity;
uniform sampler2D TextureSampler;

void main(void)
{
    vec3 lightColor = vec3(texture2D(TextureSampler, gl_TexCoord[0].st));
    gl_FragColor = vec4(lightColor * LightIntensity, 1.0);
}
