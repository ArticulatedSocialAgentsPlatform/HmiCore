varying vec3 viewVector, lightVector, normalVector, ambient;

void main()	
{
	vec3 nV = normalize(normalVector);
	float nDotL = max(dot(lightVector, nV), 0.0);
	vec3 hV = reflect(lightVector,nV);
	float nDotH = max( dot(hV, viewVector), 0.0);
	gl_FragColor =  vec4(ambient + gl_FrontMaterial.diffuse.rgb * nDotL + gl_FrontMaterial.specular.rgb * pow(nDotH, gl_FrontMaterial.shininess), gl_FrontMaterial.diffuse.a);
}