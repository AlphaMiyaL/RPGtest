#version 120

attribute vec3 vertices;
attribute vec2 textures;

varying vec2 tex_coords; //varying variables allow fs shader class to access the attributes

uniform mat4 projection; //matrix4f

void main() {
	tex_coords = textures;
	gl_Position = projection*vec4(vertices, 1);
}