package syntaxAnalysis;

import java.util.HashMap;

class Block {
	int no;
	int fatherNo;
	private HashMap<String, Integer> constTable = new HashMap<String, Integer>();
	private HashMap<String, Integer> initVarTable = new HashMap<String, Integer>();
	private HashMap<String, Integer> unInitVarTable = new HashMap<String, Integer>();

	Block(int no, int fatherNo) {
		this.no = no;
		this.fatherNo = fatherNo;
	}

	// ���س�����ͱ��������Ƿ��������ʶ��
	boolean containsKey(String name) {
		return constTable.containsKey(name) || initVarTable.containsKey(name) || unInitVarTable.containsKey(name);
	}

	// kind����Ϊ����
	// 0��ʾ����
	// 1��ʾ����
	// -1��ʾΪ��ʼ���ı���
	void put(int kind, String name, Integer offset) {
		if (kind == 0) {
			constTable.put(name, offset);
		} else if (kind == 1) {
			initVarTable.put(name, offset);
		} else if (kind == -1) {
			unInitVarTable.put(name, offset);
		}
	}

	// �ж��Ƿ��ѳ�ʼ��
	boolean isUnInit(String name) {
		return unInitVarTable.containsKey(name);
	}

	// ��δ��ʼ������תΪ��ʼ������
	void change(String name) {
		Integer offset = unInitVarTable.get(name);
		unInitVarTable.remove(name);
		initVarTable.put(name, offset);
	}

	Integer getOffset(String name) {
		Integer offset = constTable.get(name);
		if (offset != null) {
			return offset;
		}
		// ������Ϊ����������
		offset = initVarTable.get(name);
		if (offset != null) {
			return offset;
		}
		// ��Ϊ�վͷ���null
		return null;
	}
}
