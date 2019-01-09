import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Shigeki_Yoneda
 * Chordベースのノードクラス
 */
public class ChordNode extends Node{
	Dht aDHT;
	int id;
	String idString;
	int zoneId;
	public int getZoneId() {
		return zoneId;
	}
	public void setZoneId(int zoneId) {
		this.zoneId = zoneId;
	}
	/**
	 * key:ZoneId
	 * value:ZoneIdに対応するNodes
	 */
	HashMap<Integer,ArrayList<Integer>> zoneTable;
	ArrayList<Integer> myZoneTable;
	ArrayList<Integer> nextZoneTable;
	ArrayList<Integer> preZoneTable;
	public ArrayList<Integer> getNextNextZoneTable() {
		return nextNextZoneTable;
	}
	public void setNextNextZoneTable(ArrayList<Integer> nextNextZoneTable) {
		this.nextNextZoneTable = nextNextZoneTable;
	}
	ArrayList<Integer> nextNextZoneTable;
	int nextId;
	int preId;
	public int getNextId() {
		return nextId;
	}
	public void setNextId(int nextId) {
		this.nextId = nextId;
	}
	public int getPreId() {
		return preId;
	}
	public void setPreId(int preId) {
		this.preId = preId;
	}

	public ArrayList<Integer> getMyZoneTable() {
		return myZoneTable;
	}
	public void setMyZoneTable(ArrayList<Integer> myZoneTable) {
		this.myZoneTable = myZoneTable;
	}
	public ArrayList<Integer> getNextZoneTable() {
		return nextZoneTable;
	}
	public void setNextZoneTable(ArrayList<Integer> nextZoneTable) {
		this.nextZoneTable = nextZoneTable;
	}
	public ArrayList<Integer> getPreZoneTable() {
		return preZoneTable;
	}
	public void setPreZoneTable(ArrayList<Integer> preZoneTable) {
		this.preZoneTable = preZoneTable;
	}
/*(変更仮案?)
	ChordNode(int id,String address,boolean isReplica,String nextNode,String preNode){
		super();
		this.id = id;
		this.isReplica=isReplica;
		this.isJoin=true;
		aDHT = new Dht(Integer.toString(id),nextNode,preNode);
		try {
			idString = aDHT.hash(address);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		zoneTable = new HashMap<Integer,ArrayList<Integer>>();
	}
	*/
	/**
	 * 既存手法(ZoneReplication)
	 * @param id
	 * @param address
	 * @param isReplica
	 * @param nextNode
	 * @param preNode
	 * @param nextId
	 * @param preId
	 */
	ChordNode(int id,String address,boolean isReplica,String nextNode,String preNode,int nextId,int preId){
		super();
		this.id = id;
		this.isReplica=isReplica;
		this.isJoin=true;
		aDHT = new Dht(Integer.toString(id),nextNode,preNode);
		this.nextId = nextId;
		this.preId = preId;
		this.zoneId=id%4;
		try {
			idString = aDHT.hash(address);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		zoneTable = new HashMap<Integer,ArrayList<Integer>>();
	}
	public Dht getaDHT() {
		return aDHT;
	}
	public void setaDHT(Dht aDHT) {
		this.aDHT = aDHT;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getIdString() {
		return idString;
	}
	public void setIdString(String idString) {
		this.idString = idString;
	}
	/**
	 * ZoneTableありの場合の初期化(提案手法)
	 * @param id
	 * @param address
	 * @param isReplica
	 * @param nextNode
	 * @param preNode
	 * @param aZone
	 * @param nextId
	 * @param preId
	 * @param nextNextId
	 */
	ChordNode(int id,String address,boolean isReplica, String nextNode,String preNode,ZoneReplication aZone,int nextId,int preId,int nextNextId){
		this(id,address,isReplica,nextNode,preNode,nextId,preId);
		this.aZoneReplication=aZone;
		this.myZoneTable =new ArrayList<Integer>();
		this.nextZoneTable =new ArrayList<Integer>();
		this.preZoneTable =new ArrayList<Integer>();
		this.nextNextZoneTable =new ArrayList<Integer>();
	}
	/**
	 * ZoneTableを初期化するためのメソッド
	 */
	void tableInit(){
		this.zoneTable = new HashMap<Integer,ArrayList<Integer>>();
	}
	/**
	 * ZoneTableをセットするためのメソッド
	 * @param zoneTable
	 */
	void setZoneTable(HashMap<Integer,ArrayList<Integer>> zoneTable){
		this.zoneTable=zoneTable;
	}
	/**
	 * ZoneTableを取得するためのメソッド
	 * @return this.zoneTable
	 */
	HashMap<Integer,ArrayList<Integer>> getZoneTable(){
		return this.zoneTable;
	}

}
