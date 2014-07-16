package bugzillaSpider.helper;

import java.net.UnknownHostException;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

public class MongoHelper {
	private Mongo mongo = null;

	public MongoHelper() {
		try {
			mongo = new Mongo("115.29.242.187", 27017);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void addBug(DBObject bug) {
		if (mongo == null) {
			return;
		}
		DB db = mongo.getDB("bugzilla");
		DBCollection collection = db.getCollection("bug");
		collection.ensureIndex("url");
		collection.insert(bug);
	}

	public void addComment(DBObject comm) {
		if (mongo == null) {
			return;
		}
		DB db = mongo.getDB("bugzilla");
		DBCollection collection = db.getCollection("comment");
		collection.ensureIndex("commentid");
		collection.insert(comm);
	}

	public boolean existBug(String bugid) {
		DB db = mongo.getDB("bugzilla");
		DBCollection collection = db.getCollection("bug");
		DBObject obj = new BasicDBObject();
		obj.put("bug_id", bugid);
		DBCursor cur = collection.find(obj);
		if (cur.hasNext()) {
			return true;
		}
		return false;
	}

	public boolean existComment(DBObject comm) {
		DB db = mongo.getDB("bugzilla");
		DBCollection collection = db.getCollection("comment");
		DBCursor cur = collection.find(comm);
		if (cur.hasNext()) {
			return true;
		}
		return false;
	}
}
