package rendering;

import java.util.ArrayList;

import org.joml.Matrix4f;

import assets.Assets;

public class DrawString {
    private ArrayList<Texture> letters;
    
    public DrawString(String str) {
    	letters = new ArrayList<Texture>();
    	for(int i=0; i<str.length(); i++) {
			Character chara = str.charAt(i);
			Texture tex;
			if(chara.toString().matches("[A-Z]")) {
				tex = new Texture("letters/uppercase/"+chara+".png");
			}
			else if(chara.toString().matches("[a-z]")) {
				tex = new Texture("letters/lowercase/"+chara+".png");
			}
			else {
				if(chara.toString().equals("?")) {
					tex = new Texture("letters/specialcase/question.png");
				}
				else if(chara.toString().equals("\"")) {
					tex = new Texture("letters/specialcase/quote.png");
				}
				else if(chara.toString().equals(" ")){
					tex = null;
				}
				else {
					tex = new Texture("letters/specialcase/"+chara+".png");
				}
			}
			letters.add(tex);
		}
    }
    
    public void render(float x, float y, int divider, float size, Camera camera, Shader shader) {
    	for(int i=0; i<letters.size(); i++) {
    		if(letters.get(i)!=null) {
    		Matrix4f mat = new Matrix4f();
    		float subtraction = -letters.size()/2*(size/(divider));
    		camera.getUntransformedProjection().scale(size, mat);
    		mat.translate((float)((x/divider)+i*(size/(divider))+subtraction), y/divider, 0);
    		shader.setUniform("sampler", 0);
			shader.setUniform("projection", mat);
			letters.get(i).bind(0);
			Assets.getModel().render();
    		}
    	}
    }

	public ArrayList<Texture> getLetters() {
		return letters;
	}
}