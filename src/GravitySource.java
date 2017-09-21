import processing.core.PVector;

abstract class GravitySource {
    PVector     center;
    float       mass;
    float       range;
    int         type;           // 0 for uniform, 1 for leader

    GravitySource(PVector center, float mass, float range) {
        this.center = center;
        this.mass = mass;
        this.range = range;
        this.type = 0;
    }
}
