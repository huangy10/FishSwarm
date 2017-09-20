import processing.core.PVector;

abstract class GravitySource {
    PVector center;
    float mass;
    float range;

    GravitySource(PVector center, float mass, float range) {
        this.center = center;
        this.mass = mass;
        this.range = range;
    }
}
