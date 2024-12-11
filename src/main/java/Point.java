import processing.core.PApplet;
import processing.core.PVector;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static parameters.Parameters.*;
import static processing.core.PApplet.*;

public class Point {
    private static int idCounter = 0;
    private static PApplet pApplet;

    private final int id;
    private final PVector position;
    private final PVector speed;
    private List<Point> neighbours;

    public Point(float x, float y) {
        this.id = idCounter++;
        position = new PVector(x, y);
        speed = new PVector(0, 0);
    }

    public static void setPApplet(PApplet pApplet) {
        Point.pApplet = pApplet;
    }

    public void computeSpeed(Point[] points) {

        Map<Point, Float> distancesToPoints = Arrays.stream(points)
                .collect(Collectors.toMap(Function.identity(), p -> PVector.dist(p.position, position)));

        neighbours = distancesToPoints.keySet()
                .stream()
                .sorted((p, q) -> Float.compare(distancesToPoints.get(p), distancesToPoints.get(q)))
                // Closest point is itself, ignore it
                .skip(1)
                .limit(NUMBER_OF_NEIGHBOURS)
                .toList();

        float d = neighbours.stream()
                .map(distancesToPoints::get)
                .reduce(0f, Float::sum) / NUMBER_OF_NEIGHBOURS;
        PVector acceleration = neighbours.stream()
                .map(point -> point.position)
                .reduce(new PVector(0, 0),
                        (acc, neighbourPosition) -> acc.add(
                                PVector.sub(neighbourPosition, position)
                                        .setMag(PVector.dist(neighbourPosition, position) - d)
                        ));
        speed.mult(SPEED_LOSS).add(acceleration.mult(FORCE));
    }

    public void render() {
        position.add(speed);
        neighbours.forEach(neighbour -> sweep(position.x, position.y, neighbour.position.x, neighbour.position.y));
    }

    private void sweep(float x1, float y1, float x2, float y2) {
        float l = sqrt(sq(x1 - x2) + sq(y1 - y2));
        PVector speed = new PVector(x2 - x1, y2 - y1).setMag(l / SWEEP_AMOUNT);
        float x = x1, y = y1;
        for (int i = 0; i < SWEEP_AMOUNT; i++) {
            pApplet.stroke(STROKE_COLOR.red() + 36 * pApplet.randomGaussian(),
                    constrain(STROKE_COLOR.green() + 10 * pApplet.randomGaussian(), 0, 100),
                    constrain(STROKE_COLOR.blue() + 10 * pApplet.randomGaussian(), 0, 100),
                    STROKE_COLOR.alpha());
            x += speed.x;
            y += speed.y;
            float r = SWEEP_GAUSSIAN_FACTOR * pApplet.randomGaussian();
            pApplet.point(x + r * speed.x, y + r * speed.y);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Point point)) {
            return false;
        }
        return point.id == id;
    }
}
