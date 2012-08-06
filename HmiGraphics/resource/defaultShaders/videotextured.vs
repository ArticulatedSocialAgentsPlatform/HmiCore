// videotextured.vs
//

void main(void)
{
   
    gl_TexCoord[0] = gl_MultiTexCoord0;
    
    // vertex MVP transform
    gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;
     
}
