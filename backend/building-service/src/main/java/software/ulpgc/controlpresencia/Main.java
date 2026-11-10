package software.ulpgc.controlpresencia;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.javalin.Javalin;
import software.ulpgc.controlpresencia.architecture.domain.GetBuildingUseCase;
import software.ulpgc.controlpresencia.architecture.domain.ListBuildingsUseCase;
import software.ulpgc.controlpresencia.architecture.ports.BuildingReader;
import software.ulpgc.controlpresencia.infrastructure.adapters.GetBuildingEndPoint;
import software.ulpgc.controlpresencia.infrastructure.adapters.JsonBuildingReader;
import software.ulpgc.controlpresencia.infrastructure.adapters.ListBuildingsEndPoint;

import java.io.File;

public class Main {
    public static void main(String[] args) {
        File file = resolveBuildingsFile();

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();

        BuildingReader reader = new JsonBuildingReader(file, gson);
        GetBuildingUseCase getBuildingUseCase = new GetBuildingUseCase(reader);
        ListBuildingsUseCase listBuildingsUseCase = new ListBuildingsUseCase(reader);

        Javalin app = Javalin.create().start(7073);

        app.get("/buildings", context -> {
            if (context.queryParam("id") != null) {
                new GetBuildingEndPoint(getBuildingUseCase, context, gson).execute();
            } else {
                new ListBuildingsEndPoint(listBuildingsUseCase, context, gson).execute();
            }
        });

        System.out.println("Building-service activo en http://localhost:7073");
        System.out.println("-> Listar edificios: http://localhost:7073/buildings");
        System.out.println("-> Consultar edificio: http://localhost:7073/buildings?id=bld-01");

        Runtime.getRuntime().addShutdownHook(new Thread(app::stop));
    }

    private static File resolveBuildingsFile() {
        File file = new File("backend/building-service/buildings.json");
        if (file.exists()) return file;

        file = new File("building-service/buildings.json");
        if (file.exists()) return file;

        return new File("buildings.json");
    }
}
