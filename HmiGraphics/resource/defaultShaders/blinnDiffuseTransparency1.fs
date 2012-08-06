// textured.fs
//

varying vec4 texCoord;
varying float LightIntensity;
uniform sampler2D TextureSampler; // texunit 0
uniform sampler2D TransparentSampler; //texunit 1 or 0 when TextureSampler not used

void main(void)
{
    vec3 lightColor = vec3(texture2D(TextureSampler, texCoord.st));
    vec3 transparentColor = vec3(texture2D(TransparentSampler, texCoord.st));
    float alpha = transparentColor.r;
   // float alpha = 0.5 + 0.5*transparentColor.r;
   // alpha = 1.0;
   //gl_FragColor = vec4(lightColor * LightIntensity, alpha);
  // lightColor = vec3(0, 0, 0);
    
  //  if (alpha < 1.0) { 
 //      lightColor = vec3(0.5*alpha, 0.0, 0.0);
  //  } 
    gl_FragColor = vec4(lightColor, alpha);
  //  gl_FragColor = vec4(1.0, 0.0, 0.0, 1.0);
  //  if (alpha < 0.9) { 
  //     gl_FragDepth = 1.0;
  //  } else {
  //     gl_FragDepth = gl_FragCoord.z;
  //  }
}
