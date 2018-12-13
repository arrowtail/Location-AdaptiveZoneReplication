import java.util.ArrayList;
import java.util.Collections;

/**
 * ZoneReplicationクラス
 * @author Shigeki_Yoneda
 *
 */
public class ZoneReplication{
	static final int ZONENUM = 4;
	static final int MINZONENUM = 200;
	//NODENUM:1ゾーンあたりのノード数
	static final int NODENUM = ZoneSim.NODENUM/ZONENUM;
	int zoneId;
	int id;
	int maintenanceNode;
	boolean isReplica;
	int replicaCount;
	int maintenanceCount;
	int messageCount;
	int rebuildingCount;
	int heartbeatCount;
	int rebuildingHeartbeatCount;
	int reconstitutionCount;
	//送信メッセージ数
	int sendMessageCount;
	ZoneReplication(int id,boolean isReplica){
		super();
		this.id=id;
		//this.zoneId = zoneTrans(id);
		this.zoneId = id/NODENUM;
		this.isReplica = isReplica;
		this.maintenanceCount=0;
		this.messageCount=0;
		this.rebuildingCount=0;
		this.rebuildingHeartbeatCount=0;
		this.heartbeatCount=0;
		sendMessageCount=0;
	}
	/**
	 * not use
	 * @param id
	 * @return
	 */
	int zoneTrans(int id){
		int zone;
		String temp = Integer.toBinaryString(id);
		String str = String.format("%010d",Integer.parseInt(temp));
		str = str.substring(0, 2);
		zone = Integer.parseInt(str,2);
		return zone;
	}
	int getZoneId(){
		return zoneId;
	}
	/**
	 * 次のNodeを探すためのメソッド
	 * @param nodes
	 * @param nextNextZoneTable
	 * @param zoneId
	 * @return
	 */
	int searchNextZoneId(ArrayList<ChordNode> nodes,ArrayList<Integer> nextNextZoneTable,int zoneId){
		int joinId = -1;
		boolean flag = false;
		for(int id : nextNextZoneTable){
			ChordNode aNode = nodes.get(id);
			if(zoneId>=ZoneSim.ZONENUM) zoneId=0;
			else zoneId+=1;
			if(aNode.getZoneId()==zoneId){
				//とりあえず生存しているノードを確保
				if(!flag&&aNode.getIsJoin()){
					flag=true;
					joinId=id;
				}
				//もし複製があればそのままかえす
				if(aNode.getIsJoin()&&aNode.getIsReplica()){
					return id;
				}
			}
		}
		//見つからなければ生きているノードを返す
		return joinId;
	}
	/**
	 *　ZoneTableにあるノードに対してハートビートを送る
	 * @param zoneTable
	 * @return arriveCount
	 */
	public int heartBeat(ArrayList<ChordNode> nodes ,ArrayList<Integer> zoneTable){
		int arriveCount=-1;
		for(int index : zoneTable){
			if(nodes.get(index).isJoin){
				this.addHeartBeatCount(); //ハートビートした分のメッセージカウンタ
				arriveCount++;
			}
		}
		return arriveCount;
	}


	/**
	 *
	 */
	int arriveCount(ArrayList<ChordNode> nodes,ArrayList<Integer> aTable){
		int count=0;
		for(int index : aTable){
			if(nodes.get(index).getIsJoin()) count++;
		}
		return count;
	}
	/**
	 * maintenanceプロトコル
	 * @param nodes
	 */
	ArrayList<ChordNode> maintenance(ArrayList<ChordNode> nodes){
		this.maintenanceCount++;
		int nextId=0;
		if(this.zoneId+1!=ZONENUM) nextId = (this.zoneId+1) * NODENUM;
		else nextId = NODENUM;
		int zoneMaxId = nextId + NODENUM;
		for(int i=nextId;i<zoneMaxId;i++){
			if(nodes.get(i).getIsJoin()&&nodes.get(i).getIsReplica()){
				addMessage();
				return nodes;
			}
		}
		for(int i=nextId;i<zoneMaxId;i++){
			if(nodes.get(i).getIsJoin()){
				addMessage();

				nodes.get(i).setReplica(true);
				nodes.get(i).getZoneReplication().addReplicaCount();
				return nodes;
			}
		}
		return nodes;
	}
	int getMessageCount(){
		return this.messageCount;
	}
	void addMessage(){
		this.messageCount++;
	}
	void addRebuildingCount(){
		this.rebuildingCount++;
	}
	void addHeartBeatCount(){
		this.heartbeatCount++;
	}
	void addRebuildingHeartBeatCount(){
		this.rebuildingHeartbeatCount++;
	}
	int getRebuildingCount(){
		return this.rebuildingCount;
	}
	int getHeartBeatCount(){
		return this.heartbeatCount;
	}
	int getRebuildingHeartBeatCount(){
		return this.rebuildingHeartbeatCount;
	}
	void getData(Node aNode){
		if(aNode.getIsReplica()){
			aNode.putData();
		}
	}
	void replication(Node aNode){
		if(!aNode.getIsReplica()){
			aNode.setReplica(true);
		}
	}
	boolean getIsReplica(){
		return this.isReplica;
	}
	int getReplicaCount(){
		return this.replicaCount;
	}
	void addReplicaCount(){
		this.replicaCount++;
	}
	int getMaintenanceCount(){
		return this.maintenanceCount;
	}
	public int getReconstitutionCount() {
		return reconstitutionCount;
	}
	public void setReconstitutionCount(int reconstitutionCount) {
		this.reconstitutionCount = reconstitutionCount;
	}
//以下AdaptiveZoneReplication用
	/**
	 * maintenanceプロトコル
	 * @param nodes
	 * @param zoneId
	 * 4/12 ZoneTableにメッセージを送る用に変更
	 */
	 /*
	ArrayList<ChordNode> maintenance(ArrayList<ChordNode> nodes,int myId){
		this.maintenanceCount++;
		ArrayList<Integer> myZoneTable = nodes.get(myId).getMyZoneTable();
		ArrayList<Integer> nextZoneTable = nodes.get(myId).getNextZoneTable();
		ArrayList<Integer> nextNextZoneTable = nodes.get(myId).getNextNextZoneTable();
		//自分自身のZone内のハートビートを送る
		int nodeCount=heartBeat(nodes,myZoneTable);
		//次のノードを探す
		int nextId = nodes.get(myId).getNextId();
		//監視先がないなら
		if(nextId!=-1){
			if(nodes.get(nextId).getIsJoin()&&nodes.get(nextId).getIsReplica()){
				this.addMessage();
				nodes.get(nextId).setPreId(myId);
			}
			//監視先が死んでいる場合
			else if(nodes.get(nextId).getIsJoin()&&!nodes.get(nextId).getIsReplica()){
				nodes.get(nextId).setReplica(true);
				this.addMessage();
				nodes.get(nextId).setPreId(myId);
			}else if(!nodes.get(nextId).getIsJoin()){
				//nextZoneTableから複製できるNodeを探す
				nextId = searchNextZoneId(nodes,nextZoneTable,nodes.get(myId).getZoneId());
				nodes.get(myId).setNextId(nextId);
				this.addMessage();
				nodes.get(nextId).setReplica(true);
				nodes.get(myId).getZoneReplication().addReplicaCount();
				nodes.get(nextId).setMyZoneTable(nextZoneTable);
				nodes.get(nextId).setPreZoneTable(myZoneTable);
				nodes.get(nextId).setPreId(myId);
				//nextIdの次のZoneのIdを探す
				int nextNextId = searchNextZoneId(nodes, nextNextZoneTable,nodes.get(nextId).getZoneId());
				//nextIdが全く見つからないということがなければnextIdを設定する
				if(nextNextId!=-1){
					nodes.get(nextId).setNextZoneTable(nextNextZoneTable);
					nodes.get(nextId).setNextId(nextNextId);
					//this.addMessage();
					if(!nodes.get(nextNextId).getIsReplica()){
						nodes.get(nextNextId).setReplica(true);
					}
				}
			}else{
				nodes.get(nextId).setPreId(myId);
				this.addMessage();
			}
		}else{
			nextId = searchNextZoneId(nodes, nextZoneTable,nodes.get(myId).getZoneId());
			nodes.get(myId).setNextId(nextId);
			this.addMessage();
			nodes.get(nextId).setReplica(true);
			nodes.get(myId).getZoneReplication().addReplicaCount();
			nodes.get(nextId).setMyZoneTable(nextZoneTable);
			nodes.get(nextId).setPreZoneTable(myZoneTable);
			nodes.get(nextId).setNextZoneTable(nextNextZoneTable);
			nodes.get(nextId).setPreId(myId);
			//nextIdの次のZoneのIdを探す
			int nextNextId = searchNextZoneId(nodes, nextNextZoneTable,nodes.get(nextId).getZoneId());
			//nextIdが全く見つからないということがなければnextIdを設定する
			if(nextNextId!=-1){
				nodes.get(nextId).setNextZoneTable(nextNextZoneTable);
				nodes.get(nextId).setNextId(nextNextId);
				if(!nodes.get(nextNextId).getIsReplica()){
					nodes.get(nextNextId).setReplica(true);
				}
			}
		}
		//次のZoneのTableを取得する
		//次のZoneに自分のTableを送信する
		nodes.get(myId).setNextZoneTable(nodes.get(nextId).getMyZoneTable());
		nodes.get(myId).setNextNextZoneTable(nodes.get(nextId).getNextZoneTable());
		nodes.get(nextId).setPreZoneTable(nodes.get(myId).getMyZoneTable());

		//しきい値以下ならZone再構成メソッドを呼ぶ
		//自分、nextZoneTableとpreZoneTableの平均以下ならZone再構成メソッドをよぶ 5/9更新
		myZoneTable = nodes.get(myId).getMyZoneTable();
		int zoneAverage = (nodeCount+nodes.get(myId).getNextZoneTable().size()+nodes.get(myId).getPreZoneTable().size())/3;
		while(nodeCount<zoneAverage){
			nodes=zoneReconstitution(nodes,myId,myZoneTable,nodes.get(myId).getNextZoneTable(),nodes.get(myId).getPreZoneTable());
			nodeCount++;
			if(nodeCount>=zoneAverage) this.setReconstitutionCount(this.getReconstitutionCount()+1);
		}
		this.addMessage();
		nodes.get(nextId).getZoneReplication().addMessage();
		return nodes;
	}
	*/
	//変更ver
	ArrayList<ChordNode> maintenance(ArrayList<ChordNode> nodes,int myId){//←をつけたところは変更する必要なしと考える
		this.maintenanceCount++;//
		ArrayList<Integer> myZoneTable = nodes.get(myId).getMyZoneTable();//
		ArrayList<Integer> nextZoneTable = nodes.get(myId).getNextZoneTable();//
		ArrayList<Integer> nextNextZoneTable = nodes.get(myId).getNextNextZoneTable();//
		//自分自身のZone内のハートビートを送る
		int nodeCount=heartBeat(nodes,myZoneTable);//
		//System.out.println(myZoneTable);
		//次のノードを探す
		int nextId = nodes.get(myId).getNextId();//
		//System.out.println(nextId);
		//監視先がないなら
		if(nextId!=-1){
			if(nodes.get(nextId).getIsJoin()&&nodes.get(nextId).getIsReplica()){
				this.addMessage();
				nodes.get(nextId).setPreId(myId);
			}
			//監視先が死んでいる場合
			else if(nodes.get(nextId).getIsJoin()&&!nodes.get(nextId).getIsReplica()){
				nodes.get(nextId).setReplica(true);
				this.addMessage();
				nodes.get(nextId).setPreId(myId);
			}else if(!nodes.get(nextId).getIsJoin()){
				//nextZoneTableから複製できるNodeを探す
				nextId = searchNextZoneId(nodes,nextZoneTable,nodes.get(myId).getZoneId());
				//System.out.print("nextId:  ");
				//System.out.println(nextId);
				nodes.get(myId).setNextId(nextId);
				this.addMessage();
				nodes.get(nextId).setReplica(true);
				nodes.get(myId).getZoneReplication().addReplicaCount();
				nodes.get(nextId).setMyZoneTable(nextZoneTable);
				nodes.get(nextId).setPreZoneTable(myZoneTable);
				nodes.get(nextId).setPreId(myId);
				//nextIdの次のZoneのIdを探す
				int nextNextId = searchNextZoneId(nodes, nextNextZoneTable,nodes.get(nextId).getZoneId());
				//System.out.print("nextNextId:  ");
				//System.out.println(nextNextId);
				//nextIdが全く見つからないということがなければnextIdを設定する
				if(nextNextId!=-1){
					nodes.get(nextId).setNextZoneTable(nextNextZoneTable);
					nodes.get(nextId).setNextId(nextNextId);
					//this.addMessage();
					if(!nodes.get(nextNextId).getIsReplica()){
						nodes.get(nextNextId).setReplica(true);
					}
				}
			}else{
				nodes.get(nextId).setPreId(myId);
				this.addMessage();
			}
		}else{
			nextId = searchNextZoneId(nodes, nextZoneTable,nodes.get(myId).getZoneId());
			nodes.get(myId).setNextId(nextId);
			this.addMessage();
			nodes.get(nextId).setReplica(true);
			nodes.get(myId).getZoneReplication().addReplicaCount();
			nodes.get(nextId).setMyZoneTable(nextZoneTable);
			nodes.get(nextId).setPreZoneTable(myZoneTable);
			nodes.get(nextId).setNextZoneTable(nextNextZoneTable);
			nodes.get(nextId).setPreId(myId);
			//nextIdの次のZoneのIdを探す
			int nextNextId = searchNextZoneId(nodes, nextNextZoneTable,nodes.get(nextId).getZoneId());
			//nextIdが全く見つからないということがなければnextIdを設定する
			if(nextNextId!=-1){
				nodes.get(nextId).setNextZoneTable(nextNextZoneTable);
				nodes.get(nextId).setNextId(nextNextId);
				if(!nodes.get(nextNextId).getIsReplica()){
					nodes.get(nextNextId).setReplica(true);
				}
			}
		}
		//次のZoneのTableを取得する
		//次のZoneに自分のTableを送信する
		nodes.get(myId).setNextZoneTable(nodes.get(nextId).getMyZoneTable());
		nodes.get(myId).setNextNextZoneTable(nodes.get(nextId).getNextZoneTable());
		nodes.get(nextId).setPreZoneTable(nodes.get(myId).getMyZoneTable());

		//しきい値以下ならZone再構成メソッドを呼ぶ
		//自分、nextZoneTableとpreZoneTableの平均以下ならZone再構成メソッドをよぶ 5/9更新
		myZoneTable = nodes.get(myId).getMyZoneTable();
		//System.out.println(myZoneTable);
		int zoneAverage = (nodeCount+nodes.get(myId).getNextZoneTable().size()+nodes.get(myId).getPreZoneTable().size())/3;
		while(nodeCount<zoneAverage){
			nodes=zoneReconstitution(nodes,myId,myZoneTable,nodes.get(myId).getNextZoneTable(),nodes.get(myId).getPreZoneTable());
			nodeCount++;
			if(nodeCount>=zoneAverage) this.setReconstitutionCount(this.getReconstitutionCount()+1);
		}
		this.addMessage();
		nodes.get(nextId).getZoneReplication().addMessage();
		return nodes;
	}

	/**
	 * Zone再構成メソッド
	 */
	ArrayList<ChordNode> zoneReconstitution(ArrayList<ChordNode> nodes,int nodeId,ArrayList<Integer> myZoneTable,ArrayList<Integer> nextZoneTable,ArrayList<Integer> preZoneTable){
		int next = arriveCount(nodes,nextZoneTable);
		int pre = arriveCount(nodes,preZoneTable);
		if(next>pre){
			Collections.reverse(nextZoneTable);
			int nextId=0;
			for(int index:nextZoneTable){
				this.addRebuildingHeartBeatCount();
				if(nodes.get(index).getIsJoin()&&!nodes.get(index).getIsReplica()){
					this.addRebuildingCount();
					nodes.get(nodeId).getNextZoneTable().remove(new Integer(index));
					nodes.get(nodeId).getMyZoneTable().add(index);
					int zoneId = nodes.get(index).getZoneId();
					if(zoneId-1>=0){
						nodes.get(index).setZoneId(zoneId-1);
					}else{
						nodes.get(index).setZoneId(ZoneSim.ZONENUM-1);
					}
					nodes.get(index).setMyZoneTable(nodes.get(nodeId).getMyZoneTable());
					nodes.get(index).setPreZoneTable(nodes.get(nodeId).getPreZoneTable());
					nodes.get(index).setNextZoneTable(nodes.get(nodeId).getNextZoneTable());
					break;
				}
			}
			Collections.reverse(nextZoneTable);
			myZoneTable = nodes.get(nodeId).getMyZoneTable();
			for(int index : myZoneTable){
				nodes.get(index).setMyZoneTable(nodes.get(nodeId).getMyZoneTable());
				nodes.get(index).setNextZoneTable(nodes.get(nodeId).getNextZoneTable());
				nodes.get(index).setPreZoneTable(nodes.get(nodeId).getPreZoneTable());
			}
			nextZoneTable =nodes.get(nextId).getNextZoneTable();
			for(int index : nextZoneTable){
				nodes.get(index).setPreZoneTable(nodes.get(nextId).getMyZoneTable());
				nodes.get(index).setMyZoneTable(nodes.get(nextId).getNextZoneTable());
			}
		}else{
			int preId=0;
			Collections.reverse(preZoneTable);
			for(int index:preZoneTable){
				this.addRebuildingHeartBeatCount();
				if(nodes.get(index).getIsJoin()&&!nodes.get(index).getIsReplica()){
					this.addRebuildingCount();
					nodes.get(nodeId).getPreZoneTable().remove(new Integer(index));
					nodes.get(nodeId).getMyZoneTable().add(index);
					int zoneId = nodes.get(index).getZoneId();
					if(zoneId+1<ZoneSim.ZONENUM){
						nodes.get(index).setZoneId(zoneId+1);
					}else{
						nodes.get(index).setZoneId(0);
					}
					preId = index;
					nodes.get(preId).setMyZoneTable(nodes.get(nodeId).getMyZoneTable());
					nodes.get(preId).setNextZoneTable(nodes.get(nodeId).getNextZoneTable());
					nodes.get(preId).setPreZoneTable(nodes.get(nodeId).getPreZoneTable());
					break;
				}
			}
			Collections.reverse(preZoneTable);
			myZoneTable = nodes.get(nodeId).getMyZoneTable();
			for(int index : myZoneTable){
				nodes.get(index).setMyZoneTable(nodes.get(nodeId).getMyZoneTable());
				nodes.get(index).setNextZoneTable(nodes.get(nodeId).getNextZoneTable());
				nodes.get(index).setPreZoneTable(nodes.get(nodeId).getPreZoneTable());
			}
			preZoneTable =nodes.get(nodeId).getPreZoneTable();
			for(int index : preZoneTable){
				nodes.get(index).setMyZoneTable(nodes.get(preId).getPreZoneTable());
				nodes.get(index).setNextZoneTable(nodes.get(preId).getMyZoneTable());
			}
		}
		return nodes;
	}
}
