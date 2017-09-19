import processing.core.*;

import java.util.ArrayList;

public class Particle {
    PVector pos;
    PVector v;
    PVector a;
    float   mass;
    float   seed;
    float   perlinStrength;
    float   maxSpeed;
    int     color;
    int     idx;
    Particle[]  swarm;

    Sketch sk;

    ArrayList<GravitySource> gs;

    Particle(int idx, Sketch sketch, PVector pos, PVector v, float mass, int color) {
        this.idx = idx;
        this.sk = sketch;
        this.pos = pos;
        this.v = v;
        this.mass = mass;
        this.color = color;
        this.a = new PVector();

        maxSpeed = 10;
        perlinStrength = 3f;
        seed = sk.random(1000);
    }

    void update() {
        if (boundaryCheck()) {
            pos.x = sk.random(sk.width) - sk.width / 2;
            pos.y = sk.random(sk.height) - sk.height / 2;
        }
        a.x = 0;
        a.y = 0;
        for (GravitySource g : gs) {
            applyGravity(g);
        }
        applyDamping();
        applyResistance();
        applyPerlinEngine();
        step();
    }

    private void applyGravity(GravitySource g) {
        float d = pos.dist(g.center);
        if (d < g.range) return;
        PVector gForce = g.center.copy().sub(pos).mult(Sketch.G_CONSTANT * g.mass * mass / (d * d));
        a.add(gForce);
    }

    private void applyPerlinEngine() {
        float noise = sk.noise(pos.mag(), pos.heading(), sk.t) - 0.5f;
        PVector perlin = v.copy().normalize().rotate(PApplet.PI / 2).mult(perlinStrength * noise);
        a.add(perlin);
    }

    private void applyDamping() {
        PVector damp = v.copy().mult(-0.1f);
        a.add(damp);
    }

    private void applyResistance() {
        for (Particle p : swarm) {
            if (p.idx == idx) continue;
            PVector d = pos.copy().sub(p.pos);
            float distance = d.mag();
            PVector resist = d.normalize().mult(p.mass * mass * Sketch.G_CONSTANT / (distance * distance * 10));
            a.add(resist);
        }
    }

    private void step() {
        v.add(a).limit(maxSpeed);
        pos.add(v);
    }

    private boolean boundaryCheck() {
        return pos.x < -sk.width / 2 || pos.x > sk.width / 2 || pos.y < -sk.height / 2 || pos.y > sk.height / 2;
    }

    void display() {
//        sk.stroke(color);
//        sk.point(pos.x, pos.y);
        sk.fill(color);
        sk.noStroke();
        sk.ellipse(pos.x, pos.y, 3, 3);
    }
}
