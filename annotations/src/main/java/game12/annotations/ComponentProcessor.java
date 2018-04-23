package game12.annotations;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.tools.Diagnostic;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SupportedAnnotationTypes("game12.annotations.ComponentInfo")
@SupportedSourceVersion(SourceVersion.RELEASE_9)
public class ComponentProcessor extends AbstractProcessor {

	private static final String OUTPUT_PATH = "src-gen/main/dist/data/";

	private Messager messager;

	@Override
	public synchronized void init(ProcessingEnvironment processingEnv) {
		super.init(processingEnv);
		messager = processingEnv.getMessager();
	}

	private OutputJsonContainer outputJsonContainer = new OutputJsonContainer();

	private class OutputJsonContainer {

		private Map<String, String> core   = new HashMap<>();
		private Map<String, String> client = new HashMap<>();
		private Map<String, String> server = new HashMap<>();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {

		for (Element element : roundEnv.getElementsAnnotatedWith(ComponentInfo.class)) {
			TypeElement typeElement = (TypeElement) element;

			boolean hasEmptyConstructor = ElementFilter.constructorsIn(typeElement.getEnclosedElements())
					.stream().anyMatch(c -> c.getParameters().isEmpty());
			if (!hasEmptyConstructor) {
				messager.printMessage(Diagnostic.Kind.ERROR, "no default constructor", element);
			}

			ComponentInfo annotation = element.getAnnotation(ComponentInfo.class);
			String name = annotation.name();
			Name className = typeElement.getQualifiedName();

			Map<String, String> mapToPutIn
					= annotation.side() == ComponentSide.CORE ? outputJsonContainer.core
					: annotation.side() == ComponentSide.CLIENT ? outputJsonContainer.client
					: annotation.side() == ComponentSide.SERVER ? outputJsonContainer.server
					: outputJsonContainer.core;

			mapToPutIn.put(name, className.toString());

		}

		if (roundEnv.processingOver()) {
			File outputPath = new File(OUTPUT_PATH);
			if (!outputPath.exists() && !outputPath.mkdirs()) {
				messager.printMessage(Diagnostic.Kind.ERROR, "Could not create generated source directory: " + OUTPUT_PATH);
			}
			try (OutputStream stream = new FileOutputStream(OUTPUT_PATH + "components.json")) {
				BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(stream, "UTF-8"));
				writer.write(toJson(outputJsonContainer));
				writer.flush();
			} catch (IOException e) {
				messager.printMessage(Diagnostic.Kind.ERROR, "Could not write Components output file: " + e.getMessage());
			}
		}

		return false;
	}

	private void toJsonSingle(StringBuilder stringBuilder, Map<String, String> map) {
		for (Map.Entry<String, String> entry : map.entrySet()) {
			stringBuilder.append("        {\n");
			stringBuilder.append("            \"name\": \"").append(entry.getKey()).append("\",\n");
			stringBuilder.append("            \"class\": \"").append(entry.getValue()).append("\"\n");
			stringBuilder.append("        },\n");
		}
	}

	private String toJson(OutputJsonContainer outputJsonContainer) {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("{\n");

		stringBuilder.append("    \"core\": [\n");
		toJsonSingle(stringBuilder, outputJsonContainer.core);
		stringBuilder.append("    ],\n");

		stringBuilder.append("    \"client\": [\n");
		toJsonSingle(stringBuilder, outputJsonContainer.client);
		stringBuilder.append("    ],\n");

		stringBuilder.append("    \"server\": [\n");
		toJsonSingle(stringBuilder, outputJsonContainer.server);
		stringBuilder.append("    ]\n");

		stringBuilder.append("\n}\n");
		return stringBuilder.toString();
	}

}
