package org.ssiu.ucp.client.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ssiu.ucp.common.mode.EngineType;

import java.io.IOException;
import java.nio.file.FileSystemNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathFinder {

    private static final Logger LOG = LoggerFactory.getLogger(PathFinder.class);

    private final static String CONNECTOR_PATH = "connector";

    private final static String OPERATOR_PATH = "operator";

    private final static String UCP_LIB_PATH = "lib";

    private final static String APP_NAME = "core";

    private final static String JAR_SUFFIX = "jar";

    private static Path appRoot() {
        return Paths.get("").toAbsolutePath();
    }

    public static Path findApp(EngineType engineType) throws IOException {
        final Path appPath = appRoot().resolve(engineType.getSimpleName().toLowerCase());
        final Collection<Path> jars = findJars(appPath);
        // TODO: Accurate search
        final Optional<Path> mayApp = jars.stream()
                .filter(path -> {
                    final String jarName = path.getFileName().toString();
                    return jarName.contains(engineType.getSimpleName().toLowerCase()) && jarName.contains(APP_NAME);
                }).findFirst();

        if (mayApp.isPresent()) {
            return mayApp.get();
        } else {
            LOG.error("Can not find app in {}, contained: {}, {}", appPath, engineType.getSimpleName().toLowerCase(), APP_NAME);
            throw new FileSystemNotFoundException("can not find app");
        }
    }

    public static Collection<Path> findUcpLib() throws IOException {
        final Path libPath = appRoot().resolve(UCP_LIB_PATH);
        return findJars(libPath);
    }

    public static Collection<Path> findPlugin(EngineType engineType) throws IOException {
        final Collection<Path> connectors = findConnector(engineType);
        final Collection<Path> operators = findOperator(engineType);
        LOG.info("Find connectors: {}", connectors.stream().map(Path::toString).collect(Collectors.joining(",", "{", "}")));
        LOG.info("Find operators: {}", operators.stream().map(Path::toString).collect(Collectors.joining(",", "{", "}")));
        return Stream.concat(connectors.stream(), operators.stream()).collect(Collectors.toList());
    }

    private static Collection<Path> findConnector(EngineType engineType) throws IOException {
        final Path connectorPath = appRoot()
                .resolve(engineType.getSimpleName().toLowerCase())
                .resolve(CONNECTOR_PATH);
        return findJars(connectorPath);
    }

    private static Collection<Path> findOperator(EngineType engineType) throws IOException {
        final Path operatorPath = appRoot()
                .resolve(engineType.getSimpleName().toLowerCase())
                .resolve(OPERATOR_PATH);
        return findJars(operatorPath);
    }

    private static Collection<Path> findJars(Path path) throws IOException {
        try (final Stream<Path> fileStream = Files.list(path)) {
            return fileStream.filter(mayFile -> mayFile.toFile().isFile())
                    .filter(file -> file.getFileName().toString().endsWith(JAR_SUFFIX))
                    .collect(Collectors.toSet());
        }
    }
}
