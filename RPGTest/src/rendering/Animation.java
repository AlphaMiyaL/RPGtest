package rendering;

import io.Timer;

public class Animation {
	private Texture[] frames;
	private int pointer;
	private double elapsedTime;
	private double currentTime;
	private double lastTime;
	private double fps;
	
	public Animation(int amount, int fps, String fileName) {
		pointer = 0;
		elapsedTime=0;
		currentTime=0;
		lastTime=Timer.getTime();
		this.fps=1.0/(double)fps;
		this.frames = new Texture[amount];
		for(int i=0; i<amount; i++) {
			this.frames[i] = new Texture(fileName + "/" + i + ".png");
		}
	}
	
	public void bind() {
		bind(0);
	}
	
	public void bind(int sampler) {
		this.currentTime = Timer.getTime();
		this.elapsedTime += currentTime -lastTime;
		if(elapsedTime>=fps) {
			elapsedTime=0;
			pointer++;
		}
		if(pointer>=frames.length) {
			pointer=0;
		}
		this.lastTime=currentTime;
		frames[pointer].bind(sampler);
	}
}
