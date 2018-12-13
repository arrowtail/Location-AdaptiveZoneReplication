import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author Shigeki_Yoneda
 *
 */
public class ConsistentHash {
	static int ORIGINAL = ZoneSim.ORIGINAL;
	static int ORIGINAL2 = ZoneSim.ORIGINAL2;
	static int ORIGINAL3 = ZoneSim.ORIGINAL3;
	static int ORIGINAL4 = ZoneSim.ORIGINAL4;

	static int NODENUM = ZoneSim.NODENUM;
	// 周期1日
	static final int ROUND = ZoneSim.ROUND;
	// ノードの故障確率
	static int BRAKERATE = ZoneSim.BRAKERATE;
	// ノードの分割
	static int ZONE = ZoneSim.ZONE;
	//デバックに使用
	static int k=0;
	// maintenanceの周期
	static final int MAINTENANCECOUNT = ZoneSim.MAINTENANCECOUNT;
	//複製数
	//static int ReplicaNum=4;
	//ZONE数
	static final int ZONENUM = ZoneSim.ZONENUM;

	static final int ONEZONE = NODENUM/ZONENUM;

	//seed値
	static final int SEED = ZoneSim.SEED;
	/**
	 * 実行メソッド
	 * @param nodes
	 * @param round
	 * @param churnFrequency
	 * @return nodes
	 */
	ArrayList<ChordNode> run(ArrayList<ChordNode> nodes,int round,int churnFrequency) {
		int churnNum = 0;
		int maintenanceCount = 0;
		Random random = new Random();
		for (int sessionTime = 0; sessionTime < ROUND; sessionTime++) {
			//churnFrequencyによりchurnを行なう
			for(ChordNode aNode : nodes){
				if(aNode.getIsChurn()){
					if(random.nextInt(100)<=churnFrequency)
						aNode.churn();
				}
			}
			maintenanceCount++;
			//メンテのタイミングになったらmaintenanceプロトコルを実施する
			if (maintenanceCount % MAINTENANCECOUNT == 0) {
				for (ChordNode aNode : nodes) {
					if (aNode.getIsJoin() && aNode.getIsReplica()) {
						//通常のメンテナンスプロトコルを使用する場合
						//nodes=aNode.getZoneReplication().maintenance(nodes);
						//再構成
						int zoneId = aNode.getZoneReplication().getZoneId();
						int myId = aNode.getId();
						//System.out.println(myId);
						nodes=aNode.getZoneReplication().maintenance(nodes,myId);
					}
				}
			}
		}
		return nodes;
	}
	/**
	 * 複製を行うためのメソッド
	 * @param nodes
	 */
	void replication(ArrayList<ChordNode> nodes) {
		int zoneId = 0;
		int id = 0;
		int count=0;
		for (Node aNode : nodes) {
			if (aNode.getIsReplica()) {
				//System.out.print(count);
				//System.out.println(" ");
				//System.out.println(zoneId);
				zoneId = aNode.getZoneReplication().getZoneId();
				id = aNode.getId() + ZONE;
				if (id >= NODENUM) id -= NODENUM;
				aNode.getZoneReplication().replication(nodes.get(id));
			}
			//count++;
		}
	}

	/**
	 * 総複製数のカウント
	 * @param nodes
	 * @return replicationCount
	 */
	int replicationCount(ArrayList<ChordNode> nodes){
		int replicationCount=4;
		for(Node aNode : nodes){
			replicationCount += aNode.getZoneReplication().getReplicaCount();
		}
		return replicationCount;
	}
	/**
	 * 現在の複製数をカウントするためのメソッド
	 * @param nodes
	 * @return replicaCount
	 */
	int replicaCount(ArrayList<ChordNode> nodes) {
		int replicaCount = 0;
		for (Node aNode : nodes) {
			if (aNode.getIsJoin()) {
				if (aNode.isReplica) {
					replicaCount++;
				}
			}
		}
		return replicaCount;
	}
	/**
	 * churnしているノードのカウント
	 * @param nodes
	 * @return count
	 */
	int churnNodeCount(ArrayList<ChordNode>nodes){
		int count =0;
		for(Node aNode: nodes){
			if(aNode.getIsChurn()){
				count++;
			}
		}
		return count;
	}
	/**
	 * 全ての生存しているノードをカウントするためのメソッド
	 * @param nodes
	 * @return count
	 */
	int nodeCount(ArrayList<ChordNode> nodes) {
		int count = 0;
		for (Node aNode : nodes) {
			if (aNode.getIsJoin()) {
				count++;
			}
		}
		return count;
	}
	/**
	 * ZoneIDに属しているノードをカウントするためのメソッド
	 * @param nodes
	 * @param zoneId
	 * @return count
	 */
	int nodeCount(ArrayList<ChordNode> nodes,int zoneId){
		int count = 0;
		for(Node aNode : nodes){
			if(aNode.getIsJoin() && aNode.getZoneReplication().getZoneId()==zoneId) count++;
		}
		return count;
	}
	/**
	 * ZoneIDに属しているノードのうち生存しているノードをカウントするためのメソッド
	 * @param nodes
	 * @param zoneId
	 * @return count
	 */
	int zoneJoinnodeCount(ArrayList<ChordNode> nodes,int zoneId){
		int count = 0;
		for(Node aNode : nodes){
			if(aNode.getIsJoin() && aNode.getZoneReplication().getZoneId()==zoneId &&aNode.getIsReplica()) count++;
		}
		return count;
	}
	/**
	 * メンテナンスによるメッセージ数をカウントするためのメソッド
	 * @param nodes
	 * @return count
	 */
	int maintenanceMessageCount(ArrayList<ChordNode>nodes){
		int count =0;
		for(Node aNode:nodes){
			count+=aNode.getZoneReplication().getMaintenanceCount();
		}
		return count;
	}
	/**
	 * 再構成数をカウントするためのメソッド
	 * @param nodes
	 * @return count
	 */
	int getRebuildingCount(ArrayList<ChordNode>nodes){
		int count=0;
		for(ChordNode aNode:nodes){
			count+=aNode.getZoneReplication().getRebuildingCount();
		}
		return count;
	}
	/**
	 * 再構成する際のハートビートをカウントするためのメソッド
	 * @param nodes
	 * @return count
	 */
	int getRebuildingHeartBeatCount(ArrayList<ChordNode> nodes){
		int count=0;
		for(ChordNode aNode:nodes){
			count+=aNode.getZoneReplication().getRebuildingHeartBeatCount();
		}
		return count;
	}
	/**
	 * ハートビート数をカウントするためのメソッド
	 * @param nodes
	 * @return count
	 */
	long getHeartBeatCount(ArrayList<ChordNode>nodes){
		long count=0;
		for(ChordNode aNode:nodes){
			count +=aNode.getZoneReplication().getHeartBeatCount();
		}
		return count;
	}
	/**
	 * 再構成・ハートビートを含むメッセージ数のカウントを行うためのメソッド
	 * @param nodes
	 * @return count
	 */
	int getMssageCount(ArrayList<ChordNode>nodes){
		int count =0;
		for(Node aNode:nodes){
			count+=aNode.getZoneReplication().getMessageCount();
		}
		return count;
	}
	/**
	 * 再構成数のカウント
	 * @param nodes
	 * @return count
	 */
	int getReconstitutionCount(ArrayList<ChordNode>nodes){
		int count =0;
		for(Node aNode:nodes){
			count+=aNode.getZoneReplication().getReconstitutionCount();
		}
		return count;
	}
	/**
	 * Zone内に複製があるかカウント
	 * @param nodes
	 * @return count
	 */
	int getZoneReplicaCount(ArrayList<ChordNode> nodes){
		int count = 0;
		int flag[] = new int[ZoneSim.ZONENUM];
		int arrayLength = flag.length;
		for(int i=0;i<arrayLength;i++){
			flag[i] = 1;
		}
		for(ChordNode aNode:nodes){
			if(aNode.getIsJoin()&&aNode.getIsReplica()){
				int nodeId = aNode.getZoneId();
				if(flag[nodeId]!=0){
					count++;
					flag[nodeId]=0;
				}
			}
		}
		return count;
	}
	void Randomdata(int ZONENUM){
	 Random random = new Random();
	 ORIGINAL = random.nextInt(NODENUM/ZONENUM);
	 ORIGINAL2 =ORIGINAL+(NODENUM/ZONENUM);
	 ORIGINAL3 =ORIGINAL2+(NODENUM/ZONENUM);
	 ORIGINAL4 =ORIGINAL3+(NODENUM/ZONENUM);
	 }

	 int nextId1(int i){
		 i= i+253;
		 if(i>=1000)i=0;
		 return i;
	 }

	 int preId1(int i){
		 i= i-253;
		 if(i<0)i=759;
		 return i;
	 }

	 int nextnextId1(int i){
		 i= i+253;
		 if(i>=1000)i=0;
		 i= i+253;
		 if(i>1000)i=0;
		 return i;
	 }
	/**
	 *  初期化
	 * @param nodes
	 * @return nodes
	 **/
	ArrayList<ChordNode> init(ArrayList<ChordNode> nodes) {
		int seed = SEED;
		Dht dht = new Dht();
		ArrayList<String> ipList = new ArrayList<String>();
		for (int i = 0; i < ZoneSim.NODENUM; i++) {
			try {
				ipList.add(dht.hash(getRandomIpAddress(seed)));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			seed++;
		}
		Collections.sort(ipList);
		HashMap<Integer,String> nodeList = new HashMap<Integer, String>();
		int i=0, x=1;
		HashMap<Integer,ArrayList<Integer>> zoneTable = new HashMap<Integer,ArrayList<Integer>>();
		for(int j=0;j<ZoneSim.ZONENUM;j++){
			zoneTable.put(j, new ArrayList<Integer>());
		}
		for(String ip : ipList){
			nodeList.put(i,ip);
			int zoneId=0;
			if(i%4==3)zoneId=3;
			if(i%4==2)zoneId=2;
			if(i%4==1)zoneId=1;
			if(i%4==0)zoneId=0;
			//if(k==0)System.out.println(zoneId);
			//int zoneId=i/ZONE;// 元の設定方法
			zoneTable.get(zoneId).add(i);
			i = (i+250)%1000;
			if((x%4)==0){
				i++;
			}
			x++;
		}

		for (i = 0; i < ZoneSim.NODENUM; i++) {
			ChordNode temp = new ChordNode(i,nodeList.get(i), false, nodeList.get(i+1),nodeList.get(ZoneSim.NODENUM-1),new ZoneReplication(i,false),-1,-1,-1);
			if (i == ORIGINAL || i == ORIGINAL2 || i== ORIGINAL3 || i==ORIGINAL4) {
				if(i-ZONE<0){//0
					temp = new ChordNode(i,nodeList.get(i), true, nodeList.get(i+4),nodeList.get(ZoneSim.NODENUM-1),new ZoneReplication(i,false),nextId1(i), preId1(i), nextnextId1(i));
				}else if(i+ZONE > NODENUM-1){//759
					temp = new ChordNode(i,nodeList.get(i), true, nodeList.get(i+4),nodeList.get(ZoneSim.NODENUM-1),new ZoneReplication(i,false),nextId1(i), preId1(i), nextnextId1(i));
				}else if(i-ZONE-ZONE<0){//253
					temp = new ChordNode(i,nodeList.get(i), true, nodeList.get(i+4),nodeList.get(ZoneSim.NODENUM-1),new ZoneReplication(i,false),nextId1(i), preId1(i), nextnextId1(i));
				}else{//506
					temp = new ChordNode(i,nodeList.get(i), true, nodeList.get(i+4),nodeList.get(ZoneSim.NODENUM-1),new ZoneReplication(i,false),nextId1(i), preId1(i), nextnextId1(i));
				}
			}else if (i >= ZoneSim.NODENUM-1) {
				temp = new ChordNode(i,nodeList.get(i), false, nodeList.get(0),nodeList.get(i-4),new ZoneReplication(i, false),-1,-1,-1);
			} else {
				temp = new ChordNode(i,nodeList.get(i), false, nodeList.get(i+4),nodeList.get(i-4), new ZoneReplication(i,false),-1,-1,-1);
			}
			HashMap<Integer,ArrayList<Integer>> table = new HashMap<Integer,ArrayList<Integer>>();
			int zoneId = i/ZONE;
			if(i%4==3)zoneId=3;
			if(i%4==2)zoneId=2;
			if(i%4==1)zoneId=1;
			if(i%4==0)zoneId=0;
			int nextId = zoneId+1;
			int preId = zoneId-1;
			int nextNextId = nextId+1;
			table.put(zoneId, zoneTable.get(zoneId));
			if(nextId>=ZoneSim.ZONENUM) {
				nextId=0;
				nextNextId=1;
				table.put(nextId, zoneTable.get(nextId));
				table.put(preId, zoneTable.get(preId));
				table.put(nextNextId, zoneTable.get(nextNextId));
			}else if(preId<0){
				preId=ZoneSim.ZONENUM-1;
				table.put(nextId, zoneTable.get(nextId));
				table.put(preId,zoneTable.get(preId));
				if(nextNextId>=ZoneSim.ZONENUM) nextNextId = 0;
				table.put(nextNextId,zoneTable.get(nextNextId));
			}else{
				table.put(nextId, zoneTable.get(nextId));
				table.put(preId, zoneTable.get(preId));
				if(nextNextId>=ZoneSim.ZONENUM) nextNextId = 0;
				table.put(nextNextId, zoneTable.get(nextNextId));
			}
			temp.setNextZoneTable(table.get(nextId));
			temp.setPreZoneTable(table.get(preId));
			temp.setMyZoneTable(table.get(zoneId));
			temp.setNextNextZoneTable(table.get(nextNextId));
			temp.setZoneTable(table);

			nodes.add(temp);
		}
		return nodes;
	}

	/**
	 * churnRateの割合分churnするノードをランダムに選ぶ。
	 * @param nodes
	 * @param churnRate
	 * @return nodes
	 */
	public ArrayList<ChordNode> initChurnNode(ArrayList<ChordNode> nodes,int nodeNum,double churnRate){
		Random random = new Random();
		int churnNodeCount = 0;
		int churnNodeNum = (int)(nodeNum*churnRate/100);
		while(churnNodeNum > churnNodeCount){
			int nodeId = random.nextInt(ZoneSim.NODENUM);//ここでchurnするノードを決めている
			//int nodeId = churnNodeCount;//ChurnRateが10の場合、0~99までchurnさせる
			if(!nodes.get(nodeId).getIsChurn()){
				//if(k==0)System.out.println(nodeId);
				nodes.get(nodeId).setIsChurn(true);
				churnNodeCount++;
			}
		}
		k++;
		return nodes;
	}
	/**
	 * churnするノードのリストを返すメソッド
	 * @param nodes
	 * @param nodeNum
	 * @param churnRate
	 * @return churnNodeList
	 */
	public ArrayList<Integer> initChurnNodeList(ArrayList<ChordNode> nodes,int nodeNum,double churnRate){
		ArrayList<Integer> churnNodeList = new ArrayList<Integer>();
		Random random = new Random();
		int churnNodeNum = (int)(nodeNum*churnRate/100);
		for(int i=0;i<churnNodeNum;i++){
			int nodeId = random.nextInt(ZoneSim.NODENUM);
			churnNodeList.add(nodeId);
		}
		return churnNodeList;
	}
	/**
	 * IPアドレスを生成するためのメソッド
	 * @param seed
	 * @return sb.toString()
	 */
	public String getRandomIpAddress(int seed){
		StringBuilder sb = null;
		Random random = new Random(seed);
		int mdda = (int)((random.nextDouble()*100)*2.24);
		int mddb = (int)((random.nextDouble()*100)*2.55);
		int mddc = (int)((random.nextDouble()*100)*2.55);
		int mddd = (int)((random.nextDouble()*100)*2.55);
		String ipAA = Integer.toString(mdda);
		String ipBB = Integer.toString(mddb);
		String ipCC = Integer.toString(mddc);
		String ipDD = Integer.toString(mddd);
		sb= new StringBuilder();
		sb.append(ipAA).append(".").append(ipBB).append(".").append(ipCC).append(".").append(ipDD);
		return sb.toString();
	}
	/**
	 * テーブルの初期化を行うためのメソッド
	 * @param nodes
	 * @param zoneId
	 * @return table
	 */
	HashMap<Integer,ChordNode> tableInit(ArrayList<ChordNode> nodes,int zoneId){
		HashMap<Integer, ChordNode> table = new HashMap<Integer,ChordNode>();
		for(int i=0;i<NODENUM;i++){
			if(zoneId==nodes.get(i).getZoneReplication().getZoneId()){
				table.put(i,nodes.get(i));
			}
		}
		return table;
	}
	/**
	 * デバッグ用
	 * @param nodes
	 */
	void getZoneNodeNumCount(ArrayList<ChordNode> nodes){
		int count=0;
		for(ChordNode aNode : nodes){
			if(aNode.getIsJoin()&aNode.getIsReplica()){
				System.out.println("-----------------------");
				System.out.println(aNode.getZoneId());
				System.out.println(aNode.getMyZoneTable().size());
				System.out.println(aNode.getNextZoneTable().size());
				System.out.println(aNode.getPreZoneTable().size());
				System.out.println(aNode.getMyZoneTable());
				System.out.println(aNode.getNextZoneTable());
				System.out.println(aNode.getPreZoneTable());
				System.out.println("-----------------------");
			}
		}
	}

	/**
	 * デバッグ用
	 * @param nodes
	 */
	public void printDebug(ArrayList<ChordNode> nodes){
		for(ChordNode aNode : nodes){
			if(aNode.getIsJoin() &&aNode.getIsReplica()){
				System.out.println(aNode.getZoneId()+" "+aNode.getId() + " " + aNode.getNextId() + " " + aNode.getPreId() +" "+ aNode.getNextZoneTable());
			}
		}
		System.out.println("------");
	}
}
