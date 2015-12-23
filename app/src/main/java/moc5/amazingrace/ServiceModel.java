package moc5.amazingrace;

import com.google.gson.annotations.SerializedName;

class Route {
	@SerializedName("Id")
	private String id;

	public String getId() {
		return id;
	}

	@SerializedName("Name")
	private String name;

	public String getName() {
		return name;
	}

	@SerializedName("VisitedCheckpoints")
	private Checkpoint[] visitedCheckpoints;

	public Checkpoint[] getVisitedCheckpoints() {
		return visitedCheckpoints;
	}

	@SerializedName("NextCheckpoint")
	private Checkpoint nextCheckpoint;

	public Checkpoint getNextCheckpoint() {
		return nextCheckpoint;
	}
}

class Checkpoint {
	@SerializedName("Id")
	private String id;

	public String getId() {
		return id;
	}

	@SerializedName("Number")
	private int number;

	public int getNumber() {
		return number;
	}

	@SerializedName("Name")
	private String name;

	public String getName() {
		return name;
	}

	@SerializedName("Hint")
	private String hint;

	public String getHint() {
		return hint;
	}

	@SerializedName("Latitude")
	private double latitude;

	public double getLatitude() {
		return latitude;
	}

	@SerializedName("Longitude")
	private double longitude;

	public double getLongitude() {
		return longitude;
	}
}

class Request {
	@SerializedName("UserName")
	private String userName;

	public String getUserName() { return userName; }

	public void setUserName(String userName) { this.userName = userName; }

	@SerializedName("Password")
	private String password;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) { this.password = password; }
}

class CheckpointRequest extends Request {
	@SerializedName("CheckpointId")
	private String checkpointId;

	public String getCheckpointId() { return checkpointId;	}

	public void setCheckpointId(String id) {
		checkpointId = id;
	}

	@SerializedName("Secret")
	private String secret;

	public String getSecret() { return secret; }

	public void setSecret(String s) {
		secret = s;
	}
}

class RouteRequest extends Request {
	@SerializedName("RouteId")
	private String routeId;

	public String getRouteId() { return routeId; }

	public void setRouteId(String id) { routeId = id;	}
}