package net.sf.latexdraw.command.shape;

import io.github.interacto.jfx.test.UndoableCmdTest;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import net.sf.latexdraw.LatexdrawExtension;
import net.sf.latexdraw.model.MathUtils;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.Group;
import net.sf.latexdraw.model.api.shape.Point;
import net.sf.latexdraw.model.api.shape.Shape;
import net.sf.latexdraw.service.LaTeXDataService;
import net.sf.latexdraw.service.PreferencesService;
import net.sf.latexdraw.util.Tuple;
import net.sf.latexdraw.view.jfx.Canvas;
import net.sf.latexdraw.view.jfx.ViewFactory;
import net.sf.latexdraw.view.jfx.ViewShape;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the command AlignShapes. Generated by Interacto test-gen.
 */
@Tag("command")
@ExtendWith(LatexdrawExtension.class)
class AlignShapesTest extends UndoableCmdTest<AlignShapes> {
	List<Tuple<Point, Point>> memento;
	AlignShapes.Alignment alignment;
	List<ViewShape<?>> views;
	Canvas canvas;
	Group shape;

	@BeforeEach
	void setUp() {
		bundle = new PreferencesService().getBundle();
	}

	@Override
	protected Stream<Runnable> cannotDoFixtures() {
		return Stream.of(() -> cmd = new AlignShapes(Mockito.mock(Canvas.class), AlignShapes.Alignment.BOTTOM, ShapeFactory.INST.createGroup()));
	}

	@Override
	protected Stream<Runnable> canDoFixtures() {
		return Stream.of(AlignShapes.Alignment.values()).map(alig -> () -> {
			canvas = Mockito.mock(Canvas.class);
			shape = ShapeFactory.INST.createGroup();
			shape.addShape(ShapeFactory.INST.createRectangle(ShapeFactory.INST.createPoint(10d, -2d), 6d, 6d));
			shape.addShape(ShapeFactory.INST.createRectangle(ShapeFactory.INST.createPoint(-5d, 20d), 12d, 15d));
			shape.addShape(ShapeFactory.INST.createRectangle(ShapeFactory.INST.createPoint(14d, 60d), 20d, 16d));
			views = new ArrayList<>();

			final ViewFactory vfac = new ViewFactory(Mockito.mock(LaTeXDataService.class));
			IntStream.range(0, shape.size()).forEach(i -> {
				views.add(vfac.createView(shape.getShapeAt(i).orElseThrow()).get());
				Mockito.when(canvas.getViewFromShape(shape.getShapeAt(i).orElseThrow())).thenReturn(Optional.of(views.get(i)));
			});

			alignment = alig;
			cmd = new AlignShapes(canvas, alignment, shape);
			memento = shape.getShapes().stream().map(sh -> new Tuple<>(sh.getTopLeftPoint(), sh.getBottomRightPoint())).collect(Collectors.toList());
		});
	}

	@Override
	protected Stream<Runnable> doCheckers() {
		return Stream.of(() -> {
			switch(alignment) {
				case TOP:
					assertThat(shape.getShapes().stream().mapToDouble(sh -> sh.getTopLeftPoint().getY()).boxed().collect(Collectors.toList())).allMatch(value -> MathUtils.INST.equalsDouble(value, -2d, 0.000001d));
					break;
				case BOTTOM:
					assertThat(shape.getShapes().stream().mapToDouble(sh -> sh.getBottomRightPoint().getY()).boxed().collect(Collectors.toList())).allMatch(value -> MathUtils.INST.equalsDouble(value, 76d, 0.000001d));
					break;
				case LEFT:
					assertThat(shape.getShapes().stream().mapToDouble(sh -> sh.getTopLeftPoint().getX()).boxed().collect(Collectors.toList())).allMatch(value -> MathUtils.INST.equalsDouble(value, -5d, 0.000001d));
					break;
				case RIGHT:
					assertThat(shape.getShapes().stream().mapToDouble(sh -> sh.getBottomRightPoint().getX()).boxed().collect(Collectors.toList())).allMatch(value -> MathUtils.INST.equalsDouble(value, 34d, 0.000001d));
					break;
				case MID_HORIZ:
					assertThat(shape.getShapes().stream().mapToDouble(sh -> sh.getGravityCentre().getY()).boxed().collect(Collectors.toList())).allMatch(value -> MathUtils.INST.equalsDouble(value, 37d, 0.000001d));
					break;
				case MID_VERT:
					assertThat(shape.getShapes().stream().mapToDouble(sh -> sh.getGravityCentre().getX()).boxed().collect(Collectors.toList())).allMatch(value -> MathUtils.INST.equalsDouble(value, 14.5d, 0.000001d));
					break;
			}
		});
	}

	@Override
	protected Stream<Runnable> undoCheckers() {
		return Stream.of(() -> {
			int i = 0;
			for(final Shape shape : shape.getShapes()) {
				assertThat(shape.getTopLeftPoint()).isEqualTo(memento.get(i).a);
				assertThat(shape.getBottomRightPoint()).isEqualTo(memento.get(i).b);
				i++;
			}
		});
	}

	@AfterEach
	void tearDownAlignShapesTest() {
		if(views != null) {
			views.forEach(v -> v.flush());
			views.clear();
		}
		if(shape != null) {
			shape.clear();
		}
	}
}
