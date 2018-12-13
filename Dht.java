import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;

import javax.xml.bind.DatatypeConverter;

/**
 * @author Shigeki_Yoneda
 * DHTを簡易的に再現するためのクラス
 */
public class Dht {
	String successor;
	String predicessor;
//	int fingerTable;
	HashMap<String, Integer> hashTable;
	Dht(){
	}
	Dht(String id,String sucsessor,String predicessor){
		hashTable = new HashMap<String,Integer>();
		init(sucsessor,predicessor);
		try {
			hash(id);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 前後のノード情報の初期化するためのメソッド
	 * @param sucsessor
	 * @param predicessor
	 */
	void init(String sucsessor,String predicessor){
		this.successor = sucsessor;
		this.predicessor = predicessor;
	}
	/**
	 * 受け取ったアドレスをSHA-1でハッシュ化
	 * @param address
	 * @return result
	 * @throws NoSuchAlgorithmException
	 */
	String hash(String address) throws NoSuchAlgorithmException{
		String algorithm ="SHA-1";
		Charset charset = StandardCharsets.UTF_8;
        //ハッシュ生成処理
        byte[] bytes = MessageDigest.getInstance(algorithm).digest(address.getBytes(charset));
        String result = DatatypeConverter.printHexBinary(bytes);
		return result;
	}
	/**
	 * 次のノードの情報を取得するためのメソッド
	 * @return succsessor
	 */
	public String getSuccsessor() {
		return this.successor;
	}
	/**
	 * 次のノード情報をセットするためのメソッド
	 * @param sucsessor
	 */
	public void setSucsessor(String successor) {
		this.successor = successor;
	}
	/**
	 * 前のノード情報を取得するためのメソッド
	 * @return predicessor
	 */
	public String getPredicessor() {
		return predicessor;
	}
	/**
	 * 前の情報をセットするためのメソッド
	 * @param predicessor
	 */
	public void setPredicessor(String predicessor) {
		this.predicessor = predicessor;
	}
	/**
	 * ハッシュテーブルを取得するためのメソッド
	 * @return hashTable
	 */
	public HashMap<String, Integer> getHashTable() {
		return hashTable;
	}
	/**
	 * ハッシュテーブルをセットするためのメソッド
	 * @param hashTable
	 */
	public void setHashTable(HashMap<String, Integer> hashTable) {
		this.hashTable = hashTable;
	}
	/*
	public int getFingerTable() {
		return fingerTable;
	}
	public void setFingerTable(int fingerTable) {
		this.fingerTable = fingerTable;
	}
*/

}
