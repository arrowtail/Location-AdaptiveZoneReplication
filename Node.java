import java.util.HashMap;

/**
 * ノードクラス
 * @author Shigeki_Yoneda
 *
 */
public class Node {
	int id;
	int nextNode;
	boolean isReplica;
	HashMap<Integer, Node> list;
	boolean isChurn;
	int nowSessionTime;
	boolean isJoin;
	ZoneReplication aZoneReplication;
	static int replicationNum = 0;
	int churnFrequency;
	int churnCount;

	int realLocation;

	HashMap<Integer,Node> map;

	Node(){

	}
	// なし
	Node(int id, boolean isReplica, int nextNode) {
		this.id = id;
		this.isReplica = isReplica;
		list = new HashMap<Integer, Node>();
		map =new HashMap<Integer, Node>();
		this.nextNode = nextNode;
		// test
		this.isJoin = true;
		this.isChurn = false;
		this.churnFrequency=0;
	}

	// ZoneReplciation
	Node(int id, boolean isReplica, int nextNode, ZoneReplication aZone) {
		this(id, isReplica, nextNode);
		this.aZoneReplication = aZone;
	}
	// 位置情報考慮
	Node(int id, boolean isReplica, int nextNode, ZoneReplication aZone, int realLocation) {
		this(id, isReplica, nextNode, aZone);
		this.realLocation = realLocation;
	}


	ZoneReplication getZoneReplication() {
		return this.aZoneReplication;
	}

	int getId() {
		return this.id;
	}

	boolean getIsReplica() {
		return this.isReplica;
	}

	void setReplica(boolean isReplica) {
		this.isReplica = isReplica;
		replicationNum++;
	}

	Node getNode(int id) {
		return list.get(id);
	}

	boolean getIsJoin() {
		return this.isJoin;
	}

	void nodeBreak() {
		this.isJoin = false;
		this.churnCount=0;
		if (this.isReplica) {
			this.isReplica = false;
		}
	}
	int getChurnCount(){
		return this.churnCount;
	}
	void addChurnCount(){
		this.churnCount++;
	}
	void setChurnCount(int churnCount){
		this.churnCount=churnCount;
	}
	void nodeJoin() {
		this.isJoin = true;
	}

	boolean getIsChurn() {
		return this.isChurn;
	}

	void setIsChurn(boolean isChurn) {
		this.isChurn = isChurn;
	}

	boolean putData() {
		return this.isReplica;
	}

	public int getChurnFrequency() {
		return churnFrequency;
	}

	public void setChurnFrequency(int churnFrequency) {
		this.churnFrequency = churnFrequency;
	}
	public HashMap<Integer,Node> getMap(){
		return this.map;
	}
	public void setMap(HashMap<Integer, Node> map){
		this.map = map;
	}
	// churn頻度に沿ってchurnを発生させる
	void churn() {
		if (getIsJoin()) {
			nodeBreak();
		} else {
			nodeJoin();
		}
	}
	void addChurnFrequency(){
		setChurnFrequency(getChurnFrequency()+1);
	}
}
