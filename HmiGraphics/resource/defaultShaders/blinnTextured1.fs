// blinnTextured1.fs
//


varying vec3 ecPosition;
varying vec3 ecNormal;
varying vec4 specularCol;
varying float specExp;
varying vec4 ambientCol;
varying vec4 texCoord;

uniform sampler2D diffuseSampler;

const int numLights = 3;

void main () {
	vec3 N = normalize(ecNormal);                                            // normalized normal vector
	vec3 V = normalize(-ecPosition);                                         // normalized view vector
	
	
	vec4 diffuseCol = texture2D(diffuseSampler, texCoord.st);
	
	vec4 color =    vec4(0.0, 0.0, 0.0, 0.0);
	vec4 diffuse =  vec4(0.0, 0.0, 0.0, 0.0);
	vec4 specular = vec4(0.0, 0.0, 0.0, 0.0);
	vec4 ambient =  vec4(0.0, 0.0, 0.0, 0.0);
	float intensity = 0.0;
	float attenuation = 1.0;
	
	for(int i = 0; i < numLights; i++) {
      vec3 LV = (gl_LightSource[i].position.w == 0) ? vec3(gl_LightSource[i].position) : vec3(gl_LightSource[i].position) - ecPosition;    // directional or positional light?
	   vec3 L = normalize(LV); 
	   
	   vec3 H = normalize(L + V);
	   intensity = max(dot(N, L), 0.0);
	   if (intensity > 0.0) {
	      float att = gl_LightSource[i].linearAttenuation;
	      if (att > 0.0) {
	         float d = length(LV);
	         float attenuation = 1.0 / (1.0 + att * d);
	      }
	      ambient = ambientCol * gl_LightSource[i].ambient;
	      diffuse = intensity * diffuseCol * gl_LightSource[i].diffuse;
	      specular =  pow(max(dot(H, N), 0.0), specExp) * specularCol * gl_LightSource[i].specular;
	      
         color += (ambient +diffuse + specular) * attenuation;
	   } 
   }

	gl_FragColor = vec4(color.rgb, diffuseCol.a);
	
}