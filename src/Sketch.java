import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;

import java.util.ArrayList;

public class Sketch extends PApplet {

    final static int NUM_PARTICLES = 100;
    final static float G_CONSTANT = 0.05f;

    public float t = 0;
    private Particle[] particles;
    private ArrayList<GravitySource> gs;
    private GravitySource mouseG;
    private GravitySource pMouseG;

    public boolean swim = false;

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
        mouseGravitySource();

        for(int i = 0; i < NUM_PARTICLES; i += 1) {
            particles[i].update();
        }

        for(int i = 0; i < NUM_PARTICLES; i += 1) {
            particles[i].display();
        }

        if (mouseG != null && !gs.isEmpty()) {
            stroke(color(255, 0, 0 ));
            strokeWeight(1);
            ellipse(mouseG.center.x, mouseG.center.y, mouseG.range, mouseG.range);
            ellipse(pMouseG.center.x, pMouseG.center.y, pMouseG.range, pMouseG.range);
        }

        t += 0.01;
    }

    public void keyPressed(KeyEvent event) {
        if (event.getKey() == 's') {
            swim = !swim;
            println("swim");
        }
    }

    private void createParticles() {
        particles = new Particle[NUM_PARTICLES];
        for (int i = 0; i < NUM_PARTICLES; i += 1) {
            particles[i] = new Particle(
                    i, this, PVector.random2D().mult(random(width / 3)), PVector.random2D().mult(2),
                    random(20, 30), color(180));
            particles[i].gs = gs;
            particles[i].swarm = particles;
        }
    }

    private void mouseGravitySource() {
        if (mousePressed) {
            if (mouseG == null) {
                mouseG = new GravitySource(new PVector(mouseX - width / 2, mouseY - height / 2), 75, 10);
                pMouseG = new GravitySource(new PVector(pmouseX - width / 2, pmouseY - height / 2), 50, 10);
                gs.add(mouseG);
                gs.add(pMouseG);
            } else {
                if (gs.isEmpty()) {
                    gs.add(mouseG);
                    gs.add(pMouseG);
                }
                mouseG.move(mouseX - width / 2, mouseY - height / 2);
                PVector dir = (new PVector(mouseX - pmouseX, mouseY - pmouseY));
                if (dir.mag() > 1) {
                    PVector old = dir.normalize().mult(-50).add(mouseG.center);
                    pMouseG.move(old.x, old.y);
                }
            }
        } else if (!gs.isEmpty()) {
            gs.clear();
        }
    }
}
