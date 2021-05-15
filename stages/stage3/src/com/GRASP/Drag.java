package com.GRASP;

interface Drag {
    void move(Screen screen, float x, float y,
	      float dx, float dy);
    void drop(Screen screen, float x, float y,
	      float vx, float vy);
    void translate(float x, float y);
};
