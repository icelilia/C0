package syntaxAnalysis;

import java.util.HashMap;

class FuncTable {
	private HashMap<String, Func> funcTable = new HashMap<String, Func>();

	FuncTable() {
	}

	// �����Ƿ��иú���
	boolean containsKey(String name) {
		return funcTable.containsKey(name);
	}

	// ���ú�����ֵ
	void put(String name, Func value) {
		funcTable.put(name, value);
	}

	// ���ڴ˺����򷵻ر������
	// �������򷵻�null
	Func get(String name) {
		return funcTable.get(name);
	}
}
