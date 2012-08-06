// textured.fs
//

varying vec4 texCoord[2];
varying float LightIntensity;
uniform sampler2D TextureSampler;  // texunit 0
uniform sampler2D TransparentSampler; // texunit 1

void main(void)
{
    vec3 lightColor = vec3(texture2D(TextureSampler, texCoord[0].st));
    vec3 transparentColor = vec3(texture2D(TransparentSampler, texCoord[1].st));
   // float alpha = transparentColor.r;
    float alpha = transparentColor.r;
   // alpha = 1.0;
    //gl_FragColor = vec4(lightColor * LightIntensity, alpha);
  //   if (alpha < 1.0) { 
  //     lightColor = vec3(0.5*alpha, 0.0, 0.0);
  //  } 
    
     gl_FragColor = vec4(lightColor , alpha);
  //   if (alpha < 0.9) { 
  //      gl_FragDepth = 1.0;
  //   } else {
  //      gl_FragDepth = gl_FragCoord.z;
  //   }
    
}
