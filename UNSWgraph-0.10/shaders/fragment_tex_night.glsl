out vec4 outputColor;

uniform vec4 input_color;

uniform mat4 view_matrix;
uniform mat4 proj_matrix;
uniform mat4 model_matrix;

// Light properties
uniform vec3 sunlightPos;
uniform vec3 sunlightIntensity;
uniform vec3 ambientIntensity;

// Material properties
uniform vec3 ambientCoeff;
uniform vec3 diffuseCoeff;
uniform vec3 specularCoeff;
uniform float phongExp;

uniform sampler2D tex;

uniform float torch;
uniform vec3 torchlightIntensity;
uniform vec3 torchlightPos;

in vec4 viewPosition;
in vec3 m;

in vec2 texCoordFrag;

void main()
{
	vec3 m_unit = normalize(m);
	vec3 v = normalize(-viewPosition.xyz);
	vec3 ambient = ambientIntensity*ambientCoeff;
	
	if(torch == 0) {								// day mode
	    // Compute the s, v and r vectors
	    vec3 s = normalize(view_matrix*vec4(sunlightPos,1) - viewPosition).xyz;
	    vec3 r = normalize(reflect(-s,m_unit));
	    
	    vec3 diffuse = max(sunlightIntensity*diffuseCoeff*dot(normalize(m_unit),s), 0.0);
	    vec3 specular;
	
	    // Only show specular reflections for the front face
	    if (dot(m_unit,s) > 0)
	        specular = max(sunlightIntensity*specularCoeff*pow(dot(r,v),phongExp), 0.0);
	    else
	        specular = vec3(0);
	
	    vec4 ambientAndDiffuse = vec4(ambient + diffuse, 1);

    	outputColor = ambientAndDiffuse*input_color*texture(tex, texCoordFrag) + vec4(specular, 1);
    }
    
    else {											// night mode
    	vec3 s = normalize(view_matrix*vec4(torchlightPos,1) - viewPosition).xyz;
	    vec3 r = normalize(reflect(-s,m_unit));
	    
	    vec3 diffuse = max(torchlightIntensity*diffuseCoeff*dot(normalize(m_unit),s), 0.0);
	    vec3 specular;
	
	    // Only show specular reflections for the front face
	    if (dot(m_unit,s) > 0)
	        specular = max(torchlightIntensity*specularCoeff*pow(dot(r,v),phongExp), 0.0);
	    else
	        specular = vec3(0);
	
	    vec4 ambientAndDiffuse = vec4(ambient + diffuse, 1);

    	outputColor = ambientAndDiffuse*input_color*texture(tex, texCoordFrag) + vec4(specular, 1);

    
    }
    
    
}