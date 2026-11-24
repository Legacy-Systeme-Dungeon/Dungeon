package engine.rendering.renderingPipeline;

import java.util.List;

public class RenderingPipeline {
  private List<RenderPass> renderPasses;
  private RenderContext context;

  public RenderingPipeline(List<RenderPass> renderPasses, RenderContext context) {
    this.renderPasses = renderPasses;
    this.context = context;
  }

  public void execute() {
    // TODO execute render passes
  }

  public RenderContext context() {
    return context;
  }

}
