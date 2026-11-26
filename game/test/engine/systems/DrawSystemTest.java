package engine.systems;

import engine.Entity;
import engine.components.DrawComponent;
import engine.components.PositionComponent;
import engine.utils.components.MissingComponentException;
import org.junit.jupiter.api.Test;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class DrawSystemTest {

  @Test
  void shouldBuildDSDataWithCorrectComponents() {
    // Arrange
    Entity entity = mock(Entity.class);
    DrawComponent dc = mock(DrawComponent.class);
    PositionComponent pc = mock(PositionComponent.class);

    when(entity.fetch(DrawComponent.class))
      .thenReturn(Optional.of(dc));

    when(entity.fetch(PositionComponent.class))
      .thenReturn(Optional.of(pc));

    // Act
    DrawSystem.DSData result = DrawSystem.DSData.build(entity);

    // Assert
    assertSame(entity, result.e());
    assertSame(dc, result.dc());
    assertSame(pc, result.pc());
  }

  @Test
  void shouldThrowExceptionWhenDrawComponentIsMissing() {
    // Arrange
    Entity entity = mock(Entity.class);

    when(entity.fetch(DrawComponent.class))
      .thenReturn(Optional.empty());

    //Act & Assert
    assertThrows(MissingComponentException.class, () -> DrawSystem.DSData.build(entity));

    verify(entity).fetch(DrawComponent.class);
    verify(entity, never()).fetch(PositionComponent.class);
  }

  @Test
  void shouldThrowExceptionWhenPositionComponentIsMissing() {
    // Arrange
    Entity entity = mock(Entity.class);
    DrawComponent dc = mock(DrawComponent.class);

    when(entity.fetch(DrawComponent.class))
      .thenReturn(Optional.of(dc));

    when(entity.fetch(PositionComponent.class))
      .thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(MissingComponentException.class, ()-> DrawSystem.DSData.build(entity));

    verify(entity).fetch(DrawComponent.class);
    verify(entity).fetch(PositionComponent.class);
  }
}
