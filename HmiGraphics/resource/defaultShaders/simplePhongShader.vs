varying vec3 viewVector, lightVector, normalVector, ambient;

//SH coefficients (Orange book)
const float c1 = 0.429043;
const float c2 = 0.511664;
const float c3 = 0.743125;
const float c4 = 0.886227;
const float c5 = 0.247708;

const vec3 l00 = vec3( 	 0.64,  0.67,  0.73);
const vec3 l1m1 = vec3(  0.28,  0.32,  0.33);
const vec3 l10 = vec3(	 0.42,  0.60,  0.77);
const vec3 l11 = vec3(  -0.05, -0.04, -0.02);
const vec3 l2m2 = vec3( -0.10, -0.08, -0.05);
const vec3 l2m1 = vec3(	 0.25,  0.39,  0.53);
const vec3 l20 = vec3(   0.38,  0.54,  0.71);
const vec3 l21 = vec3(   0.06,  0.01, -0.02);
const vec3 l22 = vec3(  -0.03, -0.02, -0.03);


void main()	
{	
	normalVector = normalize(gl_NormalMatrix * gl_Normal);
	vec3 n = normalVector;	
	gl_TexCoord[0] = gl_MultiTexCoord0;
	
	vec4 ecPosition4 = gl_ModelViewMatrix * gl_Vertex;
	vec3 ecPosition3 = ecPosition4.xyz / ecPosition4.w;
	viewVector = normalize(ecPosition3);
	lightVector = normalize(gl_LightSource[0].position.xyz - ecPosition3);

	ambient = vec3(c1 * l22 * (n.x * n.x - n.y * n.y) + 
		c3 * l20 * n.z * n.z +
		c4 * l00 -
		c5 * l20 +
		2.0 * c1 * l2m2 * n.x * n.y +
		2.0 * c1 * l21  * n.x * n.z +
		2.0 * c1 * l2m1 * n.y * n.z +
		2.0 * c2 * l11 * n.x +
		2.0 * c2 * l1m1 * n.y +
		2.0 * c2 * l10 * n.z) * gl_FrontMaterial.ambient.rgb;


	gl_Position = ftransform();
}

