package engine.rendering.renderingPipeline;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import engine.components.PositionComponent;
import engine.utils.components.draw.shader.ShaderList;

public abstract class RenderPass {

  public abstract void render(RenderContext renderContext);

  protected FrameBuffer processShaders(
      TextureRegion textureRegion, ShaderList shaderList, PositionComponent positionComponent) {
    return null; // TODO
  }

  protected FrameBuffer drawToIntermediateFbo(Runnable renderAction, ShaderList shaderList, int width, int height) {
    return null; // TODO
  }

  protected void setTextureFiltering(Texture t) {
    // TODO
  }

  protected void drawFboToBatch(FrameBuffer fbo, int width, int height) {
    // TODO
  }

  protected FrameBuffer applyShadersAndCompose(
    FrameBuffer fboA, FrameBuffer fboB, ShaderList shaders, int width, int height) {
    return null; // TODO
  }

}
