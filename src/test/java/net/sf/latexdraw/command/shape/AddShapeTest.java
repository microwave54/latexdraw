package net.sf.latexdraw.command.shape;

import io.github.interacto.jfx.test.UndoableCmdTest;
import java.util.stream.Stream;
import net.sf.latexdraw.model.ShapeFactory;
import net.sf.latexdraw.model.api.shape.Drawing;
import net.sf.latexdraw.model.api.shape.Shape;
import net.sf.latexdraw.service.PreferencesService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for the command AddShape. Generated by Interacto test-gen.
 */
@Tag("command")
class AddShapeTest extends UndoableCmdTest<AddShape> {
	Drawing drawing;
	Shape shape;

	@BeforeEach
	void setUp() {
		bundle = new PreferencesService().getBundle();
	}

	@Override
	protected Stream<Runnable> canDoFixtures() {
		return Stream.of(() -> {
			drawing = ShapeFactory.INST.createDrawing();
			shape = Mockito.mock(Shape.class);
			cmd = new AddShape(shape, drawing);
		});
	}

	@Override
	protected Runnable doChecker() {
		return () -> {
			assertThat(drawing.contains(shape)).isTrue();
			assertThat(drawing.size()).isEqualTo(1);
			assertThat(drawing.isModified()).isTrue();
		};
	}

	@Override
	protected Runnable undoChecker() {
		return () -> {
			assertThat(drawing.contains(shape)).isFalse();
			assertThat(drawing.isEmpty()).isTrue();
			assertThat(drawing.isModified()).isFalse();
		};
	}

	@AfterEach
	void tearDownAddShapeTest() {
		this.drawing = null;
		this.shape = null;
	}
}
