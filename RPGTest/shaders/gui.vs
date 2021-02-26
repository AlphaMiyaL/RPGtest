#version 120

attribute vec3 vertices;
attribute vec2 textures;

varying vec2 tex_coords; //varying variables allow fs shader class to access the attributes

uniform mat4 projection; //matrix4f
uniform mat4 texModifier;

void main() {
	tex_coords = (texModifier * vec4(textures, 0, 1)).xy;
	gl_Position = projection*vec4(vertices, 1);
}