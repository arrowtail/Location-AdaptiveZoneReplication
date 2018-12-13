import java.util.ArrayList;

/**
 * ZoneReplicationを実行するためのクラス
 * @author Shigeki_Yoneda
 *
 */
public class ZoneSim {
/*
	static final int ORIGINAL = 0;
	static final int ORIGINAL2 = 250;
	static final int ORIGINAL3 = 500;
	static final int ORIGINAL4 = 750;
*/

	//追加の場合
	static final int ORIGINAL = 0;
	static final int ORIGINAL2 = 253;
	static final int ORIGINAL3 = 506;
	static final int ORIGINAL4 = 759;

	static int NODENUM = 1000;
	// 周期1日
	static final int ROUND = 1000;
	// ノードの故障確率
	static int BRAKERATE = 0;
	// ノードの分割
	static int ZONE = 250;
	//Zone数
	static int ZONENUM = 4;
	// maintenanceの周期
	static final int MAINTENANCECOUNT = 10;
	// churn確率
	static double CHURNRATE = 0;
	static int CHURNFREQUENCY = 0;
	//seed値
	static final int SEED = 1145141919;
	/**
	 * @param args
	 *　ノード数　churnfrequency churnrateをコマンドライン引数にとる
	 */
	public static void main(String[] args) {
		NODENUM = Integer.parseInt(args[0]);
		CHURNFREQUENCY = Integer.parseInt(args[1]);
		CHURNRATE = Double.parseDouble(args[2]);
		ZONE = NODENUM/ZONENUM;
		int replicaAverage = 0;
		int nodeAverage = 0;
		int arriveZone = 0; //生存しているゾーン
		int message = 0; //メッセージ数
		int rebuildCount=0;//再構成数??
		long heartbeatCount=0;//ハートビート数
		int rebuildHeartBeatCount=0;//再構成ハートビート数
		int reconstitutionCount=0;//再構成

/*
		for (int i = 0; i < ROUND; i++) {
			System.out.println(i);
			ArrayList<ChordNode> nodes = new ArrayList<ChordNode>();
			ConsistentHash aConsistentHash = new ConsistentHash();
			nodes = aConsistentHash.init(nodes);
			nodes = aConsistentHash.initChurnNode(nodes, NODENUM, CHURNRATE);
			aConsistentHash.replication(nodes);
			nodes = aConsistentHash.run(nodes,ROUND,CHURNFREQUENCY);
//再構築
			rebuildCount += aConsistentHash.getRebuildingCount(nodes);
//ハートビート
			heartbeatCount += aConsistentHash.getHeartBeatCount(nodes);
			rebuildHeartBeatCount += aConsistentHash.getRebuildingHeartBeatCount(nodes);
			replicaAverage += aConsistentHash.replicaCount(nodes);
			nodeAverage += aConsistentHash.nodeCount(nodes);
			arriveZone += aConsistentHash.getZoneReplicaCount(nodes);
			message += aConsistentHash.getMssageCount(nodes);
			reconstitutionCount += aConsistentHash.getReconstitutionCount(nodes);
		}
		System.out.println("ConsistentHash");
*/

		for (int i = 0; i < ROUND; i++) {
			System.out.println(i);
			ArrayList<ChordNode> nodes = new ArrayList<ChordNode>();
			CustomZone aCustomZone = new CustomZone();
			nodes = aCustomZone.init(nodes);
			nodes = aCustomZone.initChurnNode(nodes, NODENUM, CHURNRATE);
			aCustomZone.replication(nodes);
			nodes = aCustomZone.run(nodes,ROUND,CHURNFREQUENCY);
//再構築
			rebuildCount += aCustomZone.getRebuildingCount(nodes);
//ハートビート
			heartbeatCount += aCustomZone.getHeartBeatCount(nodes);
			rebuildHeartBeatCount += aCustomZone.getRebuildingHeartBeatCount(nodes);
			replicaAverage += aCustomZone.replicaCount(nodes);
			nodeAverage += aCustomZone.nodeCount(nodes);
			arriveZone += aCustomZone.getZoneReplicaCount(nodes);
			message += aCustomZone.getMssageCount(nodes);
			reconstitutionCount += aCustomZone.getReconstitutionCount(nodes);
		}
		System.out.println("CustomZone");

//生存率
		//System.out.println(MAXCHURNRATE*0.01 + " " + (double)arriveZone/(ROUND*ZONENUM)*100 + " " + message/ROUND);
//メッセージ数,ハートビート数,再構成,再構成ハートビート,生存率,再構成回数
			System.out.println(CHURNFREQUENCY*0.01 + " " + message/(ROUND) + " " + heartbeatCount/ROUND + " " +  rebuildCount/ROUND + " " +rebuildHeartBeatCount/ROUND + " " + (double)arriveZone/(ROUND*ZONENUM)*100 + " " + reconstitutionCount/ROUND);
//横軸がchurnRateの場合
		//System.out.println(CHURNRATE*0.01 + " " + message/(ROUND) + " " + heartbeatCount/ROUND + " " +  rebuildCount/ROUND + " " +rebuildHeartBeatCount/ROUND + " " + (double)arriveZone/(ROUND*ZONENUM)*100 + " " + reconstitutionCount/ROUND);

		//再構築にかかったメッセージ数
// 	System.out.println(MAXCHURNRATE + " " + rebuildCount/ROUND);
//ハートビートにかかったメッセージ数
//		System.out.println(MAXCHURNRATE + " " + heartbeatCount/ROUND);
	}
}
