import processing.core.PVector;

public class GravitySource {
    PVector center;
    float mass;

    GravitySource(PVector center, float mass) {
        this.center = center;
        this.mass = mass;
    }

    public void move(float x, float y) {
        PVector t = new PVector(x, y);
        PVector d = t.sub(center);
        center.add(d.limit(3));
    }
}
