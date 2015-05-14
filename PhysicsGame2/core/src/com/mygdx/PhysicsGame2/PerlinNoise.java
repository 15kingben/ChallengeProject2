package com.mygdx.PhysicsGame2;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

public class PerlinNoise {

	// PerlinNoise.cpp : Defines the entry point for the console application.
	//
	final int SIZE;
	Random r = new Random();
	float[] pixels;
	
	final float OCT = 4;
	float pers = (float) (1.f/Math.sqrt(2.f));
	
	public PerlinNoise(int size, float pers){
		this.pers = pers;
		SIZE = size;
		pixels = new float[SIZE+1];
		
			for(int i = 0; i < SIZE + 1; ++i){
				pixels[i] = r.nextFloat() * 2 - 1;//-1 to 1
				pixels[i] = r.nextFloat() * 2 - 1;
			}
		
		
				
	}

	
	
	public float genNoise(float loc){
		
		int intLoc = (int) loc;
		float fLoc = loc - (float)intLoc;
		
		float v1 = smoothNoise(intLoc);
		float v2 = smoothNoise(intLoc + 1);
		
		return ease(v1, v2, fLoc);
		
	}
	
	
	float cameraNoise(float x){
		float total = 0.f;
		
		for(int i = 0; i <= OCT; i++){
			float freq = (float) Math.pow(2, i);
			float amp = (float) Math.pow(pers, i);
			total += genNoise(x * freq) * amp ;
		}
		return total;
	}
	

	float smoothNoise(int x){
		return noise(x)/2.f + noise(x-1) / 4.f + noise(x+1) / 4.f;
	}

	private float lerp(float a, float b, float x){
		return (a * (1 - x) + b * x);
	}

	private float ease(float a, float b, float x){
		float Sx = (float) (3.f * Math.pow(x , 2) - 2 * Math.pow(x , 3));
		return a + Sx*(b-a);
	}
	
	private float noise(int x){		 

		x = (x<<13) ^ x;
    	return (float) ( 1.0 - ( (x * (x * x * 15731 + 789221) + 1376312589) & 2147483647) / 1073741824.0);    

	}
}
