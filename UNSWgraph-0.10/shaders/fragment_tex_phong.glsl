
out vec4 outputColor;

uniform vec4 input_color;

uniform mat4 view_matrix;

// Light properties
uniform vec3 lightPos;
uniform vec3 lightIntensity;
uniform vec3 ambientIntensity;
uniform vec3 sunlightIntensity;

// Material properties
uniform vec3 ambientCoeff;
uniform vec3 diffuseCoeff;
uniform vec3 specularCoeff;
uniform float phongExp;

uniform sampler2D tex;

in vec4 viewPosition;
in vec3 m;

in vec2 texCoordFrag;

void main()
{
	vec3 m_unit = normalize(m);
    // Compute the s, v and r vectors
    vec3 s = normalize(view_matrix*vec4(lightPos,1) - viewPosition).xyz;
    vec3 v = normalize(-viewPosition.xyz);
    vec3 r = normalize(reflect(-s,m_unit));
   	
   	// sunlight
    vec3 sunS = normalize(view_matrix*vec4(lightPos,0)).xyz;
    vec3 sunR = normalize(reflect(-sunS,m));

    vec3 ambient = ambientIntensity*ambientCoeff;
    vec3 diffuse = max(lightIntensity*diffuseCoeff*dot(normalize(m_unit),s), 0.0);
    vec3 specular;
    
    // sunlight
    vec3 sunDiffuse = max(sunlightIntensity*diffuseCoeff*dot(m,sunS), 0.0);
    vec3 sunSpecular;
    
    // Only show specular reflections for the front face
    if (dot(m,sunS) > 0)
        sunSpecular = max(sunlightIntensity*specularCoeff*pow(dot(sunR,v),phongExp), 0.0);
    else
        sunSpecular = vec3(0);

    //Only show specular reflections for the front face		--> if we need torch?
    if (dot(m_unit,s) > 0)	
        specular = max(lightIntensity*specularCoeff*pow(dot(r,v),phongExp), 0.0);
    else
        specular = vec3(0);
        
    //specular = sunSpecular;
    diffuse = sunDiffuse;

    vec4 ambientAndDiffuse = vec4(ambient + diffuse, 1);

    outputColor = ambientAndDiffuse*input_color*texture(tex, texCoordFrag) + vec4(specular, 1);
}