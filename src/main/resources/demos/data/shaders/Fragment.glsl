#version 120

uniform float time;

float rand(vec2 co){
    return fract(sin(dot(co.xy ,vec2(12.9898,78.233))) * 43758.5453+ time);
}

void main(void)
{
    gl_FragColor = vec4(rand(vec2(1.0,1.0)), rand(vec2(0.0,1.0)), rand(vec2(1.0,0.0)), 1.0);
}