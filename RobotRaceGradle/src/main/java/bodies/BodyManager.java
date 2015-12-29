/*
 * Course: Computer Graphics
 * Course Code: 2IV60
 * Assignment: RobotRace
 * Students: Arjan Boschman & Robke Geenen
 */
package bodies;

import bodies.assembly.Vertex;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.List;
import javax.media.opengl.GL2;

/**
 *
 * @author Arjan Boschman
 */
public class BodyManager {

    /**
     * The type format used for the index buffers. This integer refers to a
     * {@link GL2} enum value.
     */
    public static final int INDEX_BUFFER_TYPE = GL2.GL_UNSIGNED_INT;
    /**
     * The default buffer offset used for index buffers.
     */
    public static final int INDEX_BUFFER_OFFSET = 0;

    private int dataBufferName;

    public Initialiser makeInitialiser(GL2 gl) {
        return new Initialiser(gl);
    }

    /**
     * Must be called at the start of the draw phase, before any of the bodies
     * managed by this instance are drawn.
     *
     * @param gl The instance of GL2 responsible for drawing the bodies.
     * @see #endDraw Must be called in conjunction with this method.
     */
    public void startDraw(GL2 gl) {
        gl.glEnableClientState(GL2.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL2.GL_NORMAL_ARRAY);
        //Use the name under which the data buffer was stored to bind it.
        gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, dataBufferName);
        final int stride = 2 * Vertex.COORD_COUNT * Double.BYTES;
        /**
         * Tell OpenGL what formats and what stride length to expect when
         * extracting vertex coordinate information from the data buffer.
         */
        gl.glVertexPointer(Vertex.COORD_COUNT, GL2.GL_DOUBLE, stride, 0);
        /**
         * Tell OpenGL what formats, stride length and offset to expect when
         * extracting normal information from the data buffer.
         */
        final int normalPointerOffset = Vertex.COORD_COUNT * Double.BYTES;
        gl.glNormalPointer(GL2.GL_DOUBLE, stride, normalPointerOffset);
    }

    /**
     * Must be called at the end of the draw phase, after all the bodies managed
     * by this instance are drawn.
     *
     * @param gl The instance of GL2 responsible for drawing the bodies.
     * @see #startDraw Must be called in conjunction with this method.
     */
    public void endDraw(GL2 gl) {
        gl.glDisableClientState(GL2.GL_NORMAL_ARRAY);
        gl.glDisableClientState(GL2.GL_VERTEX_ARRAY);
    }

    @SuppressWarnings("PublicInnerClass")
    public class Initialiser {

        private final GL2 gl;
        /**
         * The data buffer; contains all vertices and normals given to this
         * object through {@link #addData}.
         */
        private DoubleBuffer dataBuffer = DoubleBuffer.allocate(0);

        private Initialiser(GL2 gl) {
            this.gl = gl;
        }

        /**
         * TODO:...
         *
         * It is very important that this method is called. After calling this
         * method, the initialiser cannot be used anymore.
         */
        public void finish() {
            //Obtain buffer name.
            final int[] singleBufferName = new int[1];
            gl.glGenBuffers(1, singleBufferName, 0);
            BodyManager.this.dataBufferName = singleBufferName[0];
            //Push the data buffer to OpenGL.
            gl.glBindBuffer(GL2.GL_ARRAY_BUFFER, BodyManager.this.dataBufferName);
            gl.glBufferData(GL2.GL_ARRAY_BUFFER, dataBuffer.capacity() * Double.BYTES, dataBuffer, GL2.GL_STATIC_DRAW);
            //Erase buffer data so nothing will stay in memory or silently fail if people abuse this class.
            dataBuffer = null;
        }

        /**
         * TODO:...
         *
         * @param data
         * @param indexBuffers
         * @return Index buffer names.
         */
        public int[] addData(DoubleBuffer data, List<IntBuffer> indexBuffers) {
            //Append databuffer to the one already stored in here.
            final int coordLength = 2 * Vertex.COORD_COUNT;
            final int oldLength = appendToDataBuffer(data) / coordLength;
            //Increment each index by the previous length of the databuffer.
            incrementEachElement(indexBuffers, oldLength);
            //Retrieve index buffer names and push them to OpenGL.
            final int[] bufferNames = pushIndexArraysToGL(indexBuffers);
            //Return index buffer names.
            return bufferNames;
        }

        /**
         * Append the given buffer to the data buffer stored as a class member.
         *
         * @param addition The buffer to append.
         * @return The length of the old buffer.
         */
        private int appendToDataBuffer(DoubleBuffer addition) {
            final int oldLength = dataBuffer.capacity();
            final DoubleBuffer newData = DoubleBuffer.allocate(oldLength + addition.capacity());
            newData.put(dataBuffer);
            newData.position(oldLength);
            newData.put(addition);
            newData.position(0);
            dataBuffer = newData;
            return oldLength;
        }

        /**
         * Increment all the elements of all the buffers by the given amount.
         *
         * @param intBuffers  The buffers.
         * @param incrementBy The amount to increments all the elements by.
         */
        private void incrementEachElement(List<IntBuffer> intBuffers, int incrementBy) {
            intBuffers.stream().forEach((intBuffer) -> {
                for (int i = 0; i < intBuffer.capacity(); i++) {
                    intBuffer.put(i, intBuffer.get(i) + incrementBy);
                }
                intBuffer.position(0);
            });
        }

        /**
         * Let OpenGL generate some buffer names and register each index buffer
         * to each of the names.
         *
         * @param indexBuffers The index buffer that are to be registered with
         *                     OpenGL.
         * @return The buffer names. Each of these corresponds to one of the
         *         index buffers given as a parameter.
         */
        private int[] pushIndexArraysToGL(List<IntBuffer> indexBuffers) {
            final int[] bufferNames = new int[indexBuffers.size()];
            gl.glGenBuffers(bufferNames.length, bufferNames, 0);
            for (int i = 0; i < indexBuffers.size(); i++) {
                gl.glBindBuffer(GL2.GL_ELEMENT_ARRAY_BUFFER, bufferNames[i]);
                gl.glBufferData(GL2.GL_ELEMENT_ARRAY_BUFFER,
                        indexBuffers.get(i).capacity() * Integer.BYTES,
                        indexBuffers.get(i), GL2.GL_STATIC_DRAW);
            }
            return bufferNames;
        }
    }

}
