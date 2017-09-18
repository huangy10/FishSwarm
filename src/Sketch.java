import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;

public class Sketch extends PApplet {

    final static int NUM_PARTICLES = 1000;
    final static float G_CONSTANT = 0.1f;

    public float t = 0;
    private Particle[] particles;
    private ArrayList<GravitySource> gs;
    private GravitySource mouseG;

    public void settings() {
        size(800, 800);
    }

    public void setup() {
        frameRate(30);
        gs = new ArrayList<>();
        createParticles();
    }

    public void draw() {
        background(0);
        translate(width / 2, height / 2);
        if (mousePressed) {
            if (mouseG == null) {
                mouseG = new GravitySource(new PVector(mouseX - width / 2, mouseY - height / 2), 100);
                gs.add(mouseG);
            } else {
                if (gs.isEmpty()) gs.add(mouseG);
                mouseG.move(mouseX - width / 2, mouseY - height / 2);
            }
        }

        for(int i = 0; i < NUM_PARTICLES; i += 1) {
            particles[i].update();
        }

        for(int i = 0; i < NUM_PARTICLES; i += 1) {
            particles[i].display();
        }

        if (mouseG != null) {
            fill(color(255, 0, 0 ));
            ellipse(mouseG.center.x, mouseG.center.y, 10, 10);
        }

        t += 0.01;
    }

    private void createParticles() {
        particles = new Particle[NUM_PARTICLES];
        for (int i = 0; i < NUM_PARTICLES; i += 1) {
            particles[i] = new Particle(
                    this, PVector.random2D().mult(random(width / 3)), PVector.random2D().mult(5),
                    30, color(255));
            particles[i].gs = gs;
        }
    }
}