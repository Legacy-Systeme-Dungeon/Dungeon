package engine.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Cell;
import com.badlogic.gdx.utils.Align;
import org.junit.jupiter.api.Test;

class BaseContainerUITest {

  private static String layout(BaseContainerUI ui) {
    Cell<?> cell = ui.getCells().first();

    String tableAlign = Align.toString(ui.getAlign());
    String cellAlign = Align.toString(cell.getAlign());
    String padding =
        String.format(
            "%s,%s,%s,%s",
            cell.getPadTop(), cell.getPadLeft(), cell.getPadBottom(), cell.getPadRight());

    return tableAlign + " | " + cellAlign + " | " + padding;
  }

  @Test
  void constructorWithContentOnly() {
    BaseContainerUI ui = new BaseContainerUI(new Actor());
    assertEquals("center,center | center,center | 0.0,0.0,0.0,0.0", layout(ui));
  }

  @Test
  void constructorWithAlign() {
    BaseContainerUI ui = new BaseContainerUI(new Actor(), Align.topLeft);
    assertEquals("top,left | top,left | 0.0,0.0,0.0,0.0", layout(ui));
  }

  @Test
  void constructorWithGrow() {
    BaseContainerUI ui = new BaseContainerUI(new Actor(), true, false);
    Cell<?> cell = ui.getCells().first();
    assertEquals("center,center | center,center | 0.0,0.0,0.0,0.0", layout(ui));
    assertEquals(1, cell.getExpandX());
    assertEquals(1f, cell.getFillX());
  }

  @Test
  void constructorTopRight() {
    BaseContainerUI ui = new BaseContainerUI(new Actor(), Align.topRight, 10, 20, false, false);
    assertEquals("top,right | top,right | 20.0,0.0,0.0,10.0", layout(ui));
  }

  @Test
  void constructorBottomLeft() {
    BaseContainerUI ui = new BaseContainerUI(new Actor(), Align.bottomLeft, 10, 20, false, false);
    assertEquals("bottom,left | bottom,left | 0.0,10.0,20.0,0.0", layout(ui));
  }

  @Test
  void setAlignRepositions() {
    BaseContainerUI ui = new BaseContainerUI(new Actor(), Align.center, 10, 20, false, false);
    ui.setAlign(Align.topRight);
    assertEquals("top,right | top,right | 20.0,0.0,0.0,10.0", layout(ui));
  }

  @Test
  void setOffsetRepositions() {
    BaseContainerUI ui = new BaseContainerUI(new Actor(), Align.topRight, 0, 0, false, false);
    ui.setOffset(10, 20);
    assertEquals("top,right | top,right | 20.0,0.0,0.0,10.0", layout(ui));
  }

  @Test
  void setPlacementRepositions() {
    BaseContainerUI ui = new BaseContainerUI(new Actor(), Align.center, 0, 0, false, false);
    ui.setPlacement(Align.bottomLeft, 10, 20);
    assertEquals("bottom,left | bottom,left | 0.0,10.0,20.0,0.0", layout(ui));
  }

  @Test
  void setContentReplacesOldContentAndKeepsPlacement() {
    Actor oldContent = new Actor();
    Actor newContent = new Actor();
    BaseContainerUI ui = new BaseContainerUI(oldContent, Align.topRight, 10, 20, false, false);

    ui.setContent(newContent);

    assertSame(newContent, ui.getCells().first().getActor());
    assertTrue(ui.getChildren().contains(newContent, true));
    assertFalse(ui.getChildren().contains(oldContent, true));
    assertEquals("top,right | top,right | 20.0,0.0,0.0,10.0", layout(ui));
  }

  @Test
  void getContentReturnsCurrentContent() {
    Actor content = new Actor();
    BaseContainerUI ui = new BaseContainerUI(content, false, false);
    assertSame(content, ui.getContent());
  }

  @Test
  void onResizeChangesSize() {
    BaseContainerUI ui = new BaseContainerUI(new Actor(), false, false);
    ui.onResize(800, 600);
    assertEquals(800f, ui.getWidth());
    assertEquals(600f, ui.getHeight());
  }
}
