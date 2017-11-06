package one.oktw.sponge.internal.galaxy;

import com.mongodb.client.MongoCollection;
import one.oktw.sponge.Main;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.eq;
import static java.util.stream.Collectors.toList;
import static one.oktw.sponge.Main.getMain;
import static one.oktw.sponge.internal.galaxy.Groups.ADMIN;
import static one.oktw.sponge.internal.galaxy.Groups.MEMBER;


public class GalaxyManager {
    private Main main = getMain();
    private MongoCollection<Document> database = main.getDatabaseManager().getDatabase().getCollection("Galaxy");

    public Galaxy createGalaxy(String name, UUID creator, UUID... members) {
        UUID uuid = UUID.randomUUID();
        List<Document> memberList = Arrays.asList(members).parallelStream()
                .map(member -> new Document("UUID", member).append("Group", MEMBER))
                .collect(toList());
        memberList.add(new Document("UUID", creator).append("Group", ADMIN));
        Document document = new Document("UUID", uuid)
                .append("Name", name)
                .append("Members", memberList);
        database.insertOne(document);
        return new Galaxy(uuid);
    }

    public Galaxy getGalaxy(UUID uuid) {
        return new Galaxy(uuid);
    }

    public ArrayList<Galaxy> searchGalaxy(String name) {
        ArrayList<Galaxy> galaxyList = new ArrayList<>();
        database.find(eq("Name", name)).forEach(
                (Consumer<Document>) document -> galaxyList.add(new Galaxy((UUID) document.get("UUID")))
        );
        return galaxyList;
    }
}
