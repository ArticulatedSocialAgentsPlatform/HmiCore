// textured.fs
//

varying vec4 texCoord;
varying float LightIntensity;

uniform sampler2D TransparentSampler; //texunit 1

void main(void)
{
    vec3 lightColor = vec3(0, 0, 0);
    vec3 transparentColor = vec3(texture2D(TransparentSampler, texCoord.st));
    float alpha = transparentColor.r;
   // alpha = 1.0;
   //gl_FragColor = vec4(lightColor * LightIntensity, alpha);
  // lightColor = vec3(0, 0, 0);
    gl_FragColor = vec4(lightColor, alpha);
}
