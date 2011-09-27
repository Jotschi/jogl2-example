package demos.nehe.lesson34;

import javax.media.opengl.GL2;

class RenderMode {
    public static final RenderMode QUADS = new RenderMode(GL2.GL_QUADS);
    public static final RenderMode LINES = new RenderMode(GL2.GL_LINES);
    private int value;

    private RenderMode(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
