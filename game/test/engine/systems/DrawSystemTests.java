package engine.systems;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import engine.Entity;
import engine.Game;
import engine.components.DrawComponent;
import engine.components.PositionComponent;
import engine.level.elements.ILevel;
import engine.utils.Point;
import java.util.List;
import java.util.Optional;
import java.util.TreeMap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DrawSystemTest {

  private ILevel mockLevel;

  @BeforeEach
  void setUp() {
    // Reset camera position to origin for deterministic frustum culling
    CameraSystem.camera().viewportWidth = 20;
    CameraSystem.camera().viewportHeight = 20;
    CameraSystem.camera().position.set(0, 0, 0);
    CameraSystem.camera().update();

    mockLevel = mock(ILevel.class);
    when(mockLevel.tileAt(any(Point.class))).thenReturn(Optional.empty());
    Game.currentLevel(mockLevel);
  }

  @AfterEach
  void tearDown() {
    Game.currentLevel(null);
  }

  private Entity createEntity(float x, float y, float sortOffset, boolean visible) {
    Entity entity = new Entity();
    DrawComponent dc = mock(DrawComponent.class);
    when(dc.getWidth()).thenReturn(1.0f);
    when(dc.getHeight()).thenReturn(1.0f);
    when(dc.sortOffset()).thenReturn(sortOffset);
    when(dc.isVisible()).thenReturn(visible);

    PositionComponent pc = new PositionComponent(new Point(x, y));

    entity.add(dc);
    entity.add(pc);
    return entity;
  }

  @Test
  void testSortingByYAndSortOffset() {
    // Entity A: Y=3, offset=0 -> effective Y = 3 (rendered first)
    Entity entityA = createEntity(0, 3, 0f, true);
    // Entity B: Y=2, offset=5 -> effective Y = 7 (rendered first among all)
    Entity entityB = createEntity(0, 2, 5f, true);
    // Entity C: Y=1, offset=0 -> effective Y = 1 (rendered last)
    Entity entityC = createEntity(0, 1, 0f, true);

    List<Entity> entities = List.of(entityA, entityB, entityC);

    List<DrawSystem.DSData> result = DrawSystem.prepareEntityGroup(entities);

    assertEquals(3, result.size());
    assertEquals(entityB, result.get(0).e()); // effective Y = 7
    assertEquals(entityA, result.get(1).e()); // effective Y = 5
    assertEquals(entityC, result.get(2).e()); // effective Y = 1
  }

  @Test
  void testFilteringInvisibleEntities() {
    Entity visibleEntity = createEntity(0, 2, 0f, true);
    Entity invisibleEntity = createEntity(0, 4, 0f, false);

    List<Entity> entities = List.of(visibleEntity, invisibleEntity);

    List<DrawSystem.DSData> result = DrawSystem.prepareEntityGroup(entities);

    assertEquals(1, result.size());
    assertEquals(visibleEntity, result.get(0).e());
  }

}
