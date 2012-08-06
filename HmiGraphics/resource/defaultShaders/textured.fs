// textured.fs
//

varying vec4 texCoord[2];
varying float LightIntensity;
varying vec4 col;
uniform sampler2D TextureSampler;
uniform sampler2D TransparentSampler;

void main(void)
{
    vec3 lightColor = vec3(texture2D(TextureSampler, texCoord[0].st));
  //  vec3 transparentColor = vec3(texture2D(TransparentSampler, texCoord[1].st));
  //  float alpha = 1.0 - transparentColor.r;
   float alpha = 1.0;
  //  gl_FragColor = vec4(lightColor * LightIntensity, alpha);
    gl_FragColor = col;
}
