package syntaxAnalysis;

import java.util.ArrayList;

public class FuncTable {
	private ArrayList<Func> funcList = new ArrayList<Func>();

	FuncTable() {
	}

	// ��Ӻ���
	void addFunc(Func func) {
		funcList.add(func);
	}

	// �����Ƿ��иú���
	boolean containsFunc(String name) {
		for (Func func : funcList) {
			if (func.name.contentEquals(name)) {
				return true;
			}
		}
		return false;
	}

	// ���غ�����index
	Integer getIndex(String name) {
		int i;
		for (i = 0; i < funcList.size(); i++) {
			if (funcList.get(i).name.contentEquals(name)) {
				return i;
			}
		}
		return null;
	}

	// ���غ���������
	Func getFunc(String name) {
		int i;
		for (i = 0; i < funcList.size(); i++) {
			if (funcList.get(i).name.contentEquals(name)) {
				return funcList.get(i);
			}
		}
		return null;
	}

	public ArrayList<Func> getFuncList() {
		return this.funcList;
	}

}
