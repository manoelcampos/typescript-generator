
package cz.habarta.typescript.generator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class SettingsTest {
    /**
     * Checks if the method can load a class from a given class name,
     * either it has a generic type argument or not.
     */
    @Test
    void testLoadPrimitiveOrRegularClass() {
        // A map where each key is a type reference (class/interface) and each value is a list of class names representing that type
        final var typeToClassName = Map.of(
            List.class, List.of("java.util.List", "java.util.List<String>", "java.util.List<java.util.List<String>>"),
            Map.class, List.of("java.util.Map", "java.util.Map<Integer, String>", "java.util.Map<Integer, java.util.List<String>>")
        );

        typeToClassName.forEach((type, classNameList) -> {
            classNameList.forEach(className -> assertTypeLoadedFromClassName(className, type));
        });
    }

    /**
     * Asserts that a class is loaded from a given class name.
     * @param className name of the class to load (that may contain generic arguments, even nested ones)
     * @param expectedClass the class that sould be loaded from the given class name
     */
    private void assertTypeLoadedFromClassName(final String className, final Class<?> expectedClass) {
        try {
            final var loadedClass = Settings.loadPrimitiveOrRegularClass(getClass().getClassLoader(), className);
            assertEquals(expectedClass, loadedClass);
        } catch (ClassNotFoundException e) {
            Assertions.fail(e);
        }
    }

    /**
     * Checks if generic type arguments are parsed correctly, even when there are nested generic types.
     */
    @Test
    void testParseGenericName() {
        final var className = "Class";
        final String[] nonNestedGenericArgumentTypes = {"T1", "T2"};

        assertEquals(newGenericName(className, nonNestedGenericArgumentTypes), Settings.parseGenericName("Class<T1, T2>"));
        assertEquals(newGenericName(className, nonNestedGenericArgumentTypes), Settings.parseGenericName("Class[T1, T2]"));
        assertEquals(newGenericName(className, "T1[T2]", "T3"), Settings.parseGenericName("Class[T1[T2], T3]"));
        assertEquals(newGenericName(className, "T1<T2>", "T3"), Settings.parseGenericName("Class<T1<T2>, T3>"));
    }

    /**
     * Creates a new {@link Settings.GenericName} instance.
     * @param className name of a class that have generic type arguments.
     * @param genericArguments generic type arguments
     * @return a new {@link Settings.GenericName} instance.
     */
    private static Settings.GenericName newGenericName(final String className, final String ...genericArguments) {
        return new Settings.GenericName(className, Arrays.asList(genericArguments));
    }

    @Test
    public void testParseModifiers() {
        assertEquals(0, Settings.parseModifiers("", Modifier.fieldModifiers()));
        assertEquals(Modifier.STATIC, Settings.parseModifiers("static", Modifier.fieldModifiers()));
        assertEquals(Modifier.STATIC | Modifier.TRANSIENT, Settings.parseModifiers("static | transient", Modifier.fieldModifiers()));
    }

    @Test
    public void testNpmDependenciesValidation() {
        String exceptionMessage = "'npmDependencies', 'npmDevDependencies' and 'npmPeerDependencies' parameters are only applicable when generating NPM 'package.json'.";

        {
            Settings settings = new Settings();
            settings.outputKind = TypeScriptOutputKind.module;
            settings.jsonLibrary = JsonLibrary.jackson2;
            settings.generateNpmPackageJson = false;
            settings.npmPackageDependencies.put("dependencies", "version");

            RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> settings.validate());
            assertEquals(exceptionMessage, exception.getMessage());
        }

        {
            Settings settings = new Settings();
            settings.outputKind = TypeScriptOutputKind.module;
            settings.jsonLibrary = JsonLibrary.jackson2;
            settings.generateNpmPackageJson = false;
            settings.npmDevDependencies.put("dependencies", "version");

            RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> settings.validate());
            assertEquals(exceptionMessage, exception.getMessage());
        }

        {
            Settings settings = new Settings();
            settings.outputKind = TypeScriptOutputKind.module;
            settings.jsonLibrary = JsonLibrary.jackson2;
            settings.generateNpmPackageJson = false;
            settings.npmPeerDependencies.put("dependencies", "version");

            RuntimeException exception = Assertions.assertThrows(RuntimeException.class, () -> settings.validate());
            assertEquals(exceptionMessage, exception.getMessage());
        }
    }
}
