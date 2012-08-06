// textured.fs
//



varying vec4 texCoord;
varying float LightIntensity;
varying vec4 color;


uniform vec4 transparentColor;

void main(void)
{
    vec3 lightColor = color.rgb * LightIntensity;
    float alpha = 1 - transparentColor.r;
  
    gl_FragColor = vec4(lightColor , alpha);
  
    
}
