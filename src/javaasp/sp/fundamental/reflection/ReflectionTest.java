package javaasp.sp.fundamental.reflection;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

public class ReflectionTest {
	public static void main(String[] args) {
		URLClassLoader classLoader;
//		String workingDirectory = System.getProperty("user.dir");
//		String jarFilePath = workingDirectory + "\\Calculator.jar";
		
		String jarFilePath = "./Calculator.jar";
		File jarFile = new File(jarFilePath);
		
		try {
			URL classURL = new URL("jar:" + jarFile.toURI().toURL() + "!/");
			classLoader = new URLClassLoader(new URL[] {classURL});
			
			Class<?> c = classLoader.loadClass("Calculator");
			Constructor<?> constructor = c.getConstructor(new Class[]{});
			Object object = constructor.newInstance(new Object[]{});
			
			Method method = c.getMethod("add", new Class[]{Integer.TYPE, Integer.TYPE});
			System.out.println(method.invoke(object, 1, 2));
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
}
