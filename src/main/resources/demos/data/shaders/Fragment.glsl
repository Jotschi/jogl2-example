#version 120

uniform float time;
varying vec2 textureCoord;

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453+ time);
}

void main(void)
{
	gl_FragColor = vec4(rand(textureCoord), rand(textureCoord), rand(textureCoord), 1.0);
}