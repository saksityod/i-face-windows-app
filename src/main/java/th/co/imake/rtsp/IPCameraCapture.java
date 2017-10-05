package th.co.imake.rtsp;

import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;

import javax.swing.SwingWorker;

import uk.co.caprica.vlcj.component.DirectMediaPlayerComponent;
import uk.co.caprica.vlcj.player.direct.BufferFormat;
import uk.co.caprica.vlcj.player.direct.BufferFormatCallback;
import uk.co.caprica.vlcj.player.direct.DirectMediaPlayer;
import uk.co.caprica.vlcj.player.direct.RenderCallback;
import uk.co.caprica.vlcj.player.direct.RenderCallbackAdapter;
import uk.co.caprica.vlcj.player.direct.format.RV32BufferFormat;

public class IPCameraCapture extends SwingWorker<Void, Void> {
    
    protected static final int      MAX_FPS = 30;
    protected static final int      VIDEO_WIDTH = 1280;
    protected static final int      VIDEO_HEIGHT = 720;
    public static final int video_width = 850;//935

   // private static final int height = 700;
    public static final int video_height = 500;//400
    
    private DirectMediaPlayerComponent mediaPlayerComponent ;

    private BufferedImage imageIP = null;
    // event delegate to notify about each new grabbed frame
    private IPCameraCaptureEvents callback;    
    // byte buffer
    byte[] buffer = null;
    
    public static interface IPCameraCaptureEvents {
        public void onNewFrame(BufferedImage image);
    }
    
    public IPCameraCapture( IPCameraCaptureEvents callback ) {
        this.callback = callback;
    }
    
    public boolean init() {           
    	 imageIP = GraphicsEnvironment
 	            .getLocalGraphicsEnvironment()
 	            .getDefaultScreenDevice()
 	            .getDefaultConfiguration()
 	            .createCompatibleImage(video_width, video_height);
    	BufferFormatCallback bufferFormatCallback = new BufferFormatCallback() {
            @Override
            public BufferFormat getBufferFormat(int sourceWidth, int sourceHeight) {
                return new RV32BufferFormat(video_width, video_height);
            }
        };
        mediaPlayerComponent = new DirectMediaPlayerComponent(bufferFormatCallback) {
            @Override
            public RenderCallback onGetRenderCallback() {
            	
                return new IPCameraRenderCallbackAdapter();
            }
        };
       
        mediaPlayerComponent.getMediaPlayer().playMedia(IFaceTech.media_url);
       
        return true;
    }

    @Override
    protected Void doInBackground() throws Exception {
       // while (!isCancelled()) {
            //image = imageIP  ;//frameConverter.convert(imageIP);
           // callback.onNewFrame(imageIP);
        //}
        return null;
    }
    
    public int getWidth() { return VIDEO_WIDTH; }     
    public int getHeight() { return VIDEO_HEIGHT; } 
    private class IPCameraRenderCallbackAdapter extends RenderCallbackAdapter {

        private IPCameraRenderCallbackAdapter() {
            super(new int[video_width * video_height]);
        }

        @Override
        protected void onDisplay(DirectMediaPlayer mediaPlayer, int[] rgbBuffer) {
			// Simply copy buffer to the image and repaint
        	imageIP.setRGB(0, 0, video_width, video_height, rgbBuffer, 0, video_width);
        	callback.onNewFrame(imageIP);
        }
    }
    

}
