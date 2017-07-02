package com.medievallords.carbyne.bounty;

import com.medievallords.carbyne.Carbyne;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.UpdateOptions;
import lombok.Getter;
import org.bson.Document;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Dalton on 6/28/2017.
 */
public class BountyManager
{

    private Carbyne main = Carbyne.getInstance();
    private MongoCollection<Document> taskCollection = main.getMongoDatabase().getCollection("bounties");
    @Getter
    private Map<UUID, Double> bounties;

    public BountyManager()
    {
        bounties = new HashMap<>();
        load();
    }

    public void load()
    {
        for(Document kDoc : taskCollection.find())
        {
            UUID uuid = UUID.fromString(kDoc.getString("uuid"));
            Document bDoc =kDoc.get("bountyDoc", Document.class);
            Double bounty = bDoc.getDouble("bounty");
            bounties.put(uuid, bounty);
        }
    }

    public void save()
    {
        for(UUID key : bounties.keySet())
        {
            Double bounty =bounties.get(key);
            Document kDoc = new Document();
            kDoc.put("uuid", key.toString());
            Document bountyDoc = new Document();
            bountyDoc.put("bounty", bounty);
            kDoc.put("bountyDoc", bountyDoc);
            taskCollection.replaceOne(new Document("uuid", key.toString()), kDoc, new UpdateOptions().upsert(true));
        }
    }

}
