import processing.core.PVector;

class StaticGravitySource extends GravitySource {

    StaticGravitySource(PVector center, float mass, float range) {
        super(center, mass, range);
    }

    void move(float x, float y) {
        center.x = x;
        center.y = y;
    }
}
