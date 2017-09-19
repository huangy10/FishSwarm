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

    Sketch sk;

    ArrayList<GravitySource> gs;

    Particle(Sketch sketch, PVector pos, PVector v, float mass, int color) {
        this.sk = sketch;
        this.pos = pos;
        this.v = v;
        this.mass = mass;
        this.color = color;
        this.a = new PVector();

        maxSpeed = 10;
        perlinStrength = 2f;
        seed = sk.random(1000);
    }

    public void update() {
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
        applyPerlinEngine();
        step();
    }

    private void applyGravity(GravitySource g) {
        float d = pos.dist(g.center);
        if (d < g.range) return;
        PVector gForce = g.center.copy().sub(pos).mult(Sketch.G_CONSTANT * g.mass * mass / (d * d));
//        PVector gForce = g.center.copy().sub(pos).mult(Sketch.G_CONSTANT * Sketch.sqrt(g.mass * mass) / d);
        a.add(gForce);
    }

    private void applyPerlinEngine() {
        float dir = PConstants.PI * 4.12341212f * sk.noise(seed, sk.t);
        PVector force = PVector.fromAngle(dir).mult(perlinStrength);
        a.add(force);
    }

    private void applyDamping() {
        PVector damp = v.mult(-0.03f);
        a.add(damp);
    }

    private void step() {
        v.add(a).limit(maxSpeed);
        pos.add(v);
    }

    private boolean boundaryCheck() {
        return pos.x < -sk.width / 2 || pos.x > sk.width / 2 || pos.y < -sk.height / 2 || pos.y > sk.height / 2;
    }

    public void display() {
        sk.stroke(color);
        sk.point(pos.x, pos.y);
    }
}
