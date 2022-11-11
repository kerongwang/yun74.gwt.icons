package yun74.bindery.generator;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

import yun74.bindery.GWTIcon;

@SupportedAnnotationTypes("yun74.bindery.GWTIcon")
public class GWTIconsMavenProcesser extends AbstractProcessor {
	private Filer mFiler;
	private Messager mMessager;
	private Elements mElementUtils;
	private final String basepath = "src/main/java";
	private final Map<String, GWTIconSVGParser> allsvg = new HashMap<>();

	@Override
	public SourceVersion getSupportedSourceVersion() {
		if (SourceVersion.latest().compareTo(SourceVersion.RELEASE_8) > 0)
			return SourceVersion.latest();

		return SourceVersion.RELEASE_8;
	}

	private void iteratorResource(Path path, String ext) {
		try {
			Files.list(path).filter(p -> {
				return p.toFile().isDirectory() || p.toString().endsWith(ext);
			}).forEach(p -> {
				if (p.toFile().isDirectory())
					iteratorResource(p, ext);
				else {
					try (InputStream in = new FileInputStream(p.toFile())) {
						GWTIconSVGParser svg = GWTIconSVGParser.parser(in);
						if(null != svg) {
							svg.filePath = p;
							allsvg.put(p.getFileName().toString().replace(ext, ""), svg);
						}
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		mFiler = processingEnv.getFiler();
		mMessager = processingEnv.getMessager();
		mElementUtils = processingEnv.getElementUtils();
		// list all files ends with .svg
		iteratorResource(Paths.get(basepath), ".svg");
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		Set<? extends Element> bindViewElements = roundEnv.getElementsAnnotatedWith(GWTIcon.class);

		if (bindViewElements.size() == 0)
			return false;

		for (Element element : bindViewElements) {
			PackageElement packageElement = mElementUtils.getPackageOf(element);
			String requirefile = element.getSimpleName().toString().replace("_", "-");
			Object path = allsvg.get(requirefile);
			if (null != path)
				note("Found: " + requirefile + " as: " + path.toString());
			else
				note("missing: " + packageElement.getQualifiedName() + "." + requirefile);
		}

		return true;
	}

	private void note(String msg) {
		mMessager.printMessage(Diagnostic.Kind.NOTE, msg);
	}
}
