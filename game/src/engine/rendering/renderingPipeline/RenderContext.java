package engine.rendering.renderingPipeline;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import engine.Entity;
import engine.rendering.AtmosphereRenderer;
import engine.systems.DrawSystem;
import engine.utils.components.draw.FrameBufferPool;
import engine.utils.components.draw.shader.ShaderList;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public record RenderContext(
  FrameBufferPool FBO_POOL,
  TreeMap<Integer, List<Entity>> sortedEntities,
  Matrix4 fboProjectionMatrix,
  TextureRegion fboRegion,
  Sprite emissiveSprite,
  Map<Entity, FrameBuffer> entityFboCache,
  Map<DrawSystem.DSData, FrameBuffer> outlined,
  ShaderList levelShaders,
  AtmosphereRenderer atmosphereRenderer,
  FrameBuffer FboA,
  FrameBuffer FboB,
  FrameBuffer levelBuffer,
  TreeMap<Integer, FrameBuffer> depthFbos,
  List<DrawSystem.DSData> drawn
) {
  // TODO Instanziierung der Felder
}
