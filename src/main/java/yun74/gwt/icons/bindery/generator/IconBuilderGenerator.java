package yun74.gwt.icons.bindery.generator;

import java.io.PrintWriter;

import com.google.gwt.core.ext.Generator;
import com.google.gwt.core.ext.GeneratorContext;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.TypeOracle;
import com.google.gwt.dev.resource.Resource;
import com.google.gwt.dev.resource.ResourceOracle;
import com.google.gwt.user.rebind.ClassSourceFileComposerFactory;
import com.google.gwt.user.rebind.SourceWriter;
import yun74.gwt.icons.bindery.IconBuilderBase;

public class IconBuilderGenerator extends Generator {
	private String implTypeName;
	private String implPackageName;

	@Override
	public String generate(TreeLogger logger, GeneratorContext context, String typeName)
			throws UnableToCompleteException {

		TypeOracle typeOracle = context.getTypeOracle();
		assert (typeOracle != null);

		JClassType objectType = typeOracle.findType(typeName);
		if (objectType == null) {
			logger.log(TreeLogger.ERROR, "Unable to find metadata for type '" + typeName + "'", null);
			throw new UnableToCompleteException();
		}

		if (objectType.isInterface() == null) {
			logger.log(TreeLogger.ERROR, objectType.getQualifiedSourceName() + " is not an interface", null);
			throw new UnableToCompleteException();
		}

		implTypeName = objectType.getSimpleSourceName() + "Impl";
		implPackageName = objectType.getPackage().getName().replace(".generator", "");

		ClassSourceFileComposerFactory composerFactory = new ClassSourceFileComposerFactory(implPackageName,
				implTypeName);

		composerFactory.addImport(IconBuilderBase.class.getCanonicalName());
		composerFactory.setSuperclass(IconBuilderBase.class.getSimpleName());

		PrintWriter printWriter = context.tryCreate(logger, implPackageName, implTypeName);

		if (printWriter != null) {
			logger.log(TreeLogger.INFO, "======================= Porcess Yun74Icons ==============================", null);

			SourceWriter sourceWriter = composerFactory.createSourceWriter(context, printWriter);
			sourceWriter.indentln("@Override public void init() {");
			ResourceOracle resource = context.getResourcesOracle();
			for (String path : resource.getPathNames()) {
				if (!path.endsWith(".svg"))
					continue;

				Resource r = resource.getResource(path);
				if (null != r) {
					logger.log(TreeLogger.INFO, "Process '" + path + "'", null);

					GWTIconSVGParser svg = GWTIconSVGParser.parser(resource.getResourceAsStream(path));
					if (svg != null)
						sourceWriter.indentln("addResource(\"" + path + "\", \"" + svg.view + "\", \"" + svg.path + "\");");
				}
			}

			sourceWriter.indentln("}");
			sourceWriter.commit(logger);
		}
		return implPackageName + "." + implTypeName;
	}
}
