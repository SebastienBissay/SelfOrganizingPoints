import processing.core.PApplet;

import static parameters.Parameters.*;
import static save.SaveUtil.saveSketch;

public class SelfOrganizingPoints extends PApplet {

    private Point[] points;

    public static void main(String[] args) {
        PApplet.main(SelfOrganizingPoints.class);
    }

    @Override
    public void settings() {
        size(WIDTH, HEIGHT);
        randomSeed(SEED);
    }

    @Override
    public void setup() {
        colorMode(COLOR_MODE, 360, 100, 100, 100);
        background(BACKGROUND_COLOR.red(), BACKGROUND_COLOR.green(), BACKGROUND_COLOR.blue());
        blendMode(BLEND_MODE);
        noFill();

        Point.setPApplet(this);

        points = new Point[NUMBER_OF_POINTS];
        for (int i = 0; i < NUMBER_OF_POINTS; i++) {
            points[i] = new Point(random(MARGIN, WIDTH - MARGIN), random(MARGIN, HEIGHT - MARGIN));
        }

    }

    @Override
    public void draw() {
        for (Point p : points) {
            p.computeSpeed(points);
            p.render();
        }

        if (frameCount >= NUMBER_OR_ITERATIONS) {
            noLoop();
            saveSketch(this);
        }
    }
}
