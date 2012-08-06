// textured.fs
//

varying vec4 texCoord;
varying float LightIntensity;
uniform sampler2D TextureSampler;  // texunit 0
uniform vec4 transparentColor;

void main(void)
{
    vec3 lightColor = vec3(texture2D(TextureSampler, texCoord.st));
    //vec3 transparentColor = vec3(texture2D(TransparentSampler, texCoord[1].st));
   // float alpha = transparentColor.r;
    float alpha = 1 - transparentColor.r;
  
    gl_FragColor = vec4(lightColor , alpha);
  
    
}
