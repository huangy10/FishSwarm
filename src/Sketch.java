import processing.core.PApplet;
import processing.core.PVector;
import processing.event.KeyEvent;

import java.util.ArrayList;

public class Sketch extends PApplet {

    final static int NUM_PARTICLES = 100;
    final static float G_CONSTANT = 0.05f;

    float t = 0;
    private Particle[] particles;
    private ArrayList<GravitySource> gs;
    private StaticGravitySource mouseG;
    private StaticGravitySource pMouseG;
    private ArrayList<GravitySource>    leaders;

    boolean swim = false;
    float   halfWidth;
    float   halfHeight;

    public void settings() {
        size(800, 800);
    }

    public void setup() {
        frameRate(30);
        halfWidth = width / 2;
        halfHeight = height / 2;
        gs = new ArrayList<>();
        leaders = new ArrayList<>();
        createParticles();

        noiseSeed(second());
    }

    public void draw() {
        background(255);
        translate(width / 2, height / 2);
        mouseGravitySource();

        for(int i = 0; i < NUM_PARTICLES; i += 1) {
            particles[i].update();
        }

        for(int i = 0; i < NUM_PARTICLES; i += 1) {
            particles[i].display();
        }

        if (mousePressed && mouseG != null && !gs.isEmpty()) {
            stroke(color(255, 0, 0 ));
            strokeWeight(1);
            ellipse(mouseG.center.x, mouseG.center.y, mouseG.range, mouseG.range);
            ellipse(pMouseG.center.x, pMouseG.center.y, pMouseG.range, pMouseG.range);
        }
        t += 0.01;
        surface.setTitle("Framerate: " + frameRate);
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
                    random(20, 30), color(30), color(200));
            particles[i].gs = gs;
            particles[i].swarm = particles;
        }
        convertParticleToLeader(particles[NUM_PARTICLES - 1]);
        convertParticleToLeader(particles[NUM_PARTICLES - 2]);
        convertParticleToLeader(particles[NUM_PARTICLES - 3]);
    }

    private void convertParticleToLeader(Particle leader) {
        leader.asGravitySource = true;
        leader.color = color(255, 0, 0);
        leader.stopColor = color(100, 0, 0);
        leader.setMass(20f);
        leader.range = 50;
        leader.type  = 1;
        leaders.add(leader);
    }

    private void mouseGravitySource() {
        if (mousePressed) {
            if (mouseG == null) {
                mouseG = new StaticGravitySource(new PVector(mouseX - width / 2, mouseY - height / 2), 75, 10);
                pMouseG = new StaticGravitySource(new PVector(pmouseX - width / 2, pmouseY - height / 2), 50, 10);
                gs.add(mouseG);
                gs.add(pMouseG);
            } else {
                if (gs.size() <= leaders.size()) {
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
        } else if (gs.size() > leaders.size()) {
            gs.clear();
            gs.addAll(leaders);
        } else if (gs.isEmpty()) {
            gs.addAll(leaders);
        }
    }
}
