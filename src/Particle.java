import processing.core.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

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
    float   resistRange;
    float   size;
    boolean dead = false;

    private static final int TRACE_SIZE = 5;
    private static final float DAMPING = 0.05f;
    private static final float RESIST_STRENGTH = 0.1f;

    LinkedList<PVector> trace;
    Particle[]  swarm;
    private Sketch sk;

    ArrayList<GravitySource> gs;

    Particle(int idx, Sketch sketch, PVector pos, PVector v, float mass, int color) {
        this.idx = idx;
        this.sk = sketch;
        this.pos = pos;
        this.v = v;
        this.mass = mass;
        this.color = color;
        this.a = new PVector();

        maxSpeed = sk.random(10, 13);
        size = 5;
        resistRange = size * 2;
        perlinStrength = 1f;
        seed = sk.random(1000);

        trace = new LinkedList<>();
    }

    void update() {
        if (dead) return;
        if (boundaryCheck()) {
            pos.x = sk.random(sk.width) - sk.width / 2;
            pos.y = sk.random(sk.height) - sk.height / 2;
            trace.clear();
//            dead = true;
            return;
        }
        if (trace.size() >= TRACE_SIZE) {
            PVector p = trace.pollLast();
            p.x = pos.x;
            p.y = pos.y;
            trace.offerFirst(p);
        } else {
            trace.offerFirst(pos.copy());
        }

        a.x = 0;
        a.y = 0;
        for (GravitySource g : gs) {
            applyGravity(g);
        }
        applyDamping();
        applyResistance();
        applyPerlinEngine();
        swim();
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
        PVector damp = v.copy().mult(-DAMPING);
        a.add(damp);
    }

    private void applyResistance() {
        for (Particle p : swarm) {
            if (p.idx == idx) continue;
            PVector d = pos.copy().sub(p.pos);
            float distance = d.mag();
            PVector resist = d.normalize()
                    .mult(p.mass * mass * Sketch.G_CONSTANT / (distance * distance * 10))
                    .mult(RESIST_STRENGTH);
            a.add(resist);
        }
    }

    private void swim() {
        if (sk.swim) {
            a.add(v.normalize().mult(2f));
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
        if (dead) return;
        if (trace.isEmpty()) return;
        sk.stroke(sk.lerpColor(sk.color(50), color, v.mag() / maxSpeed));
        sk.strokeWeight(size);
        PVector pre = trace.get(0);
        if (trace.size() == 1) {
            sk.point(pre.x, pre.y);
        } else {
            PVector p;
            for (int i = 1; i < trace.size(); i += 1) {
                sk.strokeWeight(PApplet.lerp(size, 1, (float)i / trace.size()));
                p = trace.get(i);
                sk.line(p.x, p.y, pre.x, pre.y);
                pre = p;
            }
        }
    }
}
