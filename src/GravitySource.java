import processing.core.PVector;

public class GravitySource {
    PVector center;
    float mass;
    float range;

    GravitySource(PVector center, float mass, float range) {
        this.center = center;
        this.mass = mass;
        this.range = range;
    }

    public void move(float x, float y) {
        center.x = x;
        center.y = y;
    }
}
