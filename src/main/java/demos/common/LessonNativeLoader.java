package demos.common;

import com.jogamp.common.jvm.JNILibLoaderBase;
import com.jogamp.gluegen.runtime.NativeLibLoader;


public class LessonNativeLoader {

	static {
		ClassPathLoader loader = new ClassPathLoader();
		loader.loadLibrary("gluegen-rt", true);
		JNILibLoaderBase.setLoadingAction(loader);
		NativeLibLoader.disableLoading();
	}

	
}
