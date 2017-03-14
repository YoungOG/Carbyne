package com.medievallords.carbyne.leaderboards.profile;

import com.medievallords.carbyne.Carbyne;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import lombok.Setter;
import org.bson.Document;

import java.util.UUID;

/**
 * Created by Calvin on 1/24/2017
 * for the Carbyne-Gear project.
 */

@Getter
@Setter
public class StatProfile {

    private Carbyne carbyne = Carbyne.getInstance();
    private MongoCollection<Document> statProfileCollection = carbyne.getMongoDatabase().getCollection("stat-profiles");

    private UUID uniqueId;
    private String name;
    private int kills;
    private int deaths;
    double kdRatio;
    private int killStreak;
    private int balance;

    public StatProfile(UUID uniqueId) {
        this.uniqueId = uniqueId;
    }

    public void save() {
        Document document = new Document("uniqueId", uniqueId.toString())
                .append("name", name)
                .append("kills", kills)
                .append("deaths", deaths)
                .append("kdRatio", kdRatio)
                .append("killStreak", killStreak)
                .append("balance", balance);

        statProfileCollection.replaceOne(Filters.eq("uniqueId", uniqueId.toString()), document, new UpdateOptions().upsert(true));
    }

    public void delete() {
        if (statProfileCollection.find(Filters.eq("uniqueId", uniqueId.toString())).first() != null) {
            statProfileCollection.deleteOne(statProfileCollection.find(Filters.eq("uniqueId", uniqueId.toString())).first());
        }
    }
}
