import processing.core.*;

import java.util.ArrayList;
import java.util.LinkedList;

public class Particle extends GravitySource {
    PVector pos;
    PVector v;
    PVector a;
    float   seed;
    float   perlinStrength;
    float   maxSpeed;
    int     color;
    int     stopColor;
    int     idx;
    float   resistRange;
    float   size;
    boolean dead = false;
    boolean asGravitySource = false;

    private static final int    TRACE_SIZE = 5;
    private static final float  DAMPING = 0.05f;
    private static final float  RESIST_STRENGTH = 0.5f;
    private static final float  BOUND_AVOID_RANGE = 80;
    private static final float  BOUND_AVOID_STRENGTH = 1f;
    private static final float  PERLIN_STRENGTH = 1.5f;
    private static final float  EXTRA_BOUNDARY = 100f;

    LinkedList<PVector> trace;
    Particle[]  swarm;
    private Sketch sk;

    ArrayList<GravitySource> gs;

    Particle(int idx, Sketch sketch, PVector pos, PVector v, float mass, int color, int stopColor) {
        super(pos, mass, 100);

        this.idx = idx;
        this.sk = sketch;
        this.pos = pos;
        this.v = v;
        this.mass = mass;
        this.pmass = mass;
        this.color = color;
        this.stopColor = stopColor;
        this.a = new PVector();

        maxSpeed = sk.random(5, 13);
        size = 5;
        resistRange = size * 2;
        perlinStrength = PERLIN_STRENGTH;
        seed = sk.random(1000);

        trace = new LinkedList<>();
    }

    void update() {
        if (dead) return;
        if (boundaryCheck()) {
            pos.x = sk.random(sk.width) - sk.width / 2;
            pos.y = sk.random(sk.height) - sk.height / 2;
            trace.clear();
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
//        applyResistance();
        applyPerlinEngine();
        applyBoundaryAvoid();
        swim();
        pulse();
        step();
    }

    private void applyGravity(GravitySource g) {
        if (asGravitySource && g.type != 0) return;
        float d = pos.dist(g.center);
        if (d < g.range) return;
        PVector gForce = g.center.copy().sub(pos)
                .mult(Sketch.G_CONSTANT * g.mass * mass / (d * d));
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

    private void applyBoundaryAvoid() {
        if ( pos.x + sk.halfWidth < BOUND_AVOID_RANGE ) {
            a.add(new PVector(BOUND_AVOID_STRENGTH, 0));
        } else if (pos.x + BOUND_AVOID_RANGE > sk.halfWidth) {
            a.add(new PVector(-BOUND_AVOID_STRENGTH, 0));
        }

        if ( pos.y + sk.halfHeight < BOUND_AVOID_RANGE ) {
            a.add(new PVector(0, BOUND_AVOID_STRENGTH));
        } else if ( pos.y + BOUND_AVOID_RANGE > sk.halfHeight) {
            a.add(new PVector(0, -BOUND_AVOID_STRENGTH));
        }
    }

    private void swim() {
        if (sk.swim) {
            a.add(v.copy().normalize().mult(getSwimForce()));
        }
    }

    private float getSwimForce() {
        if (counter == 0)
            return asGravitySource ? 0.4f * sk.noise(seed, sk.t) : 0.2f;
        else {
            return 0.6f;
        }
    }

    private int counter = 0;
    private float pmass = 0;

    private void pulse() {
        if (!asGravitySource) return;
        if (counter > 0) {
            mass = 2 * pmass;
            counter -= 1;
        } else {
            mass = pmass;
            if (sk.random(1) < 0.01) {
                counter = 30;
            }
        }
    }

    private void step() {
        v.add(a).limit(maxSpeed);
        pos.add(v);
    }

    private boolean boundaryCheck() {
        return pos.x < -sk.halfWidth - EXTRA_BOUNDARY ||
                pos.x > sk.halfWidth + EXTRA_BOUNDARY ||
                pos.y < -sk.halfHeight - EXTRA_BOUNDARY ||
                pos.y > sk.halfHeight + EXTRA_BOUNDARY;
    }

    void display() {
        if (dead) return;
        if (trace.isEmpty()) return;
        sk.stroke(sk.lerpColor(stopColor, color, v.mag() / maxSpeed));
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

    void setMass(float mass) {
        this.mass = mass;
        this.pmass = mass;
    }
}
